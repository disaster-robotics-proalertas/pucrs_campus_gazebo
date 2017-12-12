===============
AgentSpeak Codes
===============

To test the AgentSpeak plans created to this work we have two options, with JASON or with Pyson (another tools or libraries were not tested here).

 - JASON
	To test with JASON, the JASON plugin must be installed on the Eclipse (more information at http://jason.sourceforge.net/mini-tutorial/eclipse-plugin/#id.iyxfqy5c7qw)
	After the plugin is installed, we can create a new JASON project. The AgentSpeak files (.asl files) must be placed at the folder /project_name/src/asl
	To add agents to the environment, we need to change the [project_name].mas2j file. In this file we have a section called "agents" and at this section we can add the agent as the following: collectPoint1 collect_point [beliefs="pose(7,8)"]; This example is adding a new agent called "collectPoint1" using the collect_point ASL file and starting it with a initial belief called "pose" with two parameters "7" and "8".
	After the agents are added to the environment, we can start the project as default by Eclipse (a play button placed at the top of the Eclipse).
	To test this scenario, we created a executor_agent that is responsible to call the execute belief of the transportation agents in a loop.

 - Pyson
	To test with Pyson, the Pyson plugin must be installed (more information at https://github.com/niklasf/pyson/)
	After the plugin is installed, we can execute our AgentSpeak plans in every one folder.
	To test it, we need to create a new .py file following the example.py file placed at ../../../src/agentspeak/Pyson
	After that, we need only to execute in a terminal the command python [file_name].py to test it.

Plans created:
 - To achieve the goal of this work we have created some AgentSpeak plans. Initially, all agents start only printing their basic information, like initial position and initial status.
 - After that, the rescue points start sending their position to the delivery agents and ambulances and then send a new supply request or a victim transportation request;
 - When a delivery agent receives a new supply request, the agent validates the supply quantity through its load capacity. If the supply quantitiy is lower than its load capacity, the agent add this request to its belief base;
 - At this moment, the rescue points change their status to "WAITING";
 - Now, the delivery agent will notify all the other agents that it will do the request. The another agents will receive this broadcast and the agent with the lower name will be able to do the request, e.g., the agent1 is notifying the agent2 about the request and in this case the agent1 has priority about the agent2.
- When the agent accepts a request and can do that, it changes its status to "GOING_TO_COLLECT" and asks the bridge (Pyson or JASON) to move the robot at ROS to the position of the collect point.
- When the agent arrives the destination, it collects the supply and changes its status to "GOING_TO_DELIVERY_POINT", then the agent asks the brigde to move the robot at the ROS to the position of the rescue point that asked the supply.
- When a supply is delivered, the building agent changes its status do "AVAILABLE" and in the next execution it will ask for a new supply.
- The process described above about the delivery agent is similar to the ambulance agent, the difference is instead of collect a supply in the collect point, the first step after receive a new request is to get the victim at the rescue point.

Limitations
- We found a limitation on Pyson that we cannot update a belief using "-+" operator. For this reason we created simple plans only to remove the current belief and add a new one with an updated value.
- The conditional on the AgentSpeak-py never satisfied our plans while in Pyson we could achieve the conditions properly.
