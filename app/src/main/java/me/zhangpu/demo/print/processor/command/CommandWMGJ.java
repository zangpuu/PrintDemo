package me.zhangpu.demo.print.processor.command;

import me.zhangpu.demo.print.base.PrinterConfig;

/**
 * 外卖管家指令集
 */
public class CommandWMGJ extends AbsStringCommand {
    /**
     * @param fontMultiple 正常字体的放大倍数，fontMultiple用于标识8个字体大小列表的下标
     *                     其中，fontMultiple=1表示默认大小
     * @throws Exception
     */
    @Override
    public void setFontSize(int fontMultiple) throws Exception {
//        指令集下对应的字体列表：{0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};，原理解析：0x[纵向放大倍数][横向放大倍数]
        //http://doc.open.xiaowm.com/400979
        String cmd = null;
        switch (fontMultiple) {
            case 1: //默认字体
                cmd = "|5";
                break;
            case 2:
                cmd = "|7";
                break;
            default:
                cmd = "|5";
                break;
        }
        if (cmd != null) {
            this.cmdString.append(cmd);
        }
    }

    @Override
    public void printText(String data, int lang) {
        if (data != null && data.length() > 0) {
            this.cmdString.append(data);
        }
    }

    @Override
    public int startWrite(PrinterConfig printer, String uniq) throws Exception {
        return -1;
    }

    @Override
    public int getTotalLength() {
        return this.cmdString.length();
    }

    @Override
    public int startBytesWrite(PrinterConfig printer, String uniq) throws Exception {
        return 0;
    }
}
