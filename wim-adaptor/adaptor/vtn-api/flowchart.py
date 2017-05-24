import logging
from flask_restful import Resource
from flask import jsonify, request
import utils

class FlowChart(Resource):
	
	flows  = []

	def get(self):
		logging.debug("call for flow chart. Returning: " + str(self.flows))
		return str(self.flows)

	def post(self):
		data = request.json
		cond_name = data["instance_id"]
		in_seg = data["in_seg"]
		out_seg = data["out_seg"]
		ports = data["ports"]
		logging.debug("Calling set_condition method")
		utils.set_condition(cond_name,in_seg, out_seg)
		logging.info("Conditon set completed")
		### TODO 
		##  Implement logic to connection with database getting switch IDs and etcetera 
		### We need. the Vbridge where it will be. the Interface for in & out 

		# utils.set_redirect(cond_name, vbr, port_id_in, port_id_out)

		flow = {'data': data}
		self.flows.append(flow)
		logging.debug("call to POST new flow chart")
		return data

	def delete(self):
		data = request.jso
		cond_name = data["instance_id"]
		logging.debug("Call to delete condition: "+cond_name)
		utils.delete_condition(cond_name)
		return "Success"
