package com.bulasuo.art.bean;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XViewUtil;
import com.bulasuo.art.R;

import org.greenrobot.greendao.annotation.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author abu
 *         2018/3/21    16:15
 *         bulasuo@foxmail.com
 */

public class LotteryBean {
    public String
            lotteryId,
            issueNum,
            bonusTime,
            rewardTime,
            baseCode,
            specCode,
            saleTotal,
            winName,
            winCount,
            winMoney,
            bonusBlance;

    public String[] getBaseCodes(){
        if(!TextUtils.isEmpty(baseCode))
            return baseCode.split(",");
        else
            return new String[0];
    }

    public String[] getSpecCodes(){
        if(!TextUtils.isEmpty(specCode))
            return specCode.split(",");
        else
            return new String[0];
    }

    public int
            affirm;


    /**
     * 宽度5,小数2位,右对齐,左边不足补空格
     */
    private static final String BlanceFormate = "%5.2f亿";

    public String getBlanceStr() {
        if(bonusBlance == null) return null;
        float bonusBlance1 = Float.valueOf(bonusBlance) / 100000000;
        return bonusBlance1 > 1
                ? String.format(Locale.getDefault(), BlanceFormate, bonusBlance1)
                : null;
    }

    private static final String NumStr = "第%s期";
    private static final SimpleDateFormat formatIn =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat formatOut =
            new SimpleDateFormat("MM-dd EEEE", Locale.getDefault());

    public String getNumStr(){
        return String.format(Locale.getDefault(), NumStr, issueNum);
    }

    public String getBonusTimeStr(){
        try {
            return formatOut.format(formatIn.parse(bonusTime));
        } catch (ParseException e) {
//            ToastUtil.showException(e);
        }
        return null;
    }

    public void updatePoints(@NotNull LinearLayout linearLayout) {
        try {
            String[] baseCodes = getBaseCodes();
            String[] specCodes = getSpecCodes();
            TextView textView;
            for (int i = 0, j = linearLayout.getChildCount(); i < j; i++) {
                textView = (TextView) linearLayout.getChildAt(i);
                if (i < baseCodes.length) {
                    textView.setText(baseCodes[i]);
                    textView.setBackgroundResource(R.drawable.ic_circle_bg);
                    XViewUtil.visvable(textView, View.VISIBLE);
                } else if (i < baseCodes.length + specCodes.length) {
                    textView.setText(specCodes[i - baseCodes.length]);
                    textView.setBackgroundResource(R.drawable.ic_circle_red_bg);
                    XViewUtil.visvable(textView, View.VISIBLE);
                } else {
                    XViewUtil.visvable(textView, View.INVISIBLE);
                }
            }
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

}
