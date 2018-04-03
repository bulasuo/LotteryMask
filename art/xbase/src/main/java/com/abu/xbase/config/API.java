package com.abu.xbase.config;

import com.abu.xbase.app.BaseApp;

/**
 * @author abu
 *         2017/11/16    00:10
 *         ..
 */

public class API {

    public static String BUCKET = " ";
    public static String ENDPOINT = " ";
    public static String BASE_URL;
    public static final String SOCKRT_HOST = " ";
    public static final int SOCKRT_PORT = 10088;
    public static final int TIM_APPID = 1400044920;
    public static final String TIM_ACCOUNT_TYPE = "18213";

    static {
        if (BaseApp.RELEASE)
            BASE_URL = " ";
        else
            BASE_URL = " ";
//        HOST = "http://www.pinestudy.com:8090";
    }

}
