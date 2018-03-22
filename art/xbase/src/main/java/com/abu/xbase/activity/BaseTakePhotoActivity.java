package com.abu.xbase.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;

import com.abu.xbase.config.XConstant;
import com.abu.xbase.util.ToastUtil;
import com.abu.xbase.util.XFileUtil;
import com.abu.xbase.util.XUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * @author abu
 *         2017/11/22    16:51
 *         bulasuo@foxmail.com
 */

public abstract class BaseTakePhotoActivity extends BasePermissionActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //打开图库返回
            case XConstant.RequestCode.REQUEST_ACTION_PICK:
                if (resultCode == -1) {
                    File fileTemp = XFileUtil.createFileBy_currentTime_providerPath();
                    if (!XFileUtil.copyUri2File(data.getData(), fileTemp)) {
                        ToastUtil.showShort("操作失败!");
                        return;
                    }
                    XFileUtil.uriTemp = XFileUtil.file2Uri(fileTemp);
                    Uri uriResult = XFileUtil.createUriBy_currentTime_providerPath();
                    XFileUtil.resultUriTemp = uriResult;
                    final Intent intent = XUtil.crop(XFileUtil.uriTemp, uriResult);
                    //授予权限
                    XFileUtil.grantUriPermission(intent, uriResult);
                    startActivityForResult(intent, XConstant.RequestCode.REQUEST_CAMERA_CROP);
                }
                break;
            //相机拍照返回
            case XConstant.RequestCode.REQUEST_ACTION_CAMERA:
                if (resultCode == -1) {
                    Uri uriResult = XFileUtil.createUriBy_currentTime_providerPath();
                    XFileUtil.resultUriTemp = uriResult;
                    final Intent intent = XUtil.crop(XFileUtil.uriTemp, uriResult);
                    //授予权限
                    XFileUtil.grantUriPermission(intent, uriResult);
                    startActivityForResult(intent, XConstant.RequestCode.REQUEST_CAMERA_CROP);
                } else if (resultCode == 0)
                    XFileUtil.deleteFile(XFileUtil.uriTemp);
                break;
            //裁剪返回
            case XConstant.RequestCode.REQUEST_CAMERA_CROP:
                if (resultCode == -1) {
                    Uri uri = data.getData() == null ? XFileUtil.resultUriTemp : data.getData();
                    Message msg = obtainMessage(XConstant.EventBus.REQUEST_TO_RESULT);
                    msg.arg1 = XConstant.RequestCode.FRAGMENT_USER_INFO_FOR_PICK;
                    msg.obj = uri;
                    EventBus.getDefault().post(msg);
                } else if (resultCode == 0) {
                    XFileUtil.deleteFile(XFileUtil.uriTemp);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
