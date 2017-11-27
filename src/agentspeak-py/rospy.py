#!/usr/bin/env python
import rospy
from std_msgs.msg import String

rospy.Subscriber("odom", Odometry, self.Position)

def Position(self, msg):
    print msg.pose.pose