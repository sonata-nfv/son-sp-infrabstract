# The SFC agent for the compute node of the PoP 
import socket
import logging
import json

def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    return s.getsockname()[0]

def order_pairs(pairs):    ## TODO --- FIX IT
    ordered_pairs = []
    for item in pairs:
        ordered_pairs.append((item["port"],item["order"],item["vc_id"]))
    ordered_pairs.sort(key=lambda tup: tup[1])
    logger.debug("Ordered the PoP list")
    return ordered_pairs

### Logging Config ### 
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
fh = logging.FileHandler('sfc.log')
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
fh.setFormatter(formatter)
logger.addHandler(fh)

## Main part 
logger.info("")
logger.info("===SONATA PROJECT===")    
logger.info("Starting SFC Node Agent")

server = get_ip()
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = (server, 55555)
logger.info('starting up on %s port %s' % server_address)
sock.bind(server_address)
sock.listen(5)


data = {"action": "add", "pairs": [{"node": "compute1", "out": ["fa:16:3e:00:c8:7e", 1, "fb26a078-b2e9-4767-8b55-723b4ddb35f5"], 
"order": 0, "in": ["fa:16:3e:90:11:b5", 0, "fb26a078-b2e9-4767-8b55-723b4ddb35f5"]}], "exit": "compute2"}

while true:
	logger.info(" --- Waiting for data ---")
	conn, address = sock.accept()
	logger.info("connection established with "+str(address))
	data = conn.recv(4096)
	jsonResponse=json.loads(data)
	returnflag = "SUCCESS"
    try:
        jsonMANA = jsonResponse["action"] # Check json request type
        #uuid = jsonResponse["instance_id"]
        # TODO ----- ADD uuid in message send from controller
        exit = jsonResponse["exit"] 
    except KeyError:
        message="There is some error with json file"
        logger.error(message)
        conn.send(message)
        conn.close()
        continue
    if (jsonMANA=="add"):
        try:
            src = jsonResponse["in_segment"]
            dst = jsonResponse["out_segment"]
            pairs = jsonResponse["pairs"] #get the unordered port list
            logging.debug("Starting ordering PoPs")
            pairlist = order_pairs(pairs) # pass it the function to order it
            # TODO       FIX IT 
        except:
            message="There is some error with json file"
            logger.error(message)
            conn.send(message)
            conn.close()
            continue

        logger.info("Json message succesfully ACCEPTED : "+data)
        logger.info("SOURCE SEGMENT -> "+src)
        logger.info("DESTINATION SEGMENT -> "+dst)	

        



		#logging.info("Data received: "+jsonResponse)
		conn.send("SUCCESS")
		conn.close()