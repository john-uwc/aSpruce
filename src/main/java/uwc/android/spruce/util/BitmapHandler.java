package uwc.android.spruce.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Base64;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BitmapHandler {

    /**
     * 将彩色图转换为黑白图
     *
     * @return 返回转换好的位图
     */
    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }

    public static Bitmap getThumbnail(Context context, long origId) {
        return Thumbnails.getThumbnail(context.getContentResolver(), origId, Thumbnails.MICRO_KIND, null);
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    /**
     * 保存图片到本地,这个是把图片压缩成字节流然后保存到本地，所以本地的图片是无法显示的
     *
     * @param mBitmap
     * @param imageURL
     * @param cxt
     */
    public static void saveBitmap(Bitmap mBitmap, String imageURL, Context cxt) {

        String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1); // 传入一个远程图片的url，然后取最后的图片名字

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = cxt.openFileOutput(bitmapName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取本地私有文件夹的图片
     *
     * @param cxt
     * @return
     */
    public static Bitmap getBitmap(String fileName, Context cxt) {
        String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = cxt.openFileInput(bitmapName);
            ois = new ObjectInputStream(fis);
            byte[] byteArray = (byte[]) ois.readObject();
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是读取文件产生异常
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        // 读取产生异常，返回null
        return null;
    }

    public static File getBitmapFile(String path, String fileName, Bitmap bitmap) {
        File files = new File(path);
        if (!files.exists()) {
            files.mkdirs();
        }
        File file = new File(path, fileName);

        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 图片压缩
     *
     * @author caoguang
     * @tags @param bmp
     * @tags @param limitKB
     * @tags @param needRecycle
     * @tags @return
     * @since 2015-4-12 下午3:42:48
     */
    public static byte[] compressBmpToByteArray(Bitmap bmp, int limitKB, boolean needRecycle) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(CompressFormat.PNG, options, baos);
        while (baos.toByteArray().length / 1024 > limitKB && options > 10) {
            baos.reset();
            options -= 10;
            bmp.compress(CompressFormat.JPEG, options, baos);
        }
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] compressJpgToByteArray(Bitmap bmp, int limitKb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > limitKb && options > 10) {
            baos.reset();
            options -= 10;
            bmp.compress(CompressFormat.JPEG, options, baos);
        }
        byte[] result = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] wxCompressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // if (baos.toByteArray().length / 1024 > 1024) {//
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        // baos.reset();// 重置baos即清空baos
        // image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//
        // 这里压缩50%，把压缩后的数据存放到baos中
        // }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 100f;// 这里设置高度为800f
        float ww = 100f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        } else {
            be = 3;
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    private static byte[] compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        // ByteArrayInputStream isBm = new
        // ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        // Bitmap bitmap = BitmapFactory.decodeStream(isBm, null_,
        // null_);//把ByteArrayInputStream数据生成图片
        return baos.toByteArray();
    }

    public static byte[] compressBmpToByteArray(Bitmap bmp, int limitKB) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(CompressFormat.PNG, options, baos);
        while (baos.toByteArray().length / 1024 > limitKB && options > 10) {
            baos.reset();
            options -= 10;
            bmp.compress(CompressFormat.JPEG, options, baos);
        }
        byte[] result = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 压缩图片
     *
     * @author caoguang
     * @tags @param filePath
     * @tags @param limitKB
     * @tags @return
     * @since 2015-4-14 下午1:10:56
     */
    public static Bitmap compressJpgFile(String filePath, int limitKB) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        if (bmp != null) {
            bmp.compress(CompressFormat.JPEG, options, baos);
            while (baos.toByteArray().length / 1024 > limitKB && options > 10) {
                baos.reset();
                options -= 10;
                bmp.compress(CompressFormat.JPEG, options, baos);
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            return bitmap;
        }
        return null;
    }

    /**
     * 压缩jpeg图片
     */
    public static Bitmap compressJpgBmp(Bitmap bmp, int limitKB) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > limitKB) {
            baos.reset();
            if (options > 10) {
                options -= 10;
            } else {
                options -= 1;
            }
            bmp.compress(CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void saveBitmap(String dirPath, String fileName, Bitmap bitmap) {
        File file = new File(dirPath, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片保存到本地, 并更新本地数据库。
     *
     * @param bm
     * @throws IOException 2015-10-19
     * @author 李天富
     */
    public static void saveImage(Context context, String dirpath, String filename, Bitmap bm) throws IOException {

        String filePath = dirpath + File.separator + "image" + filename;
        File myCaptureFile = new File(filePath);
        if (!myCaptureFile.getParentFile().exists()) {
            myCaptureFile.getParentFile().mkdirs();
        }
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();

        // 发送广播，通知更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(myCaptureFile);
        intent.setData(uri);
        context.sendBroadcast(intent);

    }

    /**
     * 获取imageview中的图片资源并保存
     *
     * @param context
     * @param dirpath  保存路径
     * @param filename 保存文件名
     * @param view     imageview 2015-10-20
     * @author
     */
    public static boolean saveImageFromImageView(Context context, String dirpath, String filename, ImageView view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bm = view.getDrawingCache();
        boolean isSuccess = true;
        try {
            BitmapHandler.saveImage(context, dirpath, filename, bm);
        } catch (IOException e) {
            e.printStackTrace();
            isSuccess = false;
        }

        return isSuccess;
    }

    @Deprecated
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap getSmallBitmap(byte[] data) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap resizeBitmap(Bitmap bitmapOrg, int newWidth, int newHeight) {
        if (bitmapOrg == null)
            return bitmapOrg;
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = null;

        try {
            resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resizedBitmap;
    }

    @SuppressLint("NewApi")
    public static String bitmapToString(String filePath) {
        Bitmap bm = getSmallBitmap(filePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static InputStream bitmap2InputStream(Bitmap bm) throws Exception {
        return byte2InputStream(bmpToByteArray(bm));
    }

    public static InputStream byte2InputStream(byte[] in) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片
     * @return 图片的旋转角度
     */
    public static int qryRotateDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param source
     *            需要旋转的图片
     * @param degree
     *            旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateByDegree(Bitmap source, int degree) {
        Bitmap rotated = source;
        if (0 != degree) {
            try {
                // 根据旋转角度，生成旋转矩阵
                Matrix matrix = new Matrix();matrix.postRotate(degree);
                // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
                rotated = Bitmap.createBitmap(source
                        , 0, 0, source.getWidth(), source.getHeight()
                        , matrix
                        , true);
                source.recycle();
            } catch (OutOfMemoryError e) {
            }
        }
        return rotated;
    }
}
