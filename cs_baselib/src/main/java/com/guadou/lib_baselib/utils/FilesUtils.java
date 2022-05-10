package com.guadou.lib_baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 压缩图片到本地
 */
public class FilesUtils {

    private static FilesUtils mInstance = null;
    private Context mContext;
    public static String SDPATH = "";

    private FilesUtils() {
        mContext = CommUtils.getContext();
        SDPATH = hasSdcard() ? mContext.getExternalCacheDir() + "/pos/" : mContext.getCacheDir() + "/pos/";

        //如果不存在就创建一个文件夹
        File dir = new File(SDPATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static FilesUtils getInstance() {
        if (mInstance == null)
            return new FilesUtils();
        else
            return mInstance;
    }

    public String getSDPATH() {
        return SDPATH;
    }


    /**
     * 这个方法一般用于原图为jpg，没有透明底的，如果是png那么不能用这个压缩会变黑底
     * 保存压缩过后的Bitmap到本地内存卡
     * picName 是保持的图片名称，带后缀
     */
    public void saveBitmap(Bitmap bm, String picName) {

        System.out.println("-----------------------------");
        try {
            if (!isFileExist("")) {
                System.out.println("创建文件");
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把bitmap-png格式的图片 转换成jpg图片，保存到指定路径中
     * 不建议直接保存png图片到本地，会显示黑底
     * 这个方法是转换成jpg去保存，因jpg不支持透明，如png透明图片，则转成白底！
     *
     * @param bitmap      源图
     * @param newfileName 新图片的名称
     */
    public void savePng2JpgBitmap(Bitmap bitmap, String newfileName) {
        //复制Bitmap  因为png可以为透明，jpg不支持透明，把透明底明变成白色
        //主要是先创建一张白色图片，然后把原来的绘制至上去
        Bitmap outB = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outB);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);

        try {
            if (!isFileExist("")) {
                System.out.println("创建文件");
                File tempf = createSDDir("");
            }
            File file = new File(SDPATH, newfileName);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            if (outB.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        System.out.println(file.exists());
        return file.exists();
    }

    public File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    /**
     * 删除所有压缩过后保存的全部图片路径
     */
    public void deleteBitmapCacheDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteBitmapCacheDir();
        }
        dir.delete();
    }

    /**
     * 删除该文件夹下面的指定的文件
     * @param fileNamefullpath 这里是文件的全路径
     */
    public void deleteBitmap(String fileNamefullpath) {
        File f = new File(fileNamefullpath);
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * 判断是否有SD卡
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

}
