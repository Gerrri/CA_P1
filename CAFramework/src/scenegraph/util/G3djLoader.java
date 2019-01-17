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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import math.Mat4;
import math.Quaternion;
import math.Vec3;
import math.function.FunctionR1Quaternion;
import math.function.FunctionR1Vec3;
import math.function.util.PolygonVec3;
import math.function.util.SphericalLinear;
import animation.AbstController;
import animation.JointController;
import scenegraph.AbstMeshData;
import scenegraph.AbstNode;
import scenegraph.AbstSpatial;
import scenegraph.Channel;
import scenegraph.Group;
import scenegraph.Joint;
import scenegraph.MaterialState;
import scenegraph.SkinCluster;
import scenegraph.TextureState;
import scenegraph.TriangleMesh;
import util.FileIO;
import util.Vec3Array;

public class G3djLoader
{

	private String fileName;

	enum TextureType
	{
		AMBIENT, BUMP, DIFFUSE, EMISSIVE, NONE, NORMAL, REFLECTION, SHININESS, SPECULAR, TRANSPARENCY
	}

	enum MeshType
	{
		TRIANGLES, LINES, POINTS, TRIANGLE_STRIP, LINE_STRIP
	}

	enum AttributeType
	{
		POSITION, NORMAL, COLOR, COLORPACKED, TANGENT, BINORMAL, TEXCOORD0, TEXCOORD1, TEXCOORD2, TEXCOORD3, TEXCOORD4, BLENDWEIGHT0, BLENDWEIGHT1, BLENDWEIGHT2, BLENDWEIGHT3
	}

	EnumMap<AttributeType, Integer> numPerAttribute = new EnumMap<AttributeType, Integer>(
			AttributeType.class);

	class Mesh
	{
		AttributeType[] attributes;
		int numWeights;
		float[] vertices;
		MeshPart[] parts;

	}

	class MeshPart
	{
		String id;
		int[] indices;
		MeshType type;
	}

	class Texture
	{
		String id;
		String filename;
		TextureType type;
		// float uvTranslation[];
		// float uvScaling[];
	}

	class Material
	{
		String id;
		Vec3 ambient;
		Vec3 diffuse;
		Vec3 emissive;
		Vec3 specular;
		Vec3 reflection;
		float shininess;
		float opacity;
		Texture[] textures;

		public Material()
		{
			id = "Material";
			ambient = new Vec3();
			diffuse = new Vec3();
			emissive = new Vec3();
			specular = new Vec3();
			reflection = new Vec3();
			shininess = 0;
			opacity = 1;
			textures = null;
		}
	}

	class Bone
	{
		String id;
		Vec3 translation;
		Quaternion rotation;
		Vec3 scale;
	}

	class NodePart
	{
		String meshPartId;
		String materialId;
		Bone[] bones;
		// int [] uvMapping;
	}

	class Node
	{
		String id;
		Vec3 translation;
		Quaternion rotation;
		Vec3 scale;
		NodePart[] meshes;
		Node[] children;

		public Node()
		{
			id = "Node";
			translation = new Vec3();
			rotation = new Quaternion();
			scale = new Vec3(1, 1, 1);
			meshes = null;
			children = null;
		}
	}

	class Keyframe
	{
		float keytime;
		Vec3 translation;
		Quaternion rotation;
		Vec3 scale;
	}

	class NodeAnimation
	{
		String id;
		Keyframe[] keyframes;
	}

	class Animation
	{
		String id;
		NodeAnimation[] bones;
		float endTime;
	}

	private Mesh[] meshes;
	private Material[] materials;
	private Node[] nodes;
	private Animation[] animations;

	HashMap<String, Joint> nodeCache = new HashMap<String, Joint>(100);
	HashSet<Joint> jointNodes = new HashSet<Joint>(100);
	ArrayList<String> allJoints = new ArrayList<String>(100);

	private Group object;
	private ArrayList<SkinCluster> skins = new ArrayList<SkinCluster>(100);
	private ArrayList<Group> meshSceneNodes;
	private ArrayList<TriangleMesh> triangleMeshes;
	private ArrayList<JointController> controllers;
	
