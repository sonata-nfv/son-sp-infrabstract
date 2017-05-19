import logging
from flask_restful import Resource
from flask import jsonify

class FlowChart(Resource):
	
	flows  = [] # TODO make this dictionary and return normally
	
	def get(self):
		logging.debug("call for flow chart. Returning: " + str(self.flows))
		return str(self.flows)