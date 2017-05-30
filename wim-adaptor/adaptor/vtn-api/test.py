import logging 
import requests
import utils 
def get_info():
    logging.debug("Request for info")
    return username, password, host, url, headers

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



password = "admin"
host = "10.30.0.13"
url = 'http://' + host + ':8181/restconf/'
headers = {'Content type': 'application/json'}
username = "admin"
headers = {'Content type': 'application/json'}

cond_name = "testing"
source = "10.100.50.50/24"
dest = "10.100.41.60/24"

#set_condition(cond_name, source, dest)

data = {"ports": [{"port":"22", "order":"2"},{"port":"10.100.32.200/24", "order":"1"},{"port":"55", "order":"5"},{"port":"44", "order":"4"},{"port":"33", "order":"3"},{"port":"66", "order":"6"}]}
print data

pops = data["ports"]
ordered_ports =[]
for item in pops:
    ordered_ports.append((item["port"],item["order"]))
ordered_ports.sort(key=lambda tup: tup[1])
logging.debug("Ordered the port list")

for i in range(1,len(ordered_ports)):
    port_in, vbr1 = utils.get_switch(ordered_ports[i-1][0])
    logging.debug("port coming is : "+port_in+" with vbridge "+vbr1)
    port_out, vbr2 = utils.get_switch(ordered_ports[i][0])
    logging.debug("port to redirect is : "+port_out+" with vbridge "+vbr2)
    utils.set_redirect(cond_name, vbr1, port_in, port_out)
logging.debug(" Inter PoP redirections completed ")
