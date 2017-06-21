from newim import get_info
import requests
import json
import logging 
from sqlalchemy import create_engine

e = create_engine('sqlite:///database/wim_info.db')

def get_switch(segment):
	logging.debug("Incoming request for segment: "+segment)
	conn = e.connect()
	query = conn.execute('SELECT port_id, bridge_name FROM connectivity WHERE segment="%s";'%segment)
	dt = query.fetchone()
	#TODO implement try 
	port, switch = dt[0],dt[1]
	logging.info("get_switch method completed. Returning: "+port+" "+switch)
	return port, switch

def set_condition(cond_name, source, dest):
	logging.debug("Incoming set_condition call")
	s_url = 'operations/vtn-flow-condition:set-flow-condition'
	username, password, host, url, headers = get_info()
	data = {'input': {'name': cond_name, 'vtn-flow-match': [
	    {'index': '1', 'vtn-inet-match': {'source-network': source, 'destination-network': dest}}]}}
	'''
	 this curl --user "username":"pass" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-flow-condition:set-flow-condition
	# -d '{"input":{"name":"cond1", "vtn-flow-match":[{"index":"1",
	# "vtn-inet-match":{"source-network":"10.0.0.1/32",
	# "destination-network":"10.0.0.3/32"}}]}}'
	'''
	logging.debug("Sending request to VTN to implement condition "+cond_name)
	r = requests.post(url + s_url, headers=headers,
	                  auth=(username, password), json=data)
	logging.info("Got this as response: " +str(r) )
	if not r.status_code == 200:
	    logging.error('FLOW COND ERROR ' + str(r.status_code))
	return r.status_code

def delete_condition(cond_name):
    s_url = 'operations/vtn-flow-condition:remove-flow-condition'
    username, password, host, url, headers = get_info()
    data = {'input': {'name': cond_name}}
    logging.debug("Sending request to delete condition "+cond_name)
    r = requests.post(url+s_url, headers=headers, auth=(username, password), json=data)
    logging.info("Got response:" +str(r))
    if not r.status_code == 200:
    	logging.error("Condition removal ERROR " + str(r.status_code))
    return r.status_code

def set_redirect(cond_name, vbr, port_id_in, port_id_out):
	s_url = 'operations/vtn-flow-filter:set-flow-filter'
	logging.debug("Incoming set_redirect call")
	username, password, host, url, headers = get_info()
	vtn_name = get_vtn_name()
	data = {"input": {"output": "false", "tenant-name": vtn_name, "bridge-name": vbr, "interface-name": port_id_in, "vtn-flow-filter": [
	    {"index": "1", "condition": cond_name, "vtn-redirect-filter": {"redirect-destination": {"bridge-name": vbr, "interface-name": port_id_out}, "output": "true"}}]}}
	'''
	 this: curl --user "username":"pass" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-flow-filter:set-flow-filter
	  -d '{"input":{"output":"false","tenant-name":"vtn1", "bridge-name":"vbr", interface-name":"if5", "vtn-flow-filter":[{"condition":"cond_1","index":"1","vtn-redirect-filter":
	  {"redirect-destination":{"bridge-name":"vbr1","interface-name":"if3"},"output":"true"}}]}}'
	'''
	logging.debug("Sending request to set condition: "+str(data))
	r = requests.post(url + s_url, headers=headers,
	                  auth=(username, password), json=data)
	logging.info("Got response:" +str(r))
	if not r.status_code == 200:
	    logging.error('FLOW FILTER ERROR ' + str(r.status_code))

def get_vtn_name():
	s_url = 'operational/vtn:vtns/'
	logging.debug(" Got request for VTN name ")
	username, password, host, url, headers = get_info()
	r = requests.get(url + s_url, headers=headers, auth=(username, password))
	json_data = json.loads(r.text)
	# at the moment there is only on vtn tenant, so one name. TODO --- think
	# about if more
	name = json_data['vtns']['vtn'][0]['name']
	logging.info("VTN name recieved. Sending back: "+name)
	return name

def order_pop(pops):
	ordered_pop = []
	for item in pops:
		ordered_pop.append((item["port"],item["order"]))
	ordered_pop.sort(key=lambda tup: tup[1])
	logging.debug("Ordered the PoP list")
	return ordered_pop

def get_locations():
	logging.debug("Incoming request for location")
	conn = e.connect()
	query = conn.execute('SELECT segment, location FROM connectivity;')
	dt = query.fetchall()
	logging.debug("Show locations: " + str(dt))
	locations = []
	for d in dt:
		dicti = {"segment" : d[0], "location" : d[1]}
		locations.append(dicti)	
	return locations 	 
	
	
