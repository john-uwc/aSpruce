package uwc.android.spruce.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片压缩工具类
 */
public class Compressor {

    private static final String TAG = "Compressor";

    private static final int DEFAULT_IMAGE_MAX_HEIGHT = 800;
    private static final int DEFAULT_IMAGE_MAX_WIDTH = 480;

    private static Quality defauQuality = Quality.QUALITY_80;

    public enum Quality {
        QUALITY_ORIGINAL, QUALITY_90, QUALITY_80, QUALITY_70, QUALITY_60,QUALITY_50, QUALITY_30
    };

    /**
     * 通过压缩图片的尺寸来压缩图片大小，通过读入流的方式，可以有效防止网络图片数据流形成位图对象时内存过大的问题；
     *
     * @param \InputStream 要压缩图片，以流的形式传入
     * @param targetWidth  缩放的目标宽度
     * @param targetHeight 缩放的目标高度
     * @return 缩放后的图片
     */
    public static Bitmap compressBySize(String filepath, int targetWidth, int targetHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, opts);

        // 得到图片的宽度、高度；
        int height = opts.outHeight, width = opts.outWidth;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
        int hRatio = (int) Math.ceil(height / (0 < targetHeight ? targetHeight : height));
        int wRatio = (int) Math.ceil(width / (0 < targetWidth ? targetWidth : width));
        int ratio = !(wRatio > 1 || hRatio > 1)? 1 : (wRatio > hRatio ?  wRatio : hRatio);
        opts.inSampleSize = Math.min(8, (int)Math.pow(2, (int)(Math.log(ratio)/Math.log(2))));
        Log.d(TAG, "compressBySize -> ratio:" + opts.inSampleSize);

