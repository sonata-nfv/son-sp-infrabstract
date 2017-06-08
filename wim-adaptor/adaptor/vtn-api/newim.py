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
__author__ = "Stavros Kolometsos - NCSR Demokritos, Dario Valocchi(Ph.D.) - UCL"

import argparse,parser
import requests
import socket 
import json
import utils
from flowchart import FlowChart, Flows, Location
import logging
from flask import Flask, jsonify
from flask_restful import Resource, Api

app = Flask(__name__)
api = Api(app)

def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    return s.getsockname()[0]

def get_info():
    logging.debug("Request for info")
    return username, password, host, url, headers



parser = argparse.ArgumentParser()   #handler for arguments passed 
parser.add_argument("-v", "--host",help="Enter the address for the host containing VTN",type=str, required=True)  # option configurations, needs to be required
parser.add_argument("-u", "--user",help="Enter Username",type=str,required=True)
parser.add_argument("-p", "--password",help="Enter Password",type=str, required=True)
args = parser.parse_args()

if args.host:
    host = args.host
if args.user:
    username = args.user
if args.password:
    password = args.password


url = 'http://'+host+':8181/restconf/' #this is should be the same always
headers = {'Content type' : 'application/json'} #also this


api.add_resource(FlowChart, '/flowchart/')
api.add_resource(Flows, '/flowchart/<string:res_name>')
api.add_resource(Location, '/location/')

if __name__ == "__main__":
    logging.basicConfig(level=logging.DEBUG, filename='log.log', format='%(asctime)s - %(levelname)s - %(message)s')
    vtn_name = utils.get_vtn_name()
    logging.debug("VTN name recieved: " + vtn_name)
    local = get_ip()
    app.run(debug=True,host=local)

