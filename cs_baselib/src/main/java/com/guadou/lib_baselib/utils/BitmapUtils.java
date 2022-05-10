package com.guadou.lib_baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片的保存和压缩到本地的工具类
 */
public class BitmapUtils {

    private static BitmapUtils mInstance = null;
    private Context mContext;
    private static String SDPATH = "";

    private BitmapUtils(Context context) {
        mContext = context;
        SDPATH = hasSdcard() ? context.getExternalFilesDir(null) + "/pos/" : context.getFilesDir() + "/pos/";

        //如果不存在就创建一个文件夹
        File dir = new File(SDPATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static BitmapUtils getInstance(Context context) {
        if (mInstance == null)
            return new BitmapUtils(context);
        else
            return mInstance;
    }

    public String getSDPATH() {
        return SDPATH;
    }

    /**
     * 获取压缩过后的小图片
     */
    public Bitmap getSmallBitmap(String filePath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1200f;//这里设置高度为800f
        float ww = 720f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(filePath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }


    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 50 && options > 10) {
//        while (baos.size() / 1024 > 100 && options > 10) {
            baos.reset();//重置baos即清空baos
            //这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//每次都减少10
        }
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }


    /**
     * 保存压缩过后的Bitmap到本地内存卡
     */
    public File saveBitmap(Bitmap bm, String picName) {

        System.out.println("-----------------------------");
        try {
            if (!isFileExist("")) {
                System.out.println("创建文件");
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return f;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public File createSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    /**
     * 删除所有压缩过后保存的图片
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
     * 判断是否有SD卡
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }


}
