
===============
Setting Up Jason
===============

- JaCaMo (http://jacamo.sourceforge.net/) 

Is a framework for Multi-Agent Programming that combines three separate technologies, each of them being well-known on its own and developed for a number of years so they are fairly robust and fully-fledged. JaCaMo is a combination of Jason, Cartago and Moise.

- Jason (http://jason.sourceforge.net/wp/)

Is an interpreter for an extended version of AgentSpeak. It implements the operational semantics of that language, and provides a platform for the development of multi-agent systems, with many user-customisable features.
CArtAgO - Common ARTifact infrastructure for AGents Open environments - (http://cartago.sourceforge.net/) is a general purpose framework/infrastructure that makes it possible to program and execute virtual environments – also said virtual / application / software environments – for multi-agent systems.

- Moise (http://moise.sourceforge.net/) 

Is an organisational model for Multi-Agent Systems based on notions like roles, groups, and missions. It enables an MAS to have an explicit specification of its organisation. This specification is to be used both by the agents to reason about their organisation and by an organisation platform that enforces that the agents follow the specification. To install JaCaMo Eclipse Plugin please go to http://jacamo.sourceforge.net/eclipseplugin/tutorial/

- Rosbridge (http://wiki.ros.org/rosbridge_suite)

For communication between Jason and ROS was used the Rosbridge. It provides a JSON API to ROS functionality for non-ROS programs. There are a variety of front ends that interface with rosbridge, including a WebSocket server for web browsers to interact with. Rosbridge_suite is a meta-package containing rosbridge, various front end packages for rosbridge like a WebSocket package, and helper packages. Allows publishing or subscribing to ROS topics by sending a JSON. Covers also service calls, getting and setting params, and more.

Divided into three parts:

rosbridge_library: responsible for converting the JSON to ROS commands and vice versa. 

rosapi: provides service calls for getting ROS meta-information list of topics, services, params, etc.

rosbridge_server: provides a WebSocket connection/interface to rosbridge.

To install Rosbridge run:

```
$ sudo apt-get install ros-<rosdistro>-rosbridge-server
```

Initialize RosBridge:

```
$ source /opt/ros/<rosdistro>/setup.bash
```

```
$ roslaunch rosbridge_server rosbridge_websocket.launch
```

Run the tortoisebot

```
$ roslaunch tortoisebot tortoisebot.launch
```

Run the teleop_twist_keyboard

```
$ rosrun teleop_twist_keyboard teleop_twist_keyboard.py
```

To see what is being published in the topic

```
$ rostopic echo /cmd_vel
```

- Import the project in eclipse

Run RosTest.java

to run as argument: ws://localhost:9090

Run > Run Configurations > tab Arguments > field Program Arguments set: ws://localhost:9090

This only the first time. Then it gets stored.

subscribe to the topic /cdm_vel to observe what is published

if you change the values of the speed of theft with the telop will print on the screen of the eclipse read the values of the topic

publishes a test value in the topic  /cdm_vel 

if you put a speed you can see her in the robot in the gazebo.



Artefato Cartago <-> RosInterface.java <-> Rosbridge.java <-> Rosbridge server
