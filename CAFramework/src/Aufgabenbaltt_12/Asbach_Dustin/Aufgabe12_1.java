/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
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
*Aufgabenblatt : 12
*Aufgabe : 12.1 
**/

package Aufgabenbaltt_12.Asbach_Dustin;

import java.util.ArrayList;

import math.Vec3;
import animation.AbstController;
import animation.CameraController;
import animation.JointController;
import animation.TimeSliderController;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.*;
import scenegraph.util.VisualHelp;
import scenegraph.util.BVHClip;
import scenegraph.util.Loader;

public class Aufgabe12_1
{
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
	public Aufgabe12_1(int width, int height)
	{
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Skelett Szene");
		
		// generate root element of the world
		world = new Group ("Bewegtes Skelett");

		camera = new PerspectiveCamera (150,50,120);
		camera.setFov(80f);
		camera.setZRange(1, 1000);
		world.attachChild(camera);

	
		light = new AmbientLight("Lichtquelle 1");
		light.setDiffuse (new Vec3(1, 1, 1));
		world.attachChild(light);
		
		controllers = new ArrayList<AbstController>();
		controllers.add(new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp())); 
		
		//walking clip
		BVHClip clip_walking = Loader.loadBVH ("gerri_walking.bvh");
		clip_walking.setRepeatType(AbstController.RepeatType.CLAMP);

		if (clip_walking != null)
		{
			clip_walking.extract(1, 5);
			
			Group skeleton_walking = clip_walking.getSkeleton();
			skeleton_walking.setScale (0.5f, 0.5f, 0.5f);
			skeleton_walking.setTranslation(10, 0, 0);
			world.attachChild (skeleton_walking);
			
			ArrayList<JointController> jointController_walking = clip_walking.getControllers();
			controllers.addAll(jointController_walking);
			controllers.add(new TimeSliderController(jointController_walking, 400, 15, 5));
		}
		
		
		//crouching clip
		BVHClip clip_crouching = Loader.loadBVH ("gerri_crouched_walking.bvh");
		clip_crouching.setRepeatType(AbstController.RepeatType.CLAMP);

		if (clip_crouching != null)
		{
			clip_crouching.extract(5, 10);
			clip_crouching.schedule(4.5f);
			
			
			Group skeleton_crouching = clip_crouching.getSkeleton();
			skeleton_crouching.setScale (0.5f, 0.5f, 0.5f);
			skeleton_crouching.setTranslation(10, 0, 0);
			//world.attachChild (skeleton_crouching);
			
			ArrayList<JointController> jointController_crouching = clip_crouching.getControllers();
			controllers.addAll(jointController_crouching);
			//controllers.add(new TimeSliderController(jointController_crouching, 400, 15, 5));
		}
		
		
		
		// kombinieren walking -> crouche
		
		clip_walking.combine(clip_crouching);
		
		world = VisualHelp.makeGrid (world, 10, 20);
		renderer.setCamera (camera);
	}
	
	
	/**
	 * @param time The time in seconds since start of the animation
	 */
	public void update(float time)
	{
		if (controllers != null)
		{
			for (int i = 0; i < controllers.size(); i++)
			{
				if (controllers.get(i).update(time))
				{
					world.updateTransform();
				}
			}
		}
		renderer.setCamera(camera);
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
							
		Aufgabe12_1 demo   = new Aufgabe12_1( width, height );
		demo.gameLoop();
	}
	
}
