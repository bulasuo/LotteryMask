package com.abu.xbase.sliderbanner;

import android.view.LayoutInflater;
import android.view.View;

import com.abu.xbase.R;

import java.util.List;

import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.DotView;
import in.srain.cube.views.banner.BannerAdapter;
import in.srain.cube.views.banner.SliderBanner;

public class SliderBannerController<T> {

    public final static int Height = (int) (LocalDisplay.SCREEN_WIDTH_PIXELS * 1f / 500 * 281f);
    private SliderBanner mSliderBanner;
    private InnerAdapter<T> mBannerAdapter = new InnerAdapter<>();
    private DotView mDotView;
    private ViewBinder mViewBinder;

    public interface ViewBinder<T>{
        View onBindItemView(LayoutInflater layoutInflater, int position, T data);
    }

    public SliderBannerController(SliderBanner sliderBanner, ViewBinder<T> viewBinder) {

        mViewBinder = viewBinder;
        mDotView = sliderBanner.findViewById(R.id.slider_banner_indicator_xbase);

        mSliderBanner = sliderBanner;

        sliderBanner.setAdapter(mBannerAdapter);
    }

    public void play(List<T> list) {
        mBannerAdapter.setData(list);
        mBannerAdapter.notifyDataSetChanged();
        mSliderBanner.setDotNum(list.size());
        mSliderBanner.beginPlay();
    }

    private class InnerAdapter<T> extends BannerAdapter {
        private List<T> mDataList;

        public void setData(List<T> datas) {
            mDataList = datas;
        }

        public T getItem(int position) {
            if (mDataList == null)
                return null;
            return mDataList.get(position);
        }

        @Override
        public int getPositionForIndicator(int position) {
            if (null == mDataList || mDataList.size() == 0) {
                return 0;
            }
            return position % mDataList.size();
        }

        @Override
        public View getView(LayoutInflater layoutInflater, int position) {
            position = getPositionForIndicator(position);
            if(mViewBinder != null) return mViewBinder.onBindItemView(layoutInflater, position, getItem(position));
            return null;
        }

        @Override
        public int getCount() {
            if (mDataList == null) {
                return 0;
            }
            return Integer.MAX_VALUE;
        }
    }
}
