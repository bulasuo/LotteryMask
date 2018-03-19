package com.abu.xbase.diffutil;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.abu.xbase.util.XUtil;

import java.util.List;

/**
 * @author abu
 *         2018/1/4    14:23
 *         bulasuo@foxmail.com
 */

public class DiffCallBack<T> extends DiffUtil.Callback {

    private List<T> mOldDatas, mNewDatas;

    public DiffCallBack(List<T> mOldDatas, List<T> mNewDatas) {
        this.mOldDatas = mOldDatas;
        this.mNewDatas = mNewDatas;
    }

    @Override
    public int getOldListSize() {
        return mOldDatas != null ? mOldDatas.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewDatas != null ? mNewDatas.size() : 0;
    }

    //是不是同一个item 看id
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return XUtil.equals(mOldDatas.get(oldItemPosition), mNewDatas.get(newItemPosition));
    }

    //同一个id的item 是不是内容相同
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return XUtil.equals(mOldDatas.get(oldItemPosition), mNewDatas.get(newItemPosition));
    }

    //该方法在DiffUtil高级用法中用到 ，暂且不提
    @Override
    @Nullable
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return null;
    }
}
