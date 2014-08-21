package com.muhavision.cv;

import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.helper.opencv_core.AbstractIplImage;
import org.bytedeco.javacpp.opencv_core.IplImage;

import com.muhavision.control.EulerAngles;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_video.*;

import java.awt.image.BufferedImage;
import java.util.Vector;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.helper.opencv_core.AbstractIplImage;
import org.bytedeco.javacpp.helper.opencv_core.CvArr;
import org.bytedeco.javacv.CanvasFrame;

public class MarkerTracker {

	//red
	//H:3.0 S:209.0 V:203.0 X:0.0
	//H:8.0 S:211.0 V:174.0 X:0.0
	//H:0.0 S:182.0 V:80.0 X:0.0
	//H:2.0 S:181.0 V:203.0 X:0.0
	//H:2.0 S:234.0 V:196.0 X:0.0
	//H:4.0 S:232.0 V:181.0 X:0.0
	//H:8.0 S:244.0 V:181.0 X:0.0
	
	//green
	//H:62.0 S:179.0 V:124.0 X:0.0
	//H:63.0 S:167.0 V:127.0 X:0.0
	//H:63.0 S:139.0 V:145.0 X:0.0
	//H:67.0 S:204.0 V:80.0 X:0.0
	//H:70.0 S:202.0 V:77.0 X:0.0
	//H:63.0 S:166.0 V:106.0 X:0.0
	//H:57.0 S:180.0 V:102.0 X:0.0
	//H:71.0 S:183.0 V:131.0 X:0.0
	//H:72.0 S:115.0 V:120.0 X:0.0
	
	static CanvasFrame canvas = new CanvasFrame("Quad Cam Live");
	
	static CvScalar green_min = cvScalar(47, 102, 65, 0);
	static CvScalar green_max = cvScalar(85, 240, 160, 0);
				
	static CvScalar red_min   = cvScalar(0, 210, 120, 0);
	static CvScalar red_max   = cvScalar(20, 255, 220, 0);
	
	public static EulerAngles getMarkerData(BufferedImage bufferedimg){
		
		if(bufferedimg!=null){
		
			IplImage source = AbstractIplImage.createFrom(bufferedimg);
		
			IplImage thrs_green = hsvThreshold(source, green_min, green_max);
			IplImage thrs_red = hsvThreshold(source, red_min, red_max);
			
			cvErode(thrs_green, thrs_green, null, 4);
			//cvErode(thrs_red, thrs_red, null, 1);
			
			canvas.showImage(thrs_red);
		
			CvMoments moments_green = new CvMoments();
			CvMoments moments_red = new CvMoments();
		
			cvMoments(thrs_green, moments_green, 1);
        	cvMoments(thrs_red, moments_red, 1);
        
        	double mom10_green = cvGetSpatialMoment(moments_green, 1, 0);
        	double mom01_green = cvGetSpatialMoment(moments_green, 0, 1);
        	double area_green = cvGetCentralMoment(moments_green, 0, 0);
        	int posX_green = (int) (mom10_green / area_green);
        	int posY_green = (int) (mom01_green / area_green);
        
        	double mom10_red = cvGetSpatialMoment(moments_red, 1, 0);
        	double mom01_red = cvGetSpatialMoment(moments_red, 0, 1);
        	double area_red = cvGetCentralMoment(moments_red, 0, 0);
        	int posX_red = (int) (mom10_red / area_red);
        	int posY_red = (int) (mom01_red / area_red);
        	
        	EulerAngles angle = new EulerAngles();
        	
        	angle.picX = posX_green;
        	angle.picY = posY_green;
        	
        	angle.picX2 = posX_red;
        	angle.picY2 = posY_red;
        	
        	return angle;
        
		}
		
		return new EulerAngles();
	}
	
	static IplImage hsvThreshold(IplImage orgImg, CvScalar min, CvScalar max) {
        IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        
        CvScalar s=cvGet2D(imgHSV,120,160);                
        System.out.println( "H:"+ s.val(0) + " S:" + s.val(1) + " V:" + s.val(2) + " X:" + s.val(3));//Print values
        
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        cvInRangeS(imgHSV, min, max, imgThreshold);
        cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold);
        return imgThreshold;
    }
	
	//hsv h deli� z dve, s in v pomno�i� z 255
	
		//green
		//85.0 S:149.0 V:192.0 X:0.0
		//H:82.0 S:146.0 V:206.0 X:0.0
		//H:83.0 S:101.0 V:192.0 X:0.0
		//H:83.0 S:156.0 V:188.0 X:0.0
		//H:83.0 S:160.0 V:178.0 X:0.0
		//H:85.0 S:133.0 V:167.0 X:0.0
		//
		//H:82.0 S:111.0 V:206.0 X:0.0
		//H:83.0 S:136.0 V:142.0 X:0.0
		//H:76.0 S:106.0 V:199.0 X:0.0
		
		//red
		//H:0.0 S:128.0 V:160.0 X:0.0
		//H:2.0 S:172.0 V:203.0 X:0.0
		//H:2.0 S:172.0 V:203.0 X:0.0
		//H:0.0 S:178.0 V:196.0 X:0.0
		//H:2.0 S:172.0 V:203.0 X:0.0
		//H:179.0 S:190.0 V:203.0 X:0.0
		//
		//H:167.0 S:127.0 V:203.0 X:0.0
		//H:175.0 S:118.0 V:203.0 X:0.0
		//H:167.0 S:127.0 V:203.0 X:0.0
		
		//static CvScalar green_min = cvScalar(76, 105, 120, 0);
		//static CvScalar green_max = cvScalar(87, 183, 216, 0);
			
		//static CvScalar red_min = cvScalar(0, 128, 160, 0);
		//static CvScalar red_max = cvScalar(2, 190, 203, 0);
	
}
