package com.abu.xbase.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.abu.xbase.config.CrashHandler;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.abu.xbase.util.ToastUtil;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;


/**
 * @author abu
 *         2017/11/7    15:14
 *         ..
 */

public abstract class BaseApp extends Application {
    private static BaseApp instance;
    private static Handler handler;
    private static RefWatcher refWatcher;

    private static RefWatcher getRefWatcher() {
        return refWatcher;
    }

    /**
     * 是否调试
     */
    public static boolean DEBUG;

    /**
     * 是否release
     */
    public static boolean RELEASE;

    /**
     * FireProvider的SCHEME_TAG
     */
    public static String _FILE_PROVIDER_SCHEME_TAG;


    public static void watch(Object watchedReference) {
        try {
            getRefWatcher().watch(watchedReference);
        } catch (Exception e) {
            ToastUtil.showException(e);
        }

    }

    public static BaseApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        instance = this;
        if (shouldInit()) {
            initApp();
        }
    }

    protected abstract boolean isDEBUG();

    protected abstract boolean isRELEASE();

    protected abstract String getFileProviderSchemeTag();

    protected void initApp() {
        DEBUG = isDEBUG();
        RELEASE = isRELEASE();
        _FILE_PROVIDER_SCHEME_TAG = getFileProviderSchemeTag();

        BaseApp app = BaseApp.getInstance();
        RetrofitUtil.init(DEBUG, app, null);
        //崩溃捕获
        if (RELEASE) {
            CrashHandler.getInstance().init(app);
        }
        if (DEBUG) {
            refWatcher = LeakCanary.install(app);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * @return true : 当前进程是包名进程
     */
    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
            String mainProcessName = getPackageName();
            int myPid = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Handler getMainHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }
}