	private Mat4 HeadRotation = new Mat4();
	
	public Group read(String name)
	{
		return read(name, false);
	}

	public Group read(String name, boolean bonesVisible)
	{
		object = new Group(name);
		numPerAttribute.put(AttributeType.POSITION, 3);
		numPerAttribute.put(AttributeType.NORMAL, 3);
		numPerAttribute.put(AttributeType.COLOR, 4);
		numPerAttribute.put(AttributeType.COLORPACKED, 1);
		numPerAttribute.put(AttributeType.TANGENT, 3);
		numPerAttribute.put(AttributeType.BINORMAL, 3);
		numPerAttribute.put(AttributeType.TEXCOORD0, 2);
		numPerAttribute.put(AttributeType.TEXCOORD1, 2);
		numPerAttribute.put(AttributeType.TEXCOORD2, 2);
		numPerAttribute.put(AttributeType.TEXCOORD3, 2);
		numPerAttribute.put(AttributeType.TEXCOORD4, 2);
		numPerAttribute.put(AttributeType.BLENDWEIGHT0, 2);
		numPerAttribute.put(AttributeType.BLENDWEIGHT1, 2);
		numPerAttribute.put(AttributeType.BLENDWEIGHT2, 2);
		numPerAttribute.put(AttributeType.BLENDWEIGHT3, 2);

		fileName = name;
		String path = FileIO.pathOfFbx(fileName);

		try
		{
			InputStream fis = new FileInputStream(path);

			// create JsonReader object
			JsonReader jsonReader = Json.createReader(fis);

			// get JsonObject from JsonReader
			JsonObject jsonObject = jsonReader.readObject();

			// close IO resource and JsonReader
			jsonReader.close();
			fis.close();

			if (jsonObject.containsKey("meshes"))
				meshes = getMeshes(jsonObject.getJsonArray("meshes"));

			if (jsonObject.containsKey("materials"))
				materials = getMaterials(jsonObject.getJsonArray("materials"));

			if (jsonObject.containsKey("nodes"))
				nodes = getNodes(jsonObject.getJsonArray("nodes"));

			if (jsonObject.containsKey("animations"))
				animations = getAnimations(
						jsonObject.getJsonArray("animations"));

			createMeshes();
			createSceneObjects(object, bonesVisible);
		} catch (Exception e)
		{
			System.err.println("*Error* Can't read G3dj file: " + fileName);
			e.printStackTrace();
		}

		return object;
	}

