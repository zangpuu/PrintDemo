package me.zhangpu.demo.print;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.OutputStream;

import me.zhangpu.demo.print.base.esc.ESCPrinterCommand;

/**
 * PrintImage,图片打印类,将Bigmap转换为可打印的流
 */
public class PrintImage {
    /**
     * 打印图片
     *
     * @param out OutputStream
     * @param bm  Bitmap
     */
    public static void printBitmap(OutputStream out, Bitmap bm) throws IOException {
        int width = bm.getWidth();
        int height = bm.getHeight();
        byte[] pixels = null;
        final int bytesWidth = width / 8;// x方向压缩
        pixels = new byte[bytesWidth * height];// 压缩后图像缓存
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < bytesWidth; x++) {

                // x方向压缩8:1
                byte b = 0;
                for (int i = 0; i < 8; i++) {
                    int color = bm.getPixel(x * 8 + i, y);
                    if ((color & 0xffffff) < 0xac9c9c && (color & 0xffffff) > 0/* matrix.get(x, y*8+i) */) {
//					if ((color & 0xffffff) == 0) {
                        b |= 1 << (7 - i);// 使用大端
                    }
                }
                pixels[y * bytesWidth + x] = b;
            }
        }
        byte[] command = new byte[8];
        command[0] = 0x1d;
        command[1] = 0x76;
        command[2] = 0x30;
        command[3] = 0x00;
        command[4] = (byte) (width >> 3);// xL
        command[5] = (byte) (width >> 11);// xH
        command[6] = (byte) (height);// yL
        command[7] = (byte) (height >> 8);// yH
        out.write(command);
        out.write(pixels);
        feed((byte) 1, out);
        out.flush();
    }

    public static void feed(byte lines, OutputStream out) throws IOException {
        if (lines <= 1) {
            byte[] tcmd = new byte[1];
            tcmd[0] = ESCPrinterCommand.LF;
            out.write(tcmd);
        } else {
            byte[] tcmd = new byte[3];
            tcmd[0] = ESCPrinterCommand.ESC;
            tcmd[1] = 0x64;
            tcmd[2] = lines;   //0.125mm line height
            out.write(tcmd);
        }
    }
}
