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
import math.Vec3;
import math.Vec4;

/**
 * Class to store and provide access to the render engine's material attributes.
 * @author Ursula Derichs
 * @version 1.0
 */
public class MaterialState extends AbstRenderState
{

	/**
	 * Channel names for the material's attributes
	 */
	public static String EMISSIVE = "Emissive";
	public static String AMBIENT = "Ambient";
	public static String DIFFUSE =	"Diffuse"; 
	public static String SPECULAR ="Specular"; 
	public static String SHININESS ="Shininess";
	
	/**
	 * emissive color component
	 */
	private Vec4 emissive;

	/**
	 *  ambient color component
	 */
	private Vec4 ambient;

	/**
	 * diffuse color component
	 */
	private Vec4 diffuse;

	/**
	 * specular color component
	 */
	private Vec4 specular;

	/**
	 * shininess of the material
	 */
	private PrimitiveData shininess;


	/**
	 * Constructs the material state. The attributes are 
	 * initialized as follows: 
	 * - emissive: Color::BLACK 
	 * - ambient: 20% grey 
	 * - diffuse: 80% grey 
	 * - specular: Color::BLACK 
	 * - shininess: 0
	 */
	public MaterialState()
	{
		setName ("defaultMaterial");
		// generate the default material attributes
		emissive = new Vec4 (Color.black(), 1);
		ambient = new Vec4(0.2f, 0.2f, 0.2f, 1);
		diffuse = new Vec4(0.8f, 0.8f, 0.8f, 1);
		specular = new Vec4(Color.black(),1);
		shininess = new PrimitiveData(0f);

		// create channels to the material attributes
		channels.add(new Channel(EMISSIVE, emissive));
		channels.add(new Channel(AMBIENT, ambient));
		channels.add(new Channel(DIFFUSE, diffuse));
		channels.add(new Channel(SPECULAR, specular));
		channels.add(new Channel(SHININESS, shininess));
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		MaterialState obj = (MaterialState) super.clone();
		// copy material attributes
		obj.emissive = new Vec4 (emissive);
		obj.ambient = new Vec4(ambient);
		obj.diffuse = new Vec4(diffuse);
		obj.specular = new Vec4(specular);
		obj.shininess = new PrimitiveData(shininess.f);

		// create channels to the material attributes
		obj.channels.add(new Channel(EMISSIVE, obj.emissive));
		obj.channels.add(new Channel(AMBIENT, obj.ambient));
		obj.channels.add(new Channel(DIFFUSE, obj.diffuse));
		obj.channels.add(new Channel(SPECULAR, obj.specular));
		obj.channels.add(new Channel(SHININESS, obj.shininess));
		return obj;
	}
	
	/**
	 * Returns the type of this render state.
	 * 
	 * @return the constant AbstRenderState::RS_MATERIAL
	 */
	public renderType type()
	{
		return renderType.MATERIAL;
	}

	/**
	 * Returns the emissive component of the material.
	 * 
	 * @return emissive color
	 */
	public Vec4 getEmissive()
	{
		return emissive;
	}

	/**
	 * Sets the emissive component of the material.
	 * 
	 * @param color new emissive color
	 */
	public void setEmissive(Vec4 color)
	{
		emissive.set(color);
	}
	
	
	/**
	 * Sets emissive color with alpha value = 1
	 * @param color new emissive color
	 */
	public void setEmissive(Vec3 color)
	{
		emissive.set(color, 1);
	}
	
	public void setEmissive(float r, float g, float b)
	{
		emissive.set(r, g, b, 1);
	}
	
	public void setEmissive(float r, float g, float b, float alpha)
	{
		emissive.set(r, g, b, alpha);
	}

	/**
	 * Return the ambient component of the material.
	 * 
	 * @return ambient color
	 */

	public Vec4 getAmbient()
	{
		return ambient;
	}

	/**
	 * Set the ambient component of the material.
	 * 
	 * @param color  new ambient color
	 */

	public void setAmbient(Vec4 color)
	{
		ambient.set(color);
	}
	
	public void setAmbient(Vec3 color)
	{
		ambient.set(color, 1);
	}

	public void setAmbient(float r, float g, float b)
	{
		ambient.set(r, g, b, 1);
	} 
	
	public void setAmbient(float r, float g, float b, float alpha)
	{
		ambient.set(r, g, b, alpha);
	}
	/**
	 * Return the diffuse component of the material.
	 * 
	 * @return diffuse color
	 */

	public Vec4 getDiffuse()
	{
		return diffuse;
	}

	/**
	 * Set the diffuse component of the material.
	 * 
	 * @param color  new diffuse color
	 */

	public void setDiffuse(Vec4 color)
	{
		diffuse.set(color);
	}
	
	public void setDiffuse(Vec3 color)
	{
		diffuse.set(color, 1);
	}
	
	public void setDiffuse(float r, float g, float b)
	{
		diffuse.set(r, g, b, 1);
	}
	
	public void setDiffuse(float r, float g, float b, float alpha)
	{
		diffuse.set(r, g, b, alpha);
	}

	/**
	 * Return the specular component of the material.
	 * 
	 * @return specular color
	 */

	public Vec4 getSpecular()
	{
		return specular;
	}

	/**
	 * Set the specular component of the material.
	 * 
	 * @param color  new specular color
	 */

	public void setSpecular(Vec4 color)
	{
		specular.set(color);
	}
	
	public void setSpecular(Vec3 color)
	{
		specular.set(color, 1);
	}
	
	public void setSpecular(float r, float g, float b)
	{
		specular.set(r, g, b, 1);
	}

	public void setSpecular(float r, float g, float b, float alpha)
	{
		specular.set(r, g, b, alpha);
	}
	
	/**
	 * Return the shininess of the material.
	 * 
	 * @return shininess color
	 */

	public float getShininess()
	{
		return shininess.f;
	}

	/**
	 * Sets the shininess of the material.
	 * 
	 * @param shininess new shininess value
	 */

	public void setShininess(float shininess)
	{
		this.shininess.f = shininess;
	}

	/** 
	 * Passes the material state to the renderer
	 * @see scenegraph.AbstSpatial#onDraw(renderer.AbstRenderer)
	 */
	public void onDraw(AbstRenderer renderer)
	{
	   	renderer.setMaterialState(this);
	}
}
