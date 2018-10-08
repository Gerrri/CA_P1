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

import java.io.File;

import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.ARBDebugOutputCallback;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTAbgr;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
//import org.newdawn.slick.opengl.Texture;
//import org.newdawn.slick.opengl.TextureLoader;
//import org.newdawn.slick.util.ResourceLoader;

import math.Mat3;
import math.Mat4;
import math.Vec3;
import math.Vec4;
import scenegraph.AbstGeometry;
import scenegraph.AbstLight;
import scenegraph.Blendshape;
import scenegraph.ColorState;
import scenegraph.Line;
import scenegraph.MaterialState;
import scenegraph.ParticleGroup;
import scenegraph.PerspectiveCamera;
import scenegraph.Point;
import scenegraph.SkinCluster;
import scenegraph.TextureState;
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
import util.Image;
import util.Vec3Array;
import util.VertexBuffer;

/**
 * OpenGL Renderer (LWJGL version) - only using OpenGL core functionality.
 * 
 * This version is not using the deprecated fixed function API. The main
 * difference to OglRenderer is the usage of shaders and the VBO buffering of
 * mesh data. As Glut cannot be used, all geometric primitives (cube, sphere,
 * etc.) are loaded as meshes.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class Ogl3Renderer extends AbstRenderer
{
	/**
	 * use the inverse of the camera transformation matrix to view the scene
	 * from the camera's perspective
	 */
	protected Mat4 cameraTransform;
	protected ShaderProgram shaderProgram;
	protected HashMap<String, Integer> textureIDs;
	protected HashMap<AbstGeometry, VertexBuffer> meshBufferData;


	private boolean closeRequested = false;

	public static final String POS_X = "posx.jpg";
	public static final String NEG_X = "negx.jpg";
	public static final String POS_Y = "posy.jpg";
	public static final String NEG_Y = "negy.jpg";
	public static final String POS_Z = "posz.jpg";
	public static final String NEG_Z = "negz.jpg";

	/**
	 * width and height of the scene to be rendered
	 */


	boolean verticesLoaded = false;

	/**
	 * Constructor creates display with given dimensions (width and height) and
	 * initializes main OpenGL characteristics.
	 * 
	 * @param width
	 *            width of the display
	 * @param height
	 *            height of the display
	 */
	public Ogl3Renderer(int width, int height)
	{
		this.width = width;
		this.height = height;

		try
		{
			Display.setDisplayMode(new DisplayMode(width, height));
			if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_MACOSX)
				Display.create(new PixelFormat(0, 16, 0, 0),
						new ContextAttribs(3, 2).withProfileCore(true));
			else
				Display.create(new PixelFormat(0, 16, 0, 0),
						new ContextAttribs(3, 2).withProfileCore(true)
								.withDebug(true));
			// Display.create(new PixelFormat(0, 16, 0, 0),new ContextAttribs(3,
			// 1));

			System.out.println("GL_VENDOR: " + glGetString(GL_VENDOR));
			System.out.println("GL_RENDERER: " + glGetString(GL_RENDERER));
			System.out.println("GL_VERSION: " + glGetString(GL_VERSION));

			// ARBDebugOutput
			// .glDebugMessageCallbackARB(new ARBDebugOutputCallback(
			// new ARBDebugOutputCallback.Handler()
			// {
			// public void handleMessage(int msgSource,
			// int msgType, int msgId,
			// int msgSeverity, String message)
			// {
			// System.out.println(message); // set breakpoint to this
			// // line (conditional)
			// // and then examine
			// // call stack
			// }
			// }));
			//
			// ARBDebugOutput.glDebugMessageControlARB(GL_DONT_CARE ,
			// GL_DONT_CARE, GL_DONT_CARE, null, false); // filter out
			// notifications (ID: 33387)

			Display.setTitle("Computer Animations Praktikum");
			Display.setResizable(true);
			glViewport(0, 0, width, height);
		} catch (LWJGLException e)
		{
			e.printStackTrace();
		}

		// glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		// glEnable(GL_CULL_FACE);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		shaderProgram = new ShaderProgram(getPathForPackage() + "Color_vs.glsl",
				getPathForPackage() + "Color_fs.glsl");
		shaderProgram.useProgram();

		activeLights = 0;
		shaderProgram.setUniform("noLights", activeLights);
		textureIDs = new HashMap<String, Integer>();

		meshBufferData = new HashMap<AbstGeometry, VertexBuffer>();
		shaderProgram.setUniform("textured", 0);
	}

	/**
	 * @return The path to directory where the source file of this class is
	 *         located.
	 */
	private String getPathForPackage()
	{
		String locationOfSources = "src";
		String packageName = this.getClass().getPackage().getName();
		String path = locationOfSources + File.separator
				+ packageName.replace(".", File.separator) + File.separator;
		return path;
	}

	/**
	 * Default constructor, creates 600x600 display as default
	 */
	public Ogl3Renderer()
	{
		this(600, 600); // default width and height 600x600
	}

	/**
	 * reset size of the display
	 * 
	 * TODO is not working as expected
	 */
	public void setSize(int width, int height)
	{
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
	public void setTitle(String title)
	{
		Display.setTitle(title);
	}

	/**
	 * Clears the depth buffer
	 */
	public void clearZBuffer()
	{
		glClear(GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Clears the back buffer (for the next image to be displayed)
	 */
	public void clearBackBuffer()
	{
		glClear(GL_COLOR_BUFFER_BIT);
	}

	/**
	 * clears all buffers (back and depth buffer)
	 */
	public void clearBuffers()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * display the image established in the back buffer
	 */
	public void displayBackBuffer()
	{
		Display.update();
	}

	/**
	 * enables lighting if bEnbled flag is set.
	 */
	public void setLightingEnabled(boolean bEnabled)
	{
	}

	/**
	 * loads given matrix transformation matrix (as Modelview matrix)
	 * 
	 * @param trans
	 *            4x4 transformation matrix
	 */
	public void loadTransform(Mat4 trans)
	{
		super.loadTransform(trans);
		shaderProgram.setUniform("modelViewMatrix", transform);
		Mat3 normalMatrix = new Mat3(transform);
//		normalMatrix.inverse().transpose();
		shaderProgram.setUniform("normalMatrix", normalMatrix);
	}

	/**
	 * The camera stays in the coordinate origin, and the scene is inverse
	 * transformed against the camera position to get the perspective view.
	 * 
	 * @param camera
	 *            camera object
	 */
	public void setCamera(PerspectiveCamera camera)
	{
		float zNear, zFar;

		zNear = camera.getZNear();
		zFar = camera.getZFar();

		// load perspective projection matrix
		Mat4 projectionMatrix = Mat4.perspective(camera.getFov(), (float) width,
				(float) height, zNear, zFar);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);

		// load modelview matrix for scene
		// the inverse of the camera transformation matrix is used as
		// transformation matrix to see the scene from the camera's position.
		cameraTransform = new Mat4(camera.getGlobalTransform());
		cameraTransform.inverse();
		loadTransform(cameraTransform);
	}

	private VertexBuffer getBuffer(AbstGeometry obj)
	{
		VertexBuffer buffer;
		if (meshBufferData.containsKey(obj))
		{
			buffer = meshBufferData.get(obj);
		} else
		{
			buffer = new VertexBuffer();
			meshBufferData.put(obj, buffer);
		}
		return buffer;
	}

	/**
	 * Draws the triangle faces of a given mesh
	 * 
	 * @param mesh
	 *            mesh of triangles
	 */
	public void draw(TriangleMesh mesh)
	{
		VertexBuffer buffer = getBuffer(mesh);

		// now define what the buffers contain

		if (buffer.isEmpty())
		{
			buffer.createVAO();
			buffer.loadVertices(mesh.vertices().toFloatBuffer());
			buffer.loadNormals(mesh.vertexNormals().toFloatBuffer());
			buffer.loadFaces(mesh.triangleIndices());
			if (mesh.hasUVs())
			{
				buffer.loadTexcoord(mesh.uv().toFloatBuffer());
			}
		}

		if (mesh.meshData() instanceof SkinCluster)
		{
			SkinCluster skin = (SkinCluster) mesh.meshData();
			if (skin.hasJoints())
			{
				int numWeights = Math.min(skin.getNumWeights(), 4);
				int numVertices = skin.getNumVertices();
				shaderProgram.setUniform("jointMatrices",
						skin.getJointMatrices());
				shaderProgram.setUniform("numWeights", numWeights);
				if (numWeights > 0)
				{
					buffer.loadWeights(skin.getWeights(), numWeights,
							numVertices);
					buffer.loadWeightIndices(skin.getJointIndices(), numWeights,
							numVertices);
				}
			}
		} else
			shaderProgram.setUniform("numWeights", 0);

		if (mesh.meshData() instanceof Blendshape)
		{
			buffer.loadVertices(mesh.vertices().toFloatBuffer());
			buffer.loadNormals(mesh.vertexNormals().toFloatBuffer());
		}

		buffer.useVAO();
		buffer.useIndices();
		if (mesh.isWireframe())
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glDrawElements(GL_TRIANGLES, mesh.numTriangles() * 3, GL_UNSIGNED_INT,
				0);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		glBindVertexArray(0);

		// release buffers
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	/**
	 * Draws a polygon line (connects all points of the line with a linear
	 * segment)
	 * 
	 * @param line
	 *            line object containing the points to be connected
	 */
	public void draw(Line line)
	{
		int nVertices = line.numVertices();

		shaderProgram.setUniform("numWeights", 0);

		VertexBuffer buffer = getBuffer(line);

		if (buffer.isEmpty())
		{
			buffer.createVAO();
			buffer.loadVertices(line.getVertices().toFloatBuffer());
		}

		buffer.useVAO();
		// draw vertices via shader
		glLineWidth(line.getLineWidth());
		glDrawArrays(GL_LINE_STRIP, 0, nVertices);

		if (line.debug())
		{
			glPointSize(10.0f);
			glDrawArrays(GL_POINTS, 0, nVertices);
		}

		glBindVertexArray(0);

		// release buffers
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// release vertex attrib arrays
		// glDisableVertexAttribArray(0);
	}

	/**
	 * Draws a simple point
	 * 
	 * @param point
	 *            point object containing the position
	 */
	public void draw(Point point)
	{

		VertexBuffer buffer = getBuffer(point);
		if (buffer.isEmpty())
		{
			buffer.createVAO();
			buffer.loadVertices(point.getTranslation().toFloatBuffer());
		}

		buffer.useVAO();
		// draw vertices via shader
		glPointSize(point.getSize());
		glDrawArrays(GL_POINTS, 0, 1);

		glBindVertexArray(0);

		// release buffers
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// release vertex attrib arrays
		// glDisableVertexAttribArray(0);
	}

	/**
	 * Draws a cloud of particles
	 * 
	 * @param pgroup
	 *            object containing the positions of all particles
	 */
	public void draw(ParticleGroup pgroup)
	{
		Vec3Array points = pgroup.getParticlePositions();
		if (points == null)
			return;

		VertexBuffer buffer = getBuffer(pgroup);
		if (buffer.isEmpty())
		{
			buffer.createVAO();
		}
		buffer.loadVertices(points.toFloatBuffer());
		int noPoints = points.length();

		buffer.useVAO();
		// draw vertices via shader
		glPointSize(pgroup.getPointSize());
		glDrawArrays(GL_POINTS, 0, noPoints);

		// release buffers
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// release vertex attrib arrays
		// glDisableVertexAttribArray(0);
	}

	/**
	 * sets background color of display
	 * 
	 * @param color
	 *            new background color
	 */
	public void setBackgroundColor(Vec3 color)
	{
		super.setBackgroundColor(color);
		glClearColor(color.x, color.y, color.z, 1f);
	}

	/**
	 * sets the color rendering state for the next drawings
	 * 
	 * @param state
	 *            color rendering state to be set
	 */
	public void setColorState(ColorState state)
	{
		super.setColorState(state);
		// shaderProgram.setUniform ("materialEmission", new Vec4());
		// shaderProgram.setUniform("materialAmbient", new Vec4());
		shaderProgram.setUniform("materialDiffuse", state.getColor());
		// shaderProgram.setUniform ("materialSpecular", new Vec4());
		// shaderProgram.setUniform ("materialShininess", 0.0f);
		shaderProgram.setUniform("colorVec", state.getColor());
		shaderProgram.setUniform("textured", 0);
	}

	/**
	 * sets the material rendering state for the next drawings
	 * 
	 * @param state
	 *            material rendering state to be set
	 */
	public void setMaterialState(MaterialState state)
	{
		super.setMaterialState(state);
		shaderProgram.setUniform("materialEmission", state.getEmissive());
		shaderProgram.setUniform("materialAmbient", state.getAmbient());
		shaderProgram.setUniform("materialDiffuse", state.getDiffuse());
		shaderProgram.setUniform("materialSpecular", state.getSpecular());
		shaderProgram.setUniform("materialShininess", state.getShininess());
		shaderProgram.setUniform("textured", 0);
	}

	/**
	 * sets the texture rendering state for the next drawings
	 * 
	 * @param state
	 *            texture rendering state to be set
	 */
	public void setTextureState(TextureState state)
	{
		super.setTextureState(state);

		if (state.hasTexture())
		{
			int textureID = 0;
			String textureName = state.getTextureName();
			if (!textureIDs.containsKey(textureName)) // is Texture already
														// registered?
			{
				glActiveTexture(GL_TEXTURE0);
				textureID = glGenTextures();
				textureIDs.put(textureName, textureID);

				// if (state.target() == TextureState.TexTarget.CUBE_MAP)
				// {
				// loadCubeMapTextures (textureID, textureName);
				// }
				// else
				{
					loadSingleTexture(textureID, textureName);
				}
			} else
			{
				textureID = textureIDs.get(textureName);
			}

			// if (state.target() == TextureState.TexTarget.CUBE_MAP)
			// {
			// shaderProgram.setUniform("textured", 2);
			// shaderProgram.setCubeMap("background", textureID);
			//
			// }
			// else
			{
				shaderProgram.setUniform("textured", 1);
				shaderProgram.setTexture("tex", textureID);
			}
		} else
			shaderProgram.setUniform("textured", 0);
	}

	private void loadSingleTexture(int textureID, String name)
	{
		Image img = new Image(name);

		// configure the texture
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexImage2D(GL_TEXTURE_2D, 0, img.getInternalFormat(), img.getWidth(),
				img.getHeight(), 0, img.getFormat(), GL_UNSIGNED_BYTE,
				img.getData());
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
				GL_LINEAR_MIPMAP_LINEAR);
		glTexParameterf(GL_TEXTURE_2D,
				EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 16);
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	// private void loadCubeMapTextures (int textureID, String name)
	// {
	// name = name + '/';
	//
	// // configure the texture
	// glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
	// loadCubeMap (GL_TEXTURE_CUBE_MAP_POSITIVE_X, new Image (name + POS_X));
	// loadCubeMap (GL_TEXTURE_CUBE_MAP_NEGATIVE_X, new Image (name + NEG_X));
	// loadCubeMap (GL_TEXTURE_CUBE_MAP_POSITIVE_Y, new Image (name + NEG_Y));
	// loadCubeMap (GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, new Image (name + POS_Y));
	// loadCubeMap (GL_TEXTURE_CUBE_MAP_POSITIVE_Z, new Image (name + POS_Z));
	// loadCubeMap (GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, new Image (name + NEG_Z));
	// glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R,
	// GL_CLAMP_TO_EDGE);
	// glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S,
	// GL_CLAMP_TO_EDGE);
	// glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T,
	// GL_CLAMP_TO_EDGE);
	// glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	// glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	// glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
	// }
	//
	//
	// private void loadCubeMap (int cubeSide, Image img)
	// {
	// glTexImage2D( cubeSide, 0, img.getInternalFormat(), img.getWidth(),
	// img.getHeight(), 0, img.getFormat(), GL_UNSIGNED_BYTE, img.getData());
	// }

	/**
	 * Switches on specified light
	 * 
	 * @param nLight
	 *            light number
	 * @param light
	 *            light object (containing attenuation, type attributes)
	 * @see renderer.AbstRenderer#enableLight(int, scenegraph.AbstLight)
	 */
	protected void enableLight(int nLight, AbstLight light)
	{
		activeLights++;
		shaderProgram.setUniform("noLights", activeLights);

		String index = "[" + nLight + "]";

		shaderProgram.setUniform("lightAmbient" + index, light.getAmbient());
		shaderProgram.setUniform("lightDiffuse" + index, light.getDiffuse());
		shaderProgram.setUniform("lightSpecular" + index, light.getSpecular());

		Vec3 factors;
		if (light.getAttenuate())
		{
			factors = new Vec3(light.getConstantAttenuation(),
					light.getLinearAttenuation(),
					light.getQuadraticAttenuation());
		} else
			factors = new Vec3(1, 0, 0);

		shaderProgram.setUniform("lightFactors" + index, factors);

		Vec4 position;
		switch (light.type())
		{
		case LT_AMBIENT:
			shaderProgram.setUniform("lightType" + index,
					light.type().ordinal());
			break;

		case LT_DIRECTIONAL:
			shaderProgram.setUniform("lightType" + index,
					light.type().ordinal());
			position = Vec4.zAxis();
			position.transform(light.getGlobalTransform());
			position.transform(cameraTransform);
			shaderProgram.setUniform("lightPos" + index, position);
			break;

		case LT_POINT:
		case LT_SPOT:
			shaderProgram.setUniform("lightType" + index,
					light.type().ordinal());
			position = Vec4.origin();
			// TODO: replace getLocalTransform() with getGlobalTransform().
			// Problem: globalTransform must be
			// calculated beforehand (by updateWorldTransform (new Mat4()).
			position.transform(light.getLocalTransform());
			position.transform(cameraTransform);
			shaderProgram.setUniform("lightPos" + index, position);
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
	protected void disableLight(int nLight)
	{
		if (activeLights > 0)
		{
			if (nLight <= activeLights)
			{
				activeLights--;
				shaderProgram.setUniform("noLights", activeLights);
			}
		}
	}

	/**
	 * Checks if user requested to close the display
	 * 
	 * @return true, if close was requested false, no close requested
	 */
	public boolean closeRequested()
	{
		return closeRequested || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)
				|| Display.isCloseRequested();
	}

	/**
	 * Closes the display
	 */
	public void close()
	{
		Display.destroy();
	}

	public void setDepthMask(boolean depthMask)
	{
		if (depthMask == true)
		{
			glDepthMask(depthMask);
			glEnable(GL_DEPTH_TEST);
		} else
		{
			glDisable(GL_DEPTH_TEST);
			glDepthMask(depthMask);
		}
	}

}
