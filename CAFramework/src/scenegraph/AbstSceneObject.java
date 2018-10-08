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

import java.util.ArrayList;

/**
 * Base class for scene graph objects and objects who store date for the scene
 * graph objects (e.g. mesh data, which is used by TriangleMesh). For animated
 * scenes, the characteristics (attributes) of an object must be adaptable over
 * time. In order to allow manipulations of certain attributes from the outside,
 * this class provides the channel mechanism. An attribute can be accessed by
 * specifying a channel name.
 * 
 * When traversing the scene graph during rendering, an object might possibly be
 * treated only when its internal state has changed since the last rendering
 * pass. Thus, an AbstObject manages an "update" flag which has to be set when
 * the object's state is changed (derived objects will do this in their mutator
 * functions). The flag will be reset by the renderer after adopting the state.
 * Resetting the flag after processing an object is not mandatory for the
 * renderer, though.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public abstract class AbstSceneObject implements ChannelInterface, Cloneable {

	/**
	 * List of data channels of the node
	 */
	protected ArrayList<Channel> channels;

	/**
	 * Name of the node
	 */
	protected String name;

	/**
	 * true, if an update of the state is necessary, otherwise false
	 */
	protected boolean update;

	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		 return super.clone();
	}
	/**
	 * Returns all channels of the object, or null if the object has no channels
	 * 
	 * @return list of channels
	 */
	public ArrayList<Channel> getChannels() {
		return channels;
	}
	
	public void setChannels(ArrayList<Channel> channels) {
		this.channels = channels;
	}

	/**
	 * Returns the channel with a given name
	 * 
	 * @param cname
	 *            The name of the queried channel
	 * @return The channel with specified name if it exists, otherwise null
	 */
	public Channel getChannel(String cname) {
		for (int i = 0; i < channels.size(); i++) {
			if (channels.get(i).getName().equals(cname))
				return channels.get(i);
		}
		return null;
	}

	/**
	 * Sets the name of the node
	 * 
	 * @param name
	 *            The name of the node
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the node
	 * 
	 * @return name of the node
	 */
	public String getName() {
		return name;
	}

	/**
	 * To signal, that the state of the object has changed, or not
	 * 
	 * @param value
	 *            true, if an update of the state of the object has been made,
	 *            otherwise false
	 */
	public void setUpdate(boolean value) {
		update = value;
	}

	/**
	 * Indicates, if the state of the object has changed
	 * 
	 * @return true, if state of object has changed, otherwise false
	 */
	public boolean getUpdate() {
		return update;
	}

}
