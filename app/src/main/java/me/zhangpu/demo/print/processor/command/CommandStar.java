package me.zhangpu.demo.print.processor.command;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.zhangpu.demo.print.base.PrinterConfig;
import me.zhangpu.demo.print.encoding.Lang;
import me.zhangpu.demo.print.processor.BitmapUtil;
import me.zhangpu.demo.print.processor.PrintResultCode;

/**
 * Created by virgil on 2018/1/25.
 */

public class CommandStar extends ICommand {
    public StringBuilder sb = new StringBuilder();

    public CommandStar() {
        super();
    }

    static void c(List<byte[]> var0, t var1, int var2, boolean var3) {
        int var4 = var1.b();
        int var5 = var1.a();
        if (var4 > 1024) {
            var4 = 1024;
        }

        if (var2 < 0) {
            var2 = 0;
        }

        if (var2 > 1024) {
            var2 = 1024;
        }

        if (var2 + var4 > 1024) {
            var4 = 1024 - var2;
        }

        int var6 = 0;

        for (int var7 = 0; var7 < var5; ++var7) {
            byte[] var8 = new byte[3 + (var2 + var4 + 7) / 8];
            var8[0] = 98;
            var8[1] = (byte) ((var2 + var4 + 7) / 8 % 256);
            var8[2] = (byte) ((var2 + var4 + 7) / 8 / 256);
            int var9 = 3;
            int var10 = -1;

            int var11;
            for (var11 = 0; var11 < var2; var11 += 8) {
                var8[var9++] = 0;
            }

            var11 = 0;
            byte var12;
            int var13;
            if (var2 % 8 != 0) {
                var11 = 8 - var2 % 8;
                --var9;
                var12 = 0;

                for (var13 = 0; var13 < var11; ++var13) {
                    var12 = (byte) (var12 << 1);
                    if (var1.a(var13, var7)) {
                        var12 = (byte) (var12 | 1);
                    }
                }

                if (var12 != 0) {
                    var10 = var9;
                }

                var8[var9++] = var12;
            }

            while (var11 < var4) {
                var12 = 0;

                for (var13 = 0; var13 < 8; ++var13) {
                    var12 = (byte) (var12 << 1);
                    if (var1.a(var11 + var13, var7)) {
                        var12 = (byte) (var12 | 1);
                    }
                }

                if (var12 != 0) {
                    var10 = var9;
                }

                var8[var9++] = var12;
                var11 += 8;
            }

            if (var10 == -1) {
                ++var6;
            } else {
                if (var3) {
                    var9 = var10 + 1;
                    var8[1] = (byte) ((var9 - 3) % 256);
                    var8[2] = (byte) ((var9 - 3) / 256);
                }

                byte[] var15;
                for (; var6 != 0; var0.add(var15)) {
                    var15 = new byte[]{27, 42, 114, 89, 49, 48, 48, 0};
                    if (var6 >= 100) {
                        var6 -= 100;
                    } else {
                        var15[4] = (byte) (48 + var6 / 100);
                        var6 %= 100;
                        var15[5] = (byte) (48 + var6 / 10);
                        var6 %= 10;
                        var15[6] = (byte) (48 + var6);
                        var6 = 0;
                    }
                }

                var0.add(Arrays.copyOfRange(var8, 0, var9));
            }
        }

        byte[] var14;
        for (; var6 != 0; var0.add(var14)) {
            var14 = new byte[]{27, 42, 114, 89, 49, 48, 48, 0};
            if (var6 >= 100) {
                var6 -= 100;
            } else {
                var14[4] = (byte) (48 + var6 / 100);
                var6 %= 100;
                var14[5] = (byte) (48 + var6 / 10);
                var6 %= 10;
                var14[6] = (byte) (48 + var6);
                var6 = 0;
            }
        }

    }

    @Override
    public void reset() throws Exception {
        byte[] tcmd = new byte[2];
        tcmd[0] = CommandEsc.ESC;
        tcmd[1] = 0x40;
        cmdList.add(tcmd);
    }

    @Override
    public void setChineseEncodingMode() throws IOException {
//        byte[] cmd = new byte[3];
//        cmd[0] = 0x1C;
//        cmd[1] = 0x21;
//        cmd[2] = 1;
//        cmdList.add(cmd);
//
//        byte[] cmd2 = new byte[2];
//        cmd2[0] = 0x1C;
//        cmd2[1] = 0x26;
//        cmdList.add(cmd2);
    }

    @Override
    public void setEncodingType(byte n) throws IOException {
//        byte[] cmd = new byte[3];
//        cmd[0] = 0x1B;
//        cmd[1] = 0x74;
//        cmd[2] = n;
//        cmdList.add(cmd);
    }

