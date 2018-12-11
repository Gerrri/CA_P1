/*CA Winter 2018/19
*Name,  Vorname :  Asbach, Dustin
*Matrikelnummer :  11117108
*Aufgabenblatt : 8
*Aufgabe : 8.1
*/
package Aufgabenblatt_8.Asbach_Dustin;

import animation.AbstController;
import math.Mat4;
import math.Vec3;
import math.function.util.PolygonVec3;
import scenegraph.Channel;

public class EulerRotationController extends AbstController {

	math.function.util.PolygonVec3 linear;
	Channel channel;

	public EulerRotationController(String name, Channel channel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		
		this.channel = channel;
		// Zeiten dfinieren
			float[] zeit = { 0, 5, 10, 15 };
		
		
		// Winkel
			Vec3[] points = new Vec3[4];
			float pi = (float) Math.PI;
			points[0] = new Vec3(0, 0, 0);
			points[1] = new Vec3(pi / 2f, -pi / 4f, pi / 2f);
			points[2] = new Vec3(-pi, pi / 180f * 60f, 0);
			points[3] = new Vec3(pi / 4f, 0, pi / 4f);
			
			
		//Funktion
			linear = new PolygonVec3(points, zeit);
	}

	public boolean update(float time) {
		// nur wenn sich was gändert hat
		if (!super.update(time)) {
			return false;
		}

		float localTime = getLocalTime(time);

		if (localTime <= 15) {
			
			// Matrix erstellen
				Mat4 rotatation = Mat4.rotationZ(linear.eval(localTime).z)
						.mul(Mat4.rotationY(linear.eval(localTime).y)
						.mul(Mat4.rotationX(linear.eval(localTime).x)));

			
				Mat4 Data = (Mat4) channel.getData();
				Data.set(rotatation);
		}

		return true;
	}

}
