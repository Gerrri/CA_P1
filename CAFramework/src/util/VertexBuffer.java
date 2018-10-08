package util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;




/**
 * A collection of convenience methods that should be in BufferUtils but aren't
 */
public class VertexBuffer 
{
	public int VAOid;
	public int verticesBufferID;
	public int facesBufferID;
	public int normalsBufferID;
	public int texCoordBufferID;
	public int weightsBufferID;
	public int weightIndicesBufferID;
	
	private final int VERTICES = 0;
	private final int NORMALS = 1;
	private final int TEXCOORDS = 2;
	private final int WEIGHTS = 3;
	private final int WEIGHTINDICES = 4;

	public VertexBuffer()
	{
		verticesBufferID = 0;
		facesBufferID = 0;
		normalsBufferID = 0;
		texCoordBufferID = 0;
		weightsBufferID = 0;
		weightIndicesBufferID = 0;
		VAOid = 0;
	}
	
	public boolean isEmpty()
	{
		return VAOid == 0;
	}
	
	public void useVAO()
	{
		glBindVertexArray(VAOid);
	}
	
	public void useIndices()
	{
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, facesBufferID);
	}
	
	public void destroy()
	{
		glDisableVertexAttribArray(VERTICES);
		glDisableVertexAttribArray(NORMALS);
		glDisableVertexAttribArray(TEXCOORDS);
		glDisableVertexAttribArray(WEIGHTS);
		glDisableVertexAttribArray(WEIGHTINDICES);
	}
	
	public void createVAO()
	{
		if (VAOid != 0)
			glDeleteVertexArrays( VAOid );
		
		VAOid = glGenVertexArrays();
	}
	
	public void loadVertices(FloatBuffer vertices)
	{
		glBindVertexArray(VAOid);
		
		if (verticesBufferID == 0)
			verticesBufferID = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, verticesBufferID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

		glEnableVertexAttribArray(VERTICES);
		glVertexAttribPointer(VERTICES, 3, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public void loadNormals(FloatBuffer normals)
	{
		glBindVertexArray(VAOid);
		
		if (normalsBufferID == 0)
			normalsBufferID = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, normalsBufferID);
		glBufferData(GL_ARRAY_BUFFER, normals, GL_DYNAMIC_DRAW);
		
		glEnableVertexAttribArray(NORMALS);
		glVertexAttribPointer(NORMALS, 3, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public void loadFaces(int [] indices)
	{
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices, 0, indices.length);
		indexBuffer.flip();
		
		glBindVertexArray(VAOid);
		
		if (facesBufferID == 0)
			facesBufferID = glGenBuffers();

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,facesBufferID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_DYNAMIC_DRAW);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public void loadTexcoord(FloatBuffer texcoord)
	{
		glBindVertexArray(VAOid);
		
		if (texCoordBufferID == 0)
			texCoordBufferID = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, texCoordBufferID);
		glBufferData(GL_ARRAY_BUFFER, texcoord, GL_DYNAMIC_DRAW);

		glEnableVertexAttribArray(TEXCOORDS);
		glVertexAttribPointer(TEXCOORDS, 3, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);		
	}
	
	public void loadWeights(float[][] weights, int numWeights, int numVertices)
	{
		int len = 4*numVertices;
		float[] weightsStreamlined = new float [len];
		int k=0;
		for (int i=0; i < numVertices; i++)
		{
			for (int j=0; j < numWeights; j++)
			{
				weightsStreamlined[k++] = weights[i][j];
			}
			for (int l=numWeights; l < 4; l++)
			{
				weightsStreamlined[k++] = 0;
			}
		}
		
		FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(len);
		weightBuffer.put(weightsStreamlined);
		weightBuffer.flip();
		glBindVertexArray(VAOid);
		
		if (weightsBufferID == 0)
			weightsBufferID = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, weightsBufferID);
		glBufferData(GL_ARRAY_BUFFER, weightBuffer, GL_DYNAMIC_DRAW);

		glEnableVertexAttribArray(WEIGHTS);
		glVertexAttribPointer(WEIGHTS, 4, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);			
	}
	
	
	public void loadWeightIndices(int [][] indices, int numWeights, int numVertices)
	{
		int len = 4*numVertices;
		int[] indicesStreamlined = new int [len];
		int k=0;
		for (int i=0; i < numVertices; i++)
		{
			for (int j=0; j < numWeights; j++)
			{
				indicesStreamlined[k++] = indices[i][j];
			}
			for (int l=numWeights; l < 4; l++)
			{
				indicesStreamlined[k++] = 0;
			}
		}
		
		IntBuffer weightIndices = BufferUtils.createIntBuffer(len);
		weightIndices.put(indicesStreamlined);
		weightIndices.flip();
		
		glBindVertexArray(VAOid);
		
		if (weightIndicesBufferID == 0)
			weightIndicesBufferID = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, weightIndicesBufferID);
		glBufferData(GL_ARRAY_BUFFER, weightIndices, GL_DYNAMIC_DRAW);

		glEnableVertexAttribArray(WEIGHTINDICES);
		glVertexAttribPointer(WEIGHTINDICES, 4, GL_INT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);		
	}
}
