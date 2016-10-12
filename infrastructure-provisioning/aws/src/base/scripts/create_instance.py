#!/usr/bin/python
import argparse
import json
from dlab.aws_actions import *
from dlab.aws_meta import *
import sys


parser = argparse.ArgumentParser()
parser.add_argument('--node_name', type=str, default='DSS-POC-TEST-instance')
parser.add_argument('--ami_id', type=str, default='ami-7172b611')
parser.add_argument('--instance_type', type=str, default='t2.small')
parser.add_argument('--key_name', type=str, default='BDCC-DSS-POC')
parser.add_argument('--security_group_ids', type=str, default='sg-1e0f7f79,sg-12345')
parser.add_argument('--subnet_id', type=str, default='subnet-1e6c9347')
parser.add_argument('--iam_profile', type=str, default='')
parser.add_argument('--infra_tag_name', type=str, default='BDCC-DSA-test-infra')
parser.add_argument('--infra_tag_value', type=str, default='tmp')
parser.add_argument('--user_data_file', type=str, default='')
args = parser.parse_args()


if __name__ == "__main__":
    success = False
    instance_tag = {"Key": args.infra_tag_name, "Value": args.infra_tag_value}
    if args.node_name != '':
        try:
            instance_id_test = get_instance_by_name(args.node_name)
            if instance_id == '':
                print "Creating instance %s of type %s in subnet %s with tag %s." % \
                      (args.node_name, args.instance_type, args.subnet_id, json.dumps(instance_tag))
                instance_id = create_instance(args, instance_tag)
            else:
                print "REQUESTED INSTANCE ALREADY EXISTS AND RUNNING"
            print "Instance_id " + instance_id
            print "Public_hostname " + get_instance_attr(instance_id, 'public_dns_name')
            print "Private_hostname " + get_instance_attr(instance_id, 'private_dns_name')
            success = True
        except:
            success = False
    else:
        parser.print_help()
        sys.exit(2)

    if success:
        sys.exit(0)
    else:
        sys.exit(1)
