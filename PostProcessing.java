import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


public class PostProcessing {

	public void findSequence(int startingFrame, String[] colorEachFrame, int totalFrames )
	{
		
		String [] colorFrame = colorEachFrame;

	      ArrayList<String> sequence = new ArrayList<String>();
		  HashMap<String, Integer> cumulativeColorCounts = new HashMap<String, Integer>();
	  	  cumulativeColorCounts.put("Yellow", 0);
	  	  cumulativeColorCounts.put("Green", 0);
	  	  cumulativeColorCounts.put("Blue", 0);
	  	  cumulativeColorCounts.put("Red", 0);
	  	  
		  
	      for(int z = startingFrame; z < totalFrames; z++)
	      {
	      	  System.out.println();
	      	  System.out.println("Yellow:" + cumulativeColorCounts.get("Yellow"));
	      	  System.out.println("Green:" + cumulativeColorCounts.get("Green"));
	      	  System.out.println("Blue:" + cumulativeColorCounts.get("Blue"));
	      	  System.out.println("Red:" + cumulativeColorCounts.get("Red"));
	      	  
	      	  
	      	  
	      	String lastColor = "";

	        
	      	 //int setThresh = (int) (( i - firstFrameStart) / 7 );
	      	 int setThresh = 4;
	      	 System.out.println("ThresHold: " + setThresh);
	    	  
	       	 // prevents random colors from accumulating a "hit", looks for groupings

	    	  if((( z - 1 >= 0 && colorFrame[z - 1] == colorFrame[z]) || (z + 1 < totalFrames && colorFrame[z + 1] == colorFrame[z]))
	    	    || ((z - 2 >= 0 && colorFrame[z - 2] == colorFrame[z]) || (z + 2 < totalFrames && colorFrame[z + 2] == colorFrame[z])))
	    	  {
	    		  lastColor = colorFrame[z];
	    		  cumulativeColorCounts.put(colorFrame[z], cumulativeColorCounts.get(colorFrame[z]) + 1);
	    	  }

	      	  
	      	 // TODO need to prevent dead frames after and before sequence
	      	 // Maybe take pic without lights and remove frames similar to it.
	      	 
	      	 
	      	 if(cumulativeColorCounts.get("Yellow") == setThresh && !sequence.contains("Yellow"))
	      		 sequence.add("Yellow");
	      	 else if(cumulativeColorCounts.get("Green") == setThresh && !sequence.contains("Green"))
	      		 sequence.add("Green");
	      	 else if(cumulativeColorCounts.get("Blue") == setThresh && !sequence.contains("Blue"))
	      		 sequence.add("Blue");
	         else if(cumulativeColorCounts.get("Red") == setThresh && !sequence.contains("Red"))
	        	 sequence.add("Red");
	        
	      	 if(sequence.size() == 3)
	      		 break;
	      	 
	      }
	      
	      
	      if(sequence.size() < 3)
	      {
	    	  sequence.clear();
	    	  for(int v = 0; v < 3; v++)
	    		  sequence.add("Blue");
	      }
	      // put sequence failure option here
	      
	      
	  	  System.out.println("\n\n\nFINAL SEQUENCE: " + sequence.toString());
	}


