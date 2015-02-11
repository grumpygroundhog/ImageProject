/**
 * To be automatically recognized as an image manipulation method,
 * all the methods in this class must be declared static and
 * takes a GVpicture parameter.
 * <p/>
 * The GVpicture class provides several methods, but the ones that will
 * be used more frequently are:
 * <p/>
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
        int row, col, color;
        byte[][][] picArray = myPic.getRGBPixels();
        GVpicture flipped = new GVpicture(width, height);
        byte[][][] flippedArray = flipped.getRGBPixels();
        for (color = 0; color < picArray.length; color++) {
            for (row = 0; row < picArray[color].length; row++) {
                for (col = 0; col < picArray[color][row].length; col++) {


                    flippedArray[color][row][col] = picArray[color][row][picArray[color][row].length - col - 1];

                }
            }
        }
        myPic.setImage(flipped);


    }

    public static void copyTopHalf(GVpicture myPic) {
        int width = myPic.getWidth();
        int height = myPic.getHeight();
        int row, col, color;
        int countDown = 0;
        byte[][][] picArray = myPic.getRGBPixels();
        GVpicture flipped = new GVpicture(width, height);
        byte[][][] flippedArray = flipped.getRGBPixels();

        for (color = 0; color < picArray.length; color++) {
            for (row = 0; row < picArray[color].length; row++) {

                for (col = 0; col < picArray[color][row].length; col++) {

                    if (row < height / 2) {
                        flippedArray[color][row][col] = picArray[color][row][col];
                        countDown = row + 1;

                    } else {
                        if (col == 0) {
                            countDown--;
                        }
                        flippedArray[color][row][col] = picArray[color][countDown][col];

                        if (countDown == -1) {
                            countDown = row;
                        }
                    }
                }


            }


        }
        myPic.setImage(flipped);

    }

    public static void checkerBoard(GVpicture myPic) {
        final int width = myPic.getWidth();
        final int height = myPic.getHeight();
        final int checkerWidth = width / 8;
        final int checkHeight = height / 8;
        int widthCount = 0;
        int colorAvg= 0;
        int row, col, color;
        byte[][][] picArray = myPic.getRGBPixels();
        GVpicture flipped = new GVpicture(width, height);
        byte[][][] checkerArray = flipped.getRGBPixels();
        for (color = 0; color < picArray.length; color++) {
            for (row = 0; row < picArray[color].length; row++) {
                for (col = 0; col < picArray[color][row].length; col++) {

                    if(col <= widthCount && widthCount < 2)
                    {
                        colorAvg = byteAverage(picArray[0][row][col], picArray[1][row][col], picArray[2][row][col]);
                        checkerArray[color][row][col] = (byte)colorAvg;
                        widthCount++;
                    }
                    else
                    {

                    }


                }
            }
        }
        myPic.setImage(flipped);


    }


    private static int byteAverage(byte one, byte two, byte three) {
        int toReturn = ((one & 0xFF) + (two & 0xFF) + (three & 0xFF))/3;
        return toReturn;
    }


}