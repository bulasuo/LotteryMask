package com.abu.andtest;

import android.util.Log;

/**
 * @author abu
 *         2018/3/16    14:17
 *         bulasuo@foxmail.com
 */

public class JNI02 {

    static
    {
        System.loadLibrary("JNISample");
        //名字注意，需要跟你的build.gradle ndk节点       下面的名字一样
    }

    public native String getWorldxxxx();

    public void javaMethod1() {
        Log.e("xx", "javaMethod1");
        System.out.println("sssssss");
    }

    public void javaMethod2(String in) {
        System.out.println("sssssssxxxxxxxxx");
        Log.e("xx", "xx"+in);
        System.out.println("sssssss"+in);
    }
}
