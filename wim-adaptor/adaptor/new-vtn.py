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
__author__= "Stavros Kolometsos - NCSR Demokritos, Dario Valocchi(Ph.D.) - UCL"

import argparse
import requests 
import json

def set_condition(cond_name,vbr,source,dest):
	s_url = 'operations/vtn-flow-condition:set-flow-condition'
	data = {'input' : {'name': cond_name, 'vtn-flow-match': [{'index': '1','vtn-inet-match':{ 'source-network': source, 'destination-network': dest }}]}}
	r = requests.post(url+s_url, headers=headers, auth=(username,password), json=data) # this curl --user "username":"pass" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-flow-condition:set-flow-condition -d '{"input":{"name":"cond1", "vtn-flow-match":[{"index":"1", "vtn-inet-match":{"source-network":"10.0.0.1/32", "destination-network":"10.0.0.3/32"}}]}}'
	print r 
	if not r.status_code==200:
		print 'FLOW COND ERROR '+str(r.status_code)
		exit(1)

def set_redirect(cond_name,vbr,port_id_in,port_id_out):
	s_url = 'operations/vtn-flow-filter:set-flow-filter'
	data = {"input": { "output": "false", "tenant-name": vtn_name, "bridge-name": vbr, "interface-name":port_id_in, "vtn-flow-filter":[{"index": "1", "condition":cond_name, "vtn-redirect-filter":{"redirect-destination": {"bridge-name": vbr, "interface-name": port_id_out}, "output": "true"}}]}}
	r = requests.post(url+s_url, headers=headers, auth=(username,password), json=data) # this: curl --user "username":"pass" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-flow-filter:set-flow-filter -d -d '{"input":{"output":"false","tenant-name":"vtn1", "bridge-name": "vbr1", "interface-name":"if5","vtn-flow-filter":[{"condition":"cond_1","index":"1","vtn-redirect-filter":{"redirect-destination":{"bridge-name":"vbr1","interface-name":"if3"},"output":"true"}}]}}'
	if not r.status_code==200: 
		print 'FLOW FILTER ERROR '+str(r.status_code)
		exit(1)

def get_vtn_name():
	s_url='operational/vtn:vtns/'
	r = requests.get(url+s_url,headers=headers, auth=(username,password))
	json_data=json.loads(r.text)
	name = json_data['vtns']['vtn'][0]['name'] # at the moment there is only on vtn tenant, so one name. TODO --- think about if more 
	return name

''' Propably implented this way   

if args.configuration:
	host, username, password = args.configuration
	url = 'http://'+host+':8181/restconf/' #this is should be the same always
	headers = {'Content type' : 'application/json'} #also this 
'''

if __name__=="__main__":
	username="admin"
	password="admin"
	host = "10.30.0.13"
	url = 'http://'+host+':8181/restconf/'
	headers = {'Content type' : 'application/json'}

	vtn_name = get_vtn_name()

