package com.mack.example.blink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mack.example.blink.Device.RequestUserPermission;
import com.mack.example.blink.TakePicture.Camera2Activity;

//Version 3.0
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        RequestUserPermission requestUserPermission = new RequestUserPermission(this);
        requestUserPermission.verityStoragePermissions();
    }

    public void onClick(View view) {
//        Intent intent = new Intent(this, TakePictureActivity.class);
        Intent intent = new Intent(this, Camera2Activity.class);

//        switch (Id) {
//            case R.id.GALAXY_NOTE_FE : intent = new Intent(this, TakePictureActivity.class);
//                EditText editText_sec = (EditText)findViewById(R.id.editText_sec);
//                EditText editText_iso = (EditText)findViewById(R.id.editText_iso);
//                String millisec = editText_sec.getText()+"";
//                String iso = editText_iso.getText()+"";
//
//                if (millisec != null && millisec != "") {
//                    intent.putExtra("millisec", Integer.parseInt(millisec));
//                }
//
//                if (iso != null && iso != "") {
//                    intent.putExtra("iso", Integer.parseInt(iso));
//                }
//                break;
//            default : return ;
//        }

        try{
            startActivity(intent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