	void createMeshes()
	{
		int numClusters = meshes.length;
		triangleMeshes = new ArrayList<TriangleMesh>(numClusters);
		for (int indexMesh = 0; indexMesh < numClusters; indexMesh++)
		{
			Mesh mesh = meshes[indexMesh];
			int nPositionsPerVertex = 0;
			int numWeights = 0;

			// How many values are available per Vertex?
			for (AttributeType attribute : mesh.attributes)
			{
				int num = numPerAttribute.get(attribute);
				nPositionsPerVertex += num;
				if (attribute.name().startsWith("BLENDWEIGHT"))
				{
					numWeights++;
				}
			}

			int numVertices = mesh.vertices.length / nPositionsPerVertex;

			Vec3Array positions = new Vec3Array(numVertices);
			Vec3Array normals = new Vec3Array(numVertices);
			Vec3Array texcoords = new Vec3Array(numVertices);
			float[][] weights = new float[numVertices][numWeights];
			int[][] weightIndices = new int[numVertices][numWeights];

			float[] vertices = mesh.vertices;
			int k = 0;
			for (int i = 0; i < numVertices; i++)
			{
				for (AttributeType attribute : mesh.attributes)
				{
					int num = numPerAttribute.get(attribute);
					switch (attribute)
					{
					case POSITION:
						positions.push_back(vertices[k++], vertices[k++],
								vertices[k++]);
						break;
					case NORMAL:
						normals.push_back(vertices[k++], vertices[k++],
								vertices[k++]);
						break;
					case BLENDWEIGHT0:
						weightIndices[i][0] = (int) vertices[k++];
						weights[i][0] = vertices[k++];
						break;
					case BLENDWEIGHT1:
						weightIndices[i][1] = (int) vertices[k++];
						weights[i][1] = vertices[k++];
						break;
					case BLENDWEIGHT2:
						weightIndices[i][2] = (int) vertices[k++];
						weights[i][2] = vertices[k++];
						break;
					case BLENDWEIGHT3:
						weightIndices[i][3] = (int) vertices[k++];
						weights[i][3] = vertices[k++];
						break;
					case TEXCOORD0:
						texcoords.push_back(vertices[k++], vertices[k++], 0);
						break;
					default:
						k += num;
					}
				}
			}

			SkinCluster skinCluster = new SkinCluster(positions, weights,
					weightIndices, numWeights);
			MeshPart[] parts = mesh.parts;
			int numParts = parts.length;
			for (int i = 0; i < numParts; i++)
			{
				switch (parts[i].type)
				{
				case TRIANGLES:
					TriangleMesh tmesh = new TriangleMesh(skinCluster,
							parts[i].indices, normals, texcoords);
					tmesh.setName(parts[i].id);
					triangleMeshes.add(tmesh);
					break;
				case LINES:
					// create line mesh
					break;
				default:
					break;
				}
			}
		}
	}

	void createSceneObjects(Group objects, boolean bonesVisible)
	{
		meshSceneNodes = new ArrayList<Group>();
		for (Node node : nodes)
		{
			Group newObject = createGroup(node);
			objects.attachChild(newObject);
		}

		if (bonesVisible)
			visualizeJoints();

		controllers = new ArrayList<JointController>();
		if (animations != null)
		{
			for (Animation animation : animations)
			{
				controllers.addAll(createControllers(animation));
			}
		}

	}

	private ArrayList<JointController> createControllers(Animation animation)
	{
		int numJoints = animation.bones.length;
		ArrayList<JointController> controllers = new ArrayList<JointController>(
				numJoints);
		for (int i = 0; i < numJoints; i++)
		{
			NodeAnimation anim = animation.bones[i];
			Group joint = nodeCache.get(anim.id);
			Channel transChannel = joint.getChannel(AbstSpatial.TRANSLATION);
			Channel rotChannel = joint.getChannel(AbstSpatial.ROTATION);

			int numFrames = anim.keyframes.length;
			if (numFrames > 0)
			{
				// float duration = anim.keyframes[numFrames-1].keytime;

				Vec3[] translations = new Vec3[numFrames];
				Quaternion[] rotations = new Quaternion[numFrames];
				float[] grid = new float[numFrames];
				for (int j = 0; j < numFrames; j++)
				{
					Keyframe currentFrame = anim.keyframes[j];
					translations[j] = currentFrame.translation;
					rotations[j] = currentFrame.rotation;
					grid[j] = currentFrame.keytime;
				}

				FunctionR1Vec3 transFunc;
				if (translations[0] != null)
					transFunc = new PolygonVec3(translations, grid);
				else
					transFunc = null;

				FunctionR1Quaternion rotFunc;
				if (rotations[0] != null)
					rotFunc = new SphericalLinear(rotations, grid);
				else
					rotFunc = null;

				JointController jc = new JointController(transFunc, rotFunc,
						transChannel, rotChannel,
						AbstController.RepeatType.CYCLE, animation.endTime);
				jc.setName(anim.id);
				controllers.add(jc);
			}
		}

		return controllers;
	}

