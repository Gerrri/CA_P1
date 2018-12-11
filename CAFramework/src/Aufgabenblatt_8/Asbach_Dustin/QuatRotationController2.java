/*CA Winter 2018/19
*Name,  Vorname :  Asbach, Dustin
*Matrikelnummer :  11117108
*Aufgabenblatt : 8
*Aufgabe : 8.2
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

public class QuatRotationController2 extends AbstController {

	SphericalCubic sc;
	Channel channel;

	public QuatRotationController2(String name, Channel channel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.channel = channel;
		//Zeit
		float[] zeit = new float[4];
		for (int i = 0; i < zeit.length; i++) {
			zeit[i] = i * 5f;
		}
		
		Vec3[] winkel = new Vec3[4];
		Vec3[] control = new Vec3[6];
		float pi = (float) Math.PI;
		
		// 	Winkel
			winkel[0] = new Vec3(0, 0, 0);
			winkel[1] = new Vec3(pi / 2f, -pi / 4f, pi / 2f);
			winkel[2] = new Vec3(-pi, pi / 180f * 60f, 0);
			winkel[3] = new Vec3(pi / 4f, 0, pi / 4f);
			
			
			
			
		// 	für controller
			control[0] = new Vec3	(pi / 180f * 60f, -pi / 180f * 10f, pi / 180f * 40f);
			control[1] = new Vec3	(pi / 180f * 110f, -pi / 180f * 30f, pi / 180f * 80f);
			control[2] = new Vec3	(pi / 180f * 70f, pi / 180f * -50f, pi / 180f * 100f);
			control[3] = new Vec3	(pi / 180f * -200f, pi / 180f * 80f, pi / 180f * 20f);
			control[4] = new Vec3	(pi / 180f * -160f, pi / 180f * 40f, pi / 180f * -20f);
			control[5] = new Vec3	(pi / 180f * -20f, pi / 180f * 20f, pi / 180f * 20f);
		
			
			
			
		// 	Funktion
			sc = new SphericalCubic(winkel, control, zeit);
	}

	public boolean update(float time) {
		// gerändert?
		if (!super.update(time)) {
			return false;
		}

		float akt = getLocalTime(time);

		if (akt <= 15) {
			Mat3 rot = sc.eval(akt).toRotationMatrix();
			Mat4 vRef = (Mat4) channel.getData();
			vRef.set(rot);
		}

		return true;
	}

}
