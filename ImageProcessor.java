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
        int widthCountGray = 0;
        int widthCountColor = 0;
        int heightCount = 0;
        int colorAvg = 0;
        int tempRow = 0;
        int row, col, color;
        byte[][][] picArray = myPic.getRGBPixels();
        GVpicture checkerImage = new GVpicture(width, height);
        byte[][][] checkerArray = checkerImage.getRGBPixels();
        for (color = 0; color < picArray.length; color++) {
            tempRow = 0;
            widthCountGray = 0;
            heightCount = 0;


            for (row = 0; row < picArray[color].length; row++) {

                for (col = 0; col < picArray[color][row].length; col++) {
                    if (row > tempRow) {
                        heightCount++;
                        tempRow = row;
                    }


                    if ((heightCount < checkHeight) && (widthCountGray < checkerWidth)) {
                        colorAvg = byteAverage(picArray[0][row][col], picArray[1][row][col], picArray[2][row][col]);
                        checkerArray[color][row][col] = (byte) colorAvg;
                        widthCountGray++;
                    } else {
                        checkerArray[color][row][col] = picArray[color][row][col];
                        widthCountGray++;

                        if (widthCountGray == checkerWidth * 2) {
                            widthCountGray = 0;
                        }
                        if (heightCount == checkHeight * 2) {
                            heightCount = 0;
                        }
                    }

                }
            }
        }


        myPic.setImage(checkerImage);


    }


    private static int byteAverage(byte one, byte two, byte three) {
        int toReturn = ((one & 0xFF) + (two & 0xFF) + (three & 0xFF)) / 3;
        return toReturn;
    }

    public static void pixelate(GVpicture myPic) {
        final int width = myPic.getWidth();
        final int height = myPic.getHeight();
        int row, col, color;
        int prevAvg = 0;
        int runningAvg = 0;
        int count;
        byte[][][] picArray = myPic.getRGBPixels();
        GVpicture pixelated = new GVpicture(width, height);
        byte[][][] pixArray = pixelated.getRGBPixels();
        for (color = 0; color < picArray.length; color++) {
            for (row = 0; row < picArray[color].length; row++) {
                for (col = 0; col < picArray[color][row].length; col = col + 8) {


                    for (int upToEight = 0; upToEight <= 7; upToEight++) {
                        if (upToEight == 0) {
                            runningAvg = 0;
                            prevAvg = byteAverage(picArray[0][row][upToEight], picArray[1][row][upToEight], picArray[2][row][upToEight]);
                            int avg = byteAverage(picArray[0][row][upToEight + 1], picArray[1][row][upToEight + 1], picArray[2][row][upToEight + 1]);
                            runningAvg = (runningAvg + prevAvg + avg) / 3;
                        } else {
                            prevAvg = byteAverage(picArray[0][row][upToEight], picArray[1][row][upToEight], picArray[2][row][upToEight]);
                            int avg = byteAverage(picArray[0][row][upToEight + 1], picArray[1][row][upToEight + 1], picArray[2][row][upToEight + 1]);
                            runningAvg = (runningAvg + prevAvg + avg) / 3;
                        }

                    }

                    for (count = 0; count < 8; count++) {
                        pixArray[color][row][col + count] = (byte) runningAvg;

                    }


                }
            }
        }
        myPic.setImage(pixelated);


    }

    public static void reduce(GVpicture myPic) {
        final int width = myPic.getWidth();
        final int height = myPic.getHeight();
        int row, col, color;
        int countDown = 0;
        byte[][][] picArray = myPic.getRGBPixels();
        GVpicture reducedPic = new GVpicture(width, height);
        byte[][][] reducedArray = reducedPic.getRGBPixels();
        for (color = 0; color < picArray.length; color++) {
            for (row = 0; row < picArray[color].length; row = row + 2) {

                for (col = 0; col < picArray[color][row].length; col = col + 2) {

                    if (row == 0) {
                        reducedArray[color][row][col] = picArray[color][row][col];
                    } else {
                        reducedArray[color][row / 2][col / 2] = picArray[color][row][col];
                    }

                }


            }


        }
        myPic.setImage(copyImage(reducedPic, width, height));


    }

    private static GVpicture copyImage(GVpicture myPic, int width, int height) {
        int bigWidth = width;
        int bigHeight = height;
        int smallWidth = myPic.getWidth() / 2;
        int smallHeight = myPic.getHeight() / 2;
        int row, col, color;
        byte[][][] picArray = myPic.getRGBPixels();
        GVpicture outPic = new GVpicture(bigWidth, bigHeight);
        byte[][][] outArray = outPic.getRGBPixels();
        for (color = 0; color < picArray.length; color++) {
            for (row = 0; row < picArray[color].length; row++) {

                for (col = 0; col < picArray[color][row].length; col++) {

                    if (col < smallWidth && row < smallHeight) {
                        outArray[color][row][col] = picArray[color][row][col];
                    }
                    if (col > smallWidth && row < smallHeight) {
                        outArray[color][row][col] = picArray[color][row][col - smallWidth];
                    }
                    if (col > smallWidth && row > smallHeight) {
                        outArray[color][row][col] = picArray[color][row - smallHeight][col - smallWidth];
                    }
                    if (col < smallWidth && row > smallHeight) {
                        outArray[color][row][col] = picArray[color][row - smallHeight][col];
                    }

                }


            }


        }
        return outPic;
    }

    public static void invert(GVpicture myPic) {
        int width = myPic.getWidth();
        int height = myPic.getHeight();
        int row, col, color;
        int countDown = 0;
        byte[][][] picArray = myPic.getRGBPixels();
        GVpicture inverted = new GVpicture(width, height);
        byte[][][] invertArray = inverted.getRGBPixels();

        for (color = 0; color < picArray.length; color++) {
            for (row = 0; row < picArray[color].length; row++) {

                for (col = 0; col < picArray[color][row].length; col++) {

                    invertArray[color][row][col] = (byte) (0xFFFFFF - picArray[color][row][col]);


                }


            }
            myPic.setImage(inverted);

        }


    }
}