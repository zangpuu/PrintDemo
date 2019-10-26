package me.zhangpu.demo.print.processor;

public class PrintResultCode {
    /**
     * 打印成功
     */
    public final static int SUCCESS = 0;
    /**
     * 没有这事打印数据
     */
    public final static int NO_DATA = 1;
    /**
     * 打印机配置不合法
     */
    public final static int CONFIG_ERROR = 2;
    /**
     * 打印机连接失败
     */
    public final static int PRINTER_CONNECT_FAIL = 3;
    /**
     * 打印出现异常
     */
    public final static int PRINT_EXCEPTION = 4;

    /**
     * 纸将尽
     */
    public final static int PRINT_PAPER_NEARLY_OUT = 5;
    /**
     * 纸已经用完了
     */
    public final static int PRINT_PAPER_OUT = 6;

    /**
     * 打印中(指令发送打印机，发送打印机成功)
     * {@link com.mwee.android.print.printer.cloud.CloudPrintResult#SENDED}
     */
    public final static int PRINTED = 7;
    /**
     * 打印机状态检测时，超时
     */
    public final static int PRINTER_CHECK_TIME_OUT = 8;

    /**
     * 打印机开盖
     */
    public final static int PRINTER_UNCOVER = 9;

    /**
     * 打印机不在线(云打印)
     */
    public final static int PRINTER_NOT_CONNECTED = 10;
    /**
     * 打印机开盖或缺纸
     */
    public final static int PRINTER_PAPER_END_OR_UNCOVER = 11;
}
