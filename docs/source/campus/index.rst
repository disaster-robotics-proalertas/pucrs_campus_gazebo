
===============
Building the PUCRS Campus on Gazebo
===============

Creation of the PUCRS Campus Scenario in the Gazebo


To run the example we created, you need:

1. Ubuntu 14.04 with `Kinect <http://wiki.ros.org/kinetic/Installation/Ubuntu>`_ distribution installed (ros-kinetic-desktop-full)
2. Python


1. Extraction of campus map PUCRS
--------

Go to the website `Open Street Map <http://www.openstreetmap.org/>`_.

Click the Export button at the top of the screen.

Fill in the latitude and longitude coordinate range (scroll through the coordinate fields by pressing a Tab key to view an area on the map).

Click the Export button  the left side of the screen (the file extension will be .osm).


2. Map editing
--------

Visit the site *https://josm.openstreetmap.de/wiki/Download* to download the 'JOSM <https://josm.openstreetmap.de/wiki/Download>_' application *josm-tested.jar*.

Open an application with the command: 

	$ java -jar josm-tested.jar

Choose the *.osm* extension file.

Changes to map attributes are made in the right part of the application.

Save the changes


3. 3D Map Creation
--------

Access the website *http://osm2world.org/* to download the 'OSM2world <http://osm2world.org/>_' application.

Open an application with the command: 
	
	$ java -jar OSM2World.jar

Check out the changes performed in the previous step.

Export the file to the object format (.obj).


4. Conversion from *.obj* format to *.stl* format.
--------

Run the command: *http://www.openscenegraph.org/index.php/documentation/guides/user-guides/55-osgconv*

	$ osgconv map_pucrs.obj map_pucrs.stl


5. Upload the map in the Gazebo
--------

Run the command: 

	$ gazebo to load the empty environment.

Press Ctrl + M or go to the Template Editor.

Click Add Custom Shapes.

Select the .stl extension file.

Save the template.

Close the Template Editor.

Save the world file.


6. Loading Turtlebot in the New World
--------

Run the command: 

	$ roslaunch turtlebot_gazebo turtlebot_world.launch world_file:=/opt/ros/kinetic/share/turtlebot_gazebo/worlds/campus.world




You will see a 3D scenario in the Gazebo Simulator and a Turtlebot at the point of origin.



