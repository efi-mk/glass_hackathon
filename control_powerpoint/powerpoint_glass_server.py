

from socket import *
import sys
import select
import time
import os

HOST = '192.168.2.104'
PORT = 8989

address = (HOST,PORT)
server_socket = socket(AF_INET,SOCK_DGRAM)
server_socket.bind(address)


while (1):
        recv_data, addr = server_socket.recvfrom(2048)
	print recv_data
	if (recv_data == "b"):
		command = "P"
	if (recv_data == "f"):
		command = "N"
	os.system("sendkeys.vbs " + command)