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

import com.fasterxml.jackson.databind.JsonNode;

import env.AgentListener;
import env.Identifier;
import env.Numeral;
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
						System.out.println("Linear:"+msg.linear.x+","+msg.linear.y+","+msg.linear.z);
						System.out.println("Angular:"+msg.angular.x+","+msg.angular.y+","+msg.angular.z);
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
		
		move2goalold(-2.296945,-3.568250,0.499120);	
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

				//System.out.println("RET RI size:"+ret.size());				
				//System.out.println("RET RI:"+ret.toString());
				
		return ret;

	}

	public Collection<Percept> getNextPerception(String agent, String entity) throws Exception {
		return perceptsQueue.peek() != null? new LinkedList<Percept>(perceptsQueue.poll()) : new LinkedList<Percept>();

	}

	
	
	public void move2goalold(double goalx,double goaly,double goalz) {
		double goalX=goalx;
		double goalY=goaly;
		double goalZ=goalz;
		double distance_tolerance = 3;
		
		double linearX=0;
		double linearY=0;
		double linearZ=0;
		
		double angularX=0;
		double angularY=0;
		double angularZ=0;
		
		while (Math.sqrt(Math.pow((goalX - currentPose.position.x), 2) + Math.pow((goalY - currentPose.position.y), 2)) >= distance_tolerance) {
			
		    //Porportional Controller
            
			//linear velocity in the x-axis:
            linearX = 1.5 * Math.sqrt(Math.pow((goalX - currentPose.position.x), 2) + Math.pow((goalY - currentPose.position.y), 2));
            linearY = 0;
            linearZ = 0;
            

            //angular velocity in the z-axis:
            angularX = 0;
            angularY = 0;
            //angularZ = 4 * (Math.atan2(goalY - currentPose.position.y, currentPose.position.x) - self.pose.theta);
            //angularZ = 4 * (Math.atan2(goalY - currentPose.position.y, goalX - currentPose.position.x) - currentPose.position.z);
            angularZ = 4 * (Math.atan2(goalY - currentPose.position.y, goalX - currentPose.position.x) - currentPose.orientation.z);
//
            //Publishing our vel_msg
            //Publisher pub = new Publisher("/cmd_vel", "geometry_msgs/Twist", rosbridge);
            Publisher pub = new Publisher("/cmd_vel_mux/input/teleop", "geometry_msgs/Twist", rosbridge);
            
    		publishing(pub,linearX,linearY,linearZ,angularX,angularY,angularZ,500);
		}
		

	}
	
	public void move2goal() {
		double goalX=-4.548552;
		//double goalY=0;
		double goalY=3.196932;
		//double goalZ=0.500000;
		double goalZ=0;
		
		double distance_tolerance = 1;
		
		double linearX=0;
		double linearY=0;
		double linearZ=0;
		
		double angularX=0;
		double angularY=0;
		double angularZ=0;
		double error=0;
		double phi_old=0;
		double phi_new=0;
		double phi_desired=0;
		double k=1;
		double w=0;
		double t=0.1;
		double v=1;
		
		double x_old=currentPose.position.x;
		double y_old=currentPose.position.y;
		double x_new=0;
		double y_new=0;

		
		while (Math.sqrt(Math.pow((goalX - currentPose.position.x), 2) + Math.pow((goalY - currentPose.position.y), 2)) >= distance_tolerance) {
			
		    //Porportional Controller
           
		//	if norm([r;s] - [x_new;y_new],2) < error_threshold
			
			
			if (goalX >= x_new) {
				// phi_desired = vpa(Math.atan((s-y_new)/(r-x_new)));
				 phi_desired = Math.atan((goalY-y_new)/(goalX-x_new));
			}else {
				if (goalY >= y_new) {
					phi_desired = (Math.PI/2) + Math.atan(Math.abs((goalY-y_new)/(goalX-x_new)));	
				}else {
					phi_desired = -(Math.PI/2) - Math.atan(Math.abs((goalY-y_new)/(goalX-x_new)));
				}
				
			}
			
			
            error=phi_desired-phi_old;
              //w: angular velocity
			
            w=k*error;
            
           
			//linear velocity in the x-axis:;
            linearX=v*Math.cos(phi_old);
            linearY=v*Math.sin(phi_old);
//            linearX=v*Math.cos(phi_new);
//            linearY=v*Math.sin(phi_new);
            
            x_new=x_old+linearX * t;
            y_new=y_old+linearY * t;
            phi_new=t*w+phi_old;
            
            //phi_old=Math.atan((goalY-y_new)/(goalX-x_new));    


            x_old=x_new;
            y_old=y_new;
            phi_old=phi_new;
            
            //Publishing our vel_msg
            //Publisher pub = new Publisher("/cmd_vel", "geometry_msgs/Twist", rosbridge);
            Publisher pub = new Publisher("/cmd_vel_mux/input/teleop", "geometry_msgs/Twist", rosbridge);
            
    		publishing(pub,linearX,linearY,linearZ,angularX,angularY,angularZ,300);
		}
		

	}

	
	
	
	
	
}
