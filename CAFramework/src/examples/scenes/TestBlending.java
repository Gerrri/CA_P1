/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package examples.scenes;

import java.util.ArrayList;

import animation.AbstController;
import animation.CameraController;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.*;
import util.OBJContainer;
import util.OBJGroup;
import util.OBJMaterial;

public class TestBlending
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
	private final ArrayList<AbstController> controllers;
	private AbstLight light;
	
	
	/**
	 * Constructs a scene with a simple shape, which is deformed over time
	 */
	public TestBlending(int width, int height) {

		// generate and initialize Renderer
		renderer = new Ogl3Renderer(width, height);
		renderer.initState();
		renderer.setTitle("Aufgabenblatt 10");

		// generate root element of the scene
		world = new Group("Formen ueberblenden");

		camera = new PerspectiveCamera(0, 2, 5);
		camera.setZRange(1f, 100);
		world.attachChild(camera);
		renderer.setCamera(camera);

		light = new PointLight("Licht");
		light.setTranslation(0, 5, 10);
		world.attachChild(light);

		// load .obj (containing multiple groups of vertices, faces, normals and
		// material)
		OBJContainer obj = OBJContainer.loadFile("formen.obj");
		// get one of the groups by its name
		OBJGroup baseShape = obj.getGroup("zylinder");
		// ...and use it as a base shape for the blending mesh
		StaticMesh base = new StaticMesh(baseShape);
		// now define the target array, which in this case consists only of one
		// target shape
		int nTargets = 1;
		StaticMesh[] targets = new StaticMesh[nTargets];
		// get the target group
		targets[0] = new StaticMesh(obj.getGroup("krug"));
		// create a blending mesh by defining the base shape and the target shapes
		Blendshape blend = new Blendshape(base, nTargets, targets);
		// Create a triangle mesh with the blending mesh and indices of the base shape
		TriangleMesh tm = new TriangleMesh(blend, baseShape.getIndices());

		// load the material of the base shape and create a corresponding
		// MaterialState node.
		OBJMaterial mat = baseShape.getMaterial();
		MaterialState matState = new MaterialState();
		matState.setAmbient(mat.getAmbientColor());
		matState.setDiffuse(mat.getDiffuseColor());
		matState.setSpecular(mat.getSpecularColor());
		matState.setShininess(mat.getSpecular());
		
		// include MaterialState node and TriangleMesh into scene.
		world.attachChild(matState);
		world.attachChild(tm);

		controllers = new ArrayList<AbstController>();
		controllers.add((new CameraController(
				camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), 
				camera.getFocus(), camera.getUp())));

		// 1) create arrays for time grid and corresponding weights for the target
		// 2) define linear interpolation function for the grid and the weights
		// 3) use FunctionR1R1Controller to control the channel "WEIGHT_0" of the
		//    blendshape and use the chosen interpolation function
		// 4) add this controller to the list of controllers
		
	}
	
	
	/**
	 * @param time The time in seconds within the animation
	 */
	public void update( float time )
	{
		boolean updated = false;
		for (int i = 0; i < controllers.size(); i++) 
		{
			if (controllers.get(i).update(time))
				updated = true;
		}
		if (updated)
		{
			world.updateWorldData();
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
		int width  = 800;
		int height = 800;
							
		TestBlending demo   = new TestBlending( width, height );
		demo.gameLoop();
	}
}

