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

import scenegraph.Channel;

/**
 * Class for data flow to object attributes. A data channel provides a typed
 * channel through which an object's attributes can be modified from the
 * outside. The data channel mechanism allows to control attributes without
 * exact knowledge of the semantics of this attribute. An animation curve could,
 * for example, control the translation of an object as well as its rotation;
 * even though it would rarely make sense to exchange these curves, the
 * mechanism used for setting keyframes, controlling the behavior of the curves,
 * and calculating the curves' values at a given time are exactly the same - the
 * animation curve does not need any knowledge about what its output is used
 * for. For these reasons the data channel class provides a generic way to
 * connect a data source's output with a controlled attribute. An object's data
 * channels can be distinguished from each other by the name of the attribute
 * which they represent.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class Channel {
	/**
	 * name of the channel. A channel is always accessed by it's name
	 */
	private String name;

	/**
	 * The object, which can be accessed through the channel
	 */
	private Object data;

	/**
	 * The object's class
	 */
	private Class cls;

	/**
	 * Constructs a named channel to the object
	 * 
	 * @param name
	 *            name of the channel
	 * @param data
	 *            object that can be accessed via the channel
	 */
	public Channel(String name, Object data) {
		this.name = name;
		this.data = data;
		this.cls = data.getClass();
	}

	/**
	 * Sets name of the channel
	 * 
	 * @param name
	 *            to be set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return channel name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return data type of channel
	 */
	public Class getDataType() {
		return cls;
	}

	/**
	 * @return reference to data
	 */
	public Object getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Name: " + getName() + ", Klasse: " + getDataType().getName()
				+ ", Wert: " + getData();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "Hello World";
		Channel channel = new Channel("First Text", s);		
		System.out.println(channel);
		
		Float fc = 3.141f;
		Channel fchannel = new Channel("Some digits", fc);
		System.out.println(fchannel);
		Float a = 1.234f;
		// Float b = a +  fchannel.getData(); 
		Float b = a + (Float) fchannel.getData();
		System.out.println(b);				
	}

}