	private Group createGroup(Node node)
	{
		Joint newGroup = nodeCache.get(node.id);
		if (newGroup == null)
		{
			newGroup = new Joint(node.id);
			nodeCache.put(node.id, newGroup);
		}

		newGroup.setTranslation(node.translation);
		newGroup.setRotation(node.rotation);
		newGroup.setScale(node.scale);
		newGroup.updateTransform();
		if (node.children != null)
		{
			for (Node child : node.children)
			{
				newGroup.attachChild(createGroup(child));
			}
		}

		if (node.meshes != null)
		{
			for (NodePart nodePart : node.meshes)
			{
				newGroup.attachChild(createSkin(nodePart));
			}
			meshSceneNodes.add(newGroup);
		}

		return newGroup;
	}

	private Group createSkin(NodePart nodePart)
	{
		Group skin = new Group("Skin" + nodePart.meshPartId);
		String materialId = nodePart.materialId;
		for (Material material : materials)
		{
			if (material.id.equals(materialId))
			{
				MaterialState mat = new MaterialState();
				skin.attachChild(mat);
				mat.setName(materialId);
				mat.setAmbient(material.ambient);
				mat.setDiffuse(material.diffuse);
				mat.setEmissive(material.emissive);
				mat.setSpecular(material.specular);
				mat.setShininess(material.shininess);

				Texture[] textures = material.textures;
				if (textures != null)
				{
					for (Texture texture : textures)
					{
						if (texture.type == TextureType.DIFFUSE)
						{
							TextureState texState = new TextureState(
									texture.filename,
									TextureState.TexType.DIFFUSE);
							skin.attachChild(texState);
						}
					}
				}

				break;
			}
		}

		String meshId = nodePart.meshPartId;
		for (TriangleMesh tmesh : triangleMeshes)
		{
			if (tmesh.getName().equals(meshId))
			{
				TriangleMesh newmesh = new TriangleMesh(tmesh);
				SkinCluster s = ((SkinCluster) newmesh.meshData());
				if (nodePart.bones != null)
				{
					int numBones = nodePart.bones.length;
					ArrayList<Joint> jointlist = new ArrayList<Joint>(numBones);
					ArrayList<Mat4> jointBindings = new ArrayList<Mat4>(
							numBones);
					for (int j = 0; j < numBones; j++)
					{
						Bone bone = nodePart.bones[j];
						Joint joint = nodeCache.get(bone.id);
						if (joint == null)
						{
							joint = new Joint(bone.id);
							nodeCache.put(bone.id, joint);
						}
						allJoints.add(bone.id);
						Mat4 bindTransform = Mat4.scale(bone.scale).inverse();
						bindTransform.mul(new Mat4(
								bone.rotation.toRotationMatrix().inverse()));
						bindTransform.mul(
								Mat4.translation((bone.translation.negate())));
						jointBindings.add(bindTransform);
						jointlist.add(joint);
						jointNodes.add(joint);
					}
					s.setJoints(jointlist);
					s.setJointBindings(jointBindings);
					skins.add(s);
				}
				skin.attachChild(newmesh);
			}
		}

		return skin;
	}

	private void visualizeJoints()
	{
		float maxBoneLength = 0;

		for (Joint joint : jointNodes)
		{
			float boneLength = joint.getTranslation().length();
			if (boneLength > maxBoneLength)
				maxBoneLength = boneLength;
		}

		float scale = maxBoneLength / 40;

		for (Joint joint : jointNodes)
		{
			joint.attachBones(scale);
		}
	}

	private Mesh[] getMeshes(JsonArray jsonMeshes)
	{
		int numMeshes = jsonMeshes.size();
		Mesh[] meshes = new Mesh[numMeshes];

		int indexMesh = 0;
		for (JsonValue meshval : jsonMeshes)
		{
			JsonObject mesh = (JsonObject) meshval;
			meshes[indexMesh] = new Mesh();

			Mesh m = meshes[indexMesh];
			m.attributes = getAttributes(mesh.getJsonArray("attributes"));
			m.vertices = getVertices(mesh.getJsonArray("vertices"));
			m.parts = getParts(mesh.getJsonArray("parts"));

			indexMesh++;
		}

		return meshes;
	}

