package com.mack.example.blink;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mack.example.blink.Device.RequestUserPermission;

import java.io.IOException;

public class MainActivity extends Activity {

    public boolean FLASH_ON = false;
    public int interval = 1000;

    private android.hardware.Camera mCamera;
    private Camera.Parameters cameraParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestUserPermission requestUserPermission = new RequestUserPermission(this);
        requestUserPermission.verityStoragePermissions();

        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);


        //test start

//        SurfaceTexture dummy = new SurfaceTexture(0);
//        try {
//            mCamera.setPreviewTexture(dummy);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //test end


        cameraParams = mCamera.getParameters();

//        cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        mCamera.setParameters(cameraParams);

        Flash flash = new Flash();
        flash.start();
    }

    class Flash extends Thread {
        @Override
        public void run() {
            while (true) {
                if (interval == 0)break;

                try {
                    sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (FLASH_ON){
                    Log.d("Flash", "Blink");

                    SurfaceTexture dummy = new SurfaceTexture(0);
                    try {
                        mCamera.setPreviewTexture(dummy);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //flash torch;
                    cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(cameraParams);
                    mCamera.startPreview();

                    //flash off;
                    cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(cameraParams);
                    mCamera.stopPreview();
                }
            }
        }
    }

    public void flash_start(int interval) {
        this.interval = interval;
        FLASH_ON = true;

        mCamera.startPreview();
    }

    public void flash_stop() {
        FLASH_ON = false;
        this.interval = 1000;

        mCamera.stopPreview();
    }

    public void flash_end() {
        this.interval = 0;
        FLASH_ON = false;
    }

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_blink :
                EditText editText = (EditText)findViewById(R.id.editText_interval);
                flash_start(Integer.parseInt(editText.getText()+""));

                break;
            case R.id.button_stop :
                flash_stop();

                break;
            case R.id.button_end :
                flash_end();
        }
    }
}
