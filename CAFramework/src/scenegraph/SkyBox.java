/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package scenegraph;

import renderer.AbstRenderer;
import scenegraph.TextureState.TexTarget;
import scenegraph.TextureState.TexType;
import util.Vec3Array;

/**
 * Class for basic geometric shapes: Cube, Sphere, Torus, Teapot, Cone and
 * Tetrahedron. Instead of the vertices, this class only stores some
 * characteristic attributes for the shapes (like the radius for a sphere
 * etc.)
 * 
 * @author Ursula Derichs
 * @version 1.0
 */



public class SkyBox extends AbstSpatial {

	private final int SIZE = 200;
	private Cube cube;
	private TextureState texture;
	private AbstCamera camera;


	/**
	 * Constructs a Skybox that servers as a background for the 3D scene
	 * 
	 * @param name
	 *            object's name
	 */
	public SkyBox(String name, AbstCamera cam) {
		
		// set the color of the sibling elements
		texture = new TextureState(name, TexType.DIFFUSE, TexTarget.CUBE_MAP);
		
		// Kommentar
		// generate a cube and attac it to the scene
		cube = new Cube("Background", SIZE);
//		cube.setTranslation(0, SIZE, 0);
		camera = cam;
	}

	/**
	 * Copy constructor
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public SkyBox(SkyBox obj) {
		cube = new Cube(SIZE);
		
	}

	@Override
	public void onDraw(AbstRenderer renderer)
	{
//		setTranslation (camera.location());

		// switch off renderer depth test 
		renderer.setDepthMask(false);
		renderer.setTextureState(texture);
		renderer.draw(cube);

		renderer.setDepthMask(true);
	}	
}
