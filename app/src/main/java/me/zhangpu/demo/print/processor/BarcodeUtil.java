package me.zhangpu.demo.print.processor;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import me.zhangpu.demo.print.printer.PrintLog;


public class BarcodeUtil {

    private static final String TAG = "BarcodeUtil";

    /**
     * 解析扫到的条形码
     *
     * @param orderNoStr
     * @return int[0]:订单号
     * int[1]:点菜数量
     * int[2]:菜的序号
     * int[3]:日期
     */
    public static int[] parsingBarcode(String orderNoStr) {
        int[] noArr = new int[4];
        if (orderNoStr != null && orderNoStr.length() == 9) {
            try {
                noArr[0] = Integer.parseInt(orderNoStr.substring(0, 3), 16);
                noArr[1] = Integer.parseInt(orderNoStr.substring(3, 5), 16);
                noArr[2] = Integer.parseInt(orderNoStr.substring(5, 7), 16);
                noArr[3] = Integer.parseInt(orderNoStr.substring(7, 9), 16);
            } catch (Exception e) {
                return null;
            }
        }
        return noArr;
    }

    /**
     * 生成长度为九的条形码
     * 十进制转成十六进制
     * <p>
     * 1、orderNo 订单号
     * 2、count 菜总数
     * 3、index 菜的序号
     * 4、day  日期
     *
     * @return
     */
    public static String getBarcode(int orderNo, int count, int index, int day) {
        StringBuffer barSB = new StringBuffer();
        try {
            barSB.append(buwei(Integer.toHexString(orderNo), 3));
            barSB.append(buwei(Integer.toHexString(count), 2));
            barSB.append(buwei(Integer.toHexString(index), 2));
            barSB.append(buwei(Integer.toHexString(day), 2));
        } catch (Exception e) {
            PrintLog.e("","",e);
            return "000000000";
        }
        return barSB.toString();
    }

    /**
     * 长度不足时用“0”补位
     *
     * @param value  值
     * @param length 额定长度
     * @return
     */
    private static String buwei(String value, int length) {
        if (value != null) {
            return getZero(length - value.length()) + value;
        }
        return getZero(length);

    }

    /**
     * 生成count个“0”
     *
     * @param count
     * @return
     */
    private static String getZero(int count) {
        if (count < 1) {
            return "";
        }
        StringBuffer ab = new StringBuffer();
        for (int i = 0; i < count; i++) {
            ab.append("0");
        }
        return ab.toString();
    }

    /**
     * 生成条形码
     *
     * @param str
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createBarCodeImage(String str, final int width, final int height) {
        Bitmap bitmap = null;
        try {
            // 判断URL合法性
            if (str == null || "".equals(str) || str.length() < 1) {
                return null;
            }
            BitMatrix result;
            try {
                result = new MultiFormatWriter().encode(str,
                        BarcodeFormat.CODE_128, width, height, null);
            } catch (IllegalArgumentException iae) {
                // Unsupported format
                return null;
            }
            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? 0x00000000 : 0xffffffff;
                }
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        } catch (Exception e) {
            PrintLog.e("","",e);
            bitmap = null;
        }
        return bitmap;
    }

    public static Bitmap createQRImage(String str, final int width, final int height) {
        Bitmap bitmap = null;
        try {
            // 判断URL合法性
            if (str == null || "".equals(str) || str.length() < 1) {
                return null;
            }
            BitMatrix result;
            try {
                result = new MultiFormatWriter().encode(str,
                        BarcodeFormat.QR_CODE, width, height, null);
            } catch (IllegalArgumentException iae) {
                // Unsupported format
                PrintLog.e(TAG, "", iae);
                return null;
            }
            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? 0x00000000 : 0xffffffff;
                }
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        } catch (Exception e) {
            PrintLog.e("","",e);
            bitmap = null;
        }
        return bitmap;
    }
}
