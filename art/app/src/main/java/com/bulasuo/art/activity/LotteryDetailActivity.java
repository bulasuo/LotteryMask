package com.bulasuo.art.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.abu.xbase.activity.BaseActivity;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XDisplayUtil;
import com.abu.xbase.util.XUtil;
import com.abu.xbase.util.XViewUtil;
import com.bulasuo.art.R;
import com.bulasuo.art.bean.LotteryBean;

import java.text.DecimalFormat;
import java.util.Locale;

import butterknife.BindView;

/**
 * @author abu
 *         2018/3/26    16:08
 *         bulasuo@foxmail.com
 */

public class LotteryDetailActivity extends BaseActivity {

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
    @BindView(R.id.img_enter)
    ImageView imgEnter;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_points)
    LinearLayout llPoints;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_bonus)
    TextView tvBonus;
    @BindView(R.id.tv_total_sale)
    TextView tvTotalSale;
    @BindView(R.id.tab_detail)
    TableLayout tabDetail;

    public static void launch(Context context, LotteryBean lotteryBean) {
        context.startActivity(new Intent(context, LotteryDetailActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(FLAG_OBJ, lotteryBean));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_detail);
        initBar();
        initView();
    }

    @Override
    protected LotteryBean getObj() {
        return (LotteryBean) super.getObj();
    }

    private String url = null;
    private void initBarRightImg(){
        switch (getObj().mTitle) {
            case "双色球":
                url = "http://m.500.com/datachart/ssq/jb.html";
                break;
            case "福彩3D":
                url = "http://m.500.com/datachart/sd/";
                break;
            case "七乐彩":
                url = "http://m.500.com/datachart/qlc/jb.html";
                break;
            case "大乐透":
                url = "http://m.500.com/datachart/dlt/jb.html";
                break;
            case "七星彩":
                url = "http://m.500.com/datachart/qxc/zx/0.html";
                break;
            case "排列三":
                url = "http://m.500.com/datachart/pls/jb.html";
                break;
            case "排列五":
                url = "http://m.500.com/datachart/plw/zx/0.html";
                break;
            default:
                url = "";
                break;
        }
        if (!TextUtils.isEmpty(url)) {
            XViewUtil.visvable(barImgRight, View.VISIBLE);
            barImgRight.setPadding(5, XDisplayUtil.dip2px(this, 15), 5, 5);
            barImgRight.setImageResource(R.drawable.ic_zoushi);
            barImgRight.setOnClickListener(v ->
                    AppTitleBarBaseWebViewActivity.launch(this, url,
                            String.format(Locale.getDefault(),
                                    "%s走势图", getObj().mTitle)));
        }
    }

    private void initBar() {
        barTvTitle.setText(String.format(Locale.getDefault(),
                "%s开奖详情", getObj().mTitle));
        XViewUtil.visvable(barImgLeft, View.VISIBLE);
        barImgLeft.setOnClickListener(v -> onBackPressed());
        initBarRightImg();
    }

    private void initView() {
        LotteryBean bean = getObj();
        String bonusBlanceStr = bean.getBlanceStr();
        tvNum.setText(bean.getNumStr());
        tvDate.setText(bean.getBonusTimeStr());
        bean.updatePoints(llPoints);
        tvTotal.setText(bonusBlanceStr);
        tvTitle.setText(getObj().mTitle);

        DecimalFormat myformat = new DecimalFormat();
        myformat.applyPattern("##,###");
        String bonus = myformat.format(Double.valueOf(getObj().bonusBlance == null ? "0" : getObj().bonusBlance));
        tvBonus.setText(XUtil.highLightString(bonus + "元", bonus));
        String totalSale = myformat.format(Double.valueOf(getObj().saleTotal == null ? "0" : getObj().saleTotal));
        tvTotalSale.setText(XUtil.highLightString(totalSale + "元", totalSale));
        try {
            String[] winNames = getObj().winName.split(",");
            String[] winCounts = getObj().winCount.split(",");
            String[] winMoneys = getObj().winMoney.split(",");

            for (int i = 0, j = winNames.length; i < j; i++) {
                View tableRow = getLayoutInflater().inflate(R.layout.item_tabrow_, null);
                tabDetail.addView(tableRow);
                ((TextView)tableRow.findViewById(R.id.tv_0)).setText(winNames[i]);
                ((TextView)tableRow.findViewById(R.id.tv_1)).setText(winCounts[i]);
                ((TextView)tableRow.findViewById(R.id.tv_2)).setText(myformat.format(Double.valueOf(winMoneys[i])));
            }

        } catch (Exception e) {
            ToastUtil.showException(e);
        }

    }

}
