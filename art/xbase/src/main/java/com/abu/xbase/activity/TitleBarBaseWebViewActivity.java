package com.abu.xbase.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.abu.xbase.R;
import com.abu.xbase.util.XDisplayUtil;
import com.abu.xbase.util.XViewUtil;

/**
 * @author abu
 *         2018/2/7    15:22
 *         bulasuo@foxmail.com
 */

public class TitleBarBaseWebViewActivity extends BaseWebViewActivity {

    /**
     * @param context
     * @param url     加载的url
     * @param title   标题
     */
    public static void launch(Context context, String url, String title) {
        BaseWebViewActivity.launch(context, url, title, TitleBarBaseWebViewActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.view_toolbar_xbase, mFrameLayout, true);
        ImageView imageView = view.findViewById(R.id.bar_img_left);
        XViewUtil.visvable(imageView, View.VISIBLE);
        imageView.setOnClickListener(v -> onBackPressed());
        ((TextView) view.findViewById(R.id.bar_tv_title)).setText((String) getObj());
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mBridgeWebView.getLayoutParams();
        int height48 = XDisplayUtil.dip2px(this, 48);
        params.topMargin = height48;
//        mBridgeWebView.setLayoutParams(params);
        params = (FrameLayout.LayoutParams) mTextView.getLayoutParams();
        params.topMargin = height48;
//        mTextView.setLayoutParams(params);
    }
}
