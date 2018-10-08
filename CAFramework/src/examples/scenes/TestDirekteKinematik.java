package examples.scenes;

import java.util.ArrayList;

import math.Mat4;
import math.Vec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.AbstLight;
import scenegraph.AmbientLight;
import scenegraph.Color;
import scenegraph.Group;
import scenegraph.PerspectiveCamera;
import scenegraph.PointLight;
import scenegraph.util.G3djLoader;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;

public class TestDirekteKinematik {
	/**
	 * flag is set, when program shall be terminated
	 */
	public static boolean exit = false;		

	/**
	 * root of the world graph
	 */
	private Group world;				
	
    /**
     * camera through which world is seen
     */
    private final PerspectiveCamera camera;
    
	/**
	 * render engine for the world 
	 */
	private final AbstRenderer renderer;
	private final ArrayList<AbstController> controllers;

	private AbstLight light;
	
	/**
	 * Test class for importing mesh into the world 
	 */
	public TestDirekteKinematik(int width, int height)
	{
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Direkte Kinematik");
		renderer.setBackgroundColor(Color.white());
		
		// generate root element of the world
		world = new Group ("Anna");
		
		camera = new PerspectiveCamera(0, 10, 30);
		camera.setZRange(1f, 200f);
		camera.focus (new Vec3(0, 9, 0));
		world.attachChild(camera);

		light = new AmbientLight("ambientes Licht");
		light.setDiffuse (new Vec3(0.1f, 0.1f, 0.1f));
		world.attachChild(light);
	
		light = new PointLight("Punktlicht");
		light.setDiffuse (new Vec3(1f, 1f, 1f));
		light.setTranslation (10,10,60);
		world.attachChild(light);
	    
		// load human model (skeleton and skin mesh)
		G3djLoader loader = new G3djLoader();
		Group modell = loader.read("anna_stehend.g3dj");
		modell.setScale(0.1f, 0.1f, 0.1f);
		modell.setTranslation(0,0,2);
//		modell.setWireFrame(true);	
		
		world.attachChild(modell);
		world = VisualHelp.makeGrid(world, 10);
		
		controllers = new ArrayList<AbstController>();
		controllers.add(new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp())); 

		//
		// search the joints in the model, that shall be animated
		//
		
		//
		// set controllers to animate joints
		//
		
		
		world.updateWorldTransform(new Mat4());
		world.updateWorldData();
		renderer.setCamera (camera);
	}
	

	/**
	 * @param time The time in seconds since start of the animation
	 */
	public void update(float time)
	{
		boolean update = false;
		for (int i = 0; i < controllers.size(); i++)
		{
			if (controllers.get(i).update(time))
			{
				update = true;
			}
		}
		if (update)
		{
			world.updateWorldTransform(new Mat4());
			world.updateWorldData();
			renderer.setCamera(camera);
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
		int width  = 800;
		int height = 800;
							
		TestDirekteKinematik demo   = new TestDirekteKinematik( width, height );
		demo.gameLoop();
	}
	
}
