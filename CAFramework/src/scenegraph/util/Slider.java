package scenegraph.util;

import math.Vec3;
import scenegraph.Color;
import scenegraph.ColorState;
import scenegraph.Group;
import scenegraph.Line;
import scenegraph.Sphere;

public class Slider {
	
	private float len; 
	private int steps;
	private Sphere ball; 
    private Vec3 up, down; 

	/**
	 * Constructs a slider on a specified position and with a specified length.
	 * The slider is attached to a specified group.
	 * 
	 * @param world
	 * @param pos
	 * @param len
	 */
	public Slider (Group world, Vec3 pos, float len) 
	{
		this.len = len;
		this.steps = 20;
		Group sliderGroup = new Group("Slider Group");
		ball = new Sphere("Slider Ball");
		float sliderBallSize = len / steps;
		ball.setScale (sliderBallSize, sliderBallSize, sliderBallSize);
		ColorState ballColor = new ColorState(Color.red());
		sliderGroup.attachChild(ballColor);
		sliderGroup.attachChild(ball);
		Vec3 sliderStart = new Vec3();
		Vec3 sliderEnd = new Vec3 (0, len, 0);
		Line slider = new Line (new Vec3[]{sliderStart, sliderEnd});
		slider.setLineWidth(1);
		ColorState sliderColor = new ColorState(Color.white());
		sliderGroup.attachChild(sliderColor);
		sliderGroup.attachChild(slider);
		sliderGroup.setTranslation(pos);
		world.attachChild(sliderGroup);
		
		float deltaSlider = len / steps;
		up = new Vec3 (0, deltaSlider, 0);
		down = new Vec3 (0, -deltaSlider, 0);
	}
	
	public void moveUp()
	{	
		Vec3 pos = ball.getTranslation();
		pos.add(up);
		pos.y = Math.min (len, pos.y);
		ball.setTranslation(pos);
	}
	
	public void moveDown ()
	{
		Vec3 pos = ball.getTranslation();
		pos.add(down);
		pos.y = Math.max (0, pos.y);
		ball.setTranslation(pos);
	}
	
	public int getSteps() {
		return steps;
	}

}
