package com.bulasuo.art.bean;

import android.text.TextUtils;

import com.abu.xbase.util.ToastUtil;

import retrofit2.Response;

/**
 * @author abu
 *         2018/3/22    17:44
 *         bulasuo@foxmail.com
 */

public class BaseResponseBeanData {
    public BaseResponseBean data;

    public static boolean isSuccessful(Response<BaseResponseBeanData> response,
                                       boolean showErrInfo){
        boolean success =  response != null
                && response.body() != null
                && response.body().data != null
                && response.body().data.result != null
                && TextUtils.equals(response.body().data.result, "01001");
        if(!success && showErrInfo){
            showErrInfo(response);
        }
        return success;
    }

    private static void showErrInfo(Response<BaseResponseBeanData> response){
        try {
            ToastUtil.showShort(response.body().data.desc);
        }catch (NullPointerException e){
            ToastUtil.showShort("请求失败!");
        }
    }
}
