// The delivery agent AgentSpeak implementation

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
	
	?pose(X, Y);
	.print("My initial pose is X: ", X, " Y: ", Y);
	
	+currentStatus("AVAILABLE");
	?currentStatus(Status);
	.print("My initial status - ", Status).

+receiveBuildingPose(Type, X, Y) [source(Source)] : true
	<-
	+buildingPose(Type, Source, X, Y);
	.print("The building ", Source, " told me its position is X: ", X, " Y: ", Y).
		
+newSupplyRequest(Supply) [source(Destination)] : true
	<-
	.print("A new request is available from ", Destination, " to delivery ", Supply);
	.abolish(newSupplyRequest(Supply));
	+availableRequests(Destination, Supply).

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
	  Status == "GOING_TO_COLLECT" & 
	  pose(X, Y) & 
	  buildingPose("COLLECT_POINT", _, DestX, DestY) &
	  arrived(X, Y, DestX, DestY)
	<-
	.print("I am arrived to the collect point and the supply was collected.");
	!collectSupply.
	
+!chooseAction 
	: currentStatus(Status) & 
	  Status == "GOING_TO_COLLECT" & 
	  pose(X, Y) & 
	  buildingPose("COLLECT_POINT", _, DestX, DestY) & 
	  not arrived(X, Y, DestX, DestY)
	<-
      .print("I am still going to the collect point.").
	
+!chooseAction 
	: currentStatus(Status) & 
	  Status == "GOING_TO_DELIVERY_POINT" &
	  pose(X, Y) &  
	  currentRequest(Requestor, _) &
	  buildingPose(_, Requestor, DestX, DestY) & 
	  arrived(X, Y, DestX, DestY)
	<-
	.print("I am arrived to the delivery point.");
	!deliverSupply.
	
+!chooseAction 
	: currentStatus(Status) & 
	Status == "GOING_TO_DELIVERY_POINT" & 
	pose(X, Y) & 
	currentRequest(Requestor, _) &
  	buildingPose(_, Requestor, DestX, DestY) &
	not arrived(X, Y, DestX, DestY)
	<-
	.print("I am still going to the delivery point.").
		
+!goToCollectPoint : buildingPose("COLLECT_POINT", _, DestX, DestY) & currentRequest(_,Supply)
	<-
	.print("I am going to the collect point to collect ", Supply);
	.my_name(MyName);	
	.go_to(DestX, DestY, MyName, X).

+!findNewRequestToDo : availableRequests(Destination,Supply)
	<-
	+currentRequest(Destination, Supply);

	!updateStatus("GOING_TO_COLLECT");
	
	-availableRequests(Destination, Supply);
	.broadcast(tell, acceptingRequest(Destination,Supply));
	.print("Notifying the another agents that I am accepting the request to deliver ", Supply, " to ", Destination);
	!goToCollectPoint.

+!findNewRequestToDo : true
	<-
	.print("No more requests available. Waiting for a new one.").

+!goToDeliveryPoint : true
	<-
	?currentRequest(Destination, _);
	?buildingPose(_, Destination, DestX, DestY);
	
	.print("Going to deliver the supply");
	.my_name(MyName);	
	.go_to(DestX, DestY, MyName, X).

+!collectSupply : true
	<-
	?currentRequest(_,Supply);
	.print(Supply, " collected.");
	
	!updateStatus("GOING_TO_DELIVERY_POINT").

+!deliverSupply : true
	<-
	?currentRequest(Destination, Supply);
	.send(Destination, tell, receiveSupply(Supply));
	
	.abolish(newSupplyRequest(Supply));
	-availableRequests(Destination, Supply);

	?currentRequest(Dest,Sup);
	-currentRequest(Dest,Sup);
	+currentRequest("","");	

	!updateStatus("AVAILABLE");
	
	.broadcast(tell, requestCompleted(Destination, Supply));
		
	.print("Supply delivered").
		
+acceptingRequest(Destination,Supply) [source(Source)] : 
	Source \== self & 
	currentRequest(CurrentDestination, _) &
	Destination == CurrentDestination
	<-
	.my_name(MyName);
	if(MyName > Source){
		.print("The agent ", Source, " (with higher priority) accepted the request to deliver ", Supply, " to ", Destination);
		.abolish(availableRequests(Destination, Supply));
		.abolish(acceptingRequest(Destination,Supply));
		
		-currentRequest(Destination,Supply);
		
		!updateStatus("AVAILABLE");
	};
	-acceptingRequest(Destination,Supply).
	
+acceptingRequest(Destination,Supply) [source(Source)] :
	true 
	<-
	-availableRequests(Destination, Supply);
	.abolish(acceptingRequest(Destination,Supply));
	.print("I am busy and the agent ", Source, " will deliver ", Supply, " to ", Destination).
	
+requestCompleted(Destination, Supply) [source(Source)] : true
	<-
	.print("The agent ", Source, " tell me that the request was completed.");
	-availableRequests(Destination, Supply);
	.abolish(requestCompleted(Destination, Supply)). 
	
+newTransportationRequest [source(Source)] : true
	<-
	.print("I am a delivery agent. Ignoring this victmin transportation request from ", Source).
	
+!updateStatus(Status) : true
	<-
	?currentStatus(CurrentStatus);
	-currentStatus(CurrentStatus);
	+currentStatus(Status).