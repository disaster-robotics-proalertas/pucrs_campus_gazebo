#!/usr/bin/env python
"""Agent class to publish the robot pose"""

import rospy
from geometry_msgs.msg  import Twist
from nav_msgs.msg import Odometry
from turtlesim.msg import Pose
from math import pow,atan2,sqrt
class AgentPublisher():

    def __init__(self, positionGoalX, positionGoalY):
       
        rospy.init_node('check_odometry')  
        self.velocity_publisher = rospy.Publisher('/cmd_vel', Twist, queue_size=10)
        self.pose_subscriber = rospy.Subscriber('/odom', Odometry, self.callback)
        self.pose = Pose()
        self.goal_pose = Pose()

        self.goal_pose.x = positionGoalX
        self.goal_pose.y = positionGoalY
        self.rate = rospy.Rate(10)

     #Callback function implementing the pose value received
    def callback(self, data):
        self.pose.x = round(data.pose.pose.position.x, 4)
        self.pose.y = round(data.pose.pose.position.y, 4)

    def get_distance(self, goal_x, goal_y):
        distance = sqrt(pow((goal_x - self.pose.x), 2) + pow((goal_y - self.pose.y), 2))
        return distance

    def moveToGoal(self):
        distance_tolerance = 2
        vel_msg = Twist()

        while sqrt(pow((self.goal_pose.x - self.pose.x), 2) + pow((self.goal_pose.y - self.pose.y), 2)) >= distance_tolerance:
            vel_msg.linear.x = 1.5 * sqrt(pow((self.goal_pose.x - self.pose.x), 2) + pow((self.goal_pose.y - self.pose.y), 2))
            vel_msg.linear.y = 0
            vel_msg.linear.z = 0

            vel_msg.angular.x = 0
            vel_msg.angular.y = 0
            vel_msg.angular.z = 4 * (atan2(self.goal_pose.y - self.pose.y, self.goal_pose.x - self.pose.x) - self.pose.theta)
          
            #Publishing our vel_msg
            self.velocity_publisher.publish(vel_msg)
            self.rate.sleep()
        #Stopping our robot after the movement is over
        vel_msg.linear.x = 0
        vel_msg.angular.z =0
        self.velocity_publisher.publish(vel_msg)

        rospy.spin()




