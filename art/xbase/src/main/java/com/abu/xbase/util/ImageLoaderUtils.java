package com.abu.xbase.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.abu.xbase.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * @author abu
 *         2018/2/5    9:09
 *         bulasuo@foxmail.com
 */

public class ImageLoaderUtils {

    final static int IMAGE_FADE_TIME = 100;
    public static final int MAXWIDTH_OR_HEIGHT = 1000;//加载的图片的最大宽度或高度
    public static final int MINWIDTH_OR_HEIGHT = 600;//加载的图片的较小宽度或高度
    public static final int SMALLWIDTH_OR_HEIGHT = 300;//加载的图片的较小宽度或高度

    public static void display(Context context, ImageView imageView, String url, int placeholder, int error) {
        try {
            Glide.with(context)
                    .load(url)
                    .placeholder(placeholder)
                    .error(error).crossFade().into(imageView);
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

    public static void displayHeadImg(Context context, ImageView imageView, String url) {
        try {
//            url = API.IMG + url;
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade().into(imageView);
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

    /**
     * 圆形头像
     *
     * @param context
     * @param imageView
     * @param url
     */
    public static void displayHeadCircleImg(Context context, final ImageView imageView, String url) {
        try {
//            url = API.IMG + url;
            Glide.with(context)
                    .load(url)
                    .error(R.drawable.ic_head_default_xbase)
                    .placeholder(R.drawable.ic_head_default_xbase)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(final GlideDrawable resource, GlideAnimation<? super
                                GlideDrawable> glideAnimation) {
                            if (imageView == null) {
                                return;
                            }
                            imageView.post(() -> imageView.setImageDrawable(resource));
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            if (imageView == null) {
                                return;
                            }
                            imageView.setImageDrawable(errorDrawable);
                        }
                    });
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

    /**
     * 圆形图
     *
     * @param context
     * @param imageView
     * @param url
     */
    public static void displayCircleImg(Context context, final ImageView imageView, String url) {
        try {
            Glide.with(context)
                    .load(url)
//                    .error(R.drawable.ic_head_default)
//                    .placeholder(R.drawable.ic_head_default)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(final GlideDrawable resource, GlideAnimation<? super
                                GlideDrawable> glideAnimation) {
                            if (imageView == null) {
                                return;
                            }
                            imageView.post(() -> imageView.setImageDrawable(resource));
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            if (imageView == null) {
                                return;
                            }
                            imageView.setImageDrawable(errorDrawable);
                        }
                    });
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

    public static void displayCircleImg(Context context, final ImageView imageView, int sourceId) {
        try {
            Glide.with(context)
                    .load(sourceId)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(final GlideDrawable resource, GlideAnimation<? super
                                GlideDrawable> glideAnimation) {
                            if (imageView == null) {
                                return;
                            }
                            imageView.post(() -> imageView.setImageDrawable(resource));
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            if (imageView == null) {
                                return;
                            }
                            imageView.setImageDrawable(errorDrawable);
                        }
                    });
        } catch (Exception e) {
            ToastUtil.showException(e);
        }
    }

}