	private AttributeType[] getAttributes(JsonArray jsonAttributes)
	{
		int numAttributes = jsonAttributes.size();
		AttributeType[] attributes = new AttributeType[numAttributes];
		int indexAttributes = 0;
		for (JsonValue jsonAttribute : jsonAttributes)
		{
			String attribute = strip(jsonAttribute.toString());
			attributes[indexAttributes] = AttributeType.valueOf(attribute);
			indexAttributes++;
		}

		return attributes;
	}

	private float[] getVertices(JsonArray jsonVertices)
	{
		int numVertices = jsonVertices.size();
		float[] vertices = new float[numVertices];
		for (int i = 0; i < numVertices; i++)
		{
			JsonNumber num = jsonVertices.getJsonNumber(i);
			vertices[i] = (float) num.doubleValue();
		}

		return vertices;
	}

	private MeshPart[] getParts(JsonArray jsonParts)
	{
		int numParts = jsonParts.size();
		MeshPart[] parts = new MeshPart[numParts];
		for (int i = 0; i < numParts; i++)
		{
			JsonObject part = jsonParts.getJsonObject(i);
			parts[i] = new MeshPart();
			parts[i].id = part.getString("id");
			String type = part.getString("type");
			parts[i].type = MeshType.valueOf(type);
			parts[i].indices = getIndices(part.getJsonArray("indices"));
		}

		return parts;
	}

	private int[] getIndices(JsonArray jsonIndices)
	{
		int numIndices = jsonIndices.size();
		int[] indices = new int[numIndices];
		for (int i = 0; i < numIndices; i++)
		{
			JsonNumber num = jsonIndices.getJsonNumber(i);
			indices[i] = (int) num.intValue();
		}

		return indices;
	}

	private Material[] getMaterials(JsonArray jsonMaterials)
	{
		int numMaterials = jsonMaterials.size();
		Material[] materials = new Material[numMaterials];

		int indexMesh = 0;
		for (JsonValue matval : jsonMaterials)
		{
			JsonObject material = (JsonObject) matval;
			materials[indexMesh] = new Material();
			Material mat = materials[indexMesh];
			mat.id = material.getString("id");
			if (material.containsKey("diffuse"))
				mat.diffuse = getVec3(material.getJsonArray("diffuse"));

			if (material.containsKey("emissive"))
				mat.emissive = getVec3(material.getJsonArray("emissive"));

			if (material.containsKey("ambient"))
				mat.ambient = getVec3(material.getJsonArray("ambient"));

			if (material.containsKey("specular"))
				mat.specular = getVec3(material.getJsonArray("specular"));

			if (material.containsKey("reflection"))
				mat.reflection = getVec3(material.getJsonArray("reflection"));

			if (material.containsKey("shininess"))
				mat.shininess = getFloat(material.getJsonNumber("shininess"));

			if (material.containsKey("opacity"))
				mat.opacity = getFloat(material.getJsonNumber("opacity"));

			if (material.containsKey("textures"))
				mat.textures = getTextures(material.getJsonArray("textures"));

			// getTexture()

			indexMesh++;
		}

		return materials;
	}

	private Texture[] getTextures(JsonArray texArray)
	{
		int numTextures = texArray.size();
		Texture textures[] = new Texture[numTextures];

		int index = 0;
		for (JsonValue texval : texArray)
		{
			JsonObject jsonTex = (JsonObject) texval;
			textures[index] = new Texture();
			Texture texture = textures[index];

			texture.id = jsonTex.getString("id");
			texture.filename = jsonTex.getString("filename");
			String texType = jsonTex.getString("type");
			texture.type = TextureType.valueOf(texType);

			index++;
		}
		return textures;
	}

	private Vec3 getVec3(JsonArray xyz)
	{
		float[] f = new float[3];
		for (int i = 0; i < 3; i++)
		{
			JsonNumber num = xyz.getJsonNumber(i);
			f[i] = (float) num.doubleValue();
		}
		return new Vec3(f[0], f[1], f[2]);
	}

