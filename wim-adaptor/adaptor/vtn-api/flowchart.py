import logging
from flask_restful import Resource
from flask import jsonify, request
import utils

flows = []

class FlowChart(Resource):
	
	

	def get(self):
		logging.debug("call for flow chart. Returning: " + str(flows))
		return str(flows)

	def post(self):
		data = request.json
		cond_name = data["instance_id"]
		in_seg = data["in_seg"]
		out_seg = data["out_seg"]
		pops = data["ports"]
		ordered_pop =[]
	
		for item in pops:
			ordered_pop.append((item["port"],item["order"]))
		ordered_pop.sort(key=lambda tup: tup[1])
		logging.debug("Ordered the port list")
		# Put the incoming PoPs in order 
		logging.debug("Calling set_condition method")
		utils.set_condition(cond_name,in_seg, out_seg)
		logging.info("Conditon set completed")
		port_in, vbr1 = utils.get_switch(in_seg)
		port_out, vbr2 = utils.get_switch(ordered_pop[0][0])
		logging.info("Redirect from SOURCE to First PoP completed")
		# Redirecting through the PoPs now
		for i in range(1,len(ordered_pop)):
		    port_in, vbr1 = utils.get_switch(ordered_pop[i-1][0])
		    logging.debug("port coming is : "+port_in+" with vbridge "+vbr1)
		    port_out, vbr2 = utils.get_switch(ordered_pop[i][0])
		    logging.debug("port to redirect is : "+port_out+" with vbridge "+vbr2)
		    utils.set_redirect(cond_name, vbr1, port_in, port_out)
		logging.debug(" Inter PoP redirections completed ")
	
		# TODO
		# Need to implement (or not) going from last PoP to Outer Segment -- leaving Wan 
		#Just add to the flow array 
		flow = {'data': data}
		flows.append(flow)
		logging.debug("call to POST new flow chart")
		return 200

class Flows(Resource):

	def get(self, res_name):
		for flow in flows:
			if flow['data']['instance_id'] == res_name:
				return flow 

	def delete(self, res_name):
		logging.debug("Call to delete condition: " +res_name)
		utils.delete_condition(res_name)
		for flow in flows:
			if flow['data']['instance_id'] == res_name:
				flows.remove(flow)
		return "Success"

