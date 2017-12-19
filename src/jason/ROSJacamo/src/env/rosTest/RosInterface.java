package rosTest;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;

//import eis.AgentListener;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import env.AgentListener;
import env.Percept;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.geometry_msgs.Twist;
import ros.msgs.pose.Pose;
import ros.msgs.std_msgs.PrimitiveMsg;
import ros.tools.MessageUnpacker;


public class RosInterface implements Runnable{

    private String configFile = "eismassimconfig.json";
    private RosBridge rosbridge;
    private AgentListener agListener;
    int perceptNum = 0;
    
    private Set<Percept> twistPercepts = Collections.synchronizedSet(new HashSet<Percept>());
    private AbstractQueue<Collection<Percept>> perceptsQueue = new ConcurrentLinkedQueue<Collection<Percept>>();
    Pose currentPose=new Pose();

    public RosInterface() {
        super();
        setup();
        
    }

    public RosInterface(String configFile){
        super();
        this.configFile = configFile;
        setup();
    }

    /**
     * Setup method to be called at the end of each constructor.
     */
    private void setup(){
    	rosbridge = new RosBridge();
    	rosbridge.setRosInterface(this);
    }

    public void start() {//throws ManagementException{
        new Thread(this).start();
    }


    public void run() {
        
        
        //toEulerAngleYaw();
        
        while (!rosbridge.hasConnected()) {
            	rosbridge.connect("ws://localhost:9090",true);
        }        	
            
        
        
        
        
        
        this.subscribre();
    }

    public boolean isEntityConnected(String entityName){
    	return rosbridge.hasConnected();
    }
    
    


