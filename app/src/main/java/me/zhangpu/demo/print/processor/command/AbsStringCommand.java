package me.zhangpu.demo.print.processor.command;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * 基于字符创的指令集
 */
public abstract class AbsStringCommand extends ICommand {
    protected StringBuilder cmdString = new StringBuilder();


    @Override
    public void reset() throws Exception {

    }

    @Override
    public void setAlign(byte just) throws Exception {

    }

    @Override
    public void setFont(byte font) throws Exception {

    }

    @Override
    public void setFontSize(int size) throws Exception {

    }

    @Override
    public void setFontColor(byte color) throws Exception {

    }

    @Override
    public void setFontSizeFor76(int size) throws Exception {

    }

    @Override
    public void setUnderline(byte underline) throws Exception {

    }

    @Override
    public void setBold(byte bold) throws Exception {

    }

    @Override
    public void feed(byte lines) throws Exception {

    }

    @Override
    public void margin(byte mm) throws Exception {

    }

    @Override
    public void cut() throws Exception {

    }

    @Override
    public void beep() throws Exception {

    }

    @Override
    public void kickDrawer() throws Exception {

    }

    @Override
    public void printBitmap(Bitmap bm) throws Exception {

    }

    @Override
    public void printQRBitmap(Bitmap bm, int size) throws Exception {

    }

    @Override
    public void printBarcode(String barcode, boolean big) throws Exception {

    }

    @Override
    public void printFoodBoxBarcode(String barcode) throws Exception {

    }

    @Override
    public void setChineseEncodingMode() throws IOException {

    }

    @Override
    public void setEncodingType(byte n) throws IOException {

    }

    @Override
    public void setLanguage() throws IOException {

    }

    @Override
    public void setJDWZ() throws IOException {

    }

    @Override
    public void setGprintText(String data) throws IOException {

    }

    @Override
    public void reSetLine() throws IOException {

    }

    @Override
    public void setLine(int margin) throws IOException {

    }
}
