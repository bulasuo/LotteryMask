package com.bulasuo.art.app;

import com.abu.xbase.app.BaseApp;
import com.bulasuo.art.BuildConfig;

/**
 * @author abu
 *         2018/3/19    10:55
 *         bulasuo@foxmail.com
 */

public class App extends BaseApp {
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
