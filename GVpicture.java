import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;


/**
 * This class is written as a "playground" for CS1/CS2 students to 
 * exercise 2D array manipulation applied to image processing..... 
 * 
 * @author Hans Dulimarta
 * @version Winter 2008
 */
public class GVpicture extends JPanel {
    /** constant for images retrieved from the web */
    public final static int WEB_IMAGE = 1;
    
    /** constant for images loaded from the local computre */
    public final static int LOCAL_IMAGE = 2;
    
    /** a type constant for color images */
    public final static int RGB_TYPE = BufferedImage.TYPE_INT_RGB;
    
    /** a type constant for grayscale images */
    public final static int GRAY_TYPE = BufferedImage.TYPE_BYTE_GRAY;
    
    private BufferedImage image;
    private String extension;
    private boolean imageIsSet;
    private int type;
    
    /* the following array stores the pixel values of the picture as
     * a 3D array. data[0] is the red layer, data[1] is the green layer,
     * and data[2] is the blue layer. Each layer is a two-dimensional
     * array of unsigned byte values
     */
    private byte[][][] data;
    
    /**
     * Constructor to create an empty RGB image of a given size
     * @param width   width of the image (in pixels)
     * @param height  height of the image (in pixels)
     */
    public GVpicture (int width, int height)
    {
    	this (width, height, RGB_TYPE);
    }

    /**
     * Constructor to create an empty color or grayscale image of a given size
     * @param width   width of the image (in pixels)
     * @param height  height of the image (in pixels)
     * @param type    type (RGB_TYPE or GRAY_TYPE)
     */
    public GVpicture (int width, int height, int type)
    {
    	if (type == RGB_TYPE || type == GRAY_TYPE)
    		image = new BufferedImage (width, height, type);
    	else 
    		throw new IllegalArgumentException (
    				"Image type must be either " +
    				"GVPicture.RGB_TYPE or GVPicture.GRAY_TYPE");
    	this.type = type;
    	imageToArray();
        extension = "jpg";
        imageIsSet = false;
        setPreferredSize(new Dimension(width + 12, height + 12));
        setBorder (BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    }
    
    /**
     * Retrieve the image type (RGB_TYPE or GRAY_TYPE)
     * @return the type of this image
     */
    public int getType ()
    {
    	return type;
    }
    
    /** 
     * Get the height of this image (in pixels)
     */
    public int getHeight ()
    {
        return image.getHeight();
    }
    
    /** 
     * Get the width of this image (in pixels)
     */
    public int getWidth ()
    {
        return image.getWidth();
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension (image.getWidth() + 12, 
            image.getHeight() + 12);
    }

    /**
     * Constructor
     * @param filename  the name of a file found locally on this computer
     *                  or the URL of an image on the internet
     * @param sourceType used either WEB_IMAGE or LOCAL_IMAGE constant above
     */
    public GVpicture(String filename, int sourceType) {
        try {
            if (sourceType == LOCAL_IMAGE)
                image = ImageIO.read(new File(filename));
            else
                image = ImageIO.read(new URL(filename));
          	data = new byte[3][image.getHeight()][image.getWidth()];
            imageToArray();
        } catch (IOException e) {
            System.err.printf("Can't read image file ", filename);
        }
    }
    
    /**
     * Save the picture to a local file
     * @param outfile the name of the image
     */
    public void save (String outfile)
    {
        int dotpos;
        String outExtension = extension;
        
        dotpos = outfile.lastIndexOf(".");
        if (dotpos > 0)
            outExtension = outfile.substring(dotpos+1);
        try {
            arrayToImage();
            ImageIO.write(image, outExtension, new File(outfile));
        } catch (IOException e) {
            System.err.println ("Could not write the image to " + outfile);
        }
    }

    
    /**
     * Get a reference to the 2D array of gray level values.
     * 
     * @return the address of the 2D array object
     */
    public byte[][] getGrayPixels()
    {
    	return data[0];
    }
    
    /**
     * Get a reference to a 3D array of the pixel values. The dimension 
     * the array is data[3][height][width].
     * 
     * @return
     */
    public byte[][][] getRGBPixels ()
    {
        return data;
    }

    /**
     * Get a reference to a 2D array of the RED pixels. The dimension 
     * the array is data[height][width].
     */
    public byte[][] getRedPixels ()
    {
        return type == RGB_TYPE ? data[0] : null;
    }
    
    /**
     * Get a reference to a 2D array of the GREEN pixels. The dimension 
     * the array is data[height][width].
     */
    public byte[][] getGreenPixels ()
    {
        return type == RGB_TYPE ? data[1] : null;
    }
    
    /**
     * Get a reference to a 2D array of the BLUE pixels. The dimension 
     * the array is data[height][width].
     */
    public byte[][] getBluePixels ()
    {
        return type == RGB_TYPE ? data[2] : null;
    }

    /**
     * Get the current image of the picture
     * 
     * @return a reference to the current image object
     */
    public BufferedImage getImage ()
    {
        return image;
    }
    
    /**
     * Set the image of this picture to an image of another picture
     * @param pic a reference to the other picture
     */
    public void setImage (GVpicture pic)
    {
        image = new BufferedImage (pic.getImage().getWidth(),
                pic.getImage().getHeight(), pic.getType());
        imageToArray();
        type = pic.type;
        int i, j, k;
        
        //if (type == RGB_TYPE)
        //{
        	byte[][][] picData = pic.getRGBPixels();
			for (i = 0; i < 3; i++)
				for (j = 0; j < image.getHeight(); j++)
					for (k = 0; k < image.getWidth(); k++)
						data[i][j][k] = picData[i][j][k];
        /*}
        else {
        	byte[][] picData = pic.getGrayPixels();
        	for (j = 0; j < image.getHeight(); j++)
        		for (k = 0; k < image.getWidth(); k++)
        			gdata[j][k] = picData[j][k];
        }
        */
        imageIsSet = true;
        repaint();
    }
    
    /**
     * Set the image of this picture from a local file (.jpg, .gif, .png)
     * @param f
     */
    public void setImage (File f)
    {
        imageIsSet = true;
        try {
            image = ImageIO.read(f);
            switch (image.getColorModel().getColorSpace().getType())
            {
            case ColorSpace.TYPE_RGB:
            	type = RGB_TYPE;
            	break;
            case ColorSpace.TYPE_GRAY:
            	type = GRAY_TYPE;
            	break;
            default:
            	throw new IllegalArgumentException(
            			"Unsupported colorspace " + image.getColorModel().
            			getColorSpace().getType());
            }
            imageToArray();
            repaint();
        } catch (IOException e) {
            System.err.println("Error reading image file" + f.getName());
        }
    }
    
    
    /**
     * Set the image of this picture from an image on the Internet.
     * @param the URL of the target image
     */
    public void setImage (URL addr)
    {
        imageIsSet = true;
        try {
            image = ImageIO.read(addr);
            switch (image.getColorModel().getColorSpace().getType())
            {
            case ColorSpace.TYPE_RGB:
            	type = RGB_TYPE;
            	break;
            case ColorSpace.TYPE_GRAY:
            	type = GRAY_TYPE;
            	break;
            default:
            	throw new IllegalArgumentException(
            			"Unsupported colorspace " + image.getColorModel().
            			getColorSpace().getType());
            }
            imageToArray();
            repaint();
        } catch (IOException e) {
            System.err.println("Error downloading image from" + addr);
        }
        
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            arrayToImage();
            g.drawImage(image, 0, 0, null);
        }
        else {
        //if (!imageIsSet) {
            g.setColor(Color.YELLOW);
            FontMetrics fm = g.getFontMetrics();
            g.drawString("No image", 
                    (image.getWidth() - fm.stringWidth("No image"))/2, 
                    image.getHeight()/2);
        }
    }
    
