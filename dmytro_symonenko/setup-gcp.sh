#!/bin/bash

#/usr/bin/python infrastructure-provisioning/scripts/deploy_dlab.py \
#--conf_service_base_name dlab --gcp_region us-west1 --gcp_zone us-west1-a \
#--conf_os_family debian --conf_cloud_provider gcp --key_path /key/path/ \
#--conf_key_name key_name --gcp_ssn_instance_size n1-standard-1 \
#--gcp_project_id project_id --gcp_service_account_path /path/to/auth/file.json \
#--action create

gcp_region="europe-west1"
gcp_zone="europe-west1-b"
os_family="debian"
key_path="/Users/dmytrosymonenko/.ssh"
key_name="dlab_key"
project_id="epam-curse"
account_path="/Users/dmytrosymonenko/.ssh/ds-dlab.jso"

/usr/bin/python ../infrastructure-provisioning/scripts/deploy_dlab.py \
--conf_service_base_name dlab --gcp_region $gcp_region --gcp_zone $gcp_zone \
--conf_os_family $os_family --conf_cloud_provider gcp --key_path $key_path \
--conf_key_name $key_name --gcp_ssn_instance_size n1-standard-1 \
--gcp_project_id $project_id --gcp_service_account_path $account_path \
--action create