 	public int startFromHist(int totalNumFrames, double[][] histFrame)
    {
    	 int firstFrameStart = 0;
         boolean firstFrame = true;
	   	  while(firstFrame)
	   	  {
	   		  if(firstFrameStart + 1 == totalNumFrames)
	   			  break;
	   		  
	   		  for(int v = 0; v < 25; v++)
	   		  {
	   			  if(histFrame[firstFrameStart][v] >= 1.50*histFrame[firstFrameStart + 1][v] || histFrame[firstFrameStart][v] <= 0.50*histFrame[firstFrameStart + 1][v])
	   				  firstFrame = false;
	   		  }
	   		  firstFrameStart++;
	   	  }
	   	  
	   	  return firstFrameStart;
    }
    
    
    public int startFromPosi(int totalNumFrames, ArrayList<Mat> threshLight, ArrayList<Mat> originals, boolean writeOutToFiles)
    {
    	 int count = 0;
    	 boolean notFinished = true;
         

    	 List<Mat> boundBoxes =new ArrayList<Mat>();
    	 List<Mat> boundBoxesCumulative =new ArrayList<Mat>();

    	
    	 List<Point> centers =new ArrayList<Point>();
         
	   	  while(notFinished)
	   	  {
	   		 
	   		  
	   		  Mat edges = new Mat();
	   		  Mat hierarchy = new Mat();
	   		  
	   		  Mat DrawOUT = new Mat();
	   		  
	   		  
	   		  List<MatOfPoint> contours =new ArrayList<MatOfPoint>();
	   		  
	   		  DrawOUT = threshLight.get(count);
	   		 
	   		  Mat blahhold = new Mat();
	   		  
	   		  Imgproc.threshold(DrawOUT,blahhold, 245,255,Imgproc.THRESH_BINARY_INV);
	   		  Imgproc.findContours( blahhold, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE );
	
	   		  MatOfPoint2f         approxCurve = new MatOfPoint2f();
	
	   		  
	   		  Point largest = new Point();
	   		  Point rectPoint1largest = new Point();
	   		  Point rectPoint2largest = new Point();
	   		  double currentLargestArea = 0;
	   		  
	   		    //For each contour found
	   		    for (int xx=0; xx<contours.size(); xx++)
	   		    {
	   		        //Convert contours(i) from MatOfPoint to MatOfPoint2f
	   		        MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(xx).toArray() );
	   		        //Processing on mMOP2f1 which is in type MatOfPoint2f
	   		        double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
	   		        Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
	
	   		        //Convert back to MatOfPoint
	   		        MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
	
	   		        // Get bounding rect of contour
	   		        Rect rect = Imgproc.boundingRect(points);
	   		        
	   		        // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
	   		        //Core.rectangle(blahhold, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255.0, 255.0, 255.0), 3); 
	   		        // Core.circle(blahhold, new Point(rect.x+(rect.width/2),rect.y+(rect.height/2)), 4, new Scalar(255.0, 255.0, 255.0));
	
	   		        if(rect.area() > currentLargestArea)
	   		        {
	   		        	currentLargestArea = rect.area();
	   		        	largest = new Point(rect.x+(rect.width/2),rect.y+(rect.height/2)).clone();
	   		        	rectPoint1largest = new Point(rect.x,rect.y);
	   		        	rectPoint2largest = new Point(rect.x+rect.width,rect.y+rect.height);
	   		        }
	   		        
	   		    }
	
	   		   centers.add(largest);
	
	   		   System.out.println(" Point of Largest : " + largest.toString() + ", Area: " + currentLargestArea);
	   		   
	   		   Mat output = new Mat();
	   		   output = originals.get(count).clone();
	   		   
	   		   Core.circle(output, largest, 10, new Scalar(250, 0, 250));
  		       Core.rectangle(output, rectPoint1largest, rectPoint2largest ,new Scalar(250, 0, 250), 3); 
  		       
  		       boundBoxes.add(output.clone());
  		       
  		      
	   	        
	   	       
	   		count++;
	   		
		   	 if(count == totalNumFrames)
	  			  break;
	   	  }
	       
	   	  
	   	  //Printing
	   	  
	   	  if(writeOutToFiles)
	   	  {
	   		  for(int i = 0; i < totalNumFrames; i++)
	   		  {
	   			 Highgui.imwrite("largestBoundingBox" + i + ".jpg", boundBoxes.get(i));
	   			 Mat cumulative = new Mat();
	   			 
	   			 for(int z = 0; z <= i; z++)
	   			 {
	   				 cumulative = boundBoxes.get(i);
	   				 Core.circle(cumulative, centers.get(z), 10, new Scalar(250, 0, 250));
	   			 }
   				 Core.putText(cumulative, new Integer(i).toString(), new Point(centers.get(i).x + 15, centers.get(i).y), Core.FONT_HERSHEY_COMPLEX_SMALL, 1.3, new Scalar(250, 0, 250));

	   			 Highgui.imwrite("largestBoundingBoxCumulative" + i + ".jpg", cumulative);
	   		  }
	   	  }
	   	  
	   	  
	   	  
	   	  //Finding start
	   	  int firstFrameStart2 = 0;
	   	  if(centers.size() != 0)
	   	  {
	   		  System.out.println("\n Size Centers Array : " + centers.size());
	   		  
	   	 
	   		  boolean firstFrame2 = true;
	   		  while(firstFrame2)
	   		  {
	   			  if(firstFrameStart2 + 1 == centers.size() || firstFrameStart2 > 15)
	   				  break;
	   			
	   			  
	   			  if(Math.sqrt(Math.pow(centers.get(firstFrameStart2).x - centers.get(firstFrameStart2+1).x, 2) + Math.pow(centers.get(firstFrameStart2).y - centers.get(firstFrameStart2+1).y, 2))> 40 )
	   				  firstFrame2 = false;
	   			  
	   			  System.out.println("\nDIFF: " + Math.abs(centers.get(firstFrameStart2).x  - centers.get(firstFrameStart2+1).x));
	   			  
	   			  
	   			  firstFrameStart2++;
	   		  }
	   	  }
   	  

	   	  System.out.println("\nStart at frame:" + firstFrameStart2);
	   	  
	   	  return firstFrameStart2;
   	
   	  
   	  
    }
	
	
}
