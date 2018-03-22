package com.abu.xbase.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.abu.xbase.app.BaseApp;
import com.abu.xbase.config.XConstant;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * @author abu
 *         2015/12/6    17:26
 *         bulasuo@foxmail.com
 */

public class NetStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ToastUtil.showDebug("NetStateChangeReceiver");
        if (XUtil.isNetworkAvailable(context)) {
            EventBus.getDefault().post(BaseApp.getMainHandler()
                    .obtainMessage(XConstant.EventBus.NET_CONNECT_SUCCESS));
        }
    }
}
