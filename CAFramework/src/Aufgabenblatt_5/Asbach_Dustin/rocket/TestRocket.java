
package Aufgabenblatt_5.Asbach_Dustin.rocket;

import java.util.ArrayList;

import Aufgabenblatt_5.Asbach_Dustin.ufo.FunctionR1Vec3Controller;
import math.Vec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.*;
import scenegraph.util.Loader;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;

public class TestRocket {
	
	/**
	 * flag is set, when program shall be terminated
	 */
	public static boolean exit = false;		

	/**
	 * root of the scene graph
	 */
	private Group world;				
	
    /**
     * camera through which scene is seen
     */
    private PerspectiveCamera camera;
    
	/**
	 * render engine for the scene 
	 */
	private final AbstRenderer renderer;
	private final ArrayList<AbstController> controllers;
	
	private FunctionR1Vec3Controller rocket_ctl;
	
	/**
	 * Constructor constructs all scene objects and orders them hierarchically.
	 * 
	 * @param width
	 *            The horizontal window size in pixels
	 * @param height
	 *            The vertical window size in pixels
	 */
	public TestRocket(int width, int height) 
	{
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Aufgabe 5.2");
		
		// generate root element of the scene
		world = new Group ("Raketenstart");
		
		// generate camera and attach it to the scene
		camera = new PerspectiveCamera (0,30,80);
		camera.focus (new Vec3(0,30,0));
		camera.setZRange(1f, 200f);
		world.attachChild(camera);		

		AmbientLight al = new AmbientLight("Umgebungslicht");
		al.setDiffuse (new Vec3(1f, 1f, 1f));
		world.attachChild(al);
		
		// generate light and attach it to the scene
		PointLight light = new PointLight("Lichtquelle");
		light.setTranslation (0, 5, 10);		
		world.attachChild(light);

		// set the color of the sibling elements
		Group ufo = Loader.loadMesh("tintin.obj");
		ufo.setScale(0.5f, 0.5f, 0.5f);
		world.attachChild(ufo);
		
		// TakeOff + Controller			TakeOff(beschleunigung, dauer1, start, dauer2, tmin,tmax
		TakeOff to = new TakeOff(new Vec3(0, 0.2f, 0), 2.0f, new Vec3(0,0,0), 3.0f, 0, 15);
		rocket_ctl = new FunctionR1Vec3Controller(AbstController.RepeatType.CLAMP, ufo.getChannel(AbstSpatial.TRANSLATION),to);
		
		
		
		controllers = new ArrayList<AbstController>();
		controllers.add(rocket_ctl);
		controllers.add(new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp())); 
		
		
		world = VisualHelp.makeGrid(world, 5);
		renderer.setCamera (camera);
	}

	
	/**
	 * @param time The time in seconds since start of the animation
	 */
	public void update( float time ) {
		boolean updated = false;
		for (int i = 0; i < controllers.size(); i++) 
		{
			if (controllers.get(i).update(time))
				updated = true;
		}
			
		if (updated)
			renderer.setCamera (camera);
	}
	
	/**
	 * Gameloop
	 */
	private  void gameLoop() {
		final int FPS = 25;					// frames per second
		final int deltaTime = 1000/FPS;		// delta time in milliseconds
		long updateRealTime;
		long sleepTime;						// time to wait until next frame
		long animationTime;					// animation time in milliseconds (starts at 0)
		long animationStartTime;			// system time when the animation started 
		float animationTimeSec;				// animation time in seconds

		
		updateRealTime = System.currentTimeMillis();
		animationStartTime = updateRealTime;
		
		while (!closeRequested() && !exit) {				
			// animationTime in seconds
			animationTime = updateRealTime - animationStartTime;
			animationTimeSec = (float)animationTime/1000.0f;
			update( animationTimeSec );	
			draw();
			updateRealTime += deltaTime;
			sleepTime = updateRealTime - System.currentTimeMillis();
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			else {
				updateRealTime = System.currentTimeMillis();
			}
		}
		
		cleanup();
	}
			
	public void draw()
	{
		renderer.clearBuffers();
		world.draw(renderer);
		renderer.displayBackBuffer();
	}
	
	public boolean closeRequested()
	{
		return renderer.closeRequested();
	}
	
	public void cleanup()
	{
		renderer.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int width  = 500;
		int height = 1000;
							
		TestRocket demo   = new TestRocket( width, height );
		demo.gameLoop();
	}
}