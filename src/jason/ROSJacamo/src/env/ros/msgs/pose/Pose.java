package ros.msgs.pose;

/**
 * A Java Bean for the Vector3 ROS geometry_msgs/Twist message type. This can be used both for publishing Twist messages to
 * {@link ros.RosBridge} and unpacking Twist messages received from {@link ros.RosBridge} (see the {@link ros.tools.MessageUnpacker}
 * documentation for how to easily unpack a ROS Bridge message into a Java object).
 * @author James MacGlashan.
 */
public class Pose {
	public Vector3 position = new Vector3();
	public Vector4 orientation = new Vector4();

	public Pose(){}

	public Pose(Vector3 position, Vector4 orientation) {
		this.position = position;
		this.orientation = orientation;
	}
}
