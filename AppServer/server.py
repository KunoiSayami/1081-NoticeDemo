import time
import json
from http.server import BaseHTTPRequestHandler, HTTPServer
import json
import cgi

def build_status_json(status: int, options: list, errors: dict):
	'''
		errors structure should be: {code: ... , info: ...}
	'''
	return json.dumps({'status': status, 'options': options, 'errors': errors}).encode()

# https://gist.github.com/nitaku/10d0662536f37a087e1b
class Server(BaseHTTPRequestHandler):

	conn = None

	def _set_headers(self):
		self.send_response(200)
		self.send_header('Content-type', 'application/json')
		self.end_headers()

	def do_HEAD(self):
		self._set_headers()

	# GET sends back a Hello world message
	def do_GET(self):
		self._set_headers()
		self.wfile.write(build_status_json(200, [], 0))

	def getJsonObject(self, *args, **kwargs):
		return 200, [], {'code': 0, 'info': ''}

	# POST echoes the message adding a JSON field
	def do_POST(self):
		ctype, pdict = cgi.parse_header(self.headers.get_content_type())

		# refuse to receive non-json content
		if ctype != 'application/json':
			self.send_response(400)
			self.end_headers()
			return

		# read the message and convert it into a python dictionary
		length = int(self.headers.get('content-length'))
		message = json.loads(self.rfile.read(length))

		# add a property to the object, just to mess with data
		message['received'] = 'ok'

		status, options, errors = self.getJsonObject()

		# send the message back
		self._set_headers()
		self.wfile.write(build_status_json(status, options, errors))

if __name__ == "__main__":
		hts = HTTPServer(('127.0.0.1', 8080), Server)
		hts.serve_forever()