/*CA Winter 2018/19
*Name,  Vorname :  Asbach, Dustin
*Matrikelnummer :  11117108
*Aufgabenblatt : 8
*Aufgabe : 8.1
*/
package Aufgabenblatt_8.Asbach_Dustin;

import animation.AbstController;
import math.Mat3;
import math.Mat4;
import math.Quaternion;
import math.Vec3;
import math.function.util.PolygonVec3;
import math.function.util.SphericalLinear;
import scenegraph.Channel;

public class QuatRotationController extends AbstController {

	math.function.util.SphericalLinear sl;
	Channel channel;

	public QuatRotationController(String name, Channel channel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.channel = channel;
		//Zeit
			float[] zeit = { 0, 5, 10, 15 };
			
		Vec3[] p = new Vec3[4];
		float pi = (float) Math.PI;
		
		
		//Winkel
			p[0] = new Vec3	(0, 0, 0);
			p[1] = new Vec3	(pi / 2f, -pi / 4f, pi / 2f);
			p[2] = new Vec3	(-pi, pi / 180f * 60f, 0);
			p[3] = new Vec3	(pi / 4f, 0, pi / 4f);
			
		//Funktion
			sl = new SphericalLinear(p, zeit);
	}	

	public boolean update(float time) {
		// geändert?
		if (!super.update(time)) {
			return false;
		}

		float akt = getLocalTime(time);

		if (akt <= 15) {
			Mat3 rotation = sl.eval(akt).toRotationMatrix();
			Mat4 Data = (Mat4) channel.getData();
			Data.set(rotation);
		}

		return true;
	}

}
