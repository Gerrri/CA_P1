package Aufgabenblatt_6.Asbach_Dustin;


/**
*CA Winter 2018/19
*Name , Vorname : Asbach , Dustin
*Matrikelnummer : 11117108
*Aufgabenblatt : 5
*Aufgabe : 5.2
**/

import java.util.ArrayList;

import math.MathUtil;
import math.Vec3;
import math.function.ArcLengthTrafoBuilder;
import math.function.FunctionR1R1;
import math.function.FunctionR1Vec3;
import math.function.FunctionR1Vec3Util;
import math.function.util.SinFunc;
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

public class TestArc {
	
	public static boolean exit = false;
	private Group world;
    private final Sphere obj;
    private final PerspectiveCamera camera;
	private final PointLight light;
	private final AbstRenderer renderer;
	private final ArrayList<AbstController> controllers;

	
	/**
	 * @param width The horizontal window size in pixels
	 * @param height The vertical window size in pixels
	 */
	public TestArc(int width, int height) 
	{
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Tempokontrolle auf einer Kurve");
		
		world = new Group ("Komplette Szene");
		camera = new PerspectiveCamera(0, 0, 30);
		camera.setFov(50f);
		camera.setZRange(5f, 1000f);
		camera.focus (new Vec3(0.0f, 5.0f, 0.0f));
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
		
		obj = new Sphere();;
		obj.setTranslation(0.0f, 0.0f, 0.0f);
		world.attachChild(obj);
		
		// Create sinus curve to simulate a mountain. 
		float freq = 1.0f/MathUtil.PI;
		float max = 1.0f/freq*MathUtil.PI;
		SinFunc curve = new SinFunc(10,freq,0);
		curve.setTMin(0);
		curve.setTMax(max);
		
		//Parametrisieren nach der Bogenlänge
		ArcLengthTrafoBuilder altf = new ArcLengthTrafoBuilder(curve, 100, 0, max/2);
		
		ArcLengthTrafoBuilder altf2 = new ArcLengthTrafoBuilder(curve, 100, max/2, max);
		
		
		FunctionR1Vec3 func = FunctionR1Vec3Util.compose(altf2.getArcLengthParamCurve(), new FunctionR1R1(0, max) {
			
			@Override
			public float eval(float t) {
				float temp;
				
				temp = 3*t;
				
				return temp;
			}
		});
		
		
		
		
		// create controller to move sphere along the sinus curve
		controllers.add( new FunctionR1Vec3Controller(AbstController.RepeatType.CLAMP, 
				obj.getChannel("Translation"), func));
		
		
		world = VisualHelp.makeGrid(world, 10);
		world = VisualHelp.makePath(world, func);
		//world = VisualHelp.makePath(world, altf.getArcLengthParamCurve());
		//world = VisualHelp.makePath(world, altf2.getArcLengthParamCurve());
		
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
							
		TestArc demo   = new TestArc( width, height );
		demo.gameLoop();
	}
}