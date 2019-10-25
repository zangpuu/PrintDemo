package me.zhangpu.demo.print.printer.ipPrinter.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by lorin on 2018/7/2.
 * 优化任务数据对象
 */

public class OptimizationTaskModel {

    private List<byte[]> data;
    private String taskUniq;
//    private int optimizationMode;
//    private int processType;
    private String configUniq;
    private InputStream inputStream;
    private OutputStream outputStream;

    public List<byte[]> getData() {
        return data;
    }

    public void setData(List<byte[]> data) {
        this.data = data;
    }

    public String getTaskUniq() {
        return taskUniq;
    }

    public void setTaskUniq(String taskUniq) {
        this.taskUniq = taskUniq;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public String getConfigUniq() {
        return configUniq;
    }

    public void setConfigUniq(String configUniq) {
        this.configUniq = configUniq;
    }
}
