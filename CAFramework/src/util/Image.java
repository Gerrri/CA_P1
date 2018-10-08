package util;

//import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.GL_R8;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.EXTAbgr;


public class Image
{
	private String imageName;
	private int width, height;
	private int internalFormat;
	private int format;
	private ByteBuffer imageBuffer;
	private BufferedImage img;

	public Image (String filename)
	{
		// load image from file
		loadImage(filename);
	}
	
	
	/**
	 * Sets the size of the contained image.
	 * The image data stored beforehand will be lost.
	 * @param width	new width of the image in pixels
	 * @param height	new width of the image in pixels
	 */

	public void setSize(int width, int height)
	{
    	this.width = width;
		this.height = height;
	}


	/**
	 * Reads an image from a file.
	 * @param filename	name of the image file to read
	 * @return true, if the image file was successfully read,
	 *         false, otherwise
	 */

	public boolean loadImage(String filename)
	{
		boolean tga = false;
		String path = FileIO.pathOfTextures(filename);
		try
		{		
			if( filename.endsWith(".tga") )
			{
				img = loadTGA( new File(path) );
				tga = true;
			}
			else
			{
				img = ImageIO.read( new File(path) );
			}
			
			width = img.getWidth();
			height = img.getHeight();
			
			DataBuffer dataBuffer = img.getRaster().getDataBuffer();
			byte[]     data       = null;
			
			if( dataBuffer.getDataType() == DataBuffer.TYPE_BYTE )
			{
				data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
			}
			else if( dataBuffer.getDataType() == DataBuffer.TYPE_INT )
			{
				int[] intData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
				
				int srcLength = intData.length;
				data = new byte[srcLength << 2];
			    
			    for( int i = 0; i < srcLength; ++i )
			    {
			        int x = intData[i];
			        int j = i << 2;
			        data[j++] = (byte) ((x >>> 0) & 0xff);           
			        data[j++] = (byte) ((x >>> 8) & 0xff);
			        data[j++] = (byte) ((x >>> 16) & 0xff);
			        data[j++] = (byte) ((x >>> 24) & 0xff);
			    }
			}
			else
			{
				System.err.println( "TexturePool : image datatype not supported!" );
				return false;
			}
			
			
			imageBuffer = ByteBuffer.allocateDirect( data.length );
		    imageBuffer.order( ByteOrder.nativeOrder() );
		    
		    if (!tga)
		    {
			    // vertically flip the image by storing the buffer data upside-down
			    int line = 3*width;
			    int offset = line*height - line;
			    while ( offset >= 0)
			    {
			    	imageBuffer.put(data, offset, line);
			    	offset -= line;
			    }
		    }
		    else
		    {
		    	imageBuffer.put( data, 0, data.length );
		    }
		    
		    imageBuffer.flip();
		    
		    determineFormats();
		    return true;
		    
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return false;
		}
	}
		
	
	private void determineFormats()
	{
	    internalFormat = GL_RGB;
	    format         = GL_RGB;
	    
	    switch( img.getType() )
	    {
	    	case BufferedImage.TYPE_BYTE_GRAY:
	    	{
	    		internalFormat = GL_R8;
	    		format         = GL_RED;
	    		break;
	    	}
	    	case BufferedImage.TYPE_3BYTE_BGR:
	    	{
	    		internalFormat = GL_RGB8;
	    		format         = GL_BGR;
	    		break;
	    	}
	    	case BufferedImage.TYPE_4BYTE_ABGR:
	    	{
	    		internalFormat = GL_RGBA8;
	    		format         = EXTAbgr.GL_ABGR_EXT;
	    		break;
	    	}
	    	case BufferedImage.TYPE_INT_BGR:
	    	{
	    		internalFormat = GL_RGB8;
	    		format         = GL_BGR;
	    		break;
	    	}
	    	case BufferedImage.TYPE_INT_RGB:
	    	{
	    		internalFormat = GL_RGB8;
	    		format         = GL_RGB;
	    		break;
	    	}
	    	case BufferedImage.TYPE_INT_ARGB:
	    	{
	    		internalFormat = GL_RGBA8;
	    		format         = GL_RGBA;
	    		System.err.println( "ARGB Textures are not supported, please convert texture " + imageName );
	    		break;
	    	}
	    	default:
	    		break;
	    }
		
	}


