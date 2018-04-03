package com.abu.xbase.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abu.xbase.activity.BaseActivity;
import com.abu.xbase.app.BaseApp;
import com.abu.xbase.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author abu
 *         2017/11/19    16:50
 *         ..
 */

public abstract class BaseFragment extends BaseResumeTaskFragment {
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getContentViewLayoutID(), container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    /**
     * 获取根布局资源id
     */
    protected abstract int getContentViewLayoutID();

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (getView() == null) {
            return;
        }
        ToastUtil.showDebug("onHiddenChanged::" + hidden + "-" + isHidden() + getClass().getSimpleName());
        if (!hidden) {
            onResume();
        } else {
            onPause();
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (BaseApp.DEBUG) {
            BaseApp.watch(this);
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 防止覆盖Bundle而报 already active 异常
     *
     * @param args
     */
    @Override
    public void setArguments(Bundle args) {
        if (args == null) {
            if (getArguments() != null)
                getArguments().clear();
            return;
        }
        if (getArguments() != null)
            getArguments().putAll(args);
        else if (!this.isAdded()) {
            super.setArguments(args);
        }
    }

    /**
     * 显示等待框
     *
     * @param show
     */
    public void showProgress(boolean show) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity)
            ((BaseActivity) activity).showProgress(show);
    }

    public void showProgress(int msgId) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity)
            ((BaseActivity) activity).showProgress(msgId);
    }

    public void dealResponse(Call call, Response response) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity)
            ((BaseActivity) activity).dealResponse(call, response);
    }

    public void dealFailure(Call call, Throwable t) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity)
            ((BaseActivity) activity).dealFailure(call, t);
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
