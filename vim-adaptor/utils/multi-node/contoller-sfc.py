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
__author__= "Christos Sakkas - NCSR Demokritos, Stavros Kolometsos - NCSR Demokritos, Dario Valocchi(Ph.D.) - UCL"


# The SFC Agent for the controller node of the PoP 
import socket
import sys
import os
import time
import json
import argparse
import parser
import logging 

#### FUNCTIONS
def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    return s.getsockname()[0]


### Logging Config ### 
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
fh = logging.FileHandler('sfc_controller.log')
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
fh.setFormatter(formatter)
logger.addHandler(fh)


#configurations 
parser = argparse.ArgumentParser()   #handler for arguments passed 
parser.add_argument("-s", "--server",help="pass the local server ip. If not, it finds it automatically",type=str)  # option configurations, needs to be required
parser.add_argument("-i", "--brint",help="pass the connection of br-int to br-tun port, or use default '2' ",type=str)
parser.add_argument("-p", "--provider",help="pass the connection of br-provider to br-int port, or use default '2' ",type=str)

parser.add_argument("-t", "--tun",help="pass the connection of br-tun to br-int port, or use default '2' ",type=str)   # TODO number of nodes. and port corresponding to nodes
 
args = parser.parse_args()  # pass the arguments to the parser
# default values for ports 
brintTun = "2"
brintport = "2"
brprovider = "2"

logger.info("")
logger.info("===SONATA PROJECT===")
logger.info("Starting SFC Agent")

if args.server:  # parse the server adress passed
    server = args.server
else:
    server = get_ip() #finds IP to set up the socket 
if args.brint:
    brintport = args.brint
if args.tun:
   brintTun = tun
if args.provider:
    brprovider = args.provider 


# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

server_address = (server, 55555)
logger.info('starting up on %s port %s' % server_address)
sock.bind(server_address)

while True: 
    logger.info(" --- Waiting for data ---")
    data, address = sock.recvfrom(4096)
    logger.info("Recieved data from:" + str(address))
    jsonResponse=json.loads(data)
    returnflag = "SUCCESS"    
    try: 
        jsonMANA = jsonResponse["action"] # Check json request type 
        uuid = jsonResponse["instance_id"]
    except KeyError:
        message="There is some error with json file"
        logger.error(message)
        sock.sendto(message, address)
        continue
    if (jsonMANA=="add"):
        try:
            src = jsonResponse["in_segment"]
            dst = jsonResponse["out_segment"]
        except:
            message="There is some error with json file"
            print message
            logger.error(message)
            sock.sendto(message, address)
            continue

        logger.info("Json message succesfully ACCEPTED : "+data)
        logger.info("SOURCE SEGMENT -> "+src)
        logger.info("DESTINATION SEGMENT -> "+dst)
        fo = open(uuid, "w")
        # Starting to place rules, beggining from driving external traffic inside to br-tun 
        logger.info("PoP incoming traffic to br-provider :")
        logger.info("ovs-ofctl add-flow br-provider priority=66,dl_type=0x0800,in_port=1,nw_src="+src+",nw_dst="+dst+",actions=output:"+brprovider)
        os.system("ovs-ofctl add-flow br-provider priority=66,dl_type=0x0800,in_port=1,nw_src="+src+",nw_dst="+dst+",actions=output:"+brprovider)
        fo.write("ovs-ofctl --strict del-flows br-provider priority=66,dl_type=0x0800,in_port=1,nw_src="+src+",nw_dst="+dst+"\n")

        # From br-int towards br-tun 
        logger.info("PoP from br-int to br-tun :")
        logger.info("ovs-ofctl add-flow br-int priority=66,dl_type=0x800,in_port=1,nw_src="+src+",nw_dst="+dst+",actions=output:"+brintport)
        os.system("ovs-ofctl add-flow br-int priority=66,dl_type=0x800,in_port=1,nw_src="+src+",nw_dst="+dst+",actions=output:"+brintport)
        fo.write("ovs-ofctl --strict del-flows br-int priority=66,dl_type=0x0800,in_port=1,nw_src="+src+",nw_dst="+dst+"\n")

        #Now the traffic is inside br-tun, and logic must be applied to find out at which node has to go 

