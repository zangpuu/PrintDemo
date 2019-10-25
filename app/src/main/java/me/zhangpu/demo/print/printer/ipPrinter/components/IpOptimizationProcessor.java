package me.zhangpu.demo.print.printer.ipPrinter.components;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import me.zhangpu.demo.print.printer.PrintLog;
import me.zhangpu.demo.print.printer.ipPrinter.model.OptimizationTaskModel;
import me.zhangpu.demo.print.printer.ipPrinter.model.ReceiptCmdModel;
import me.zhangpu.demo.print.printer.ipPrinter.utils.IpPrinterConts;
import me.zhangpu.demo.print.processor.ByteProcessUtil;
import me.zhangpu.demo.print.processor.PrintResultCode;

/**
 * Created by lorin on 2018/7/2.
 * 网口优化处理类
 */

public class IpOptimizationProcessor {

    //Gsrn命令，用来监控内容是否打完
    private static final byte[] GSRN_CMD = {0x1d, 0x72, 0x01};
    private static final byte[] EPSON_Bill_CONTROL_CMD = {0x1d, 0x28, 0x48, 0x06, 0x00, 0x30, 0x30};

    private static final String LOG_TAG = "IpPrinterConfig";

    private static final byte BYTE_BILL_CONTROL_1 = 0x37;
    private static final byte BYTE_BILL_CONTROL_2 = 0x22;
    private static final byte BYTE_BILL_CONTROL_7 = 0x00;

    private static final int LENGTH_BYTE_BILL_CONTROL = 7;

    public static final byte[] REAL_TIME_DETECT = {0x10, 0x04, 0x02};


    /**
     * 得到是否可使用优化模式结果
     * 由于优化模式非常依赖打印机类型版本，所以并不作为一般设置对外开放
     *
     * @param model
     * @param mode
     * @return
     */
    public static String canUseOptimizationMode(int model, int mode) {

        String result = IpPrinterConts.RECEIPT_MODE_NO_MATCH;
        switch (model) {
            case IpPrinterConts.OPTIMIZATION_MODEL_GROUP_EPSON_TMU220B:
                if (mode == IpPrinterConts.OPTIMIZATION_MODE_FLOW_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_GSRN;
                }
                break;

            case IpPrinterConts.OPTIMIZATION_MODEL_GROUP_EPSON_T60_903_901:
                if (mode == IpPrinterConts.OPTIMIZATION_MODE_FLOW_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_GSRN;
                } else if (mode == IpPrinterConts.OPTIMIZATION_MODE_PACKAGE_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL;
                }
                break;

            case IpPrinterConts.OPTIMIZATION_MODEL_GROUP_GPRINTER_GP_L80250II:
                if (mode == IpPrinterConts.OPTIMIZATION_MODE_FLOW_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_GSRN;
                } else if (mode == IpPrinterConts.OPTIMIZATION_MODE_PACKAGE_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL;
                }
                break;
//            case IpPrinterConts.OPTIMIZATION_MODEL_GROUP_GPRINTER_GP_3150TIN:
//                if (mode == IpPrinterConts.OPTIMIZATION_MODE_FLOW_CONTROL) {
//                    result = IpPrinterConts.RECEIPT_MODE_GSRN;
//                }
//                break;
            case IpPrinterConts.OPTIMIZATION_MODEL_GROUP_GPRINTER_GP_L80250:
                if (mode == IpPrinterConts.OPTIMIZATION_MODE_FLOW_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_GSRN;
                } else if (mode == IpPrinterConts.OPTIMIZATION_MODE_PACKAGE_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL;
                }
                break;
            case IpPrinterConts.OPTIMIZATION_MODEL_GROUP_SPRINTER_SP_POS88VM:
                if (mode == IpPrinterConts.OPTIMIZATION_MODE_FLOW_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_GSRN;
                } else if (mode == IpPrinterConts.OPTIMIZATION_MODE_PACKAGE_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL;
                }
                break;
            case IpPrinterConts.OPTIMIZATION_MODEL_GROUP_SPRINTER_SP_POS88VBT_POS88V:
                if (mode == IpPrinterConts.OPTIMIZATION_MODE_FLOW_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_GSRN;
                } else if (mode == IpPrinterConts.OPTIMIZATION_MODE_PACKAGE_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL;
                }
                break;
            case IpPrinterConts.OPTIMIZATION_MODEL_GROUP_IPRT_T801M:
                if (mode == IpPrinterConts.OPTIMIZATION_MODE_FLOW_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_GSRN;
                } else if (mode == IpPrinterConts.OPTIMIZATION_MODE_PACKAGE_CONTROL) {
                    result = IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL;
                }
                break;

            default:
                break;
        }
        return result;

    }

