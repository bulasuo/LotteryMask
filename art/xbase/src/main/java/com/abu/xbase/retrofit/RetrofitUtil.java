package com.abu.xbase.retrofit;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.abu.xbase.config.API;
import com.abu.xbase.util.XUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author abu
 *         2018/1/4    19:03
 *         bulasuo@foxmail.com
 */

public class RetrofitUtil {
    private static boolean DEBUG;
    private static final long CONNECTION_TIMEOUT = 10;
    private static final long WRITE_TIMEOUT = 15;
    private static final long READ_TIMEOUT = 15;
    private static Retrofit defaultRetrofit, gsonRetrofit;
    private static Application mAppContext;
    private static final String HOST = API.HOST;
    private static final HashMap<Retrofit, HashMap<String, Object>> serviceMap = new HashMap<>();

    public RetrofitUtil() {
        throw new IllegalArgumentException("please use static method!");
    }

    @FunctionalInterface
    public interface Function {
        /**
         * 获取token
         *
         * @return token
         */
        String apply();
    }

    private static Function reLogin;

    public static void init(boolean debug, Application appContext, Function reLogin1) {
        DEBUG = debug;
        mAppContext = appContext;
        reLogin = reLogin1;
    }

    public static <T> T getService(Retrofit retrofit, final Class<T> serviceClass) {
        HashMap<String, Object> map = serviceMap.get(retrofit);
        T service;
        if (map != null) {
            service = (T) map.get(serviceClass.getName());
            if (service != null)
                return service;
            service = retrofit.create(serviceClass);
            map.put(serviceClass.getName(), service);
            return service;
        } else {
            service = retrofit.create(serviceClass);
            map = new HashMap<>();
            map.put(serviceClass.getName(), service);
            serviceMap.put(retrofit, map);
            return service;
        }
    }

    /**
     * 请使用 {@link #getGsonRetrofit(String)}
     */
    @Deprecated
    public static Retrofit getDefaultRetrofit(String host) {
        if (defaultRetrofit == null
                || !XUtil.equals(gsonRetrofit.baseUrl(), HttpUrl.parse(host))) {
            defaultRetrofit = new Retrofit.Builder()
                    .baseUrl(host)
                    .client(getDefaultClient())
                    .build();
        }
        return defaultRetrofit;
    }

    /**
     * 请使用 {@link #getGsonRetrofit()}
     */
    @Deprecated
    public static Retrofit getDefaultRetrofit() {
        return getDefaultRetrofit(HOST);
    }

    public static Retrofit getGsonRetrofit(String host) {
        if (gsonRetrofit == null
                || !XUtil.equals(gsonRetrofit.baseUrl(), HttpUrl.parse(host))) {
            Gson gson = new GsonBuilder()
                    //配置你的Gson 可自定义Gson
                    .setDateFormat("yyyy-MM-dd hh:mm:ss")
                    .create();
            gsonRetrofit = new Retrofit.Builder()
                    .baseUrl(host)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getDefaultClient())
                    .build();
        }
        return gsonRetrofit;
    }

    public static Retrofit getGsonRetrofit() {
        return getGsonRetrofit(HOST);
    }

    private static OkHttpClient getDefaultClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);
        CookieHandler cookieHandler = new CookieManager(new PersistentCookieStore(mAppContext),
                CookiePolicy.ACCEPT_ALL);
        return new OkHttpClient.Builder()
                .addInterceptor(new CustomInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .sslSocketFactory(createSSLSocketFactory(), new TrustAllManager())
                .cookieJar(new JavaNetCookieJar(cookieHandler))
                .build();
    }

    public static class CustomInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            String agentStr = getUserAgent();
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("User-Agent", agentStr)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip,deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.9;en;q=0.8")
                    .method(original.method(), original.body())
                    .build();
            Response response = chain.proceed(request);
            return response;
        }
    }

    /**
     * 获取User-Agent
     */
    private static String mUserAgent;

    public static String getUserAgent() {
        if (mUserAgent == null)
            mUserAgent = "device:" + fromUnicodeU(String.valueOf(Build.MODEL)) +
                    ";os:android" + Build.VERSION.SDK +
                    ";" + getVersionCodeAndName(mAppContext);
        return mUserAgent;
    }

    private static String getVersionCodeAndName(Context context) {
        PackageInfo pi;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return "versionCode:" + pi.versionCode + ";versionName:" + pi.versionName + ";";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "versionCode:null;versionName:null";
    }

    private static String fromUnicodeU(String ua) {
        return XUtil.fromUnicodeU(ua);
    }

    private static final String MEDIA_TYPE_FORM_DATA = "multipart/form-data";
    private static final String MEDIA_TYPE_STREAM = "application/octet-stream";

    @NonNull
    public static MultipartBody.Part createPart(String key, String value) {
        return MultipartBody.Part.createFormData(key, value);
    }

    @NonNull
    public static MultipartBody.Part createPart(String key, File file) {
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(MEDIA_TYPE_STREAM), file);
        return MultipartBody.Part.createFormData(key, file.getName(), requestBody);
    }

    /**
     * 默认信任所有的证书
     */
    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(new KeyManager[]{}, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            /*//示例
            if("yourhostname".equals(hostname)){
                return true;
            } else {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify(hostname, session);
            }*/
            return true;
        }
    }
}
