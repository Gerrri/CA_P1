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

/**
 * Base class for different type of light sources within the scene. At the
 * moment only point light (LT_POINT) is implemented as a concrete class. The
 * ambient, diffuse and specular characteristics of the light, and the constant,
 * linear and quadratic factors of the attenuation formula are stored and get be
 * accessed here.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public abstract class AbstLight extends AbstSpatial {
	/**
	 * Light types, currently only LT_POINT and LT_AMBIENT implemented
	 */
	public enum lightType {
		LT_AMBIENT, LT_DIRECTIONAL, LT_POINT, LT_SPOT, LT_MAX
	};

	/**
	 * Light characteristics.
	 */
	protected Vec3 ambient;
	protected Vec3 diffuse;
	protected Vec3 specular;
	PrimitiveData attenuate;
	PrimitiveData constant;
	PrimitiveData linear;
	PrimitiveData quadratic;

	/**
	 * Channel names for the light attributes. Channels can be used to
	 * manipulate the object's attributes from outside.
	 */
	public static String AMBIENT = "Ambient";
	public static String DIFFUSE = "Diffuse";
	public static String SPECULAR = "Specular";
	public static String ATTENUATE = "Attenuate";
	public static String CONSTANT = "Constant Attenuation";
	public static String LINEAR = "Linear Attenuation";
	public static String QUADRATIC = "Quadratic Attenuation";

	/**
	 * Constructor for light with default light attributes. The constructor
	 * makes all attributes available through channels.
	 * 
	 * @param name
	 *            name of the light object
	 */
	public AbstLight(String name) {
		super(name);
		ambient = Color.black();
		diffuse = Color.white();
		specular = Color.white();
		attenuate = new PrimitiveData(false);
		constant = new PrimitiveData(1.0f);
		linear = new PrimitiveData(0f);
		quadratic = new PrimitiveData(0f);

		channels.add(new Channel(AMBIENT, ambient));
		channels.add(new Channel(DIFFUSE, diffuse));
		channels.add(new Channel(SPECULAR, specular));
		channels.add(new Channel(ATTENUATE, attenuate));
		channels.add(new Channel(CONSTANT, constant));
		channels.add(new Channel(LINEAR, linear));
		channels.add(new Channel(QUADRATIC, quadratic));
	}

	/**
	 * Constructs light with a default name
	 */
	public AbstLight() {
		this("Lichtquelle");
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstLight obj = (AbstLight) super.clone();
		obj.ambient = new Vec3(ambient);
		obj.diffuse = new Vec3(diffuse);
		obj.specular = new Vec3(specular);
		obj.attenuate = new PrimitiveData (attenuate.f);
		obj.constant = new PrimitiveData(constant.f);
		obj.linear = new PrimitiveData(linear.f);
		obj.quadratic = new PrimitiveData(quadratic.f);
		 
		channels.add(new Channel(AMBIENT, obj.ambient));
		channels.add(new Channel(DIFFUSE, obj.diffuse));
		channels.add(new Channel(SPECULAR, obj.specular));
		channels.add(new Channel(ATTENUATE, obj.attenuate));
		channels.add(new Channel(CONSTANT, obj.constant));
		channels.add(new Channel(LINEAR, obj.linear));
		channels.add(new Channel(QUADRATIC, obj.quadratic));
		return obj;
	}

	/**
	 * Returns the type of the light source.
	 * 
	 * @return type of light source
	 */
	public abstract lightType type();

	/**
	 * Sets the ambient color of the light.
	 * 
	 * @param ambient
	 *            value to set the ambient color to
	 */
	public void setAmbient(Vec3 ambient) {
		this.ambient.set(ambient);
		setUpdate(true);
	}

	/**
	 * Returns the ambient color of the light.
	 * 
	 * @return ambient color
	 */
	public Vec3 getAmbient() {
		return ambient;
	}

	/**
	 * Sets the diffuse color of the light.
	 * 
	 * @param diffuse
	 *            value to set the diffuse color to
	 */
	public void setDiffuse(Vec3 diffuse) {
		this.diffuse.set(diffuse);
		setUpdate(true);
	}

	/**
	 * Returns the diffuse color of the light.
	 * 
	 * @return diffuse color
	 */
	public Vec3 getDiffuse() {
		return diffuse;
	}

	/**
	 * Sets the specular color of the light.
	 * 
	 * @param specular
	 *            value to set the specular color to
	 */
	public void setSpecular(Vec3 specular) {
		this.specular.set(specular);
		setUpdate(true);
	}

	/**
	 * Returns the specular color of the light.
	 * 
	 * @return specular color
	 */
	public Vec3 getSpecular() {
		return specular;
	}

	/**
	 * Sets whether the emitted light is attenuated.
	 * 
	 * @param attenuate
	 *            whether the light is to be attenuated
	 */
	public void setAttenuate(boolean attenuate) {
		this.attenuate.b = attenuate;
		setUpdate(true);
	}

	/**
	 * Returns true if the light is to be attenuated.
	 * 
	 * @return boolean value whether the light is to be attenuated
	 */
	public boolean getAttenuate() {
		return attenuate.b;
	}

	/**
	 * Sets the constant attenuation factor of the light.
	 * 
	 * @param constant
	 *            constant attenuation factor
	 */
	public void setConstantAttenuation(float constant) {
		this.constant.f = constant;
		setUpdate(true);
	}

	/**
	 * Returns the constant attenuation factor of the light.
	 * 
	 * @return constant attenuation factor
	 */
	public float getConstantAttenuation() {
		return constant.f;
	}

	/**
	 * Sets the linear attenuation factor of the light.
	 * 
	 * @param linear
	 *            constant linear factor
	 */
	public void setLinearAttenuation(float linear) {
		this.linear.f = linear;
		setUpdate(true);
	}

	/**
	 * Returns the linear attenuation factor of the light.
	 * 
	 * @return linear attenuation factor
	 */
	public float getLinearAttenuation() {
		return linear.f;
	}

	/**
	 * Sets the quadratic attenuation factor of the light.
	 * 
	 * @param quadratic
	 *            quadratic attenuation factor
	 */
	public void setQuadraticAttenuation(float quadratic) {
		this.quadratic.f = quadratic;
		setUpdate(true);
	}

	/**
	 * Returns the quadratic attenuation factor of the light.
	 * 
	 * @return quadratic attenuation factor
	 */
	public float getQuadraticAttenuation() {
		return quadratic.f;
	}

	/**
	 * Tells the renderer to attach the light to the scene
	 * 
	 */
	public void onDraw(AbstRenderer renderer) {
		renderer.attach(this);
	}

	public String toString() {
		return "[ambient=" + ambient + ", diffuse=" + diffuse + ", specular="
				+ specular + ", attenuate=" + attenuate.b + ", constant="
				+ constant.f + ", linear=" + linear.f + ", quadratic="
				+ quadratic.f + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
