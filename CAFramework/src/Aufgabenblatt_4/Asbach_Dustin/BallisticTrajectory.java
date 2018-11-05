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
*Aufgabenblatt : 3
*Aufgabe : 3.2
**/

package Aufgabenblatt_4.Asbach_Dustin;

import java.time.Clock;
import java.util.ArrayList;
import math.Vec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.AbstLight;
import scenegraph.AbstSpatial;
import scenegraph.AmbientLight;
import scenegraph.Color;
import scenegraph.ColorState;
import scenegraph.Cube;
import scenegraph.Cylinder;
import scenegraph.Group;
import scenegraph.Line;
import scenegraph.MaterialState;
import scenegraph.PerspectiveCamera;
import scenegraph.PointLight;
import scenegraph.Sphere;
import scenegraph.util.Loader;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;
import examples.controller.CirclePosController;
import examples.scenes.TestCube;

public class BallisticTrajectory
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
	 * Gruppen für Elemente
	 */
	private float pi = 3.14159265359f;
	
	//private Group sun_sys = new Group("sun_sys");
	
	//private CirclePosController CSC_mond;


	
	/**
	 * Test class for importing mesh into the world 
	 */
	public BallisticTrajectory(int width, int height)
	{

		renderer = new Ogl3Renderer(width, height);
		renderer.initState();
		renderer.setTitle("Mesh laden");
		renderer.setBackgroundColor (Color.skyBlue());

		// generate root element of the world
	    world = new Group("Mesh");

	    
		camera = new PerspectiveCamera(0, 30, 50);
	    //camera = new PerspectiveCamera(0, 5, 0);
	    
	    camera.focus(new Vec3(0,8,0));
	    //camera.focus(new Vec3(0,0,0));
		camera.setZRange(1f, 100f);
		world.attachChild(camera);
		renderer.setCamera(camera);

		light = new AmbientLight("Lichtquelle");
		light.setDiffuse (new Vec3(0.5f, 0.5f, 0.5f));
		world.attachChild(light);
		
		//Kugel
		//Sphere kugel = new Sphere();
		//kugel.setRadius(1f);
			
		
		//bal_ctl = new BallisticController("test", new Vec3(6, 10, 3), new Vec3(-20f, 10f, -0f), new Vec3(-0f, -30f, -0f), 
		//									kugel.getChannel(AbstSpatial.TRANSLATION));
		
		
		// Cam controller
		controllers = new ArrayList<AbstController>();
				
		Group kugeln = new Group("Kugeln");
		//farbe auf Rot stellen
		ColorState col_clock_ring = new ColorState (Color.green());
		kugeln.attachChild(col_clock_ring);
		
		
		
		ArrayList<BallisticController> bal_ctl = new ArrayList<BallisticController>() ;  
		
		for(int i=0;i<1000;i++) {
			Sphere kugel = new Sphere(1f);
			kugeln.attachChild(kugel);
			
			
			// Ballistic Controller  STARTPUNKT --- Startgeschwindigkeit --- beschleunigung
			bal_ctl.add(new BallisticController("test", new Vec3(0, 25, 0), new Vec3((40*((float)Math.random()-0.5f)), 10f, (((float)Math.random()-0.5f))*40), new Vec3(-0f, -18f, -0f), 
					kugel.getChannel(AbstSpatial.TRANSLATION)));
			
			//bal_ctl.get(i).setRate((float)Math.random()+1);

			controllers.add(bal_ctl.get(i));
								
		}
		
		controllers.add(new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp())); 
		
		//VisualHelp.makeTimeGrid(world, 20, 10);
		//VisualHelp.markTimeOnGrid(10f);
		world.attachChild(kugeln);
		world = VisualHelp.makeGrid(world, 30);
		

	}
	
	
	/**
	 * @param time The time in seconds since start of the animation
	 */
	public void update( float time ) {
		boolean updated = false;
		for (int i = 0; i < controllers.size(); i++) 
		{
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
		//final int FPS = 25;					// frames per second				//modifiziert !
		final int deltaTime = 1000/20;		// delta time 20 milliseconds			//modifiziert !
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
				//modifiziert start!
				sleepTime = deltaTime - sleepTime % deltaTime;
				try {
					Thread.sleep(sleepTime);
					
				}catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//modifiziert ende!
				
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
		int width  = 1200;
		int height = 800;
							
		BallisticTrajectory demo   = new BallisticTrajectory( width, height );
		demo.gameLoop();
	}
	
}
