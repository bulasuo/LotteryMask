package com.abu.xbase.util;

import android.support.annotation.UiThread;
import android.widget.Toast;

import com.abu.xbase.app.BaseApp;


/**
 * @author abu
 *         2017/11/7    19:03
 *         bulasuo@foxmail.com
 */

@UiThread
public class ToastUtil {
    private static final boolean DEBUG = BaseApp.DEBUG;

    public static void showException(Throwable e) {
        if (!DEBUG) {
            return;
        }
        if (e == null)
            return;
        try {
            if (XUtil.isUIThread()){
                final Toast toast = Toast.makeText(BaseApp.getInstance()
                        , e.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
            e.printStackTrace();
            ToastUtil.showDebug(e.toString());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void showDebug(CharSequence msg) {
        if (!DEBUG) {
            return;
        }
        try {
            System.out.println(Thread.currentThread().getId() + "-showDEBUG:::" + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showShort(CharSequence msg) {
        try {
            final Toast toast = Toast.makeText(BaseApp.getInstance()
                    , msg, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLong(CharSequence msg) {
        try {
            final Toast toast = Toast.makeText(BaseApp.getInstance()
                    , msg, Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
