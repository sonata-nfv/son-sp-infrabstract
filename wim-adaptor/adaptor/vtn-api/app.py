from flask import Flask, jsonify
from flask_restful import Resource, Api


app = Flask(__name__)
api = Api(app)

class Hello(Resource):
	def get(self):
		message = {"Hello" : "VTN Master"}
		return jsonify(message)

api.add_resource(Hello, '/')

if __name__ == '__main__':
	app.run(debug=True)