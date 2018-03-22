package com.abu.xbase.diffutil;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import com.abu.xbase.util.XUtil;

import java.util.List;

/**
 * @author abu
 *         2018/1/4    14:23
 *         bulasuo@foxmail.com
 */

public abstract class BaseDiffCallBackT<T> extends DiffUtil.Callback {

    private List<T> mOldDatas, mNewDatas;

    public DiffUtil.DiffResult getDiff(List<T> mOldDatas, List<T> mNewDatas, boolean detectMoves) {
        this.mOldDatas = mOldDatas;
        this.mNewDatas = mNewDatas;
        return DiffUtil.calculateDiff(this, detectMoves);
    }

    public boolean dispatchUpdates(boolean loadMore, RecyclerView.Adapter adapter,
                                   List<T> mOldDatas, List<T> mNewDatas, boolean detectMoves) {
        if (loadMore)
            mNewDatas.addAll(0, mOldDatas);
        if (!mOldDatas.equals(mNewDatas)) {
            DiffUtil.DiffResult diffResult = getDiff(mOldDatas, mNewDatas, detectMoves);
            mOldDatas.clear();
            mOldDatas.addAll(mNewDatas);
            diffResult.dispatchUpdatesTo(adapter);
            return true;
        }
        return false;
    }

    @Override
    public int getOldListSize() {
        return mOldDatas != null ? mOldDatas.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewDatas != null ? mNewDatas.size() : 0;
    }

    /**
     * 是不是同一个item 看bean.id
     *
     * @param oldItemPosition
     * @param newItemPosition
     * @return {return mOldDatas.get(oldItemPosition).id
     * == mNewDatas.get(newItemPosition).id;}
     */
    @Override
    public abstract boolean areItemsTheSame(int oldItemPosition, int newItemPosition);

    /**
     * 同一个id的item 是不是内容相同
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return XUtil.equals(mOldDatas.get(oldItemPosition), mNewDatas.get(newItemPosition));
    }

    /**
     * 该方法在DiffUtil高级用法中用到 ，暂且不提
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Override
    @Nullable
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return null;
    }
}
