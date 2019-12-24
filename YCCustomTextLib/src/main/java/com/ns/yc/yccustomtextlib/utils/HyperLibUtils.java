/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.ns.yc.yccustomtextlib.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 功能工具类
 *     revise:
 * </pre>
 */
public final class HyperLibUtils {

    private static final String IMG_REGEX1 = "<img.*?src=\\\"(.*?)\\\".*?>";
    private static final String IMG_REGEX2 = "<(img|IMG)(.*?)(/>|></img>|>)";
    private static final String IMG_REGEX3 = "(src|SRC)=(\"|\')(.*?)(\"|\')";

    /**
     * dp转化为px
     * @param context                   上下文
     * @param dipValue                  dp值
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    /**
     * 关键字高亮显示
     * @param target                    需要高亮的关键字
     * @param text	                    需要显示的文字
     * @return                          spannable
     */
    public static SpannableStringBuilder highlight(String text, String target, int color) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span;
        try {
            Pattern p = Pattern.compile(target);
            Matcher m = p.matcher(text);
            while (m.find()) {
                // 需要重复！
                span = new ForegroundColorSpan(color);
                spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannable;
    }

    /**
     * 根据img标签分割出图片和字符串
     * @param targetStr                 要处理的字符串
     * @description                     切割字符串，将文本和img标签碎片化，如"ab<img>cd"转换为"ab"、"<img>"、"cd"
     */
    public static List<String> cutStringByImgTag(String targetStr) {
        List<String> splitTextList = new ArrayList<>();
        Pattern pattern = Pattern.compile(IMG_REGEX1);
        Matcher matcher = pattern.matcher(targetStr);
        int lastIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                splitTextList.add(targetStr.substring(lastIndex, matcher.start()));
            }
            splitTextList.add(targetStr.substring(matcher.start(), matcher.end()));
            lastIndex = matcher.end();
        }
        if (lastIndex != targetStr.length()) {
            splitTextList.add(targetStr.substring(lastIndex));
        }
        return splitTextList;
    }


    /**
     * 获取img标签中的src值
     * @param content                   内容
     * @return
     */
    public static String getImgSrc(String content){
        String strSrc = null;
        //目前img标签标示有3种表达式
        //<img alt="" src="1.jpg"/>   <img alt="" src="1.jpg"></img>     <img alt="" src="1.jpg">
        //开始匹配content中的<img />标签
        Pattern pImg = Pattern.compile(IMG_REGEX2);
        Matcher mImg = pImg.matcher(content);
        boolean resultImg = mImg.find();
        if (resultImg) {
            while (resultImg) {
                //获取到匹配的<img />标签中的内容
                String strImg = mImg.group(2);
                //开始匹配<img />标签中的src
                Pattern pSrc = Pattern.compile(IMG_REGEX3);
                Matcher mSrc = pSrc.matcher(strImg);
                if (mSrc.find()) {
                    strSrc = mSrc.group(3);
                }
                //结束匹配<img />标签中的src
                //匹配content中是否存在下一个<img />标签，有则继续以上步骤匹配<img />标签中的src
                resultImg = mImg.find();
            }
        }
        return strSrc;
    }

    /**
     * 从html文本中提取图片地址，或者文本内容
     * @param html                      传入html文本
     * @param isGetImage                true获取图片，false获取文本
     * @return
     */
    public static ArrayList<String> getTextFromHtml(String html, boolean isGetImage){
        ArrayList<String> imageList = new ArrayList<>();
        ArrayList<String> textList = new ArrayList<>();
        //根据img标签分割出图片和字符串
        List<String> list = cutStringByImgTag(html);
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i);
            if (text.contains("<img") && text.contains("src=")) {
                //从img标签中获取图片地址
                String imagePath = getImgSrc(text);
                imageList.add(imagePath);
            } else {
                textList.add(text);
            }
        }
        //判断是获取图片还是文本
        if (isGetImage) {
            return imageList;
        } else {
            return textList;
        }
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     * @param filePath                              文件路径
     * @param newWidth                              宽
     * @param newHeight                             高
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int newWidth, int newHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap decodeFile = BitmapFactory.decodeFile(filePath, options);
        HyperLogUtils.d("HyperLibUtils----getSmallBitmap---byteCount压缩前大小--"+decodeFile);
        // Calculate inSampleSize
        // 计算图片的缩放值
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        int bitmapByteCount = bitmap.getByteCount();
        HyperLogUtils.d("HyperLibUtils----getSmallBitmap---byteCount压缩中大小--"+bitmapByteCount);
        // 质量压缩
        Bitmap newBitmap = compressImage(bitmap, 500);
        int byteCount = newBitmap.getByteCount();
        HyperLogUtils.d("HyperLibUtils----getSmallBitmap---byteCount压缩后大小--"+byteCount);
        if (bitmap != null){
            // 手动释放资源
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     * 计算图片的缩放值
     * @param options                           属性
     * @param reqWidth                          宽
     * @param reqHeight                         高
     * @return
     */
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

    /**
     * 质量压缩
     * @param image                             bitmap
     * @param maxSize                           最大值
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int maxSize){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 80;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while ( os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }
        Bitmap bitmap = null;
        byte[] b = os.toByteArray();
        if (b.length != 0) {
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return bitmap;
    }

    /**
     * 通过像素压缩图片，将修改图片宽高，适合获得缩略图，Used to get thumbnail
     * @param srcPath                           图片路径
     * @param pixelW                            宽
     * @param pixelH                            高
     * @return
     */
    public static Bitmap compressBitmapByPath(String srcPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        //此时返回bm为空
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        //这里设置高度为800f
        float hh = pixelH;
        //这里设置宽度为480f
        float ww = pixelW;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        //be=1表示不缩放
        int be = 1;
        //如果宽度大的话根据宽度固定大小缩放
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            //如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        //设置缩放比例
        newOpts.inSampleSize = be;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        //return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * 通过大小压缩，将修改图片宽高，适合获得缩略图，Used to get thumbnail
     * @param image                                 图片
     * @param pixelW                                宽
     * @param pixelH                                高
     * @return
     */
    public static Bitmap compressBitmapByBmp(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if( os.toByteArray().length / 1024>1024) {
            //判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();
            //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);
            //这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 设置高度为240f时，可以明显看到图片缩小了
        float hh = pixelH;
        // 设置宽度为120f，可以明显看到图片缩小了
        float ww = pixelW;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        //be=1表示不缩放
        int be = 1;
        if (w > h && w > ww) {
            //如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            //如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        //设置缩放比例
        newOpts.inSampleSize = be;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        int desWidth = (int) (w / be);
        int desHeight = (int) (h / be);
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
        //压缩好比例大小后再进行质量压缩
        //return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * 根据Uri获取真实的文件路径
     * @param context                               上下文
     * @param uri                                   图片uri地址
     * @return
     */
    public static String getFilePathFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        ContentResolver resolver = context.getContentResolver();
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
            if (pfd == null) {
                return null;
            }
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);
            File outputDir = context.getCacheDir();
            File outputFile = File.createTempFile("image", "tmp", outputDir);
            String tempFilename = outputFile.getAbsolutePath();
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return new File(tempFilename).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null){
                    input.close();
                }
                if (output != null){
                    output.close();
                }
            } catch (Throwable t) {
                // Do nothing
                t.printStackTrace();
            }
        }
        return null;
    }



    public static String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap decodeResource(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }


}
