/**
 * To be automatically recognized as an image manipulation method,
 * all the methods in this class must be declared static and
 * takes a GVpicture parameter.
 *
 * The GVpicture class provides several methods, but the ones that will
 * be used more frequently are:
 *
 * getWidth(): returns an int
 * getHeight(): returns an int
 * getRGBPixels(): returns byte[][][]   (a 3D array of bytes)
 * getRedPixels(), getGreenPixels(), getBluePixels(): return byte[][] (a 2D array of bytes)
 */
public class ImageProcessor {

    /* The following method show only the red channel of an image
     * by "masking" its green and blue channel
     * @param myPic a reference to the input image 
     */

    public static void showRedOnly(GVpicture myPic) {

        byte[][] green = myPic.getGreenPixels();
        byte[][] blue = myPic.getBluePixels();

        int row, col;
        for (row = 0; row < myPic.getHeight(); row++) {
            for (col = 0; col < myPic.getWidth(); col++) {
                green[row][col] = 0;
                blue[row][col] = 0;
            }
        }
    }

    public static void placeRedBorder(GVpicture myPic) {
        final int C = myPic.getWidth();
        final int R = myPic.getHeight();
        byte[][] rLayer = myPic.getRedPixels();
        byte[][] gLayer = myPic.getGreenPixels();
        byte[][] bLayer = myPic.getBluePixels();

        /* place red line at the top & bottom */
        for (int k = 0; k < C; k++) {
            /* place red pixels at the top row */
            for (int m = 0; m < 8; m++) {
                rLayer[m][k] = (byte) 255;
                bLayer[m][k] = (byte) 0;
                gLayer[m][k] = (byte) 0;
            }

            /* place red pixels at the bottom row */
            for (int m = 1; m <= 8; m++) {
                rLayer[R - m][k] = (byte) 255;
                bLayer[R - m][k] = (byte) 0;
                gLayer[R - m][k] = (byte) 0;
            }
        }

        for (int m = 0; m < R; m++) {
            /* 8-pixel thick left border */
            for (int k = 0; k < 8; k++) {
                rLayer[m][k] = (byte) 255;
                bLayer[m][k] = gLayer[m][k] = 0;
            }
            
            /* 8-pixel thick right border */
            for (int k = 1; k <= 8; k++) {
                rLayer[m][C - k] = (byte) 255;
                bLayer[m][C - k] = gLayer[m][C - k] = 0;
            }
        }
    }

    public static void flipVertical(GVpicture myPic) {
        final int width = myPic.getWidth();
        final int height = myPic.getHeight();
        GVpicture flipped = new GVpicture(width,height);
        int row, col, color;
        byte[][][] picArray = myPic.getRGBPixels();
        //byte[][][] picArrayTemp;
        //picArrayTemp = picArray;

        for (color = 0; color < 3; color++) {
            for (row = 0; row < picArray[color].length; row++) {
                for (col = 0; col < picArray[color][row].length; col++) {


                        byte temp = picArray[color][row][col];
                        picArray[color][row][col] = picArray[color][row][picArray[color][row].length - col - 1];
                        picArray[color][row][picArray[color][row].length -col -1] = temp;
                        //picArray[color][row][picArray.length -i -1] = temp;

                }
            }
        }

    }
}