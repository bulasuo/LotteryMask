package com.bulasuo.art.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abu.xbase.activity.BaseActivity;
import com.abu.xbase.util.XViewUtil;
import com.bulasuo.art.R;

import butterknife.BindView;

/**
 * @author abu
 *         2018/3/19    15:14
 *         ..
 */

public class GZActivityDetail extends BaseActivity {

    public static final int TYPE_SSQ = 0;
    public static final int TYPE_FC3D = 1;
    public static final int TYPE_SSC = 2;
    public static final int TYPE_QLC = 3;
    public static final int TYPE_DLT = 4;
    public static final int TYPE_QXC = 5;
    public static final int TYPE_PL3 = 6;
    public static final int TYPE_PL5 = 7;
    public static final int TYPE_K3 = 8;
    public static final int TYPE_11X5 = 9;

    @BindView(R.id.bar_img_left)
    ImageView barImgLeft;
    @BindView(R.id.bar_tv_title)
    TextView barTvTitle;

    public static void launch(Context context, int type) {
        context.startActivity(new Intent(context, GZActivityDetail.class)
                .putExtra(FLAG_OBJ, type)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected Integer getObj() {
        return (int) super.getObj();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getObj()) {
            case TYPE_SSQ:
                setContentView(R.layout.activity_gz_detail_ssq);
                barTvTitle.setText("双色球");
                break;
            case TYPE_FC3D:
                setContentView(R.layout.activity_gz_detail_fc3d);
                barTvTitle.setText("福彩3D");
                break;
            case TYPE_SSC:
                setContentView(R.layout.activity_gz_detail_ssc);
                barTvTitle.setText("时时彩");
                break;
            case TYPE_QLC:
                setContentView(R.layout.activity_gz_detail_qlc);
                barTvTitle.setText("七乐彩");
                break;
            case TYPE_DLT:
                setContentView(R.layout.activity_gz_detail_dlt);
                barTvTitle.setText("大乐透");
                break;
            case TYPE_QXC:
                setContentView(R.layout.activity_gz_detail_qxc);
                barTvTitle.setText("七星彩");
                break;
            case TYPE_PL3:
                setContentView(R.layout.activity_gz_detail_pls);
                barTvTitle.setText("排列三");
                break;
            case TYPE_PL5:
                setContentView(R.layout.activity_gz_detail_plw);
                barTvTitle.setText("排列五");
                break;
            case TYPE_K3:
                setContentView(R.layout.activity_gz_detail_k3);
                barTvTitle.setText("块五");
                break;
            case TYPE_11X5:
                setContentView(R.layout.activity_gz_detail_11x5);
                barTvTitle.setText("11选5");
                break;
            default:
                break;
        }
        initBar();
    }

    private void initBar() {
        XViewUtil.visvable(barImgLeft, View.VISIBLE);
        barImgLeft.setOnClickListener(v -> onBackPressed());
    }
}
