package com.abu.xbase.util;

import android.support.annotation.IntDef;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.greendao.annotation.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abu
 *         2018/3/7    14:55
 *         ..
 */

public class XViewUtil {

    public XViewUtil() {
        throw new IllegalArgumentException("please use static method!");
    }

    /**
     * @param viewGroup
     * @param selectView
     * @return true: 入参和原来状态不一样并且置新
     * false:入参和原来状态一样或者没有找到selectView
     */
    public static boolean selectChild(@NotNull ViewGroup viewGroup,
                                      @NotNull View selectView) {
        View view;
        boolean r = false;
        for (int i = 0, j = viewGroup.getChildCount(); i < j; i++) {
            view = viewGroup.getChildAt(i);
            if (view.getId() == selectView.getId())
                r = select(view, true);
            else
                select(view, false);
        }
        return r;
    }

    /**
     * @param view
     * @param select true if the view must be selected, false otherwise
     * @return true: 入参和原来状态不一样并且置新   false:入参和原来状态一样
     */
    public static boolean select(@NotNull View view, boolean select) {
        if (view.isSelected() != select) {
            view.setSelected(select);
            return true;
        }
        return false;
    }

    /**
     * @hide
     */
    @IntDef({View.VISIBLE, View.INVISIBLE, View.GONE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Visibility {}

    /**
     * @param view
     * @param visibility @param visibility One of {@link View#VISIBLE},
     *                   {@link View#INVISIBLE}, or {@link View#GONE}.
     * @return true: 入参和原来状态不一样并且置新   false:otherwise
     */
    public static boolean visvable(@NotNull View view, @Visibility int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
            return true;
        }
        return false;
    }
}
