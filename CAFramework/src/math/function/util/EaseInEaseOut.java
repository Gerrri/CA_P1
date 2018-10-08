package math.function.util;

import math.function.FunctionR1R1;


public class EaseInEaseOut extends FunctionR1R1{
	float t0, t1, t2, t3;
	float arcLength;
	float vhalf;
	
	public EaseInEaseOut(float t0, float t1, float t2, float t3, float arcLength)
	{
		super(t0, t3);
		assert ((t1 >= t0) && (t2 >= t1) && (t3 >= t2)) : "EaseInEaseOut values are invalid: "
				+ t0 + ", " + t1 + ", " + t2 + ", " + t3;
		
		this.t0 = t0;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.arcLength = arcLength;
		vhalf = arcLength / (t2 + t3 - t0 - t1);
	}
	
	@Override
	public float eval(float t) {
		float res=0;
		if (t>= t0 && t < t1) {
			res = vhalf * (t*t - 2*t0*t + t0*t0) / (t1-t0);
		} else if (t >= t1 && t < t2) {
			res= vhalf * (2*t - t1 -t0);
		} else if (t >= t2 && t <= t3)  {
			res = vhalf * (t*t - 2*t3*t + t3*(t0+t1) + t2*(t2 - t0 - t1)) / (t2-t3);
		} else if (t > t3)
			res = arcLength;
		return res;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		float t0 = 0.0f;
		float t1 = 1.0f;
		float t2 = 4.0f;
		float t3 = 5.0f;
		float arcLength = 10.0f;
		int samples = 101;
		float delta = (t3-t0) / (samples -1);
		
		EaseInEaseOut f = new EaseInEaseOut(t0, t1, t2, t3, arcLength);
		
		for (int i = 0; i < samples; i++) {
			float t = t0 + i * delta;
			float x = f.eval(t);
			System.out.println(t+ ", " + x);
		}

	}

}
