/*CA Winter 2018/19
*Name,  Vorname :  Asbach, Dustin
*Matrikelnummer :  11117108
*Aufgabenblatt : 8
*Aufgabe : 8.2
*/
package Aufgabenblatt_8.Asbach_Dustin;

import animation.AbstController;
import math.Mat4;
import math.Vec3;
import math.function.FunctionR1Vec3;
import math.function.FunctionR1Vec3Util;
import math.function.util.BezierCurve;
import math.function.util.PolygonVec3;
import math.function.util.ScaleInterval;
import scenegraph.Channel;

public class EulerRotationController2 extends AbstController {

	FunctionR1Vec3 ret;
	Channel channel;

	public EulerRotationController2(String name, Channel channel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.channel = channel;
		Vec3[][] points = new Vec3[3][4];
		float pi = (float) Math.PI;
		// 1
			points[0][0] = new Vec3	(0, 0, 0);
			points[0][1] = new Vec3	(pi / 180f * 60f, -pi / 180f * 10f, pi / 180f * 40f);
			points[0][2] = new Vec3	(pi / 180f * 110f, -pi / 180f * 30f, pi / 180f * 80f);
			points[0][3] = new Vec3	(pi / 2f, -pi / 4f, pi / 2f);
		// 2
			points[1][0] = new Vec3	(pi / 2f, -pi / 4f, pi / 2f);
			points[1][1] = new Vec3	(pi / 180f * 70f, pi / 180f * -50f, pi / 180f * 100f);
			points[1][2] = new Vec3	(pi / 180f * -200f, pi / 180f * 80f, pi / 180f * 20f);
			points[1][3] = new Vec3	(-pi, pi / 180f * 60f, 0);
		// 3
			points[2][0] = new Vec3	(-pi, pi / 180f * 60f, 0);
			points[2][1] = new Vec3	(pi / 180f * -160f, pi / 180f * 40f, pi / 180f * -20f);
			points[2][2] = new Vec3	(pi / 180f * -20f, pi / 180f * 20f, pi / 180f * 20f);
			points[2][3] = new Vec3	(pi / 4f, 0, pi / 4f);
			
		
		// Bezierkurve
		BezierCurve[] b1 = new BezierCurve[3];
			b1[0] = new BezierCurve(points[0]);
			b1[1] = new BezierCurve(points[1]);
			b1[2] = new BezierCurve(points[2]);
		
		// Skalierung
		ScaleInterval[] scal = new ScaleInterval[3];
			scal[0] = new ScaleInterval(0, 5);
			scal[1] = new ScaleInterval(5, 10);
			scal[2] = new ScaleInterval(10, 15);
			
		FunctionR1Vec3[] funktion = new FunctionR1Vec3[3];
		for (int i = 0; i < 3; i++) {
			funktion[i] = FunctionR1Vec3Util.compose(b1[i], scal[i]);
		}
		// assembling segments into final curve
		ret = FunctionR1Vec3Util.connect(funktion[0], 5, funktion[1], 10, funktion[2]);
	}

	public boolean update(float time) {
		// nur wenn sich was gändert hat
		if (!super.update(time)) {
			return false;
		}

		float localTime = getLocalTime(time);

		if (localTime <= 15) {
			
			// Matrix erstellen
				Mat4 rotatation = Mat4.rotationZ(ret.eval(localTime).z)
						.mul(Mat4.rotationY(ret.eval(localTime).y)
						.mul(Mat4.rotationX(ret.eval(localTime).x)));

			
				Mat4 Data = (Mat4) channel.getData();
				Data.set(rotatation);
		}

		return true;
	}

}
