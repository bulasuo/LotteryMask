package com.bulasuo.art.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abu.xbase.fragment.BaseFragment;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XViewUtil;
import com.bulasuo.art.R;
import com.bulasuo.art.bean.BaseResponseBeanData;
import com.bulasuo.art.bean.LotteryBean;
import com.bulasuo.art.services.AppAPI;
import com.bulasuo.art.services.LotteryService;

import org.greenrobot.greendao.annotation.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author abu
 *         2018/3/21    09:55
 *         bulasuo@foxmail.com
 */

public class LotteryFragment extends BaseFragment {
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
    @BindView(R.id.img_enter_1)
    ImageView imgEnter1;
    @BindView(R.id.tv_title_1)
    TextView tvTitle1;
    @BindView(R.id.tv_num_1)
    TextView tvNum1;
    @BindView(R.id.tv_date_1)
    TextView tvDate1;
    @BindView(R.id.ll_points_1)
    LinearLayout llPoints1;
    @BindView(R.id.tv_total_1)
    TextView tvTotal1;
    @BindView(R.id.img_enter_2)
    ImageView imgEnter2;
    @BindView(R.id.tv_title_2)
    TextView tvTitle2;
    @BindView(R.id.tv_num_2)
    TextView tvNum2;
    @BindView(R.id.tv_date_2)
    TextView tvDate2;
    @BindView(R.id.ll_points_2)
    LinearLayout llPoints2;
    @BindView(R.id.tv_total_2)
    TextView tvTotal2;
    @BindView(R.id.img_enter_3)
    ImageView imgEnter3;
    @BindView(R.id.tv_title_3)
    TextView tvTitle3;
    @BindView(R.id.tv_num_3)
    TextView tvNum3;
    @BindView(R.id.tv_date_3)
    TextView tvDate3;
    @BindView(R.id.ll_points_3)
    LinearLayout llPoints3;
    @BindView(R.id.tv_total_3)
    TextView tvTotal3;
    @BindView(R.id.img_enter_4)
    ImageView imgEnter4;
    @BindView(R.id.tv_title_4)
    TextView tvTitle4;
    @BindView(R.id.tv_num_4)
    TextView tvNum4;
    @BindView(R.id.tv_date_4)
    TextView tvDate4;
    @BindView(R.id.ll_points_4)
    LinearLayout llPoints4;
    @BindView(R.id.tv_total_4)
    TextView tvTotal4;
    @BindView(R.id.img_enter_5)
    ImageView imgEnter5;
    @BindView(R.id.tv_title_5)
    TextView tvTitle5;
    @BindView(R.id.tv_num_5)
    TextView tvNum5;
    @BindView(R.id.tv_date_5)
    TextView tvDate5;
    @BindView(R.id.ll_points_5)
    LinearLayout llPoints5;
    @BindView(R.id.tv_total_5)
    TextView tvTotal5;
    @BindView(R.id.img_enter_6)
    ImageView imgEnter6;
    @BindView(R.id.tv_title_6)
    TextView tvTitle6;
    @BindView(R.id.tv_num_6)
    TextView tvNum6;
    @BindView(R.id.tv_date_6)
    TextView tvDate6;
    @BindView(R.id.ll_points_6)
    LinearLayout llPoints6;
    @BindView(R.id.tv_total_6)
    TextView tvTotal6;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_lottery;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadDate();
    }

    private void loadDate() {
        RetrofitUtil.getService(RetrofitUtil.
                getGsonRetrofit(AppAPI.HOST_LOTTERY_MAIN_LIST), LotteryService.class)
                .applyMainList()
                .enqueue(new Callback<BaseResponseBeanData>() {
                    @Override
                    public void onResponse(Call<BaseResponseBeanData> call, Response<BaseResponseBeanData> response) {
                        if (getActivity().isFinishing()) return;
                        dealResponse(call, response);
                        if (BaseResponseBeanData.isSuccessful(response, true)) {
                            updateView(response.body().data.numberList);
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponseBeanData> call, Throwable t) {
                        dealFailure(call, t);
                    }
                });
    }

    private static final String NumStr = "第%s期";
    private static final SimpleDateFormat formatIn =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat formatOut =
            new SimpleDateFormat("MM-dd EEEE", Locale.getDefault());

    private void updatePoints(@NotNull LinearLayout linearLayout,
                              String[] baseCodes, String[] specCodes) {
        try {
            TextView textView;
            for (int i = 0, j = linearLayout.getChildCount(); i < j; i++) {
                textView = (TextView) linearLayout.getChildAt(i);
                if (i < baseCodes.length) {
                    textView.setText(baseCodes[i]);
                    textView.setBackgroundResource(R.drawable.ic_circle_bg);
                    XViewUtil.visvable(textView, View.VISIBLE);
                } else if (i < baseCodes.length + specCodes.length) {
                    textView.setText(specCodes[i - baseCodes.length]);
                    textView.setBackgroundResource(R.drawable.ic_circle_red_bg);
                    XViewUtil.visvable(textView, View.VISIBLE);
                } else {
                    XViewUtil.visvable(textView, View.INVISIBLE);
                }
            }
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

    /**
     * 宽度5,小数2位,右对齐,左边不足补空格
     */
    private static final String BlanceFormate = "%5.2f亿";

    private String getBlanceStr(String bonusBlanceStr) {
        float bonusBlance = Float.valueOf(bonusBlanceStr) / 100000000;
        return bonusBlance > 1
                ? String.format(Locale.getDefault(), BlanceFormate, bonusBlance)
                : null;
    }

    private void updateView(ArrayList<LotteryBean> numberList) {
        try {
            LotteryBean bean = numberList.get(0);
            String[] baseCodes = bean.getBaseCodes();
            String[] specCodes = bean.getSpecCodes();
            String bonusBlanceStr = getBlanceStr(bean.bonusBlance);
            tvNum.setText(String.format(Locale.getDefault(), NumStr, bean.issueNum));
            tvDate.setText(formatOut.format(formatIn.parse(bean.bonusTime)));
            updatePoints(llPoints, baseCodes, specCodes);
            tvTotal.setText(bonusBlanceStr);

            bean = numberList.get(1);
            baseCodes = bean.getBaseCodes();
            specCodes = bean.getSpecCodes();
            bonusBlanceStr = getBlanceStr(bean.bonusBlance);
            tvNum1.setText(String.format(Locale.getDefault(), NumStr, bean.issueNum));
            tvDate1.setText(formatOut.format(formatIn.parse(bean.bonusTime)));
            updatePoints(llPoints1, baseCodes, specCodes);
            tvTotal1.setText(bonusBlanceStr);

            bean = numberList.get(2);
            baseCodes = bean.getBaseCodes();
            specCodes = bean.getSpecCodes();
            bonusBlanceStr = getBlanceStr(bean.bonusBlance);
            tvNum2.setText(String.format(Locale.getDefault(), NumStr, bean.issueNum));
            tvDate2.setText(formatOut.format(formatIn.parse(bean.bonusTime)));
            updatePoints(llPoints2, baseCodes, specCodes);
            tvTotal2.setText(bonusBlanceStr);

            bean = numberList.get(3);
            baseCodes = bean.getBaseCodes();
            specCodes = bean.getSpecCodes();
            bonusBlanceStr = getBlanceStr(bean.bonusBlance);
            tvNum3.setText(String.format(Locale.getDefault(), NumStr, bean.issueNum));
            tvDate3.setText(formatOut.format(formatIn.parse(bean.bonusTime)));
            updatePoints(llPoints3, baseCodes, specCodes);
            tvTotal3.setText(bonusBlanceStr);

            bean = numberList.get(4);
            baseCodes = bean.getBaseCodes();
            specCodes = bean.getSpecCodes();
            bonusBlanceStr = getBlanceStr(bean.bonusBlance);
            tvNum4.setText(String.format(Locale.getDefault(), NumStr, bean.issueNum));
            tvDate4.setText(formatOut.format(formatIn.parse(bean.bonusTime)));
            updatePoints(llPoints4, baseCodes, specCodes);
            tvTotal4.setText(bonusBlanceStr);

            bean = numberList.get(5);
            baseCodes = bean.getBaseCodes();
            specCodes = bean.getSpecCodes();
            bonusBlanceStr = getBlanceStr(bean.bonusBlance);
            tvNum5.setText(String.format(Locale.getDefault(), NumStr, bean.issueNum));
            tvDate5.setText(formatOut.format(formatIn.parse(bean.bonusTime)));
            updatePoints(llPoints5, baseCodes, specCodes);
            tvTotal5.setText(bonusBlanceStr);

            bean = numberList.get(6);
            baseCodes = bean.getBaseCodes();
            specCodes = bean.getSpecCodes();
            bonusBlanceStr = getBlanceStr(bean.bonusBlance);
            tvNum6.setText(String.format(Locale.getDefault(), NumStr, bean.issueNum));
            tvDate6.setText(formatOut.format(formatIn.parse(bean.bonusTime)));
            updatePoints(llPoints6, baseCodes, specCodes);
            tvTotal6.setText(bonusBlanceStr);

        } catch (Exception e) {
            ToastUtil.showException(e);
        }


    }


}
