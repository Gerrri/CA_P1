/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package scenegraph.util;

import math.Vec3;
import math.function.FunctionR1Vec3;
import renderer.AbstRenderer;
import scenegraph.Color;
import scenegraph.ColorState;
import scenegraph.Group;
import scenegraph.Line;
import scenegraph.Sphere;
import scenegraph.StaticMesh;
import scenegraph.TriangleMesh;


/**
 * Collection of visualization helper functions, e.g. floor grid with coordinate
 * axes, animation path drawing
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public class VisualHelp {
	
	private static Group timeGrid;		// time grid to visualize the timing of frames
	private static int secs;			// number of seconds that should be visualized
	private static float offset;		// vertical distance of the grid to the scene
	private static float mid;			// positioning of grid along x-axis, with y as middle
	private static float fps;			// framerate
	private static int lastRow;			// the row where the last time was marked 
	private static Group rows[];		// for each second there is a row
	private static Sphere point;	// little red dot to mark a time on the grid

	/**
	 * Generates a grey transparent floor grid with red x-Axis and blue z-Axis.
	 * @param world		the group for which the grid is generated.
	 * @param noGrids	the number of vertical and horizontal lines of the grid.
	 * @return			the new world group (which includes the grid)
	 */
	public static Group makeGrid(Group world, int noGrids, float scale) {
		float len = noGrids*scale;
		
		Vec3[] z = { new Vec3(0, 0, -len), new Vec3(0, 0, len) };
		Vec3[] x = { new Vec3(-len, 0, 0), new Vec3(len, 0, 0) };

		float[] positions = { -len, 0, -len, len, 0, -len,
				-len, 0, len, len, 0, len };
		int[] indices = { 0, 1, 2, 2, 1, 3 };


		StaticMesh floorMesh = new StaticMesh(positions);
		TriangleMesh floor = new TriangleMesh(floorMesh, indices);

		Group grid = new Group("Boden");
		Line xAxis = new Line(x);
		ColorState col = new ColorState(Color.red());
		grid.attachChild(col);
		grid.attachChild(xAxis);

		Line zAxis = new Line(z);
		col = new ColorState(Color.blue());
		grid.attachChild(col);
		grid.attachChild(zAxis);

		col = new ColorState(Color.grey());
		grid.attachChild(col);

		for (int i = 1; i <= noGrids; i++) {
			float pos = scale * i;
			Line zp = new Line(zAxis);
			Line nzp = new Line(zAxis);
			zp.setTranslation(pos, 0, 0);
			nzp.setTranslation(-pos, 0, 0);
			Line xp = new Line(xAxis);
			Line nxp = new Line(xAxis);
			xp.setTranslation(0, 0, pos);
			nxp.setTranslation(0, 0, -pos);
			grid.attachChild(zp);
			grid.attachChild(nzp);
			grid.attachChild(xp);
			grid.attachChild(nxp);
		}

		col = new ColorState(Color.grey(), 0.1f);
		grid.attachChild(col);
		grid.attachChild(floor);
//		grid.setTranslation(0f,-0.1f, 0f);

		Group scene = new Group ("Szene mit Raster");
		scene.attachChild (world);
		scene.attachChild(grid);
		return scene;
	}
	
	public static Group makeGrid(Group world, int noGrids) {
		return makeGrid(world, noGrids, 1);
	}

	/**
	 * Generates a time grid on the screen. The main purpose is to visualize the
	 * frame rendering at fixed and varying frame periods.
	 * 
	 * Each vertical line represents one second. The horizontal lines represent
	 * the frames per seconds. Assuming you have a framerate of 25 FPS and you
	 * would like to observe at which rate your frames are rendered within 10
	 * seconds, the grid would have 10 horizontal lines and 25+1 vertical lines.
	 * With markTimeOnGrid() the actual time would be symbolized by small red
	 * dot on the grid. When the 10 seconds are over, the new times will be
	 * displayed starting again from the top line (after the old time points
	 * have been deleted).
	 * 
	 * @param world
	 *            the group where the time grid is attached to
	 * @param framerate
	 *            currently used framerate
	 * @param seconds
	 *            number of horizontal lines that represent seconds
	 * @return the world group including the time grid
	 */
	public static Group makeTimeGrid (Group world, int framerate, int seconds)
	{
		fps = framerate;
		secs = seconds;
		mid = (float)(framerate) / 2.0f;
		offset = 10;

		Vec3[] x = { new Vec3(-mid, offset, 0), new Vec3(mid, offset, 0) };
		Vec3[] y = { new Vec3(-mid, offset+seconds-1, 0), new Vec3(-mid, offset, 0) };

		timeGrid = new Group("Zeitraster");
		ColorState col = new ColorState(Color.grey());
		timeGrid.attachChild(col);

		Line horizontal = new Line(x);
		timeGrid.attachChild (horizontal);
		for (int sec = 1; sec < seconds; sec++) 
		{
			Line next = new Line(horizontal);
			next.setTranslation (0,sec,0);
			timeGrid.attachChild(next);
		}

		Line vertical = new Line(y);
		timeGrid.attachChild (vertical);
		for (int frame = 1; frame <= framerate; frame++)
		{
			Line next = new Line(vertical);
			next.setTranslation (frame,0,0);
			timeGrid.attachChild(next);
		}
		timeGrid.attachChild(new ColorState(Color.red()));

		Group scene = new Group ("Szene mit Raster");
		scene.attachChild (world);
		scene.attachChild(timeGrid);
		lastRow = -1;
		return scene;
	}
	
	/**
	 * Calculates the position of specified <time> on the grid. A small red dot
	 * (sphere) is produced to mark that time on the grid.
	 * 
	 * @param time
	 *            point in time, which should me marked on the grid
	 */
	public static void markTimeOnGrid (float time)
	{
		if (lastRow == -1)
		{
			rows = new Group [secs];
			point = new Sphere (0.1f);
		}
		
		float xValue = time % (float) secs;
		int row = (int) xValue;
		xValue = xValue %1;
		xValue *= (float)fps;
		
		if (row != lastRow)
		{
			timeGrid.detachChild(rows[row]);
			rows[row] = new Group ("zeile");
			timeGrid.attachChild(rows[row]);
			lastRow = row;
		}
		Sphere p = new Sphere(point);
		p.setTranslation (xValue-mid, offset+secs-row-1, 0);
		rows[row].attachChild(p);
	}
	
	/**
	 * Draws a function path/curve in the interval [<min>, <max>] in the
	 * specified color.
	 * 
	 * @param world
	 *            Group to which path is to be added
	 * @param func
	 *            function to be drawn
	 * @param min
	 *            starting point of curve
	 * @param max
	 *            end point of curve
	 * @param pathCol	
	 * 			  color for the path
	 * @return group containing the curve
	 */
	public static Group makePath(Group world, FunctionR1Vec3 func, float min,
			float max, Vec3 pathCol) {
		Group path = new Group("Pfad");
		Line polygon = new Line(func, 200, min, max);
		ColorState col = new ColorState(pathCol);
		path.attachChild(col);
		path.attachChild(polygon);

		Group scene = new Group("Szene mit Funktionspfad");
		scene.attachChild(world);
		scene.attachChild(path);
		return scene;
	}
	
	/**
	 * Convenience function for makePath with some defaults. Draws a function
	 * path/curve in the interval [-100, 100] in orange color.
	 * @param world
	 *            Group to which path is to be added
	 * @param func
	 *            function to be drawn
	 * @return group containing the curve
	 */
	public static Group makePath(Group world, FunctionR1Vec3 func) {
		return makePath (world, func, -100, 100, Color.orange());
	}
	
}
