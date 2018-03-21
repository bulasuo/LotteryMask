package com.bulasuo.art.activity;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.abu.xbase.activity.BaseWebViewActivity;
import com.abu.xbase.activity.TitleBarBaseWebViewActivity;
import com.abu.xbase.jsbridge.BridgeWebView;

/**
 * @author abu
 *         2018/3/21    15:29
 *         bulasuo@foxmail.com
 */

public class AppTitleBarBaseWebViewActivity extends TitleBarBaseWebViewActivity {

    /**
     * @param context
     * @param url     加载的url
     * @param title   标题
     */
    public static void launch(Context context, String url, String title) {
        BaseWebViewActivity.launch(context, url, title, AppTitleBarBaseWebViewActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mBridgeWebView.getLayoutParams();
        params.topMargin = 0;
//        mBridgeWebView.setLayoutParams(params);
        params = (FrameLayout.LayoutParams) mTextView.getLayoutParams();
        params.topMargin = 0;
//        mTextView.setLayoutParams(params);
    }

    @Override
    protected MyBridgeWebViewClient getMyBridgeWebViewClient(BridgeWebView webView) {
        return new AppMyBridgeWebViewClient(webView);
    }

    public class AppMyBridgeWebViewClient extends MyBridgeWebViewClient{
        public AppMyBridgeWebViewClient(BridgeWebView webView) {
            super(webView);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String js = "(function() {document.getElementById('uiHead').style.display='none';})()";
            mBridgeWebView.loadUrl("javascript:"+js);
        }
    }
}
