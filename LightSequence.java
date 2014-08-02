

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;        
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class LightSequence {
	
	
	public static void main (String args[]) throws IOException{
		  
		// TIMESTAMPS could be useful
		
		System.in.read();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  
 
		int i = 15;
		
		/*
		VideoCapture camera = new VideoCapture(0);

		if(!camera.isOpened())
		{
			System.out.println("Error");
		}
		else
		{
			long totalTime = 3500; // in millisecs
			long startTime = System.currentTimeMillis();
			boolean toFinish = false;
	
			Mat frame = new Mat();
			while(!toFinish)
			{
				if (camera.read(frame))
				{
					System.out.println("Frame Obtained");
					System.out.println("Captured Frame Width " + frame.width() + " Height " + frame.height());
					Highgui.imwrite("camera" + i + ".jpg", frame);
					i++;
					System.out.println("OK");
					toFinish = (System.currentTimeMillis() - startTime >= totalTime);
				}
			}  
		}
		*/
		  
		 
		// make sure each class acts independently
		// there is a reliance on the pictures, needs to be arugment of mat[]
		  
		 ArrayList<Mat> hold = new ArrayList<Mat>();
		  
		 for(int z = 0; z < i; z++)
		 {
			 hold.add(Highgui.imread("camera" + z + ".jpg",Highgui.CV_LOAD_IMAGE_COLOR));
			 System.out.println("READ " + z); 
		 }
		  
		 ProcessorHistBulk processorBulk = new ProcessorHistBulk(hold);
		 double[][] histFrame = processorBulk.getHistFrames(); 
		 String[] colorFrame = processorBulk.getColorFrames();
		 
		 
		 PostProcessing post = new PostProcessing();
		 
		 
		 //int firstFrameStart2 = post.startFromHist(i, histFrame);
		 int firstFrameStart2 = post.startFromPosi(i, processorBulk.getThreshLight(), hold, true);
		 
		 post.findSequence(firstFrameStart2, colorFrame, i);
		 
		 processorBulk.printToFileCombinedWithHist();
		 processorBulk.printToFileOriginals();
		 processorBulk.printToFileThreshCombined();
		 processorBulk.printToFileThreshDark();
		 processorBulk.printToFileThreshLight();
			  
			  
	}
    
}  
    