package com.bulasuo.art.bean;

import android.text.TextUtils;

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


}
