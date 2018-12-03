/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package Aufgabenblatt_7.Asbach_Dustin;

import java.util.ArrayList;

import math.Vec3;
import math.function.FunctionR1Vec3;
import math.function.FunctionR1Vec3Util;
import math.function.util.BezierCurve;
import math.function.util.PiecewiseLinear;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.AbstLight;
import scenegraph.AmbientLight;
import scenegraph.Color;
import scenegraph.Group;
import scenegraph.PerspectiveCamera;
import scenegraph.util.Loader;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;
import animation.FunctionR1Vec3Controller;

public class TestBezierKubisch
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
	public TestBezierKubisch(int width, int height)
	{
		renderer = new Ogl3Renderer(width, height);
		renderer.initState();
		renderer.setTitle("Aufgabenblatt 7");
		renderer.setBackgroundColor (Color.skyBlue());

		// generate root element of the world
		world = new Group("Mesh");

		camera = new PerspectiveCamera(0, 20, 0);
		camera.setZRange(0.1f, 2000f);
		world.attachChild(camera);
		renderer.setCamera(camera);

		light = new AmbientLight("Lichtquelle");
		light.setDiffuse (new Vec3(1f, 1f, 1f));
		world.attachChild(light);

		
		// load mesh object
		Group meshes = Loader.loadMesh("town.obj");
		world.attachChild (meshes);
		
		Group bird = Loader.loadMesh("bird.obj");
		bird.setScale(0.1f, 0.1f, 0.1f);
		bird.setTranslation(-6f,0, -2f);
		world.attachChild (bird);		
		
		world = VisualHelp.makeGrid(world, 7);
		
		
		controllers = new ArrayList<AbstController>();
		controllers.add(new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp()));
		
		
		
		// Strecke
		
		// s1
			Vec3 vec_points1[] = new Vec3[4];
			vec_points1[0] = new Vec3(-6, 0.01f, -2);
			vec_points1[1] = new Vec3(2.5f, 0.01f, -2.2f);
			vec_points1[2] = new Vec3(2.5f, 0.01f, -2.2f);
			vec_points1[3] = new Vec3(2.5f, 0.01f, 0);
			
			BezierCurve s1 = new BezierCurve(vec_points1);

	
		// s2
			Vec3 vec_points2[] = new Vec3[4];
			vec_points2[0] = new Vec3(2.5f, 0.01f, 0);
			vec_points2[1] = new Vec3(2.5f, 0.01f, 4.4f);
			vec_points2[2] = new Vec3(-3f, 0.01f, -1f);
			vec_points2[3] = new Vec3(-2.5f, 0.01f, 6);
			
			BezierCurve s2 = new BezierCurve(vec_points2);
	
			
				
								
		// Zusammen setzen
			
			float i = 1; // verzögerung beim start
			
			// shift s1 in time to interval [0,3]
			float[] t1 = { 0.0f, 1.0f };
			float[] grid1 = { 0.0f+i, 4.0f+i };
			PiecewiseLinear trafo1 = new PiecewiseLinear(t1, grid1);
			FunctionR1Vec3 s1_prime = FunctionR1Vec3Util.compose(s1, trafo1);
			
			// shift s2 in time to interval [3,6]
			float[] t2 = { 0.0f, 1.0f };
			float[] grid2 = { 4.0f+i, 8.0f+i };
			PiecewiseLinear trafo2 = new PiecewiseLinear(t2, grid2);
			FunctionR1Vec3 s2_prime = FunctionR1Vec3Util.compose(s2, trafo2);
			
			
			
			
		
		world = VisualHelp.makePath(world, FunctionR1Vec3Util.connect(s1_prime,s2_prime));
		
		
		controllers.add( new FunctionR1Vec3Controller(AbstController.RepeatType.CLAMP, bird.getChannel("Translation"), FunctionR1Vec3Util.connect(s1_prime,s2_prime)));
		
			

		
		
		
		/*vec_points1[0] = new Vec3(-6, 0.01f, -2);
		vec_points1[1] = new Vec3(0, 0.01f, -2);
		vec_points1[2] = new Vec3(2.5f, 0.01f, 0);
		vec_points1[3] = new Vec3(0, 0.01f, 2);
		vec_points1[4] = new Vec3(-2.5f, 0.01f, 6);
		*/
		
		
		
		
	

		
		
		
	}
	
	
	/**
	 * @param time The time in seconds since start of the animation
	 */
	public void update( float time ) {
		boolean update = false;
		for (int i = 0; i < controllers.size(); i++) {
			if (controllers.get(i).update(time))
				update = true; 
		}
		if (update)
		{
			camera.updateTransform();
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
		int height = 800;
							
		TestBezierKubisch demo   = new TestBezierKubisch( width, height );
		demo.gameLoop();
	}
	
}
