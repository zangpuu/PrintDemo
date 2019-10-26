package me.zhangpu.demo.print.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class ImageProcess {


    //对图像进行线性灰度变化
    public static Bitmap lineGrey(Bitmap image) {
        //得到图像的宽度和长度
        int width = image.getWidth();
        int height = image.getHeight();
        //创建线性拉升灰度图像
        Bitmap linegray = null;
        linegray = image.copy(Bitmap.Config.RGB_565, true);
        //依次循环对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到每点的像素值
                int col = image.getPixel(i, j);
                int alpha = col & 0xFF000000;
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 增加了图像的亮度
                red = (int) (1.1 * red + 30);
                green = (int) (1.1 * green + 30);
                blue = (int) (1.1 * blue + 30);
                //对图像像素越界进行处理
                if (red >= 255) {
                    red = 255;
                }

                if (green >= 255) {
                    green = 255;
                }

                if (blue >= 255) {
                    blue = 255;
                }
                // 新的ARGB
                int newColor = alpha | (red << 16) | (green << 8) | blue;
                //设置新图像的RGB值
                linegray.setPixel(i, j, newColor);
            }
        }
        return linegray;
    }

    /**
     * 转换为纯位图
     *
     * @param bmpSrc    Bitmap
     * @param threshold int | 8bit,颜色边界，大于边界处理为黑色，小于边界处理为白色
     * @return Bitmap
     */
    public static Bitmap toBinary(Bitmap bmpSrc, int threshold) {
        int width, height;
        height = bmpSrc.getHeight();
        width = bmpSrc.getWidth();
        Bitmap bmpBinary = Bitmap.createBitmap(bmpSrc);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get one pixel color
                int pixel = bmpSrc.getPixel(x, y);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                int gray = (int) (red * 0.3 + green * 0.59 + blue * 0.11);

                //get binary value
                if (gray < threshold) {
                    bmpBinary.setPixel(x, y, 0xFF000000);
                } else {
                    bmpBinary.setPixel(x, y, 0xFFFFFFFF);
                }

            }
        }
        return bmpBinary;
    }

    /**
     * 计算平均阈值
     *
     * @param img Bitmap
     * @return Bitmap
     */
    public static Bitmap binarization(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int area = width * height;
        int gray[][] = new int[width][height];
        int average = 0;// 灰度平均值
        int graysum = 0;
        int graymean = 0;
        int grayfrontmean = 0;
        int graybackmean = 0;
        int pixelGray;
        int front = 0;
        int back = 0;
        int[] pix = new int[width * height];
        img.getPixels(pix, 0, width, 0, 0, width, height);
        for (int i = 1; i < width; i++) { // 不算边界行和列，为避免越界
            for (int j = 1; j < height; j++) {
                int x = j * width + i;
                int r = (pix[x] >> 16) & 0xff;
                int g = (pix[x] >> 8) & 0xff;
                int b = pix[x] & 0xff;
                pixelGray = (int) (0.3 * r + 0.59 * g + 0.11 * b);// 计算每个坐标点的灰度
                gray[i][j] = (pixelGray << 16) + (pixelGray << 8) + (pixelGray);
                graysum += pixelGray;
            }
        }
        graymean = (int) (graysum / area);// 整个图的灰度平均值
        average = graymean;
//        PrintLog.i(TAG, "Average:" + average);
        for (int i = 0; i < width; i++) // 计算整个图的二值化阈值
        {
            for (int j = 0; j < height; j++) {
                if (((gray[i][j]) & (0x0000ff)) < graymean) {
                    graybackmean += ((gray[i][j]) & (0x0000ff));
                    back++;
                } else {
                    grayfrontmean += ((gray[i][j]) & (0x0000ff));
                    front++;
                }
            }
        }
        int frontvalue = (int) (grayfrontmean / front);// 前景中心
        int backvalue = (int) (graybackmean / back);// 背景中心
        float G[] = new float[frontvalue - backvalue + 1];// 方差数组
        int s = 0;
//        PrintLog.i(TAG, "Front:" + front + "**Frontvalue:" + frontvalue + "**Backvalue:" + backvalue);
        for (int i1 = backvalue; i1 < frontvalue + 1; i1++)// 以前景中心和背景中心为区间采用大津法算法（OTSU算法）
        {
            back = 0;
            front = 0;
            grayfrontmean = 0;
            graybackmean = 0;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (((gray[i][j]) & (0x0000ff)) < (i1 + 1)) {
                        graybackmean += ((gray[i][j]) & (0x0000ff));
                        back++;
                    } else {
                        grayfrontmean += ((gray[i][j]) & (0x0000ff));
                        front++;
                    }
                }
            }
            grayfrontmean = (int) (grayfrontmean / front);
            graybackmean = (int) (graybackmean / back);
            G[s] = (((float) back / area) * (graybackmean - average)
                    * (graybackmean - average) + ((float) front / area)
                    * (grayfrontmean - average) * (grayfrontmean - average));
            s++;
        }
        float max = G[0];
        int index = 0;
        for (int i = 1; i < frontvalue - backvalue + 1; i++) {
            if (max < G[i]) {
                max = G[i];
                index = i;
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int in = j * width + i;
                if (((gray[i][j]) & (0x0000ff)) < (index + backvalue)) {
                    pix[in] = Color.rgb(0, 0, 0);
                } else {
                    pix[in] = Color.rgb(255, 255, 255);
                }
            }
        }

        Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        temp.setPixels(pix, 0, width, 0, 0, width, height);
        return temp;
    }


    // Return histogram of grayscale image
    public static int[] imageHistogram(Bitmap input) {

        int[] histogram = new int[256];

        for (int i = 0; i < histogram.length; i++) histogram[i] = 0;

        for (int i = 0; i < input.getWidth(); i++) {
            for (int j = 0; j < input.getHeight(); j++) {
                int pixel = input.getPixel(i, j);

                int red = Color.red(pixel);
                histogram[red]++;
            }
        }

        return histogram;

    }

    public static Bitmap toFixedSizeAndGrayscale(Bitmap bmpOriginal) {
        return toFixedSizeAndGrayscale(bmpOriginal, 400);

    }

    /**
     * 图片灰阶
     *
     * @param bmpOriginal Bitmap
     * @param maxWidth    float | 图片最大宽度
     * @return Bitmap
     */
    public static Bitmap toFixedSizeAndGrayscale(Bitmap bmpOriginal, float maxWidth) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();

        float scale = 1;
        if (width > maxWidth) {
            scale = maxWidth / width;
        }

        height = (int) (height * scale);
        width = (int) (width * scale);
        if (height <= 0) {
            return null;
        }

        Bitmap resize = Bitmap.createScaledBitmap(bmpOriginal, width, height, false);
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(resize, (float) (maxWidth - width), 0.0F, paint);
//        bmpOriginal.recycle();

//        resize.recycle();
        return bmpGrayscale;
    }
}
