
===============
Building of a Modular Scenario in the Gazebo
===============


To run the example we created, you need:

1. Ubuntu 16 with `Kinect <http://wiki.ros.org/kinetic/Installation/Ubuntu>`_ distribution installed (ros-kinetic-desktop-full)
2. Python


1. Load the Gazebo environment
--------

Run the command: 

	$ gazebo 

to load the empty environment.


2. Creating the environment
--------

Select the **Insert** tab and click on **connecting to model database** *(http://gazebosim.org/models/)*.

Select the model and position it in the environment.

There are options for **Translation Mode**, **Rotation Mode** and **Scale Mode**.

After setting, save the world.


3. Loading Turtlebot in the new world
--------

Run the command: 

	$ roslaunch turtlebot_gazebo turtlebot_world.launch world_file:=/opt/ros/kinetic/share/turtlebot_gazebo/worlds/campus.world



You will see a 3D scenario in the Gazebo Simulator and a Turtlebot at the point of origin.



