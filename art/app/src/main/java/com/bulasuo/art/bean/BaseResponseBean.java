package com.bulasuo.art.bean;

import android.text.TextUtils;

import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XUtil;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Response;

/**
 * @author abu
 *         2018/2/5    17:12
 *         bulasuo@foxmail.com
 */

public class BaseResponseBean {

    public ArrayList<LotteryBean> numberList;
    public String result, desc;

    public static boolean isSuccessful(Response<BaseResponseBean> response,
                                           boolean showErrInfo){
        boolean success =  response != null
                && response.body() != null
                && response.body().result != null
                && TextUtils.equals(response.body().result, "01001");
        if(!success && showErrInfo){
            showErrInfo(response);
        }
        return success;
    }

    private static void showErrInfo(Response response){
        try {
            ToastUtil.showShort(
                    XUtil.jsonStr2Object(response.errorBody().string(),
                            BaseResponseBean.class).desc);
        }catch (NullPointerException | IOException e){
            ToastUtil.showShort("请求失败!");
        }
    }
}