    /**
     * 通过优化模式发送数据
     * 理论上进入此方法不会进入default，因为config创建之初receiptMode已经确定
     * 若没有则会按照常规逻辑运行，不会进入优化流程
     *
     * @param optimizationTaskModel
     * @param receiptMode
     * @return
     */
    public static int doOptimizationProcess(OptimizationTaskModel optimizationTaskModel, String receiptMode, int printerModel) {

        if (optimizationTaskModel == null || TextUtils.isEmpty(receiptMode)) {
            return PrintResultCode.PRINT_EXCEPTION;
        }

        if (IpPrinterConts.OPTIMIZATION_MODEL_GROUP_EPSON_TMU220B == printerModel
                || IpPrinterConts.OPTIMIZATION_MODEL_GROUP_EPSON_T60_903_901 == printerModel
                || IpPrinterConts.OPTIMIZATION_MODEL_GROUP_GPRINTER_GP_L80250II == printerModel) {
            boolean isRealTimeOK = detectRealTime(optimizationTaskModel.getOutputStream(),
                    optimizationTaskModel.getInputStream(),
                    optimizationTaskModel.getTaskUniq(),
                    optimizationTaskModel.getConfigUniq());

            if (!isRealTimeOK) {
                return PrintResultCode.PRINTER_PAPER_END_OR_UNCOVER;
            }
        }

        switch (receiptMode) {
            case IpPrinterConts.RECEIPT_MODE_GSRN:
                return doOptimization(optimizationTaskModel, receiptMode, 1000, 20 * 1000);
            case IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL:
                return doOptimization(optimizationTaskModel, receiptMode, 3000, 10 * 1000);
            default:
                break;
        }
        return PrintResultCode.PRINT_EXCEPTION;

    }

    /**
     * 实际优化方法，通过输入输出流来进行数据传输
     * 根据返回结果判断执行状态
     *
     * @param optimizationTaskModel
     * @param receiptMode
     * @param singleMaxLength
     * @param timeout
     * @return
     */
    private static int doOptimization(OptimizationTaskModel optimizationTaskModel, String receiptMode, int singleMaxLength, int timeout) {
        List<byte[]> cmds = ByteProcessUtil.processCmds(optimizationTaskModel.getData(), singleMaxLength);

        boolean isTransmitOK = true;
        int sendTotalCount = cmds.size();
        OutputStream outputStream = optimizationTaskModel.getOutputStream();
        InputStream inputStream = optimizationTaskModel.getInputStream();
        for (int i = 0; i < cmds.size(); i++) {

            if (outputStream != null) {
                ReceiptCmdModel currentReceiptCmd = buildReceiptCmd(receiptMode);
                try {

                    byte[] output = cmds.get(i);
                    byte[] readStatus = currentReceiptCmd.getWholeInstruction();
                    outputStream.write(output);
                    outputStream.write(readStatus);
                    outputStream.flush();

//                    if (output.length > singleMaxLength) {
//                        LogUtil.logOnlineDebug("[Model " + receiptMode + "] write byte = \n" + "HUGE DATA(MAYBE IMAGE) OMIT");
//                    } else {
                    //                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!readStream(currentReceiptCmd.getId(), receiptMode, inputStream, optimizationTaskModel.getTaskUniq(), sendTotalCount, i, optimizationTaskModel.getConfigUniq(), timeout)) {
                    isTransmitOK = false;
                    break;
                }
            }
        }

        if (isTransmitOK) {
            return PrintResultCode.SUCCESS;
        } else {
            return PrintResultCode.PRINTER_CHECK_TIME_OUT;
        }

    }

