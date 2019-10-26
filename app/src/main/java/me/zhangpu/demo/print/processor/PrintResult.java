package me.zhangpu.demo.print.processor;

public class PrintResult {
    /**
     * 打印结果
     */
    public int result = PrintResultCode.SUCCESS;
    /**
     * 打印异常的文案
     */
    public String errorMsg = "";
    /**
     * 打印失败的trace
     */
    public Throwable trace = null;
    public int totalBytes=0;
    public boolean needExtraWait=true;
    public PrintResult() {
    }

    public void buildMsg() {
        switch (result) {
            case PrintResultCode.SUCCESS:
                break;
            case PrintResultCode.PRINTER_CONNECT_FAIL:
                errorMsg = "打印机连接失败";
                break;
            case PrintResultCode.CONFIG_ERROR:
                errorMsg = "打印机配置连接失败";
                break;
            case PrintResultCode.PRINT_EXCEPTION:
                errorMsg = "打印机异常";
                break;
            case PrintResultCode.NO_DATA:
                errorMsg = "没有数据需要打印";
                break;
            case PrintResultCode.PRINTED:
                errorMsg = "打印中";
                break;

            case PrintResultCode.PRINT_PAPER_OUT:
                errorMsg = "打印机缺纸";
                break;

            case PrintResultCode.PRINTER_NOT_CONNECTED:
                errorMsg = "打印机不在线";
                break;

            case PrintResultCode.PRINTER_UNCOVER:
                errorMsg = "打印机开盖";
                break;

            default:
                break;
        }
    }

    public void addTrace(Throwable e) {
        trace = e;
    }
}
