/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package Aufgabenbaltt_13.Asbach_Dustin;

/**
*CA Winter 2018/19
*Name , Vorname : Asbach , Dustin
*Matrikelnummer : 11117108
*Aufgabenblatt : 13
*Aufgabe : 13.1 
**/

import java.util.ArrayList;

import particles.DampedSpring;
import particles.Force;
import particles.Gravity;
import particles.Particle;
import particles.ParticleEulerController;
import particles.ParticleSystem;
import math.Vec3;
import math.Vec4;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.AbstCamera;
import scenegraph.AmbientLight;
import scenegraph.Channel;
import scenegraph.Group;
import scenegraph.MaterialState;
import scenegraph.PerspectiveCamera;
import scenegraph.Point;
import scenegraph.util.VisualHelp;
import animation.AbstController;
import animation.CameraController;

public class TestSpring {

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
	 * 
	 */
	private final ArrayList<AbstController> controllers;
	
	/**
	 * Constructor constructs all scene objects and orders them hierarchically.
	 * 
	 * @param width
	 *            The horizontal window size in pixels
	 * @param height
	 *            The vertical window size in pixels
	 */
	public TestSpring(int width, int height)  {
		// generate and initialize Renderer 
		renderer = new Ogl3Renderer (width, height);
		renderer.initState();
		renderer.setTitle ("Spring forces");
		
		// generate root element of the scene
		world = new Group ("Two Particles connected by a damped spring");
		
		// generate camera and attach it to the scene
		camera = new PerspectiveCamera(0, 30, 40);
		camera.setZRange(camera.getZNear(),500);
		world.attachChild(camera);
		
		AmbientLight light = new AmbientLight("Raumlicht");
		world.attachChild(light);
		
		// Create list with all controllers
		controllers = new ArrayList<AbstController>();
				
		// Create Camera controller
		CameraController camctrl = new CameraController(camera.getChannel(AbstCamera.TRANSLATION), camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp());
		controllers.add(camctrl);
		
		// Create shape of mass points
		Point massPoint = new Point(6);


		// Create particle system		
			ArrayList<Particle> particles = new ArrayList<Particle>();
			ArrayList<Force> forces = new ArrayList<Force>();
		
		
		float L = 1.0f;
		float ks = 10f;
		float kd = 10f;
		
		float x_pos;
		float mass=0.5f;
		
		//Create other Particles 1 to 10
		MaterialState mat;
		Group dummyGroup;
		int anz = 11;
		
		
		Channel c1_trans = null;
		Vec3 c1_vec3 = null;
		
		for(int i =0;i<anz;i++) {
			mat = new MaterialState ();
			mat.setAmbient (createRandomVec3UnitCube());
			mat.setDiffuse (createRandomVec3UnitCube());
			mat.setSpecular (createRandomVec3UnitCube());
			mat.setShininess (500);
			world.attachChild(mat);
			
			// x postion berechnen
			x_pos = i-5;
			
			
			dummyGroup = new Group("Cube_" + i+1);
			dummyGroup.setTranslation(new Vec3(x_pos, 4f, 0.0f));
			dummyGroup.attachChild(massPoint);
			world.attachChild(dummyGroup);
			
			
			particles.add(new Particle(mass, dummyGroup.getChannel("Translation"), new Vec3()));
			
			if(i!=anz-1 && i!=0) {
				//Erdanzeihung
				forces.add(new Gravity(particles.get(i), new Vec3(0, -2f, 0)));
			}
			
			if(i==0) {
				 c1_trans = dummyGroup.getChannel("Translation");
				 c1_vec3 = dummyGroup.getTranslation();			
			}
						
			
		}
		
		//Kr�fte von rechts nach links
		for(int i=1;i<anz-1;i++) {
			forces.add(new DampedSpring(particles.get(i), particles.get(i+1), ks, kd, L));
		}
		
		//Kr�fte von links nach rechts
		for(int i=anz-2;i>0;i--) {
			forces.add(new DampedSpring(particles.get(i), particles.get(i-1), ks, kd, L));
		}
		
		
		
		
		ParticleSystem psystem = new ParticleSystem(particles, forces);

		ParticleEulerController peCtrl;
		peCtrl = new ParticleEulerController("EulerController", psystem, 1);
		controllers.add(peCtrl);
		
		//Keyboard Controller
		KeyboardTranslationController ktc;
		ktc = new KeyboardTranslationController(c1_trans, c1_vec3);
		controllers.add(ktc);
		
		
		
		
		
		// create additional grid for visualization
		world = VisualHelp.makeGrid(world, 5);
		
		// calculate transformation matrices 
		world.updateTransform();
		
		// set the viewport for the scene
		renderer.setCamera (camera);
	}

	public Vec3 createRandomVec3Ball(float radius) {
		double x = Math.random();
		double y = Math.random();
		double z = Math.random();
		x -= 0.5;
		y -= 0.5;
		z -= 0.5;
		
		Vec3 vec = new Vec3((float) x, (float) y, (float) z);
		vec.normalize();
		vec.mul(radius);
		return vec;		
	}
	
	public Vec4 createRandomVec4UnitCube() {
		double x = Math.random();
		double y = Math.random();
		double z = Math.random();
		double alpha = Math.random();
		return  new Vec4((float) x, (float) y, (float) z, (float) alpha);
	}
	
	public Vec3 createRandomVec3UnitCube() {
		double x = Math.random();
		double y = Math.random();
		double z = Math.random();
		return  new Vec3((float) x, (float) y, (float) z);
	}
	
	/**
	 * @param time TThe time in seconds since start of the animation (global time)
	 */
	public void update( float time ) {
		boolean isUpdated = false;
		for (int i = 0; i < controllers.size(); i++) {
			if (controllers.get(i).update(time)) {
				isUpdated = true;
			}	
		}
		
		if (isUpdated) {
			world.updateTransform();
			renderer.setCamera (camera);
		}
	}
	
	/**
	 * Gameloop
	 */
	private  void gameLoop() {
		final int FPS = 60;					// frames per second
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
			
	public void draw() {
		renderer.clearBuffers();
		world.draw(renderer);
		renderer.displayBackBuffer();
	}
	
	public boolean closeRequested() {
		return renderer.closeRequested();
	}
	
	public void cleanup() {
		renderer.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int width  = 1400;
		int height = 1000;
							
		TestSpring demo  = new TestSpring( width, height );
		demo.gameLoop();
	}
}
