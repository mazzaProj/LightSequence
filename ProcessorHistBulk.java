import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


public class ProcessorHistBulk {
	
	 private processorHist my_pro;
 	 private String[] colorFrame;
 	 private double[] valueFrame;
 	 private double[][] histFrame;
 	 private ArrayList<Mat> threshCombined;
 	 private ArrayList<Mat> threshDark;
 	 private ArrayList<Mat> threshLight;
 	 private ArrayList<Mat> originals;
 	 private ArrayList<Mat> threshCombinedWithHist;
 	 
	
	public String[] getColorFrames()
	{
		return this.colorFrame;
	}
	
	public double[] getValueFrames()
	{
		return this.valueFrame;
	}
	
	public double[][] getHistFrames()
	{
		return this.histFrame;
	}
	
	public ArrayList<Mat> getThreshLight()
	{
		return this.threshLight;
	}
	
	public ArrayList<Mat> getThreshDark()
	{
		return this.threshDark;
	}
	
	public ArrayList<Mat> getOriginals()
	{
		return this.originals;
	}
	
	public void printToFileThreshCombined()
	{
		for(int i = 0; i < threshCombined.size(); i ++)
		{
			Highgui.imwrite("Combined" + i + ".jpg",  this.threshCombined.get(i));
		}
	}
	
	public void printToFileThreshDark()
	{
		for(int i = 0; i < threshDark.size(); i ++)
		{
			Highgui.imwrite("Dark" + i + ".jpg",  this.threshDark.get(i));
		}
	}
	
	public void printToFileThreshLight()
	{
		for(int i = 0; i < threshCombined.size(); i ++)
		{
			Highgui.imwrite("Light" + i + ".jpg",  this.threshLight.get(i));
		}
	}
	
	public void printToFileOriginals()
	{
		for(int i = 0; i < originals.size(); i ++)
		{
			Highgui.imwrite("Original" + i + ".jpg",  this.originals.get(i));
		}
	}
	
	public void printToFileCombinedWithHist()
	{
		for(int i = 0; i < originals.size(); i ++)
		{
			Highgui.imwrite("threshCombinedWithHist" + i + ".jpg",  this.threshCombinedWithHist.get(i));
		}
	}
	
	ProcessorHistBulk(ArrayList<Mat> hold)
	{
		 int i = hold.size();
		 int z = 0;
	     Mat m = new Mat();
	     Mat mRgba = new Mat();
	  	  
	     my_pro = new processorHist();
	  	 colorFrame = new String[i];
	  	 valueFrame = new double[i];
	  	 histFrame = new double[i][25];
	  	 

	 	 threshCombined = new ArrayList<Mat>();
	 	 threshDark = new ArrayList<Mat>();
	 	 threshLight = new ArrayList<Mat>();
	 	 originals = new ArrayList<Mat>();
	 	 threshCombinedWithHist = new ArrayList<Mat>();
	  	 
	     
	  	  
	      for(z = 0; z < i; z++)
	      {
	    	  m = hold.get(z);
	      	  m.copyTo(mRgba);
	      	  
	      	  mRgba = my_pro.detect(mRgba);
	      	  
	      	  colorFrame[z] = my_pro.getNameColorWithHighest();
	    	  valueFrame[z] = my_pro.getValueColorWithHighest();
	    	  threshCombined.add(my_pro.getThreshCombined());
	    	  threshLight.add(my_pro.getThreshLight());
	    	  threshDark.add(my_pro.getThreshDark());
	    	  originals.add(hold.get(z).clone());
	    	  
	    	  for(int q = 0; q < 25; q++)
	    		  histFrame[z][q] = my_pro.getHistValue(q);
	      	  
	    	  threshCombinedWithHist.add(mRgba.clone());
	    	  
	      	 // Highgui.imwrite("camera" + z + ".jpg",  mRgba);
	          
	      }
	      
	}
	
	
}
