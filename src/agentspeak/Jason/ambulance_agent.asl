// The ambulance agent AgentSpeak implementation

/* Initial beliefs and rules */

arrived(CurrentX, CurrentY, DestX, DestY) :- CurrentX == DestX & CurrentY == DestY. 

/* Initial goals */

!start.

/* Plans */

+!start : not pose(_,_) 
	<- 
	+pose(0,0);
	!start.

+!start : true 
	<- 
	.my_name(Name);
	.print("I am the agent ", Name, ". I am entering in the simulation.");
	
	+pose(0,0);
	?pose(X, Y);
	.print("My initial pose is X: ", X, " Y: ", Y);
	
	+currentStatus("AVAILABLE");
	?currentStatus(Status);
	.print("My initial status - ", Status).
	
+receiveBuildingPose(Type, X, Y) [source(Source)] : true
	<-
	+buildingPose(Type, Source, X, Y);
	.print("The building ", Source, " told me its position is X: ", X, " Y: ", Y).

+newTransportationRequest [source(Requestor)] : true
	<-
	.print("A new victim transportation request is available from ", Requestor);
	.abolish(newTransportationRequest);
	+availableRequests(Requestor).

+execute(PosX, PosY)
	<-
	.print("Updating my pose to X: ", PosX, " - Y: ", PosY);
	
	?pose(CurX, CurY);
	-pose(CurX, CurY);
	+pose(PosX, PosY);

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
	  currentRequest(Requestor) & 
	  buildingPose(_, Requestor, DestX, DestY) & 
	  arrived(X, Y, DestX, DestY)
	<-
	.print("I am arrived to the rescue point and I am getting the victim.");
	!getVictim.
	
+!chooseAction 
	: currentStatus(Status) & 
	  Status == "GOING_TO_RESCUE" & 
	  pose(X, Y) & 
	  currentRequest(Requestor) & 
	  buildingPose(_, Requestor, DestX, DestY) & 
	  not arrived(X, Y, DestX, DestY)
	<-
      .print("I am still going to the rescue point ", Requestor).
      
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
	.print("I am still going to the hospital.");
	!goToHospital.

+!acceptRequest : availableRequests(Requestor)
	<-
	+currentRequest(Requestor);
	-availableRequests(Requestor);
	.broadcast(tell, acceptingTransportationRequest(Requestor));
	.print("Notifying the another agents that I am accepting the request to get the victim to the hospital").

+!acceptRequest : true
	<-
	.print("No more requests available. Waiting for a new one.").

+!goToRescuePoint : currentRequest(Requestor) & buildingPose(_, Requestor, DestX, DestY)
	<-
	-availableRequests(Requestor);
	!updateStatus("GOING_TO_RESCUE");
	.go_to(DestX, DestY);
	.print("I am going to the rescue point to collect").

+!goToHospital : buildingPose("HOSPITAL", _, DestX, DestY)
	<-
	.print("Going to hospital.");
	.go_to(DestX, DestY).

+!findNewRequestToDo : availableRequests(Requestor)
	<-
	+currentRequest(Requestor);
	!updateStatus("GOING_TO_RESCUE");
	.broadcast(tell, acceptingTransportationRequest(Requestor));
	.print("Notifying the another agents that I am accepting the victim transportation to ", Requestor);
	!goToRescuePoint.

+!findNewRequestToDo : true
	<-
	.print("No more requests available. Waiting for a new one.").
	
+!getVictim : true
	<-
	.print("Getting the victim.");
	!updateStatus("GOING_TO_HOSPITAL").
	
+!deliverVictim : currentRequest(Requestor)
	<-
	.abolish(newTransportationRequest);
	-availableRequests(Requestor);
	!updateCurrentRequest("");
	!updateStatus("AVAILABLE");
	
	.broadcast(tell, transportationRequestCompleted(Requestor));
		
	.print("Victim transported").
	
+acceptingTransportationRequest(Requestor) [source(Source)] : 
	Source \== self & 
	currentRequest(CurrentDestination) &
	Requestor == CurrentDestination
	<-
	.my_name(MyName);
	if(MyName > Source){
		.print("The agent ", Source, " (with higher priority) accepted the request to rescue the victim from ", Requestor);
		.abolish(availableRequests(Requestor));
		.abolish(acceptingTransportationRequest(Requestor));
		-currentRequest(Requestor);
		!updateStatus("AVAILABLE");
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

+!updateStatus(Status) : true
	<-
	?currentStatus(CurrentStatus);
	-currentStatus(CurrentStatus);
	+currentStatus(Status).

+!updateCurrentRequest(Request) : true
	<-
	?currentRequest(CurrentRequest);
	-currentRequest(CurrentRequest);
	+currentRequest(Request).
