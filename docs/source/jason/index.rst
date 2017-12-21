
===============
Setting Up Jason
===============

- JaCaMo (http://jacamo.sourceforge.net/) 

Is a framework for Multi-Agent Programming that combines three separate technologies, each of them being well-known on its own and developed for a number of years so they are fairly robust and fully-fledged. JaCaMo is a combination of Jason, Cartago and Moise.

- Jason (http://jason.sourceforge.net/wp/)

Is an interpreter for an extended version of AgentSpeak. It implements the operational semantics of that language, and provides a platform for the development of multi-agent systems, with many user-customisable features.

- CArtAgO (http://cartago.sourceforge.net/) 

Is a general purpose framework/infrastructure that makes it possible to program and execute virtual environments – also said virtual / application / software environments – for multi-agent systems.

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

In other console run the tortoisebot

```
$ roslaunch tortoisebot tortoisebot.launch
```

To see what is being published in the topic run in other console

```
$ rostopic echo /cmd_vel
```

- Import the project in eclipse

Run JaCaMo Application testServer1.jcm

Your turtlebot will receive information from the agent programmed in Jason regarding the move goals and will send information about your current position and orientation to the agent.

This information can be viewed on the JaCaMo console that will open when the simulation starts.


