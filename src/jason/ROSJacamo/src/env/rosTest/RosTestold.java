package rosTest;

import com.fasterxml.jackson.databind.JsonNode;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.geometry_msgs.Twist;
import ros.msgs.std_msgs.PrimitiveMsg;
import ros.tools.MessageUnpacker;

/**
 * Example of connecting to rosbridge with publish/subscribe messages. Takes one argument:
 * the rosbridge websocket URI; for example: ws://localhost:9090.
 * @author James MacGlashan.
 */





public class RosTestold {

	public static void main(String[] args) {

		if(args.length != 1){
			System.out.println("Need the rosbridge websocket URI provided as argument. For example:\n\tws://localhost:9090");
			System.exit(0);
		}

		RosBridge bridge = new RosBridge();
		bridge.connect(args[0], true);

		//bridge.subscribe(SubscriptionRequestMsg.generate("/ros_to_java")
		bridge.subscribe(SubscriptionRequestMsg.generate("/cmd_vel")
				
				//	.setType("std_msgs/String")
				.setType("geometry_msgs/Twist")
				
					.setThrottleRate(1)
					.setQueueLength(1),
				new RosListenDelegate() {

					public void receive(JsonNode data, String stringRep) {
						//System.out.println("data:"+data.toString());
						System.out.println("stringRep:"+stringRep);
						MessageUnpacker<Twist> unpacker = new MessageUnpacker<Twist>(Twist.class);
						Twist msg = unpacker.unpackRosMessage(data);
						System.out.println("Linear:");
						System.out.println("x:"+msg.linear.x);
						System.out.println("y:"+msg.linear.y);
						System.out.println(":"+msg.linear.z);
						System.out.println("Angular:");
						System.out.println("x:"+msg.angular.x);
						System.out.println("y:"+msg.angular.y);
						System.out.println(":"+msg.angular.z);
					}
				}
		);

//		System.out.println("sleeping...");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Publisher pub = new Publisher("/java_to_ros", "std_msgs/String", bridge);
		Publisher pub = new Publisher("/cmd_vel", "geometry_msgs/Twist", bridge);
//		Twist t1 = new Twist();
//		t1.linear.x=1;
//		t1.linear.y=0;
//		t1.linear.z=0;
//		t1.angular.x=0;
//		t1.angular.y=0;
//		t1.angular.z=0;
//		pub.publish(t1);
//		
//		try {
//			Thread.sleep(1500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		publishing(pub,1,0,0,0,0,0,4500);
		publishing(pub,0,0,0,0,0,0,500);
		publishing(pub,0,0,0,0,0,-3.2,2000);
		publishing(pub,0,0,0,0,0,0,500);
		publishing(pub,1,0,0,0,0,0,2500);
		publishing(pub,0,0,0,0,0,0,500);
		publishing(pub,1,0,0,0,0,0,2500);
		publishing(pub,0,0,0,0,0,0,500);
		publishing(pub,0,0,0,0,0,-3.2,2000);
		publishing(pub,0,0,0,0,0,0,500);
		publishing(pub,1,0,0,0,0,0,2500);
		publishing(pub,0,0,0,0,0,0,500);
		
		
//		t1.linear.x=0;
//		t1.linear.y=0;
//		t1.linear.z=0;
//		t1.angular.x=0;
//		t1.angular.y=0;
//		t1.angular.z=0;
//		pub.publish(t1);
//		
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		t1.linear.x=0;
//		t1.linear.y=0;
//		t1.linear.z=0;
//		t1.angular.x=0;
//		t1.angular.y=0;
//		t1.angular.z=-3.2;
//		pub.publish(t1);
//		
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		t1.linear.x=1;
//		t1.linear.y=0;
//		t1.linear.z=0;
//		t1.angular.x=0;
//		t1.angular.y=0;
//		t1.angular.z=0;
//		pub.publish(t1);
//		
//		try {
//			Thread.sleep(1500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		t1.linear.x=0;
//		t1.linear.y=0;
//		t1.linear.z=0;
//		t1.angular.x=0;
//		t1.angular.y=0;
//		t1.angular.z=0;
//		pub.publish(t1);
		
//		Publisher pub = new Publisher("/java_to_ros", "std_msgs/String", bridge);
//		for(int i = 0; i < 10; i++) {
//			pub.publish(new PrimitiveMsg<String>("hello from java " + i));
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	public static void publishing(Publisher pubp,double lx,double ly,double lz,double ax,double ay,double az,int sleepTime) {
		Twist t1 = new Twist();
		t1.linear.x=lx;
		t1.linear.y=ly;
		t1.linear.z=lz;
		t1.angular.x=ax;
		t1.angular.y=ay;
		t1.angular.z=az;
		pubp.publish(t1);
		
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	

}
