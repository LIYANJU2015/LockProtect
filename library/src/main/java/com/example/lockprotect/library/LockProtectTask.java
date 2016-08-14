package com.example.lockprotect.library;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by baidu on 16/8/14.
 */
public class LockProtectTask {

    private CountDownTimer mCountDownTimer;

    private long mTotalTime = 60 * 1000;

    private long mInterval = 1000;

    private HandlerThread mHandlerThread;

    private LockProtectTaskManager.CountDownTimerListener mListener;

    private int mId;

    public void setCountDownTimerListener(LockProtectTaskManager.CountDownTimerListener listener) {
        mListener = listener;
    }

    public void setTotalTime(long totalTime) {
        mTotalTime = totalTime;
    }

    public void setInterval(long interval) {
        mInterval = interval;
    }

    public void startLockProtect(int id){
        if (mHandlerThread == null) {
            mId = id;
            mHandlerThread = new HandlerThread("LockProtectTask");
            mHandlerThread.start();
            Handler handler = new Handler(mHandlerThread.getLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    startCountDownTimer();
                }
            });
        }
    }

    private void startCountDownTimer() {
        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(mTotalTime, mInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (mListener != null) {
                        mListener.updateProgress(mId, millisUntilFinished);
                    }
                }

                @Override
                public void onFinish() {
                    mCountDownTimer = null;
                    if (mHandlerThread != null){
                        mHandlerThread.quit();
                        mHandlerThread = null;
                    }
                    if (mListener != null) {
                        mListener.finish(mId);
                    }
                }
            }.start();
        }
    }

}
