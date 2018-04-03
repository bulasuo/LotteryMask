package com.bulasuo.art.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.abu.xbase.activity.BaseActivity;
import com.abu.xbase.activity.BasePermissionActivity;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.abu.xbase.util.ImageLoaderUtils;
import com.abu.xbase.util.SharePrefUtil;
import com.abu.xbase.util.ThreadPool;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XUtil;
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
 *         ..
 */

public class DownloadActivity extends BaseActivity {
    @BindView(R.id.img_)
    ImageView img;

    /**
     * @param context
     * @param obj     下载的url FLAG_OBJ {@link #getObj()}
     */
    public static void launch(BasePermissionActivity context, Serializable obj) {

        context.checkPermission(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new BasePermissionActivity.PermissionListener() {
                    @Override
                    public void onGranted() {
                        context.startActivity(new Intent(context, DownloadActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(FLAG_OBJ, obj));
                    }

                    @Override
                    public void onDenied() {
                        new AlertDialog.Builder(context)
                                .setMessage("请允许存取存储卡权限!")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", (dialog, which) -> {
                                    dialog.dismiss();
                                    XUtil.jump2AppDetailSettings(context);
                                })
                                .show();
                    }
                });
    }

    @Override
    protected String getObj() {
        return (String) super.getObj();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ImageLoaderUtils.displayWithoutCache(this,
                img, "http://dycpcc.cpapp.diyiccapp.com/appqgtp/999.png");
        loadApk();
    }

    private void loadApk() {
        showProgress(true);
        undateProgressIfShowing(0);
        HttpUrl httpUrl = HttpUrl.parse(getObj());
        ThreadPool.getPool().execute(() ->{
            try{
                Response<ResponseBody> response =
                        RetrofitUtil.getService(RetrofitUtil.getDownloadRetrofit(
                                httpUrl.scheme() + "://" + httpUrl.host() + "/"),
                                ConfigService.class)
                                .applyDownload(httpUrl.uri().getRawPath())
                                .execute();
                long contentLength = response.body().contentLength();
                int lastProgress = 0;
                long lastUpdateProgressTime = 0;
                ToastUtil.showDebug("xxxxxxxxxx");
                ToastUtil.showDebug("applyDownload_onResponse_start::contentLength()-"
                        +contentLength);
                int i=0;
                InputStream is = null;
                FileOutputStream fos = null;
                BufferedInputStream bis = null;
                File file = null;
                boolean success = false;
                try {
                    is = response.body().byteStream();
                    file = new File(
                            DownloadActivity.this.getExternalCacheDir() + "/apk/"
                                    + "cp.apk");
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    fos = new FileOutputStream(file);
                    bis = new BufferedInputStream(is);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        i += len;
                        if(contentLength > 0 && i <= contentLength) {
                            long currT = System.currentTimeMillis();
                            if(currT - lastUpdateProgressTime > 200){
                                int finalProgress = (int) (i * 100 / contentLength);
                                if(finalProgress != lastProgress) {
                                    lastUpdateProgressTime = currT;
                                    lastProgress = finalProgress;
                                    DownloadActivity.this.runOnUiThread(() -> {
                                        ToastUtil.showDebug("finalProgress:" + finalProgress);
                                        undateProgressIfShowing(finalProgress);
                                    });
                                }
                            }


                        }
                        fos.write(buffer, 0, len);
                        fos.flush();
                    }
                    success = true;
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
                try{
                    if(success && file != null){
                        String packageName = XUtil.getApkPackageName(this, file);
                        ToastUtil.showDebug("packageName::"+packageName);
                        SharePrefUtil.saveString(this, "packageName", packageName);
                        XUtil.installApk(this, file);

                    }
                }catch (Exception e){
                    ToastUtil.showException(e);
                }
                ToastUtil.showDebug("applyDownload_onResponse_end::-"+i);
                DownloadActivity.this.runOnUiThread(()-> showProgress(false));

            }catch (Exception e){
                ToastUtil.showDebug("err");
                ToastUtil.showException(e);
            }
        });

    }
}