	private void subscribre() {

		rosbridge.subscribe(SubscriptionRequestMsg.generate("/cmd_vel_mux/input/teleop")
				.setType("geometry_msgs/Twist")
					.setThrottleRate(1)
					.setQueueLength(1),
				new RosListenDelegate() {

					public void receive(JsonNode data, String stringRep) {
						//System.out.println("data:"+data);
						MessageUnpacker<Twist> unpacker = new MessageUnpacker<Twist>(Twist.class);
						Twist msg = unpacker.unpackRosMessage(data);
						//System.out.println("Linear:"+msg.linear.x+","+msg.linear.y+","+msg.linear.z);
						//System.out.println("Angular:"+msg.angular.x+","+msg.angular.y+","+msg.angular.z);
						twistPercepts.clear();
						twistPercepts.add(new Percept("twist_linear", new Numeral(msg.linear.x),new Numeral(msg.linear.y),new Numeral(msg.linear.z)));
						twistPercepts.add(new Percept("twist_angular", new Numeral(msg.angular.x),new Numeral(msg.angular.y),new Numeral(msg.angular.z)));
						perceptsQueue.add(Collections.synchronizedSet(new HashSet<Percept>(twistPercepts)));
					}
				}
		);

		
rosbridge.subscribe(SubscriptionRequestMsg.generate("/odom")
				
				.setType("nav_msgs/Odometry")
				
					.setThrottleRate(1)
					.setQueueLength(1),
				new RosListenDelegate() {

					public void receive(JsonNode data, String stringRep) {
						JsonNode msg = data.path("msg");												
						JsonNode pose1 = msg.path("pose");
						JsonNode pose2 = pose1.path("pose");
						//System.out.println("pose2:"+pose2);
						
						MessageUnpacker<Pose> unpacker = new MessageUnpacker<Pose>(Pose.class);
						Pose msgPose = unpacker.unpackRosMessage(pose2);
						currentPose =msgPose;
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
		);
		
		
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

		//must be called in EISArtifact
		move2goalDeb2(-2,0,0);
		move2goalDeb2(-2,-2,0);
		move2goalDeb2(0,-2,0);
		move2goalDeb2(0,0,0);
	
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

	
	public void attachAgentListener(AgentListener listener) {
		agListener = listener;
	}
	
	
	public Map<String, Collection<Percept>> getAllPercepts(String agent, String entity) throws Exception {

		Map<String, Collection<Percept>> ret = new HashMap<String, Collection<Percept>>();
		// get all percepts
		//LinkedList<Percept> all = getAllPerceptsFromEntity(entity);
		LinkedList<Percept> all=null;

				// add annonation
				for (Percept p : all)
					p.setSource(entity);

				// done
				ret.put(entity, all);
				
		return ret;

	}

	public Collection<Percept> getNextPerception(String agent, String entity) throws Exception {
		return perceptsQueue.peek() != null? new LinkedList<Percept>(perceptsQueue.poll()) : new LinkedList<Percept>();

	}

	
	public void move2goalDeb2(double goalx,double goaly,double goalz) {
		Publisher pub = new Publisher("/cmd_vel_mux/input/teleop", "geometry_msgs/Twist", rosbridge);
		double goalX=goalx;
		double goalY=goaly;
		
		double goalZ=goalz;
		double distance_tolerance = 0.1;

		double linearX=0;
		double linearY=0;
		double linearZ=0;

		double angularX=0;
		double angularY=0;
		double angularZ=0;

		double desired_angleX = Math.atan2(goalY - currentPose.position.y, goalX - currentPose.position.x);
		System.out.println("desired_angleX:"+desired_angleX);
		
		desired_angleX = Math.atan2(goalX - currentPose.position.x,goalY - currentPose.position.y);
		System.out.println("desired_angleX2:"+desired_angleX);
		
		double desired_angle = Math.atan2(goalY - currentPose.position.y, goalX - currentPose.position.x);
		double desired_angle_degree = desired_angle*(180/Math.PI);
		
		boolean clockwise = ((desired_angle<0)?true:false);
		//double desired_angle2 = Math.atan2(goalX - currentPose.position.x, goalY - currentPose.position.y);
		//boolean clockwise2 = ((desired_angle2<0)?true:false);
		
		
		System.out.println("currentPose.position.x:"+currentPose.position.x);
		System.out.println("currentPose.position.y:"+currentPose.position.y);
		System.out.println("goalX:"+goalX);
		System.out.println("goalY:"+goalY);
		System.out.println("desired_angle:"+desired_angle+" Degree:"+desired_angle_degree);
		System.out.println("clockwise:"+clockwise);
		//System.out.println("toEulerAngleYaw:"+toEulerAngleYaw());
	
		
		long startTimeMillis = System.currentTimeMillis();
		long startSeconds = TimeUnit.MILLISECONDS.toSeconds(startTimeMillis);
		
		double current_angle = 0.0;
		double angular_speed=0.3;
		double angle=0;
		current_angle = toEulerAngleYaw();
		System.out.println("current_angle:"+current_angle);
		double turn_angle = 0;
		
		

		
        
		//rotation
		while (Math.abs(desired_angle)-Math.abs(current_angle) > 0.1) {
			
		if (clockwise) {
			angularZ = -Math.abs(angular_speed);//angular_speed
			}
		else
		 	{
			angularZ = Math.abs(angular_speed);//angular_speed
		 	}
		//angularZ = Math.abs(angular_speed);//angular_speed

		//Publisher pub2 = new Publisher("/cmd_vel_mux/input/teleop", "geometry_msgs/Twist", rosbridge);
		publishing(pub,linearX,linearY,linearZ,angularX,angularY,angularZ,300);

	
		long endtimeMillis = System.currentTimeMillis();
		long endSeconds = TimeUnit.MILLISECONDS.toSeconds(endtimeMillis);
		current_angle = angular_speed * (endSeconds-startSeconds);
		//current_angle = toEulerAngleYaw();
		//System.out.println("current_angle:"+current_angle);
		}
		
		angularZ=0;
		Publisher pub3 = new Publisher("/cmd_vel_mux/input/teleop", "geometry_msgs/Twist", rosbridge);
		publishing(pub3,linearX,linearY,linearZ,angularX,angularY,angularZ,300);

		 	
		
      System.out.println("end rotation");
	
  	
      try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		
      
      
    //move forward
      double current_distance = 0.0;
  	linearX = 0.3;
	linearY = 0;
	linearZ = 0;
	//angular velocity
	angularX = 0;
	angularY = 0;
	angularZ = 0; 
	double distance2;
	distance2=getDistance(currentPose.position.x, currentPose.position.y, goalX, goalY);
	
	startTimeMillis = System.currentTimeMillis();
	startSeconds = TimeUnit.MILLISECONDS.toSeconds(startTimeMillis); 
      do{
		publishing(pub,linearX,linearY,linearZ,angularX,angularY,angularZ,300);
		long endtimeMillis = System.currentTimeMillis();
		long endSeconds = TimeUnit.MILLISECONDS.toSeconds(endtimeMillis);
		current_distance = linearX * (endSeconds-startSeconds);
  		//System.out.println("distance:"+distance2+"  current_distance:"+current_distance);
  }while(current_distance<distance2);
       System.out.println("distance:"+distance2+"  current_distance:"+current_distance);
       linearX = 0;
		publishing(pub,linearX,linearY,linearZ,angularX,angularY,angularZ,300);
		System.out.println("currentPose.position.x:"+currentPose.position.x);
		System.out.println("currentPose.position.y:"+currentPose.position.y);
		System.out.println("end move goal");
		
      
		
		
      try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
      

//		//rotation - attempt
      //while (Math.abs(turn_angle + 0.3) < Math.abs(desired_angle)){    
//		while (Math.abs(current_angle + 0.3) < Math.abs(desired_angle)){
//      	
////      	if (clockwise) {
////  			angularZ = -Math.abs(angular_speed);//angular_speed
////  			}
////  		else
////  		 	{
////  			angularZ = Math.abs(angular_speed);//angular_speed
////  		 	}
//      	
//      	angularZ = Math.abs(angular_speed);//angular_speed
//      	
//      	publishing(pub,linearX,linearY,linearZ,angularX,angularY,angularZ,0);
//      	 
//          angle = toEulerAngleYaw() - current_angle;
//          System.out.println("angle:"+angle);
//
//          double res = angle;
//          while (res > Math.PI) {
//              res -= 2.0 * Math.PI;
//              }
//          while (res < -Math.PI) {
//              res += 2.0 * Math.PI;
//          }
//          
//          double delta_angle = res;
//          
//          turn_angle += delta_angle;
//          current_angle = toEulerAngleYaw();
//          System.out.println("delta_angle:"+delta_angle);
//          System.out.println("turn_angle:"+turn_angle);
//          System.out.println("current_angle:"+current_angle);
//          
//          
//      }
//		
//      angularZ=0;
//		//Publisher pub3 = new Publisher("/cmd_vel_mux/input/teleop", "geometry_msgs/Twist", rosbridge);
//		publishing(pub,linearX,linearY,linearZ,angularX,angularY,angularZ,0);
//      
//      try {
//			Thread.sleep(6000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
      
      
      //move forward - attempt 
//		double distance;
//		
//		do{
//			//linear velocity 
//			distance=getDistance(currentPose.position.x, currentPose.position.y, goalX, goalY);
//			System.out.println("distance:"+distance);
//			linearX = 0.5;
//			linearY = 0;
//			linearZ = 0;
//			//angular velocity
//			angularX = 0;
//			angularY = 0;
//			angularZ = 0; //4*(atan2(goal_pose.y - turtlesim_pose.y, goal_pose.x - turtlesim_pose.x)-turtlesim_pose.theta);
//
//			publishing(pub,linearX,linearY,linearZ,angularX,angularY,angularZ,0);
//			
//		}while(getDistance(currentPose.position.x, currentPose.position.y, goalX, goalY)>distance_tolerance);
//		
//		linearX = 0;
//		publishing(pub,linearX,linearY,linearZ,angularX,angularY,angularZ,0);
//		System.out.println("end move goal");
//		
//	}
	}
	
	
	//get the euclidian distance between two points 
	public double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));
	}

	
	
	
//not used
	public void toEulerAngle()
	{
		double w=1;
		double x=0;
		double y=1;
		double z=0;
		
		
		// roll (x-axis rotation)
		double sinr = +2.0 * (w * x + y * z);
		double cosr = +1.0 - 2.0 * (x * x + y * y);
		double roll = Math.atan2(sinr, cosr);
		//X = math.degrees(math.atan2(t0, t1))
		
		
		// pitch (y-axis rotation)
		double sinp = +2.0 * (w * y - z * x);
		double pitch;
		if (Math.abs(sinp) >= 1)
			pitch = Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
		else
			pitch = Math.asin(sinp);
		//Y = math.degrees(math.asin(t2))
		
		
		// yaw (z-axis rotation)
		double siny = +2.0 * (w * z + x * y);
		double cosy = +1.0 - 2.0 * (y * y + z * z);  
		double yaw = Math.atan2(siny, cosy);
		//Z = math.degrees(math.atan2(t3, t4))
		
		System.out.println("Roll"+roll);	
		System.out.println("Pitch"+pitch);
        System.out.println("Yaw"+yaw);
	}	
	
	
	public double toEulerAngleYaw()
	{
		double w=currentPose.orientation.w;
		double x=currentPose.orientation.x;
		double y=currentPose.orientation.y;
		double z=currentPose.orientation.z;

		// yaw (z-axis rotation)
		double siny = +2.0 * (w * z + x * y);
		double cosy = +1.0 - 2.0 * (y * y + z * z);  
		double yaw = Math.atan2(siny, cosy);
		
		return yaw;
		
	}	

	//not used
	public void RotationSet(double x, double y, double z, double w) {
	    double sqw = w*w;
	    double sqx = x*x;
	    double sqy = y*y;
	    double sqz = z*z;
		double unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise is correction factor
		double test = x*y + z*w;
		double heading, attitude, bank;
		if (test > 0.499*unit) { // singularity at north pole
			heading = 2 * Math.atan2(x,w);
			attitude = Math.PI/2;
			bank = 0;
			return;
		}
		if (test < -0.499*unit) { // singularity at south pole
			heading = -2 * Math.atan2(x,w);
			attitude = -Math.PI/2;
			bank = 0;
			return;
		}
	    heading = Math.atan2(2*y*w-2*x*z , sqx - sqy - sqz + sqw);
		attitude = Math.asin(2*test/unit);
		bank = Math.atan2(2*x*w-2*y*z , -sqx + sqy - sqz + sqw);
		System.out.println("heading:"+heading);
		System.out.println("attitude:"+attitude);
		System.out.println("bank:"+bank);
		
	}
	
	
	
	
}
