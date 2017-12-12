// The Rescue Point agent AgentSpeak implementation

/* Initial beliefs and rules */
deliverable("").

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
	!sendPose;
	!execute.
	
+!sendPose : pose(X, Y)
	<- 
	.broadcast(tell, receiveBuildingPose("RESCUE_POINT", X, Y));
	.print("Sending my position to the other agents").
		
+!execute : currentStatus(Status) & Status == "AVAILABLE"
	<-
	.print("I am available. I will make a request");
	!chooseRequest.

+!execute : true
	<-
	.print("My current status is ", Status).
	
+!chooseRequest : true
	<-
	.random(NewNumber);
	.print("The new number to choose the supply to be requested is ", NewNumber);
	!chooseRequest(NewNumber).
	
+!chooseRequest(Number) : Number <= 0.17
	<-
	.print("I will request medicines");
	!requestSupply("MEDICINES").
	
+!chooseRequest(Number) : Number > 0.17 & Number <= 0.34
	<-
	.print("I will request food");
	!requestSupply("FOOD").
	
+!chooseRequest(Number) : Number > 0.34 & Number <= 0.51
	<-
	.print("I will request water");
	!requestSupply("WATER").

+!chooseRequest(Number) : Number > 0.51 & Number <= 0.68
	<-
	.print("I will request blankets");
	!requestSupply("BLANKETS").
	
+!chooseRequest(Number) : Number > 0.68 & Number <= 0.85
	<-
	.print("I will request curatives");
	!requestSupply("CURATIVES").
	
+!chooseRequest(Number) : Number > 0.85
	<-
	.print("I will request a victim transportation");
	!requestTransportation.
	
+!requestSupply(Supply) : true
	<-
	-+deliverable(Supply);
	.random(Qtde);
	.print("Requesting ", Qtde, " items of ", Supply);
	.broadcast(tell, newSupplyRequest(Supply, Qtde * 100));
	.print("Changing my status to WAITING");
	-+currentStatus("WAITING").
	
+!requestTransportation : true
	<-
	-+deliverable("TRANSPORTATION");
	.print("Requesting TRANSPORTATION");
	.broadcast(tell, newTransportationRequest);
	.print("Changing my status to WAITING");
	-+currentStatus("WAITING").

+receiveSupply(Supply) : deliverable(ExpectedSupply) & Supply == ExpectedSupply 
	<-
	-+currentStatus("AVAILABLE");
	-+deliverable("");
	.abolish(receiveSupply(Supply));
	.print("Supply received");
	!execute.
		
+receiveSupply(Supply) : deliverable(ExpectedSupply) & Supply \== ExpectedSupply
	<-
	.print("I am expecting ", ExpectedSupply, " but I am received ", Supply).
