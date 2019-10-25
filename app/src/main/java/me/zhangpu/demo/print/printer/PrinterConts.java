package me.zhangpu.demo.print.printer;

/**
 * Created by lorin on 2018/6/27.
 */

public class PrinterConts {
    //优化模式
    //默认
    public static final int OPTIMIZATION_MODE_NONE = 0;
    //流控
    public static final int OPTIMIZATION_MODE_FLOW_CONTROL = 1;
    //一票一控
    public static final int OPTIMIZATION_MODE_PACKAGE_CONTROL = 2;

    public static final String RECEIPT_MODE_NO_MATCH = "NoMatch";

    public static final String RECEIPT_MODE_GSRN = "Gsrn";

    public static final String RECEIPT_MODE_COMMON_PACKAGE_CONTROL = "CommonPackageControl";

}
