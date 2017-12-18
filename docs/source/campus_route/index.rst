
===============
ROS package with OSM using the dijkstra algorithm
===============


To run the example we created, you need:

1. Ubuntu 16 with `Kinect <http://wiki.ros.org/kinetic/Installation/Ubuntu>`_ distribution installed (ros-kinetic-desktop-full)
2. Python


1. `MichalDoris/osm_planner <https://github.com/MichalDobis/osm_planner>`_ repository clone
--------

Make the repository clone with the command: 

	$ git clone https://github.com/MichalDobis/osm_planner -b without_nav_core

Move the cloned folder to: 

	$ ~/catkin_ws/src


2. Compile the project
--------

Position the terminal in the 
	
	$ ~/catkin_ws

Run 

	$ catkin_make

Run 

	$ source devel/setup.bash


3. Change of parameter
--------

Open the file *osm_planner/config/ros_param.yaml*

Change the **robot_base_frame** parameter from *'base_link'* to *'map'*


4. Extracting the map of the selected area
--------

Go to the website `Open Street Map <http://www.openstreetmap.org/>`_.

Click the **Export** button at the top of the screen.

Fill in the latitude and longitude coordinate range (scroll through the coordinate fields by pressing a Tab key to view an area on the map).

Click the **Export** button  the left side of the screen (the file extension will be *.osm*).


5. Map editing
--------

Visit the site *https://josm.openstreetmap.de/wiki/Download* to download the `JOSM <https://josm.openstreetmap.de/wiki/Download>`_ application *josm-tested.jar*.

Open an application with the command: 

	$ java -jar josm-tested.jar

Choose the *.osm* extension file.

Changes to map attributes are made in the right part of the application.

Save the changes


6. Inserting the map
--------

Insert the *.osm* file into the **osm_example** folder


7. Creating the Route File
--------

Create the *.yaml* file inside the *osm_planner/test* folder with the attributes **target_lon**, **target_lat**, **source_lon**, and **source_lat**

Example:
	target_lon: -51.1746618   # longitude of the destination

	target_lat: -30.0585084   # destination's latitude

	source_lon: -51.1754576   # longitude of origin

	source_lat: -30.0586203   # source latitude


8. Creating the new *.launch* plan file
--------

Add content below to the new file

	<?xml version="1.0" ?>
	<launch>
	  <include file="$(find osm_planner)/launch/planner.launch" ns="osm_planner">
	    <!--Defining this arg is necessary to change file path -->
	    <arg name="filepath" default="$(find osm_planner)/osm_example/[NEW WORLD].osm"/>
	  </include>
	  <!--RVIZ for drawing routes-->
	  <param name="visualization" value="true"/> <!--do not use namespace for param visualization-->
	  <node name="rviz" pkg="rviz" type="rviz" args="-d $(find osm_planner)/rviz/routes.rviz" ns="osm_planner" required="true"/>

	  <!--Simulation of navigation-->
	  <node name="navigation_example" pkg="osm_planner" type="navigation_example" ns="osm_planner" output="screen"/>
	  <!--Geographics coordinates - start and target position -->
	  <rosparam file="$(find osm_planner)/test/[ROUTE_FILE].yaml" command="load"/>
	</launch>


9. Change of obstacle detection
--------

Open the **osm_planner/src/navigation_example.cpp** file

Remove the lines from **115** to **126**.

Recompile the project (step 2).


10. Executing the application
--------

Run the command 

	$ roslaunch osm_planner new_plan.launch

After loading the environment, press **Enter** to start the trajectory of the robot.



You will see a 2D map with only the streets and the robot traversing the smallest path from the source to the target.


