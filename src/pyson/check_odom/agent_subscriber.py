#!/usr/bin/env python
import rospy
from nav_msgs.msg import Odometry
import collections

"""Agent class to subscribe to robot pose"""
class AgentSubscriber:

	def __init__(self):
		self.posX = ' '
		self.posY = ' '
		
	def callback_position(data):
		self.posX = data.pose.pose.position.x
		self.posY = data.pose.pose.position.y
		Point = collections.namedtuple('Position', ['x', 'y'])
		p = Point(posX, posY)
		print "POST"
		print p
		return p

	rospy.init_node('check_odometry')     #change this for the robot node namedtupl
	odom_sub = rospy.Subscriber('/odom', Odometry, callback_position)
	rospy.spin()
