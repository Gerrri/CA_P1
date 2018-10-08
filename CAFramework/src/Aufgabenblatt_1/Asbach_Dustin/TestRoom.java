/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package Aufgabenblatt_1.Asbach_Dustin;

import java.util.ArrayList;

import math.Vec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.AbstLight;
import scenegraph.AmbientLight;
import scenegraph.Color;
import scenegraph.ColorState;
import scenegraph.Cube;
import scenegraph.Group;
import scenegraph.PerspectiveCamera;
import scenegraph.util.Loader;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;
import examples.scenes.TestCube;

public class TestRoom
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

	private AbstLight light;
	
	private Cube cube_Sideboard;
	private Cube cube_Table_board;
	
	/**
	 * Test class for importing mesh into the world 
	 */
	public TestRoom(int width, int height)
	{
		renderer = new Ogl3Renderer(width, height);
		renderer.initState();
		renderer.setTitle("Mesh laden");
		
		renderer.setBackgroundColor (Color.skyBlue());

		
		
		// generate root element of the world
	    world = new Group("Mesh");

		camera = new PerspectiveCamera(0, 10, 35);
		camera.focus(new Vec3(0,7,0));
		camera.setZRange(1f, 100f);
		world.attachChild(camera);
		renderer.setCamera(camera);

		light = new AmbientLight("Lichtquelle");
		light.setDiffuse (new Vec3(0.5f, 0.5f, 0.5f));
		world.attachChild(light);
		
		
		//CUBE !!
			// set the color of all following elements
			ColorState col_Sideboard = new ColorState (Color.grey());
			world.attachChild(col_Sideboard);
				
			// generate a cube and attach it to the scene
			cube_Sideboard = new Cube();
			cube_Sideboard.setSize(4);
			cube_Sideboard.setTranslation(10, 2, 0);
			cube_Sideboard.setScale(1, 1, 3);
			world.attachChild(cube_Sideboard);
			
	    //Tisch
			// set the color of all following elements
			ColorState col_Table_board = new ColorState (Color.black());
			world.attachChild(col_Table_board);
			
			// generate a cube and attach it to the scene
			cube_Table_board = new Cube();
			cube_Table_board.setScale(1.5f, 0.5f, 3);
			world.attachChild(cube_Table_board);
			
			
			
			
		
		
		
		// load mesh object
		Group meshes = Loader.loadMesh("zimmer.obj");
		world.attachChild (meshes);

		world = VisualHelp.makeGrid(world, 30);
		
		

		
		
		
	}
	
	
	/**
	 * @param time The time in seconds since start of the animation
	 */
	public void update( float time ) {
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
							
		TestRoom demo   = new TestRoom( width, height );
		demo.gameLoop();
	}
	
}
