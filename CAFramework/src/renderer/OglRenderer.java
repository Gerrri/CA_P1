/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package renderer;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;

import static org.lwjgl.util.glu.GLU.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.PixelFormat;

import math.Mat4;
import math.Vec3;
import math.Vec4;
import scenegraph.AbstLight;
import scenegraph.ColorState;
import scenegraph.Line;
import scenegraph.MaterialState;
import scenegraph.ParticleGroup;
import scenegraph.PerspectiveCamera;
import scenegraph.Point;
import scenegraph.TriangleMesh;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import util.Vec3Array;

/**
 * OpenGL Renderer (LWJGL version) - using fixed functions.
 * 
 * Glut is used for geometric primitives like cube, sphere, teapot etc.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class OglRenderer extends AbstRenderer {
	/**
	 * use the inverse of the camera transformation matrix to view the scene
	 * from the camera's perspective
	 */
	private Mat4 cameraTransform;


	/**
	 * Constructor creates display with given dimensions (width and height) and
	 * initializes main OpenGL characteristics.
	 * 
	 * @param width
	 *            width of the display
	 * @param height
	 *            height of the display
	 */
	public OglRenderer(int width, int height) {
		this.width = width;
		this.height = height;

		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_MACOSX)
				Display.create(new PixelFormat(0, 16, 0, 0),
						new ContextAttribs(3, 2).withProfileCore(true));
			else
				Display.create(new PixelFormat(0, 16, 0, 0),
						new ContextAttribs(3, 1));

			System.out.println("OpenGL version: " + glGetString(GL_VERSION));
			Display.setVSyncEnabled(true);
			Display.setTitle("CG FHK");
			Display.setResizable(true);
			glViewport(0, 0, width, height);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		// glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_NORMALIZE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);


		glLightModeli(GL12.GL_LIGHT_MODEL_COLOR_CONTROL,
		 GL12.GL_SEPARATE_SPECULAR_COLOR);
		glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, GL_TRUE);

	}

	/**
	 * Default constructor, creates 600x600 display as default
	 */
	public OglRenderer() {
		this(600, 600); // default width and height 600x600
	}

	/**
	 * reset size of the display
	 * 
	 * TODO is not working as expected
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;

		glViewport(0, 0, width, height);
		Display.update();
	}

	/**
	 * Set title of display
	 * 
	 * @param title
	 *            new title of the display
	 */
	public void setTitle(String title) {
		Display.setTitle(title);
	}

	/**
	 * Clears the depth buffer
	 */
	public void clearZBuffer() {
		glClear(GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Clears the back buffer (for the next image to be displayed)
	 */
	public void clearBackBuffer() {
		glClear(GL_COLOR_BUFFER_BIT);
	}

	/**
	 * clears all buffers (back and depth buffer)
	 */
	public void clearBuffers() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * display the image established in the back buffer
	 */
	public void displayBackBuffer() {
		Display.update();
	}

	/**
	 * enables lighting if bEnbled flag is set.
	 */
	public void setLightingEnabled(boolean bEnabled) {
		if (bEnabled) {
			if (numActiveLights() > 0)
				glEnable(GL_LIGHTING);
		} else
			glDisable(GL_LIGHTING);
	}

	/**
	 * loads given matrix transformation matrix (as Modelview matrix)
	 * 
	 * @param trans
	 *            4x4 transformation matrix
	 */
	public void loadTransform(Mat4 trans) {
		super.loadTransform(trans);
		glLoadMatrix(trans.toFloatBuffer());
	}

	/**
	 * The camera stays in the coordinate origin, and the scene is inverse
	 * transformed against the camera position to get the perspective view.
	 * 
	 * @param camera
	 *            camera object
	 */
	public void setCamera(PerspectiveCamera camera) {
		float zNear, zFar;

		zNear = camera.getZNear();
		zFar = camera.getZFar();

		// load perspective projection matrix
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(camera.getFov(), (float)width / (float) height * camera.aspectRatio(), zNear, zFar);

		// load modelview matrix for scene
		// the inverse of the camera transformation matrix is used as
		// transformation matrix to see the scene from the camera's position.
		glMatrixMode(GL_MODELVIEW);
		cameraTransform = new Mat4(camera.getGlobalTransform());
		cameraTransform.inverse();
		loadTransform(cameraTransform);
	}

	/**
	 * Draws the triangle faces of a given mesh
	 * 
	 * @param mesh
	 *            mesh of triangles
	 */
	public void draw(TriangleMesh mesh) {
		int nTriangles = mesh.numTriangles();
		int[] connect = mesh.triangleIndices();
		Vec3Array vertex = mesh.vertices();
		Vec3Array vertexNormal = mesh.vertexNormals();

		int index = 0;
		Vec3 v, vn;

		glBegin(GL_TRIANGLES);
		for (int i = 0; i < nTriangles; i++) 
		{
			for (int j = 0; j < 3; j++) 
			{
				v = vertex.at(connect[index]);
				vn = vertexNormal.at(connect[index++]);
				glNormal3f(vn.x, vn.y, vn.z);
				glVertex4f(v.x, v.y, v.z, 1f);
			}
		}
		glEnd();

	}

	/**
	 * Draws a polygon line (connects all points of the line with a linear
	 * segment)
	 * 
	 * @param line
	 *            line object containing the points to be connected
	 */
	public void draw(Line line) {
		int nVertices = line.numVertices();
		Vec3Array vertices = line.getVertices();

		glBegin(GL_LINE_STRIP);

		for (int n = 0; n < nVertices; n++) {
			Vec3 v = vertices.at(n);
			glVertex3f(v.x, v.y, v.z);
		}
		glEnd();
		
		if (line.debug())
		{
			glPointSize(10.0f);
			glBegin (GL_POINTS);
			for (int n = 0; n < nVertices; n++) {
				Vec3 v = vertices.at(n);
				glVertex3f(v.x, v.y, v.z);
			}
			glEnd();
		}
	}
	
	/**
	 * Draws a simple point 
	 * 
	 * @param point
	 *            point object containing the position
	 */
	public void draw(Point point) {
		Vec3 pos = point.getTranslation();
		float size = point.getSize();

		glPointSize(size);
		glBegin(GL_POINTS);
		glVertex3f(pos.x, pos.y, pos.z);
		glEnd();
	}
	
	/**
	 * Draws a cloud of particles
	 * 
	 * @param pgroup
	 *            object containing the positions of all particles
	 */
	public void draw(ParticleGroup pgroup) {
		Vec3Array pos = pgroup.getParticlePositions();	
		if (pos == null) 
			return; 
		
		float size = pgroup.getPointSize();

		glPointSize(size);
		glBegin(GL_POINTS);
		for (int i=0; i < pos.length(); i++)
		{
			Vec3 v = pos.at(i);
			glVertex3f(v.x, v.y, v.z);
		}

		glEnd();
	}
	

	/**
	 * sets background color of display
	 * 
	 * @param color
	 *            new background color
	 */
	public void setBackgroundColor(Vec3 color) {
		super.setBackgroundColor(color);
		glClearColor(color.x, color.y, color.z, 1f);
	}

	/**
	 * sets the color rendering state for the next drawings
	 * 
	 * @param state
	 *            color rendering state to be set
	 */
	public void setColorState(ColorState state) {
		super.setColorState(state);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_DIFFUSE);
		Vec4 v = state.getColor();
		glColor4f(v.x, v.y, v.z, v.w);
	}

	/**
	 * sets the material rendering state for the next drawings
	 * 
	 * @param state
	 *            material rendering state to be set
	 */
	public void setMaterialState(MaterialState state) {
		super.setMaterialState(state);
		glDisable(GL_COLOR_MATERIAL);
		glShadeModel(GL_SMOOTH);

		glMaterial(GL_FRONT, GL_EMISSION, state.getEmissive().toFloatBuffer());
		glMaterial(GL_FRONT, GL_AMBIENT, state.getAmbient().toFloatBuffer());
		glMaterial(GL_FRONT, GL_DIFFUSE, state.getDiffuse().toFloatBuffer());
		glMaterial(GL_FRONT, GL_SPECULAR, state.getSpecular().toFloatBuffer());
		glMaterialf(GL_FRONT, GL_SHININESS, state.getShininess());
	}

	/**
	 * Switches on specified light
	 * 
	 * @param nLight
	 *            light number
	 * @param light
	 *            light object (containing attenuation, type attributes)
	 * @see renderer.AbstRenderer#enableLight(int, scenegraph.AbstLight)
	 */
	protected void enableLight(int nLight, AbstLight light) {
		glEnable(GL_LIGHTING);

		int nIndex = GL_LIGHT0 + nLight;

		glEnable(nIndex);

		glLight(nIndex, GL_AMBIENT, light.getAmbient().toFloatBuffer());
		glLight(nIndex, GL_DIFFUSE, light.getDiffuse().toFloatBuffer());
		glLight(nIndex, GL_SPECULAR, light.getSpecular().toFloatBuffer());

		if (light.getAttenuate()) {
			glLightf(nIndex, GL_CONSTANT_ATTENUATION,
					light.getConstantAttenuation());
			glLightf(nIndex, GL_LINEAR_ATTENUATION,
					light.getLinearAttenuation());
			glLightf(nIndex, GL_QUADRATIC_ATTENUATION,
					light.getQuadraticAttenuation());
		} else {
			glLightf(nIndex, GL_CONSTANT_ATTENUATION, 1.0f);
			glLightf(nIndex, GL_LINEAR_ATTENUATION, 0.0f);
			glLightf(nIndex, GL_QUADRATIC_ATTENUATION, 0.0f);
		}

		switch (light.type()) {
		case LT_DIRECTIONAL:
			glLight(nIndex, GL_POSITION, Vec3.zAxis().toFloatBuffer());
			break;

		case LT_POINT:
		case LT_SPOT:
			glLight(nIndex, GL_POSITION, Vec4.origin().toFloatBuffer());
			break;

		default:
			break;
		}

	}

	/**
	 * Switches off specified light
	 * 
	 * @param nLight
	 *            number of light to be switched off
	 */
	protected void disableLight(int nLight) {
		int nIndex = GL_LIGHT0 + nLight;

		glDisable(nIndex);

		if (nLight == 0) {
			// when light 0 is turned off, we assume that no lighting is active
			glDisable(GL_LIGHTING);
		}
	}

	/**
	 * Checks if user requested to close the display
	 * 
	 * @return true, if close was requested false, no close requested
	 */
	public boolean closeRequested() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			return true;
		return Display.isCloseRequested();
	}

	/**
	 * Closes the display
	 */
	public void close() {
		Display.destroy();
	}
	
	
	public void setDepthMask(boolean depthMask)
	{
		if (depthMask == true)
		{
			glDepthMask(depthMask);
			glEnable(GL_DEPTH_TEST);
		}
		else
		{
			glDisable(GL_DEPTH_TEST);
			glDepthMask (depthMask);
		}
	}
}
