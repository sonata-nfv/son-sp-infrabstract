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
		order_pop = data["order_pop"]
		logging.debug("Calling set_condition method")
		utils.set_condition(cond_name,in_seg, out_seg)
		logging.info("Conditon set completed")
		### TODO 
		##  Implement logic to connection with database getting switch IDs and etcetera 
		### We need. the Vbridge where it will be. the Interface for in & out 

		# utils.set_redirect(cond_name, vbr, port_id_in, port_id_out)

		flow = {'data': data}
		flows.append(flow)
		logging.debug("call to POST new flow chart")
		return data

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