	private Quaternion getQuat(JsonArray xyzw)
	{
		float[] f = new float[4];
		for (int i = 0; i < 4; i++)
		{
			JsonNumber num = xyzw.getJsonNumber(i);
			f[i] = (float) num.doubleValue();
		}
		return new Quaternion(f[0], f[1], f[2], f[3]);
	}

	private float getFloat(JsonNumber v)
	{
		return (float) v.doubleValue();
	}

	private Node[] getNodes(JsonArray jsonNodes)
	{
		int numNodes = jsonNodes.size();
		Node[] nodes = new Node[numNodes];

		int indexMesh = 0;
		for (JsonValue nodval : jsonNodes)
		{
			JsonObject jsonNode = (JsonObject) nodval;
			nodes[indexMesh] = new Node();
			Node node = nodes[indexMesh];

			node.id = jsonNode.getString("id");

			if (jsonNode.containsKey("translation"))
				node.translation = getVec3(
						jsonNode.getJsonArray("translation"));

			if (jsonNode.containsKey("rotation"))
				node.rotation = getQuat(jsonNode.getJsonArray("rotation"));

			if (jsonNode.containsKey("scale"))
				node.scale = getVec3(jsonNode.getJsonArray("scale"));

			if (jsonNode.containsKey("parts"))
				node.meshes = getNodeParts(jsonNode.getJsonArray("parts"));

			if (jsonNode.containsKey("children"))
				node.children = getNodes(jsonNode.getJsonArray("children"));

			indexMesh++;
		}
		return nodes;
	}

	private NodePart[] getNodeParts(JsonArray jsonParts)
	{
		int numParts = jsonParts.size();
		NodePart[] meshes = new NodePart[numParts];
		for (int i = 0; i < numParts; i++)
		{
			JsonObject part = jsonParts.getJsonObject(i);
			meshes[i] = new NodePart();
			meshes[i].meshPartId = part.getString("meshpartid");
			meshes[i].materialId = part.getString("materialid");
			if (part.containsKey("bones"))
				meshes[i].bones = getBones(part.getJsonArray("bones"));

			// if (part.containsKey("uvMapping"))
			// getChildren (meshes[i].uvMapping,
			// part.getJsonArray("uvMapping"));

		}

		return meshes;
	}

	private Bone[] getBones(JsonArray jsonBones)
	{
		int numBones = jsonBones.size();
		Bone[] bones = new Bone[numBones];
		for (int i = 0; i < numBones; i++)
		{
			JsonObject bone = jsonBones.getJsonObject(i);
			bones[i] = new Bone();
			bones[i].id = bone.getString("node");

			if (bone.containsKey("translation"))
				bones[i].translation = getVec3(
						bone.getJsonArray("translation"));

			if (bone.containsKey("rotation"))
				bones[i].rotation = getQuat(bone.getJsonArray("rotation"));

			if (bone.containsKey("scale"))
				bones[i].scale = getVec3(bone.getJsonArray("scale"));
		}

		return bones;
	}

	private Animation[] getAnimations(JsonArray jsonAnimations)
	{
		int numAnimations = jsonAnimations.size();
		Animation[] animations = new Animation[numAnimations];
		for (int i = 0; i < numAnimations; i++)
		{
			JsonObject animation = jsonAnimations.getJsonObject(i);
			animations[i] = new Animation();
			animations[i].id = animation.getString("id");
			animations[i].endTime = 0;

			if (animation.containsKey("bones"))
			{
				animations[i].bones = getNodeAnimations(
						animation.getJsonArray("bones"));
				animations[i].endTime = getMax(animations[i].bones);
			}
		}

		return animations;
	}

	private float getMax(NodeAnimation[] nodeAnimations)
	{
		float max = 0;
		for (NodeAnimation anim : nodeAnimations)
		{
			float lastTime = anim.keyframes[anim.keyframes.length - 1].keytime;
			if (lastTime > max)
				max = lastTime;
		}
		return max;
	}

