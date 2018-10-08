/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package examples.scenes;

import math.Vec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.*;
import scenegraph.util.Loader;

public class TestScene
{
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
	 * Constructs a scene with a moving teapot between 4 pillars 
	 */
	public TestScene(int width, int height)
	{
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Teekannen Szene");
		
		world = new Group ("Teekanne zwischen Saeulen");
		
		camera = new PerspectiveCamera(1,4,6);
		camera.setFov(30f);
		camera.setZRange(0.5f, 100f);
		world.attachChild(camera);
		
		AmbientLight light = new AmbientLight("Raumlicht");
		light.setAmbient (new Vec3(0.1f, 0.1f, 0.1f));
		world.attachChild(light);
		
		PointLight light2 = new PointLight("Licht 2");
		light2.setTranslation (1f,5f, 5f);
		light2.setDiffuse (new Vec3(0.8f,0.8f,0.8f));
		world.attachChild(light2);
		
		Group group = new Group ("Scene");
		group.setScale (2,2,2);
		world.attachChild(group);
		
		MaterialState mat = new MaterialState ();
		mat.setAmbient (0.5f,0.2f,0.2f);
		mat.setDiffuse (0.9f,0.8f,0.1f);
		mat.setSpecular (1,1,1);
		mat.setShininess (100);
		group.attachChild(mat);
				
	    Cube floor = new Cube ("Bodenplatte");
	    floor.setScale (1, 0.025f, 1);
	    group.attachChild (floor);
	    
		Group teapot = Loader.loadMesh("teapot.obj");
		teapot.setScale(0.2f,0.2f,0.2f);
		group.attachChild (new ColorState(Color.white()));
		group.attachChild (teapot);
		
		Cube base = new Cube("Balken");
		base.setTranslation (0,0.2625f, 0);
		base.setScale(0.2f,0.5f,0.2f);
		
		Sphere kugel = new Sphere ("Kugel");
		kugel.setScale (0.1f, 0.1f, 0.1f);
		kugel.setTranslation (0,0.6f,0);
		
		Group pillar = new Group ("Saeule");
		pillar.attachChild(kugel);
		pillar.attachChild(base);
		
		Group pillarGroup = new Group("Saeule1");
	    pillarGroup.attachChild(new ColorState (Color.red()));
		pillarGroup.attachChild (pillar);
		pillarGroup.setTranslation(-0.4f, 0, -0.4f);
		group.attachChild(pillarGroup);
	
		Group pillarGroup2 = new Group("Saeule2");
		pillarGroup2.attachChild(new ColorState (Color.green()));
		pillarGroup2.attachChild (pillar);
		pillarGroup2.setTranslation(-0.4f, 0, 0.4f);
		group.attachChild(pillarGroup2);
		
		Group pillarGroup3 = new Group("Saeule3");
		pillarGroup3.attachChild(new ColorState (Color.blue()));
		pillarGroup3.attachChild (pillar);
		pillarGroup3.setTranslation(0.4f, 0, 0.4f);
		group.attachChild(pillarGroup3);
		
		Group pillarGroup4 = new Group("Saeule4");
		pillarGroup4.attachChild(new ColorState (Color.yellow()));
		pillarGroup4.attachChild (pillar);
		pillarGroup4.setTranslation(0.4f, 0, -0.4f);
		group.attachChild(pillarGroup4);
	    
		world.updateTransform();
		renderer.setCamera (camera);
	}
	
	
	/**
	 * @param time The time in seconds between the last two frames
	 */
	public void update( float time )
	{
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
		int width  = 800;
		int height = 800;
							
		TestScene demo   = new TestScene( width, height );
		demo.gameLoop();
	}
}
