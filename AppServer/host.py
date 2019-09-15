from libpy3.mysqldb import mysqldb
from configparser import ConfigParser
from http.server import HTTPServer
from server import Server

class appServer:
    def __init__(self):
        self.config = ConfigParser()
        self.config.read('config.ini')

        self.mysql_conn = mysqldb(
            self.config['mysql']['host'],
            self.config['mysql']['user'],
            self.config['mysql']['password'],
            self.config['mysql']['database']
        )

        setattr(Server, 'conn', self.mysql_conn)
        self.server_handle = HTTPServer(
            (self.config['server']['address'], self.config['server']['port']),
            Server)


    def run(self):
        self.server_handle.serve_forever()