    /**
     * copy the image to a local array
     */
    private void imageToArray ()
    {
        int i, j;
        //if (type == RGB_TYPE) {
			data = new byte[3][image.getHeight()][image.getWidth()];
			for (i = 0; i < image.getHeight(); i++) {
				for (j = 0; j < image.getWidth(); j++) {
					int pixel = image.getRGB(j, i);
					data[2][i][j] = (byte) ((pixel >> 0) % 256);
					data[1][i][j] = (byte) ((pixel >> 8) % 256);
					data[0][i][j] = (byte) ((pixel >> 16) % 256);
				}
			}
		//}
    }
    
    /**
     * use the RGB array as the image data 
     */
    private void arrayToImage ()
    {
    	if (image == null || image.getHeight() != data[0].length
    			|| image.getWidth() != data[0][0].length) {
    		image = new BufferedImage(data[0][0].length, data[0].length,
    				BufferedImage.TYPE_INT_RGB);
    	}
    	if (type == RGB_TYPE) {
			for (int i = 0; i < image.getHeight(); i++) {
				for (int j = 0; j < image.getWidth(); j++) {
					int pixel;
					pixel = (data[0][i][j] << 16) & 0x00FF0000
							| (data[1][i][j] << 8) & 0x0000FF00
							| (data[2][i][j] & 0x000000FF);
					image.setRGB(j, i, pixel);
				}
			}
		}
    	else {
			for (int i = 0; i < image.getHeight(); i++) {
				for (int j = 0; j < image.getWidth(); j++) {
					int pixel;
					pixel = (data[0][i][j] << 16) & 0x00FF0000
							| (data[0][i][j] << 8) & 0x0000FF00
							| (data[0][i][j] & 0x000000FF);
					image.setRGB(j, i, pixel);
				}
			}
    	}
    }
}
