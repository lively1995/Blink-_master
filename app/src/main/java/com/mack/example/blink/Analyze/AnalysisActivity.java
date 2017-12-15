package com.mack.example.blink.Analyze;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.mack.example.blink.R;
import com.mack.example.blink.Device.mApplication;

/**
 * Created by steven on 2017. 12. 5..
 */

public class AnalysisActivity extends Activity{
    ImageView img1, img2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_analysis);
        mApplication app = (mApplication)getApplicationContext();

        img1 = (ImageView)findViewById(R.id.img1);
        img2 = (ImageView)findViewById(R.id.img2);

        FindPupil fpL = new FindPupil(app.eye_l);
        Bitmap eLeft = fpL.findingPupil();
        if(eLeft == null){
            Toast.makeText(getApplicationContext(),"NullLeft" , Toast.LENGTH_LONG);
        }else
        {
            img1.setImageBitmap(eLeft);
        }

        FindPupil fpR = new FindPupil(app.eye_r);
        Bitmap eRight = fpR.findingPupil();
        if(eRight == null){
            Toast.makeText(getApplicationContext(),"NullRight" , Toast.LENGTH_LONG);
        }else{
            img2.setImageBitmap(eRight);
        }




    }
}
