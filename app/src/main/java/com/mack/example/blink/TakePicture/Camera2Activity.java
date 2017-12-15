package com.mack.example.blink.TakePicture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mack.example.blink.Device.RequestUserPermission;
import com.mack.example.blink.R;

public class Camera2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        //get the Permission
        RequestUserPermission requestUserPermission = new RequestUserPermission(this);
        requestUserPermission.verityStoragePermissions();

        //TO using the camera app.
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }
}
