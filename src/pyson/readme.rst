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


Agent Subscriber
-------------

This is the responsible for getting the robot position. The node *check_odometry* sends the position back to the callback and
already inform Pyson interface to alter the agent's belief.

	def execute():
		def callback(data):
			posX = data.pose.pose.position.x
			posY = data.pose.pose.position.y
			Point = collections.namedtuple('Position', ['x', 'y'])
			position = Point(posX, posY)

		
			term = pyson.Literal("execute", (position.x, position.y))
			intention = pyson.runtime.Intention()
			deliveryAgent1.call(pyson.Trigger.addition, pyson.GoalType.belief, term, intention)
			env.run()

	
	rospy.init_node('check_odometry')    
	odom_sub = rospy.Subscriber('/odom', Odometry, callback)
	rospy.spin()


Agent Publisher
---------------

This is the responsible for sending the information of where to go for the robot. The *go_to* function receives this infomatation from the AgentSpeak.

	@actions.add_function(".go_to", (int,int, ))
	def go_to(x,y):
		agent = AgentPublisher(x, y)
		agent.moveToGoal()
		return 1




For information on how to install Pyson, check: https://github.com/niklasf/pyson.
