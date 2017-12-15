package com.mack.example.blink.Device;

import com.mack.example.blink.R;

/**
 * Created by cksrb on 2017. 12. 7..
 */

public class CameraOptions {

    private int iso;
    private int sec;//Inter millisec

    public CameraOptions() {
        iso = 320;
        sec = 200;
    }

    public CameraOptions(int MODEL) {
        switch (MODEL) {
            case R.string.huawei_nexus_6p :
                iso = 320;
                sec = 50;
                break;
//                iso = 400;
//                sec = 200;
//                break;
            case R.string.galaxy_note_fe :
            default :
                iso = 320;
                sec = 200;
        }
    }

    public int getIso(){return iso;}

    public int getSec(){return sec;}
}