    /**
     * 构建优化指令
     * 根据不同模式构建需要的指令内容
     * （可优化）
     *
     * @param receiptMode
     * @return
     */
    private static ReceiptCmdModel buildReceiptCmd(String receiptMode) {
        ReceiptCmdModel receiptCmdModel = new ReceiptCmdModel();
        switch (receiptMode) {

            case IpPrinterConts.RECEIPT_MODE_GSRN:
                receiptCmdModel.setBasicInstruction(GSRN_CMD);
                receiptCmdModel.setWholeInstruction(GSRN_CMD);
                return receiptCmdModel;

            case IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL:
                byte[] wholeCmd = new byte[11];
                byte[] id = ByteProcessUtil.createRandomId();
                System.arraycopy(EPSON_Bill_CONTROL_CMD, 0, wholeCmd, 0, EPSON_Bill_CONTROL_CMD.length);
                System.arraycopy(id, 0, wholeCmd, EPSON_Bill_CONTROL_CMD.length, 4);
                receiptCmdModel.setBasicInstruction(EPSON_Bill_CONTROL_CMD);
                receiptCmdModel.setWholeInstruction(wholeCmd);
                receiptCmdModel.setId(id);

                return receiptCmdModel;
            default:
                return null;
        }

    }

    /**
     * 读取流数据，根据返回判断执行结果
     * （可优化，传参太多）
     *
     * @param id
     * @param receiptMode
     * @param inStream
     * @param taskUniq
     * @param totalSendCount
     * @param currentCount
     * @param configUniq
     * @param timeout
     * @return
     */
    public static boolean readStream(byte[] id, String receiptMode, InputStream inStream, String taskUniq, int totalSendCount, int currentCount, String configUniq, int timeout) {

        long startTimeStamp = System.currentTimeMillis();

        ByteBuffer checkBuffer = ByteBuffer.allocate(64);
        byte[] receiptByte = null;

        while (true) {

            if ((System.currentTimeMillis() - startTimeStamp) > timeout) {
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                PrintLog.e(LOG_TAG, "", e);
            }

            byte[] resultBytes = getReceiptResult(inStream);

            if (resultBytes == null || resultBytes.length == 0) {
                continue;
            }

            checkBuffer.put(resultBytes);

            int contentLength = checkBuffer.position();
            byte[] validData = new byte[contentLength];
            System.arraycopy(checkBuffer.array(), 0, validData, 0, contentLength);

            byte[] checkResult = checkReceiptMessageValid(validData, receiptMode);

            if (checkResult != null) {
                receiptByte = checkResult;
                break;
            }

        }

        if (receiptByte == null) {
            recordReceiptResult(taskUniq, "NOT RECEIVED", totalSendCount, currentCount, configUniq);
            PrintLog.d(LOG_TAG, "Receipt:NOT RECEIVED");
            return false;
        }

        if (receiptByte != null) {

            boolean isMessageVaild = buildReceiptProcessResult(id, receiptByte, receiptMode);
            StringBuilder resultInfo = new StringBuilder();
            resultInfo.append("Result Message:[" + ByteProcessUtil.bytes2HexString(receiptByte) + "]");
            if (id != null && id.length > 0) {
                resultInfo.append(" PackageID:[" + ByteProcessUtil.bytes2HexString(id) + "]");
            }
            recordReceiptResult(resultInfo.toString(), taskUniq, "" + isMessageVaild, totalSendCount, currentCount, configUniq);
            PrintLog.d(LOG_TAG, "Receipt:" + ByteProcessUtil.bytes2HexString(receiptByte));
            return isMessageVaild;
        } else {
            recordReceiptResult(taskUniq, "READ FAIL", totalSendCount, currentCount, configUniq);
            PrintLog.d(LOG_TAG, "Receipt:READ FAIL");
            return false;
        }

    }

