package examples.scenes;

import java.util.ArrayList;

import math.Vec3;
import math.function.util.PolygonVec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.Group;
import scenegraph.MaterialState;
import scenegraph.PerspectiveCamera;
import scenegraph.PointLight;
import scenegraph.Sphere;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;
import animation.FunctionR1Vec3Controller;

public class TestVelocityControl {
	
	public static boolean exit = false;
	private Group world;
    private final PerspectiveCamera camera;
	private final PointLight light;
	private final AbstRenderer renderer;
	private final ArrayList<AbstController> controllers;

	
	/**
	 * @param width The horizontal window size in pixels
	 * @param height The vertical window size in pixels
	 */
	public TestVelocityControl(int width, int height) 
	{
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Kugel rollt im Viereck");
		
		world = new Group ("Komplette Szene");
		camera = new PerspectiveCamera(0, 0, 30);
		camera.setFov(50f);
		camera.setZRange(5f, 100f);
		world.attachChild(camera);
		renderer.setCamera (camera);
		
		controllers = new ArrayList<AbstController>();
		controllers.add (  new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp())); 

		light = new PointLight("Lichtquelle1");
		light.setTranslation (0.0f, 20, 16.0f);
		light.setAmbient (new Vec3(0.0f, 0.0f, 0.0f));
		light.setDiffuse (new Vec3(1.0f, 1.0f, 1.0f));
		light.setSpecular(new Vec3(1.0f, 1.0f, 1.0f));
		light.setConstantAttenuation(0.0f);
		world.attachChild(light);
		
		// Create sphere
		MaterialState mat = new MaterialState();
		mat.setAmbient (0.0f, 0.0f, 0.0f);
		mat.setSpecular(1.0f, 1.0f, 1.0f);
		mat.setDiffuse(1.0f, 0.0f, 0.0f);
		mat.setShininess(100);
		world.attachChild (mat);
		
		Sphere obj = new Sphere();
		obj.setTranslation(0.0f, 0.0f, 0.0f);
		world.attachChild(obj);
		
		// Create square, where sphere shall move along. 
		final Vec3[] points = { 
				new Vec3(-6,-6, 0), 
				new Vec3(-6, 6, 0), 
				new Vec3( 6, 6, 0), 
				new Vec3( 6,-6, 0),
				new Vec3(-6,-6, 0)};
		final float[] grid = {0, 6, 12, 18, 24};
		PolygonVec3 curve = new PolygonVec3(points, grid);
					
		controllers.add( new FunctionR1Vec3Controller(AbstController.RepeatType.CLAMP, 
				obj.getChannel("Translation"), curve));
		
//		world = VisualHelp.makeGrid(world, 10);
		world = VisualHelp.makePath(world, curve);
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
		int width  = 800;
		int height = 600;
							
		TestVelocityControl demo   = new TestVelocityControl( width, height );
		demo.gameLoop();
	}
}