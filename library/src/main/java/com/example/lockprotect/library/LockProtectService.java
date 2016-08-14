package com.example.lockprotect.library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;
import android.util.Log;

/**
 * Created by baidu on 16/8/14.
 */
public class LockProtectService extends Service {

    private LockProtectTaskManager mLockProtectControl;

    public static final String MESSAGER = "messager";

    public static final String TOTALTIME = "totalTime";

    public static final String ACTION_START = "start_lock_protect";

    public static final String ACTON_LISTNER = "start_listener";

    public static final String ACTION_CHECK = "start_check";

    public static final String ID = "id";

    public static final String INTERVAL = "interval";

    private ArrayMap<Integer, Messenger> mMessagerMap = new ArrayMap<>();

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mLockProtectControl = new LockProtectTaskManager();
    }

    private void sendMessage(int id, long millisUntilFinished) {
        Messenger messenger = mMessagerMap.get(id);
        Log.v("xx", " sendMessage messenger " + messenger);
        if (messenger != null) {
            Message msg = Message.obtain();
            msg.obj = millisUntilFinished;
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void lockProtectFinish(int id) {
        mLockProtectControl.removeLockProtectTask(id);
        mMessagerMap.remove(id);
        saveLockProtectByID(mContext, id, false);
        if (mLockProtectControl.getLockProtectSize() == 0) {
            Log.v("XX", "lockProtectFinish stopSelf ");
            stopSelf();
        }
    }

    private synchronized void addStartLockProtect(int id, long totalTime, Messenger messenger) {
        mMessagerMap.put(id, messenger);
        mLockProtectControl.addStartLockProtect(id, totalTime, 0, new LockProtectTaskManager.CountDownTimerListener() {
            @Override
            public void updateProgress(int id, long millisUntilFinished) {
                Log.v("XX", " updateProgress id " + id + " millisUntilFinished " + millisUntilFinished);
                saveCountDownTime(mContext, id, millisUntilFinished);
                sendMessage(id, millisUntilFinished);
            }

            @Override
            public void finish(int id) {
                Log.v("XX", " finish id " + id);
                lockProtectFinish(id);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            long totalTime = intent.getLongExtra(TOTALTIME, 0);
            Messenger messenger = intent.getParcelableExtra(MESSAGER);
            int id = intent.getIntExtra(ID, 0);

            if (ACTION_START.equals(action)) {
                saveLockProtectByID(mContext, id, true);
                addStartLockProtect(id, totalTime, messenger);
            } else if (ACTON_LISTNER.equals(action)) {
                Log.v("xx", " ACTON_LISTNER id " + id + " messenger " + messenger);
                if (mMessagerMap.containsKey(id)) {
                    mMessagerMap.put(id, messenger);
                } else { //如果id 本地存储的还在，但是map里没有了，可以认定倒计时没完成，继续让倒计时从断的时间完成
                    if (messenger != null) {
                        Log.v("XX"," restart startLockProtect>>>>>>");
                        totalTime = getCountDownTime(mContext, id);
                        addStartLockProtect(id, totalTime - 1000, messenger);
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开始指定id进锁保护机制
     *
     * @param context
     * @param id
     * @param messenger
     * @param totalTime
     * @param interval
     */
    public static void startLockProtect(Context context, int id, Messenger messenger, long totalTime, long interval) {
        Intent intent = new Intent(context, LockProtectService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(ID, id);
        intent.putExtra(MESSAGER, messenger);
        intent.putExtra(TOTALTIME, totalTime);
        intent.putExtra(INTERVAL, interval);
        context.startService(intent);
    }

    /**
     * 取消倒计时监听 当activity 退出 就要取消对倒计时的监听
     *
     * @param context
     * @param id
     */
    public static void cancelCountDownListener(Context context, int id) {
        setCountDownListener(context, id, null);
    }

    /**
     * 重新进行锁保护监听
     *
     * @param context
     * @param id
     * @param messager
     */
    public static void setCountDownListener(Context context, int id, Messenger messager) {
        if (isLockProtectedByID(context, id)) {
            Intent intent = new Intent(context, LockProtectService.class);
            intent.setAction(ACTON_LISTNER);
            if (messager != null) {
                intent.putExtra(MESSAGER, messager);
            }
            intent.putExtra(ID, id);
            context.startService(intent);
        }
    }

    public static boolean isLockProtectedByID(Context context, int id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("lockProtect", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(String.valueOf(id), false);
    }

    public static void saveLockProtectByID(Context context, int id, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("lockProtect", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(String.valueOf(id), value).apply();
    }

    public static void saveCountDownTime(Context context, int id, long time) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("time", Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(String.valueOf(id), time).apply();
    }

    public static long getCountDownTime(Context context, int id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("time", Context.MODE_PRIVATE);
        return sharedPreferences.getLong(String.valueOf(id), 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
