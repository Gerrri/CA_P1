
package Aufgabenblatt_6.Asbach_Dustin;

import java.util.ArrayList;

import math.Vec3;
import math.function.FunctionR1R1;
import math.function.FunctionR1Vec3;
import math.function.FunctionR1Vec3Util;
import math.function.util.PolygonVec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.AbstNode;
import scenegraph.AmbientLight;
import scenegraph.Color;
import scenegraph.Group;
import scenegraph.MaterialState;
import scenegraph.PerspectiveCamera;
import scenegraph.PointLight;
import scenegraph.Sphere;
import scenegraph.util.Loader;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;
import animation.FunctionR1Vec3Controller;
import examples.controller.HideController;

public class TestBillard {
	
	public static boolean exit = false;
	private Group world;
    private final Sphere obj;
    private final PerspectiveCamera camera;
	private final AbstRenderer renderer;
	private final ArrayList<AbstController> controllers;

	
	/**
	 * @param width The horizontal window size in pixels
	 * @param height The vertical window size in pixels
	 */
	public TestBillard(int width, int height) 
	{
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Billardkugel");
		renderer.setBackgroundColor(Color.white());
		
		world = new Group ("Komplette Szene");
		camera = new PerspectiveCamera(0, 40, 20);
		camera.setFov(50f);
		camera.setZRange(1f, 100f);
		world.attachChild(camera);
		renderer.setCamera (camera);
		
		controllers = new ArrayList<AbstController>();
		controllers.add (  new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp())); 
		
		// generate light and attach it to the scene
		PointLight light = new PointLight("Lichtquelle");
		light.setTranslation (0, 30, 10);		
		world.attachChild(light);
		
		// Create sphere
		MaterialState mat = new MaterialState();
		mat.setAmbient (0.0f, 0.0f, 0.0f);
		mat.setSpecular(1.0f, 1.0f, 1.0f);
		mat.setDiffuse(1.0f, 0.0f, 0.0f);
		mat.setShininess(100);
		world.attachChild (mat);
		
		// billard ball
		obj = new Sphere();
		obj.setTranslation(0.0f, 0.0f, 0.0f);
		world.attachChild(obj);
		
		// load billard table
		Group billard = Loader.loadMesh("pool.obj");
		billard.setScale(0.15f, 0.15f, 0.15f);
		world.attachChild(billard);
		
		// Create path, where billard ball shall move along. 
		final Vec3[] points = { 
				new Vec3(	-17,	12.3f, 	-5.0f	), 
				new Vec3(	-10,	12.3f, 	8.2f	), 
				new Vec3( 	10, 	12.3f, 	-8.2f	), 
				new Vec3( 	17,		12.3f, 	2		),
				new Vec3(	0,		12.3f, 	8.5f	)};
		
		final float[] grid = {0, 6, 12, 18, 24};
		final float[] t = {6, 12, 14, 15, 17};
		
		PolygonVec3 curve = new PolygonVec3(points, grid);
		ParamTrans pt = new ParamTrans(0, 17, grid,t);
		FunctionR1Vec3 newcurve = FunctionR1Vec3Util.compose(curve, pt);
		
		// Reparametrization
		// Compose curve with reparametrization
		
		
		controllers.add( new FunctionR1Vec3Controller(AbstController.RepeatType.CLAMP, 
				obj.getChannel("Translation"), newcurve));
		
		// HideController will hide the ball at the global start time
		AbstController hc = new HideController (obj.getChannel(AbstNode.HIDDEN));
		hc.setGlobalStartTime(17.3f);
		controllers.add(hc);
		
		world = VisualHelp.makeGrid(world, 10);
		world = VisualHelp.makePath(world, newcurve);
	}

	
	/**
	 * @param time The time in seconds since start of the animation
	 */
	public void update( float time ) {
		boolean updated = false;
//		System.out.println(time);
		for (int i = 0; i < controllers.size(); i++) {
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
		int width  = 1000;
		int height = 600;
							
		TestBillard demo   = new TestBillard( width, height );
		demo.gameLoop();
	}
}