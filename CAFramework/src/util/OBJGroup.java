package util;



public class OBJGroup
{
	private Vec3Array positions;
	private Vec3Array normals;
	private Vec3Array texCoords;

	private int [] indices; 
	private OBJMaterial material; 
	private String name;

	
	public OBJGroup( Vec3Array positions, Vec3Array normals, Vec3Array texCoords, int[] indices, OBJMaterial material, String name )
	{
		this.positions = positions;
		this.normals   = normals;
		this.texCoords = texCoords;
		this.indices  = indices;
		this.material = material;
		this.name = name;
	}
	
	
	public Vec3Array getPositions()
	{
		return positions;
	}
	
	
	public Vec3Array getNormals()
	{
		return normals;
	}
	
	
	public Vec3Array getTexCoords()
	{
		return texCoords;
	}
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	public int[] getIndices() {
		return indices;
	}


	public void setIndices(int[] indices) {
		this.indices = indices;
	}


	public OBJMaterial getMaterial() {
		return material;
	}


	public void setMaterial(OBJMaterial material) {
		this.material = material;
	}


}
