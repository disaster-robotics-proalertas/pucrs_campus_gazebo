import AgentPublisher 
import AgentSubscriber

class AgentPyshon:

    def send():
        subscriber = new AgentSubscriber
        position = subscriber.callback_position()
        #send to pyson position.x, position.y


    def receive(posX, posY):
        publiser = new AgentPublisher(posX, posY)
        publiser.move2goal(posX, posY)