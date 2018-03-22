package com.bulasuo.art.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abu.xbase.activity.BaseActivity;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.abu.xbase.util.ImageLoaderUtils;
import com.abu.xbase.util.ThreadPool;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XViewUtil;
import com.bulasuo.art.R;
import com.bulasuo.art.services.ConfigService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import butterknife.BindView;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author abu
 *         2018/3/22    20:13
 *         bulasuo@foxmail.com
 */

public class DownloadActivity extends BaseActivity {
    @BindView(R.id.view_toolbar)
    Guideline viewToolbar;
    @BindView(R.id.bar_img_left)
    ImageView barImgLeft;
    @BindView(R.id.bar_tv_left)
    TextView barTvLeft;
    @BindView(R.id.bar_tv_title)
    TextView barTvTitle;
    @BindView(R.id.bar_tv_right)
    TextView barTvRight;
    @BindView(R.id.bar_img_right)
    ImageView barImgRight;
    @BindView(R.id.img_)
    ImageView img;

    /**
     * @param context
     * @param obj     下载的url FLAG_OBJ {@link #getObj()}
     */
    public static void launch(Context context, Serializable obj) {
        context.startActivity(new Intent(context, DownloadActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(FLAG_OBJ, obj));
    }

    @Override
    protected String getObj() {
        return (String) super.getObj();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initBar();
        ImageLoaderUtils.displayWithoutCache(this,
                img, "http://dycpcc.cpapp.diyiccapp.com/appqgtp/999.png");
        loadApk();
    }

    private void initBar() {
        XViewUtil.visvable(barImgLeft, View.VISIBLE);
        barImgLeft.setOnClickListener(v -> onBackPressed());
    }

    private void loadApk() {
        showProgress(true);
        HttpUrl httpUrl = HttpUrl.parse(getObj());
        ThreadPool.getPool().execute(() ->{
            try{
                Response<ResponseBody> response =
                        RetrofitUtil.getService(RetrofitUtil.getDownloadRetrofit(
                                httpUrl.scheme() + "://" + httpUrl.host() + "/"),
                                ConfigService.class)
                                .applyDownload(httpUrl.uri().getRawPath())
                                .execute();

                ToastUtil.showDebug("xxxxxxxxxx");
                ToastUtil.showDebug("applyDownload_onResponse_start::contentLength()-"
                        +response.body().contentLength());
                int i=0;
                InputStream is = null;
                FileOutputStream fos = null;
                BufferedInputStream bis = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(
                            DownloadActivity.this.getExternalCacheDir() + "/apk/"
                                    + "text_img.apk");
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    fos = new FileOutputStream(file);
                    bis = new BufferedInputStream(is);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        i += len;
                        // TODO: 2018/3/22 进度 
                        fos.write(buffer, 0, len);
                        fos.flush();
                    }
                } catch (IOException e) {
                    ToastUtil.showException(e);
                }finally {
                    if(fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            ToastUtil.showException(e);
                        }
                    }
                    if(bis != null){
                        try{
                            bis.close();
                        }catch (IOException e){
                            ToastUtil.showException(e);
                        }
                    }
                    if(is != null){
                        try{
                            is.close();
                        }catch (IOException e){
                            ToastUtil.showException(e);
                        }
                    }
                }
                ToastUtil.showDebug("applyDownload_onResponse_end::-"+i);
                DownloadActivity.this.runOnUiThread(()->{
                    showProgress(false);
                });

            }catch (Exception e){
                ToastUtil.showDebug("err");
                ToastUtil.showException(e);
            }
        });

    }
}
