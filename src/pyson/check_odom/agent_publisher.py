#!/usr/bin/env python
"""Agent class to publish the robot pose"""

import rospy
from geometry_msgs.msg import Twist, Point, Quaternion
import tf
from nav_msgs.msg import Odometry
from turtlesim.msg import Pose
import PyKDL
from math import radians, copysign, sqrt, pow, pi


class AgentPublisher():

    def __init__(self, posX, posY, name):
       
      
        rospy.init_node('check_odometry', anonymous=False)
        rospy.on_shutdown(self.shutdown)

        rate = 20
        r = rospy.Rate(rate)
        
        self.goal_distance = rospy.get_param("~goal_distance", 10.0)      # meters
        self.goal_angle = rospy.get_param("~goal_angle", radians(90))    # degrees converted to radians
        self.linear_speed = rospy.get_param("~linear_speed", 0.8)        # meters per second
        self.angular_speed = rospy.get_param("~angular_speed", 0.7)      # radians per second
        self.angular_tolerance = rospy.get_param("~angular_tolerance", radians(2)) # degrees to radians
        
        # Publisher to control the robot's speed using the name of the robot
        self.cmd_vel = rospy.Publisher(name+'/cmd_vel', Twist, queue_size=5)
        self.base_frame = rospy.get_param('~base_frame', '/base_link')
        self.odom_frame = rospy.get_param('~odom_frame', '/odom')
        self.tf_listener = tf.TransformListener()
        self.odom_frame = '/odom'
       
        try:
            self.tf_listener.waitForTransform(self.odom_frame, '/base_link', rospy.Time(), rospy.Duration(1.0))
            self.base_frame = '/base_link'
        except (tf.Exception, tf.ConnectivityException, tf.LookupException):
            rospy.loginfo("Cannot find transform between /odom and /base_link")
            rospy.signal_shutdown("tf Exception")  
                
        position = Point()

        for i in range(4):
            move_cmd = Twist()
            
            move_cmd.linear.x = self.linear_speed
            (position, rotation) = self.get_odom()             
            x_start = position.x
            y_start = position.y
            distance = 0
            
            while distance < self.goal_distance and not rospy.is_shutdown():     
                self.cmd_vel.publish(move_cmd)
                r.sleep()
        
                (position, rotation) = self.get_odom()
                
                #Euclidean distance
                distance = sqrt(pow((position.x - x_start), 2) + 
                                pow((position.y - y_start), 2))
                
            # Stop the robot before rotating
            move_cmd = Twist()
            self.cmd_vel.publish(move_cmd)
            rospy.sleep(1.0)

            move_cmd.angular.z = self.angular_speed
            last_angle = rotation
            turn_angle = 0
            
            # Begin the rotation
            while abs(turn_angle + self.angular_tolerance) < abs(self.goal_angle) and not rospy.is_shutdown():    
                self.cmd_vel.publish(move_cmd) 
                r.sleep()
                (position, rotation) = self.get_odom()
                angle = rotation - last_angle

                res = angle
                while res > pi:
                    res -= 2.0 * pi
                while res < -pi:
                    res += 2.0 * pi
                
                delta_angle = res
                
                turn_angle += delta_angle
                last_angle = rotation

            move_cmd = Twist()
            self.cmd_vel.publish(move_cmd)
            rospy.sleep(1.0)            
        self.cmd_vel.publish(Twist())
 

    def get_odom(self):
        try:
            (trans, rot)  = self.tf_listener.lookupTransform(self.odom_frame, self.base_frame, rospy.Time(0))
        except (tf.Exception, tf.ConnectivityException, tf.LookupException):
            rospy.loginfo("TF Exception")
            return
        quat = Quaternion(*rot)
        rot = PyKDL.Rotation.Quaternion(quat.x, quat.y, quat.z, quat.w)

        return (Point(*trans), rot.GetRPY()[2])
            
    def shutdown(self):
        rospy.loginfo("Stopping the robot...")
        self.cmd_vel.publish(Twist())
        rospy.sleep(1)
