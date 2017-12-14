#!/usr/bin/env python
import rospy
from nav_msgs.msg import Odometry
import collections

"""Agent class to subscribe to robot pose"""
class AgentSubscriber:

	def __init__(self, robot_name):
		self.name=robot_name
		
	def callback_position(data):
		posX = data.pose.pose.position.x
		posY = data.pose.pose.position.y
		Point = collections.namedtuple('Position', ['x', 'y'])
		p = Point(posX, posY)
		print p
		return p

	rospy.init_node('check_odometry')     #change this for the robot node namedtupl
	odom_sub = rospy.Subscriber(robot_name+'/odom', Odometry, callback_position)
	rospy.spin()