        // 设置好缩放比例后，加载图片进内存；
        opts.inJustDecodeBounds = false;
        return BitmapHandler.rotateByDegree(BitmapFactory.decodeFile(filepath, opts), BitmapHandler.qryRotateDegree(filepath));
    }

    /**
     * 压缩图片，自动计算要压缩的宽和高，默认压缩质量为原图的60%
     *
     * @param fromPath 原始图片路径
     * @param toPath   压缩后的图片的保存路径
     * @return
     */
    public static File compressImage(String fromPath, String toPath) {
        try {
            FileInputStream is = new FileInputStream(fromPath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            byte[] data = baos.toByteArray();

            int degree = BitmapHandler.qryRotateDegree(fromPath);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
            Bitmap bitmap = BitmapFactory.decodeFile(fromPath, opts);

            // Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
            // data.length,
            // opts);

            // int bitmapWidth = bitmap.getWidth();
            // int bitmapHeight = bitmap.getHeight();

            int bitmapWidth = opts.outWidth;
            int bitmapHeight = opts.outHeight;

            int[] params = Compressor.calcSize(bitmapWidth, bitmapHeight);
            int width = params[0];
            int height = params[1];
            opts.inJustDecodeBounds = false;

            opts.inDither = false;
            opts.inPurgeable = true;
            opts.inTempStorage = new byte[12 * 1024];

            // bitmap = BitmapFactory.decodeFile(fromPath,opts);

            // File file2 = new File(fromPath);
            // bitmap = BitmapFactory.decodeStream(fis, null_, opts);
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

            File file = scaleImage(toPath, width, height, defauQuality, bitmap, bitmapWidth, bitmapHeight, degree);

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 压缩图片，自动计算要压缩的宽和高
     *
     * @param fromPath 原始图片路径
     * @param toPath   压缩后的图片路径
     * @param quality  压缩质量
     * @return
     */
    public static File compressImage(String fromPath, String toPath, Quality quality) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fromPath);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int[] params = Compressor.calcSize(bitmapWidth, bitmapHeight);
            int width = params[0];
            int height = params[1];
            int degree = BitmapHandler.qryRotateDegree(fromPath);
            File file = scaleImage(toPath, width, height, quality, bitmap, bitmapWidth, bitmapHeight, degree);

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按照指定的分辨率和质量压缩图片到指定目录
     *
     * @param fromPath
     * @param toPath
     * @param width
     * @param height
     * @param quality
     */
    public static File compressImage(String fromPath, String toPath, int width, int height, Quality quality) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fromPath);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int degree = BitmapHandler.qryRotateDegree(fromPath);
            File file = scaleImage(toPath, width, height, quality, bitmap, bitmapWidth, bitmapHeight, degree);

            return file;
        } catch (Exception e) {
            e.printStackTrace();
//            MLog.v(TAG, "compressImage : " + e);
            return null;
        }
    }

    private static File scaleImage(String toPath, int width, int height, Quality quality, Bitmap bitmap, int bitmapWidth, int bitmapHeight, int degree) throws FileNotFoundException, IOException, NullPointerException {
        // 缩放图片的尺寸
        float scaleWidth = (float) width / bitmapWidth;
        float scaleHeight = (float) height / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 产生缩放后的Bitmap对象
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);

        if (degree > 0) {
            resizeBitmap = BitmapHandler.rotateByDegree(resizeBitmap, degree);
        }

        // 保存图片到指定的目录
        File myCaptureFile = new File(toPath);
        FileOutputStream out = new FileOutputStream(myCaptureFile);
        int qualityValue = fetchImageQuality(quality);

        if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, qualityValue, out)) {
            out.flush();
            out.close();
        }
        if (!bitmap.isRecycled()) {
            bitmap.recycle();// 释放资源，否则容易内存溢出 fuck
        }
        if (!resizeBitmap.isRecycled()) {
            resizeBitmap.recycle();
        }

        return myCaptureFile;
    }

    private static int fetchImageQuality(Quality quality) {
        int qualityValue = 100;
        switch (quality) {
            case QUALITY_ORIGINAL:
                qualityValue = 100;
                break;
            case QUALITY_90:
                qualityValue = 90;
                break;
            case QUALITY_80:
                qualityValue = 80;
                break;
            case QUALITY_70:
                qualityValue = 80;
                break;
            case QUALITY_60:
                qualityValue = 80;
                break;
            default:
                break;
        }
        return qualityValue;
    }

    /**
     * 计算出合适的图片分辨率
     *
     * @param width
     * @param height
     * @return
     */
    private static int[] calcSize(int width, int height) {
        int[] params = new int[2];
        // 得到图片比例
        double percent = (((double) width) / height);
        int mWidth = 0;
        int mHeight = 0;
        if (width > height) {
//            MLog.v(TAG, "宽图");
            int ratio = width / height;
            if (ratio > 1.5) {
                // 超级大宽图
                if (height >= DEFAULT_IMAGE_MAX_HEIGHT) {
                    // 计算应该缩放的宽
                    mWidth = (int) (DEFAULT_IMAGE_MAX_HEIGHT * percent);
                    mHeight = DEFAULT_IMAGE_MAX_HEIGHT;
                } else {
                    mWidth = width;
                    mHeight = height;
                }
            } else {
                // 小宽图
                if (width >= DEFAULT_IMAGE_MAX_WIDTH) {
                    mWidth = DEFAULT_IMAGE_MAX_WIDTH;
                    mHeight = (int) (DEFAULT_IMAGE_MAX_WIDTH / percent);
                } else {
                    mWidth = width;
                    mHeight = height;
                }
            }
        } else if (height > width) {
//            MLog.v(TAG, "长图");
            if (width >= DEFAULT_IMAGE_MAX_WIDTH) {
                mWidth = DEFAULT_IMAGE_MAX_WIDTH;
                // 计算应该缩放的高
                mHeight = (int) (DEFAULT_IMAGE_MAX_WIDTH / percent);
                params[0] = mWidth;
                params[1] = mHeight;
            } else {
                mWidth = width;
                mHeight = height;
            }
        } else {// 宽高一致

//            MLog.v(TAG, "正方形图");

            if (width >= DEFAULT_IMAGE_MAX_WIDTH) {
                mWidth = DEFAULT_IMAGE_MAX_WIDTH;
                mHeight = DEFAULT_IMAGE_MAX_WIDTH;
            } else {
                mWidth = width;
                mHeight = height;
            }
        }
        params[0] = mWidth;
        params[1] = mHeight;
        return params;
    }
}
