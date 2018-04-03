package com.abu.xbase.activity;

import android.support.v7.app.AppCompatActivity;

import java.util.Iterator;
import java.util.Stack;

/**
 * 处理回退事件
 *
 * @author abu
 *         2017/11/13    09:21
 *         ..
 */

public abstract class BaseBackPressedActivity extends AppCompatActivity {

    /**
     * 返回事件栈
     */
    private Stack<BackListener> backStack;

    /**
     * 返回事件监听器
     */
    public interface BackListener {
        /**
         * 返回按钮事件
         *
         * @return 是否拦截事件
         */
        boolean onBack();
    }

    /**
     * remove 第一个请求的listener
     *
     * @param listener {@link BackListener}
     */
    public void removeBackListener(BackListener listener) {
        if (backStack == null) {
            return;
        }
        Iterator<BackListener> i = backStack.iterator();
        for (BackListener l = null; i.hasNext(); l = i.next()) {
            if (l == listener) {
                i.remove();
                break;
            }
        }
    }

    /**
     * add 监听器
     *
     * @param listener {@link BackListener}
     */
    public void addBackListener(BackListener listener) {
        if (backStack == null) {
            backStack = new Stack<>();
        }
        backStack.addElement(listener);
    }


    /**
     * pop返回事件栈,直至事件被拦截或end
     *
     * @return 是否拦截返回事件
     */
    protected boolean onBack() {
        if (backStack != null) {
            for (; !backStack.isEmpty(); ) {
                if (backStack.pop().onBack()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!onBack()) {
            super.onBackPressed();
        }
    }
}
