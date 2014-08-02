import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


public class processorHist {

	private Scalar colorWithHighest = new Scalar(0);
    private String nameColorWithHighest = "";
    private double valueColorWithHighest = 0;
    private double[] histValues = new double[25];
    private int icoun = 0;
    private Mat threshCombined = new Mat();
    private Mat threshLight = new Mat();
    private Mat threshDark = new Mat();
    
    public String getNameColorWithHighest()
    {
    	return this.nameColorWithHighest;
    }
    
    public double getValueColorWithHighest()
    {
    	return this.valueColorWithHighest;
    }
    
    public double getHistValue(int index)
    {
    	return this.histValues[index];
    }
    
    public Mat getThreshCombined()
    {
    	return this.threshCombined.clone();
    }
    
    public Mat getThreshLight()
    {
    	return this.threshLight.clone();
    }
    
    public Mat getThreshDark()
    {
    	return this.threshDark.clone();
    }
    
    public void printThreshLight()
    {
    	
    }
    
    public void printThreshDark()
    {
    	
    }
    
    public void printThreshCombined()
    {
    	
    }
    
    public Mat detect(Mat inputframe){ 
    	
         Mat mRgba=new Mat();
         inputframe.copyTo(mRgba);
         
         ArrayList<Mat> bgr_planes = new ArrayList<Mat>();
         Core.split(mRgba, bgr_planes);
         
         Size sizeRgba = mRgba.size();
         Point mP1 = new Point();
         Point mP2 = new Point();
         
         Mat hist = new Mat();
         
         int mHistSizeNum = 25;
         Mat mIntermediateMat = new Mat();
         MatOfInt[] mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
         float[] mBuff = new float[mHistSizeNum];
         MatOfInt mHistSize = new MatOfInt(mHistSizeNum);
         MatOfFloat mRanges = new MatOfFloat(0f, 256f);
         Mat mMat0 = new Mat();
         
         int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
         if(thikness > 5) thikness = 5;
         int offset = (int) ((sizeRgba.width - (5*mHistSizeNum + 4*10)*thikness)/2);
         
         
        // Scalar[] mColorsRGB = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };
         
         //Scalar mWhilte = Scalar.all(255);
         
         Scalar[] mColorsHue = new Scalar[] {
                 new Scalar(255, 0, 0, 255), new Scalar(255, 60, 0, 255), new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),
                 new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255), new Scalar(20, 255, 0, 255), new Scalar(0, 255, 30, 255),
                 new Scalar(0, 255, 85, 255), new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),
                 new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255), new Scalar(0, 0, 255, 255), new Scalar(64, 0, 255, 255), new Scalar(120, 0, 255, 255),
                 new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255), new Scalar(255, 0, 0, 255)
         };
         
         String[] mColorsHueString = new String[] {
                 "Blue","Blue", "Blue", "Blue","Blue",
                 "Green", "Green", "Green", "Green", "Green",
                 "Green", "Green", "Yellow", "Yellow", "Yellow",
                 "Red", "Red", "Red", "Red", "Red",
                 "Red", "Blue", "Blue" , "Blue", "Blue"
         };

        
 	    Mat HSV = new Mat();
 	    Mat threshold = new Mat();
 	    org.opencv.imgproc.Imgproc.cvtColor(inputframe,HSV, org.opencv.imgproc.Imgproc.COLOR_BGR2HSV);
 	    Core.inRange(HSV,new Scalar(0,0,(255*0.75)),new Scalar(180,255,255),threshold);
 	    Mat dst = new Mat();
 	    //org.opencv.imgproc.Imgproc.cvtColor(threshold, threshold, org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR);
 	    inputframe.copyTo(dst, threshold);
 	    
 	    threshDark = threshold;
 	    
 	    Mat mGrey = new Mat();
 	    Imgproc.cvtColor( dst, mGrey, Imgproc.COLOR_BGR2GRAY);  
 	    
 	    Mat removeBrightMask = new Mat();
 	    Imgproc.threshold(mGrey,removeBrightMask, 200,255,Imgproc.THRESH_BINARY_INV);
 	   
 	    
 	    threshLight = removeBrightMask;
 	    
 	    //Highgui.imwrite("cameraTTTG" + icoun + ".jpg",  removeBrightMask);
       
 	    Mat dst1 = new Mat();
 	    
 	    dst.copyTo(dst1, removeBrightMask);
 	    // Highgui.imwrite("cameraTTT.jpg",  dst);
 	    // Value
       
         Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV_FULL);
         
         Mat superMask = new Mat();
         Core.bitwise_and(removeBrightMask, threshold, superMask);
         
         threshCombined = superMask;
         //Highgui.imwrite("cameraTTTSUPER" + icoun + ".jpg",  superMask);
         
         icoun++;
         
         Imgproc.calcHist(Arrays.asList(mIntermediateMat), mChannels[0], superMask, hist, mHistSize, mRanges);
         Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
         hist.get(0, 0, mBuff);
         
         double highestVal = 99999999;
         int indexHighest = 0;
         
         for(int h=0; h<mHistSizeNum; h++) {
      	   
      	   
      	   
             mP1.x = mP2.x = offset + (4 * (mHistSizeNum + 10) + h) * thikness;
             mP1.y = sizeRgba.height-1;
             mP2.y = mP1.y - 2 - (int)mBuff[h];
             
             histValues[h] = mP2.y;
             
             if(mP2.y < highestVal)
             {
          	   highestVal = mP2.y;
          	   valueColorWithHighest = mP2.y;
          	   colorWithHighest = mColorsHue[h];
          	   nameColorWithHighest = mColorsHueString[h];
          	   indexHighest = h;
          	   
             }
             
             Core.line(dst1, mP1, mP2, mColorsHue[h], thikness);
         }

         System.out.println("highestColor: " + colorWithHighest.toString() + " " + highestVal + " -   " + nameColorWithHighest + " index:" + indexHighest);  

         
         return dst1;  
    }  
    
	
}
