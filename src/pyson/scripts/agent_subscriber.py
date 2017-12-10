#!/usr/bin/env python
import rospy
from nav_msgs.msg import Odometry
import collections
import AgentPyson as pyson

"""Agent class to subscribe to robot pose"""

class AgentSubscriber:

	def __init__(self, name):
		self.name = name
    	self.posX = ' '
    	self.posY = ' '

	def callback_position(msg):
	    posX = msg.pose.pose.position.x
	    posY = msg.pose.pose.position.y
		Point = collections.namedtuple('Position', ['x', 'y'])
		p = Point(posX, posY)
		print 'Agent '+ self.name + ' at ' + posX, posY
		return p


	rospy.init_node('check_odometry')     #change this for the robot node name
	odom_sub = rospy.Subscriber('/odom', Odometry, callback_position)
	rospy.spin()
