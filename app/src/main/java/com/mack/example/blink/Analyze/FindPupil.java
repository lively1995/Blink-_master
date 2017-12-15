package com.mack.example.blink.Analyze;

import android.graphics.Bitmap;
import android.util.Log;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRound;
import static org.bytedeco.javacpp.opencv_core.cvarrToMat;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GAUSSIAN;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughCircles;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

/**
 * Created by steven on 2017. 12. 5..
 */
public class FindPupil {
    private Bitmap rawImage = null;
    private opencv_core.IplImage sourceImage = null;
    private opencv_core.Mat ROI_mat = null;
    AndroidFrameConverter converterToBitmap = null;
    OpenCVFrameConverter.ToMat converterToMat = null;
    private Frame frame;

//    private opencv_core.IplImage middleImage = null;
//    private opencv_core.IplImage imsiImage = null;


    public FindPupil(Bitmap source) {
        this.rawImage = source;
    }

    public Bitmap findingPupil() {
//        ROI_mat = cvarrToMat(sourceImage);
        sourceImage = opencv_core.IplImage.create(rawImage.getWidth(), rawImage.getHeight(), IPL_DEPTH_8U, 4);
        rawImage.copyPixelsToBuffer(sourceImage.getByteBuffer());
        cvSmooth(sourceImage, sourceImage, CV_GAUSSIAN, 3, 0, 0.0, 0.0);
        opencv_core.IplImage grayImage = cvCreateImage(cvGetSize(sourceImage), 8, 1);
        cvCvtColor(sourceImage, grayImage, CV_RGB2GRAY);
        cvThreshold(grayImage, grayImage, 0.0, 255.0, CV_THRESH_BINARY + CV_THRESH_OTSU);
        opencv_core.IplImage edge = cvCreateImage(cvGetSize(grayImage), 8, 1);
        cvCanny(grayImage, edge, 0, 255, 3);
        opencv_core.CvMemStorage storage = cvCreateMemStorage(0);
        opencv_core.CvSeq circles = cvHoughCircles(edge, storage, CV_HOUGH_GRADIENT, 1.0, 100.0, 150.0, 25.0, 8, 200);

        int cx[] = new int[50];
        int cy[] = new int[50];
        int radius[] = new int[50];
        Log.d("circles.total() = ", circles.total() + "");
        for (int k = 0; k < circles.total(); k++) {
            BytePointer p =  cvGetSeqElem(circles, k);

            cx[k] = cvRound(p.get(0));
            cy[k] = cvRound(p.get(1));
            radius[k] = cvRound(p.get(2));

            Log.d("circle", "circle[" + k + "]=(" + cx[k] + ", " + cy[k] + ", " + radius[k] + ")");
            cvCircle(sourceImage, cvPoint(cx[k], cy[k]), 3, CV_RGB(0, 255, 0), -1, 8, 0);
            cvCircle(sourceImage, cvPoint(cx[k], cy[k]), radius[k], CV_RGB(255, 0, 0), 3, 8, 0);
        }
        Log.d("cicles", circles.total() + "");

        if(circles.total() != 0){
//            opencv_core.Rect ResultRect1 = new opencv_core.Rect(cx[0]-radius[0], cy[0] - radius[0], radius[0] * 2, radius[0] * 2);
//            opencv_core.Rect ResultRect2 = new opencv_core.Rect(cx[1]-radius[1], cy[1] - radius[1], radius[1] * 2, radius[1] * 2);
            opencv_core.Mat ROI_result = cvarrToMat(sourceImage);
            opencv_core.Mat R1 = ROI_result.adjustROI(cx[0]-radius[0], cy[0] - radius[0], radius[0] * 2, radius[0] * 2);
//            opencv_core.Mat R2 = ROI_result.adjustROI(cx[1]-radius[1], cy[1] - radius[1], radius[1] * 2, radius[1] * 2);
            return makeMatToBitmap(R1);
        }
        Log.d("isThis", "TestTest");
        return null;
    }

    /**
     * 분석이 끝난 Mat을 Bitmap으로 변환 후 리턴한다.
     *
     * @return64
     */
    private Bitmap makeMatToBitmap(opencv_core.Mat source) {
        converterToBitmap = new AndroidFrameConverter();
        converterToMat = new OpenCVFrameConverter.ToMat();
        frame = converterToMat.convert(source);

        Bitmap bitmap = converterToBitmap.convert(frame);
        return bitmap;
    }
}
