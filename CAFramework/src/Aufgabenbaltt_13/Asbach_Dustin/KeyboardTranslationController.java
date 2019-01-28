package Aufgabenbaltt_13.Asbach_Dustin;

import math.Vec3;

import org.lwjgl.input.Keyboard;

import scenegraph.Channel;
import animation.AbstController;

public class KeyboardTranslationController extends AbstController {

    private Channel transChannel;
    private Vec3 pos;
    private Vec3 deltaX, deltaY, deltaZ, mdeltaX, mdeltaY, mdeltaZ;
    
    
	/**
	 * channel to manipulate 3-dimensional characteristics of an object; the
	 * object, which is available through the channel, must be of type Vec3
	 */

    public KeyboardTranslationController(Channel transChannel, Vec3 startPos) {
    	super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    	
    	this.transChannel = transChannel;
    	this.pos = new Vec3(startPos);
    	deltaX = new Vec3(1.0f, 0.0f, 0.0f);
    	deltaY = new Vec3(0.0f, 1.0f, 0.0f);
    	deltaZ = new Vec3(0.0f, 0.0f, 1.0f);
    	mdeltaX = new Vec3(-1.0f, 0.0f, 0.0f);
    	mdeltaY = new Vec3( 0.0f,-1.0f, 0.0f);
    	mdeltaZ = new Vec3( 0.0f, 0.0f,-1.0f);
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

		if (Keyboard.isKeyDown(Keyboard.KEY_U)) {
			pos.add(deltaZ);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_J)) {
			pos.add(mdeltaZ);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
			pos.add(mdeltaX);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
			pos.add(deltaX);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			pos.add(mdeltaY);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
			pos.add(deltaY);
		}
		
		((Vec3) transChannel.getData()).set(pos);
    	
		return true;	// channel updated
	}
}
