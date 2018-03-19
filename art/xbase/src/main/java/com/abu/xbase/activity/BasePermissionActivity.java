package com.abu.xbase.activity;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * @author abu
 *         2017/11/22    19:17
 *         bulasuo@foxmail.com
 */

public class BasePermissionActivity extends BaseActivity {
    private static final int REQUEST_CODE_PERMISSION = 9999;
    private PermissionListener mListener;

    public interface PermissionListener {
        void onGranted();

        void onDenied();
    }

    public void checkPermission(@NonNull String[] permissions, @NonNull PermissionListener listener) {
        boolean granted = true;
        //权限拒绝后是否解释
        boolean rationale = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                    rationale = true;
                }

            }
        }

        if (!granted) {
            mListener = listener;
            if (rationale) {
                listener.onDenied();
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION);
            }
        } else {
            listener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mListener != null) {
                    mListener.onGranted();
                    mListener = null;
                }
            } else {
                if (mListener != null) {
                    mListener.onDenied();
                    mListener = null;
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
