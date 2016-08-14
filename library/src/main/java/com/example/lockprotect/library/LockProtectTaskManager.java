package com.example.lockprotect.library;

import android.support.v4.util.ArrayMap;

/**
 * Created by baidu on 16/8/14.
 */
public class LockProtectTaskManager {

    private ArrayMap<Integer, LockProtectTask> mArrayMap = new ArrayMap();

    public void removeLockProtectTask(int id){
        mArrayMap.remove(id);
    }

    public int getLockProtectSize(){
        return mArrayMap.size();
    }

    public void addStartLockProtect(int id, long totalTime, long interval, CountDownTimerListener listener){
        if (!mArrayMap.containsKey(id)) {
            LockProtectTask lockProtectTask = new LockProtectTask();
            if (totalTime != 0){
                lockProtectTask.setTotalTime(totalTime);
            }
            if (interval != 0){
                lockProtectTask.setInterval(interval);
            }
            if (listener != null) {
                lockProtectTask.setCountDownTimerListener(listener);
            }
            lockProtectTask.startLockProtect(id);
            mArrayMap.put(id, lockProtectTask);
        }
    }

    public void addStartLockProtect(int id, CountDownTimerListener callBack) {
        addStartLockProtect(id, 0, 0, callBack);
    }

    public interface CountDownTimerListener {

        public void updateProgress(int id, long millisUntilFinished);

        public void finish(int id);
    }

}
