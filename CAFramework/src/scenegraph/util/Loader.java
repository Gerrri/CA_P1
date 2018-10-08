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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import animation.AbstController;
import animation.JointController;
import math.Quaternion;
import math.Vec3;
import math.function.util.PolygonVec3Equidistant;
import math.function.util.SphericalLinearEquidistant;
import scenegraph.AbstSpatial;
import scenegraph.Group;
import scenegraph.Joint;
import scenegraph.MaterialState;
import scenegraph.StaticMesh;
import scenegraph.TextureState;
import scenegraph.TriangleMesh;
import util.FileIO;
import util.OBJContainer;
import util.OBJGroup;
import util.OBJMaterial;
import util.Vec3Array;


/**
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public class Loader {
	
    private static Scanner scan;
    private static String fileName;
    private static BVHClip  data; 

	public static Group loadMesh(String filename) {
		Group meshGroup = new Group(filename);
		OBJContainer obj = OBJContainer.loadFile(filename);
		ArrayList<OBJGroup> groups = obj.getGroups();
		for (OBJGroup group : groups)
		{
			Group objGroup = new Group(group.getName());
			
			Vec3Array texCoord = null;
			
			OBJMaterial mat = group.getMaterial();
			if (mat != null)
			{
				MaterialState matState = new MaterialState();
				matState.setAmbient(mat.getAmbientColor());
				matState.setDiffuse(mat.getDiffuseColor());
				matState.setSpecular(mat.getSpecularColor());
				matState.setShininess(mat.getSpecular());
				matState.setName(mat.getName());
				objGroup.attachChild (matState);
				
				String diffuseMap = mat.getDiffuseTextureName();
				if (diffuseMap != null)
				{
					texCoord = group.getTexCoords();
					TextureState texState = new TextureState(diffuseMap, TextureState.TexType.DIFFUSE);
					objGroup.attachChild (texState);
				}
			}
			
			int [] indices = group.getIndices();
			StaticMesh m = new StaticMesh (group);
			TriangleMesh tm = new TriangleMesh (m, indices, group.getNormals(), texCoord);
			objGroup.attachChild (tm);
			meshGroup.attachChild(objGroup);
		}
		return meshGroup;
	}
	
	
    public static BVHClip loadBVH(String name) {

        fileName = name;
        String path    = FileIO.pathOfBvh( fileName );


        
		try
		{	
			
		    FileInputStream fstream = new FileInputStream( path );  
		    DataInputStream in      = new DataInputStream( fstream );  
		    
            scan = new Scanner(in);
            scan.useLocale(Locale.US);
            data = new BVHClip();
  

            loadFromScanner();
		    fstream.close();
		    in.close();
		    scan.close();
        } 
		catch( Exception e ) 
		{
			System.err.println( "*Error* Can't read BVH file: " + fileName );
			e.printStackTrace();
        }
		
        String clipName = fileName;
        if(fileName.contains(".")) 
        	clipName = fileName.substring(0, fileName.lastIndexOf('.'));
        data.setName(clipName);
		
        return data;
    }
    
	private static void loadFromScanner() throws IOException
	{
		String token = scan.next();
		Joint root = null;
		if (token.equals("HIERARCHY"))
		{
			token = scan.next();
			if (token.equals("ROOT"))
			{
				token = scan.next();
				root = readJoint(token);
				data.setSkeleton(root);
				token = scan.next();
			}
		}
		if (token.equals("MOTION"))
		{
			scan.next();
			data.setNumFrames(scan.nextInt());
			scan.next();
			scan.next();
			data.setTimePerFrame(scan.nextFloat());
			readValues();
		}

		if (root != null)
			root.attachSkeleton();
	}

    private static Joint readJoint(String name) {

        Joint joint = new Joint(name);
        JointController jointController  = new JointController();
        jointController.setName(name);
        jointController.setRepeatType(AbstController.RepeatType.CYCLE);
        
        if (! name.equals("Site"))
	        data.getControllers().add(jointController);

        String token = scan.next();
        if (token.equals("{")) {
            token = scan.next();
            if (token.equals("OFFSET")) {
            	Vec3 offset = new Vec3 (scan.nextFloat(), scan.nextFloat(), scan.nextFloat());
            	joint.setTranslation (offset);
                token = scan.next();
            }
            if (token.equals("CHANNELS")) {
            	Vec3 rotOrder[] = new Vec3[3];
            	int rotOrderIndex =0;
            	String channelName;
            	int nbChan = scan.nextInt();
                for (int i = 0; i < nbChan; i++) {
                	channelName = scan.next();
                	if (channelName.equals("Xposition"))
                	{
                		jointController.setTransChannel(joint.getChannel(AbstSpatial.TRANSLATION));
                	}
                	else if (channelName.equals("Zrotation"))
                		rotOrder[rotOrderIndex++] = Vec3.zAxis();
                	else if (channelName.equals("Yrotation"))
                		rotOrder[rotOrderIndex++] = Vec3.yAxis();
                	else if (channelName.equals("Xrotation"))
                		rotOrder[rotOrderIndex++] = Vec3.xAxis();
         
                }
                if (rotOrderIndex > 0)
                {
//                	jointController.setRotOrder (new Mat3(rotOrder[0], rotOrder[1], rotOrder[2]));
                	jointController.setRotChannel(joint.getChannel(AbstSpatial.ROTATION));
                }
                token = scan.next();
            }
            while (token.equals("JOINT") || token.equals("End")) {
                joint.attachChild(readJoint(scan.next()));
                token = scan.next();
            }
        }
        return joint;
    }

	private static void readValues()
	{
		int numFrames = data.getNumFrames();
		float frameDuration = data.getTimePerFrame();
		float duration = (numFrames) * frameDuration; 
		ArrayList<JointController> controllers = data.getControllers();
		int numJoints = controllers.size();
		data.setNumJoints(numJoints);
		
		Vec3 [][] translations = new Vec3 [numJoints][numFrames];
		Quaternion [][] rotations = new Quaternion[numJoints] [numFrames];
		
		float x, y, z;
		for (int i=0; i < numFrames; i++ )
		{
			for (int j=0; j < numJoints; j++)
			{
				JointController jc = (JointController)(controllers.get(j));
				jc.setMaxTime(duration);
				if (jc.getTransChannel() != null)
				{
					// read translation values
					x = scan.nextFloat();
					y = scan.nextFloat();
					z = scan.nextFloat();
					
					translations[j][i] = new Vec3(x,y,z);
				}
				else 
					translations[j][i] = null;
			
				if (jc.getRotChannel() != null)
				{
					// read rotation angles, transform degrees to radians
					// TODO, could be a different order then z-x-y
					z = (float) Math.toRadians(scan.nextFloat());
					x = (float) Math.toRadians(scan.nextFloat());
					y = (float) Math.toRadians(scan.nextFloat());
				
					rotations[j][i] = Quaternion.fromEulerAngles (new Vec3(x, y, z));
				}
			}
		}
		
		for (int j=0; j < numJoints; j++)
		{
			JointController jc = (JointController)(controllers.get(j));
			if (jc.getTransChannel() != null)
				jc.setTranslationsFunc (new PolygonVec3Equidistant (translations[j], 0, duration));
	
			if (jc.getRotChannel() != null)
				jc.setRotationsFunc (new SphericalLinearEquidistant (rotations[j], 0, duration));
		}
	}
	
}