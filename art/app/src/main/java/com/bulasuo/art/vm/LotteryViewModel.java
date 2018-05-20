package com.bulasuo.art.vm;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;

import com.bulasuo.art.bean.LotteryBean;

/**
 * @author abu
 * 2018/5/4    15:36
 * bulasuo@foxmail.com
 */
public class LotteryViewModel extends ViewModel {

    private ObservableArrayList<LotteryBean> datas = new ObservableArrayList<>();



    public ObservableArrayList<LotteryBean> getDatas() {
        return datas;
    }

    public void setDatas(ObservableArrayList<LotteryBean> datas) {
        this.datas = datas;
    }

    /*@BindingAdapter("bind:lottery_datas")
    public static void setRecycleViewDatas(RecyclerView view, List<LotteryBean> datas)
    {
        ((ProductAdapter)view.getAdapter()).setData(items);
    }*/
}
