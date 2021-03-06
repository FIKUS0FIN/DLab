#!/usr/bin/python

# *****************************************************************************
#
# Copyright (c) 2016, EPAM SYSTEMS INC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ******************************************************************************

import json
import time
from fabric.api import *
from dlab.fab import *
from dlab.meta_lib import *
from dlab.actions_lib import *
import sys
import os
import logging
import argparse
import multiprocessing


parser = argparse.ArgumentParser()
parser.add_argument('--uuid', type=str, default='')
args = parser.parse_args()


def configure_dataengine_service(instance, emr_conf):
    emr_conf['instance_ip'] = instance.get('PrivateIpAddress')
    # configuring proxy on Data Engine service
    try:
        logging.info('[CONFIGURE PROXY ON DATAENGINE SERVICE]')
        print('[CONFIGURE PROXY ON DATAENGINE SERVICE]')
        additional_config = {"proxy_host": emr_conf['edge_instance_hostname'], "proxy_port": "3128"}
        params = "--hostname {} --instance_name {} --keyfile {} --additional_config '{}' --os_user {}" \
            .format(emr_conf['instance_ip'], emr_conf['cluster_name'], emr_conf['key_path'],
                    json.dumps(additional_config), emr_conf['os_user'])
        try:
            local("~/scripts/{}.py {}".format('common_configure_proxy', params))
        except:
            traceback.print_exc()
            raise Exception
    except Exception as err:
        append_result("Failed to configure proxy.", str(err))
        terminate_emr(emr_conf['cluster_id'])
        sys.exit(1)

    try:
        logging.info('[CONFIGURE DATAENGINE SERVICE]')
        print('[CONFIGURE DATAENGINE SERVICE]')
        try:
            configure_data_engine_service_pip(emr_conf['instance_ip'], emr_conf['os_user'], emr_conf['key_path'])
        except:
            traceback.print_exc()
            raise Exception
    except Exception as err:
        append_result("Failed to configure dataengine service.", str(err))
        terminate_emr(emr_conf['cluster_id'])
        sys.exit(1)