	private NodeAnimation[] getNodeAnimations(JsonArray jsonNodeAnimations)
	{
		int numNodeAnimations = jsonNodeAnimations.size();
		NodeAnimation[] nodeAnimations = new NodeAnimation[numNodeAnimations];
		for (int i = 0; i < numNodeAnimations; i++)
		{
			JsonObject nodeAnimation = jsonNodeAnimations.getJsonObject(i);
			nodeAnimations[i] = new NodeAnimation();
			nodeAnimations[i].id = nodeAnimation.getString("boneId");
			nodeAnimations[i].keyframes = getKeyframes(
					nodeAnimation.getJsonArray("keyframes"));
		}

		return nodeAnimations;
	}

	private Keyframe[] getKeyframes(JsonArray jsonKeyframes)
	{
		int numKeyframes = jsonKeyframes.size();
		Keyframe[] keyframes = new Keyframe[numKeyframes];
		for (int i = 0; i < numKeyframes; i++)
		{
			JsonObject keyframe = jsonKeyframes.getJsonObject(i);
			keyframes[i] = new Keyframe();
			keyframes[i].keytime = getFloat(keyframe.getJsonNumber("keytime"))
					/ 1000;

			if (keyframe.containsKey("translation"))
				keyframes[i].translation = getVec3(
						keyframe.getJsonArray("translation"));

			if (keyframe.containsKey("rotation"))
				keyframes[i].rotation = getQuat(
						keyframe.getJsonArray("rotation")).normalize();

			if (keyframe.containsKey("scale"))
				keyframes[i].scale = getVec3(keyframe.getJsonArray("scale"));
		}

		return keyframes;
	}

	public ArrayList<SkinCluster> getSkins()
	{
		return skins;
	}

	public ArrayList<TriangleMesh> getMeshes()
	{
		return triangleMeshes;
	}

	public ArrayList<JointController> getControllers()
	{
		return controllers;
	}

	public void retarget(BVHClip clip)
	{
		Joint bvhRoot = (Joint) clip.getSkeleton().getChild(1);
		String name = bvhRoot.getName();
		Joint fbxRoot = (Joint) object.searchNode(".*(:|_)?" + name);

		object.updateWorldTransform(new Mat4());
		setBindPose(fbxRoot);
		compensateScale(fbxRoot, 1);
		resetParents(fbxRoot);
		object.updateWorldTransform(new Mat4());

		for (JointController jc : clip.getControllers())
		{
			name = jc.getName();
			AbstNode node = fbxRoot.searchNode(".*:?" + name);
			if (node != null && node instanceof Group)
			{
				jc.setTransChannel(node.getChannel(AbstSpatial.TRANSLATION));
				jc.setRotChannel(node.getChannel(AbstSpatial.ROTATION));
			}
		}

		JointController root = JointController.getByName(clip.getControllers(),
				bvhRoot.getName());
		root.liftTranslation((fbxRoot.getTranslation()));

	}


	public void freezeRotationsBody(Joint root, Mat4 parentRotation)
	{
		object.updateWorldTransform(new Mat4()); 
		
		Vec3 trans = root.getTranslation();
		trans.transform(1, parentRotation);
		root.setTranslation(trans);

		Mat4 rotMatrix = new Mat4(root.getRotationMatrix());
		root.setRotationMatrix(new Mat4());
		Mat4 newParentRotation = Mat4.mul(parentRotation, rotMatrix);
		
//	the head as the root of the face must be rotated in the original bind pose, 
//	so that the keyframed animations fit
		if (root.getName().equals("Head"))
		{	
			Joint connect = new Joint ("Connector");
			connect.setTranslation (new Vec3());
			connect.setRotationMatrix (Mat4.inverse(newParentRotation));
			List<AbstNode> children = root.getChildren();

			for (int i = 0; i < children.size(); i++)
			{
				AbstNode child = children.get(i);
						
				if (child instanceof Joint)
				{
					connect.attachChild(child);
					child.setParent(connect);
				}
			}
			connect.setParent(root);
			root.detachAllChildren();
			root.attachChild(connect);
			
			return;
		}
		

		List<AbstNode> children = root.getChildren();

		if (children == null)
			return;

		for (int i = 0; i < children.size(); i++)
		{
			AbstNode child = children.get(i);
					
			if (child instanceof Joint)
				freezeRotationsBody((Joint) child, newParentRotation);
		}
	}

