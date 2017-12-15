package com.mack.example.blink.TakePicture;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mack.example.blink.Device.RequestUserPermission;
import com.mack.example.blink.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TakePictureActivity100 extends Activity implements SurfaceHolder.Callback{

    private Camera mCamera;
    private Camera.Parameters cameraParams;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean previewing = false;

    private LayoutInflater controlInflater = null;

    private Button button_takePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        RequestUserPermission requestUserPermission = new RequestUserPermission(this);
        requestUserPermission.verityStoragePermissions();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        Button button_start = (Button)findViewById(R.id.button_start);
//        Button button_stop = (Button)findViewById(R.id.button_stop);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        ViewGroup.LayoutParams layoutParamsControl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

//        settingCameraCallback();

        button_takePicture = (Button)findViewById(R.id.button_takePicture);
        button_takePicture.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Flash", "Blink");

                SurfaceTexture dummy = new SurfaceTexture(0);
                try {
                    mCamera.setPreviewTexture(dummy);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //flash torch;
                mCamera.stopPreview();

                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(cameraParams);
                mCamera.startPreview();

//                Log_CameraPictureSize();

                mCamera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);

                //flash off;
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(cameraParams);
//                mCamera.stopPreview();

//                mCamera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
            }
        });

//        LinearLayout layoutBackground = (LinearLayout)findViewById(R.id.background);
//        layoutBackground.setOnClickListener(new LinearLayout.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                button_takePicture.setEnabled(false);
//                mCamera.autoFocus(myAutoFocusCallback);
//            }
//        });

        RelativeLayout layoutBackground = (RelativeLayout) findViewById(R.id.background);
        layoutBackground.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_takePicture.setEnabled(false);
                mCamera.autoFocus(myAutoFocusCallback);
            }
        });

//        button_start.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!previewing) {
//                    mCamera = Camera.open();
//
//                    cameraParams = mCamera.getParameters();
//
//                    cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                    cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//
//                    mCamera.setParameters(cameraParams);
//
//                    if (mCamera != null) {
//                        try{
//                            mCamera.setPreviewDisplay(surfaceHolder);
//                            mCamera.startPreview();
//                            previewing = true;
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//
//        button_stop.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mCamera != null && previewing) {
//                    mCamera.stopPreview();
//                    mCamera.release();
//                    mCamera = null;
//
//                    previewing = false;
//                }
//            }
//        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        cameraParams = mCamera.getParameters();

        cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        mCamera.setParameters(cameraParams);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (previewing) {
            mCamera.stopPreview();
            previewing = false;
        }

        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
                previewing = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        previewing = false;
    }

    private Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean b, Camera camera) {
            button_takePicture.setEnabled(true);
        }
    };

    private Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    private Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

        }
    };

    private Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Uri uriTarget = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());

            OutputStream imageFileOS;

            try {
                imageFileOS = getContentResolver().openOutputStream(uriTarget);
                imageFileOS.write(bytes);
                imageFileOS.flush();
                imageFileOS.close();

                Toast.makeText(TakePictureActivity100.this, "Image saved: " + uriTarget.toString(), Toast.LENGTH_LONG).show();
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCamera.startPreview();
        }
    };

    public void Log_CameraPictureSize(){
        List sizes = cameraParams.getSupportedPictureSizes();
        Camera.Size result = null;
        for (int i=0;i<sizes.size();i++){
            result = (Camera.Size) sizes.get(i);
            Log.d("PictureSize", "Supported Size. Width: " + result.width + "height : " + result.height);
        }

        result = cameraParams.getPictureSize();
        Log.d("PictureSize", "Current Size. Width: " + result.width + "height : " + result.height);
    }
}
