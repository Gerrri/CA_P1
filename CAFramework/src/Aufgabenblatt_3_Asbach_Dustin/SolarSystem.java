/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package Aufgabenblatt_3_Asbach_Dustin;

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

public class SolarSystem
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
	
	private Group sun_sys = new Group("sun_sys");
	private Group mer_sys = new Group("mer_sys");
	private Group erd_sys = new Group("erd_sys");
	private Group mon_sys = new Group("mon_sys");
	private Group uni_sys = new Group("uni_sys");
	
	private CirclePosController CSC_mond;
	private CirclePosController CSC_erde;
	private CirclePosController CSC_merkur;
	
	
	/**
	 * Test class for importing mesh into the world 
	 */
	public SolarSystem(int width, int height)
	{
		renderer = new Ogl3Renderer(width, height);
		renderer.initState();
		renderer.setTitle("Mesh laden");
		renderer.setBackgroundColor (Color.skyBlue());

		// generate root element of the world
	    world = new Group("Mesh");

	    
		camera = new PerspectiveCamera(0, 7, 20);
	    //camera = new PerspectiveCamera(0, 5, 0);
	    
	    camera.focus(new Vec3(0,2,0));
	    //camera.focus(new Vec3(0,0,0));
		camera.setZRange(1f, 100f);
		world.attachChild(camera);
		renderer.setCamera(camera);
		

		light = new AmbientLight("Lichtquelle");
		light.setDiffuse (new Vec3(0.5f, 0.5f, 0.5f));
		world.attachChild(light);
		
		//Sonne
			ColorState gelb = new ColorState (Color.yellow());
			sun_sys.attachChild(gelb);
			
			Sphere sonne = new Sphere();
			sonne.setRadius(1.2f);		
			
			sun_sys.setRotation(pi/4, 0, 0);
			
			sun_sys.attachChild(sonne);
			
			uni_sys.attachChild(sun_sys);
			
		
		
		//Merkur
			ColorState rot = new ColorState (Color.red());
			mer_sys.attachChild(rot);
			
			Sphere merkur = new Sphere();
			merkur.setRadius(0.8f);
			
			mer_sys.attachChild(merkur);
			mer_sys.setRotation(pi/2, 0, 0);
			
			//drehen
			CSC_merkur = new CirclePosController("ctl_merk",5f,merkur.getChannel(AbstSpatial.TRANSLATION));
			
			uni_sys.attachChild(mer_sys);
			
		//Mond
			ColorState gruen = new ColorState (Color.green());
			mon_sys.attachChild(gruen);
			
			Sphere mond = new Sphere();
			mond.setRadius(0.5f);		
			mon_sys.attachChild(mond);
			mon_sys.setRotation(0, pi/6, 0);
			
			erd_sys.attachChild(mon_sys);
			
			
			//drehen
			CSC_mond = new CirclePosController("ctl_mond",3f,mond.getChannel(AbstSpatial.TRANSLATION));
			CSC_mond.setRate(3);
			
			
			
		//Erde
			ColorState blau = new ColorState (Color.blue());
			erd_sys.attachChild(blau);
			
			Sphere erde = new Sphere();
			erde.setRadius(1.0f);
			
			erd_sys.attachChild(erde);
			
			
			//drehen
			CSC_erde = new CirclePosController("ctl_erd",10f,erd_sys.getChannel(AbstSpatial.TRANSLATION));
						
			sun_sys.attachChild(erd_sys);
		
		
		
		// Cam controller
		controllers = new ArrayList<AbstController>();
		controllers.add(CSC_merkur);
		controllers.add(CSC_erde);
		controllers.add(CSC_mond);
		controllers.add(new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp())); 
		

		world.attachChild(uni_sys);
		//world.attachChild(mer_sys);
		//world.attachChild(erd_sys);

		
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
		int width  = 1200;
		int height = 800;
							
		SolarSystem demo   = new SolarSystem( width, height );
		demo.gameLoop();
	}
	
}
