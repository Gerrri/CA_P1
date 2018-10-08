package examples.controller;

import math.function.FunctionR1Vec3;
import scenegraph.Channel;
import animation.AbstController;
import animation.AbstController.RepeatType;

public class FuncRotController extends AbstController {

    private Channel rotChannel;    
    private FunctionR1Vec3 func;
    
    
	/**
	 * Die Vec3-Werte die ein Funktion func liefert werden als Eulerwinkel
	 * interpretiert und daraus eine Rotationsmatrix erzeugt.
	 * Intrinsische y-x-z''-Rotation
	 * @param func Die Funktion liefert Eulerwinkel
	 * @param rotChannel Channel-Objekt vom Typ Mat4
	 * @param loopMode 
	 */
    public FuncRotController(FunctionR1Vec3 func, Channel rotChannel, RepeatType loopMode) {
    	super(loopMode, func.getTMin(), func.getTMax());
    	
    	this.func = func;
    	this.rotChannel = rotChannel;
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
				
		// map application time to controller time
    	float ctrlTime = getLocalTime(time);
  
    	// ToDo

    	
		return true;	// channel updated
	}
}
