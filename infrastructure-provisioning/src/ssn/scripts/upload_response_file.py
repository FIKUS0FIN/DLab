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

import sys
import argparse
import logging
from dlab.ssn_lib import *


parser = argparse.ArgumentParser()
parser.add_argument('--instance_name', type=str, default='')
parser.add_argument('--local_log_filepath', type=str, default='')
args = parser.parse_args()


if __name__ == "__main__":
    if not upload_response_file(args.instance_name, args.local_log_filepath):
        logging.error('Failed to upload response file')
        sys.exit(1)