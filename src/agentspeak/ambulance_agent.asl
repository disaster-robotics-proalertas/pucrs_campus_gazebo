// The ambulance agent AgentSpeak implementation

/* Initial beliefs and rules */

arrived(CurrentX, CurrentY, DestX, DestY) :- CurrentX == DestX & CurrentY == DestY. 

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
	.print("My initial status - ", Status).
	
+receiveBuildingPose(Type, X, Y) [source(Source)] : true
	<-
	+buildingPose(Type, Source, X, Y);
	.print("The building ", Source, " told me its position is X: ", X, " Y: ", Y).

+newTransportationRequest [source(Destination)] : true
	<-
	.print("A new victim transportation request is available from ", Destination);
	.abolish(newTransportationRequest);
	+availableRequests(Destination).

+execute(PosX, PosY)
	<-
	.print("Updating my pose to X: ", PosX, " - Y: ", PosY);
	-+pose(PosX, PosY);
	.abolish(execute(PosX, PosY));
	!chooseAction.

+!chooseAction 
	: currentStatus(Status) & Status == "AVAILABLE"
	<-
	!findNewRequestToDo.
	
+!chooseAction 
	: currentStatus(Status) & 
	  Status == "GOING_TO_RESCUE" & 
	  pose(X, Y) & 
	  buildingPose("RESCUE_POINT", _, DestX, DestY) &
	  arrived(X, Y, DestX, DestY)
	<-
	.print("I am arrived to the rescue point and I am getting the victim.");
	!getVictim;
	!goToHospital.
	
+!chooseAction 
	: currentStatus(Status) & 
	  Status == "GOING_TO_RESCUE" & 
	  pose(X, Y) & 
	  buildingPose("RESCUE_POINT", _, DestX, DestY) & 
	  not arrived(X, Y, DestX, DestY)
	<-
      .print("I am still going to the rescue point.").
      
+!chooseAction 
	: currentStatus(Status) & 
	  Status == "GOING_TO_HOSPITAL" &
	  pose(X, Y) &  
	  buildingPose("HOSPITAL", _, DestX, DestY)& 
	  arrived(X, Y, DestX, DestY)
	<-
	.print("I am arrived on the hospital.");
	!deliverVictim.
	
+!chooseAction 
	: currentStatus(Status) & 
	Status == "GOING_TO_HOSPITAL" & 
	pose(X, Y) & 
	buildingPose("HOSPITAL", _, DestX, DestY) &
	not arrived(X, Y, DestX, DestY)
	<-
	.print("I am still going to the hospital.").

+!acceptRequest : availableRequests(Destination)
	<-
	+currentRequest(Destination);
	-availableRequests(Destination);
	.broadcast(tell, acceptingTransportationRequest(Destination));
	.print("Notifying the another agents that I am accepting the request to get the victim to the hospital").

+!acceptRequest : true
	<-
	.print("No more requests available. Waiting for a new one.").

+!goToRescuePoint : buildingPose("RESCUE_POINT", _, DestX, DestY) & availableRequests(Destination)
	<-
	-+currentStatus("GOING_TO_RESCUE");
	!go_to(DestX, DestY);
	.print("I am going to the rescue point to collect").
	
+!goToRescuePoint : true
	<-
	.print("I do not need to move to a rescue point.").

+!goToHospital : true
	<-
	?buildingPose("HOSPITAL", _, DestX, DestY);
	
	-+currentStatus("GOING_TO_HOSPITAL");
	.print("Going to hospital.");
	!go_to(DestX, DestY).

+!findNewRequestToDo : availableRequests(Requestor)
	<-
	+currentRequest(Requestor);
	-+currentStatus("GOING_TO_RESCUE");
	-availableRequests(Requestor);
	.broadcast(tell, acceptingTransportationRequest(Requestor));
	.print("Notifying the another agents that I am accepting the victim transportation to ", Requestor);
	!goToRescuePoint.

+!findNewRequestToDo : true
	<-
	.print("No more requests available. Waiting for a new one.").

+!go_to(X, Y) : true
	<-
	.print("I need to go to X: ", X, " Y: ", Y, " but I do not know how I can do it.").
	
+!getVictim : true
	<-
	.print("Getting the victim.").
	
+!deliverVictim : true
	<-
	?currentRequest(Requestor);
	.abolish(newTransportationRequest);
	-availableRequests(Requestor);
	-+currentRequest("");
	-+currentStatus("AVAILABLE");
	
	.broadcast(tell, transportationRequestCompleted(Requestor));
		
	.print("Victim transported").
	
+acceptingTransportationRequest(Requestor) [source(Source)] : 
	Source \== self & 
	currentRequest(CurrentDestination) &
	Requestor == CurrentDestination
	<-
	.my_name(MyName);
	.eval(HasPriority, MyName < Source);
	if(not HasPriority){
		.print("The agent ", Source, " (with higher priority) accepted the request to rescue the victim from ", Requestor);
		.abolish(availableRequests(Requestor));
		.abolish(acceptingTransportationRequest(Requestor));
		-currentRequest(Requestor);
		-+currentStatus("AVAILABLE");
	};
	-acceptingTransportationRequest(Requestor).
	
+acceptingTransportationRequest(Requestor) [source(Source)] :
	true 
	<-
	-availableRequests(Requestor);
	-acceptingTransportationRequest(Requestor);
	.print("I am busy and the agent ", Source, " will deliver rescue the victim on the ", Requestor).
	
+requestCompleted(Requestor) [source(Source)] : true
	<-
	.print("The agent ", Source, " tell me that the request was completed.");
	-availableRequests(Requestor);
	.abolish(requestCompleted(Requestor)).
	
+newSupplyRequest(Supply) [source(Source)] : true
	<-
	.print("I am an ambulance. Ignoring this supply request from ", Source). 