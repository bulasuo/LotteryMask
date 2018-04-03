package com.abu.xbase.view;

import android.graphics.Path;

/**
 * @author abu
 *         2018/3/3    23:02
 *         ..
 */

public class XPath extends Path {
    public float mLastX, mLastY;

    @Override
    public void quadTo(float x1, float y1, float x2, float y2) {
        mLastX = x2;
        mLastY = y2;
        super.quadTo(x1, y1, x2, y2);
    }

    @Override
    public void moveTo(float x, float y) {
        mLastX = x;
        mLastY = y;
        super.moveTo(x, y);
    }
}
