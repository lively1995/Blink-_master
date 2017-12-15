/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mack.example.blink.Analyze;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.mack.example.blink.Device.mApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * View which displays a bitmap containing a face along with overlay graphics that identify the
 * locations of detected facial landmarks.
 */
public class FaceView extends View {
    private Bitmap mBitmap;
    private SparseArray<Face> mFaces;
    public int eyeX1, eyeY1, eyeX2, eyeY2;
    public Bitmap EyeL, EyeR;
    private mApplication app;
    static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public FaceView(Context context, AttributeSet attrs) {

        super(context, attrs);


    }

    /**
     * Sets the bitmap background and the associated face detections.
     */
    void setContent(Bitmap bitmap, SparseArray<Face> faces) {
        mBitmap = bitmap;
        mFaces = faces;
        app = (mApplication)mContext;
        invalidate();
        Log.d("setContent size ", " "+mFaces.size());
        for (int i = 0; i < mFaces.size(); ++i) {
            Log.d("setContent", "@@@@@@@@@2");
            Face face = mFaces.valueAt(i);
            int j=0;
            for (Landmark landmark : face.getLandmarks()) {
                Log.d("setContent", "@@@@@@@@@3");
                if(j==0){
                    app.setXYLeft((int)(landmark.getPosition().x), (int) (landmark.getPosition().y));
                    Log.d("setxyleft", ""+(int)(landmark.getPosition().x)+ " "+ (int) (landmark.getPosition().y));
                }
                if(j==1){
                    app.setXYRight((int)(landmark.getPosition().x), (int) (landmark.getPosition().y));
                    Log.d("setxyRight", ""+(int)(landmark.getPosition().x)+ " "+ (int) (landmark.getPosition().y));
                }
                j++;
            }
        }

    }

    /**
     * Draws the bitmap background and the associated face landmarks.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((mBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
            drawFaceAnnotations(canvas, scale);
        }
    }

    /**
     * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
     * positioning the facial landmark graphics.
     */
    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }

    /**
     * Draws a small circle for each detected landmark, centered at the detected landmark position.
     * <p>
     *
     * Note that eye landmarks are defined to be the midpoint between the detected eye corner
     * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
     * pupil position.
     */
    private void drawFaceAnnotations(Canvas canvas, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
//        for (int i = 0; i < mFaces.size(); ++i) {
//            Face face = mFaces.valueAt(i);
//            int j=0;
//            for (Landmark landmark : face.getLandmarks()) {
//                int cx = (int) (landmark.getPosition().x * scale);
//                int cy = (int) (landmark.getPosition().y * scale);
//                if(j==0){
//                    app.setXYLeft((int)(landmark.getPosition().x), (int) (landmark.getPosition().y));
//                }
//                if(j==1){
//                    app.setXYRight((int)(landmark.getPosition().x), (int) (landmark.getPosition().y));
//                }
//                canvas.drawCircle(cx, cy, 10, paint);
//               j++;
//            }
//        }

    }


    public static void saveBitmaptoJpeg(Bitmap bitmap,String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
}
