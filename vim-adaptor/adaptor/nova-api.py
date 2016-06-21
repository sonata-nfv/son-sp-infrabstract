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

#def flavor_list(vcpus,memory,storage):
def flavor_list(nova):
    flavors = nova.flavors.list()
    for flavor in flavors:
       print flavor.name + " cpu " + str(flavor.vcpus) + " ram " + str(flavor.ram) + " storage " + str(flavor.disk)

parser = argparse.ArgumentParser()
parser.add_argument("-cf", "--configuration", nargs=4, help="pass the cloud url, username, password and tenant name",
                    required=True)  # option configurations, needs to be required
parser.add_argument("-f", "--flavors", action='store_true',help="list flavors") # option flavors

args = parser.parse_args()  # pass the arguments to the parser

if args.configuration:  # actions to do from the configuration
    cip = args.configuration[0]
    username = args.configuration[1]
    password = args.configuration[2]
    tenant = args.configuration[3]
    nova = autheticate(cip, username, password, tenant)  # go through authetication with this credentials


if args.flavors:  # Actions to be taken when given argument --flavor
    flavor_list(nova)

