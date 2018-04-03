package com.abu.xbase.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abu.xbase.R;
import com.abu.xbase.Task.Task;
import com.abu.xbase.app.BaseApp;
import com.abu.xbase.config.XConstant;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XUtil;
import com.abu.xbase.util.XViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.net.ConnectException;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * activity 直接基类
 *
 * @author abu
 *         2017/11/13    09:03
 *         ..
 */

public abstract class BaseActivity extends BaseResumeTaskActivity {

    private int mScreenWidth;

    public int getScreenWidth() {
        if (mScreenWidth <= 0) {
            Resources resources = this.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            mScreenWidth = dm.widthPixels;
        }
        return mScreenWidth;
    }


    public static final String FLAG_OBJ = "FLAG_OBJ_BASE_ACTIVITY";
    private AlertDialog alertDialog;
    protected Serializable obj;

    protected Serializable getObj() {
        if (obj == null)
            obj = getIntent().getSerializableExtra(FLAG_OBJ);
        return obj;
    }

    protected boolean setObj(Serializable obj1) {
        if (obj1 == null) {
            obj = null;
            getIntent().removeExtra(FLAG_OBJ);
            return true;
        }
        if (getObj() == null || obj1.getClass() == getObj().getClass()) {
            obj = obj1;
            getIntent().putExtra(FLAG_OBJ, obj1);
            return true;
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        ToastUtil.showDebug("onNewIntent" + getClass().getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(FLAG_OBJ, obj);
        super.onSaveInstanceState(outState);
    }

    private Unbinder unbinder;
    public static final String REQUEST_CODE = "REQUEST_CODE";

    public int getRequestCode() {
        return getIntent().getIntExtra(REQUEST_CODE, -1);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            obj = savedInstanceState.getSerializable(FLAG_OBJ);
        }
    }

    private void initButterKnife() {
        unbinder = ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initButterKnife();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initButterKnife();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initButterKnife();
    }

    public void onRootViewClick(View view) {
        XUtil.hideSoftKeyBoard(this, view);
    }


    private static Handler mainHandler;

    /**
     * 获取ui主线程handler
     */
    public static Handler getMainHandler() {
        if (mainHandler == null) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }

    public static Message obtainMessage() {
        return getMainHandler().obtainMessage();
    }

    public static Message obtainMessage(int what) {
        return getMainHandler().obtainMessage(what);
    }

    public static void post(Message msg) {
        EventBus.getDefault().post(msg);
    }

    public static void post(int what) {
        EventBus.getDefault().post(obtainMessage(what));
    }

    public static void post(int what, Object obj) {
        Message message = obtainMessage(what);
        message.obj = obj;
        EventBus.getDefault().post(message);
    }

    public static void postDelayDef(int what) {
        getMainHandler().postDelayed(() -> EventBus.getDefault().post(obtainMessage(what)), 1000);
    }


    /**
     * EventBus 发送页面请求处理后的结果  (不用原生的 startActivityForResult 和 setResult  是嫌弃太复杂)
     *
     * @param resultCode 返回页面处理结果的resultCode {@link XConstant.ResultCode}
     * @param object     返回数据
     */
    public void postResult(int resultCode, Object object, Task delayTask) {
        Message msg = obtainMessage(XConstant.EventBus.REQUEST_TO_RESULT);
        msg.arg1 = getRequestCode();
        msg.arg2 = resultCode;
        msg.obj = object;
        EventBus.getDefault().post(msg);
        if (delayTask != null)
            delayTask.apply();
    }

    public void postResultDelayed(int resultCode, Object object, int delay, Task delayTask) {
        getMainHandler().postDelayed(() -> postResult(resultCode, object, delayTask), delay);
    }

    public void postResultDelayedDefault(int resultCode, Object object, Task delayTask) {
        postResultDelayed(resultCode, object, 1000, delayTask);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (BaseApp.DEBUG) {
            BaseApp.watch(this);
        }
        destroyProgress();
        super.onDestroy();
    }

    private AlertDialog getProgressDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(R.layout.progress_dialog_xbase)
                    .create();
        }
        return alertDialog;
    }

    public void dealResponse(Call call, Response response) {
        try {
            ToastUtil.showDebug("dealResponse::" + response.toString());
            showProgress(false);
            if (!response.isSuccessful()) {
//            ToastUtil.showShort("请求失败!");
            }
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

    public void dealFailure(Call call, Throwable t) {
        try {
            ToastUtil.showDebug("dealFailure::" + t.toString());
            showProgress(false);
            if (!XUtil.isNetworkAvailable(this)) {
                ToastUtil.showShort("网络未连接,请稍后重试!");
            } else if (t instanceof ConnectException) {
                ToastUtil.showShort("网络连接错误!");
            } else {
                ToastUtil.showException(t);
            }
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

    public void showProgress(boolean show) {
        try {
            if (show) {
                //默认显示 waiting...提示
                showProgress(R.string.waiting_tip);
            } else {
                if (alertDialog != null && alertDialog.isShowing()) {
                    mTvProgress = null;
                    alertDialog.dismiss();
                }
            }
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

    public void showProgress(int msgId) {
        try {
            getProgressDialog();
            TextView textView = alertDialog.findViewById(R.id.tv_msg);
            if (textView != null)
                textView.setText(msgId);
            textView = alertDialog.findViewById(R.id.tv_progress);
            if (textView != null) {
                textView.setText(null);
                XViewUtil.visvable(textView, View.GONE);
            }
            mTvProgress = null;
            getProgressDialog().show();
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

    private TextView mTvProgress;

    /**
     * @param progress {@code 1-100}
     */
    public void undateProgressIfShowing(int progress){
        if(alertDialog != null && alertDialog.isShowing()) {
            try {
                if(progress == 0 || mTvProgress == null) {
                    ((TextView) alertDialog.findViewById(R.id.tv_msg))
                            .setText(R.string.downloading_tip);
                    mTvProgress = alertDialog.findViewById(R.id.tv_progress);
                    XViewUtil.visvable(mTvProgress, View.VISIBLE);
                }
                mTvProgress.setText(String.valueOf(progress).concat("%"));

            } catch (Exception e) {
                ToastUtil.showException(e);
            }
        }
    }

    private void destroyProgress() {
        if (alertDialog != null) {
            alertDialog.cancel();
            alertDialog = null;
        }
    }

    /**
     * eventbus 注册了eventbus 就至少写一个eventbus监听器
     * 否则编译错误
     */
    @Subscribe
    public void onEventMainThread(Message msg) {
        if (msg != null) {
            switch (msg.what) {
                default:
                    break;
            }
        }
    }
}
