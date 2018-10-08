
package examples.controller;

import java.util.ArrayList;

import math.Vec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.*;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;

public class TestTiming {
	
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
    private final PerspectiveCamera camera;
    
	/**
	 * render engine for the scene 
	 */
	private final AbstRenderer renderer;
	private final ArrayList<AbstController> controllers;
	
	/**
	 * Constructor constructs all scene objects and orders them hierarchically.
	 * 
	 * @param width
	 *            The horizontal window size in pixels
	 * @param height
	 *            The vertical window size in pixels
	 */
	public TestTiming(int width, int height) 
	{
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Aufgabenblatt 4");
		
		// generate root element of the scene
		world = new Group ("Kugelszene");
		
		// generate camera and attach it to the scene
		camera = new PerspectiveCamera(0,0,20);
		camera.focus(new Vec3(0,5,0));
		camera.setZRange (0.1f, 100);
		world.attachChild(camera);		

		// generate light and attach it to the scene
		PointLight light = new PointLight("Lichtquelle");
		light.setTranslation (0, 5, 10);		
		world.attachChild(light);

		// initialize controllers 
		controllers = new ArrayList<AbstController>();
		
		// add camera controller
		controllers.add(new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp()));
		
		// generate a sphere and reuse it in all six circling objects
		Sphere objTemplate = new Sphere(0.5f);
	    		
	    // green spheres
	    
		MaterialState mat = new MaterialState();
		mat.setDiffuse (0f, 1f, 0f, 1);
		world.attachChild (mat);
		
	    Sphere obj = objTemplate;
		Group test1 = new Group ("Objekt 1");
		test1.attachChild(obj);
		test1.setTranslation(-3, 10, 0);
		world.attachChild(test1);
		AbstController c1 = new CirclePosController("Kreisel1", 1f, obj.getChannel(AbstSpatial.TRANSLATION));
		controllers.add(c1);
		
		obj = new Sphere(objTemplate);
		Group test2 = new Group ("Objekt 2");
		test2.attachChild(obj);
		test2.setTranslation(3, 10, 0);
		world.attachChild(test2);
		
		
		// red spheres
		
		mat = new MaterialState();
		mat.setDiffuse (1f, 0f, 0f, 1);
		world.attachChild (mat);
		
		obj = new Sphere(objTemplate);
		Group test3 = new Group ("Objekt 3");
		test3.attachChild(obj);
		test3.setTranslation(-3, 5, 0);
		world.attachChild(test3);
		AbstController c3 = new CirclePosController("Kreisel3", 1f, obj.getChannel(AbstSpatial.TRANSLATION));
		controllers.add(c3);
		
		obj = new Sphere(objTemplate);
		Group test4 = new Group ("Objekt 4");
		test4.attachChild(obj);
		test4.setTranslation(3, 5, 0);
		world.attachChild(test4);
		
		// blue spheres
		
		mat = new MaterialState();
		mat.setDiffuse (0f, 0f, 1f, 1);
		world.attachChild (mat);
		
		obj = new Sphere(objTemplate);
		Group test5 = new Group ("Objekt 5");
		test5.attachChild(obj);
		test5.setTranslation(-3, 0, 0);
		world.attachChild(test5);
		AbstController c5 = new CirclePosController("Kreisel5", 1f, obj.getChannel(AbstSpatial.TRANSLATION));
		controllers.add(c5);
		
		mat = new MaterialState();
		mat.setDiffuse (0f, 0f, 1f, 1);
		world.attachChild (mat);
		obj = new Sphere(objTemplate);
		Group test6 = new Group ("Objekt 6");
		test6.attachChild(obj);
		test6.setTranslation(3, 0, 0);
		world.attachChild(test6);
			
		world = VisualHelp.makeGrid(world, 15);
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
		{
			renderer.setCamera (camera);
		}
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
		int height = 600;
							
		TestTiming demo   = new TestTiming( width, height );
		demo.gameLoop();
	}
}