    @Override
    public void setLanguage() throws IOException {
//        byte[] cmd = new byte[3];
//        cmd[0] = 0x1B;
//        cmd[1] = 0x52;
//        cmd[2] = 0x15;
//        cmdList.add(cmd);
    }

    /**
     * 设置默认行间距
     *
     * @throws IOException
     */
    @Override
    public void reSetLine() throws IOException {
//        byte[] cmd = new byte[2];
//        cmd[0] = 0x1B;
//        cmd[1] = 0x32;
//        cmdList.add(cmd);
    }

    @Override
    public void setLine(int margin) throws IOException {
//        byte[] cmd = new byte[3];
//        cmd[0] = 0x1B;
//        cmd[1] = 0x33;
//        cmd[2] = (byte) margin;
//        cmdList.add(cmd);
    }

    /**
     * 设置字体
     * done
     *
     * @param font byte
     * @throws Exception |
     */
    @Override
    public void setFont(byte font) throws Exception {
        byte[] tcmd = new byte[4];
        tcmd[0] = CommandEsc.ESC;
        tcmd[1] = 0x1E;
        tcmd[2] = 0x46;
        tcmd[3] = font;
        cmdList.add(tcmd);
    }

    /**
     * 选择对齐方式
     * done
     *
     * @param just | 0，左对齐；1，中间对齐；2，右对齐
     * @throws Exception |
     */
    @Override
    public void setAlign(byte just) throws Exception {
        byte[] cmd = new byte[4];
        cmd[0] = ICommand.ESC;
        cmd[1] = ICommand.GS;
        cmd[2] = 0x61;
        cmd[3] = just;
        cmdList.add(cmd);
    }

    /**
     * Add text to the buffer.
     * Text should either be followed by a line-break, or feed() should be called
     * after this to clear the print buffer.
     *
     * @param data $str Text to print
     * @param lang int  | see{@link Lang}
     * @throws Exception
     */
    @Override
    public void printText(String data, @Lang int lang) throws Exception {
//    	checkStatus();
        if (data != null && data.length() > 0) {
            data = data.replace("$", "￥");
            sb.append(data);
            byte[] byt = data.getBytes(getEncodeByLang(lang));
            cmdList.add(byt);
        }
    }

