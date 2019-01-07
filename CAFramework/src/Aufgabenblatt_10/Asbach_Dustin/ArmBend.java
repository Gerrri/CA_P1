/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package Aufgabenblatt_10.Asbach_Dustin;

import java.util.ArrayList;

import javax.naming.InitialContext;

import math.Mat4;
import math.Vec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.AbstLight;
import scenegraph.AbstSpatial;
import scenegraph.AmbientLight;
import scenegraph.Channel;
import scenegraph.Color;
import scenegraph.ColorState;
import scenegraph.Cylinder;
import scenegraph.Group;
import scenegraph.Joint;
import scenegraph.MaterialState;
import scenegraph.PerspectiveCamera;
import scenegraph.PointLight;
import scenegraph.SkinCluster;
import scenegraph.util.VisualHelp;
import util.Vec3Array;
import animation.AbstController;
import animation.CameraController;
import animation.InteractiveJointController;

public class ArmBend
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
	private ArrayList<Joint> joint_array;

	private AbstLight light;
	
	/**
	 * Test class for importing mesh into the world 
	 */
	public ArmBend(int width, int height)
	{
		renderer = new Ogl3Renderer(width, height);
		renderer.initState();
		renderer.setTitle("Mesh laden");
		renderer.setBackgroundColor(Color.white());

		// generate root element of the world
		world = new Group("Mesh");

		// generate camera and attach it to the scene
		camera = new PerspectiveCamera(0, 1, 10);
		world.attachChild(camera);	
		renderer.setCamera(camera);

		// generate lights and attach them to the scene
		light = new PointLight("Lichtquelle");
		light.setDiffuse (new Vec3(1f, 1f, 1f));
		light.setTranslation (0, 5, 10);		
		world.attachChild(light);
		
		light = new AmbientLight("Umgebungslicht");
		light.setDiffuse (new Vec3(0.3f, 0.3f, 0.3f));
		world.attachChild(light);

		// set green material
		MaterialState mat = new MaterialState();
		mat.setDiffuse (0f, 1f, 0f, 1);
		world.attachChild (mat);
		
		
		int armLen = 8;
		int slices = 10;
		int stacks = 4;
		
		
		joint_array = new ArrayList<>();
		
		// Create arm mesh in bind pose
		Cylinder arm = new Cylinder("Arm", 1, armLen, slices, stacks);
		arm.setWireframe(true);
		world.attachChild (arm);
		world.attachChild(new ColorState(Color.white()));
		
		// Create shoulder joint, attach it to scene and add it to an array of joints
		Joint shoul_joint = new Joint("Shoulder_Joint");
		shoul_joint.setTranslation(0, 4, 0);
		shoul_joint.attachSkeleton();
		world.attachChild (shoul_joint);
		joint_array.add(shoul_joint);
		
		// Create elbow joint, attach it to scene and add it to an array of joints
		Joint elb_joint = new Joint("Elbow_Joint");
		elb_joint.setTranslation(0, 0, 0);
			//elbow joint als Kind hinzufügen
			shoul_joint.attachChild(elb_joint);
		world.attachChild (elb_joint);
		joint_array.add(elb_joint);
		
		
		world.updateWorldTransform(new Mat4());		// calculate transformation matrices of hierarchy
		Vec3Array vertices = arm.meshData().vertices();
		int numVertices = arm.meshData().getNumVertices();
		
		int numWeights = 2;
		float [][] weights = new float [numVertices][numWeights ];
		int [][] indices = new int [numVertices][numWeights];
		
		// Set weights and indices per vertex

		
		
		//Grenze zwischne onerem und unteren vertices,
		//schwierig die richtigen werte zu finden
		
		
		for(int i=0; i<79;i++) {
			if(i<45) {
				weights[i][0]=0;
				weights[i][1]=1;
				
				indices[i][0]=1;
				indices[i][1]=0;
			}else {
				weights[i][0]=1;
				weights[i][1]=1;
				
				indices[i][0]=0;
				indices[i][1]=1;
			}
		}
		

		/*
		for(int i=0; i<79;i++) {
			if(i<16) {			
				indices[i][0]=0;
				indices[i][1]=0;
				
			}else if (i<32) {
				indices[i][0]=1;
				indices[i][1]=1;
			
			}/*else if(i<48){
				indices[i][0]=1;
				indices[i][1]=1;
		
			}else if(i<64){
				indices[i][0]=1;
				indices[i][1]=1;
				
			}else if(i<80){
				indices[i][0]=1;
				indices[i][1]=1;	
			}
		*/
		


				
		// Create skin cluster
		SkinCluster sc = new SkinCluster(vertices, weights, indices, numWeights);
		
		// Set Bind Pose of joints
		sc.bindJoints(joint_array);

		// Set skin cluster as Mesh for arm

		arm.setMeshData(sc);
		
		
		world = VisualHelp.makeGrid(world, 7);
		
		
		Channel[] ijc_channel = {shoul_joint.getChannel(AbstCamera.ROTATION), elb_joint.getChannel(AbstCamera.ROTATION)};
		
		controllers = new ArrayList<AbstController>();
		controllers.add(new InteractiveJointController(ijc_channel));
		controllers.add(new CameraController(camera.getChannel(AbstCamera.TRANSLATION), 
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp()));
		
		// Get rotation channels for joints and add InteractiveJointController for the channels
		
		world.updateWorldData();
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
			world.updateWorldTransform(new Mat4());
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
		int width  = 1000;
		int height = 800;
							
		ArmBend demo   = new ArmBend( width, height );
		demo.gameLoop();
	}
	
}

