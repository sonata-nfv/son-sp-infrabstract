import argparse
from keystoneclient.v2_0 import client
from heatclient.client import Client
import json
import yaml

def get_stack_list(heat):
    stacks = heat.stacks.list()
    while True:
        try:
            stack = stacks.next()
            print stack.stack_name + " (" + stack.id + "): " + stack.stack_status
        except StopIteration:
            break

def autheticate(cip, username, password, tenant):
    auth_url = 'http://'+ str(cip)+':5000/v2.0'
    keystone = client.Client(username=username, password=password, tenant_name=tenant, auth_url=auth_url)
    auth_token = keystone.auth_ref['token']['id']
    tenant_id = keystone.tenant_id
    heat_url = 'http://'+str(cip)+':8004/v1/%s' % tenant_id
    heat = Client('1', endpoint=heat_url, token=auth_token)
    return heat

def print_server(server,stackname):
    print ("resource_name: "+server['resource_name'])
    print ("physical_resource_id: " +server['physical_resource_id'])
    server = heat.resources.get(stackname, server['resource_name']).to_dict()
    #print server
    addresses = server['attributes']['addresses']
    for i in range(len(addresses)):
        add2 = addresses.values()[i]
        print ("addr: "+add2[0]['addr'])     

parser = argparse.ArgumentParser()
parser.add_argument("-cf", "--configuration", nargs=4, help="pass the cloud url, username, password and tenant name",
                    required=True)  # option configurations, needs to be required
parser.add_argument("-d", "--delete", help="delete this stack")  # option delete
parser.add_argument("-s", "--status", help="return the status of the stack")  # option status
parser.add_argument("-c", "--create", nargs=2,
                    help="create this stack with the name, give two arguments")  # option create stack

args = parser.parse_args()  # pass the arguments to the parser

if args.configuration:  # actions to do from the configuration
    cip = args.configuration[0]
    username = args.configuration[1]
    password = args.configuration[2]
    tenant = args.configuration[3]
    heat = autheticate(cip, username, password, tenant)  # go through authetication with this credentials

if args.status:  # action from the status option
    stackname = args.status
    stack_stat = heat.stacks.get(stack_id=stackname).to_dict()
    print stack_stat['stack_status']
    stack = heat.resources.list(stackname)
    for item in stack:
        stack_res = item.to_dict()
        type_res = stack_res['resource_type']
        if type_res=='OS::Nova::Server':
            print_server(stack_res,stackname)
    
if args.delete:  # Actions to do if given argument --delete
    stackname = args.delete
    stack = heat.stacks.delete(stack_id=stackname)
    print 'DELETED'

if args.create:  # Actions to be taken when given argument --create
    stackname = args.create[0]
    yamlh = args.create[1]
    stack = heat.stacks.create(stack_name=stackname, template=yamlh)
    uid = stack['stack']['id']
    print uid

