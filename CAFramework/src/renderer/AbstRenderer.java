/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package renderer;

import java.util.EnumMap;
import java.util.ArrayList;

import scenegraph.AbstLight;
import scenegraph.AbstRenderState;
import scenegraph.Color;
import scenegraph.ColorState;
import scenegraph.Cube;
import scenegraph.Group;
import scenegraph.Line;
import scenegraph.MaterialState;
import scenegraph.ParticleGroup;
import scenegraph.PerspectiveCamera;
import scenegraph.Point;
import scenegraph.TextureState;
import scenegraph.TriangleMesh;
import scenegraph.AbstRenderState.renderType;
import math.Mat4;
import math.Vec3;

/**
 * Base class for renderer.
 * A renderer provides various drawing functions to render a scene. This
 * includes drawing of geometric primitives (lines, polygons, meshes, etc.)
 * as well as functions to manage the drawing area (clear the screen, show the
 * back buffer, etc.)
 * To prevent display flicker the renderer maintains a front buffer (the
 * visible picture) and a back buffer, where drawing operations take place.
 * The back buffer will be made visible by a function call not until all
 * drawing operations are finished.
 * The renderer maintains the different states of the render engine in the
 * form of a stack. Classes derived from AbstRenderer must call the methods
 * of this class in their set*State-methods.
 * A renderer can attach user data to scenegraph objects, to cache information needed
 * by the render engine (e.g. texture or other ids needed when the underlying
 * hardware is accessed). These objects should be referenced and kept in
 * internal data structures, to make sure that this information is not lost
 * when the object is removed from the scenegraph and thus not available to release
 * hardware resources.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public abstract class AbstRenderer {

	protected int width;
	protected int height;
	protected int activeLights;
	
	/**
	 * background color
	 */
	private Vec3 backgroundColor;

	/**
	 * active transformation
	 */
	protected Mat4 transform;

	/**
	 * default render states
	 */
	protected EnumMap<AbstRenderState.renderType, AbstRenderState> defaultState;

	/**
	 * current state stack depth
	 */
	protected int nStateDepth;

	/**
	 * maximum state stack depth
	 */
	private int nMaxStateDepth;

	/**
	 * render state storage
	 */
	protected EnumMap<AbstRenderState.renderType, ArrayList<AbstRenderState>> stateStack;

	/**
	 * light state storage
	 */
	protected ArrayList<Integer> lightState;

	/**
	 * maximum number of light sources
	 */
	protected int nMaxLights;

	/**
	 * current transform stack depth
	 */
	protected int nTransformDepth;

	/**
	 * maximum transform stack depth
	 */
	private int nMaxTransDepth;

	/**
	 * transformation stack
	 */
	private ArrayList<Mat4> transformStack;

	/**
	 * Default constructor. The array of default states is initialized.
	 */
	public AbstRenderer() {
		backgroundColor = Color.black();
		transform = new Mat4();
		nStateDepth = 0;
		nMaxStateDepth = 64;
		nMaxLights = 8;
		nTransformDepth = 0;
		nMaxTransDepth = 64;
		transformStack = new ArrayList<Mat4>(nMaxTransDepth);

		// Initialize Array with default states
		defaultState = new EnumMap<AbstRenderState.renderType, AbstRenderState>(
				AbstRenderState.renderType.class);
		defaultState.put(AbstRenderState.renderType.MATERIAL, new MaterialState());
		defaultState.put(AbstRenderState.renderType.COLOR, new ColorState());
		defaultState.put(AbstRenderState.renderType.TEXTURE, new TextureState());


		// Allocate storage for state stacks, one Stack per renderType
		stateStack = new EnumMap<AbstRenderState.renderType, ArrayList<AbstRenderState>>(
				AbstRenderState.renderType.class);
		for (AbstRenderState.renderType renderType : AbstRenderState.renderType
				.values())
			stateStack.put(renderType, new ArrayList<AbstRenderState>(
					nMaxStateDepth));

		lightState = new ArrayList<Integer>(nMaxStateDepth);
	}
	
	/**
	 * Sets the size of the drawing area, the renderer will render into.
	 * 
	 * @param nWidth
	 *            width of the drawing area in pixels
	 * @param nHeight
	 *            height of the drawing area in pixels
	 */
	public abstract void setSize(int nWidth, int nHeight);
	
	public float getWidth()
	{
		return width;
	}

	public float getHeight()
	{
		return height;
	}

	/**
	 * Sets the title of the scene to be rendered
	 * 
	 * @param title
	 *            scene title
	 */
	public abstract void setTitle(String title);

	/**
	 * Clears the depth buffer.
	 */
	public abstract void clearZBuffer();

	/**
	 * Clears the back buffer with the background color.
	 */
	public abstract void clearBackBuffer();

	/**
	 * Clears depth buffer and back buffer.
	 */
	public abstract void clearBuffers();

	/**
	 * Shows the back buffer. This will end all drawing operations until the
	 * next call to beginDraw. The content of the back buffer is undefined after
	 * a call to this function.
	 */
	public abstract void displayBackBuffer();

	/**
	 * Reads the back buffer into an image. The content of the back buffer is
	 * stored in the image. The size and pixel format of the image may be
	 * changed by this function to accommodate the picture. This function should
	 * be called after all drawing operations are completed, but before
	 * displayBackBuffer is called
	 * 
	 * @param image
	 *            image object to store the picture in
	 */

	// public abstract void readBackBuffer(Image image);

	/**
	 * Attaches a light source to the scene. The given light source object is
	 * added to the current LightState.
	 * 
	 * @param light
	 *            light source object to attach
	 */

	public void attach(AbstLight light) {
		int noLights = lightState.get(nStateDepth - 1);
		if (noLights < nMaxLights) {
			noLights++;
			lightState.set(nStateDepth - 1, noLights);
			enableLight(noLights - 1, light);
		}
	}

	/**
	 * Turns lighting on or off. Lighting is enabled per default. This setting
	 * will be affected, when light sources are attached.
	 * 
	 * @param bEnabled
	 *            true, if lighting shall take place
	 */
	public abstract void setLightingEnabled(boolean bEnabled);

	/**
	 * Replaces the current transformation matrix by the given one.
	 * 
	 * @param trans
	 *            new transformation matrix
	 */
	public void loadTransform(Mat4 trans) {
		transform = trans;
	}

	/**
	 * Multiplies the current Transformation matrix with the given one. The
	 * given matrix is right multiplied.
	 * 
	 * @param trans
	 *            matrix to multiply with
	 */
	public void multTransform(Mat4 trans) {
		loadTransform(Mat4.mul(transform, trans));
	}

	/**
	 * Pushes the current transformation onto the transformation stack.
	 */
	public void pushTransform() {
		transformStack.add(nTransformDepth, transform);
		nTransformDepth++;

		if (nTransformDepth >= nMaxTransDepth) {
			nMaxTransDepth *= 2;
			transformStack.ensureCapacity(nMaxTransDepth);
		}
	}

	/**
	 * Pops the transformation stack.
	 */
	public void popTransform() {
		nTransformDepth--;
		loadTransform(transformStack.remove(nTransformDepth));
	}
	
	
	public Mat4 topTransform() {
		return transformStack.get(nTransformDepth-1);
	}

	/**
	 * Prepare the renderer for drawing actions. This function must be called on
	 * a 'per frame' basis before any drawing operations can take place. Calling
	 * 'displayBackBuffer' ends the drawing operation.
	 */

	// public abstract void beginDraw();

	/**
	 * Sets a perspective camera for rendering.
	 * 
	 * @param camera
	 *            camera to be used when rendering
	 */

	public abstract void setCamera(PerspectiveCamera camera);

	/**
	 * Draws a triangle mesh.
	 * 
	 * @param mesh
	 *            triangle mesh to be drawn
	 */
	public abstract void draw(TriangleMesh mesh);

	/**
	 * Draws a line
	 * 
	 * @param line
	 *            line to be drawn
	 */
	public abstract void draw(Line line);
	

	/**
	 * Draws a point
	 * 
	 * @param point
	 *            point to be drawn
	 */
	public abstract void draw(Point point);
	
	/**
	 */
	public abstract void draw(ParticleGroup pgroup);
	
	public void draw(Group group)
	{
		group.draw(this);
	}


	/**
	 * Puts the renderer into a default state. This function has to be called at
	 * least once before any other operations can take place.
	 */
	public void initState() {
		nStateDepth = 1;
		defaultState.get(AbstRenderState.renderType.MATERIAL).draw(this);
		defaultState.get(AbstRenderState.renderType.COLOR).draw(this);
		defaultState.get(AbstRenderState.renderType.TEXTURE).draw(this);

		for (int i = 0; i < nMaxLights; i++)
			disableLight(i);

		lightState.add(0, 0);

		nTransformDepth = 0;
		loadTransform(new Mat4());
	}

	/**
	 * Pushes the state of the render engine onto the state stack.
	 */
	public void pushState() {
		// for all possible states, put top element on stack again

		ArrayList<AbstRenderState> stack;
		for (AbstRenderState.renderType rType : AbstRenderState.renderType
				.values()) {
			stack = stateStack.get(rType);
			stack.add(nStateDepth, stack.get(nStateDepth - 1));
		}

		// additionally handle LightState
		lightState.add(nStateDepth, lightState.get(nStateDepth - 1));

		// increment state stack
		nStateDepth++;

		// check if stack needs expansion
		if (nStateDepth >= nMaxStateDepth) {
			nMaxStateDepth *= 2;
			for (AbstRenderState.renderType rType : AbstRenderState.renderType
					.values())
				stateStack.get(rType).ensureCapacity(nMaxStateDepth);

			lightState.ensureCapacity(nMaxStateDepth);
		}

	}

	/**
	 * Pop the state of the render engine from the stack.
	 */
	public void popState() {
		int i;

		assert nStateDepth > 1 : "The Renderer can not render the scenes, because there are no states to render";

		// each render state is removed from it's stack. If the state does not
		// correspond to the new top element, the state must be "renewed" in the
		// scene.

		nStateDepth--;

		for (AbstRenderState.renderType rType : AbstRenderState.renderType
				.values()) {
			ArrayList<AbstRenderState> stack = stateStack.get(rType);
			AbstRenderState state = stack.get(nStateDepth - 1);

			if (state != stack.remove(nStateDepth))
				state.draw(this);
		}

		// remove the top lightState element from stack (which is the list of
		// active lights)
		int nOldLights = lightState.remove(nStateDepth);

		// get the previous lightState from the stack (which is the list of
		// lights that shall still be active)
		int nNewLights = lightState.get(nStateDepth - 1);

		// Switch off lights, that are not active anymore
		for (i = nNewLights; i < nOldLights; i++)
			disableLight(i);
	}

	/**
	 * Sets the background color. The color set with this function is used to
	 * clear the screen.
	 * 
	 * @param color
	 *            new background color
	 */

	public void setBackgroundColor(Vec3 color) {
		backgroundColor = color;
	}

	/**
	 * Set the color state.
	 * 
	 * @param state
	 *            new color state to use
	 */

	public void setColorState(ColorState state) {
		stateStack.get(renderType.COLOR).add(nStateDepth - 1, state);
	}

	/**
	 * Set the material state.
	 * 
	 * @param state
	 *            new material state to use
	 */
	public void setMaterialState(MaterialState state) {
		stateStack.get(renderType.MATERIAL).add(nStateDepth - 1, state);
	}
	
	/**
	 * Set the texture state.
	 * 
	 * @param state
	 *            new texture state to use
	 */
	public void setTextureState(TextureState state) {
		stateStack.get(renderType.TEXTURE).add(nStateDepth - 1, state);
	}

	/**
	 * Returns the background color.
	 * 
	 * @return background color
	 */
	public Vec3 getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Returns the currently active render state.
	 * 
	 * @param rendertype
	 *            state to return
	 * @return queried state
	 */
	AbstRenderState renderState(AbstRenderState.renderType rendertype) {
		return stateStack.get(rendertype).get(nStateDepth - 1);
	}

	/**
	 * Returns a render state's default value
	 * 
	 * @param rendertype
	 *            state to return
	 * @return queried state
	 */
	AbstRenderState defaultState(AbstRenderState.renderType rendertype) {
		return defaultState.get(rendertype);
	}

	/**
	 * Returns the number of active light sources.
	 * 
	 * @return number of active light sources
	 */
	public int numActiveLights() {
		return lightState.get(nStateDepth - 1);
	}

	/**
	 * Enables a single light source
	 * 
	 * @param nLight
	 *            number of light to enable (id)
	 * @param light
	 *            light object with it's attributes
	 */
	protected abstract void enableLight(int nLight, AbstLight light);

	/**
	 * Disable a single light source.
	 * 
	 * @param nLight
	 *            number of the light to disable
	 */
	protected abstract void disableLight(int nLight);

	/**
	 * Checks if the user requested to close the display
	 * 
	 * @return true, if close display was requested, false otherwise
	 */
	public abstract boolean closeRequested();

	/**
	 * Closes the display
	 */
	public abstract void close();
	
	public abstract void setDepthMask(boolean depthMask);


}