package com.bulasuo.art.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abu.xbase.activity.BaseActivity;
import com.abu.xbase.diffutil.BaseDiffCallBackT;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.abu.xbase.util.XViewUtil;
import com.bulasuo.art.R;
import com.bulasuo.art.bean.BaseResponseBeanData;
import com.bulasuo.art.bean.LotteryBean;
import com.bulasuo.art.services.AppAPI;
import com.bulasuo.art.services.LotteryService;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author abu
 *         2018/3/26    08:46
 *         bulasuo@foxmail.com
 */

public class LotteryListActivity extends BaseActivity {

    @BindView(R.id.id_recyclerview)
    RecyclerView idRecyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
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

    private MAdapter mAdapter;
    private ArrayList<LotteryBean> datas = new ArrayList<>();
    private MDiffCallBackT<LotteryBean> mDiffCallBackT = new MDiffCallBackT<>();

    public static void launch(Context context, LotteryBean lotteryBean) {
        context.startActivity(new Intent(context, LotteryListActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(FLAG_OBJ, lotteryBean));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_list);
        initBar();
        initView();
    }

    @Override
    protected LotteryBean getObj() {
        return (LotteryBean) super.getObj();
    }

    private void initBar() {
        XViewUtil.visvable(barImgLeft, View.VISIBLE);
        barImgLeft.setOnClickListener(v -> onBackPressed());
        barTvTitle.setText(String.format(Locale.getDefault(),
                "%s开奖走势", getObj().mTitle));

    }

    private void initView() {
        idRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        idRecyclerview.setNestedScrollingEnabled(false);
        mAdapter = new MAdapter();
        mAdapter.setDatas(datas);
        idRecyclerview.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(refreshlayout -> loadMore(false));
        refreshLayout.setOnLoadmoreListener(refreshlayout -> loadMore(true));
//        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setRefreshHeader(new MaterialHeader(this)
                .setColorSchemeColors(ContextCompat.getColor(this, R.color.green_app))
                .setShowBezierWave(false));
        refreshLayout.setRefreshFooter(new BallPulseFooter(this)
                .setNormalColor(ContextCompat.getColor(this, R.color.line_gray))
                .setIndicatorColor(ContextCompat.getColor(this, R.color.green_app))
                .setAnimatingColor(ContextCompat.getColor(this, R.color.green_app))
                .setSpinnerStyle(SpinnerStyle.Scale));

        refreshLayout.autoRefresh();
    }

    private void loadMore(boolean loadMore) {
        RetrofitUtil.getService(RetrofitUtil
                .getGsonRetrofit(AppAPI.HOST_LOTTERY_MAIN_LIST), LotteryService.class)
                .applyList(getObj().lotteryId, loadMore ? datas.size() / 20 + 1 : 1)
                .enqueue(new Callback<BaseResponseBeanData>() {
                    @Override
                    public void onResponse(Call<BaseResponseBeanData> call, Response<BaseResponseBeanData> response) {
                        if (isFinishing()) return;
                        dealResponse(call, response);
                        if (BaseResponseBeanData.isSuccessful(response, true)) {
                            mDiffCallBackT.dispatchUpdates(loadMore, mAdapter,
                                    datas, response.body().data.numberList, true);

                            finishLoadMore(loadMore, true);
                            return;
                        }
                        finishLoadMore(loadMore, true);
                    }

                    @Override
                    public void onFailure(Call<BaseResponseBeanData> call, Throwable t) {
                        if (isFinishing()) return;
                        dealFailure(call, t);
                        finishLoadMore(loadMore, false);
                    }

                });

    }

    private void onItemClick(View v) {
        LotteryBean lotteryBean = (LotteryBean) v.getTag();
        lotteryBean.mTitle = getObj().mTitle;
        LotteryDetailActivity.launch(this, lotteryBean);
    }

    private void finishLoadMore(boolean loadMore, boolean hashMore) {
        if (loadMore)
            refreshLayout.finishLoadmore();
        else
            refreshLayout.finishRefresh();
//        if(hashMore)
//            refreshLayout.setEnableLoadmore(true);
//        refreshLayout.setLoadmoreFinished(!hashMore);

//        dealEmptyView();
    }

    class MAdapter extends RecyclerView.Adapter<MAdapter.MyViewHolder> {
        private ArrayList<LotteryBean> datas;

        public void setDatas(ArrayList<LotteryBean> datas1) {
            datas = datas1;
        }

        public int getIndex(LotteryBean bean) {
            if (datas == null) return 0;
            int i = datas.indexOf(bean);
            if (i == -1)
                i = 0;
            return i;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(getLayoutInflater()
                    .inflate(R.layout.item_lottery, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (datas == null || datas.size() <= position) return;
            LotteryBean bean = datas.get(position);
            holder.itemView.setOnClickListener(LotteryListActivity.this::onItemClick);
            holder.itemView.setTag(bean);
            String bonusBlanceStr = bean.getBlanceStr();
            holder.tvNum.setText(bean.getNumStr());
            holder.tvDate.setText(bean.getBonusTimeStr());
            bean.updatePoints(holder.llPoints);
            holder.tvTotal.setText(bonusBlanceStr);
            holder.tvTitle.setText(getObj().mTitle);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imgEnter;
            TextView tvTitle;
            TextView tvNum;
            TextView tvDate;
            LinearLayout llPoints;
            TextView tvTotal;

            public MyViewHolder(View view) {

                super(view);
                imgEnter = view.findViewById(R.id.img_enter);
                tvTitle = view.findViewById(R.id.tv_title);
                tvNum = view.findViewById(R.id.tv_num);
                tvDate = view.findViewById(R.id.tv_date);
                llPoints = view.findViewById(R.id.ll_points);
                tvTotal = view.findViewById(R.id.tv_total);
            }
        }
    }

    class MDiffCallBackT<T extends LotteryBean> extends BaseDiffCallBackT<T> {
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            boolean b = TextUtils.equals(mOldDatas.get(oldItemPosition).lotteryId,
                    mNewDatas.get(newItemPosition).lotteryId) &&
                    TextUtils.equals(mOldDatas.get(oldItemPosition).issueNum,
                            mNewDatas.get(newItemPosition).issueNum);
            return b;
        }
    }
}
