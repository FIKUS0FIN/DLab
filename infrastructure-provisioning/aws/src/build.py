#!/usr/bin/python
# ============================================================================
# Copyright (c) 2016 EPAM Systems Inc.
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
# ============================================================================
from fabric.api import *
import argparse
import os

parser = argparse.ArgumentParser()
parser.add_argument('--target', type=str, default='everything')
parser.add_argument('--tag', type=str, default='')
parser.add_argument('--builddir', action='')
parser.add_argument('--push', action='store_true')
args = parser.parse_args()


if args.builddir == '':
    args.builddir = os.path.dirname(os.path.realpath(__file__))