    /**
     * 构建回执结果
     * 感觉不同优化方案的结果返回判断结果
     *
     * @param idBytes
     * @param receipt
     * @param receiptMode
     * @return
     */
    private static boolean buildReceiptProcessResult(byte[] idBytes, byte[] receipt, String receiptMode) {
        switch (receiptMode) {

            case IpPrinterConts.RECEIPT_MODE_GSRN:
                if (receipt != null && receipt.length == 1) {
                    return ((receipt[0] & 0x90) == 0 && (receipt[0] & 0x0c) == 0);
                }
            case IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL:


                if (idBytes != null && idBytes.length == 4 && receipt != null && receipt.length == 4) {
                    if (ByteProcessUtil.isEpsonReceiptVaild(idBytes, receipt))
                        return true;
                }
                return false;
            default:
                return false;
        }

    }

    /**
     * 打印回执记录
     *
     * @param taskUniq
     * @param status
     * @param totalCount
     * @param currentCount
     * @param configUniq
     */
    private static void recordReceiptResult(String taskUniq, String status, int totalCount, int currentCount, String configUniq) {
        recordReceiptResult("", taskUniq, status, totalCount, currentCount, configUniq);
    }

    /**
     * 打印回执记录
     *
     * @param resultInfo
     * @param taskUniq
     * @param status
     * @param totalCount
     * @param currentCount
     * @param configUniq
     */
    private static void recordReceiptResult(String resultInfo, String taskUniq, String status, int totalCount, int currentCount, String configUniq) {
        StringBuilder sb = new StringBuilder();
        sb.append("网口回执监控：Printer[" + configUniq + "]");
        if (!TextUtils.isEmpty(resultInfo)) {
            sb.append(resultInfo);
        }
        sb.append(",taskUniq[").append(taskUniq).append("]");
        sb.append("\n");
        sb.append("receipt status [" + status + "]," + "total count [" + totalCount + "]" + "current count [" + (currentCount + 1) + "]");
    }

    /**
     * 串口查找中需要的握手返回检测
     *
     * @param inStream
     * @return
     */
    private static byte[] getReceiptResult(InputStream inStream) {

        int count = 0;
        byte[] result = new byte[0];
        if (inStream != null) {
            try {
                count = inStream.available();
            } catch (IOException e) {
                PrintLog.e(LOG_TAG, "", e);
            }
            result = new byte[count];
            if (count > 0) {
                try {
                    inStream.read(result);
                } catch (IOException e) {
                    PrintLog.e(LOG_TAG, "", e);
                }
            }
        }

        return result;
    }

    /**
     * 判断返回数据对应不同模式的合法性
     *
     * @param source
     * @return
     */
    private static byte[] checkReceiptMessageValid(byte[] source, String receiptMode) {

        switch (receiptMode) {
            case IpPrinterConts.RECEIPT_MODE_GSRN:
                if (source.length < 0) {
                    return null;
                }
                return source;

            case IpPrinterConts.RECEIPT_MODE_COMMON_PACKAGE_CONTROL:
                if (source.length < LENGTH_BYTE_BILL_CONTROL) {
                    return null;
                }

                for (int i = 0; i < (source.length + 1 - LENGTH_BYTE_BILL_CONTROL); i++) {

                    if (source[i] == BYTE_BILL_CONTROL_1
                            && source[i + 1] == BYTE_BILL_CONTROL_2
                            && source[i + LENGTH_BYTE_BILL_CONTROL - 1] == BYTE_BILL_CONTROL_7) {
                        byte[] receiptId = {source[i + 2], source[i + 3], source[i + 4], source[i + 5]};
                        return receiptId;
                    }
                }
                break;
            default:
                break;
        }

        return null;
    }

