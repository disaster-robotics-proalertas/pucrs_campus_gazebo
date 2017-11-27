import Queue, threading, socket
from time import sleep

class worker(threading.Thread):
    def __init__(self,q):
        super(worker,self).__init__()
        self.qu = q

    def run(self):
        while True:
            new_task=self.qu.get(True)
            print new_task
            i=0
            while i < 10:
                print "Listening ..."
                sleep(1)
                i += 1
                try:
                    another_task=self.qu.get(False)
                    print another_task
                except Queue.Empty:
                    pass          

task_queue = Queue.Queue()
w = worker(task_queue)
w.daemon = True
w.start()

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.bind(('localhost', 4200))
sock.listen(1)
try:
    while True:
        conn, addr = sock.accept()
        data = conn.recv(32)
        task_queue.put(data)
        conn.sendall("OK")
        conn.close()
except:
    sock.close()