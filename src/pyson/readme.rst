===============
Pyson interface
===============

The main.py file contains the integration both with AgentSpeak and with ROS.

Code explained
--------------

The main.py file is composed by an example scenario. To understant how to run it, check docs/source/pyson

Instantiating an agent
-----------------------

To instantiate a new agent, inform the literal it will respond to, the actions and its name, as in:

	with open(os.path.join(os.path.dirname(__file__), "delivery_agent.asl")) as source:
    deliveryAgent1 = env.build_agent(source, actions, name="deliveryAgent1")

Changing the agent belief
-------------------------

To change the agent belief, instantiate a new intention and a new term with the new position, and add the belief, as in:
	
	intention = pyson.runtime.Intention()

	term = pyson.Literal("pose", (7, 8))

	pyson.runtime.add_belief(term, collectPoint2, intention)

	env.run()


Agent Publisher
---------------

This is the responsible for sending the information of where to go for the robot. The *go_to* function receives this infomatation from the AgentSpeak.

	@actions.add_function(".go_to", (int,int, pyson_str))

	def go_to(x,y,name):

		agent = AgentPublisher(x, y, name)

		return 1




For information on how to install Pyson, check: https://github.com/niklasf/pyson.