if __name__ == "__main__":
    local_log_filename = "{}_{}_{}.log".format(os.environ['conf_resource'], os.environ['edge_user_name'],
                                               os.environ['request_id'])
    local_log_filepath = "/logs/" + os.environ['conf_resource'] + "/" + local_log_filename
    logging.basicConfig(format='%(levelname)-8s [%(asctime)s]  %(message)s',
                        level=logging.INFO,
                        filename=local_log_filepath)
    try:
        os.environ['exploratory_name']
    except:
        os.environ['exploratory_name'] = ''
    create_aws_config_files()
    print('Generating infrastructure names and tags')
    emr_conf = dict()
    try:
        emr_conf['exploratory_name'] = os.environ['exploratory_name']
    except:
        emr_conf['exploratory_name'] = ''
    try:
        emr_conf['computational_name'] = os.environ['computational_name']
    except:
        emr_conf['computational_name'] = ''
    emr_conf['apps'] = 'Hadoop Hive Hue Spark'
    emr_conf['service_base_name'] = os.environ['conf_service_base_name']
    emr_conf['tag_name'] = emr_conf['service_base_name'] + '-Tag'
    emr_conf['key_name'] = os.environ['conf_key_name']
    emr_conf['region'] = os.environ['aws_region']
    emr_conf['release_label'] = os.environ['emr_version']
    emr_conf['master_instance_type'] = os.environ['emr_master_instance_type']
    emr_conf['slave_instance_type'] = os.environ['emr_slave_instance_type']
    emr_conf['instance_count'] = os.environ['emr_instance_count']
    emr_conf['notebook_ip'] = get_instance_ip_address(emr_conf['tag_name'],
                                                      os.environ['notebook_instance_name']).get('Private')
    emr_conf['role_service_name'] = os.environ['emr_service_role']
    emr_conf['role_ec2_name'] = os.environ['emr_ec2_role']
    emr_conf['tags'] = 'Name=' + emr_conf['service_base_name'] + '-' + os.environ['edge_user_name'] + '-emr-' + \
                       emr_conf['exploratory_name'] + '-' + emr_conf['computational_name'] + '-' + args.uuid + \
                       ', ' + emr_conf['service_base_name'] + '-Tag=' + emr_conf['service_base_name'] + '-' + \
                       os.environ['edge_user_name'] + '-emr-' + emr_conf['exploratory_name'] + '-' + \
                       emr_conf['computational_name'] + '-' + args.uuid + \
                       ', Notebook=' + os.environ['notebook_instance_name'] + ', State=not-configured'
    emr_conf['cluster_name'] = emr_conf['service_base_name'] + '-' + os.environ['edge_user_name'] + '-emr-' + \
                               emr_conf['exploratory_name'] + '-' + emr_conf['computational_name'] + '-' + \
                               args.uuid
    emr_conf['bucket_name'] = (emr_conf['service_base_name'] + '-ssn-bucket').lower().replace('_', '-')

    tag = {"Key": "{}-Tag".format(emr_conf['service_base_name']), "Value": "{}-{}-subnet".format(
        emr_conf['service_base_name'], os.environ['edge_user_name'])}
    emr_conf['subnet_cidr'] = get_subnet_by_tag(tag)
    emr_conf['key_path'] = os.environ['conf_key_dir'] + '/' + os.environ['conf_key_name'] + '.pem'
    emr_conf['all_ip_cidr'] = '0.0.0.0/0'
    emr_conf['additional_emr_sg_name'] = '{}-{}-de-se-additional-sg'.format(emr_conf['service_base_name'],
                                                                          os.environ['edge_user_name'])
    emr_conf['vpc_id'] = os.environ['aws_vpc_id']
    emr_conf['cluster_id'] = get_emr_id_by_name(emr_conf['cluster_name'])
    emr_conf['cluster_instances'] = get_emr_instances_list(emr_conf['cluster_id'])
    emr_conf['edge_instance_name'] = emr_conf['service_base_name'] + "-" + os.environ['edge_user_name'] + '-edge'
    emr_conf['edge_instance_hostname'] = get_instance_private_ip_address(emr_conf['tag_name'],
                                                                         emr_conf['edge_instance_name'])
    emr_conf['os_user'] = 'ec2-user'

    try:
        jobs = []
        for instance in emr_conf['cluster_instances']:
            p = multiprocessing.Process(target=configure_dataengine_service, args=(instance, emr_conf))
            jobs.append(p)
            p.start()
        for job in jobs:
            job.join()
        for job in jobs:
            if job.exitcode != 0:
                raise Exception
    except:
        traceback.print_exc()
        raise Exception

    try:
        logging.info('[SUMMARY]')
        print('[SUMMARY]')
        print("Service base name: {}".format(emr_conf['service_base_name']))
        print("Cluster name: {}".format(emr_conf['cluster_name']))
        print("Cluster id: {}".format(get_emr_id_by_name(emr_conf['cluster_name'])))
        print("Key name: {}".format(emr_conf['key_name']))
        print("Region: {}".format(emr_conf['region']))
        print("EMR version: {}".format(emr_conf['release_label']))
        print("EMR master node shape: {}".format(emr_conf['master_instance_type']))
        print("EMR slave node shape: {}".format(emr_conf['slave_instance_type']))
        print("Instance count: {}".format(emr_conf['instance_count']))
        print("Notebook IP address: {}".format(emr_conf['notebook_ip']))
        print("Bucket name: {}".format(emr_conf['bucket_name']))
        with open("/root/result.json", 'w') as result:
            res = {"hostname": emr_conf['cluster_name'],
                   "instance_id": get_emr_id_by_name(emr_conf['cluster_name']),
                   "key_name": emr_conf['key_name'],
                   "user_own_bucket_name": emr_conf['bucket_name'],
                   "Action": "Create new EMR cluster"}
            print(json.dumps(res))
            result.write(json.dumps(res))
    except:
        print("Failed writing results.")
        sys.exit(0)

    sys.exit(0)