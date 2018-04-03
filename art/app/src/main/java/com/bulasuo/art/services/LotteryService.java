package com.bulasuo.art.services;


import com.bulasuo.art.bean.BaseResponseBeanData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author abu
 *         2018/3/21    16:30
 *         ..
 */


public interface LotteryService {
    String v = "lotId=001,002,003,113,110,108,109,100,101,102,104,105,106,107,085,086,088,089,090,091,092,093,094,095,096,097,098,099,022,023,024,027,028,029,030,031,032,033,034,035,036,037&";

    @GET("/bonus/getNumberNewBonus.vhtml?lotId=001,002,003,113,110,108,109&format=json")
    Call<BaseResponseBeanData> applyMainList();

    @GET("/bonus/getBonusList.vhtml?pageNum=20&format=json")
    Call<BaseResponseBeanData> applyList(@Query("lotId") String lotId,
                                         @Query("pageNo") int pageNo);

}
