/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package examples.scenes;

import java.util.ArrayList;

import math.MathUtil;
import math.Vec3;
import math.function.ArcLengthTrafoBuilder;
import math.function.FunctionR1Vec3;
import math.function.util.BezierCurve;
import particles.Force;
import particles.Gravity;
import particles.HeunMethod;
import particles.ParticleController;
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
		
		// create particleGroup for fuze particles
		// create and configure emitter for fuze particles
		// create HeunMethod or EulerMethod
		// create and configure Controller to move initial position of PointEmitter
		// create Controller to animate particles 

		// create particleGroup for explosion particles
		// create and configure emitter for explosion particles
		// create and configure Controller to animate particles 
		
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
