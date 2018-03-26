package com.abu.xbase.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;

import com.abu.xbase.app.BaseApp;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author abu
 *         2018/1/4    14:27
 *         bulasuo@foxmail.com
 */

public class XUtil {

    public XUtil() {
        throw new IllegalArgumentException("please use static method!");
    }

    public static SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static String REGEX_ZHazAZ09 = "^[\u4E00-\u9FA5A-Za-z0-9]+$";
    public static String REGEX_8_16_and = "^(?![^a-zA-Z]+$)(?!\\D+$).{8,16}$";
    public static String REGEX_8_16 = "[0-9A-Za-z]{8,16}";
    private static Gson gson;

    private static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static boolean isUIThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static void clearWebViewCookie(Context context) {
        CookieSyncManager.createInstance(context.getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        cookieManager.removeExpiredCookie();
        CookieSyncManager.getInstance().sync();
    }

    /**
     * Returns {@code true} if the arguments are equal to each other
     * and {@code false} otherwise.
     * Consequently, if both arguments are {@code null}, {@code true}
     * is returned and if exactly one argument is {@code null}, {@code
     * false} is returned.  Otherwise, equality is determined by using
     * the {@link Object#equals equals} method of the first
     * argument.
     *
     * @param a an object
     * @param b an object to be compared with {@code a} for equality
     * @return {@code true} if the arguments are equal to each other
     * and {@code false} otherwise
     * @see Object#equals(Object)
     */
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    /**
     * 版本号
     */
    public static int getVersionCode(Context context) {
        PackageInfo pi;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getVersionName(Context context) {
        PackageInfo pi;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean isConnetced = false;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null && cm.getActiveNetworkInfo() != null) {
            //如果仅仅是用来判断网络连接
            //则可以使用
            isConnetced = cm.getActiveNetworkInfo().isAvailable();
        }
        return isConnetced;
    }

    public static void jump2AppDetailSettings(Context context) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package",
                        context.getPackageName(), null));
                intent.putExtra("app_package", context.getPackageName());
                intent.putExtra("app_uid", context.getApplicationInfo().uid);
                context.startActivity(intent);
            } else {
                ToastUtil.showShort("请手动进入应用的权限设置页面!");
            }
        } catch (Exception e) {
            ToastUtil.showShort("请手动进入应用的权限设置页面!");
        }
    }

    public static void jump2AppMarket(Context context) {
        try {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showException(e);
            ToastUtil.showShort("Couldn't launch the market !");
        }
    }

    public static void setClipboard(Context context, String in) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(
                    Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(in);
        } else {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(
                    Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, in));
        }
    }

    /**
     * 显示键盘
     */
    public static void showSoftKeyBoard(final Activity activity, final View view) {
        if (activity == null || view == null)
            return;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (view instanceof EditText) {
                    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                    ((EditText) view).setSelection(((EditText) view).getText().toString().length());
                } else {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                    imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        }, 500);
    }

    /**
     * 强制显示键盘
     */
    public static void showSoftKeyBoard(final View view) {
        if (view == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                view.clearFocus();
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        }, 500);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftKeyBoard(Activity activity, View view) {
        if (activity == null || view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        /** 强制隐藏键盘*/
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideSoftKeyBoard(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (imm != null && view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static <T> T jsonStr2Object(String json, Type typeOfT) {
        return getGson().fromJson(json, typeOfT);
    }

    public static <T> T jsonStr2Object(String json, Class<T> tClass) {
        return getGson().fromJson(json, tClass);
    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(obj2Bytes(src));
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    public static <T> T deepCopy(T src) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(obj2Bytes(src));
        ObjectInputStream in = new ObjectInputStream(byteIn);
        T dest = (T) in.readObject();
        return dest;
    }

    public static byte[] obj2Bytes(Object obj) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(obj);
        return byteOut.toByteArray();
    }

    public static byte[] stream2Bytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }


    /**
     * 回到android home
     *
     * @param context 上下文
     */
    public static void backToHome(Context context) {
        context.startActivity(new Intent(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addCategory(Intent.CATEGORY_HOME));
    }

    /**
     * 获取app渠道名
     *
     * @return
     */
    public static String getAppChannel() {
        try {
            ApplicationInfo appInfo = BaseApp.getInstance().getPackageManager()
                    .getApplicationInfo(BaseApp.getInstance().getPackageName(),
                            PackageManager.GET_META_DATA);
            String c = appInfo.metaData.getString("UMENG_CHANNEL");
            return c;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * MD5值计算
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String encryptMD5ToString(final byte[] data) {
        return bytes2HexString(encryptMD5(data));
    }

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        if (len <= 0) {
            return null;
        }
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    public static byte[] hexString2bytes(String s) {
        String ss = s.replace(" ", "");
        int string_len = ss.length();
        int len = string_len / 2;
        if (string_len % 2 == 1) {
            ss = "0" + ss;
            string_len++;
            len++;
        }
        byte[] a = new byte[len];
        try {
            for (int i = 0; i < len; i++) {
                a[i] = (byte) Integer.parseInt(ss.substring(2 * i, 2 * i + 2), 16);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return a;
    }

    /**
     * MD5值计算
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] encryptMD5(final byte[] data) {
        return hashTemplate(data, "MD5");
    }

    /**
     * hash加密模板
     *
     * @param data      数据
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private static byte[] hashTemplate(final byte[] data, final String algorithm) {
        if (data == null || data.length <= 0) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 照片剪切
     *
     * @param fromUri, resultUri
     */
    public static Intent crop(Uri fromUri, Uri resultUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 授予目录临时共享权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(fromUri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, resultUri);
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 600);
        intent.putExtra("aspectY", 600);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("circleCrop", true);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return intent;
    }

    public static SpannableString highLightString(String in, String key) {
        if (TextUtils.isEmpty(in))
            return new SpannableString("");
        SpannableString s = new SpannableString(in);
        if (!TextUtils.isEmpty(key)) {
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(0xFFFF0000), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    public static SpannableString highLightString(String in, String key, int color) {
        if (TextUtils.isEmpty(in))
            return new SpannableString("");
        SpannableString s = new SpannableString(in);
        if (!TextUtils.isEmpty(key)) {
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    public static SpannableString translateString(String in, String key) {
        if (TextUtils.isEmpty(in))
            return new SpannableString("");
        SpannableString s = new SpannableString(in);
        if (!TextUtils.isEmpty(key)) {
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(0x00000000), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    public static SpannableStringSerializable translateStringSerializable(String in, String key) {
        if (TextUtils.isEmpty(in))
            return new SpannableStringSerializable("");
        SpannableStringSerializable s = new SpannableStringSerializable(in);
        if (!TextUtils.isEmpty(key)) {
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(0x00000000), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    /**
     * @author abu   2017/4/4   17:57
     * 中文 或ascii 控制字符 转 国际码
     */
    public static String fromUnicodeU(String ua) {
        if (ua == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, length = ua.length(); i < length; i++) {
            char c = ua.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                stringBuilder.append(String.format(Locale.getDefault(),
                        "\\u%04x", (int) c));
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获取apk文件的包名
     *
     * @param context
     * @param apkFile
     * @return
     */
    public static String getApkPackageName(Context context, File apkFile) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(
                    apkFile.getCanonicalPath(),
                    PackageManager.GET_ACTIVITIES);
            return info.applicationInfo.packageName;
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
        return null;
    }

    /**
     * 启动目标包名的应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean launchApkByPackage(Context context, String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (!(context instanceof Activity))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
        return false;
    }

    /**
     * 手动卸载context所属的应用,无需权限
     *
     * @param context
     * @return
     */
    public static boolean uninstallApk(Context context) {
        try {
            Uri uri = Uri.fromParts("package",
                    context.getApplicationContext().getPackageName(), null);
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            if (!(context instanceof Activity))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
        return false;
    }

    /**
     * 手动安装应用apkFile, 无需权限
     *
     * @param context
     * @param apkFile
     * @return
     */
    public static boolean installApk(Context context, File apkFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (!(context instanceof Activity))
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkUri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                apkUri = Uri.fromFile(apkFile);
            } else {
                apkUri = XFileUtil.file2Uri(apkFile);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            XFileUtil.grantUriPermission(intent, apkUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
        return false;
    }

}
