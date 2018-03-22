package com.abu.xbase.util;

import android.text.SpannableString;

import java.io.Serializable;

/**
 * @author abu
 *         2017/12/12    10:44
 *         bulasuo@foxmail.com
 */

public class SpannableStringSerializable extends SpannableString implements Serializable {
    public SpannableStringSerializable(CharSequence source) {
        super(source);
    }
}
