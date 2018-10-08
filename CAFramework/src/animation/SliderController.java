package animation;

import org.lwjgl.input.Keyboard;
import scenegraph.Channel;
import scenegraph.PrimitiveData;
import scenegraph.util.Slider;

public class SliderController extends AbstController {
	private float min, max;
	private Channel channel;
    private float deltaValue;
    private float actualValue;
    private Slider slider;
    private boolean sliderSelected; 
    private int key;
    
    private final int[] keys = {Keyboard.KEY_0, Keyboard.KEY_1, Keyboard.KEY_2, Keyboard.KEY_3, Keyboard.KEY_4, 
    		Keyboard.KEY_5, Keyboard.KEY_6, Keyboard.KEY_7, Keyboard.KEY_8, Keyboard.KEY_9};
	
	public SliderController(Slider slider, Channel value, float min, float max, int id) 
	{
		this.slider = slider;
		this.min = min;
		this.max = max;
	    this.channel = value;
	   	deltaValue = (max - min) / slider.getSteps();
	   	sliderSelected = false; 
	   	if (id >= 0 && id <= 9)
	   		key = keys [id];
	}
	
	public SliderController(Slider slider, Channel value, int id) 
	{
		this (slider, value, 0, 1, id);
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
		for (int i = 0; i < keys.length; i++)
		{
			int trykey = keys[i];
			if (Keyboard.isKeyDown(trykey))
			{
				if (trykey == key)
					sliderSelected = true; 
				else
					sliderSelected = false;
			}	
		}

		if (sliderSelected)
		{
			if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
				actualValue += deltaValue; 
				actualValue = Math.min(max, actualValue);
				slider.moveUp();
			} else if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
				actualValue -= deltaValue;
				actualValue = Math.max(min, actualValue);
				slider.moveDown();
			}
			((PrimitiveData) channel.getData()).f = actualValue;
			return true;	// channel updated
		}

		return false;
	}

}
