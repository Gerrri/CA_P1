
package Aufgabenblatt_5.Asbach_Dustin;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import math.Vec3;
import math.function.util.Looping;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.*;
import scenegraph.util.Loader;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;

public class TestUfo {
	
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
    private PerspectiveCamera cam;
    private PerspectiveCamera ufo_cam;
    
	/**
	 * render engine for the scene 
	 */
	private final AbstRenderer renderer;
	private final ArrayList<AbstController> controllers;

	private FunctionR1Vec3Controller ufo_ctl;
	int k = 1;
	/**
	 * Constructor constructs all scene objects and orders them hierarchically.
	 * 
	 * @param width
	 *            The horizontal window size in pixels
	 * @param height
	 *            The vertical window size in pixels
	 */
	public TestUfo(int width, int height) 
	{
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Aufgabenblatt 5");
		
		// generate root element of the scene
		world = new Group ("Ufo auf der Achterbahn");
		
		// generate camera and attach it to the scene
		cam = new PerspectiveCamera(0,5,10);
		world.attachChild(cam);		

		AmbientLight al = new AmbientLight("Umgebungslicht");
		al.setDiffuse (new Vec3(1f, 1f, 1f));
		world.attachChild(al);
		
		// generate light and attach it to the scene
		PointLight light = new PointLight("Lichtquelle");
		light.setTranslation (0, 5, 10);		
		world.attachChild(light);

		// set the color of the sibling elements
		Group ufo = Loader.loadMesh("ufo.obj");
		ufo.setScale(0.05f, 0.05f, 0.05f);
		world.attachChild(ufo);
		
		//Toruskurve erzeugen
		Toruskurve tk = new Toruskurve(3, 2, 1, 2, 5);
		ufo_ctl = new FunctionR1Vec3Controller(AbstController.RepeatType.CYCLE, ufo.getChannel(AbstSpatial.TRANSLATION), tk);
		
		//Looping erzeugen
		Looping looping = new  Looping();
		
		//Cam erzeugen
		ufo_cam = new PerspectiveCamera();
		world.attachChild(ufo_cam);
		
		
		controllers = new ArrayList<AbstController>();
		controllers.add(ufo_ctl);
		controllers.add(new CameraController(ufo_cam.getChannel(AbstCamera.TRANSLATION), 
				ufo_cam.getChannel(AbstSpatial.ROTATION), 
				ufo_cam.getFocus(), 
				ufo_cam.getUp(), 
				ufo.getChannel(AbstSpatial.TRANSLATION),looping));
		controllers.add(new CameraController(cam.getChannel(AbstCamera.TRANSLATION), 
				cam.getChannel(AbstCamera.ROTATION), cam.getFocus(), cam.getUp())); 
	
		
		//world = VisualHelp.makeGrid(world,20);
		world = VisualHelp.makePath(world,tk);
		world = VisualHelp.makePath(world, looping);
		renderer.setCamera(cam);
		renderer.setCamera(ufo_cam);
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
			if(Keyboard.isKeyDown(Keyboard.KEY_1) && k!=1) {k = 1;}	
			if(Keyboard.isKeyDown(Keyboard.KEY_2) && k!=2) {k = 2;}
			
			if(k == 1) {renderer.setCamera(cam);}
			if(k == 2) {renderer.setCamera(ufo_cam);}
			
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
		
		while (!closeRequested() && !TestUfo.exit) {				
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
		int width  = 800;
		int height = 800;
							
		TestUfo demo   = new TestUfo( width, height );
		demo.gameLoop();
	}
}