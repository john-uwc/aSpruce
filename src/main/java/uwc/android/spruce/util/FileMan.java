package uwc.android.spruce.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileMan {
    public enum Type {
        png, jpg;

        @Override
        public String toString() {
            String f = ".t";
            switch (this) {
                case png:
                    f = ".png";
                    break;
                case jpg:
                    f = ".jpg";
                    break;
                default:
                    break;
            }
            return f;
        }
    }

    public static File queryPublicDir(FileMan.Type type) {
        File dir = Environment.getExternalStorageDirectory();
        switch (type) {
            case png:
            case jpg:
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                break;
            default:
                break;
        }
        return dir;
    }

    public static File create(Context context, Type type) {
        File dir = context.getCacheDir();
        if (Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState())) {
            dir = queryPublicDir(type);
        }
        String file = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        return new File(dir, /*"multi_image_"+*/file + type.toString());
    }


    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内�?
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除指定文件夹下�?有文�?
    // param path 文件夹完整绝对路�?
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文�?
                delFolder(path + "/" + tempList[i]);// 再删除空文件�?
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 格式化单位
     * @param size
     * @return
     */
    public static String formatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0KB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static long calcFileSize(File f) throws Exception {//取得文件大小
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
            System.out.println("文件不存�?");
        }
        return s;
    }

    // 递归
    public static long calcDirSize(File dir) { //取得文件夹大�?
        long size = 0;
        File flist[] = dir.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + calcDirSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    public static long calcFiles(File dir) {//递归求取目录文件个数
        long size = 0;
        File flist[] = dir.listFiles();
        size = flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + calcFiles(flist[i]);
                size--;
            }
        }
        return size;
    }

    public static String fetchFileName(String file) {
        if (file == null) {
            return "未知";
        }
        int start = file.lastIndexOf("/") + 1;
        if (start == 0) {
            return file;
        }
        return file.substring(start, file.length());
    }

    

    public static String getFmtFileSize(File file) throws Exception {
        return formatSize(calcFileSize(file));
    }
}
