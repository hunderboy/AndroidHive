package com.example.user.androidhive;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by user on 2017-12-09.
 */

public class BackPressCloseHandler extends Activity {

    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;
    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {  // 2초 안에 두번 눌럿을때
//            activity.finish();
//            android.os.Process.killProcess(android.os.Process.myPid());
            ActivityCompat.finishAffinity(this);
//            System.runFinalizersOnExit(true);
//            System.exit(0);
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

}
