// Agent collect_point in project test

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true
	<-	
	.my_name(Name);
	.print("I am the agent ", Name, ". I am entering in the simulation.");
	
	?pose(X, Y);
	.print("My initial pose is X: ", X, " Y: ", Y);
	
	+currentStatus("AVAILABLE");
	?currentStatus(Status);
	.print("My initial status - ", Status);
	!sendPose.
	
+!sendPose : pose(X, Y)
	<- 
	.broadcast(tell, receiveBuildingPose("COLLECT_POINT", X, Y));
	.print("Sending my position to the other agents").
