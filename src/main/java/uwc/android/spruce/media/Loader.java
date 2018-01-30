package uwc.android.spruce.media;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.OkHttpClient;

/**
 * Created by steven on 17/2/13.
 * 图片加载工具类 —— 基于 Glide
 */
public class Loader {
    private static final String TAG = Loader.class.getSimpleName();

    /**
     * 加载图片生命周期的回调
     */
    public static class Callback {
        protected void onLoadingStarted(String url, View imageView){}

        protected void onLoadingComplete(String url, View imageView, Bitmap bitmap){}

        protected void onLoadingFailed(String url, View imageView){}

        protected void onLoadingCancelled(String url, View imageView){}
    }

    /**
     * 异步加载图片
     * @param url 图片路径
     */
    public static void loadImage(final ImageView imageView, String url) {
        loadImage(imageView, url, 0, null);
    }
    /**
     * 异步加载图片
     * @param url 图片路径
     * @param drawableId 占位图
     */
    public static void loadImage(final ImageView imageView, String url, int drawableId) {
        loadImage(imageView, url, drawableId, null);
    }

    /**
     * 异步加载图片, 带回调
     * @param url 图片路径
     * @param drawableId 占位图
     * @param callback 图片加载完成回调
     */
    public static void loadImage(final ImageView imageView, final String url, int drawableId, final Callback callback) {
        if(imageView == null) {
            Log.e(TAG, "loadImage() -> imageView is null");
            return;
        }

        Context context = imageView.getContext();
        if(!isValid(context))
            return ;

        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(drawableId)
                .error(drawableId)
                .into(new BitmapImageViewTarget(imageView){
                    /**
                     * <a href="https://github.com/bumptech/glide/issues/1764">onLoadFailed run some times</a>
                     */
                    boolean hasFailed = false;

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        if (callback != null) {
                            callback.onLoadingStarted(url, imageView);
                        }
                    }

                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(bitmap, glideAnimation);
                        if (callback != null) {
                            callback.onLoadingComplete(url, imageView, bitmap);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if(hasFailed)
                            return;
                        hasFailed = true;

                        if(callback != null){
                            callback.onLoadingFailed(url, imageView);
                        }
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        super.onLoadCleared(placeholder);
                        if(callback != null){
                            callback.onLoadingCancelled(url, imageView);
                        }
                    }
                });
    }

    /**
     * 异步加载图片, 带回调 (不缓存图片)
     * @param url 图片路径
     * @param drawableId 占位图
     * @param callback 图片加载完成回调
     */
    public static void loadImageWithoutMemoryCache(final ImageView imageView, final String url, int drawableId, final Callback callback) {
        if(imageView == null) {
            Log.e(TAG, "loadImage() -> imageView is null");
            return;
        }

        Context context = imageView.getContext();
        if(!isValid(context))
            return ;

        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(drawableId)
                .error(drawableId)
                .skipMemoryCache(true)
                .into(new BitmapImageViewTarget(imageView){
                    boolean hasFailed = false;

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        if (callback != null) {
                            callback.onLoadingStarted(url, imageView);
                        }
                    }

                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(bitmap, glideAnimation);
                        if (callback != null) {
                            callback.onLoadingComplete(url, imageView, bitmap);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if(hasFailed)
                            return;
                        hasFailed = true;

                        if(callback != null){
                            callback.onLoadingFailed(url, imageView);
                        }
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        super.onLoadCleared(placeholder);
                        if(callback != null){
                            callback.onLoadingCancelled(url, imageView);
                        }
                    }
                });
    }

    /**
     * 异步加载圆角图片, 使用 Glide 内置的圆角裁剪 —— bitmapTransform.
     *
     * 注意: bitmapTransform 与 外在的裁剪不兼容。即: https://github.com/wasabeef/glide-transformations/issues/54
     *
     *      1. 此时的 imageView 不能是自定义的圆角View.
     *      2. scaleType 也不能设。
     *
     * 否则重复裁剪, 会有 bug.
     *
     * @param url 图片路径
     * @param drawableId 占位图
     * @param radiusPixels 圆角像素值
     */
    public static void loadRoundImage(ImageView imageView, String url, int drawableId, int radiusPixels) {
        if(imageView == null) {
            Log.e(TAG, "loadRoundImage() -> imageView is null");
            return;
        }

        Context context = imageView.getContext();
        if(!isValid(context))
            return ;

        Glide.with(context)
                .load(url)
                .placeholder(drawableId)
                .error(drawableId)
                .bitmapTransform(
                        new CenterCrop(context),
                        new RoundedCornersTransformation(context, radiusPixels, 0)
                )
                .crossFade()
                .into(imageView);
    }

    /**
     * 异步加载图片, 带回调(不涉及 ImageView)
     * @param url 图片路径
     * @param callback 图片加载完成回调
     */
    public static void loadImage(Context context, final String url, final Callback callback) {
        if(!isValid(context))
            return ;

        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(){
                    boolean hasFailed = false;

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        if (callback != null) {
                            callback.onLoadingStarted(url, null);
                        }
                    }

                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (callback != null) {
                            callback.onLoadingComplete(url, null, bitmap);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        if(hasFailed)
                            return;
                        hasFailed = true;

                        if(callback != null){
                            callback.onLoadingFailed(url, null);
                        }
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        if(callback != null){
                            callback.onLoadingCancelled(url, null);
                        }
                    }
                });
    }

    /**
     * <a href="https://github.com/bumptech/glide/issues/138">Issue #138: Getting a crash in Glide 3.3 library<a/>
     *
     * reason: {@link RequestManagerRetriever#assertNotDestroyed(Activity)}
     */
    private static boolean isValid(Context context){
        if(context instanceof Activity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity)context).isDestroyed())
                return false;
        }

        GlideConfig.initGlide(context);

        return true;
    }

    /**
     * Created by steven on 17/2/13.
     * Glide 全局配置, 包括缓存策略、Https等。。<br/>
     *
     * <a href="https://github.com/bumptech/glide/wiki/Configuration">文档</a>
     */

    public static class GlideConfig implements GlideModule {
        static int DISK_CACHE_SIZE = 1024 * 1024 * 50;
        //    static int MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory()) / 8;  // 取1/8最大内存作为最大缓存

        @Override
        public void applyOptions(Context context, GlideBuilder builder) {
            builder
                    // 下面三项都是默认的, 不必设置
                    //                .setMemoryCache(new LruResourceCache(MEMORY_CACHE_SIZE))
                    //                .setBitmapPool(new LruBitmapPool(MEMORY_CACHE_SIZE))
                    // 默认 rgb565
                    //                .setDecodeFormat(DecodeFormat.PREFER_RGB_565)
                    .setDiskCache(new InternalCacheDiskCacheFactory(context, DISK_CACHE_SIZE));

        }

        // https 配置
        @Override
        public void registerComponents(Context context, Glide glide) {

            // TODO: 这个回调好像未被执行 ???
            //        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okhttpClient));
        }

        // https 配置
        // TODO: registerComponents() 无效, 暂用这个接口代替
        public static void initGlide(Context context){
            OkHttpClient okhttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true) //设置出现错误进行重新连接。
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                    .sslSocketFactory(Https.getSslSocketFactory())
                    .hostnameVerifier(new Https.UnSafeHostnameVerifier())
                    .build();

            Glide.get(context).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okhttpClient));
        }

        /**
         * Created by steven on 17/2/21.
         *
         * <a href="http://www.tuicool.com/articles/AbMBfee">Glide - Module 实例：接受自签名证书的 HTTPS<a/>
         */

        public static class Https {

            public static SSLSocketFactory getSslSocketFactory(){
                try{
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{new UnSafeTrustManager()}, new SecureRandom());
                    return sslContext.getSocketFactory();

                } catch (NoSuchAlgorithmException e){
                    throw new AssertionError(e);
                } catch (KeyManagementException e){
                    throw new AssertionError(e);
                }
            }

            public static class UnSafeHostnameVerifier implements HostnameVerifier {
                @Override
                public boolean verify(String hostname, SSLSession session){
                    return true;
                }
            }

            private static class UnSafeTrustManager implements X509TrustManager {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)throws CertificateException {}

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)throws CertificateException {}

                @Override
                public X509Certificate[] getAcceptedIssuers(){
                    return new X509Certificate[]{};
                }
            }
        }
    }


}