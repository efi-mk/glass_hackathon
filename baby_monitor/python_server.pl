#!/usr/bin/python
from BaseHTTPServer import BaseHTTPRequestHandler,HTTPServer
from urlparse import urlparse
import os

PORT_NUMBER = 8080

#This class will handles any incoming request from
#the browser 
class myHandler(BaseHTTPRequestHandler):
	
	#Handler for the GET requests
	def do_GET(self):
		if self.path == '/':
			self.send_response(200)
			f = open("img.jpg") #self.path has /test.html
			self.send_header('Content-type','image/jpeg')
			self.end_headers()
			self.wfile.write(f.read())
			f.close()
			return
		else:
			self.send_response(200)
			f = open("our_img.jpeg") #self.path has /test.html
			self.send_header('Content-type','image/jpeg')
			self.end_headers()
			self.wfile.write(f.read())
			f.close()
			return

	def do_POST(self):
		if self.path == '/store':
			length = self.headers['content-length']
			data = self.rfile.read(int(length))

			with open("our_img.jpeg", 'w') as fh:
				fh.write(data)
			os.system("sed -i '1,4d' our_img.jpeg")
			self.send_response(200)

try:
	#Create a web server and define the handler to manage the
	#incoming request
	server = HTTPServer(('', PORT_NUMBER), myHandler)
	print 'Started httpserver on port ' , PORT_NUMBER
	
	#Wait forever for incoming htto requests
	server.serve_forever()

except KeyboardInterrupt:
	print '^C received, shutting down the web server'
	server.socket.close()