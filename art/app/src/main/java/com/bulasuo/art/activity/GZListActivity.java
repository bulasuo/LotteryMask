package com.bulasuo.art.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abu.xbase.activity.BaseActivity;
import com.abu.xbase.util.XViewUtil;
import com.bulasuo.art.R;

import butterknife.BindView;

/**
 * @author abu
 *         2018/3/19    15:14
 *         bulasuo@foxmail.com
 */

public class GZListActivity extends BaseActivity {

    @BindView(R.id.view_toolbar)
    Guideline viewToolbar;
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
    @BindView(R.id.ll_toolbar)
    LinearLayout llToolbar;
    @BindView(R.id.ll_ssq)
    LinearLayout llSsq;
    @BindView(R.id.ll_fc3d)
    LinearLayout llFc3d;
    @BindView(R.id.ll_ssc)
    LinearLayout llSsc;
    @BindView(R.id.ll_qlc)
    LinearLayout llQlc;
    @BindView(R.id.ll_dlt)
    LinearLayout llDlt;
    @BindView(R.id.ll_qxc)
    LinearLayout llQxc;
    @BindView(R.id.ll_pl3)
    LinearLayout llPl3;
    @BindView(R.id.ll_pl5)
    LinearLayout llPl5;
    @BindView(R.id.ll_k3)
    LinearLayout llK3;
    @BindView(R.id.ll_11x5)
    LinearLayout ll11x5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gz_list);
        initBar();

        llSsq.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_SSQ));
        llFc3d.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_FC3D));
        llSsc.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_SSC));
        llQlc.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_QLC));
        llDlt.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_DLT));
        llQxc.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_QXC));
        llPl3.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_PL3));
        llPl5.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_PL5));
        llK3.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_K3));
        ll11x5.setOnClickListener(v->
                GZActivityDetail.launch(this, GZActivityDetail.TYPE_11X5));
    }

    private void initBar(){
        XViewUtil.visvable(barImgLeft, View.VISIBLE);
        barImgLeft.setOnClickListener(v->onBackPressed());
        barTvTitle.setText("玩法规则");
    }
}
