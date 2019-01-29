/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package Aufgabenbaltt_13.Asbach_Dustin;

import java.util.ArrayList;

import math.Vec3;
import particles.EulerMethod;
import particles.FloorRepeller;
import particles.Force;
import particles.Gravity;
import particles.ParticleController;
import particles.Viscous;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.AbstLight;
import scenegraph.AmbientLight;
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

public class TestWaterfall {
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
	public TestWaterfall(int width, int height)  {
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Aufgabenblatt 12");
		renderer.setBackgroundColor (Color.white());
		
		// generate root element of the scene
		world = new Group ("Waterfall");
		
		// generate camera and attach it to the scene
		camera = new PerspectiveCamera(0, 5, 60);
		camera.setZRange(camera.getZNear(),500);
		world.attachChild(camera);
		
		// Create list with all controllers
		controllers = new ArrayList<AbstController>();
		CameraController camctrl = new CameraController(camera.getChannel(AbstCamera.TRANSLATION), camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp());
		controllers.add(camctrl);
		
		light = new AmbientLight("Lichtquelle");
		light.setDiffuse(new Vec3(1f,1f,1f));
		world.attachChild(light);
		
		light = new PointLight("Lichtquelle");
		light.setTranslation (0,2,10);
		world.attachChild(light);
			
		float stoneRadius = 2;
		
		// load mesh object
		Group stoneObj = Loader.loadMesh("rock.obj");
		stoneObj.setScale (stoneRadius, stoneRadius, stoneRadius);
		stoneObj.setTranslation (0, 0.5f, 3);
		world.attachChild (stoneObj);
		
		// create ParticleGroup for water particles and attach to scene
		world.attachChild(new ColorState(Color.lightBlue()));
		
		ParticleGroup pg = new ParticleGroup();
		world.attachChild(pg);
		
		// Create forces 			
		ArrayList<Force> forces = new ArrayList<Force>();
		
		// generate forces and add them to the array
		forces.add(new Gravity(new Vec3(0, -9.81f, 0)));
		forces.add(new Viscous(1));
		forces.add(new FloorRepeller(new Vec3(0, 1, 0), new Vec3(0, 0, 0), 50, 0.7f));
		forces.add(new steinRepeller(new Vec3(0, 1, 2), 4, 5, 0.001f));
		
		// generate emitter for water particles
		LineEmitter le = new LineEmitter(10000, 5, new Vec3(-5,20,0), new Vec3(5,20,0));
		le.setSpeedInterval(2, 5);
		le.setVelocityCone(new Vec3(0, 0, -1), 1);
		
		// generate HeunMethod or EulerMethod
		EulerMethod em = new EulerMethod(4);
		
		// create and configure Controller to generate and move particles
		ParticleController pc = new ParticleController(pg, le, forces, em);
		controllers.add(pc);
		
		// create floor grid for visualization
		world = VisualHelp.makeGrid(world, 10, 2);
		
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
							
		TestWaterfall demo  = new TestWaterfall( width, height );
		demo.gameLoop();
	}
}
