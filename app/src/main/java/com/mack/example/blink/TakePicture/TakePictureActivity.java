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

import com.mack.example.blink.Device.mApplication;
import com.mack.example.blink.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TakePictureActivity extends Activity implements SurfaceHolder.Callback {
    private mApplication app;

    private Camera mCamera;
    private Camera.Parameters cameraParams;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean previewing = false;

    private LayoutInflater controlInflater = null;

    private Button button_takePicture;

    private int iso = 0;
    private int millisec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        app = (mApplication)getApplicationContext();

        iso = app.getCameraOptions().getIso();
        millisec = app.getCameraOptions().getSec();

//        millisec = getIntent().getIntExtra("millisec", 200);
//        iso = getIntent().getIntExtra("iso", 320);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        ViewGroup.LayoutParams layoutParamsControl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

        button_takePicture = (Button)findViewById(R.id.button_takePicture);
        button_takePicture.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                SurfaceTexture dummy = new SurfaceTexture(0);
                try {
                    mCamera.setPreviewTexture(dummy);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mCamera.autoFocus(myAutoFocusCallback);

                cameraParams.set("iso", iso);
                mCamera.setParameters(cameraParams);
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //flash on
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(cameraParams);
                mCamera.startPreview();

                try {
                    Thread.sleep(millisec);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mCamera.autoFocus(myAutoFocusCallback);

                try {
                    Thread.sleep(millisec/2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mCamera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);

                //flash off
                cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(cameraParams);

            }
        });

        RelativeLayout layoutBackground = (RelativeLayout) findViewById(R.id.background);
        layoutBackground.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_takePicture.setEnabled(false);
                mCamera.autoFocus(myAutoFocusCallback);
            }
        });

        if (iso == -1){
//            Intent intent = new Intent(this, StartActivity.class);
            finish();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        cameraParams = mCamera.getParameters();

        cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        cameraParams.set("exposure", "night");
        cameraParams.set("iso", 1250);
        cameraParams.set("whitebalance", "auto");
        cameraParams = SetPictureSize(cameraParams);

        mCamera.setParameters(cameraParams);

        Log.d("get Supported","iso-speed-values = " + mCamera.getParameters().get("iso-speed-values"));
        Log.d("get Supported","iso-values = " + mCamera.getParameters().get("iso-values"));
        Log.d("get Supported","iso-speed = " + mCamera.getParameters().get("iso-speed"));
        Log.d("get Supported","iso = " + mCamera.getParameters().get("iso"));
        Log.d("get Supported","iso-speed-values = " + mCamera.getParameters().get("ISO-SPEED-VALUES"));
        Log.d("get Supported","iso-values = " + mCamera.getParameters().get("ISO-VALUSE"));
        Log.d("get Supported","iso-speed = " + mCamera.getParameters().get("ISO-SPEED"));
        Log.d("get Supported","iso = " + mCamera.getParameters().get("ISO"));
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

                Toast.makeText(getApplicationContext(), "Image saved: " + uriTarget.toString(), Toast.LENGTH_LONG).show();
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (app.getMODEL() == R.string.huawei_nexus_6p)mCamera.release();//Nexus 6p+++++++
        }
    };

    private Camera.Parameters SetPictureSize(Camera.Parameters cameraParams) {
        List sizes = cameraParams.getSupportedPictureSizes();

        Camera.Size result = null;
        int width = 0, height = 0;
        for (int i = 0; i < sizes.size(); i++) {
            result = (Camera.Size) sizes.get(i);
            if (result.width * result.height > width * height) {
                width = result.width;
                height = result.height;
            }
        }

        cameraParams.setPictureSize(width, height);
        return cameraParams;
    }
}
