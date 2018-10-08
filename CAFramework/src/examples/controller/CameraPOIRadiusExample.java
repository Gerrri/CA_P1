/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package examples.controller;

import java.util.ArrayList;

import math.Vec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.Channel;
import scenegraph.Color;
import scenegraph.ColorState;
import scenegraph.Cube;
import scenegraph.Group;
import scenegraph.PerspectiveCamera;
import scenegraph.util.VisualHelp;
import animation.AbstController;

public class CameraPOIRadiusExample {
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
	
	/**
	 * Constructor constructs all scene objects and orders them hierarchically.
	 * 
	 * @param width
	 *            The horizontal window size in pixels
	 * @param height
	 *            The vertical window size in pixels
	 */
	public CameraPOIRadiusExample(int width, int height)  {
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("CircleControllerExample");
		
		// generate root element of the scene
		world = new Group ("Cube on Circle");
		
		// generate camera and attach it to the scene
		camera = new PerspectiveCamera(2, 5, 10);
		world.attachChild(camera);		
			
		// set the color of all following elements
		ColorState col = new ColorState (Color.orange());
		world.attachChild( col);
	
		// generate a cube and attach it to the scene
		Cube obj = new Cube();
		world.attachChild(obj);
		
		// Create list with all controllers
		controllers = new ArrayList<AbstController>();
		
		// Get translation channel from obj
		Channel objTranslationChannel = obj.getChannel("Translation");
		
		// Create controller and connect channel with controller
		CirclePosRadiusController pctrl = new CirclePosRadiusController("Circle", objTranslationChannel);
		
		// Add controller to controller array
		controllers.add(pctrl);
		
		// Create connection between position of cube and POI of camera
		ConnectorAddVec3 cctrl = new ConnectorAddVec3("Connect", new Vec3(2.0f, 5.0f, 10.0f), obj.getChannel("Translation"), camera.getChannel("Translation"));
		controllers.add(cctrl);
		
		// Create input controller
		InputController ictrl = new InputController("Input", pctrl.getChannel("Radius"), 0.1f);
		controllers.add(ictrl);
		
		// create additional grid for visualization
		world = VisualHelp.makeGrid(world, 5);
		
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
		int width  = 800;
		int height = 800;
							
		CameraPOIRadiusExample demo   = new CameraPOIRadiusExample( width, height );
		demo.gameLoop();
	}
}
