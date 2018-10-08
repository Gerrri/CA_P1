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
import util.Vec3Array;
import math.Vec3;
import math.function.FunctionR1Vec3;

/**
 * Class for polygon lines. The line is specified by an array of vertices which
 * are connected with each other by a line. 
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class Line extends AbstGeometry
{
	/**
	 * 	vertices of the line polygon
	 */
	Vec3Array vertices;
	
	/**
	 * number of vertices
	 */
	protected int nVertices;
	
	/*
	 * when debug flag is set, the vertices of the line are displayed in another color
	 */
	private boolean debug = false;
	
	private float lineWidth = 1;
	


	/**
	 * Constructs an empty line
	 * @param name name of the line object
	 */
	public Line(String name)
	{
		super (name);
	}
	
	/**
	 * Constructs a line consisting of vertices that are connected with each
	 * other in the order of their position in the array.
	 * 
	 * @param vertices
	 *            array of line polygon vertices
	 */
	public Line(Vec3 [] vertices)
	{
		super ("Line");
		this.nVertices = vertices.length;
		this.vertices = new Vec3Array (vertices);
	}
	
	/**
	 * Constructs a line by connecting the vertices that are connected with each
	 * other. Vertices are given in the form of positions, each position
	 * consists of 3 float values.
	 * 
	 * @param positions
	 *            array of vertex position data. Each vertex is made up of 3
	 *            values (representing the x, y, z, coordinates of the vertex).
	 */
	public Line(float [] positions)
	{
		super ("Line");
		this.nVertices = positions.length/3;
		this.vertices = new Vec3Array (positions);
	}
	
   /**
    * Constructs a polygonal line by sampling a 3D function.
    * @param func 3D function assigns a vertex to each value
    * @param samples number of samples within the function's interval
    * @param min left interval border for the sampling
    * @param max right interval border for the sampling
    */
	public Line(FunctionR1Vec3 func, int samples, float  min, float max)
    {
	   super("Function Sample");
	   
	   nVertices = samples;
	   vertices = new Vec3Array(samples);
	   
	   // determine the sampling interval
	   float tmin = Math.max(func.getTMin(), min);
	   float tmax = Math.min(func.getTMax(), max);
	   
	   //calculate the distance between the samples
	   float step = (tmax-tmin)/ (samples-1);
	   
	   // evaluating the vertices
	   for (int i=0; i<samples; i++)
		   vertices.push_back(func.eval(tmin + i * step));  
   }
	
	
	/**
	 * Copy Constructor
	 * @param obj object to be copied
	 */
	public Line( Line obj) 
	{
		super(obj);
		
		this.nVertices = obj.nVertices;
		this.vertices = new Vec3Array (obj.vertices); 
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Line obj = (Line) super.clone();
		obj.vertices = new Vec3Array (vertices); 
		return obj;
	}
	
	/**
	 * Return the number of vertices of the line.
	 * @return number of vertices
	 */
	public int numVertices()
	{
		return nVertices;
	}
	
	/**
	 * Return the number vertices of the line.
	 * @return vertices of the polygon line
	 */
	public Vec3Array getVertices()
	{
		return vertices;
	}
	
	/**
	 * Sets the vertices of the line
	 * @param vertices vertices to be set
	 */
	public void setVertices (Vec3Array vertices)
	{
		this.vertices = vertices;
		this.nVertices = vertices.length();
	}
	
    /** 
     * Passes the line object to the renderer
     * @param renderer the renderer that shall draw the line
     * @see scenegraph.AbstSpatial#onDraw(renderer.AbstRenderer)
     */
    @Override
	public void onDraw( AbstRenderer renderer)
    {
    	renderer.draw( this );
    }
    
    public void setDebug (boolean debugMode)
    {
    	debug = debugMode;
    }

    public boolean debug ()
    {
    	return debug;
    }
    
	public float getLineWidth()
	{
		return lineWidth;
	}

	public void setLineWidth(float lineWidth)
	{
		this.lineWidth = lineWidth;
	}
}
