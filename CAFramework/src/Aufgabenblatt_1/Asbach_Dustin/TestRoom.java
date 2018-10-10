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
import scenegraph.MaterialState;
import scenegraph.PerspectiveCamera;
import scenegraph.PointLight;
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
	
	/**
	 * Gruppen für Elemente
	 */
	private Group Sideboard = new Group("sideboard");
	private Group Table = new Group("table");
	private Group Chair = new Group("chair");
	private Group Lamp = new Group("lamp");
	private Group Cup = new Group("cup");
	private Group Plant = new Group("plant");
	private Group figure = new Group ("figur");
	
	
	
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
		
		//Punktuelles Licht
			PointLight light2 = new PointLight("Punktlicht");
			light2.setTranslation (7f,7f, 5f);
			light2.setDiffuse (new Vec3(0.8f,0.8f,0.8f));
			world.attachChild(light2);
		
		
		//Sideboard 
			// set the color of all following elements
			ColorState col_Sideboard = new ColorState (Color.grey());
			world.attachChild(col_Sideboard);
			
			
			
			
			// generate a cube and attach it to the scene
			Cube cube_Sideboard = new Cube();
			cube_Sideboard.setSize(4);
			cube_Sideboard.setTranslation(10, 2, 3);
			cube_Sideboard.setScale(0.8f, 0.8f, 3);
			
			Sideboard.attachChild(cube_Sideboard);
			
			world.attachChild(Sideboard);
			
		
			
	    //Tisch
			// set the color of all following elements
			MaterialState mat = new MaterialState ();
			world.attachChild(mat);
			
			// board
			Cube board = new Cube();
			board.setScale(6.0f, 0.3f, 14f);
			
			
			//board.setTranslation(0, 6f, 0);
			
			//board.setTranslation(-7.5f, 6f, 7);
			
			
			
			// leg 1 VR			
			Cube cube_Table_leg1 = new Cube();
			cube_Table_leg1.setScale(0.5f, 5f, 0.5f);
			cube_Table_leg1.setTranslation(2.65f, -2.5f, 6.65f);
			
			// leg VL			
			Cube cube_Table_leg2 = new Cube();
			cube_Table_leg2.setScale(0.5f, 5f, 0.5f);
			cube_Table_leg2.setTranslation(-2.65f, -2.5f, 6.65f);
			
			// leg HR			
			Cube cube_Table_leg3 = new Cube();
			cube_Table_leg3.setScale(0.5f, 5f, 0.5f);
			cube_Table_leg3.setTranslation(2.65f, -2.5f, -6.65f);
			
			// leg HL			
			Cube cube_Table_leg4 = new Cube();
			cube_Table_leg4.setScale(0.5f, 5f, 0.5f);
			cube_Table_leg4.setTranslation(-2.65f, -2.5f, -6.65f);
			
	
			Table.attachChild(cube_Table_leg1);
			Table.attachChild(cube_Table_leg2);
			Table.attachChild(cube_Table_leg3);
			Table.attachChild(cube_Table_leg4);
			Table.attachChild(board);
			
			
		    
		    Table.setTranslation(-9f, 5.15f, 0);
		    //Table.setScale(0.9f, 0.9f, 0.9f);
		    
		   
			
			// Element to World
			world.attachChild(Table);
			
			
			
			
		// Lampe
			Lamp = Loader.loadMesh("lamp.obj");
			Lamp.setScale(0.15f, 0.15f, 0.15f);
			Lamp.setTranslation(-11, 5.3f, -5);
			Lamp.setRotation(0, 3, 0);
			world.attachChild(Lamp);
			
			
		// Kaffee Tasse
			Cup = Loader.loadMesh("kaffeetasse.obj");
			Cup.setScale(0.15f, 0.15f, 0.15f);
			Cup.setTranslation(-11, 5.3f, 3);
			world.attachChild(Cup);
			
		// Pflanze
			Plant = Loader.loadMesh("plant.obj");
			Plant.setTranslation(7, 1, -3);
			Plant.setScale(0.07f, 0.07f, 0.07f);
			world.attachChild(Plant);
			
		// Stuhl
			Chair = Loader.loadMesh("officeChair.obj");
			Chair.setScale(0.2f, 0.2f, 0.2f);
			Chair.setTranslation(-2.5f, 0f, 2);
			Chair.setRotation(0, -0.50f, 0);
			world.attachChild(Chair);
			
	    // Figur
			figure = Loader.loadMesh("anna_faces.obj");
			figure.setScale(0.2f, 0.2f, 0.2f);
			figure.setTranslation(9.4f, 3.55f, 2);
			figure.setRotation(0, -0.35f, 0);
			world.attachChild(figure);

	
			
			
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
