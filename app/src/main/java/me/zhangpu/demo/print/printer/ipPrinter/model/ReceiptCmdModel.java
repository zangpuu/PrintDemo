package me.zhangpu.demo.print.printer.ipPrinter.model;

/**
 * Created by lorin on 2018/7/4.
 * 回执命令对象
 */

public class ReceiptCmdModel {

    private byte[] basicInstruction;

    private byte[] id;

    private byte[] wholeInstruction;

    public byte[] getBasicInstruction() {
        return basicInstruction;
    }

    public void setBasicInstruction(byte[] basicInstruction) {
        this.basicInstruction = basicInstruction;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getWholeInstruction() {
        return wholeInstruction;
    }

    public void setWholeInstruction(byte[] wholeInstruction) {
        this.wholeInstruction = wholeInstruction;
    }
}
