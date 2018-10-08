package examples.controller;

import math.Mat3;
import math.Mat4;
import math.Vec3;
import math.Vec4;
import scenegraph.AbstSpatial;
import scenegraph.Channel;
import scenegraph.Joint;
import animation.AbstController;

public class OneJointIKController extends AbstController {

	private AbstSpatial target;
	private Joint joint;
    
	/**
	 * channel to manipulate 3-dimensional characteristics of an object; the
	 * object, which is available through the channel, must be of type Vec3
	 */

    public OneJointIKController(AbstSpatial target, Joint joint) {
    	super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    	
    	this.target = target;
    	this.joint = joint;
    }
	

	/**
	 * Main method to manipulate the channel with the value derived from the 3D
	 * function
	 */
	@Override
	public boolean update(float time) {
		// check if controller needs to do something
		if (!super.update(time)) {
			return false;
		}
		
		// W[welt] <- [Target]

		// W[welt]<-[Joint]
	
		
		// W[joint]<-[welt]
		
		// Berechne Target in  Weltkoordinaten: W[welt] <- [Target] * p[target]
		
		// Berechne Target in lokalen Jointkoordinaten von Joint i. W[joint]<-[welt] * p[welt]
	
		// Berechne Koordinaten Gelenk i+1 in lokalen Koordinaten von Joint i: e[joint]
		
		// Berechne Rotationsmatrix	
		Mat4 rot = new Mat4();
		
		// Aktualisiere Rotationsmatrix
		Channel channel = joint.getChannel(AbstSpatial.ROTATION);
    	
		return true;	// channel updated
	}
}
