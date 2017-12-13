===============
Setting Up Pyson
===============

Pyson is a JASON-Style AgentSpeak interpreter for Python.

Currently this plugin is under development but we could not found the currently version.

To run the example we created, you need:

1. Ubuntu 14.04 with `Indigo <http://wiki.ros.org/indigo>`_ distribution installed. 
2. Python


And should follow this steps:

1. Install Pyson
2. Download the Grizzly robot
3. Add the launch file from to Grizzly
4. Copy the package node
5. Lauch Grizzly
6. Run Pyson


1. Install Pyson
-------- 

To install Pyson is simple. First, download Pyson source code from the `GitHub repository <https://github.com/niklasf/pyson>`_. Then, you need to execute in a terminal the command:

   
 	sudo python setup.py develop
 

This command will install all the Pyson's dependencies.


2. Download the Grizzly robot
-------- 

For this work, we use `Grizzly simulator <https://github.com/g/grizzly_simulator>`_. You need to download the source from GitHub and place it in *src* in your *catkin_ws*. After that, run:

	catkin_make
	
And

	source [YOUR PATH]/catkin_ws/devel/setup.bash


3. Add the launch file from to Grizzly
-------- 

We created an odometry node to get the robot position. You need to replace the file at *catkin_ws/src/grizzly_simulator/grizzly_gazebo/launch/base_gazebo.launch* for the file at */launch/Grizzly/base_gazebo.launch*.
The file on this repository already contains the node to get the positions information:

	<node pkg="check_odom" type="check_odometry" name="main.py" output="screen" >

Where: 

- The pkg is the name of the package. 
- The type is the name of the node described at our script
- The name is the name of our script
- Output is the way the information is printed

After you replace the file, run

	catkin_make
	
	
4. Copy the package node
-------- 

In this repository, inside src/pyson/ there is a folder named *check_odom*. You need to copy this folder to your *catkin_ws*.


5. Lauch Grizzly
-------- 

To launch Grizzly, run:

	sudo apt-get update
	
	sudo apt-get install ros-indigo-grizzly-simulator ros-indigo-grizzly-desktop ros-indigo-grizzly-navigation
	
	roslaunch grizzly_gazebo grizzly_empty_world.launch
	

6. Run Pyson
-------- 

Open other terminal and run:

	roscore

In other terminar, enter the folde *check_odom* you just copied to your catkin_ws and run:

	python main.py



You will se the plans and the actions on your console, and the robot moving on Gazebo.


