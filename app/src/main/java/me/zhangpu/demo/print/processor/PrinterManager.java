package me.zhangpu.demo.print.processor;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.zhangpu.demo.print.base.PrinterConfig;
import me.zhangpu.demo.print.image.ImageProcess;
import me.zhangpu.demo.print.printer.PrintLog;
import me.zhangpu.demo.print.processor.command.CommandEsc;
import me.zhangpu.demo.print.processor.command.ICommand;


/**
 * 打印指令输出管理类
 *
 * @author virgil
 */
public class PrinterManager {
    private final static String TAG = "PrintSDK";
    private final PrinterConfig config;
    /**
     * 是否是TSC指令集
     */
    private boolean isTsc;
    /**
     * 打印数据集合
     */
    private List<PrintDataItem> data;

    private byte[] bytesData;

    /**
     * 构造方法
     *
     * @param config     PrinterConfig | 打印机的配置项
     * @param isGprinter boolean | 是否是指令
     */
    public PrinterManager(PrinterConfig config, boolean isGprinter) {
        this.config = config;
        this.isTsc = isGprinter;
        this.data = new ArrayList<>();

    }

    public PrintResult start() {
        return start("");
    }


    public PrintResult start(String uniq) {
        return start(uniq, false);
    }

    /**
     * 开始执行打印
     *
     * @param uniq 小票唯一标识
     */
    public PrintResult start(String uniq, boolean isPrintBytes) {
        PrintResult result = new PrintResult();
        List<String> log = new ArrayList<>();
        log.add(System.currentTimeMillis() + " receive task");
        String printerUniq = "null";
        try {//开始做一些异常判断

            if (isPrintBytes) {
                if (this.bytesData == null || this.bytesData.length <= 0) {
                    result.result = PrintResultCode.NO_DATA;
                    result.buildMsg();
                    return result;
                }
            } else {
                if (this.data == null || this.data.isEmpty()) {
                    result.result = PrintResultCode.NO_DATA;
                    result.buildMsg();
                    return result;
                }
            }

            if (config == null) {
                result.result = PrintResultCode.PRINTER_CONNECT_FAIL;
                result.buildMsg();
                return result;
            }
            printerUniq = config.getUniq();
            PrintLog.i("PrinterManager", "准备锁");

            synchronized (config) {
                PrintLog.i("PrinterManager", " 进入锁");
                //链接打印机
                try {
                    PrintLog.i("PrinterManager", " 开始链接");
                    this.config.connect();
                } catch (Throwable e) {
                    //重试一次，如果失败，则返回
                    PrintLog.i("PrinterManager ", "链接失败，开始重连");

                    e.printStackTrace();
                    try {
                        this.config.connect();
                        log.add(System.currentTimeMillis() + " connect failed,but succeed with retry");

                    } catch (Throwable e1) {
                        log.add(System.currentTimeMillis() + " connect failed with retry");

                        result.result = PrintResultCode.PRINTER_CONNECT_FAIL;
                        result.buildMsg();
                        this.config.closeConnect();
                        e.printStackTrace();
                        result.addTrace(e1);
                        return result;
                    }
                }
                //发送打印
                log.add(System.currentTimeMillis() + " connect success,start print to ");

                if (isPrintBytes) {
                    doPrintBytes(result, uniq, log);
                } else {
                    doPrint(result, uniq, log);
                }

//                result.result =  doPrint(result,uniq, log);

                this.config.closeConnect();
                //关闭链接
                log.add(System.currentTimeMillis() + " print success,close connect");

                //如果成功并且没有流控，休息一会儿
                if (result.result == PrintResultCode.SUCCESS && (!config.isUseReceipt)) {
                    long sleepTime = 1000;
                    if (result.needExtraWait) {
                        if (config.isOldPrinter()) {
                            sleepTime = 2000;
                        } else {
                            //如果是针式打印
                            if (config.isNeedle()) {
                                //根据 1KB 10秒钟换算出来
                                sleepTime = result.totalBytes * 12;
                                if (sleepTime < 1000) {
                                    sleepTime = 1000;
                                }
                            } else {
                                sleepTime = 1000;
                            }
                        }
                    } else {
                        sleepTime = 500;
                    }
                    try {
                        PrintLog.i("PrinterManager", " 结束等待");
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        PrintLog.e("", "", e);
                    }
                    //关闭链接
                    log.add(System.currentTimeMillis() + " finish Sleep");
                }
            }
        } catch (Throwable e) {
            result.result = PrintResultCode.PRINT_EXCEPTION;
            result.addTrace(e);
        } finally {
            StringBuilder sb = new StringBuilder("小票打印流程：Printer[").append(printerUniq).append("],taskUniq[").append(uniq).append("]").append("\n");
            if (log.size() > 0) {
                for (String temp : log) {
                    sb.append(temp).append("\n");
                }
            }
            try {
                if (this.config != null) {
                    this.config.closeConnect();
                }
            } catch (Throwable e) {
                PrintLog.e("", "", e);
            }
        }

        result.buildMsg();

        return result;
    }

