package com.abu.xbase.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abu.xbase.config.XConstant;
import com.abu.xbase.jsbridge.BridgeHandler;
import com.abu.xbase.jsbridge.BridgeWebView;
import com.abu.xbase.jsbridge.BridgeWebViewClient;
import com.abu.xbase.jsbridge.CallBackFunction;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XFileUtil;
import com.abu.xbase.util.XUtil;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author abu
 *         2017/11/29    10:28
 *         bulasuo@foxmail.com
 *         <p>
 *         ios::  https://github.com/marcuswestin/WebViewJavascriptBridge
 *         android:: https://github.com/lzyzsd/JsBridge
 */

public abstract class BaseWebViewActivity extends BaseTakePhotoActivity {
    protected FrameLayout mFrameLayout;
    protected TextView mTextView;
    protected BridgeWebView mBridgeWebView;
    private static final KeyEvent BACK_KEY_EVENT =
            new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);

    /**
     * @param context
     * @param url     加载的url
     * @param obj     FLAG_OBJ {@link #getObj()}
     * @param class1
     */
    protected static void launch(Context context, String url, Serializable obj, Class<?> class1) {
        context.startActivity(new Intent(context, class1)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(FLAG_OBJ, obj)
                .putExtra(TAG_URL, url));
    }

    protected static final String TAG_URL = "TAG_URL";

    private String getUrl() {
        return getIntent().getStringExtra(TAG_URL);
    }

    private void matchParent(View v) {
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        v.setLayoutParams(params);
    }

    private void showWaiting() {
        if (mTextView != null) {
            mTextView.setBackgroundColor(receivedError ? 0xffffffff : 0x00ffffff);
            receivedError = false;
            mTextView.setClickable(false);
            mTextView.setText("loading...");
            mTextView.setVisibility(View.VISIBLE);
            ToastUtil.showDebug(":::showWaiting");
        }
    }

    private void hideTip() {
        if (mTextView != null && !receivedError) {
            mBridgeWebView.setVisibility(View.VISIBLE);
            mTextView.setText(null);
            mTextView.setVisibility(View.GONE);
            ToastUtil.showDebug(":::hideTip");
        }
    }

    private boolean receivedError = false;

    private void showErr() {
        if (mTextView != null) {
            receivedError = true;
            mTextView.setBackgroundColor(0xffffffff);
            mTextView.setText("网络错误!");
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setClickable(true);
            mBridgeWebView.setVisibility(View.INVISIBLE);
            ToastUtil.showDebug(":::showErr");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrameLayout = new FrameLayout(this);
        mTextView = new TextView(this);
        mBridgeWebView = new BridgeWebView(this);
        mFrameLayout.addView(mBridgeWebView);
        mFrameLayout.addView(mTextView);
        setContentView(mFrameLayout);
        matchParent(mFrameLayout);
        mFrameLayout.setBackgroundColor(0xffffffff);
        matchParent(mTextView);
        showWaiting();
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBridgeWebView.reload();
            }
        });
        mTextView.setGravity(Gravity.CENTER);
        matchParent(mBridgeWebView);
        WebSettings settings = mBridgeWebView.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setUserAgentString(RetrofitUtil.getUserAgent());
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            settings.setAllowFileAccessFromFileURLs(false);
//        }
//        settings.setAllowFileAccess(false);
        /**设置webview推荐使用的窗口，使html界面自适应屏幕*/
        settings.setUseWideViewPort(true);
        /**设置可以访问文件*/
        settings.setAllowFileAccess(true);
        /**设置自动加载图片*/
        settings.setLoadsImagesAutomatically(true);


        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        /*CookieSyncManager.getInstance().sync();*/

        mBridgeWebView.setBackgroundColor(0x00000000);
        mBridgeWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mBridgeWebView.setWebChromeClient(new CustomWebChromeViewClient());
        mBridgeWebView.setWebViewClient(getMyBridgeWebViewClient(mBridgeWebView));

        mBridgeWebView.registerHandler("startSHSdk", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
//                SuanHuaModel.launchSuanHua(TLBWXActivity.this, data);
                function.onCallBack("android正在启动算话");
            }
        });
        mBridgeWebView.registerHandler("onNativeBack", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                onKeyDown(KeyEvent.KEYCODE_BACK, BACK_KEY_EVENT);
                function.onCallBack("触发原生返回键");
            }
        });
        mBridgeWebView.registerHandler("onNativeClearCookie", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                XUtil.clearWebViewCookie(BaseWebViewActivity.this);
                function.onCallBack("清除Cookie success");
            }
        });
        mBridgeWebView.registerHandler("onNativeExit", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                finish();
            }
        });
        mBridgeWebView.registerHandler("onNativeStop", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    mBridgeWebView.stopLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mBridgeWebView.registerHandler("onNativeRefresh", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                mBridgeWebView.reload();
            }
        });
        if (savedInstanceState != null) {
            mBridgeWebView.restoreState(savedInstanceState);
        } else {
            mBridgeWebView.loadUrl(getUrl());
        }
    }

    @Override
    protected void onResume() {
        if (mBridgeWebView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mBridgeWebView.onResume();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mBridgeWebView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mBridgeWebView.onPause();
            }
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBridgeWebView != null) {
            mBridgeWebView.saveState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        if (mFrameLayout != null) {
            mFrameLayout.removeAllViews();
        }
        if (null != mBridgeWebView) {
            mBridgeWebView.loadUrl("");
            mBridgeWebView.stopLoading();
            mBridgeWebView.removeAllViews();
            mBridgeWebView.destroy();
            mBridgeWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String url;
        if (keyCode == KeyEvent.KEYCODE_BACK
                && mBridgeWebView.canGoBack()
                && (url = mBridgeWebView.getUrl()) != null
                && !url.contains("posloanHomexxxxxx")
                && !url.contains("bindAcctxxxxxxxx")) {
            mBridgeWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected MyBridgeWebViewClient getMyBridgeWebViewClient(BridgeWebView webView) {
        return new MyBridgeWebViewClient(webView);
    }

    public class MyBridgeWebViewClient extends BridgeWebViewClient {

        public MyBridgeWebViewClient(BridgeWebView webView) {
            super(webView);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            ToastUtil.showDebug(":::11" + url);
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            /**重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边*/
            ToastUtil.showDebug(":::22shouldOverrideUrlLoading::" + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            ToastUtil.showDebug("::33" + event.toString());
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            handler.cancel();//停止加载问题页面。
            handler.proceed();
//            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            ToastUtil.showDebug(":::onReceivedError" + errorCode);
            if (errorCode == WebViewClient.ERROR_CONNECT || errorCode == WebViewClient.ERROR_TIMEOUT || errorCode ==
                    WebViewClient.ERROR_HOST_LOOKUP) {
                showErr();
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            showWaiting();
            CookieSyncManager.getInstance().sync();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            hideTip();
            CookieSyncManager.getInstance().sync();
            super.onPageFinished(view, url);
            /*String js = "(function() {document.getElementById('uiHead').style.display='none';})()";
            mBridgeWebView.loadUrl("javascript:"+js);*/
        }

    }

    public class CustomWebChromeViewClient extends WebChromeClient {

        /**
         * 处理alert弹出框
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            Toast.makeText(view.getContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            result.confirm();
            return true;
        }

        /**
         * 处理confirm弹出框
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   JsResult result) {
            result.confirm();
            return true;
        }

        /**
         * 处理prompt弹出框
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {
            return true;
        }

        /**
         * 扩容
         */
        /*@Override
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
            quotaUpdater.updateQuota(requiredStorage*2);
        }*/

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            ToastUtil.showDebug("openFileChooser::" + acceptType);
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            ToastUtil.showDebug("openFileChooser::" + acceptType + "_" + capture);
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ToastUtil.showDebug("onShowFileChooser::" + fileChooserParams.getFilenameHint() + "_"
                        + Arrays.toString(fileChooserParams.getAcceptTypes()));
            } else {
                ToastUtil.showDebug("onShowFileChooser::");
            }
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }
    }

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    private void openImageChooserActivity() {
        checkPermission(new String[]{
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, new BasePermissionActivity.PermissionListener() {

            @Override
            public void onGranted() {
                // 打开系统图库
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,
                        XConstant.RequestCode.REQUEST_ACTION_PICK);
            }

            @Override
            public void onDenied() {
                sendReceiveValue(Uri.EMPTY);
                Toast.makeText(BaseWebViewActivity.this.getApplicationContext(),
                        "请允许拍照权限和存取存储卡权限", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendReceiveValue(Uri uri) {
        try {
            /** 5.0以上 */
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(new Uri[]{uri});
            } else {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(uri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case XConstant.RequestCode.REQUEST_ACTION_PICK:
                if (resultCode != Activity.RESULT_OK) {
                    sendReceiveValue(Uri.EMPTY);
                }
                break;
            //裁剪返回
            case XConstant.RequestCode.REQUEST_CAMERA_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData() == null ? XFileUtil.resultUriTemp : data.getData();
                    sendReceiveValue(uri);
                } else if (resultCode == 0) {
                    sendReceiveValue(Uri.EMPTY);
                    XFileUtil.deleteFile(XFileUtil.uriTemp);
                }
                return;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
