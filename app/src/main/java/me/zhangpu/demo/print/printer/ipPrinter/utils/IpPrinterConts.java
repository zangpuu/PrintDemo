package me.zhangpu.demo.print.printer.ipPrinter.utils;


import me.zhangpu.demo.print.printer.PrinterConts;

public class IpPrinterConts extends PrinterConts {

    //优化方案集合
    //仅支持Gsrn的爱普生流控方案，无一票一控
    //典型为TMU220B
    public static final int OPTIMIZATION_MODEL_GROUP_EPSON_TMU220B = 1001;
    //爱普生T60-901&903
    public static final int OPTIMIZATION_MODEL_GROUP_EPSON_T60_903_901 = 1002;
    //佳博GP-L80250II
    public static final int OPTIMIZATION_MODEL_GROUP_GPRINTER_GP_L80250II = 2001;
//    //佳博GP3150TIN
//    public static final int OPTIMIZATION_MODEL_GROUP_GPRINTER_GP_3150TIN = 2002;
    //佳博L80250
    public static final int OPTIMIZATION_MODEL_GROUP_GPRINTER_GP_L80250 = 2003;
    //思普瑞特SP-POS88VM(网+U+串
    public static final int OPTIMIZATION_MODEL_GROUP_SPRINTER_SP_POS88VM = 3001;
    //思普瑞特SP-POS88VBT(U+蓝牙)&思普瑞特SP-POS88V(U+串)
    public static final int OPTIMIZATION_MODEL_GROUP_SPRINTER_SP_POS88VBT_POS88V = 3002;
    //爱印T801M
    public static final int OPTIMIZATION_MODEL_GROUP_IPRT_T801M = 4001;
}