	/**
	 * Returns the width of the image in pixels.
	 * @return width of the image in pixels
	 */

	public int getWidth() 
	{
	    return width;
	}

	/**
	 * Returns the height of the image in pixels.
	 * @return height of the image in pixels
	 */

	public int getHeight()
	{
		return height;
	}

	/**
	 * Returns the color depth and color format of the image.
	 * @return color depth and format type
	 */

	public int getType() 
	{
	    return img.getType();
	}


	/**
	 * Returns a pointer to the raw image data.
	 * Pixels are stored in lines from left to right, bottom to top.
	 * @return pointer to raw image data
	 */

	public ByteBuffer getData()
	{
		    return imageBuffer;
	}
	

	/**
	 * Returns the file name of the image.
	 * This will be an empty string if the image was neither read nor
	 * written to a file.
	 * @return file name
	 */

	public String imageName() 
	{
		return imageName;
	}

	/**
	 * Sets the file name of the image
	 * This function is used by the Maya plugin to set a file name without
	 * the need of dealing with actual files.
	 * @param name	new file name
	 */

	public void setImageName(String name)
	{
		imageName = name;
	}


	public int getInternalFormat()
	{
		return internalFormat;
	}


	public void setInternalFormat(int internalFormat)
	{
		this.internalFormat = internalFormat;
	}


	public int getFormat()
	{
		return format;
	}


	public void setFormat(int format)
	{
		this.format = format;
	}



	private static int offset;

	private static int btoi(byte b)
	{
		int a = b;
		return (a < 0 ? 256 + a : a);
	}

	private static int read(byte[] buf)
	{
		return btoi(buf[offset++]);
	}

	public static BufferedImage loadTGA(File file) throws IOException
	{
		byte[] buf = new byte[(int) file.length()];

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		bis.read(buf);
		bis.close();

		offset = 0;

		// Reading header bytes
		// buf[2]=image type code 0x02=uncompressed BGR or BGRA
		// buf[12]+[13]=width
		// buf[14]+[15]=height
		// buf[16]=image pixel size 0x20=32bit, 0x18=24bit
		// buf[17]=Image Descriptor Byte=0x28 (00101000)=32bit/origin
		// upperleft/non-interleaved
		for (int i = 0; i < 12; i++)
			read(buf);
		int width = read(buf) + (read(buf) << 8); // 00,04=1024
		int height = read(buf) + (read(buf) << 8); // 40,02=576
		read(buf);
		read(buf);

		int n = width * height;
		int[] pixels = new int[n];
		int idx = 0;

		if (buf[2] == 0x02 && buf[16] == 0x20)
		{ // uncompressed BGRA
			while (n > 0)
			{
				int b = read(buf);
				int g = read(buf);
				int r = read(buf);
				int a = read(buf);
				int v = (a << 24) | (r << 16) | (g << 8) | b;
				pixels[idx++] = v;
				n -= 1;
			}
		} else if (buf[2] == 0x02 && buf[16] == 0x18)
		{ // uncompressed BGR
			while (n > 0)
			{
				int b = read(buf);
				int g = read(buf);
				int r = read(buf);
				int a = 255; // opaque pixel
				int v = (a << 24) | (r << 16) | (g << 8) | b;
				pixels[idx++] = v;
				n -= 1;
			}
		} else
		{
			// RLE compressed
			while (n > 0)
			{
				int nb = read(buf); // num of pixels
				if ((nb & 0x80) == 0)
				{ // 0x80=dec 128, bits 10000000
					for (int i = 0; i <= nb; i++)
					{
						int b = read(buf);
						int g = read(buf);
						int r = read(buf);
						pixels[idx++] = 0xff000000 | (r << 16) | (g << 8) | b;
					}
				} else
				{
					nb &= 0x7f;
					int b = read(buf);
					int g = read(buf);
					int r = read(buf);
					int v = 0xff000000 | (r << 16) | (g << 8) | b;
					for (int i = 0; i <= nb; i++)
						pixels[idx++] = v;
				}
				n -= nb + 1;
			}
		}

		BufferedImage bimg = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		bimg.setRGB(0, 0, width, height, pixels, 0, width);
		return bimg;
	}

}

