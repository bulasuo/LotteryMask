package com.bulasuo.art.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abu.xbase.activity.BaseActivity;
import com.abu.xbase.activity.BasePermissionActivity;
import com.abu.xbase.retrofit.RetrofitUtil;
import com.abu.xbase.util.ImageLoaderUtils;
import com.abu.xbase.util.SharePrefUtil;
import com.abu.xbase.util.ThreadPool;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XFileUtil;
import com.abu.xbase.util.XUtil;
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
                        // TODO: 2018/3/22 进度
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
                        PackageManager pm = this.getPackageManager();
                        PackageInfo info = pm.getPackageArchiveInfo(
                                file.getAbsolutePath(),
                                PackageManager.GET_ACTIVITIES);
                        String packageName = info.applicationInfo.packageName;
                        ToastUtil.showDebug("packageName::"+packageName);
                        SharePrefUtil.saveString(this, "packageName", packageName);
                        // TODO: 2018/3/23
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri apkUri = XFileUtil.file2Uri(file);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        XFileUtil.grantUriPermission(intent, apkUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

                    }
                }catch (Exception e){
                    ToastUtil.showException(e);
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