    /**
     * 判断返回数据对应不同模式的合法性
     *
     * @param source
     * @return
     */
    private static boolean checkDLEReceiptValid(byte[] source) {

        for (int i = 0; i < source.length; i++) {
            if ((source[0] & 0x93) == 0x12) {
                return true;
            }
        }
        return false;
    }

    /**
     * 实时检测状态
     *
     * @param outputStream
     * @param inputStream
     * @return
     */
    private static boolean detectRealTime(OutputStream outputStream, InputStream inputStream, String taskUniq, String configUniq) {

        if (outputStream != null) {
            try {
                outputStream.write(REAL_TIME_DETECT);
                outputStream.flush();
            } catch (IOException e) {
                PrintLog.e(LOG_TAG, "", e);
                return false;
            }
        }

        return checkRealTime(inputStream, 2 * 1000, taskUniq, configUniq);

    }

    /**
     * 监听处理实时消息回复
     *
     * @param inputStream
     * @param timeout
     * @param taskUniq
     * @param configUniq
     * @return
     */
    public static boolean checkRealTime(InputStream inputStream, int timeout, String taskUniq, String configUniq) {
        int count = 0;
        boolean isCheckOK = false;
        long startTimeStamp = System.currentTimeMillis();
        ByteBuffer checkBuffer = ByteBuffer.allocate(64);

        while (true) {

            if ((System.currentTimeMillis() - startTimeStamp) > timeout) {
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                PrintLog.e(LOG_TAG, "", e);
            }

            byte[] resultBytes = getReceiptResult(inputStream);

            if (resultBytes == null || resultBytes.length == 0) {
                continue;
            }

            checkBuffer.put(resultBytes);

            int contentLength = checkBuffer.position();
            byte[] validData = new byte[contentLength];
            System.arraycopy(checkBuffer.array(), 0, validData, 0, contentLength);

            isCheckOK = checkDLEReceiptValid(validData);

            if (isCheckOK) {
                break;
            }

        }

        StringBuilder resultInfo = new StringBuilder();
        resultInfo.append("Check Result Message:[" + isCheckOK + "]");

        if (!isCheckOK) {
            recordCheckResult(resultInfo.toString(), taskUniq, "false", configUniq);
            PrintLog.d(LOG_TAG, "Receipt:CHECK CONNECT FAIL");
            return false;
        } else {

            recordCheckResult(resultInfo.toString(), taskUniq, "true", configUniq);
            PrintLog.d(LOG_TAG, "Receipt:CHECK CONNECT SUCCESS");
            return true;

        }

//        StringBuilder resultInfo = new StringBuilder();
//        resultInfo.append("Check Result Message:[" + isCheckOK + "]");
//
//        if (receipt != null) {
//
//            if (receipt != null && receipt.length == 1) {
//
//                if ((receipt[0] & 0x93) == 0x12) {
//                    // TODO: 2018/7/17 开盖打印细化
//                    recordCheckResult(resultInfo.toString(), taskUniq, "true", configUniq);
//                    PrintLog.d(LOG_TAG, "Receipt:" + ByteProcessUtil.bytes2HexString(receipt));
//                    return true;
//                }
//
//            }
//        }
//
//        recordCheckResult(resultInfo.toString(), taskUniq, "false", configUniq);
//        PrintLog.d(LOG_TAG, "Receipt:" + ByteProcessUtil.bytes2HexString(receipt));
    }

    private static void recordCheckResult(String resultInfo, String taskUniq, String status, String configUniq) {
        StringBuilder sb = new StringBuilder();
        sb.append("网口实时检测：Printer[" + configUniq + "]");
        if (!TextUtils.isEmpty(resultInfo)) {
            sb.append(resultInfo);
        }
        sb.append(",taskUniq[").append(taskUniq).append("]");
        sb.append("\n");
        sb.append("receipt status [" + status + "]");
    }

}
