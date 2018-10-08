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


/**
 * Class to store and provide access to the render engine's material attributes.
 * @author Ursula Derichs
 * @version 1.0
 */
public class TextureState extends AbstRenderState
{
	public enum TexType
	{
	    AMBIENT,
	    BUMP,
	    DIFFUSE,
	    EMISSIVE,
	    NONE,
	    NORMAL,
	    REFLECTION,
	    SHININESS,
	    SPECULAR,
	    TRANSPARENCY
	};
	
	public enum TexTarget
	{
	    TEX_2D,
	    CUBE_MAP
	};
	
	enum CorrectionMode
	{
	    AFFINE,
	    PERSPECTIVE,
	    MAX
	};

	enum ApplyMode
	{
	    REPLACE,
	    DECAL,
	    MODULATE,
	    BLEND,
	    MAX
	};

	enum WrapMode
	{
	    CLAMP_S_CLAMP_T,
	    CLAMP_S_WRAP_T,
	    WRAP_S_CLAMP_T,
	    WRAP_S_WRAP_T,
	    MAX
	};

	enum MagFilterMode
	{
	    NEAREST,
	    LINEAR,
	    MAX
	};

	enum MinFilterMode
	{
	    NEAREST,
	    LINEAR,
	    NEAREST_NEAREST,
	    NEAREST_LINEAR,
	    LINEAR_NEAREST,
	    LINEAR_LINEAR,
	    MAX
	};
	
	private String 			texName;
	private TexType			texType;
	private TexTarget 		texTarget;
	private CorrectionMode 	correction;
	private ApplyMode      	apply;
	private Color          	blendColor;
	private WrapMode       	wrap;
	private MagFilterMode  	magFilter;
	private MinFilterMode  	minFilter;
	private float          	priority;

	public TextureState(String name, TexType type, TexTarget target)
	{
		setName(name);
		texName = name;
		texType = type;
		texTarget = target;
	}

	public TextureState(String name, TexType type)
	{
		this( name, type, TexTarget.TEX_2D);
	}
	
	public TextureState()
	{
		this( null, TexType.NONE, TexTarget.TEX_2D);
	}
	
	/**
	 * Returns the type of this render state.
	 * 
	 * @return the constant AbstRenderState::TEXTURE
	 */
	public renderType type()
	{
		return renderType.TEXTURE;
	}
	
	
	/**
	 * Returns the type of this render state.
	 * 
	 * @return the constant AbstRenderState::RS_MATERIAL
	 */
	public TexTarget target()
	{
		return texTarget;
	}


	public String getTextureName()
	{
		return texName;
	}
	
	public boolean hasTexture()
	{
		return (texName != null);
	}

	/** 
	 * Passes the material state to the renderer
	 * @see scenegraph.AbstSpatial#onDraw(renderer.AbstRenderer)
	 */
	public void onDraw(AbstRenderer renderer)
	{
	   	renderer.setTextureState(this);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
