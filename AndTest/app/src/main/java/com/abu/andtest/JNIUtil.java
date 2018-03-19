package com.abu.andtest;

/**
 * @author abu
 *         2018/3/12    14:40
 *         bulasuo@foxmail.com
 */

public class JNIUtil {
    static
    {
        System.loadLibrary("JNISample");
        //名字注意，需要跟你的build.gradle ndk节点       下面的名字一样
    }

    public native String getWorld();


}
