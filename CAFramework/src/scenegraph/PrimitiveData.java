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

/**
 * Wrapper class for basic data types. The wrapper class is necessary to allow
 * manipulation of basic values through the channels.
 * 
 * @author Ulla
 * @version 1.0
 */
public class PrimitiveData {
	/**
	 * Integer value
	 */
	public int i;
	/**
	 * Float value
	 */
	public float f;
	/**
	 * Boolean value
	 */
	public boolean b;
	
	/**
	 * data type which is wrapped within the channel
	 */
	public enum Type {PDT_int, PDT_float, PDT_boolean};
	
	private Type type;
	
	/**
	 * Constructs wrapper for Integer
	 * @param i integer value to be wrapped
	 */
	public PrimitiveData(int i) {
		this.i = i;
		type = Type.PDT_int;
	}
	
	/**
	 * Constructs wrapper for Float
	 * @param f float value to be wrapped
	 */
	public PrimitiveData(float  f) {
		this.f = f;
		type = Type.PDT_float;
	}
	
	/**
	 * Constructs wrapper for Boolean
	 * @param b boolean value to be wrapped
	 */
	public PrimitiveData(boolean b) {
		this.b = b;
		type = Type.PDT_boolean;
	}
	
	/**
	 * Sets integer value
	 * @param i integer value
	 */
	public void set(int i)
	{
		this.i = i;
	}
	
	/**
	 * Sets integer value
	 * @param f integer value
	 */
	public void set(float f)
	{
		this.f = f;
	}
	
	/**
	 * Sets integer value
	 * @param b boolean value
	 */
	public void set(boolean b)
	{
		this.b = b;
	}
	
	/**
	 * @return integer value
	 */
	public Type getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s= "";
		switch(type) {
			case PDT_int: s= Integer.toString(i); break;
			case PDT_float: s = Float.toString(f); break;
			case PDT_boolean: s = Boolean.toString(b); break;
		}
		return s;
	}
	
	/**
	 * Equals method 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals( Object obj )
	{
        if( obj == this )
            return true;
        
        if( obj == null || obj.getClass() != this.getClass()  )
            return false;
        
        PrimitiveData pd = (PrimitiveData) obj;
        
        if (pd.type != this.type)
        	return false;
        
        switch(type) {
			case PDT_int: return (i == pd.i);
			case PDT_float: return (f == pd.f);
			case PDT_boolean: return (b == pd.b);
        }
        
        return false;               
    }


	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
