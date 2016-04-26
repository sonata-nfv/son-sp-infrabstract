from keystoneclient.v2_0 import client
from heatclient.client import Client
import yaml
import sys

def get_stack_list(heat):

	stacks = heat.stacks.list()

	while True:
		try:
			stack = stacks.next()
			print stack.stack_name+" ("+stack.id+"): "+stack.stack_status
		except StopIteration:
			break


cip = '143.233.127.3'
# h = httplib2.Http(".cache")
# h.add_credentials('admin', 'admin')
# heat = client.Client("2", "admin", "admin_password", "admin", "http://10.100.16.3:35357/v2.0")
#yamlfl = "simple_heat.yaml"



total = len(sys.argv)

#print "Number of arguments " + str(total)
#if (total < 3):
# 	print "Missing arguments"
# 	sys.exit(1)

if (len(str(sys.argv[1])) > 1):
	stackname = str(sys.argv[1])

if (total ==3 and len(str(sys.argv[2])) > 1):
	yamlfl = str(sys.argv[2])
	with open(yamlfl, 'r') as stream:
		try:
			yamlh = yaml.load(stream)
			#print(yaml.load(stream))
		except yaml.YAMLError as exc:
			print(exc)



username='sonata'
password='s0n@t@'
tenant_name='sonata'
auth_url='http://'+ str(cip) +':5000/v2.0'
keystone = client.Client(username=username, password=password, tenant_name=tenant_name, auth_url=auth_url)
auth_token = keystone.auth_ref['token']['id']
#print auth_token
tenant_id = keystone.tenant_id
heat_url = 'http://'+ str(cip) +':8004/v1/%s' % tenant_id
heat = Client('1', endpoint=heat_url, token=auth_token)

#get_stack_list(heat)

if (total == 3):
	stack = heat.stacks.create(stack_name=stackname, template=yamlh)
	uid = stack['stack']['id']
	print uid
	stack = heat.stacks.get(stack_id=uid).to_dict()
elif (total == 2):
	stack = heat.stacks.get(stack_id=stackname).to_dict()
	print "Stack in state: {}".format(stack['stack_status'])




