package com.abu.xbase.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;

import com.abu.xbase.app.BaseApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author   abu
 * 2017/11/7    18:57
 * bulasuo@foxmail.com
 */
public class XFileUtil {
    private final static String IMG = "img";
    private final static String APK = "apk";
    private final static String BROWSER = "browser";
    private final static String CRASH = "cr";
    private static String imgDir;
    private static String cachImgDir;
    private static String cachBrowserDir;
    private static String cachCrashir;
    private static String fileDir;

    private static Context appContext;

    public static Context getAppContext() {
        if (appContext == null){
            appContext = BaseApp.getInstance();
        }
        return appContext;
    }

    /**
     * 本地temp文件指针
     */
    public static Uri uriTemp;
    public static Uri resultUriTemp;
    public final static String SCHEME_TAG = BaseApp._FILE_PROVIDER_SCHEME_TAG;
    private final static String providerPath = getAppContext().getExternalCacheDir() + "/temp/";
    private static FileProvider fileProvider;

    public static FileProvider getFileProvider() {
        if (fileProvider == null){
            fileProvider = new FileProvider();
        }
        return fileProvider;
    }

    public static String getSaveImgDir() {
        if (imgDir == null) {
            final File f = getAppContext().getExternalFilesDir(IMG);
            if (!f.exists()) {
                f.mkdirs();
            }
            imgDir = f.getAbsolutePath();
        }
        return imgDir;
    }

    public static String getApkFileDir() {
        if (fileDir == null) {
            final File f = new File(getAppContext().getFilesDir(), APK);
            if (!f.exists()) {
                f.mkdirs();
            }
            fileDir = f.getAbsolutePath();
        }
        return fileDir;
    }

    public static String getImageCachDir() {
        if (cachImgDir == null) {
            final File f = new File(getAppContext().getExternalCacheDir(), IMG);
            if (!f.exists()) {
                f.mkdirs();
            }
            cachImgDir = f.getAbsolutePath();
        }
        return cachImgDir;
    }

    public static String getBarowserCachDir() {
        if (cachBrowserDir == null) {
            final File f = new File(getAppContext().getExternalCacheDir(), BROWSER);
            if (!f.exists()) {
                f.mkdirs();
            }
            cachBrowserDir = f.getAbsolutePath();
        }
        return cachBrowserDir;
    }

    public static String getCrashCachDir() {
        if (cachCrashir == null) {
            final File f = new File(getAppContext().getExternalCacheDir(), CRASH);
            if (!f.exists()) {
                f.mkdirs();
            }
            cachCrashir = f.getAbsolutePath();
        }
        return cachCrashir;
    }

    public static File createFileBy_currentTime_providerPath() {
        File file = new File(providerPath + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    public static Uri createUriBy_currentTime_providerPath() {
        return file2Uri(createFileBy_currentTime_providerPath());
    }

    /**
     * 基于现在时间搓 新建uriTemp1
     *
     * @return true if uriTemp 创建成功
     */
    public static boolean createUriTemp() {
        uriTemp = null;
        File file = createFileBy_currentTime_providerPath();
        uriTemp = FileProvider.getUriForFile(getAppContext(), SCHEME_TAG, file);
        return uriTemp != null;
    }

    /**
     * 共享的(跨应用的)uri转成文件
     *
     * @param uri
     * @param file 必需为权限内路径的文件
     * @return
     */
    public static boolean copyUri2File(Uri uri, File file) {
        boolean success = false;
        if (uri == null || file == null){
            throw new IllegalArgumentException(" -- ");
        }
        ParcelFileDescriptor pfd = null;
        try {
            pfd = getAppContext().getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            ToastUtil.showException(e);
        }
        if (pfd == null) {
            return false;
        }
        InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(pfd);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            success = true;
        } catch (IOException e) {
            ToastUtil.showException(e);
        } finally {
            try {
                is.close();
                if (os != null){
                    os.close();
                }
            } catch (IOException e) {
                ToastUtil.showException(e);
            }
        }
        return success;
    }

    /**
     * @param file 必需为权限内路径的文件
     * @return
     */
    public static Uri file2Uri(File file) {
        Uri uri = null;
        if (file != null) {
            try {
                uri = FileProvider.getUriForFile(getAppContext(), SCHEME_TAG, file);
            } catch (Exception e) {
                ToastUtil.showException(e);
            }
        }
        return uri;
    }

    /**
     * 授予该intent 对 该uri的读写权限
     *
     * @param intent
     * @param uri
     */
    public static void grantUriPermission(Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = getAppContext().getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getAppContext().grantUriPermission(packageName, uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    /**
     * @param uri 必需为权限内路径的uri 或者7.0以前的uri 比如file://开头的 但不推荐用这种uri了
     * @return
     */
    public static File uri2File(Uri uri) {
        if (uri == null) {
            return null;
        }
        File file = null;
        try {
            final Object obj = XProxyUtil.invoke(getFileProvider(), "getPathStrategy",
                    new Class[]{Context.class, String.class}, new Object[]{getAppContext(), SCHEME_TAG});
            if (obj != null){
                file = (File) XProxyUtil.invoke(obj, "getFileForUri", new Class[]{Uri.class}, new Object[]{uri});
            }
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
        return file;
    }


    /**
     * android7.0 以后uri转file绝对路径
     *
     * @author abu   2017/4/7   12:45
     */
    public static String uri2Path(Uri uri) {
        if (uri == null) {
            return null;
        }
        String uPath = uri.getPath();
        if (uPath == null) {
            return null;
        }
        final File f = uri2File(uri);
        try {
            if (f != null){
                uPath = f.getCanonicalPath();
            }
        } catch (IOException e) {
            ToastUtil.showException(e);
        }
        return uPath;
    }

    public static void deleteFile(Uri uri) {
        String p = uri2Path(uri);
        if (p == null || p.trim().length() == 0){ return;}
        final File file = new File(p);
        if (file.exists()){file.delete();}
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                deleteFileWithDelay(file);
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            deleteFileWithDelay(file);
        } else {
            ToastUtil.showDebug("文件不存在");
        }
    }

    public static void deleteFileWithDelay (File file){
        if (null!=file&&file.exists()) {
            file.delete();
            SystemClock.sleep(50);
        }
    }


}