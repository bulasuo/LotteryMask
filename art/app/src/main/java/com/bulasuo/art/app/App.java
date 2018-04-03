package com.bulasuo.art.app;

import com.abu.xbase.app.BaseApp;
import com.bulasuo.art.BuildConfig;

import cn.jpush.android.api.JPushInterface;

/**
 * @author abu
 *         2018/3/19    10:55
 *         ..
 */

public class App extends BaseApp {

    @Override
    protected void initApp() {
        super.initApp();
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
    }

    @Override
    protected boolean isDEBUG() {
        return BuildConfig._DEBUG;
    }

    @Override
    protected boolean isRELEASE() {
        return BuildConfig._RELEASE;
    }

    @Override
    protected String getFileProviderSchemeTag() {
        return BuildConfig._FILE_PROVIDER_SCHEME_TAG;
    }
}
