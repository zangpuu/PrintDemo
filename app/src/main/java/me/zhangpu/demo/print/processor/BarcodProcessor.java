package me.zhangpu.demo.print.processor;

import android.graphics.Bitmap;

import me.zhangpu.demo.print.printer.PrintLog;

public class BarcodProcessor {
    public static Bitmap createQRImage(String str, final int width, final int height) {
        try {
            return BarcodeUtil.createQRImage(str, width, height);
        } catch (Exception e) {
            PrintLog.e("","",e);
        } catch (Error e) {
            PrintLog.e("","",e);
        }
        return null;
    }
}