    /**
     * 设置字号
     *
     * @param size int
     * @throws Exception |
     */
    @Override
    public void setFontSize(int size) throws Exception {
        byte[] definedSize = {0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
        byte[] cmd = new byte[3];
        cmd[0] = 0x1D;
        cmd[1] = 0x21;
        if (size > 0 && size < definedSize.length) {
            cmd[2] = definedSize[size - 1];
        } else {
            cmd[2] = 0x00;
        }
        cmdList.add(cmd);
    }

    /**
     * 设置字号（针式打印机）
     *
     * @param size int
     * @throws Exception |
     */
    @Override
    public void setFontSizeFor76(int size) throws Exception {
        byte[] cmd = new byte[3];
        cmd[0] = 0x1C;
        cmd[1] = 0x21;
        if (size > 1) {
            cmd[2] = (byte) 30;
        } else {
            cmd[2] = 0x00;
        }
        cmdList.add(cmd);
        byte[] cmd2 = new byte[3];
        cmd2[0] = 0x1B;
        cmd2[1] = 0x21;
        if (size > 1) {
            cmd2[2] = (byte) 48;
        } else {
            cmd2[2] = 0x00;
        }
        cmdList.add(cmd2);
    }

    /**
     * 设置字体颜色
     *
     * @param color int | 0,黑色；1，红色
     * @throws Exception |
     */
    @Override
    public void setFontColor(byte color) throws Exception {
        byte[] tcmd = new byte[3];
        tcmd[0] = CommandEsc.ESC;
        tcmd[1] = 0x72;
        tcmd[2] = color;
        cmdList.add(tcmd);
    }

    /**
     * 设置下划线
     * done
     *
     * @param underline byte
     * @throws Exception
     */
    @Override
    public void setUnderline(byte underline) throws Exception {
        byte[] tcmd = new byte[3];
        tcmd[0] = ICommand.ESC;
        tcmd[1] = 0x2D;
        tcmd[2] = underline;
        cmdList.add(tcmd);
    }

    /**
     * 设置为粗体
     *
     * @param bold byte
     * @throws Exception |
     */
    @Override
    public void setBold(byte bold) throws Exception {
        byte[] tcmd = new byte[3];
        tcmd[0] = CommandEsc.ESC;
        tcmd[1] = 0x45;
        tcmd[2] = bold;
        cmdList.add(tcmd);
    }

    /**
     * 打印并走纸
     *
     * @param lines byte
     * @throws Exception |
     */
    @Override
    public void feed(byte lines) throws Exception {
        if (lines > 0) {
            byte[] cmd = new byte[3];
            cmd[0] = CommandEsc.ESC;
            cmd[1] = 0x4A;
            cmd[2] = lines;
            cmdList.add(cmd);
        }
    }

    @Override
    public void margin(byte mm) throws Exception {
        byte[] cmd = new byte[3];
        cmd[0] = CommandEsc.ESC;
        cmd[1] = 0x4A;
        cmd[2] = mm;
        cmdList.add(cmd);
    }

    /**
     * 切纸
     */
    @Override
    public void cut() throws Exception {
        byte[] cmd = new byte[4];
        cmd[0] = CommandEsc.GS;
        cmd[1] = 0x56;
        cmd[2] = 0x42;
        cmd[3] = 0;
        cmdList.add(cmd);
    }

    /**
     * 开始蜂鸣，默认响3次，每次50*3毫秒
     */
    @Override
    public void beep() throws Exception {
        byte[] cmd = new byte[4];
        cmd[0] = CommandEsc.ESC;
        cmd[1] = 0x42;
        cmd[2] = 0x3;
        cmd[3] = 0x3;
        cmdList.add(cmd);
    }

//    @Override public void printImage(int width, int height, byte[] bitmap) throws Exception {
//        byte[] sendData = null;
//        int i = 0, s = 0, j = 0, index = 0, lines = 0;
//        byte[] temp = new byte[(width / 8) * 5];
//        byte[] dHeader = new byte[8];
//        if (bitmap.length != 0) {
//            dHeader[0] = 0x1D;
//            dHeader[1] = 0x76;
//            dHeader[2] = 0x30;
//            dHeader[3] = 0x00;
//            dHeader[4] = (byte) (width / 8);
//            dHeader[5] = 0x00;
//            dHeader[6] = (byte) (bitmap.length % 256);
//            dHeader[7] = (byte) (bitmap.length / 256);
//            cmdList.add(dHeader);
//            for (i = 0; i < (bitmap.length / 5) + 1; i++) {
//                s = 0;
//                if (i < bitmap.length / 5) {
//                    lines = 5;
//                } else {
//                    lines = bitmap.length % 5;
//                }
//                for (j = 0; j < lines * (width / 8); j++) {
//                    temp[s++] = sendData[index++];
//                }
//                cmdList.add(temp);
//                try {
//                    Thread.sleep(60);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                for (j = 0; j < (width / 8) * 5; j++) {
//                    temp[j] = 0;
//                }
//            }
//        }
//    }

    /**
     * 开钱箱
     *
     * @throws Exception |
     */
    @Override
    public void kickDrawer() throws Exception {
        byte[] cmd = new byte[5];
        cmd[0] = CommandEsc.ESC;
        cmd[1] = 0x70;
        cmd[2] = 0x00;
        cmd[3] = 0x40;
        cmd[4] = 0x50;
        cmdList.add(cmd);
    }

    /**
     * 打印图片
     *
     * @param bm Bitmap
     * @throws Exception |
     */
    @Override
    public void printBitmap(Bitmap bm) throws Exception {
        int width = bm.getWidth();
        int height = bm.getHeight();
        byte[] pixels = null;
        // x方向压缩
        final int bytesWidth = width / 8;
        // 压缩后图像缓存
        pixels = new byte[bytesWidth * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < bytesWidth; x++) {

                // x方向压缩8:1
                byte b = 0;
                for (int i = 0; i < 8; i++) {
                    int color = bm.getPixel(x * 8 + i, y);
                    if ((color & 0xffffff) < GREY_EDGE && (color & 0xffffff) > 0) {
                        // 使用大端
                        b |= 1 << (7 - i);
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
        // xL
        command[4] = (byte) (width >> 3);
        // xH
        command[5] = (byte) (width >> 11);
        // yL
        command[6] = (byte) (height);
        // yH
        command[7] = (byte) (height >> 8);
        cmdList.add(command);
        cmdList.add(pixels);
        cmdBitmapIndex.put(cmdList.size() - 1, cmdList.size() - 1);
        feed((byte) 1);
    }

    /**
     * 打印二维码的图片
     *
     * @param bm Bitmap
     * @throws Exception |
     */
    @Override
    public void printQRBitmap(Bitmap bm, int size) throws Exception {
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
//					if ((color & 0xffffff) < 0xac9c9c && (color & 0xffffff)  > 0/* matrix.get(x, y*8+i) */) {
                    if ((color & 0xffffff) == 0) {
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
        cmdList.add(command);
        cmdList.add(pixels);
        cmdBitmapIndex.put(cmdList.size() - 1, cmdList.size() - 1);
    }

    /**
     * 打印条形码
     *
     * @param barcode String | 条码内容
     * @param big     boolean | 是否是大条形码
     * @throws Exception
     */
    @Override
    public void printBarcode(String barcode, boolean big) throws Exception {
        byte[] command = new byte[9];
        //设置条码高度
        command[0] = 0x1d;
        command[1] = 0x68;
        command[2] = 127;// 像素

        // 设置条码宽度
        command[3] = 0x1D;
        command[4] = 0x77;
        command[5] = (byte) (big ? 3 : 2);

        //文案打印在条码的相对位置
        command[6] = 0x1D;
        command[7] = 0x48;
        command[8] = (byte) 0;//0,不打印;1,条码上方;2,条码下方;3,条码上、下方都打印
        cmdList.add(command);

        byte[] buffer = new byte[6];
        buffer[0] = 0x1d;
        buffer[1] = 0x6b;
        buffer[2] = 73;// CODE128
        buffer[4] = 123;
        buffer[5] = 66;

        byte[] byt = barcode.getBytes();
        buffer[3] = (byte) (byt.length + 2);
        cmdList.add(buffer);
        cmdList.add(byt);
    }

    @Override
    public void printFoodBoxBarcode(String barcode) throws Exception {
        byte[] command = new byte[9];
        // 高度
        command[0] = 0x1d;
        command[1] = 0x68;
        command[2] = 70;// 像素
        command[3] = 0x1D;
        command[4] = 0x77;
        command[5] = 2;
        command[6] = 0x1D;
        command[7] = 0x48;
        command[8] = (byte) 2;
        cmdList.add(command);
        // GS K
        byte[] buffer = new byte[6];
        buffer[0] = 0x1d;
        buffer[1] = 0x6b;
        buffer[2] = 73;// CODE128
        buffer[4] = 123;
        buffer[5] = 66;

        byte[] byt = barcode.getBytes();
        buffer[3] = (byte) (byt.length + 2);
        cmdList.add(buffer);
        cmdList.add(byt);
    }

    @Override
    public void setJDWZ() throws IOException {
        byte[] cmd = new byte[4];
        cmd[0] = 0x1B;
        cmd[1] = 0x24;
        cmd[2] = (byte) (2 % 256);
        cmd[3] = (byte) (2 / 256);
        cmdList.add(cmd);
    }

    @Override
    public void setGprintText(String data) throws IOException {
        byte[] cmd = data.getBytes("UTF-8");
        cmdList.add(cmd);
    }

    private void prepareBitmap() {

    }

    @Override
    public int startWrite(PrinterConfig printer, String uniq) throws Exception {
        byte[] originCommand = getCommands();
        int totalLength = originCommand.length;
        int var2 = 0;
        byte[] splitCommand = new byte[totalLength];

        int i;
        for (i = 0; i < totalLength; ++i) {
            splitCommand[i] = originCommand[i + var2];
        }

        i = 0;
        int splitLength = splitCommand.length;

        while (i < splitLength) {

            int result = printer.write(splitCommand);
            if (result == 0) {
                break;
            }

            i += result;
            splitCommand = new byte[originCommand.length - var2 - i];
            //这么写就有点搞笑了，有空再处理
            for (int j = 0; j < splitCommand.length; ++j) {
                splitCommand[j] = originCommand[j + var2 + i];
            }
        }
        return PrintResultCode.SUCCESS;
    }

    private byte[] getCommands() {
        List<byte[]> cmd = getFinalCmdList();
        int var1 = 0;

        byte[] var3;
        for (Iterator var2 = cmd.iterator(); var2.hasNext(); var1 += var3.length) {
            var3 = (byte[]) var2.next();
        }

        byte[] var6 = new byte[var1];
        int var7 = 0;

        byte[] var5;
        for (Iterator var4 = cmd.iterator(); var4.hasNext(); var7 += var5.length) {
            var5 = (byte[]) var4.next();
            System.arraycopy(var5, 0, var6, var7, var5.length);
        }

        return var6;
    }

    @Override
    public List<byte[]> getFinalCmdList() {
        List<byte[]> cmd = new ArrayList<>();
        //Begin
        cmd.add(new byte[]{27, 42, 114, 65, 27, 42, 114, 80, 48, 0, 27, 42, 114, 69, 49, 0});
        Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
        String result=sb.toString();
//        LogUtil.log("打印的数据\n"+result);
        Bitmap bitmap = BitmapUtil.createBitmapFromText(result, 24, 680, typeface);
        t var6 = new t(bitmap, false, -1, true);
        c(cmd, var6, 0, true);
        byte[] cut = new byte[]{49, 51, 0};
        cmd.add(new byte[]{27, 42, 114, 101});
        cmd.add(cut);
        cmd.add(new byte[]{27, 12, 25});
        cmd.add(new byte[]{27, 42, 114, 70});
        cmd.add(cut);
        cmd.add(new byte[]{27, 12, 0});
        //End
        cmd.add(new byte[]{27, 42, 114, 66});
        return cmd;
    }

    class t {
        private int[][] a;
        private int b;
        private int c;

        public t(Bitmap var1, boolean var2, int var3, boolean var4) {
            Bitmap var6;
            if (var3 <= 0) {
                var3 = var1.getWidth();
            }

            if (var4) {
                var6 = Bitmap.createScaledBitmap(var1, var3, var1.getHeight() * var3 / var1.getWidth(), false);
            } else {
                var6 = Bitmap.createScaledBitmap(var1, var3, var1.getHeight(), false);
            }

            this.b = var6.getHeight();
            this.c = var6.getWidth();

            this.a = new int[this.c + 7][this.b + 7];
            int[] var7 = new int[this.c * this.b];
            var6.getPixels(var7, 0, var6.getWidth(), 0, 0, var6.getWidth(), var6.getHeight());
            int var8;
            int var9;
            for (var8 = 0; var8 < this.b; ++var8) {
                for (var9 = 0; var9 < this.c; ++var9) {
                    this.a[var9][var8] = var7[var8 * this.c + var9];
                }
            }

            if (var2) {
                this.c();
            }

        }

        public int a() {
            return this.b;
        }

        public int b() {
            return this.c;
        }

        boolean a(int var1, int var2) {
            int var3 = this.a[var1][var2];
            return Color.alpha(var3) == 0 ? false : (Color.red(var3) + Color.green(var3) + Color.blue(var3)) / 3 < 127;
        }

        private void c() {
            int[][] var1 = new int[this.c][this.b];

            for (int var2 = 0; var2 < this.b; ++var2) {
                int var3;
                int var4;
                if ((var2 & 1) == 0) {
                    for (var3 = 0; var3 < this.c; ++var3) {
                        var1[var3][var2] += 255 - this.b(var3, var2);
                        if (var1[var3][var2] >= 255) {
                            this.a[var3][var2] = -16777216;
                            var1[var3][var2] -= 255;
                        } else {
                            this.a[var3][var2] = -1;
                        }

                        var4 = var1[var3][var2] / 16;
                        if (var3 < this.c - 1) {
                            var1[var3 + 1][var2] += var4 * 7;
                        }

                        if (var2 < this.b - 1) {
                            var1[var3][var2 + 1] += var4 * 5;
                            if (var3 > 0) {
                                var1[var3 - 1][var2 + 1] += var4 * 3;
                            }

                            if (var3 < this.c - 1) {
                                var1[var3 + 1][var2 + 1] += var4;
                            }
                        }
                    }
                } else {
                    for (var3 = this.c - 1; var3 >= 0; --var3) {
                        var1[var3][var2] += 255 - this.b(var3, var2);
                        if (var1[var3][var2] >= 255) {
                            this.a[var3][var2] = -16777216;
                            var1[var3][var2] -= 255;
                        } else {
                            this.a[var3][var2] = -1;
                        }

                        var4 = var1[var3][var2] / 16;
                        if (var3 > 0) {
                            var1[var3 - 1][var2] += var4 * 7;
                        }

                        if (var2 < this.b - 1) {
                            var1[var3][var2 + 1] += var4 * 5;
                            if (var3 < this.c - 1) {
                                var1[var3 + 1][var2 + 1] += var4 * 3;
                            }

                            if (var3 > 0) {
                                var1[var3 - 1][var2 + 1] += var4 * 1;
                            }
                        }
                    }
                }
            }

        }

        private int b(int var1, int var2) {
            int var3 = this.a[var1][var2];
            float var4 = 1.5F;
            if (Color.alpha(var3) == 0) {
                return 255;
            } else {
                int var5 = (int) ((float) ((double) (Color.red(var3) + Color.green(var3) + Color.blue(var3)) / 3.0D) * var4);
                if (var5 > 255) {
                    var5 = 255;
                }

                return var5;
            }
        }
    }

    @Override
    public int startBytesWrite(PrinterConfig printer, String uniq) throws Exception {
        return 0;
    }
}
