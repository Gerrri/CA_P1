/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package Aufgabenblatt_10.Asbach_Dustin;

/**
*CA Winter 2018/19
*Name,  Vorname :  Asbach, Dustin
*Matrikelnummer :  11117108
*Aufgabenblatt : 10
*Aufgabe : 10.3
**/

import java.util.ArrayList;

import animation.AbstController;
import animation.CameraController;
import animation.SliderController;
import math.Vec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.*;
import scenegraph.util.Slider;
import scenegraph.util.VisualHelp;
import util.OBJContainer;
import util.OBJGroup;
import util.OBJMaterial;
import util.Vec3Array;

public class FaceBlending
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
	private AbstLight light;
	private final ArrayList<AbstController> controllers;
	
	/**
	 * Constructs a scene with a simple 2D triangle, which is deformed over time
	 */
	public FaceBlending(int width, int height)
	{
		
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Aufgabenblatt 10");
		renderer.setBackgroundColor (Color.white());
		
		// generate root element of the scene
		world = new Group ("Face Blendshapes");

		
		camera = new PerspectiveCamera (0,16,5);
		camera.focus (new Vec3(0,16,0));
		camera.setZRange(1f, 100f);
		world.attachChild(camera);
		renderer.setCamera (camera);

		light = new PointLight("Lichtquelle 1");
		light.setTranslation (0,20,10);
		light.setDiffuse(new Vec3(1f,1f,1f));
		world.attachChild(light);
		
		light = new AmbientLight("Umgebungslicht");
		light.setDiffuse(new Vec3(0.1f,0.1f,0.1f));
		world.attachChild(light);
		
		// read mesh data
		OBJContainer obj = OBJContainer.loadFile("anna_faces.obj");	
		
		// get body mesh, material and texture
		OBJGroup bodyGroup = obj.getGroup("body");
		StaticMesh body = new StaticMesh (bodyGroup);
		TriangleMesh bodymesh = new TriangleMesh (body, bodyGroup.getIndices(), bodyGroup.getNormals(), bodyGroup.getTexCoords());

		OBJMaterial mat = bodyGroup.getMaterial();
		MaterialState matState = new MaterialState();
		matState.setAmbient(mat.getAmbientColor());
		matState.setDiffuse(mat.getDiffuseColor());
		matState.setSpecular(mat.getSpecularColor());
		world.attachChild (matState);

		Vec3Array texCoord = null;
		String diffuseMap = mat.getDiffuseTextureName();
		if (diffuseMap != null)
		{
			texCoord = bodyGroup.getTexCoords();
			TextureState texState = new TextureState(diffuseMap, TextureState.TexType.DIFFUSE);
			world.attachChild (texState);
		}
		world.attachChild (bodymesh);
		
		// get face mesh, material and texture
		OBJGroup baseGroup = obj.getGroup("neutral");
		StaticMesh base = new StaticMesh (baseGroup);
		
		mat = baseGroup.getMaterial();
		matState = new MaterialState();
		matState.setAmbient(mat.getAmbientColor());
		matState.setDiffuse(mat.getDiffuseColor());
		matState.setSpecular(mat.getSpecularColor());
		world.attachChild (matState);

		texCoord = null;
		diffuseMap = mat.getDiffuseTextureName();
		if (diffuseMap != null)
		{
			texCoord = baseGroup.getTexCoords();
			TextureState texState = new TextureState(diffuseMap, TextureState.TexType.DIFFUSE);
			world.attachChild (texState);
		}
		
		
		// load the three target meshes "lachend", wuetend", "zwinkernd"
		OBJGroup lachendGroup = obj.getGroup("lachend");
		OBJGroup wuetendGroup = obj.getGroup("wuetend");
		OBJGroup zwinkerndGroup = obj.getGroup("zwinkernd");
		
		StaticMesh lachend = new StaticMesh (lachendGroup);
		StaticMesh wuetend = new StaticMesh (wuetendGroup);
		StaticMesh zwinkernd = new StaticMesh (zwinkerndGroup);
		

		
		
		// create blendshape with base mesh and target meshes	
		int nTargets = 3;
		StaticMesh[] targets = new StaticMesh[nTargets];
		targets[0] = lachend;
		targets[1] = wuetend;
		targets[2] = zwinkernd;
		
		Blendshape blend = new Blendshape(base, nTargets, targets);
		
		
		// create triangle mesh with blendshape and attach it to scene
		//!!!! replace base with blendshape !!!!!!!!!
		TriangleMesh tm = new TriangleMesh (blend, baseGroup.getIndices(), baseGroup.getNormals(), texCoord);
		world.attachChild(tm);
		
		
		controllers = new ArrayList<AbstController>();
		
		controllers.add((new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp())));
		
		
		// create a Slider controller for each target
		// the weight channel of target i can be addressed by the name "WEIGHT_i"
		Slider s1 = new Slider(world, new Vec3(-2,  15, 1),1);
		SliderController sc1 = new SliderController(s1, blend.getChannel("WEIGHT_0"), 0);
		
		Slider s2 = new Slider(world, new Vec3(-1.5f,  15, 1),1);
		SliderController sc2 = new SliderController(s2, blend.getChannel("WEIGHT_1"), 1);
		
		Slider s3 = new Slider(world, new Vec3(-1,  15, 1),1);
		SliderController sc3 = new SliderController(s3, blend.getChannel("WEIGHT_2"), 2);
		
		
		
		controllers.add(sc1);
		controllers.add(sc2);
		controllers.add(sc3);
		
		world = VisualHelp.makeGrid(world, 10);
		world.updateWorldData();
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
		int width  = 1400;
		int height = 800;
							
		FaceBlending demo   = new FaceBlending( width, height );
		demo.gameLoop();
	}
}

