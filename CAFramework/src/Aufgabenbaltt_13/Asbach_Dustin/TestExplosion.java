/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */
/**
*CA Winter 2018/19
*Name , Vorname : Asbach , Dustin
*Matrikelnummer : 11117108
*Aufgabenblatt : 13
*Aufgabe : 13.2 
**/

package Aufgabenbaltt_13.Asbach_Dustin;

import java.util.ArrayList;

import org.lwjgl.input.Controller;

import math.MathUtil;
import math.Vec3;
import math.function.ArcLengthTrafoBuilder;
import math.function.FunctionR1Vec3;
import math.function.util.BezierCurve;
import particles.Emitter;
import particles.EulerMethod;
import particles.Force;
import particles.Gravity;
import particles.HeunMethod;
import particles.Particle;
import particles.ParticleController;
import particles.ParticleEulerController;
import particles.ParticleSystem;
import particles.PointEmitter;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.AbstLight;
import scenegraph.AmbientLight;
import scenegraph.Channel;
import scenegraph.Color;
import scenegraph.ColorState;
import scenegraph.Group;
import scenegraph.ParticleGroup;
import scenegraph.PerspectiveCamera;
import scenegraph.PointLight;
import scenegraph.util.Loader;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;
import animation.FunctionR1Vec3Controller;

public class TestExplosion {
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
	
	/**
	 * 
	 */
	private final ArrayList<AbstController> controllers;
	
	private AbstLight light;
	
	/**
	 * Constructor constructs all scene objects and orders them hierarchically.
	 * 
	 * @param width
	 *            The horizontal window size in pixels
	 * @param height
	 *            The vertical window size in pixels
	 */
	public TestExplosion(int width, int height)  {
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Aufgabenblatt 12");
		
		// generate root element of the scene
		world = new Group ("Explosion");
		
		// generate camera and attach it to the scene
		camera = new PerspectiveCamera(0, 10, 30);
		camera.setZRange(camera.getZNear(),500);
		world.attachChild(camera);
		
		// Create list with all controllers
		controllers = new ArrayList<AbstController>();
				
		// Create Camera controller
		CameraController camctrl = new CameraController(camera.getChannel(AbstCamera.TRANSLATION), camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp());
		controllers.add(camctrl);
		
		light = new AmbientLight("Lichtquelle");
		light.setDiffuse(new Vec3(1f,1f,1f));
		world.attachChild(light);
		
		light = new PointLight("Lichtquelle");
		light.setTranslation (0,2,10);
		world.attachChild(light);
		
		// Create Bezier curve for fuze and parameterize to arc length to get constant speed
				
		Vec3 p1 = new Vec3 (-10, 0, 0);
		Vec3 p2 = new Vec3 (-3, 0, -4);
		Vec3 p3 = new Vec3 (5, 0, 3);
		Vec3 p4 = new Vec3 (10, 0, 0);
		BezierCurve fuze = new BezierCurve(new Vec3[] { p1, p2, p3, p4 });
		ArcLengthTrafoBuilder trafoStretch = new ArcLengthTrafoBuilder(fuze, 100, 0.0f, 1f);
		FunctionR1Vec3 fuzeFunc = trafoStretch.getArcLengthParamCurve();
		float fuzeLength = trafoStretch.getArcLength();
		
		// load mesh object
		Group zuender = Loader.loadMesh("xball.obj");
		zuender.setScale (0.4f, 0.4f, 0.4f);
		zuender.setTranslation (p4);
		world.attachChild (zuender);
		
		ArrayList<Particle> particles = new ArrayList<Particle>();
		ArrayList<Force> forces = new ArrayList<Force>();
			
		//Fuze
			// create particleGroup for fuze particles
			ParticleGroup pg_fuze = new ParticleGroup();
			
			// create and configure emitter for fuze particles
			PointEmitter e_fuze = new PointEmitter(100,0.1f,p1);
			e_fuze.setSpeedInterval(10, 15);
			e_fuze.setVelocityCone(new Vec3(-1, 0, 0), 0.7f);
			e_fuze.setTermination(fuzeLength);
			
			// create HeunMethod or EulerMethod
			EulerMethod em = new EulerMethod(4);
			
			
			// create and configure Controller to move initial position of PointEmitter
			FunctionR1Vec3Controller fc_fuze = new FunctionR1Vec3Controller(AbstController.RepeatType.CLAMP, e_fuze.getChannel(PointEmitter.SOURCE), fuzeFunc);
			controllers.add(fc_fuze);
			
			// create Controller to animate particles 
			ParticleController pc_fuze = new ParticleController(pg_fuze, e_fuze, null, em);
			controllers.add(pc_fuze);
			
			
		
		//Explosion
			// create particleGroup for explosion particles
			ParticleGroup pg_explosion = new ParticleGroup();
			
			// create and configure emitter for explosion particles
			PointEmitter e_explosion = new PointEmitter(50000, 0.7f, p4);
			e_explosion.setSpeedInterval(10, 15);
			e_explosion.setTermination(0.2f);
			
			// create and configure Controller to animate particles 
			ParticleController pc_explosion = new ParticleController(pg_explosion, e_explosion, null, em);
			pc_explosion.setGlobalStartTime(fuzeLength);
			controllers.add(pc_explosion);

			
			
		//attach to world
		world.attachChild(pg_explosion);
		world.attachChild(pg_fuze);
		
		// create additional grid for visualization
		world = VisualHelp.makeGrid(world, 10, 2);
		
		// vizualize path
		world = VisualHelp.makePath(world, fuzeFunc);
		
		// calculate transformation matrices 
		world.updateTransform();
		
		// set the viewport for the scene
		renderer.setCamera (camera);
	}
	
	/**
	 * @param time TThe time in seconds since start of the animation (global time)
	 */
	public void update( float time ) {
		boolean isUpdated = false;
		for (int i = 0; i < controllers.size(); i++) {
			if (controllers.get(i).update(time)) {
				isUpdated = true;
			}	
		}
		
		if (isUpdated) {
			world.updateTransform();
			renderer.setCamera (camera);
		}
	}
	
	/**
	 * Gameloop
	 */
	private  void gameLoop() {
		final int FPS = 60;					// frames per second
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
			
	public void draw() {
		renderer.clearBuffers();
		world.draw(renderer);
		renderer.displayBackBuffer();
	}
	
	public boolean closeRequested() {
		return renderer.closeRequested();
	}
	
	public void cleanup() {
		renderer.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int width  = 1400;
		int height = 1000;
							
		TestExplosion demo  = new TestExplosion( width, height );
		demo.gameLoop();
	}
}
