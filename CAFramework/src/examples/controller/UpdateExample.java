/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */


package examples.controller;

import java.util.ArrayList;

import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.Group;
import animation.AbstController;

public class UpdateExample {
	/**
	 * flag is set, when program shall be terminated
	 */
	public static boolean exit = false;		

	/**
	 * root of the scene graph
	 */
	private Group world;				
	
    
	/**
	 * render engine for the scene 
	 */
	private final AbstRenderer renderer;
	
	/**
	 * 
	 */
	private final ArrayList<AbstController> controllers;
	
	/**
	 * 
	 * @param width
	 * @param height
	 */
	UpdateExample(int width, int height) {
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Elementary Controller Example");
		
		// generate root element of the scene
		world = new Group ("Dumy group");
		
		// Create list with all controllers
		controllers = new ArrayList<AbstController>();
		
		ElementaryController ectrl = new ElementaryController("Elementary");
		controllers.add(ectrl);
		
		SimpleCircleController cctrl = new SimpleCircleController("Circle", 1.0f);
		controllers.add(cctrl);

		// calculate transformation matrices 
		world.updateTransform();
	}

	/**
	 * @param time The time in seconds since start of the animation
	 */
	public void update( float time ) {		
		for (int i = 0; i < controllers.size(); i++) {
			controllers.get(i).update(time);
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
		int width  = 500;
		int height = 600;
							
		UpdateExample demo   = new UpdateExample( width, height );
		demo.gameLoop();
	}
}
