import logging
from flask_restful import Resource, abort
from flask import jsonify, request
import utils

flows = []

class FlowChart(Resource):
	
	

	def get(self):
		logging.debug("call for flow chart. Returning: " + str(flows))
		logging.info("Call for flow accepted")
		return jsonify(flows = flows)

	def post(self):
		logging.info("POST API call incoming")
		data = request.json
		logging.debug("Recieved the following: "+str(data))
		cond_name = data["instance_id"]
		in_seg = data["in_seg"]
		out_seg = data["out_seg"]
		pops = data["ports"]
		logging.debug("Starting ordering PoPs")
		ordered_pop = utils.order_pop(pops) 
		# Put the incoming PoPs in order 
		logging.info("Calling set_condition method")
		flag = utils.set_condition(cond_name,in_seg, out_seg)
		logging.debug("Flag incoming:" +str(flag))
		if flag != 200:
			abort(500, message="Set condition uncompleted")
		logging.info("Condition set completed")

		port_in, vbr1 = utils.get_switch(in_seg)
		port_out, vbr2 = utils.get_switch(ordered_pop[0][0])
		utils.set_redirect(cond_name, vbr1, port_in, port_out)
		logging.info("Redirect from source to First PoP completed")

		# Redirecting through the PoPs now
		logging.debug("Redirect traffic through PoPs")
		for i in range(1,len(ordered_pop)):
		    port_in, vbr1 = utils.get_switch(ordered_pop[i-1][0])
		    logging.debug("port coming is: "+port_in+" with vbridge "+vbr1)
		    port_out, vbr2 = utils.get_switch(ordered_pop[i][0])
		    logging.debug("port to redirect is: "+port_out+" with vbridge "+vbr2)
		    utils.set_redirect(cond_name, vbr1, port_in, port_out)
		logging.debug(" Inter PoP redirections completed ")
	
		# TODO
		# Need to implement (or not) going from last PoP to Outer Segment -- leaving Wan 
		#Just add to the flow array 
		flow = {'data': data}
		flows.append(flow)
		logging.info("Posting new flow completed.")
		return 200

class Flows(Resource):

	def get(self, res_name):
		logging.info("Requesting "+res_name+" flow")
		for flow in flows:
			if flow['data']['instance_id'] == res_name:
				return flow
		abort(404, message="Resource not found") 

	def delete(self, res_name):
		logging.debug("Call to delete condition: " +res_name)
		
		for flow in flows:
			if flow['data']['instance_id'] == res_name:
				flows.remove(flow)
				logging.info("Resource found. Proceed to removal")
				flag = utils.delete_condition(res_name)
				if flag == 200: 
					return "Resource was deleted"
				else:
					abort(406, message="Resource was not found in VTN and not deleted")
		logging.info("Resource not found. No action taken")
		abort(404, message="Resource not found")

class Location(Resource):

	def get(self):
		logging.info("Request for Location Information incoming ")
		locations = utils.get_locations()
		if not locations:
			abort(500, message = "Unknown error, Locations couldn't be received")
		return jsonify(locations = locations)