    private void doPrintBytes(PrintResult totalResult, String uniq, List<String> log) throws Exception {
        ICommand tempCommand = config.getCommand();
        tempCommand.setCmdBytesData(bytesData);

        totalResult.totalBytes = tempCommand.getTotalLength();
        log.add(System.currentTimeMillis() + " start write command ");
        totalResult.result = tempCommand.startBytesWrite(config, uniq);
        log.add(System.currentTimeMillis() + " write finish,receive result [" + totalResult.result + "]");

        //检测打印之后，的状态，目前不生效
        if (config.supportStatusCheck()) {
            checkResultStatus(uniq, log);
        }
    }

    private void doPrint(PrintResult totalResult, String uniq, List<String> log) throws Exception {
        ICommand tempCommand = config.getCommand();

        if (isTsc) {
            //TSC指令的打印机，单独处理
        } else {

            //检测打印机的状态，目前不生效
            if (config.supportStatusCheck()) {
                int result = checkStatus(uniq, log);
                //暂时不做处理
//                if (result != PrintResultCode.SUCCESS) {
//                    return result;
//                }
            }

            //开始组建指令
            tempCommand.reset();
            //商米T1不支持这个指令
//            tempCommand.setEncodingType((byte) 2);
            tempCommand.setLanguage();
            tempCommand.setChineseEncodingMode();
            if (config.isNeedle()) {
                tempCommand.setFont((byte) 0);
            }
            tempCommand.reSetLine();
            for (int i = 0; i < this.data.size(); i++) {

                PrintDataItem toPrint = this.data.get(i);
                byte isUnderline = CommandEsc.UNDERLINE_NONE;
                if (toPrint.underline) {
                    isUnderline = CommandEsc.UNDERLINE_SINGLE;
                }
                if (toPrint.textBold != -1) {
                    tempCommand.setBold((byte) 1);
                } else {
                    tempCommand.setBold((byte) 0);
                }
                if (config.isNeedle()) {
                    if (toPrint.fontsize == -1) {
                        tempCommand.setFontSizeFor76(1);
                    } else {
                        tempCommand.setFontSizeFor76(toPrint.fontsize);
                    }
                } else {
                    //佳博打印机需设置行间距
                    if(tempCommand instanceof CommandEsc ){
                        tempCommand.setLine(toPrint.getLineSpace());
                    }
                    if (toPrint.fontsize == -1) {
                        tempCommand.setFontSize(1);
                    } else {
//                        tempCommand.setFontSize(toPrint.fontsize);

                        if (tempCommand instanceof CommandEsc) {
                            ((CommandEsc) tempCommand).setFontSize(toPrint.fontsize, toPrint.stretchType);
                        } else {
                            tempCommand.setFontSize(toPrint.fontsize);
                        }
                    }
                }
                switch (toPrint.textAlign) {
                    case PrintDataItem.ALIGN_RIGHT:
                        tempCommand.setAlign((byte) toPrint.textAlign);
                        break;
                    case PrintDataItem.ALIGN_CENTRE:
                        tempCommand.setAlign((byte) toPrint.textAlign);
                        break;
                    default:
                        tempCommand.setAlign((byte) 0);
                        break;
                }

                switch (toPrint.dataFormat) {
                    case PrintDataItem.FORMAT_FEED:
                        if (toPrint.marginTop > 0) {
                            if (config.isNeedle()) {
                                tempCommand.feed((byte) (toPrint.marginTop));
                            } else {
                                tempCommand.feed((byte) (toPrint.marginTop * 20));
                            }
                        }
                        break;
                    case PrintDataItem.FORMAT_TXT:
                        if (toPrint.marginTop > 0) {
                            if (config.isNeedle()) {
                                tempCommand.feed((byte) (toPrint.marginTop));
                            } else {
                                tempCommand.feed((byte) (toPrint.marginTop * 20));
                            }
                        }
                        if (config.isNeedle()) {
                            tempCommand.setFontColor((byte) toPrint.fontColor);
                        }
                        tempCommand.printText(toPrint.text, config.lang);
                        break;
                    case PrintDataItem.FORMAT_IMG: {
                        int maxWidth = 140;
                        if (config.isNeedle()) {
                            continue;
                        }
                        try {
                            Bitmap bitmap = toPrint.bitmap;
                            if (bitmap == null) {
                                continue;
                            }
                            bitmap = ImageProcess.toFixedSizeAndGrayscale(bitmap, maxWidth);
                            tempCommand.printBitmap(bitmap);
                        } catch (Exception e) {
                        }
                        break;
                    }
                    case PrintDataItem.FORMAT_BARCODE:
                        if (config.isNeedle()) {
                            continue;
                        }
                        tempCommand.printBarcode(toPrint.text, config.isBig());
                        break;
                    case PrintDataItem.FORMAT_CUT:
                        if (config.getSamSungCutCommand()) {
                            tempCommand.cut();
                        } else {
                            //切纸前，走一下纸
                            if (config.isNeedle()) {
                                tempCommand.feed((byte) (3 * 20));
                            } else {
                                tempCommand.feed((byte) (6 * 20));
                            }
                            //切纸前，走一下纸
                            if (config.isNeedle()) {
                                tempCommand.feed((byte) (3 * 30));
                            } else {
                                tempCommand.feed((byte) (5 * 20));
                            }
                            tempCommand.cutEpson();
                        }
                        break;
                    case PrintDataItem.FORMAT_BEEP:
                        if (config.isNeedle()) {
                            continue;
                        }
                        tempCommand.beep();
                        break;
                    case PrintDataItem.FORMAT_QR: {
                        int imageSize = 210;
                        if (config.isNeedle()) {
                            continue;
                        }
                        Bitmap bitmap = BarcodProcessor.createQRImage(toPrint.text, imageSize, imageSize);
                        if (bitmap != null) {
                            bitmap = BitmapUtil.toGrayscale(bitmap);
                            tempCommand.printQRBitmap(bitmap, toPrint.fontsize);
                        }
                        break;
                    }
                    case PrintDataItem.FORMAT_DRAWER:
                        break;
                    default:
                        break;

                }
            }
//            tempCommand.reset();

        }
        totalResult.totalBytes = tempCommand.getTotalLength();
        log.add(System.currentTimeMillis() + " start write command ");
        totalResult.result = tempCommand.startWrite(config, uniq);
        log.add(System.currentTimeMillis() + " write finish,receive result [" + totalResult.result + "]");

        //检测打印之后，的状态，目前不生效
        if (config.supportStatusCheck()) {
            checkResultStatus(uniq, log);
        }
    }

