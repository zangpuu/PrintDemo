package me.zhangpu.demo.print.base;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import me.zhangpu.demo.print.encoding.Lang;
import me.zhangpu.demo.print.printer.PrintLog;
import me.zhangpu.demo.print.processor.PrintBuilder;
import me.zhangpu.demo.print.processor.command.CommandEsc;
import me.zhangpu.demo.print.processor.command.ICommand;

public abstract class PrinterConfig implements Serializable {

    private final static String COMMAND_STAR = "star";

    public static boolean checkPrinterStatus = false;
    private ICommand command = null;
    /**
     * 标签打印的偏移量
     */
    public static int tscOffset = 13;
    /**
     * 标签打印机的反向
     * 使用{@link #reverse}
     */
    @Deprecated
    public static boolean tscReservse = false;
    /**
     * 打印机链接类型
     */
    public int type = 0;
    /**
     * 纸张类型：0，热敏；1，针式；2，标签
     * see{@link PaperType}
     */
    @PaperType
    public int paperType = PaperType.Thermal;
    /**
     * 语言：0，简体中文；1，繁体
     */
    public int lang = Lang.CHINESE_SIMPLIE;
    /**
     * 超时时间
     */
    public int timeOut = 2;
    /**
     * 重打次数
     */
    public int retryTimes = 0;
    /**
     * 打印机尺寸
     */
    public int paperSize = 48;
    /**
     * 标签打印机的逆向:1,逆向；其他，默认
     */
    public int reverse = 0;
    /**
     * 命令类型
     */
    public String commandType = "";
    public boolean controlByteAlone = false;
    public boolean isUseReceipt = false;
    protected OutputStream outputStream;
    protected InputStream inputStream;
    /**
     * 1：是，0：不是
     */
    private int mIsOldPrinter = 0;
    /**
     * 打印机型号
     */
    protected int printerModel = 0;
    /**
     * 空票类型
     */
    protected int controlType = 0;
    /**
     * 支持状态检测
     */
    private int isSupportStatusCheck = 0;
    /**
     * 三星的切刀指令
     */
    private int cutCommandSamSung = 0;
    /**
     * 是否需要切割图片数据
     */
    private boolean isNeedImgDivide = false;

    /**
     * 回执模式
     */
    protected String receiptModel = null;


    public abstract String getUniq();

    public abstract void connect() throws Exception;

    public abstract void closeConnect();

    /**
     * 输出流
     * 单条指令，整体发送逻辑基于ICommand
     *
     * @param data byte[] | 内容
     * @return int | 成功输出的内容长度
     * @throws Exception | 抛出的异常
     */
    public abstract int write(byte[] data) throws Exception;

    /**
     * 指令集合，自身掌控发送逻辑
     *
     * @param data
     * @return
     * @throws Exception
     */
    public int write(List<byte[]> data) throws Exception {
        return 0;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public byte[] read(int targetLength) throws IOException {
        if (this.inputStream != null) {
            byte[] retData = new byte[targetLength];
            byte[] result = new byte[targetLength];
            int readLen = 0;
            int totalReadLen = 0;
            while (totalReadLen < targetLength) {
                readLen = this.inputStream.read(retData, 0, targetLength);
                if (readLen > 0) {
                    PrintLog.i("PrinterStatus-Base", "len=" + readLen);
                    PrintLog.i("PrinterStatus-Base", retData);
                    System.arraycopy(retData, 0, result, totalReadLen, readLen);
                } else {
                    return result;
                }
                totalReadLen += readLen;
            }
            return result;
        }
        return null;
    }

    public boolean isBig() {
        return this.paperSize == PrintBuilder.SIZE_BIG;
    }

    /**
     * 是否针式打印机
     *
     * @return boolean
     */
    public boolean isNeedle() {
        return this.paperType == PaperType.Impact || this.paperSize == PrintBuilder.SIZE_SMALL || this.paperSize == PrintBuilder.SIZE_NORMAL;
    }

    /**
     * 是否是非常小的尺寸
     *
     * @return boolean
     */
    public boolean isTooSmall() {
        return this.paperSize == PrintBuilder.SIZE_SMALL || this.paperSize == PrintBuilder.SIZE_MIN;
    }

    /**
     * 是否是Star的Printer
     *
     * @return boolean
     */
    public boolean starPrinter() {
        return COMMAND_STAR.equalsIgnoreCase(commandType);
    }

    public void setIsOldPrinter(boolean isOldPrinter) {
        if (isOldPrinter) {
            mIsOldPrinter = 1;
        } else {
            mIsOldPrinter = 0;
        }
    }

    public boolean isOldPrinter() {
        return mIsOldPrinter == 1;
    }

    public boolean supportStatusCheck() {
        return isSupportStatusCheck == 1;
    }

    public void setIsSupportStatusCheck(int isSupportStatusCheck) {
        this.isSupportStatusCheck = isSupportStatusCheck;
    }

    /**
     * 返回硬件指令转换器
     *
     * @return
     */
    public final ICommand getCommand() {
        return generateCommand();
    }

    /**
     * 构建硬件指令转换器
     *
     * @return
     */
    protected ICommand generateCommand() {
            CommandEsc commandEsc = new CommandEsc();
            return commandEsc;
    }

    public boolean getSamSungCutCommand() {
        return cutCommandSamSung == 1;
    }

    public void setSamSungCommand(boolean useOld) {
        this.cutCommandSamSung = useOld ? 1 : 0;
    }

    public int getPrinterModel() {
        return printerModel;
    }

    public void setPrinterModel(int printerModel) {
        this.printerModel = printerModel;
    }

    public int getControlType() {
        return controlType;
    }

    public void setControlType(int controlType) {
        this.controlType = controlType;
    }

    public String getReceiptModel() {
        return receiptModel;
    }

    public boolean isNeedImgDivide() {
        return isNeedImgDivide;
    }

    public void setNeedImgDivide(boolean needImgDivide) {
        isNeedImgDivide = needImgDivide;
    }

    /**
     * 设置优化模式指令
     */
    public void setReceiptModel(int model, int mode) {
    }

}
