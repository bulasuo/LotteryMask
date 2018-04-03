package com.bulasuo.art.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;

import com.abu.xbase.fragment.BaseFragment;
import com.abu.xbase.util.ToastUtil;
import com.bulasuo.art.R;
import com.bulasuo.art.activity.GZListActivity;

import butterknife.BindView;

/**
 * @author abu
 *         2018/3/21    09:56
 *         ..
 */

public class MoreFragment extends BaseFragment {
    @BindView(R.id.ll_wanfa)
    LinearLayout llWanfa;
    @BindView(R.id.ll_bbgx)
    LinearLayout llBbgx;
    @BindView(R.id.ll_qchc)
    LinearLayout llQchc;
    @BindView(R.id.ll_tccx)
    LinearLayout llTccx;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_more;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        llWanfa.setOnClickListener(v-> GZListActivity.launch(getActivity()));
        llBbgx.setOnClickListener(v->{
            showProgress(true);
            llBbgx.postDelayed(()->{
                showProgress(false);
                new AlertDialog.Builder(getActivity())
                        .setMessage("当前已经是最新版本!")
                        .setPositiveButton("确认", null)
                        .show();
            }, 1500);
        });

        llQchc.setOnClickListener(v->{
            showProgress(true);
            llBbgx.postDelayed(()->{
                showProgress(false);
                ToastUtil.showShort("清除缓存成功!");
            }, 500);
        });
        llTccx.setOnClickListener(v->{
            getActivity().finish();

        });
    }

}
