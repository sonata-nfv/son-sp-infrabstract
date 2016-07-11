#
#  Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, THALES, NCSR Demokritos
#  ALL RIGHTS RESERVED.
#  
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#  
#      http://www.apache.org/licenses/LICENSE-2.0
#  
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#  
#  Neither the name of the SONATA-NFV, UCL, NOKIA, THALES NCSR Demokritos 
#  nor the names of its contributors may be used to endorse or promote 
#  products derived from this software without specific prior written 
#  permission.
#  
#  This work has been performed in the framework of the SONATA project,
#  funded by the European Commission under Grant number 671517 through 
#  the Horizon 2020 and 5G-PPP programmes. The authors would like to 
#  acknowledge the contributions of their colleagues of the SONATA 
#  partner consortium (www.sonata-nfv.eu).
#
__author__= "Dario Valocchi(Ph.D.)-UCL, Bruno Vidalenc(Ph.D.)-Thales"
  


import argparse
from keystoneclient.v2_0 import client
from novaclient import client as nova_client
import json
import yaml
from subprocess import call


def autheticate(cip, username, password, tenant):
    auth_url = 'http://'+ str(cip)+':5000/v2.0'
    keystone = client.Client(username=username, password=password, tenant_name=tenant, auth_url=auth_url)
    auth_token = keystone.auth_ref['token']['id']
    project_name = keystone.project_name
    nova = nova_client.Client(2.1, auth_token=auth_token,auth_url=auth_url,project_id=project_name)
    return nova

def limit_list(nova):
    limits = nova.limits.get()
    abs = {l.name: l.value for l in limits.absolute}
    print {'memory_used':abs['totalRAMUsed'],'memory_total':abs['maxTotalRAMSize'],'CPU_used':abs['totalCoresUsed'],'CPU_total':abs['maxTotalCores']}


#def flavor_list(vcpus,memory,storage):
def flavor_list(nova):
    flavors = nova.flavors.list()
    for flavor in flavors:
       print flavor.name + " cpu " + str(flavor.vcpus) + " ram " + str(flavor.ram) + " storage " + str(flavor.disk)

parser = argparse.ArgumentParser()
parser.add_argument("-cf", "--configuration", nargs=4, help="pass the cloud url, username, password and tenant name",
                    required=True)  # option configurations, needs to be required
parser.add_argument("-f", "--flavors", action='store_true',help="list flavors") # option flavors
parser.add_argument("-l", "--limits", action='store_true',help="list limits") # option limits

args = parser.parse_args()  # pass the arguments to the parser

if args.configuration:  # actions to do from the configuration
    cip = args.configuration[0]
    username = args.configuration[1]
    password = args.configuration[2]
    tenant = args.configuration[3]
    nova = autheticate(cip, username, password, tenant)  # go through authetication with this credentials


if args.flavors:  # Actions to be taken when given argument --flavor
    flavor_list(nova)

if args.limits:
    limit_list(nova)
