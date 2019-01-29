package Aufgabenbaltt_13.Asbach_Dustin;

import java.util.ArrayList;

import math.Vec3;
import particles.Emitter;
import scenegraph.Channel;
import java.util.Random;

public class LineEmitter extends Emitter{
	Vec3 x1,x2;
	ArrayList<Channel> channels;
	
	public static String X1 = "x1";
	public static String X2 = "x2";
	
	public LineEmitter(float emissionRate, float lifespan, Vec3 x1, Vec3 x2) {
		super(emissionRate, lifespan);
		this.x1 = x1;
		this.x2 = x2;
		channels = new ArrayList<>(1);
		channels.add(new Channel(X1, this.x1));
		channels.add(new Channel(X2, this.x2));
		
	}
	
	public Vec3 getInitialPosition() {
		float x,y,z;
		
		x = random_cord(x1.x, x2.x);
		y = random_cord(x1.y, x2.y);
		z = random_cord(x1.z, x2.z);
		
	
		return new Vec3(x,y,z);
	}
	

	private float random_cord(float p1, float p2) {
		Random random = new Random();
		float ret,min,max;
		
		if(p1>p2) {
			max = p1;
			min = p2;
		}else{
			max = p2;
			min = p1;
		}
		
		ret = min + random.nextFloat() * (max - min);
		
		return ret;
	}


}
