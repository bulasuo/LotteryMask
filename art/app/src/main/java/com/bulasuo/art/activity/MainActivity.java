package com.bulasuo.art.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abu.xbase.activity.BaseActivity;
import com.abu.xbase.fragment.BaseFragment;
import com.abu.xbase.util.XUtil;
import com.abu.xbase.util.XViewUtil;
import com.bulasuo.art.R;
import com.bulasuo.art.fragment.LotteryFragment;
import com.bulasuo.art.fragment.MoreFragment;
import com.bulasuo.art.fragment.NewsFragment;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private void onTabNews() {
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
