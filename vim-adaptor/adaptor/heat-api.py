#
#  Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos
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
#  Neither the name of the SONATA-NFV, UCL, NOKIA, NCSR Demokritos 
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
__author__= "Akis Kourtis - NCSR Demokritos, Stavros Kolometsos - NCSR Demokritos, Dario Valocchi(Ph.D.)-UCL"

import argparse
from keystoneclient.v2_0 import client
from heatclient.client import Client
import json
import yaml
from subprocess import call


def get_stack_list(heat): #deprecated 

    stacks = heat.stacks.list()
    while True:
        try:
            stack = stacks.next()
            print stack.stack_name + " (" + stack.id + "): " + stack.stack_status
        except StopIteration:
            break

def autheticate(cip, username, password, tenant): #function used to autheticate with keystone
    auth_url = 'http://'+ str(cip)+':5000/v2.0'
    keystone = client.Client(username=username, password=password, tenant_name=tenant, auth_url=auth_url)
    auth_token = keystone.auth_ref['token']['id']
    tenant_id = keystone.tenant_id
    heat_url = 'http://'+str(cip)+':8004/v1/%s' % tenant_id
    heat = Client('1', endpoint=heat_url, token=auth_token)
    return heat

def write_server(server,stackname):  #function used to write needed server's info to dictionary
    s_id = server['physical_resource_id']
    server = heat.resources.get(stackname, server['resource_name']).to_dict()
    s_name =server['attributes']['name']
    #flavor = server['attributes']['flavor']
    server_dict = {'server_name': s_name, 'server_id' : s_id}
    return server_dict

def write_port(in_rec,stackname): #function used to write needed port's info to dictionary
    in_rec = heat.resources.get(stackname, in_rec['resource_name']).to_dict()
    #obj = open ('portcheck.json', 'a+')
    #json.dump(in_rec, obj,indent=4, sort_keys=True)
    ip1 = in_rec['attributes']['fixed_ips']
    ip = ip1[0]['ip_address']
    port_id = in_rec['attributes']['id']
    port_dict = {'name': in_rec['attributes']['name'], 'MAC_address' : in_rec['attributes']['mac_address'], 'IP_address' : ip , 'port_id': port_id}
    return port_dict

def write_net(in_rec,stackname): #function used to write needed net's info to dictionary
    in_rec = heat.resources.get(stackname, in_rec['resource_name']).to_dict()   
    seg_id = in_rec['attributes']['provider:segmentation_id']
    net_name = in_rec['attributes']['name']
    net_id = in_rec['attributes']['id']
    sub_id = in_rec['attributes']['subnets']
    sub_name = net_name.replace(':net',':subnet')
    obj = open ('tested.json', 'a+')
    json.dump(in_rec, obj,indent=4, sort_keys=True)
    net_dict = {'segmentation_id': seg_id, 'net_name': net_name, 'net_id' : net_id, 'subnet_id': sub_id[0], 'subnet_name': sub_name}
    return net_dict

parser = argparse.ArgumentParser()   #here starts the good part. handler for arguments passed 
parser.add_argument("-cf", "--configuration", nargs=4, help="pass the cloud url, username, password and tenant name",
                    required=True)  # option configurations, needs to be required
parser.add_argument("-d", "--delete", help="delete this stack")  # option delete
parser.add_argument("-s", "--status", help="return the status of the stack")  # option status
parser.add_argument("--composition", help="return the status of the stack")  # option status
parser.add_argument("-c", "--create", nargs=2,
                    help="create this stack with the name, give two arguments")  # option create stack
parser.add_argument("-n", "--new", nargs=2, help="create a new stack with the name provided and the big string inserted ") #usage: -ci stackname "big string to transform to heat template"
parser.add_argument("-t", "--test", help="test this stack") # option delete 

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


if args.composition:  # action from the status option
    stackname = args.composition
    stack = heat.resources.list(stackname)
    server_list = []   #instantiate lists 
    port_list = []
    net_list = []
    floatingIP_list = []
    router_list = []
    subnet_list = []
    for item in stack:    #for every resource item in the stack
        stack_res = item.to_dict()
        type_res = stack_res['resource_type']
        if type_res=='OS::Nova::Server':
            dic = write_server(stack_res,stackname)
            server_list.append(dic)
        elif type_res=='OS::Neutron::Subnet':
            in_rec = heat.resources.get(stackname, stack_res['resource_name']).to_dict() #get more info of the resource 
            cidr = in_rec['attributes']['cidr']
            sub_id = in_rec['physical_resource_id']
            subnet_dic = {'subnet_id': sub_id, 'cidr': cidr}
            subnet_list.append(subnet_dic)
        elif type_res=='OS::Neutron::FloatingIP':
            in_rec = heat.resources.get(stackname, stack_res['resource_name']).to_dict()
            float_ip = in_rec['attributes']['floating_ip_address']
            port_id = in_rec['attributes']['port_id']
            floating_dic = {'floating_ip':  float_ip , 'port_id': port_id}
            floatingIP_list.append(floating_dic)
        elif type_res == 'OS::Neutron::Port':
            port_dic = write_port(stack_res,stackname)
            port_list.append(port_dic)
        elif type_res == 'OS::Neutron::Router':
            router_dic = {'router_name': stack_res['resource_name'], 'router_id': stack_res['physical_resource_id']}
            in_rec = heat.resources.get(stackname, stack_res['resource_name']).to_dict()
            router_list.append(router_dic)
        elif type_res == 'OS::Neutron::Net': 
            net_dic = write_net(stack_res,stackname)
            net_list.append(net_dic)

    servers_dict = {'servers' : server_list }
    for port in port_list:
        for fl in floatingIP_list:
            if fl['port_id'] == port['port_id']:
                port['floating_IP']= fl['floating_ip']
                floatingIP_list.remove(fl)
        del port['port_id']
    ports_dict = { 'ports' : port_list }

    for net in net_list:
        for sub in subnet_list:
            if sub['subnet_id'] == net['subnet_id']:
                net['cidr']=sub['cidr']
                subnet_list.remove(sub)
    net_dict = { 'nets': net_list}
    
    routers_dict = { 'routers' : router_list }
    final_dict = {'servers' : server_list, 'ports' : port_list, 'nets': net_list, 'routers': router_list }
    #obj = open ('output.json','w')   #write to file...used fo testing
    # json.dump(final_dict, obj,indent=4, sort_keys=True)
    print final_dict

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

