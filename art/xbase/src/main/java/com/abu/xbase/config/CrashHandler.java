package com.abu.xbase.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.abu.xbase.util.ToastUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * runTime异常处理
 * Created by abu on 2016/8/2 14:03.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    /**
     * 用来存储设备信息和异常信息
     */
    private Map<String, String> infos = new HashMap<>();
    /**
     * CrashHandler实例
     */
    @SuppressLint("StaticFieldLeak")
    private static CrashHandler instance;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        //收集错误日志并且保存在本地
        collectDeviceInfo(mContext);
        ToastUtil.showDebug("CrashHandler::\n" + infos.toString());
        restartApp(mContext);
        //让系统默认的异常处理器来处理-以及退出app
        mDefaultHandler.uncaughtException(thread, ex);
    }


    /**
     * 收集设备参数信息
     *
     * @param ctx 上下文
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            infos.clear();
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                String logHead = "\n************* Crash Log Head ****************" +
                        "\nDevice Manufacturer: " + Build.MANUFACTURER +// 设备厂商
                        "\nDevice Model       : " + Build.MODEL +// 设备型号
                        "\nAndroid Version    : " + Build.VERSION.RELEASE +// 系统版本
                        "\nAndroid SDK        : " + Build.VERSION.SDK_INT +// SDK版本
                        "\nApp VersionName    : " + versionName +
                        "\nApp VersionCode    : " + versionCode +
                        "\n************* Crash Log Head ****************\n\n";
                infos.put("LogHead", logHead);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    public static void restartApp(Context mContext) {
        if (mContext == null) {
            return;
        }
        Intent i = mContext.getPackageManager()
                .getLaunchIntentForPackage(mContext.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }
}
