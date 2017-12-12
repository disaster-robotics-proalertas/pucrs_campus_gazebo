===============
Setting Up Pyson
===============

Pyson is a JASON-Style AgentSpeak interpreter for Python.

Currently this plugin is under development but we could not found the currently version.

To install is very simple, after download from the GitHub repository (https://github.com/niklasf/pyson) we need to execute in a terminal the command:

 ```  
 	sudo python setup.py develop
 ```

This command will install all the Pyson's dependencies.


There are some examples at the "examples" folder. After the installation completes, to run any example we need to run the .py file, e.g., to run the communication example we need to execute in a terminal the command 

``` 
	python env.py
```

We found some limitations to update an agent belief. With Jason, to do that, we use the operator "-+" but we tested and in the Pyson it does not works, then we have created some auxiliaries plans to remove the current belief and add a new belief with the updated information.


## ROS integration

To be able to use ros, we need to create a subscriber and a publisher. 

### Creating a package

Inside the AgentSpeak folder there is a folder called check_odom. This is a catkin package, created to get the robot position. To create other package, use

```
	catkin_create_pkg <package-name>
```


This will create a folder with a package.xml file and a CMakeList.txt. With this folder ready, you can use it to create a node inside a robot. For this work, we use the
[Grizzly simulator](https://github.com/g/grizzly_simulator). 

#### Adding a node in the robot

To be able to get the robot position, we need to crete a node that will show the robot pose. For that, change the launch file of your robe with:

```
	<node pkg="check_odom" type="check_odometry" name="agent_subscriber.py" output="screen" >
```

In this command: 

- The pkg is the name of your package. 
- The type is the name of your node described at your script
- The name is the name of your script
- Output is the way the information is printed

### Running Pyson with AgentSpeak plans

To execute the program (inside src/agentspeak/Pyson/check_odom), be sure to:

- Launch your robot with an odometry node
- Install Pyson

And then run

```
	python main.py

```

You will se the plans and the actions on your console, and the robot moving on Gazebo.

