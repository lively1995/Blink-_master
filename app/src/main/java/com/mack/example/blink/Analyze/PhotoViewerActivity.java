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

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.mack.example.blink.R;
import com.mack.example.blink.Device.mApplication;

/**
 * Demonstrates basic usage of the GMS vision face detector by running face landmark detection on a
 * photo and displaying the photo with associated landmarks in the UI.
 */
public class PhotoViewerActivity extends Activity {
    private static final String TAG = "PhotoViewerActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        mApplication app = (mApplication)getApplicationContext();
        FaceView.setContext(getApplicationContext());
//        InputStream stream = getResources().openRawResource(R.raw.face7);
//        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        Log.d("face", ""+app.face.getGenerationId());
        Bitmap bitmap = app.face;

        // A new face detector is created for detecting the face and its landmarks.
        //
        // Setting "tracking enabled" to false is recommended for detection with unrelated
        // individual images (as opposed to video or a series of consecutively captured still
        // images).  For detection on unrelated individual images, this will give a more accurate
        // result.  For detection on consecutive images (e.g., live video), tracking gives a more
        // accurate (and faster) result.
        //
        // By default, landmark detection is not enabled since it increases detection time.  We
        // enable it here in order to visualize detected landmarks.
        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        // This is a temporary workaround for a bug in the face detector with respect to operating
        // on very small images.  This will be fixed in a future release.  But in the near term, use
        // of the SafeFaceDetector class will patch the issue.
        Detector<Face> safeDetector = new SafeFaceDetector(detector);

        // Create a frame from the bitmap and run face detection on the frame.
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = safeDetector.detect(frame);

        if (!safeDetector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "cannot be downloaded", Toast.LENGTH_LONG).show();
//                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        FaceView overlay = (FaceView) findViewById(R.id.faceView);
        overlay.setContent(bitmap, faces);
//오버라이드컨스트럭터

        Log.d("DDDDDD2", "x"+app.geteXLeft()+"y"+app.geteYLeft());
        Log.d("DDDDDD2", "x"+app.geteXRight()+"y"+app.geteYRight());

        ImageView imgV1, imgV2;

        imgV1 = (ImageView)findViewById(R.id.img1);
       try{
           Bitmap EL = Bitmap.createBitmap(bitmap, app.geteXLeft()-50, app.geteYLeft()-50, 100, 75);
           imgV1.setImageBitmap(EL);
           app.eye_l = EL;
       }catch (Exception e){
           e.printStackTrace();
       }

        imgV2 = (ImageView)findViewById(R.id.img2);
       try{
           Bitmap ER = Bitmap.createBitmap(bitmap, app.geteXRight()-50, app.geteYRight()-50, 100, 75);
           imgV2.setImageBitmap(ER);
           app.eye_r = ER;
       }catch (Exception e){
        e.printStackTrace();
    }


        Log.d("PhotoViewer", "@@@@@@@@@@@@@@@@@@");
//        imgview1 = (ImageView)findViewById(R.id.img1);
//        Log.d("Left x y", "X:"+overlay.geteXLeft()+" Y:"+overlay.geteYLeft());
//        Bitmap EyeL = Bitmap.createBitmap(bitmap, overlay.geteXLeft()-10, overlay.geteYLeft()-50, 200, 100);
//                imgview1.setImageBitmap(EyeL);
//
//        imgview2 = (ImageView)findViewById(R.id.img2);
//        Bitmap EyeR = Bitmap.createBitmap(bitmap, overlay.geteXRight()-10, overlay.geteYRight()-50, 200, 100);
////        overlay.geteXLeft()
////        overlay.geteYLeft()
////          overlay.geteYLeft()
////          overlay.geteYRight()
//                imgview2.setImageBitmap(EyeR);


        // Although detector may be used multiple times for different images, it should be released
        // when it is no longer needed in order to free native resources.
        safeDetector.release();

        Button btn = (Button)findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoViewerActivity.this, AnalysisActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