	/**
	 * Take current position and orientation of joints as binding pose
	 */
	public void recalculateBindings()
	{
		object.updateWorldTransform(new Mat4()); 
		
		for (TriangleMesh mesh : triangleMeshes)
		{
			TriangleMesh meshInObject = (TriangleMesh) object.searchNode(mesh.getName());
			if (meshInObject != null)
			{
				Mat4 mesh_transform = meshInObject.getGlobalTransform();
				AbstMeshData skin = meshInObject.meshData();
				if (skin instanceof SkinCluster)
				{
					((SkinCluster) skin).rebindJoints(mesh_transform);
				}
			}
		}
	}

	/**
	 * Reset all joints in their orientation and position so that the skeleton binding pose corresponds to 
	 * the BVH animation data produced by Mocap (with zero rotation in joints)
	 */
	public void setAnimation(Joint root, BVHClip clip)
	{
		freezeRotationsBody(root, new Mat4());
		recalculateBindings();
		
		for (JointController jc : clip.getControllers())
		{
			String name = jc.getName();
			AbstNode node = root.searchNode(".*:?" + name);
			if (node != null && node instanceof Group)
			{
				jc.setTransChannel(node.getChannel(AbstSpatial.TRANSLATION));
				jc.setRotChannel(node.getChannel(AbstSpatial.ROTATION));
			}
		}
	}

	
	public void overloadWithBVH (Joint root, BVHClip clip)
	{
		ArrayList<JointController> clipControllers = clip.getControllers();

		for (JointController contr : clipControllers)
		{
			String name = contr.getName();
			AbstNode node = root.searchNode(".*:?" + name);
			if (node != null && node instanceof Joint)
			{
				Quaternion q = new Quaternion(((Joint)node).getRotationMatrix());
				contr.setRotChannel(node.getChannel(AbstSpatial.ROTATION));
				contr.setTransChannel(node.getChannel(AbstSpatial.TRANSLATION));
				contr.startRotation(q);
			}
		}
		
		controllers.addAll(clipControllers);
	}
	
	public void setBindPose(Joint node)
	{
		node.updateWorldTransform(node.getParent().getGlobalTransform());

		List<AbstNode> children = node.getChildren();

		if (children == null)
			return;

		for (int i = 0; i < children.size(); i++)
		{
			AbstNode child = children.get(i);
			if (child instanceof Joint)
				setBindPose((Joint) child);
		}

	}

	public void compensateScale(AbstNode node, float scale)
	{
		if (node instanceof AbstSpatial)
		{
			AbstSpatial n = (AbstSpatial) node;

			Vec3 scaleV = new Vec3(n.getScale());
			n.getTranslation().mul(scale);

			if (meshSceneNodes.contains(n.getParent()))
			{
				n.setScale(scale, scale, scale);
			} else
				n.setScale(1, 1, 1);

			if (n instanceof Group)
			{

				List<AbstNode> children = ((Group) n).getChildren();

				if (children == null)
					return;

				for (int i = 0; i < children.size(); i++)
				{
					AbstNode child = children.get(i);

					compensateScale(child, scale * scaleV.x);
				}
			}
		}
	}
	
	public float getAnimationDuration()
	{
		if (animations.length > 0)
			return animations[0].endTime;
		else
			return 0;
	}
	
	public void setRepeatType(AbstController.RepeatType type)
	{
		for (AbstController controller : controllers)
			controller.setRepeatType(type);
	}

	private void resetParents(Group fbxNode)
	{
		Group parent = fbxNode.getParent();
		while (parent != object)
		{
			parent.setTranslation(0, 0, 0);
			parent.setRotation(0, 0, 0);
			parent = parent.getParent();
		}
	}

	private String strip(String str)
	{
		return str.substring(1, str.length() - 1);
	}
	

}