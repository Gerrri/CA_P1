/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package particles;

import java.util.ArrayList;

import scenegraph.Channel;
import scenegraph.ChannelInterface;
import math.Vec3;


/**
 * Generates particles and emits them from a specified position.
 * @author Ulla
 *
 */
public class PointEmitter extends Emitter implements ChannelInterface {
	
	Vec3 startPos;
	ArrayList<Channel> channels;
	
	public static String SOURCE = "Source";

	/**
	 * Constructor of class. 
	 * 
	 * @param emissionRate
	 * @param lifespan
	 * @param pos
	 */
	public PointEmitter(float emissionRate, float lifespan, Vec3 pos) {
		super(emissionRate, lifespan);
		startPos = new Vec3(pos);
		channels = new ArrayList<Channel>(1);
		channels.add (new Channel(SOURCE, startPos));
	}
	

	public Vec3 getInitialPosition() {
		return new Vec3(startPos);
	}


	@Override
	public ArrayList<Channel> getChannels() {
		// TODO Auto-generated method stub
		return channels;
	}


	@Override
	public Channel getChannel(String cname) {
		// TODO Auto-generated method stub
		if (channels.get(0).getName().equals(cname))
			return channels.get(0);
		else
			return null;
	}	
	
	

}
