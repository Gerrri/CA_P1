package examples.controller;


import math.function.FunctionR1Vec3;
import scenegraph.Channel;
import animation.AbstController;
import animation.AbstController.RepeatType;

public class FuncRootController extends AbstController {

    private Channel transChannel;
    private Channel rotChannel;
    
    private FunctionR1Vec3 func;
    
    
	/**
	 * Die Vec3-Werte die ein Funktion func liefert wird dem transChannel
	 * übergeben. * Der Frenet-Frame wird als Rotationsmatrix interpretiert und
	 * dem Channel rotChannel übergeben
	 * 
	 * @param func
	 *            Die Funktion liefert Eulerwinkel
	 * @param transChannel
	 *            Channel-Objekt vom Typ Vec3
	 * @param rotChannel
	 *            Channel-Objekt vom Typ Mat4
	 * @param loopMode
	 */
    public FuncRootController(FunctionR1Vec3 func, Channel transChannel,  Channel rotChannel, RepeatType loopMode) {
    	super(loopMode, func.getTMin(), func.getTMax());
    	
    	this.func = func;
    	this.transChannel = transChannel;
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