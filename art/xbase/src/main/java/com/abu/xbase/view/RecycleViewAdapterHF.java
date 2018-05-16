package com.abu.xbase.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author abu
 * 2018/5/16    16:46
 * bulasuo@foxmail.com
 */
public class RecycleViewAdapterHF extends RecyclerView.Adapter {

    private static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * ViewHolder
         *
         * @param itemView itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * Footer_View_Type起始位置
     */
    private static final int BASE_FOOTER_VIEW_TYPE = Integer.MIN_VALUE;

    /**
     * Header_View_Type起始位置
     */
    private static final int BASE_HEADER_VIEW_TYPE = BASE_FOOTER_VIEW_TYPE + 0x00FFFFFF;

    /**
     * Header、Footer Type的最大位置
     */
    //    private int BASE_View_TYPE = BASE_HEADER_VIEW_TYPE + 0x00FFFFFF;

    /**
     * Header type列表
     */
    private final List<Integer> mHeaderViewTypeList = new ArrayList<Integer>();

    /**
     * Footer type列表
     */
    private final List<Integer> mFooterViewTypeList = new ArrayList<Integer>();

    /**
     * HeaderView引用列表
     */
    private final SparseArray<View> mHeaderViewInfoMap = new SparseArray<View>();

    /**
     * FooterView引用列表
     */
    private final SparseArray<View> mFooterViewInfoMap = new SparseArray<View>();

    /**
     * 填充内部的adpter
     */
    private RecyclerView.Adapter mContentAdapter = null;

    private GridLayoutManager mContentLayoutManager = null;

    /**
     * 是否StaggeredGridView所用的Adpter
     */
    private boolean mIsStaggeredGrid;

