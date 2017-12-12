package ros.msgs.pose;

/**
 * A Java Bean for the Vector3 ROS geometry_msgs/Vector3 message type. This can be used both for publishing Vector3 messages to
 * {@link ros.RosBridge} and unpacking Vector3 messages received from {@link ros.RosBridge} (see the {@link ros.tools.MessageUnpacker}
 * documentation for how to easily unpack a ROS Bridge message into a Java object).
 * @author James MacGlashan.
 */
public class Vector4 {
	public double x;
	public double y;
	public double z;
	public double w;

	public Vector4(){}

	public Vector4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
}
