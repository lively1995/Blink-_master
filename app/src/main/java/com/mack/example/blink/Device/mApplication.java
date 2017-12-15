package com.mack.example.blink.Device;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;

import com.mack.example.blink.R;

/**
 * Created by steven on 2017. 12. 4..
 */

public class mApplication extends Application {
    private int MODEL;
    private CameraOptions cameraOptions = null;

    @Override
    public void onCreate() {
        super.onCreate();

        MODEL = checkModel(Build.MODEL);

        cameraOptions = new CameraOptions(MODEL);

        Log.d("Device Info", "BOARD=" + Build.BOARD + "\n" +
                "BRAND=" + Build.BRAND + "\n" +
                "DEVICE=" + Build.DEVICE + "\n" +
                "DISPLAY=" + Build.DISPLAY + "\n" +
                "FINGERPRINT=" + Build.FINGERPRINT + "\n" +
                "HARDWARE=" + Build.HARDWARE + "\n" +
                "HOST=" + Build.HOST + "\n" +
                "ID=" + Build.ID + "\n" +
                "MANUFACTURER=" + Build.MANUFACTURER + "\n" +
                "MODEL=" + Build.MODEL + "\n" +
                "PRODUCT=" + Build.PRODUCT + "\n" +
                "TAGS=" + Build.TAGS + "\n" +
                "TYPE=" + Build.TYPE + "\n" +
                "USER=" + Build.USER + "\n" +
                "RELEASE=" + Build.VERSION.RELEASE);
    }

    private int checkModel(String MODEL) {
        int model = 0;

        if (getString(R.string.galaxy_note_fe).equals(MODEL)) {
            model = R.string.galaxy_note_fe;
            Log.d("mDebug","MODEL = GALAXY NOTE FE");
        }
        else if (getString(R.string.huawei_nexus_6p).equals(MODEL)) {
            model = R.string.huawei_nexus_6p;
            Log.d("mDebug","MODEL = Huawei Nexus 6P");
        }
        else {
            //Not Supported Device Model
            model = -1;
        }

        return model;
    }

    public int getMODEL(){return MODEL;}
    public CameraOptions getCameraOptions(){return cameraOptions;}
    public Bitmap byteToBitmap(byte[] data){
        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);

        Matrix sideInversion = new Matrix();
//        sideInversion.setScale(1, -1);  // 상하반전

        sideInversion.setScale(-1, 1);  // 좌우반전
        Bitmap sideInversionImg = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), sideInversion, false);

        return sideInversionImg;
    }

    public Bitmap face, eye_l = null, eye_r = null;
    public int LeftX, LeftY, RightX, RightY;

    public void setXYLeft(int ex, int ey){
        this.LeftX=ex;
        this.LeftY=ey;
    }
    public void setXYRight(int ex, int ey){
        this.RightX=ex;
        this.RightY=ey;
    }
    public int geteXLeft(){
        return LeftX;
    }
    public int geteXRight(){
        return RightX;
    }
    public int geteYLeft(){
        return LeftY;
    }
    public int geteYRight(){
        return RightY;
    }


}
