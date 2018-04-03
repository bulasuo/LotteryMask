package com.bulasuo.art.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abu.xbase.fragment.BaseFragment;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.abu.xbase.util.ToastUtil;
import com.bulasuo.art.R;
import com.bulasuo.art.activity.LotteryListActivity;
import com.bulasuo.art.bean.BaseResponseBeanData;
import com.bulasuo.art.bean.LotteryBean;
import com.bulasuo.art.services.AppAPI;
import com.bulasuo.art.services.LotteryService;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author abu
 *         2018/3/21    09:55
 *         ..
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
    @BindView(R.id.rel)
    RelativeLayout rel;
    @BindView(R.id.rel_1)
    RelativeLayout rel1;
    @BindView(R.id.rel_2)
    RelativeLayout rel2;
    @BindView(R.id.rel_3)
    RelativeLayout rel3;
    @BindView(R.id.rel_4)
    RelativeLayout rel4;
    @BindView(R.id.rel_5)
    RelativeLayout rel5;
    @BindView(R.id.rel_6)
    RelativeLayout rel6;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_lottery;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        loadDate();
    }

    private View.OnClickListener onItemClickListener =
            v-> {
                if(v.getTag() == null)
                    return;
                LotteryListActivity.launch(getActivity(), (LotteryBean) v.getTag());
            };

    private void initView() {
        rel.setOnClickListener(onItemClickListener);
        rel1.setOnClickListener(onItemClickListener);
        rel2.setOnClickListener(onItemClickListener);
        rel3.setOnClickListener(onItemClickListener);
        rel4.setOnClickListener(onItemClickListener);
        rel5.setOnClickListener(onItemClickListener);
        rel6.setOnClickListener(onItemClickListener);
    }

    private void loadDate() {
        showProgress(true);
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

    private void updateView(ArrayList<LotteryBean> numberList) {
        try {
            LotteryBean bean = numberList.get(0);
            String bonusBlanceStr = bean.getBlanceStr();
            tvNum.setText(bean.getNumStr());
            tvDate.setText(bean.getBonusTimeStr());
            bean.updatePoints(llPoints);
            tvTotal.setText(bonusBlanceStr);
            bean.mTitle = "双色球";
            rel.setTag(bean);

            bean = numberList.get(1);
            bonusBlanceStr = bean.getBlanceStr();
            tvNum1.setText(bean.getNumStr());
            tvDate1.setText(bean.getBonusTimeStr());
            bean.updatePoints(llPoints1);
            tvTotal1.setText(bonusBlanceStr);
            bean.mTitle = "福彩3D";
            rel1.setTag(bean);

            bean = numberList.get(2);
            bonusBlanceStr = bean.getBlanceStr();
            tvNum2.setText(bean.getBonusTimeStr());
            tvDate2.setText(bean.getBonusTimeStr());
            bean.updatePoints(llPoints2);
            tvTotal2.setText(bonusBlanceStr);
            bean.mTitle = "七乐彩";
            rel2.setTag(bean);

            bean = numberList.get(3);
            bonusBlanceStr = bean.getBlanceStr();
            tvNum3.setText(bean.getBonusTimeStr());
            tvDate3.setText(bean.getBonusTimeStr());
            bean.updatePoints(llPoints3);
            tvTotal3.setText(bonusBlanceStr);
            bean.mTitle = "大乐透";
            rel3.setTag(bean);

            bean = numberList.get(4);
            bonusBlanceStr = bean.getBlanceStr();
            tvNum4.setText(bean.getBonusTimeStr());
            tvDate4.setText(bean.getBonusTimeStr());
            bean.updatePoints(llPoints4);
            tvTotal4.setText(bonusBlanceStr);
            bean.mTitle = "七星彩";
            rel4.setTag(bean);

            bean = numberList.get(5);
            bonusBlanceStr = bean.getBlanceStr();
            tvNum5.setText(bean.getBonusTimeStr());
            tvDate5.setText(bean.getBonusTimeStr());
            bean.updatePoints(llPoints5);
            tvTotal5.setText(bonusBlanceStr);
            bean.mTitle = "排列三";
            rel5.setTag(bean);

            bean = numberList.get(6);
            bonusBlanceStr = bean.getBlanceStr();
            tvNum6.setText(bean.getBonusTimeStr());
            tvDate6.setText(bean.getBonusTimeStr());
            bean.updatePoints(llPoints6);
            tvTotal6.setText(bonusBlanceStr);
            bean.mTitle = "排列五";
            rel6.setTag(bean);

        } catch (Exception e) {
            ToastUtil.showException(e);
        }


    }

}
