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
__author__= "Stavros Kolometsos - NCSR Demokritos, Dario Valocchi(Ph.D.)-UCL"

import argparse
import yaml
import json
import requests



parser = argparse.ArgumentParser()
parser.add_argument("-df", "--drop-filter", help="drop the damn flow filter")  # option configurations, needs to be required
parser.add_argument ('-c','--configuration', help = 'important configuration inputs', nargs=3, metavar =('host','username','password'))
parser.add_argument('-i','--installation', help = 'configure the VTN', nargs =1, metavar =('vtn_name'))
parser.add_argument('-d','--delete', help = 'delete given VTN ', nargs =1, metavar =('vtn_name'))
parser.add_argument ('-sf','--set-flow', help = 'set flow condition', nargs=3, metavar =('condition','source_net','dest_net'))

args = parser.parse_args()  # pass the arguments to the parser
#host = '10.30.0.13' 
url = 'http://'+host+':8181/restconf/' #this is should be the same always
headers = {'Content type' : 'application/json'} #also this 
#username ='admin' # this should be probably be taken as input
#pasword = 'admin'

if args.drop_filter:  # actions to do from the drop_filter
    '''cip = args.drop_filter[0]
    username = args.drop_filter[1] '''
    #print ("yolo")

if args.configuration:
	host, username, password = args.configuration

# Create VTN 
if args.installation:
	vtn_name= args.installation[0]
	#create VTN 
	p_url = 'operations/vtn:update-vtn'
	data = {'input' : {'tenant-name': vtn_name}}
	
	r = requests.post(url+p_url, headers = headers, auth=(username, password), json=data) # this : curl --user "admin":"admin" -H "Content-type: application/json" -X POST http://10.30.0.13:8181/restconf/operations/vtn:update-vtn -d '{"input":{"tenant-name":"vtn1"}}'
	print r.status_code
	print r.json()

	#create vbridge in VTN
	vbr = 'vbr1'
	b_url ='operations/vtn-vbridge:update-vbridge'
	data = {'input': { 'tenant-name': vtn_name, 'bridge-name' : vbr}}
	r = requests.post(url+b_url, headers = headers, auth=(username, password), json=data) # this : curl --user "admin":"admin" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-vbridge:update-vbridge -d '{"input":{"tenant-name":"vtn1", "bridge-name":"vbr1"}}'
	
	#create interfaces in vbridge
	intf1 = 'if1'
	intf2 = 'if2'
	i_url = 'operations/vtn-vinterface:update-vinterface'
	data = {'input' : {'tenant-name': vtn_name, 'bridge-name': vbr, 'interface-name':intf1 }}
	r = requests.post(url+i_url, headers = headers, auth=(username, password), json=data)   # this : curl --user "admin":"admin" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-vinterface:update-vinterface -d '{"input":{"tenant-name":"vtn1", "bridge-name":"vbr1", "interface-name":"if1"}}'
	data = {'input' : {'tenant-name': vtn_name, 'bridge-name': vbr, 'interface-name':intf2 }}
	r = requests.post(url+b_url, headers = headers, auth=(username, password), json=data) # this: curl --user "admin":"admin" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-vinterface:update-vinterface -d '{"input":{"tenant-name":"vtn1", "bridge-name":"vbr1", "interface-name":"if2"}}'
	
	#set port mapping 
	#info here should be taken by file

	m_url ='operations/vtn-port-map:set-port-map' 
	file = open("configs.txt", "r+")
	node_name1 = file.readline()
	port_name1 = file.readline()
	node_name2 = file.readline()
	port_name2 = file.readline()
	
	data = {'input' : {'tenant-name': vtn_name, 'bridge-name': vbr, 'interface-name':intf1, 'node': node_name1 , 'port-name' : port_name1 }}
	r = requests.post(url+m_url, headers = headers, auth=(username, password), json=data) #this : curl --user "admin":"admin" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-port-map:set-port-map -d '{"input":{"tenant-name":"vtn1", "bridge-name":"vbr1", "interface-name":"if1", "node":"openflow:2", "port-name":"s2-eth1"}}'
	data = {'input' : {'tenant-name': vtn_name, 'bridge-name': vbr, 'interface-name':intf2, 'node': node_name2 , 'port-name' : port_name2 }}
	r = requests.post(url+m_url, headers = headers, auth=(username, password), json=data) #this : curl --user "admin":"admin" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-port-map:set-port-map -d '{"input":{"tenant-name":"vtn1", "bridge-name":"vbr1", "interface-name":"if2", "node":"openflow:3", "port-name":"s3-eth1"}}'


#set the flow between the source and destination address and then enable filter
if args.set_flow:
	cond, source_net, dest_net = args.set_flow

	s_url = 'operations/vtn-flow-condition:set-flow-condition'
	data = {'input' : {'name': cond, 'vtn-flow-match': [{'index': '1','vtn-inet-match':{ 'source-network': source_net, 'destination-network': dest_net }}]}}
	r = requests.post(url+s_url, headers=headers, auth=(username,password), json=data) # this curl --user "admin":"admin" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-flow-condition:set-flow-condition -d '{"input":{"name":"cond1", "vtn-flow-match":[{"index":"1", "vtn-inet-match":{"source-network":"10.0.0.1/32", "destination-network":"10.0.0.3/32"}}]}}'
	s_url = 'operations/vtn-flow-filter:set-flow-filter'
	intf1 = 'if1' # needed for data 
	data = {"input": {"tenant-name":  vtn_name , "bridge-name": vbr, "interface-name":intf1, "vtn-flow-filter":[{"index": "1", "condition":cond, "vtn-pass-filter":{}}]}}
	r = requests.post(url+s_url, headers=headers, auth=(username,password), json=data) # this: curl --user "admin":"admin" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-flow-filter:set-flow-filter -d '{"input": {"tenant-name": "vtn1", "bridge-name": "vbr1", "interface-name":"if1", "vtn-flow-filter":[{"index": "1", "condition":"cond1", "vtn-pass-filter":{}}]}}'


# Delete VTN 
if args.delete:
	vtn = args.delete[0]
	d_url = 'operations/vtn:remove-vtn'
	data = {'input': {'tenant-name': vtn }}
	r = requests.post(url + d_url, headers = headers, auth=(username,password), json= data) # this: curl --user "admin":"admin" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn:remove-vtn -d '{"input":{"tenant-name":"vtn1"}}'
	print r.status_code
	print r.text

