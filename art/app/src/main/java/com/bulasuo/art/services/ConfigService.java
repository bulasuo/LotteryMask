package com.bulasuo.art.services;

import com.bulasuo.art.bean.BaseResponseBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author abu
 *         2018/3/22    19:46
 *         ..
 */

public interface ConfigService {

    @GET("/Lottery_server/get_init_data.php?type=android&appid=com.bulasuo.art")
    Call<BaseResponseBean> apply();

    @Streaming
    @GET
    Call<ResponseBody> applyDownload(@Url String fileUrl);
}