    private int checkStatus(String uniq, List<String> log) throws Exception {
        //重置缓冲区
        byte[] tcmd = new byte[2];
        tcmd[0] = ICommand.ESC;
        tcmd[1] = 0x40;
        this.config.write(tcmd);

        //发送打印机状态的实时回调
        tcmd = new byte[3];
        tcmd[0] = 0x10;
        tcmd[1] = 0x04;
        tcmd[2] = 0x04;
        this.config.write(tcmd);
        byte[] result = null;
        try {
            result = this.config.read(7);
            log.add(System.currentTimeMillis() + " result=" + PrintLog.i("PrinterStatus", result));
        } catch (IOException e) {
            PrintLog.e("PrinterStatus", uniq, (e));
            return PrintResultCode.PRINTER_CHECK_TIME_OUT;

        }
        if (result != null && result.length > 0) {
            int value = result[0];
            if ((value & 12) == 12) {
                //纸将用完
//                return PrintResultCode.PRINT_PAPER_NEARLY_OUT;

            }
            if ((value & 96) == 96) {
                //纸将用完
//                return PrintResultCode.PRINT_PAPER_OUT;
            }
        } else {
            return PrintResultCode.PRINTER_CHECK_TIME_OUT;
        }
        return PrintResultCode.SUCCESS;
    }

    private int checkResultStatus(String uniq, List<String> log) throws Exception {
        //重置缓冲区
        byte[] tcmd = new byte[2];
        tcmd[0] = ICommand.ESC;
        tcmd[1] = 0x40;
        this.config.write(tcmd);

        //发送打印机状态的实时回调
        tcmd = new byte[3];
        tcmd[0] = 0x10;
        tcmd[1] = 0x04;
        tcmd[2] = 0x03;
        this.config.write(tcmd);
        byte[] result = null;
        try {
            result = this.config.read(7);
            log.add(System.currentTimeMillis() + " checkResultStatus  result=" + PrintLog.i("PrinterStatus", result));
        } catch (IOException e) {
            PrintLog.e("PrinterStatus", "", e);
            return PrintResultCode.PRINTER_CHECK_TIME_OUT;

        }
        if (result != null && result.length > 0) {
            int value = result[0];
            if ((value & 12) == 12) {
                //纸将用完
//                return PrintResultCode.PRINT_PAPER_NEARLY_OUT;

            }
            if ((value & 96) == 96) {
                //纸将用完
//                return PrintResultCode.PRINT_PAPER_OUT;
            }
        } else {
            return PrintResultCode.PRINTER_CHECK_TIME_OUT;

        }
        return PrintResultCode.SUCCESS;
    }

    public List<PrintDataItem> getData() {
        return data;
    }

    public void setData(List<PrintDataItem> data) {
        this.data = data;
    }

    public void setBytesData(byte[] bytesData) {
        this.bytesData = bytesData;
    }
}
