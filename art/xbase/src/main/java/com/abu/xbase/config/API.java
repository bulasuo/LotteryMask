package com.abu.xbase.config;

import com.abu.xbase.app.BaseApp;

/**
 * @author abu
 *         2017/11/16    00:10
 *         bulasuo@foxmail.com
 */

public class API {

    public static String BUCKET = "pinestudy-demo";
    public static String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    public static String HOST;
    public static final String SOCKRT_HOST = "47.93.121.101";
    public static final int SOCKRT_PORT = 10088;
    public static final int TIM_APPID = 1400044920;
    public static final String TIM_ACCOUNT_TYPE = "18213";

    static {
        if (BaseApp.RELEASE)
            HOST = "http://www.pinestudy.com:8090";
        else
            HOST = "http://www.pinestudy.com:8089";
//        HOST = "http://www.pinestudy.com:8090";
    }

}
