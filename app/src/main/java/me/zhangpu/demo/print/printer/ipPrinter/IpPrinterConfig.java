package me.zhangpu.demo.print.printer.ipPrinter;


import android.os.SystemClock;
import android.text.TextUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import me.zhangpu.demo.print.base.PrinterConfig;
import me.zhangpu.demo.print.printer.PrintLog;
import me.zhangpu.demo.print.printer.ipPrinter.components.IpOptimizationProcessor;
import me.zhangpu.demo.print.printer.ipPrinter.model.OptimizationTaskModel;
import me.zhangpu.demo.print.printer.ipPrinter.utils.IpPrinterConts;

public class IpPrinterConfig extends PrinterConfig {

    public String ip = "";
    public int port = 0;
    private Socket socket;

    public IpPrinterConfig() {
    }

    @Override
    public String getUniq() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append("|").append(ip).append(":").append(port);
        return sb.toString();
    }

    @Override
    public void connect() throws Exception {

        socket = null;
        outputStream = null;

        if (TextUtils.isEmpty(ip) || port <= 0) {
            throw new NullPointerException("ip or port is illegal");
        }
        socket = new Socket();
//        if (socket.getSendBufferSize() > 1024) {
//            socket.setSendBufferSize(1024);
//        }
        socket.connect(new InetSocketAddress(ip, port), timeOut * 1000);
        socket.setSoTimeout(3000);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
    }

    @Override
    public void closeConnect() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            outputStream = null;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            socket = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ipConfig优化处理方法，此config独有
     * （可优化）
     *
     * @param data
     * @param taskUniq
     * @return
     * @throws Exception
     */
    public int processWithOptimizationMode(List<byte[]> data, String taskUniq) throws Exception {

        OptimizationTaskModel optimizationTaskModel = new OptimizationTaskModel();

        optimizationTaskModel.setData(data);
        optimizationTaskModel.setInputStream(inputStream);
        optimizationTaskModel.setOutputStream(getOutputStream());
        optimizationTaskModel.setTaskUniq(taskUniq);
        optimizationTaskModel.setConfigUniq(getUniq());

        return IpOptimizationProcessor.doOptimizationProcess(optimizationTaskModel, receiptModel, printerModel);

    }

    @Override
    public int write(byte[] data) throws Exception {
        int writeLength = data.length;
        if (outputStream != null) {
            long start = SystemClock.elapsedRealtime();
            outputStream.write(data);

            outputStream.flush();
            long writeCost = SystemClock.elapsedRealtime() - start;
            if (writeCost > 10) {
                PrintLog.i("CommandEsc", "NetPrinter steam write cost=" + writeCost);
            }

        }

//        if (inputStream != null) {
//            int length = inputStream.read();
//            LogUtil.log("NetPrinter read=" + length);
//        }
        return writeLength;
    }

//    public String getReceiptModel() {
//        return receiptModel;
//    }

    /**
     * 设置优化模式
     *
     * @param model
     * @param processType
     */
    @Override
    public void setReceiptModel(int model, int processType) {

        printerModel = model;
        controlType = processType;

        receiptModel = IpOptimizationProcessor.canUseOptimizationMode(model, processType);
        if (!IpPrinterConts.RECEIPT_MODE_NO_MATCH.equals(receiptModel)) {
            isUseReceipt = true;
        }
    }


}
