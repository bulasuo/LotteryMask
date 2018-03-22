package com.bulasuo.art.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.abu.xbase.fragment.BaseFragment;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.bulasuo.art.R;
import com.bulasuo.art.bean.BaseResponseBean;
import com.bulasuo.art.services.AppAPI;
import com.bulasuo.art.services.LotteryService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author abu
 *         2018/3/21    09:55
 *         bulasuo@foxmail.com
 */

public class LotteryFragment extends BaseFragment {
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_lottery;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadDate();
    }

    private void loadDate(){
        RetrofitUtil.getService(RetrofitUtil.
                getGsonRetrofit(AppAPI.HOST_LOTTERY_MAIN_LIST), LotteryService.class)
                .applyMainList()
                .enqueue(new Callback<BaseResponseBean>() {
                    @Override
                    public void onResponse(Call<BaseResponseBean> call, Response<BaseResponseBean> response) {

                    }

                    @Override
                    public void onFailure(Call<BaseResponseBean> call, Throwable t) {

                    }
                });
    }
}