    /**
     * 当通过contentAdapter的notifyItemRangeInserted/onItemRangeChanged等方法添加时，
     * 会存在偏移错误，因为两者中itemView的位置存在偏移。
     */
    private RecyclerView.AdapterDataObserver mContentDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeadersCount(), itemCount);
        }

        //重写onItemRangeChanged(int positionStart, int itemCount, Object payload)方法，
        // 否则父类会直接调用onItemRangeChanged(int positionStart, int itemCount)导致不能只刷新数据
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            notifyItemRangeChanged(positionStart + getHeadersCount(), itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int headerViewsCountCount = getHeadersCount();
            notifyItemRangeChanged(fromPosition + headerViewsCountCount,
                    toPosition + headerViewsCountCount + itemCount);
        }
    };

    /**
     * 无参构造适，适用于后期添加内容Adapter的情形（预留构造函数）
     */
    public RecycleViewAdapterHF() {
        this(null, null);
    }

    /**
     * 带有内容Adpter的构造函数
     *
     * @param adapter 内容Adpter
     */
    public RecycleViewAdapterHF(RecyclerView.Adapter adapter) {
        this.mContentAdapter = adapter;
    }

    /**
     * 带有内容Adpter的构造函数
     *
     * @param adapter                内容Adpter
     * @param contentLayoutMananager manager
     */
    public RecycleViewAdapterHF(RecyclerView.Adapter adapter, GridLayoutManager contentLayoutMananager) {
        this.mContentAdapter = adapter;
        this.mContentLayoutManager = contentLayoutMananager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeaderViewType(viewType)) {
            return createHeaderFooterViewHolder(mHeaderViewInfoMap.get(viewType));
        }
        if (isFooterViewType(viewType)) {
            return createHeaderFooterViewHolder(mFooterViewInfoMap.get(viewType));
        }

        return mContentAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getHeadersCount() || position >= getHeadersCount() + mContentAdapter.getItemCount()) {
            return;
        }

        mContentAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderViewTypeList.get(position);
        } else if (isFooterPosition(position)) {
            return mFooterViewTypeList.get(position - mContentAdapter.getItemCount() - getHeadersCount());
        } else {
            return mContentAdapter.getItemViewType(position - getHeadersCount());
        }
    }

    @Override
    public int getItemCount() {
        return getFootersCount() + getHeadersCount() + mContentAdapter.getItemCount();
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);

        mContentAdapter.registerAdapterDataObserver(mContentDataObserver);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);

        mContentAdapter.unregisterAdapterDataObserver(mContentDataObserver);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        adjustSpanSize(recyclerView);

        mContentAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mContentAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    /** ============================ Override Recycler.Adapter end ============================ **/

    /**
     * 获得headers数量
     *
     * @return head count
     */
    public int getHeadersCount() {
        return mHeaderViewTypeList.size();
    }

    /**
     * 获得footers数量
     *
     * @return Foot count
     */
    public int getFootersCount() {
        return mFooterViewTypeList.size();
    }

    /**
     * 内容是否为空
     *
     * @return 是否有数据
     */
    public boolean isEmpty() {
        return mContentAdapter == null || mContentAdapter.getItemCount() == 0;
    }

    /**
     * 删除某一HeaderView
     *
     * @param v 准备删除的View实例
     * @return true:成功；false：不存在该view
     */
    public boolean removeHeaderView(View v) {
        int index = mHeaderViewInfoMap.indexOfValue(v);
        if (index >= 0) {
            //删除map
            int type = mHeaderViewInfoMap.keyAt(index);
            mHeaderViewInfoMap.delete(type);

            //删除typeList
            int position = mHeaderViewTypeList.indexOf(type);
            mHeaderViewTypeList.remove(position);
            notifyItemRemoved(position);
            return true;
        }

        return false;
    }

    /**
     * 删除某一FooterView
     *
     * @param v 准备删除的View实例
     * @return true:成功；false：不存在该view
     */
    public boolean removeFooterView(View v) {
        int index = mFooterViewInfoMap.indexOfValue(v);

        if (index >= 0) {
            //删除map
            int type = mFooterViewInfoMap.keyAt(index);
            mFooterViewInfoMap.delete(type);

            //删除typeList
            int position = mFooterViewTypeList.indexOf(type);
            mFooterViewTypeList.remove(position);
            notifyItemRemoved(position + mContentAdapter.getItemCount() + getHeadersCount());
            return true;
        }

        return false;
    }

    public void removeAllHeaderView() {
        int toDelNums = getHeadersCount();
        mHeaderViewInfoMap.clear();
        mHeaderViewTypeList.clear();
        notifyItemRangeRemoved(0, toDelNums);
    }

    public void removeAllFooterView() {
        int toDelNums = getFootersCount();
        mFooterViewInfoMap.clear();
        mFooterViewTypeList.clear();
        notifyItemRangeRemoved(getHeadersCount() + mContentAdapter.getItemCount(), toDelNums);
    }

    /**
     * 添加HeadView
     *
     * @param view view
     */
    public void addHeaderView(@NonNull View view) {
        //同一个view实例不可添加两次
        if (mFooterViewInfoMap.indexOfValue(view) >= 0) {
            return;
        }

        int id;

        //给无Id对象设置Id，不会与aapt生成的Id冲突
        //使得同一个view对象生成的viewType相同，可复用原来生成的viewType而无需重复生成
        if ((id = view.getId()) < 0) {
            id = View.generateViewId();
            view.setId(id);
        }

        int type = id + BASE_HEADER_VIEW_TYPE;

        mHeaderViewInfoMap.append(type, view);
        mHeaderViewTypeList.add(type);

        //新添加的位置=插入前的总数，而getHeadersCount()=现在的总数=插入前总数+1，因而最终插入位置为
        notifyItemInserted(getHeadersCount() - 1);
    }

    /**
     * 添加FooterView
     *
     * @param view view
     */
    public void addFooterView(@NonNull View view) {
        if (mFooterViewInfoMap.indexOfValue(view) >= 0) {
            return;
        }

        int id;
        if ((id = view.getId()) < 0) {
            id = View.generateViewId();
            view.setId(id);
        }

        int type = id + BASE_FOOTER_VIEW_TYPE;

        mFooterViewInfoMap.append(type, view);
        mFooterViewTypeList.add(type);

        //新添加的位置=插入前的总数，而getItemCount()=现在的总数=插入前总数+1，因而最终插入位置为
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * footer是否存在该View实例
     *
     * @param v view
     * @return true:存在
     */
    public boolean containsFooterView(View v) {
        return mFooterViewInfoMap.indexOfValue(v) >= 0;
    }

    /**
     * header是否存在该View实例
     *
     * @param v view
     * @return true:存在
     */
    public boolean containsHeaderView(View v) {
        return mHeaderViewInfoMap.indexOfValue(v) >= 0;
    }

    public RecyclerView.Adapter getAdapter() {
        return mContentAdapter;
    }

    private boolean isHeaderViewType(int viewType) {
        return mHeaderViewInfoMap.get(viewType) != null;
    }

    private boolean isFooterViewType(int viewType) {
        return mFooterViewInfoMap.get(viewType) != null;
    }

    private boolean isHeaderPosition(int position) {
        return position < mHeaderViewTypeList.size();
    }

    private boolean isFooterPosition(int position) {
        return position >= mHeaderViewTypeList.size() + mContentAdapter.getItemCount();
    }

    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        if (mIsStaggeredGrid) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                    StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
        } else {
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
        }

        return new ViewHolder(view);
    }

    /**
     * 调整Span范围
     *
     * @param recycler recycler
     */
    private void adjustSpanSize(RecyclerView recycler) {
        if (recycler.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();

            //取决于内部的Recycler列数
            if (null != mContentLayoutManager) {
                layoutManager.setSpanCount(mContentLayoutManager.getSpanCount());
            }

            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isHeaderOrFooter = isHeaderPosition(position) || isFooterPosition(position);

                    if (isHeaderOrFooter) {
                        return layoutManager.getSpanCount();
                    }

                    if (null == mContentLayoutManager) {
                        return 1;
                    }
                    return mContentLayoutManager.getSpanSizeLookup().getSpanSize(position - getHeadersCount());
                }
            });
        }

        if (recycler.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            this.mIsStaggeredGrid = true;
        }
    }

    /**
     * 获取头或者尾布局
     *
     * @param viewType view的类型
     * @return viewholder
     */
    public RecyclerView.ViewHolder getHolderFootHeadView(int viewType) {
        if (isHeaderViewType(viewType)) {
            return new ViewHolder(mHeaderViewInfoMap.get(viewType));
        } else if (isFooterViewType(viewType)) {
            return new ViewHolder(mFooterViewInfoMap.get(viewType));
        }
        return null;

    }

}
