package com.example.lockprotect.lockprotectdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lockprotect.library.LockProtectService;

/**
 * Created by baidu on 16/8/14.
 */
public class CountDownTimerActivity2 extends Activity{

    TextView textView;

    private Handler mHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            textView.setText(" 倒计时 " + ((long)msg.obj)/1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LockProtectService.setCountDownListener(getApplicationContext(), 1, new Messenger(mHander));

        setContentView(R.layout.count_timer2);

        textView = (TextView)findViewById(R.id.tv_show);
        if (LockProtectService.isLockProtectedByID(getApplicationContext(), 1)) {
            textView.setText(" 倒计时 " + String.valueOf(LockProtectService.getCountDownTime(getApplicationContext(), 1) / 1000));
        }


        Button button = (Button)findViewById(R.id.button_count);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LockProtectService.startLockProtect(getApplicationContext(), 1, new Messenger(mHander), 0, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LockProtectService.cancelCountDownListener(getApplicationContext(), 1);
    }
}
