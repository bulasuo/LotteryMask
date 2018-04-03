package com.abu.xbase.pay;

import android.app.Activity;
import android.text.TextUtils;

import com.abu.xbase.Task.TaskR;
import com.abu.xbase.Task.TaskTR;
import com.abu.xbase.app.BaseApp;
import com.abu.xbase.util.ThreadPool;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.wxapi.BaseWXPayEntryActivity;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;

import java.util.Map;

/**
 * @author abu
 *         2018/2/8    13:34
 *         ..
 */

public class PayHelp {
    public PayHelp() {
        throw new IllegalArgumentException("please use static method!");
    }

    /**
     * @param activity
     * @param orderInfo
     * @param success   R:: 是否拦截默认处理
     *                  T:: 支付宝支付结果
     *                  :: https://docs.open.alipay.com/204/105301
     * @param fail      R:: 是否拦截默认处理
     */
    public static void aliPay(Activity activity, String orderInfo,
                              TaskTR<Map<String, String>, Boolean> success,
                              TaskR<Boolean> fail) {
        ThreadPool.getPool().execute(() -> {
            PayTask alipay = new PayTask(activity);
            Map<String, String> result = alipay.payV2(orderInfo, true);
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(result.get("resultStatus"), "9000")) {
                BaseApp.getMainHandler().post(() -> {
                    try {
                        if (success == null || !success.apply(result))
                            ToastUtil.showShort("支付成功!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showShort("支付失败!");
                    }
                });
            } else {
                BaseApp.getMainHandler().post(() -> {
                    try {
                        if (fail == null || !fail.apply())
                            ToastUtil.showShort("支付失败!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showShort("支付失败!");
                    }
                });
            }
        });

    }

    /**
     * @param success R:: 是否拦截默认处理
     *                T:: BaseResp 微信支付结果
     *                :: https://docs.open.alipay.com/204/105301
     * @param fail    R:: 是否拦截默认处理
     */
    public static void wxPay(String appid, String partnerid, String prepayid,
                             String mpackage, String noncestr, String timestamp,
                             String sign,
                             TaskTR<BaseResp, Boolean> success,
                             TaskR<Boolean> fail) {
        PayReq req = new PayReq();
        req.appId = appid;
        req.partnerId = partnerid;
        req.prepayId = prepayid;
        req.packageValue = mpackage;
        req.nonceStr = noncestr;
        req.timeStamp = timestamp;
        req.sign = sign;
        BaseWXPayEntryActivity.sendReq(req, success, fail);
    }

}
