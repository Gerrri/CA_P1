/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */
/*CA Winter 2018/19
*Name,  Vorname :  Asbach, Dustin
*Matrikelnummer :  11117108
*Aufgabenblatt : 8
*Aufgabe : 8.2
*/
package Aufgabenblatt_8.Asbach_Dustin;

import java.util.ArrayList;

import animation.AbstController;
import animation.CameraController;
import math.Vec3;
import math.function.util.PolygonVec3;
import renderer.AbstRenderer;
import renderer.Ogl3Renderer;
import scenegraph.*;
import scenegraph.util.Loader;
import scenegraph.util.VisualHelp;

public class TestFrog2 {
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
	public TestFrog2(int width, int height) {
		// generate and initialize Renderer
		renderer = new Ogl3Renderer(width, height);
		renderer.initState();
		renderer.setTitle("Aufgabenblatt 8");
		renderer.setBackgroundColor(Color.white());

		// generate root element of the world
		world = new Group("Frosch");

		camera = new PerspectiveCamera(0, 5, 15);
		camera.setZRange(0.1f, 1000f);
		world.attachChild(camera);
		renderer.setCamera(camera);

		light = new PointLight("Lichtquelle");
		light.setTranslation(0, 10, 10);
		world.attachChild(light);

		// Frog object
		Group frog = Loader.loadMesh("frog.obj");

		Group frog1 = new Group("Eulerdrehung");
		frog1.attachChild(frog);
		frog1.setTranslation(-3, 0, 0);
		Group frog2 = new Group("Quaterniondrehung");
		frog2.attachChild(frog);
		frog2.setTranslation(3, 0, 0);

		world.attachChild(frog1);
		world.attachChild(frog2);

		controllers = new ArrayList<AbstController>();

		controllers.add(new CameraController(camera.getChannel(AbstCamera.TRANSLATION),
				camera.getChannel(AbstCamera.ROTATION), camera.getFocus(), camera.getUp()));

		//Kontroller erstellen Euler
		EulerRotationController2 kontroller_eu = new EulerRotationController2("linear Rotation",frog1.getChannel("Rotation"));
		controllers.add(kontroller_eu);
		
		//Kontroller erstellen Quaternion
		QuatRotationController2 kontroller_quat = new QuatRotationController2("spheric Rotation", frog2.getChannel("Rotation"));
		controllers.add(kontroller_quat);
		
		world = VisualHelp.makeGrid(world, 10);
	}

	/**
	 * @param time The time in seconds since start of the animation
	 */
	public void update(float time) {
		boolean updated = false;
		for (int i = 0; i < controllers.size(); i++) {
			if (controllers.get(i).update(time))
				updated = true;
		}
		if (updated)
			renderer.setCamera(camera);
	}

	/**
	 * Gameloop
	 */
	private void gameLoop() {
		final int FPS = 25; // frames per second
		final int deltaTime = 1000 / FPS; // delta time in milliseconds
		long updateRealTime;
		long sleepTime; // time to wait until next frame
		long animationTime; // animation time in milliseconds (starts at 0)
		long animationStartTime; // system time when the animation started
		float animationTimeSec; // animation time in seconds

		updateRealTime = System.currentTimeMillis();
		animationStartTime = updateRealTime;

		while (!closeRequested() && !exit) {
			// animationTime in seconds
			animationTime = updateRealTime - animationStartTime;
			animationTimeSec = (float) animationTime / 1000.0f;
			update(animationTimeSec);
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
			} else {
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
		int width = 1200;
		int height = 800;

		TestFrog2 demo = new TestFrog2(width, height);
		demo.gameLoop();
	}

}
