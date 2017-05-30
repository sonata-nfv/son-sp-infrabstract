from newim import get_info
import requests
import json
import logging 
from sqlalchemy import create_engine

e = create_engine('sqlite:///database/wim_info.db')

def get_switch(segment):
	conn = e.connect()
	query = conn.execute('SELECT port_id, bridge_name FROM connectivity WHERE segment="%s";'%segment)
	dt = query.fetchone()
	#implement try 
	port, switch = dt[0],dt[1]
	return port, switch

def set_condition(cond_name, source, dest):
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
    r = requests.post(url + s_url, headers=headers,
                      auth=(username, password), json=data)
    logging.debug("Got this as response: " +str(r) )
    if not r.status_code == 200:
        logging.error('FLOW COND ERROR ' + str(r.status_code))
        #exit(1)


def delete_condition(cond_name):
    s_url = 'operations/vtn-flow-condition:remove-flow-condition'
    username, password, host, url, headers = get_info()
    data = {'input': {'name': cond_name}}
    r = requests.post(url+s_url, headers=headers, auth=(username, password), json=data)
    logging.debug("Got response:" +str(r))
    if not r.status_code == 200:
    	logging.error("Condition removal ERROR " + str(r.status_code))

def set_redirect(cond_name, vbr, port_id_in, port_id_out):
    s_url = 'operations/vtn-flow-filter:set-flow-filter'
    username, password, host, url, headers = get_info()
    data = {"input": {"output": "false", "tenant-name": vtn_name, "bridge-name": vbr, "interface-name": port_id_in, "vtn-flow-filter": [
        {"index": "1", "condition": cond_name, "vtn-redirect-filter": {"redirect-destination": {"bridge-name": vbr, "interface-name": port_id_out}, "output": "true"}}]}}
    '''
     this: curl --user "username":"pass" -H "Content-type: application/json" -X POST http://localhost:8181/restconf/operations/vtn-flow-filter:set-flow-filter
      -d '{"input":{"output":"false","tenant-name":"vtn1", "bridge-name":"vbr", interface-name":"if5", "vtn-flow-filter":[{"condition":"cond_1","index":"1","vtn-redirect-filter":
      {"redirect-destination":{"bridge-name":"vbr1","interface-name":"if3"},"output":"true"}}]}}'
	'''
    r = requests.post(url + s_url, headers=headers,
                      auth=(username, password), json=data)
    if not r.status_code == 200:
        logging.error('FLOW FILTER ERROR ' + str(r.status_code))
        #exit(1)


def get_vtn_name():
    s_url = 'operational/vtn:vtns/'
    logging.debug(" Got request for VTN name ")
    username, password, host, url, headers = get_info()
    r = requests.get(url + s_url, headers=headers, auth=(username, password))
    json_data = json.loads(r.text)
    # at the moment there is only on vtn tenant, so one name. TODO --- think
    # about if more
    name = json_data['vtns']['vtn'][0]['name']
    logging.info("VTN name recieved. Sending back")
    return name



