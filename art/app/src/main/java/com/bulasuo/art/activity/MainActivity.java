package com.bulasuo.art.activity;

import android.os.Bundle;

import com.abu.xbase.activity.BaseActivity;
import com.bulasuo.art.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
