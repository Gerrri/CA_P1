package Aufgabenblatt_8.Asbach_Dustin;
/*CA Winter 2018/19
*Name,  Vorname :  Asbach, Dustin
*Matrikelnummer :  11117108
*Aufgabenblatt : 8
*Aufgabe : 8.2
*/
import animation.AbstController;
import math.Mat3;
import math.Mat4;
import math.function.FunctionR1Vec3;
import scenegraph.Channel;

public class OrientationController extends AbstController {

	FunctionR1Vec3 func;
	Channel channel;

	public OrientationController(String name, FunctionR1Vec3 pfad, Channel channel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.func = pfad;
		this.channel = channel;
	}

	@Override
	public boolean update(float time) {
		//geändert?	
		if (!super.update(time)) {
			return false;
		}

		float zeit = getLocalTime(time);
		Mat3 rotataion = func.getFrenetFrame(zeit);
		Mat4 Data = (Mat4) channel.getData();
		Data.set(rotataion);

		return true;
	}
}
