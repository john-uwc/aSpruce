package uwc.android.spruce.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileMan {

    public enum Type {
        png, jpg;

        public String touch() {
            String fileName = new SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            switch (this) {
                case png:
                    fileName += ".png";
                    break;
                case jpg:
                    fileName += ".jpg";
                    break;
                default:
                    break;
            }
            return fileName;
        }
    }

    /**
     * 图片保存到本地, 并更新本地数据库。
     * @param context
     * @param bitmap
     * @param type
     * @param dir
     * @throws IOException 2015-10-19
     */
    public static void doSave(Context context, Bitmap bitmap, Type type, String dir) throws IOException {
        File target = new File(dir, type.touch());
        if (!target.getParentFile()
                .exists()) {
            target.getParentFile().mkdirs();
        }
        if (!target.exists()) {
            target.createNewFile();
        }
        BufferedOutputStream oStream = new BufferedOutputStream(new FileOutputStream(target));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
        oStream.flush();
        oStream.close();
        // 发送广播，通知更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(target);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 删除指定文件或文件夹下的所有有文件
     *
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                return delAllFile(path
                        + (path.endsWith(File.separator) ? "" : File.separator) + f.getName());// 删除文件夹里面的文件
            }
        }
        return file.delete();
    }

    /**
     * 计算指定文件或文件夹的大小
     * @param path
     * @return
     */
    public static long calcSize(File path) { //取得文件夹大�?
        long size = 0;
        File flist[] = path.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + calcSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }


    public static File touch(Context context, Type type) {
        File dir = context.getCacheDir();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = context.getExternalCacheDir();
        }

        return new File(dir, type.touch());
    }
}