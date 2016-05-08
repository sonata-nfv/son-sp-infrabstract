# from keystoneclient.v2_0 import client
# from heatclient.client import Client
import argparse
from keystoneclient.v3 import client
from heatclient.client import Client


def get_stack_list(heat):

    stacks = heat.stacks.list()
    while True:
        try:
            stack = stacks.next()
            print stack.stack_name + " (" + stack.id + "): " + stack.stack_status
        except StopIteration:
            break


def autheticate(cip, username, password, tenant):
    auth_url = 'http://'+ str(cip)+':5000/v3'
    keystone = client.Client(username=username, password=password, tenant_name=tenant, auth_url=auth_url)
    auth_token = keystone.auth_ref['auth_token']
    tenant_id = keystone.tenant_id
    heat_url = 'http://'+str(cip)+':8004/v1/%s' % tenant_id
    heat = Client('1', endpoint=heat_url, token=auth_token)
    return heat


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
args = parser.parse_args()  # pass the arguments to the parser

if args.status:  # action from the status option
    stackname = args.status
    stack = heat.stacks.get(stack_id=stackname).to_dict()
    print stack['stack_status']

if args.delete:  # Actions to do if given argument --delete
    stackname = args.delete
    stack = heat.stacks.delete(stack_id=stackname)
    print 'DELETED'

if args.configuration:  # actions to do from the configuration
    cip = args.configuration[0]
    username = args.configuration[1]
    password = args.configuration[2]
    tenant = args.configuration[3]
    heat = autheticate(cip, username, password, tenant)  # go through authetication with this credentials

if args.create:  # Actions to be taken when given argument --create
    stackname = args.create[0]
    yamlh = args.create[1]
    stack = heat.stacks.create(stack_name=stackname, template=yamlh)
    uid = stack['stack']['id']
    print uid

args = parser.parse_args()  # pass the arguments to the parser

if args.configuration:  # actions to do from the configuration
    cip = args.configuration[0]
    username = args.configuration[1]
    password = args.configuration[2]
    tenant = args.configuration[3]
    heat = autheticate(cip, username, password, tenant)  # go through authetication with this credentials
