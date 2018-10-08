package util;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import math.Vec3;

public class OBJContainer
{
	private static final int POSITION = 0;
	private static final int TEXCOORD = 1;
	private static final int NORMAL = 2;
	
	private ArrayList<OBJGroup> facegroups;
	
	
	private OBJContainer()
	{
		facegroups = new ArrayList<OBJGroup>();
	}
	
	
	public ArrayList<OBJGroup> getGroups()
	{
		return facegroups;
	}
	
	/**
	 * Gets the first group which matches the name. Please note, that there could be more than one group with the same name, but different materials.
	 * @param name - Group name
	 * @return the first matching group with that name
	 */
	public OBJGroup getGroup (String name)
	{
		for (OBJGroup group : facegroups)
		{
			if (group.getName().equals(name))
				return group;
		}
		return null;
	}
	
	/**
	 * Gets all matching groups with the specified name. 
	 * @param name - Group name
	 * @return an array of matching groups
	 */
	public ArrayList<OBJGroup> getGroups (String name)
	{
		ArrayList<OBJGroup> matching = new ArrayList<OBJGroup>();
		for (OBJGroup group : facegroups)
		{
			if (group.getName().equals(name))
				matching.add(group);
		}
		return matching;
	}
	
	public static OBJContainer loadFile( String filepath )
	{
		return OBJContainer.loadFile( filepath, new Vec3(0.0f), new Vec3(1.0f) );
	}
	
	
	public static OBJContainer loadFile( String filepath, Vec3 translation, Vec3 scale )
	{
		String path    = FileIO.pathOfMeshes( filepath );
		int    pathEnd = 1 + Math.max( path.lastIndexOf('\\'), path.lastIndexOf('/') );
		String folder  = path.substring( 0, pathEnd );
		
		OBJContainer   container = new OBJContainer();
		FloatArrayList positions = new FloatArrayList();
		FloatArrayList normals   = new FloatArrayList();
		FloatArrayList texCoords = new FloatArrayList();
		
		HashMap<String, ArrayList<String>> faceGroups   = new HashMap<String, ArrayList<String>>();
		ArrayList<String>                  currentGroup = new ArrayList<String>();
		HashMap<String, OBJMaterial>       materials    = new HashMap<String, OBJMaterial>();
		
		OBJMaterial currentMaterial  = new OBJMaterial();
		currentMaterial.setName("default");
		String currentGroupName = "default";
		String currentMaterialName = "default";
		String currentName = currentGroupName + ">" + currentMaterialName;
		
		materials.put(  currentName, currentMaterial );
		faceGroups.put( currentName, currentGroup );
		
		try
		{
		    FileInputStream fstream = new FileInputStream( path );  
		    DataInputStream in      = new DataInputStream( fstream );  
		    BufferedReader  br      = new BufferedReader( new InputStreamReader(in) );
		    
		    String line = "";	   

		    while( (line = br.readLine()) != null )
		    {
		    	String[] line_parts = line.split( "\\s+" );
				
				if( line_parts[0].equalsIgnoreCase("v") )
				{
					positions.add( translation.x + scale.x * toFloat(line_parts[1], 0.0f) );
					positions.add( translation.y + scale.y * toFloat(line_parts[2], 0.0f) );
					positions.add( translation.z + scale.z * toFloat(line_parts[3], 0.0f) );
				}
				else if( line_parts[0].equalsIgnoreCase("vt") )
				{
					texCoords.add( toFloat(line_parts[1], 0.0f) );
					texCoords.add( toFloat(line_parts[2], 0.0f) );
					if( line_parts.length > 3 )
						texCoords.add( toFloat(line_parts[3], 2.0f) );
					else
						texCoords.add( 0.0f );
				}
				else if( line_parts[0].equalsIgnoreCase("vn") )
				{
					normals.add( toFloat(line_parts[1], 0.0f) );
					normals.add( toFloat(line_parts[2], 1.0f) );
					normals.add( toFloat(line_parts[3], 0.0f) );
				}
				else if( line_parts[0].equalsIgnoreCase("f") )
				{
					currentGroup.add( line_parts[1] );
					currentGroup.add( line_parts[2] );
					currentGroup.add( line_parts[3] );
					
					if( line_parts.length == 5 ) // the face is a quad
					{
						currentGroup.add( line_parts[1] );
						currentGroup.add( line_parts[3] );
						currentGroup.add( line_parts[4] );
					}
					else if( line_parts.length > 5 )
						System.err.println( "N-gons with more than 4 vertices are not supported!" );
				}
				else if( line_parts[0].equalsIgnoreCase("mtllib") )
				{
					materials = OBJMaterial.parseMTL( folder, line_parts[1] );
				}
				else if( line_parts[0].equalsIgnoreCase("usemtl") )
				{
					String      materialName = line_parts[1];
					OBJMaterial tempMaterial = materials.get( materialName );
					
					if (tempMaterial == null)
					{
						System.err.println( "Material " + materialName + " is not defined");
					}
					
					else if( tempMaterial != currentMaterial )
					{
						currentMaterial = tempMaterial;
						currentName = currentGroupName + ">" + currentMaterial.getName();
						ArrayList<String> tempGroup = faceGroups.get(currentName);
						if (tempGroup != null)
							currentGroup = tempGroup;
						else
						{
							currentGroup = new ArrayList<String>();
							faceGroups.put(currentName, currentGroup);
							materials.put( materialName, currentMaterial );
						}	
					}	
				}
				else if( line_parts[0].equalsIgnoreCase("g") )
				{
					if (line_parts.length > 1)
					{
						currentGroupName = line.substring(2);
						currentGroupName.trim();
					}
					else
						currentGroupName = "default";
					
					currentName = currentGroupName + ">" + currentMaterial.getName();
					currentGroup = faceGroups.get( currentName);
					if( currentGroup == null )
					{
						currentGroup = new ArrayList<String>();
						faceGroups.put( currentName, currentGroup );
					}
				}
		    }
		    
		    fstream.close();
		    in.close();
		    br.close();

		} 
		catch( Exception e ) 
		{
			System.err.println( "*Error* Can't read OBJ file: " + path );
			e.printStackTrace();
		}
		
	    
		for( Entry<String, ArrayList<String>> entry : faceGroups.entrySet() )
		{
			ArrayList<String> faceGroup = entry.getValue();
			String            faceGroupName = entry.getKey();
			
			String[] parts = faceGroupName.split(">");
			if (parts.length != 2)
			{
				System.err.println ("Cannot interprete the .obj file, something is strange with " + faceGroupName);
				return null;
			}
			
			String groupName = parts[0];
			String materialName = parts[1];
			OBJMaterial material  = materials.get( materialName );
	    	
			if( faceGroup.size() < 3 )
				continue;
			
			container.facegroups.add( createOBJGroup(positions, texCoords, normals, faceGroup, material, groupName) );
		}
		
		return container;
	}
	
		
	private static String vertexToString( int positionIndex, int texCoordIndex, int normalIndex )
	{
		return "p" + positionIndex + "t" + texCoordIndex + "n" + normalIndex;
	}
	
	
	private static int toInt( String string, int defaultValue )
	{
		try
		{
			return Integer.parseInt( string );
		}
		catch(Exception e)
		{
			return defaultValue;
		}
	}
	
	
	private static float toFloat( String string, float defaultValue )
	{
		try
		{
			return Float.parseFloat( string );
		}
		catch(Exception e)
		{
			return defaultValue;
		}
	}
	
	
	private static OBJGroup createOBJGroup( FloatArrayList    positions, 
											FloatArrayList    texCoords, 
											FloatArrayList    normals, 
											ArrayList<String> faceGroup, 
											OBJMaterial       material, 
											String 			  name)
	{
		FloatArrayList groupPositions = new FloatArrayList();
		FloatArrayList groupTexCoords = new FloatArrayList();
		FloatArrayList groupNormals   = new FloatArrayList(); 
		IntArrayList   groupIndices   = new IntArrayList();
		
		boolean normalValues = false;
		boolean textureCoordValues = false;
				    
	    HashMap<String, Integer> uniqueVertices = new HashMap<String, Integer>();
	    int                      nextVertexID   = 0;
	    
    	for( int index = 0; index < faceGroup.size(); index += 3 )
	    {
    		// ===========================
    		// attributeIndices0: p0/t0/n0
    		// attributeIndices1: p1/t1/n1
    		// attributeIndices2: p2/t2/n2
    		// ===========================
    		
	    	String[] attributeIndices0 = faceGroup.get( index     ).split( "/" );
	    	String[] attributeIndices1 = faceGroup.get( index + 1 ).split( "/" );
	    	String[] attributeIndices2 = faceGroup.get( index + 2 ).split( "/" );

	    	
	    	// =======================================================
	    	// subtracting 1 since obj indices start at 1 instead of 0
	    	// =======================================================
	    	
	    	int position0 = toInt( attributeIndices0[POSITION], 1 ) - 1;
	    	int position1 = toInt( attributeIndices1[POSITION], 1 ) - 1;
	    	int position2 = toInt( attributeIndices2[POSITION], 1 ) - 1;
	    	
	    	int texcoord0 = position0;
	    	int texcoord1 = position1;
	    	int texcoord2 = position2;

	    	int normal0 = position0;
	    	int normal1 = position1;
	    	int normal2 = position2;
	    	
	    	if( attributeIndices0.length > 1 )
	    	{
		    	texcoord0 = toInt( attributeIndices0[TEXCOORD], 1 ) - 1;
		    	texcoord1 = toInt( attributeIndices1[TEXCOORD], 1 ) - 1;
		    	texcoord2 = toInt( attributeIndices2[TEXCOORD], 1 ) - 1;
		    	
		    	if (attributeIndices0[TEXCOORD].length() > 0) 
		    		textureCoordValues = true; 
		    	
		    	if( attributeIndices0.length > 2 )
		    	{
		    		normalValues = true; 
			    	normal0 = toInt( attributeIndices0[NORMAL], 1 ) - 1;
			    	normal1 = toInt( attributeIndices1[NORMAL], 1 ) - 1;
			    	normal2 = toInt( attributeIndices2[NORMAL], 1 ) - 1;
		    	}
	    	}

	    	// ==============================================================
	    	// really ugly and most likely extremely slow to use strings here
	    	// ==============================================================
	    	
	    	String v0String = vertexToString( position0, texcoord0, normal0 );
	    	String v1String = vertexToString( position1, texcoord1, normal1 );
	    	String v2String = vertexToString( position2, texcoord2, normal2 );

	    	Integer index0 = uniqueVertices.get( v0String );
	    	Integer index1 = uniqueVertices.get( v1String );
	    	Integer index2 = uniqueVertices.get( v2String );

	    	if( index0 == null )
	    	{
	    		OBJContainer.addAttributeVec3( groupPositions, positions, position0 );
	    		if (textureCoordValues)
	    			OBJContainer.addAttributeVec3( groupTexCoords, texCoords, texcoord0 );
	    		if (normalValues)
	    			OBJContainer.addAttributeVec3( groupNormals,   normals,   normal0 );
		    	
	    		index0 = nextVertexID;
	    		uniqueVertices.put( v0String, index0 );
	    		
	    		nextVertexID++;
	    	}
	    	
	    	if( index1 == null )
	    	{
	    		OBJContainer.addAttributeVec3( groupPositions, positions, position1 );
	    		if (textureCoordValues)
	    			OBJContainer.addAttributeVec3( groupTexCoords, texCoords, texcoord1 );
	    		if (normalValues)
	    			OBJContainer.addAttributeVec3( groupNormals,   normals,   normal1 );
		    	
	    		index1 = nextVertexID;
	    		uniqueVertices.put( v1String, index1 );
	    		
	    		nextVertexID++;
	    	}
	    	
	    	if( index2 == null )
	    	{
	    		OBJContainer.addAttributeVec3( groupPositions, positions, position2 );
	    		if (textureCoordValues)
	    			OBJContainer.addAttributeVec3( groupTexCoords, texCoords, texcoord2 );
	    		if (normalValues)
	    			OBJContainer.addAttributeVec3( groupNormals,   normals,   normal2 );
		    	
	    		index2 = nextVertexID;
	    		uniqueVertices.put( v2String, index2 );
	    		
	    		nextVertexID++;
	    	}

	    	groupIndices.add( index0 );
	    	groupIndices.add( index1 );
	    	groupIndices.add( index2 );
	    }
    	
    	groupPositions.trimToSize();
    	groupTexCoords.trimToSize();
    	groupNormals.trimToSize();
    	groupIndices.trimToSize();
    	
    	return new OBJGroup( new Vec3Array(groupPositions.toArray()), 
				 new Vec3Array(groupNormals.toArray()), 
				 new Vec3Array(groupTexCoords.toArray()), 
				 groupIndices.toArray(),
				 material,
				 name);
	}
	
	
	/**
	 * Convenience function to reduce duplicate code
	 */
	private static void addAttributeVec3( FloatArrayList destination, FloatArrayList source, int sourceIndex )
	{
		if( source.size() > sourceIndex * 3 + 2 )
    	{
			destination.add( source.get(sourceIndex * 3 + 0) );
			destination.add( source.get(sourceIndex * 3 + 1) );
			destination.add( source.get(sourceIndex * 3 + 2) );
    	}
    	else
    	{
    		destination.add( 0.0f );
    		destination.add( 0.0f );
    		destination.add( 0.0f );
    	}
	}
}
