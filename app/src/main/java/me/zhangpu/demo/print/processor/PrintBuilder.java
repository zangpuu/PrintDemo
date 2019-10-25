package me.zhangpu.demo.print.processor;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.zhangpu.demo.print.base.PrinterConfig;
import me.zhangpu.demo.print.printer.PrintLog;

@SuppressWarnings("unused")
public class PrintBuilder {

    /**
     * 80mm打印机
     */
    public final static int SIZE_BIG = 48;
    /**
     * 76mm打印机 CPL是42/35
     */
    public final static int SIZE_NORMAL = 35;
    /**
     * 76mm打印机 CPL40/33
     */
    public final static int SIZE_SMALL = 33;
    /**
     * 58mm点击
     */
    public final static int SIZE_MIN = 32;

    /**
     * 大图片模式
     */
    public final static int SHOW_IMG_BIG = 2;
    /**
     * 小图片模式
     */
    public final static int SHOW_IMG_SMALL = 1;

    static final String TAG = "PrintBuilder";
    /**
     * 正常尺寸
     */
    public int charSize = SIZE_BIG;
    public List<PrintDataItem> data = new ArrayList<>();
    public int COL4_ITEMNAME; // Width = CharSize/scale - FIXED_COL2_QTY/scale -
    /**
     * gbk字符占用的宽度，默认是一个全角是2个字符，但是有些打印机是1.5个字符
     */
    public float gbkSize = 2;
    public boolean forceSize1 = false;

    protected boolean isWMGJType = false;

//    protected PrinterConfig printerConfig = null;

    public PrintBuilder(PrinterConfig printer) {
        if (printer == null) {
            return;
        }
        isWMGJType = false;
        setCharSize(printer.paperSize);
        if (printer.starPrinter()) {
            gbkSize = 1.8f;
            forceSize1 = true;
        } else {
            if (printer.isNeedle()) {
                gbkSize = 1.5f;
            } else {
                gbkSize = 2f;
            }
        }
    }

    private void setCharSize(int charSize) {
        this.charSize = charSize;
    }

    /**
     * 切纸
     */
    public void addCut() {
        PrintDataItem cut = new PrintDataItem();
        cut.dataFormat = (PrintDataItem.FORMAT_CUT);
        this.data.add(cut);
    }

    public void addSing() {
        PrintDataItem sing = new PrintDataItem();
        sing.dataFormat = (PrintDataItem.FORMAT_BEEP);
        this.data.add(sing);
    }

    public void addKickDrawer() {
        PrintDataItem kick = new PrintDataItem();
        kick.dataFormat = (PrintDataItem.FORMAT_DRAWER);
        this.data.add(kick);
    }

    /**
     * 添加单横线
     */
    public void addHortionalLine() {
        int charSizeToAdd = charSize;
        String lstr = new String(new char[charSizeToAdd]).replace('\0', '-').concat("\n");
        PrintDataItem line = new PrintDataItem();
        line.dataFormat = (PrintDataItem.FORMAT_TXT);
        line.text = (lstr);
        this.data.add(line);
    }

    public void addDashLine(String symbol) {
        String lstr = new String(new char[charSize]).replaceAll("\0", symbol).concat("\n");
        PrintDataItem line = new PrintDataItem();
        line.dataFormat = (PrintDataItem.FORMAT_TXT);
        line.text = (lstr);
        this.data.add(line);
    }

    /**
     * 添加双横线
     */
    public void addHortionaDoublelLine() {
        String lstr = new String(new char[charSize]).replace('\0', '=').concat("\n");
        PrintDataItem line = new PrintDataItem();
        line.dataFormat = (PrintDataItem.FORMAT_TXT);
        line.textBold = (-1);
        line.text = (lstr);
        this.data.add(line);
    }

    /**
     * 重载{@link #addBlankLine(int)}
     * 默认高度20
     */
    public void addBlankLine() {
        addBlankLine(1);
    }

    /**
     * 调整行间距
     *
     * @param height int | 高度
     */
    public void addBlankLine(int height) {
        PrintDataItem line = new PrintDataItem();
        line.dataFormat = (PrintDataItem.FORMAT_FEED);
        line.marginTop = height;
        line.fontsize = 0;
        this.data.add(line);
    }

    /**
     * 添加空行
     */
    public void addLine() {
        PrintDataItem line = new PrintDataItem();
        line.dataFormat = (PrintDataItem.FORMAT_TXT);
        line.text = ("\n");
        line.fontsize = 0;
        this.data.add(line);
    }


    /**
     * {@link #addCenterText(String, int, int, String)}的工具方法
     * <p>
     * 默认marginTop为0
     * 默认使用" "作为填充字符
     * 默认字号2
     *
     * @param text String
     */
    public void addTitle(String text) {
        addCenterText(text, 2, 1);
    }

    /**
     * 添加Logo，默认居中打印
     *
     * @param bitmap Bitmap
     */
    public void addLogo(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        PrintDataItem item = new PrintDataItem();
        item.dataFormat = PrintDataItem.FORMAT_IMG;
        item.textAlign = PrintDataItem.ALIGN_CENTRE;
        item.bitmap = bitmap;
        this.data.add(item);

    }

    /**
     * {@link #addCenterText(String, int, int, String)}的工具方法
     * <p>
     * 默认marginTop为0
     * 默认使用"-"作为填充字符
     * 默认字号1
     *
     * @param txt String
     */
    public void addCenterTextPaddingWithDash(String txt) {
        addCenterText(txt, 1, 0, "-");
    }


    /**
     * 重载{@link #addCenterText(String, int, int, String)}
     * 默认marginTop为0
     * 默认使用空格作为填充字符
     * 默认字号为1
     */
    public void addCenterText(String text) {
        addCenterText(text, 1, 0, " ");
    }

    /**
     * 重载{@link #addCenterText(String, int, int, String)}
     * 默认marginTop为0
     * 默认使用空格作为填充字符
     */
    public void addCenterText(String text, int fontSize) {
        addCenterText(text, fontSize, 0, " ");
    }

    /**
     * 重载{@link #addCenterText(String, int, int, String)},
     * 默认使用空格作为填充字符
     */
    public void addCenterText(String text, int fontSize, int marginTop) {
        addCenterText(text, fontSize, marginTop, " ");
    }

    public void addCenterText(String text, int fontSize, int marginTop, String dash) {
        addCenterText(text, fontSize, marginTop, 0, dash);
    }

    /**
     * 添加居中的文字
     *
     * @param text      String | 文本
     * @param fontSize  int | 字号
     * @param marginTop int |  marginTop
     * @param dash      String | 居中的左右填充字符
     */
    public void addCenterText(String text, int fontSize, int marginTop, int color, String dash) {
        if (text == null) {
            text = "";
        }
        PrintDataItem tabPrint = new PrintDataItem();
        if (forceSize1) {
            fontSize = 1;
        }
        tabPrint.dataFormat = (PrintDataItem.FORMAT_TXT);
        tabPrint.textAlign = (PrintDataItem.ALIGN_LEFT);
        tabPrint.fontsize = (fontSize);
        tabPrint.text = PrintStringUtil.padCenter(text, this.charSize, tabPrint.fontsize, true, dash, gbkSize);
        tabPrint.marginTop = marginTop;
        tabPrint.fontColor = color;
        this.data.add(tabPrint);
    }

    public void addCenterTextV2(String text, int fontSize, int marginTop, int color, String dash, int stretchType) {
        if (text == null) {
            text = "";
        }
        PrintDataItem tabPrint = new PrintDataItem();
        if (forceSize1) {
            fontSize = 1;
        }
        tabPrint.dataFormat = (PrintDataItem.FORMAT_TXT);
        tabPrint.textAlign = (PrintDataItem.ALIGN_LEFT);
        tabPrint.fontsize = (fontSize);
        tabPrint.text = PrintStringUtil.padCenter(text, this.charSize, tabPrint.fontsize, true, dash, gbkSize);
        tabPrint.marginTop = marginTop;
        tabPrint.fontColor = color;
        tabPrint.stretchType = stretchType;
        this.data.add(tabPrint);
    }

    public void addCenterTextV2(String text, int stretchType) {
        addCenterTextV2(text, 1, 0, 0, " ", stretchType);
    }


    /**
     * 重载{@link #addLeftWithRight(String, String, int)}
     * 默认字号1
     */
    public void addLeftWithRight(String left, String right) {
        addLeftWithRight(left, right, 1);
    }

    /**
     * 默认字号1
     *
     * @param marginTop 间距
     */
    public void addLeftWithRight(int marginTop, String left, String right) {
        addLeftWithRight(left, right, 1, marginTop);
    }


    /**
     * /**
     * 添加如下样式的文案
     * 左         右
     *
     * @param left     String
     * @param right    String
     * @param fontSize int | 字号
     */
    public void addLeftWithRight(String left, String right, int fontSize) {
        addLeftWithRight(left, right, fontSize, 0);
    }

    public void addLeftWithRight(String left, String right, int fontSize, int marginTop) {
        addLeftWithRight(left, right, fontSize, marginTop, 0);
    }

    /**
     * 添加如下样式的文案
     * 左         右
     *
     * @param left      String
     * @param right     String
     * @param fontSize  int | 字号
     * @param marginTop int | 距离上边距
     */
    public void addLeftWithRight(String left, String right, int fontSize, int marginTop, int fontColor) {
        if (forceSize1) {
            fontSize = 1;
        }
        if (left == null) {
            left = "";
        }
        if (right == null) {
            right = "";
        }
        StringBuilder info = new StringBuilder();
        if (fontSize <= 0) {
            fontSize = 1;
        }
        String[] splitedcontents = {left};

        int totalRightSize = PrintStringUtil.getStringLength(right, gbkSize, fontSize) + 2;

        int oneLength = this.charSize - totalRightSize;
        COL4_ITEMNAME = oneLength;
        try {
            splitedcontents = PrintStringUtil.formatLn(oneLength, left, fontSize, gbkSize);
        } catch (UnsupportedEncodingException e) {
            PrintLog.e("", "", e);
        }
        for (int i = 0; i < splitedcontents.length; i++) {
            if (i == 0) {
                info.append(PrintStringUtil.padRight(splitedcontents[i], oneLength, fontSize, gbkSize)).append(PrintStringUtil.padLeft(right, totalRightSize, fontSize, gbkSize));
            } else {
                info.append(PrintStringUtil.padRight(splitedcontents[i], totalRightSize, fontSize, gbkSize));
            }
            if (splitedcontents.length > 1) {
                info.append("\n");
            }
        }
        if (splitedcontents.length <= 1) {
            info.append("\n");
        }
        PrintDataItem tabPrint1 = new PrintDataItem();
        tabPrint1.dataFormat = PrintDataItem.FORMAT_TXT;
        tabPrint1.textAlign = PrintDataItem.ALIGN_LEFT;
        tabPrint1.marginTop = marginTop;
        tabPrint1.fontsize = fontSize;
        tabPrint1.fontColor = fontColor;
        tabPrint1.text = info.toString();
        this.data.add(tabPrint1);
    }

    /**
     * 添加如下样式的文案
     * 左         右
     *
     * @param left      String
     * @param right     String
     * @param fontSize  int | 字号
     * @param marginTop int | 距离上边距
     */
    public void addLeftWithRightV2(String left, String right, int fontSize, int marginTop, int fontColor, int stretchType) {
        if (forceSize1) {
            fontSize = 1;
        }
        if (left == null) {
            left = "";
        }
        if (right == null) {
            right = "";
        }
        StringBuilder info = new StringBuilder();
        if (fontSize <= 0) {
            fontSize = 1;
        }
        String[] splitedcontents = {left};

        int totalRightSize = PrintStringUtil.getStringLength(right, gbkSize, fontSize) + 2;

        int oneLength = this.charSize - totalRightSize;
        COL4_ITEMNAME = oneLength;
        try {
            splitedcontents = PrintStringUtil.formatLn(oneLength, left, fontSize, gbkSize);
        } catch (UnsupportedEncodingException e) {
            PrintLog.e("", "", e);
        }
        for (int i = 0; i < splitedcontents.length; i++) {
            if (i == 0) {
                info.append(PrintStringUtil.padRight(splitedcontents[i], oneLength, fontSize, gbkSize)).append(PrintStringUtil.padLeft(right, totalRightSize, fontSize, gbkSize));
            } else {
                info.append(PrintStringUtil.padRight(splitedcontents[i], totalRightSize, fontSize, gbkSize));
            }
            if (splitedcontents.length > 1) {
                info.append("\n");
            }
        }
        if (splitedcontents.length <= 1) {
            info.append("\n");
        }
        PrintDataItem tabPrint1 = new PrintDataItem();
        tabPrint1.dataFormat = PrintDataItem.FORMAT_TXT;
        tabPrint1.textAlign = PrintDataItem.ALIGN_LEFT;
        tabPrint1.marginTop = marginTop;
        tabPrint1.fontsize = fontSize;
        tabPrint1.fontColor = fontColor;
        tabPrint1.text = info.toString();
        tabPrint1.stretchType = stretchType;
        this.data.add(tabPrint1);
    }

    public void addLeftWithRightV2(String left, String right, int stretchType) {
        addLeftWithRightV2(left, right, 1, 0, 0, stretchType);
    }


    /**
     * 打印文字,默认字号1,居左
     *
     * @param text String
     */
    public void addText(String text) {
        addText(text, 1, PrintDataItem.ALIGN_LEFT, 0);
    }

    /**
     * 打印文字,默认居左
     *
     * @param text     String
     * @param fontSize int | 字号
     */
    public void addText(String text, int fontSize) {
        addText(text, fontSize, PrintDataItem.ALIGN_LEFT, 0);
    }

    public void addText(String text, int fontSize, int textAlign, int marginTop) {
        addText(text, fontSize, textAlign, marginTop, 0);
    }

    /**
     * 打印文字,默认字号1,居左
     *
     * @param text String
     */
    public void addRedText(String text) {
        addText(text, 1, PrintDataItem.ALIGN_LEFT, 0, 1);
    }

    public void addRedText(String text, int fontSize) {
        addText(text, fontSize, PrintDataItem.ALIGN_LEFT, 0, 1);
    }

    /**
     * 打印文字
     *
     * @param text      String | 文案
     * @param fontSize  int | 字号
     * @param textAlign int |textAlign
     * @param marginTop int | marginTop
     * @param fontColor int | 颜色,see{@link PrintDataItem#fontColor}
     */
    public void addText(String text, int fontSize, int textAlign, int marginTop, int fontColor) {
        if (forceSize1) {
            fontSize = 1;
        }
        if (text == null) {
            text = "";
        }
        PrintDataItem tabPrint = new PrintDataItem();

        tabPrint.dataFormat = (PrintDataItem.FORMAT_TXT);
        tabPrint.textAlign = textAlign;
        tabPrint.fontsize = (fontSize);

        tabPrint.text = (text);
        tabPrint.marginTop = marginTop;
        tabPrint.fontColor = fontColor;
        this.data.add(tabPrint);
    }

    public void addTextV2(String text, int stretchType) {
        addTextV2(text, 1, PrintDataItem.ALIGN_LEFT, 0, 0, stretchType);
    }

    public void addTextV2(String text, int fontSize, int textAlign, int marginTop, int stretchType) {
        addTextV2(text, fontSize, textAlign, marginTop, 0, stretchType);
    }

    public void addTextV2(String text, int fontSize, int textAlign, int marginTop, int fontColor, int stretchType){
        addTextV2(text, fontSize, textAlign, marginTop, fontColor, stretchType,0);
    }

    public void addTextV2(String text, int fontSize, int textAlign, int marginTop, int fontColor, int stretchType, int lineSpace) {
        if (forceSize1) {
            fontSize = 1;
        }
        if (text == null) {
            text = "";
        }
        PrintDataItem tabPrint = new PrintDataItem();

        tabPrint.dataFormat = (PrintDataItem.FORMAT_TXT);
        tabPrint.textAlign = textAlign;
        tabPrint.fontsize = (fontSize);
        tabPrint.setLineSpace(lineSpace);

        tabPrint.text = (text);
        tabPrint.marginTop = marginTop;
        tabPrint.fontColor = fontColor;
        tabPrint.stretchType = stretchType;
        this.data.add(tabPrint);
    }

    /**
     * 蜂鸣
     */
    public void addBeep() {
        PrintDataItem tabPrint = new PrintDataItem();

        tabPrint.dataFormat = (PrintDataItem.FORMAT_BEEP);
        this.data.add(tabPrint);
    }

    /**
     * 条形码
     *
     * @param text      码内容
     * @param textAlign 对其方式
     */
    public void addBarCod(String text, int textAlign) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        PrintDataItem tabPrint = new PrintDataItem();
        tabPrint.addBarcode(text);
        tabPrint.textAlign = textAlign;
        this.data.add(tabPrint);
    }

    /**
     * 条形码--默认居中
     *
     * @param text 码内容
     */
    public void addBarCod(String text) {
        addBarCod(text, PrintDataItem.ALIGN_CENTRE);
    }

    /**
     * 二维码
     *
     * @param text      码内容
     * @param textAlign 对其方式
     * @param fontsize  延用字体值字段，设定打印图片的大小
     */
    public void addQRcode(String text, int textAlign, int fontsize) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        PrintDataItem tabPrint = new PrintDataItem();
        tabPrint.textAlign = textAlign;
        tabPrint.fontsize = fontsize;
        tabPrint.addQRcode(text);
        this.data.add(tabPrint);
    }

    public void addQRcode(String text, int textAlign) {
        addQRcode(text,textAlign,SHOW_IMG_BIG);
    }

        /**
         * 二维码 -- 默认居中
         *
         * @param text 码内容
         */
    public void addQRcode(String text) {
        addQRcode(text, PrintDataItem.ALIGN_CENTRE, SHOW_IMG_BIG);
    }

    /**
     * 菜名---单位
     * <p>
     * 如果单位过长,分多行放置单位,一行最多放四个汉字
     *
     * @param itemName
     * @param unit
     * @param fontSize
     */
    public void addItemNameWithUnit(String itemName, String unit, int fontSize) {
        addItemNameWithUnit(itemName, unit, fontSize, 0);
    }

    public void addItemNameWithUnit(String itemName, String unit, int fontSize, int fontColor) {
        if (forceSize1) {
            fontSize = 1;
        }
        StringBuilder info = new StringBuilder();
        if (fontSize <= 0) {
            fontSize = 1;
        }
        if (itemName == null) {
            itemName = "";
        }
        if (unit == null) {
            unit = "";
        }
        List<String> itemArr; //菜名数组
        List<String> unitArr; //单位数组---如果单位很长,分多行显示
        int unitMaxSize; //一行单位的最大占位长度
        if (PrintStringUtil.getStringLength(unit, gbkSize, fontSize) > 10 * fontSize) {//最多九个字符：11.11/汉字，普通字符是1个字符，1个汉字2/1.5个字符，所有字符需要乘以fontsize
            unitMaxSize = 8 * fontSize + 2;
            unitArr = processToList(unit, unitMaxSize - 2, fontSize);
        } else {
            unitArr = new ArrayList<>();
            unitArr.add(unit);
            unitMaxSize = PrintStringUtil.getStringLength(unit, gbkSize, fontSize) + 2;
        }
        int oneLength = this.charSize - unitMaxSize;
        COL4_ITEMNAME = oneLength;
        itemArr = processToList(itemName, oneLength, fontSize);
        for (int i = 0; i < (itemArr.size() > unitArr.size() ? itemArr.size() : unitArr.size()); i++) {
            String name = i < itemArr.size() ? itemArr.get(i) : "";
            String unitS = i < unitArr.size() ? unitArr.get(i) : "";
            info.append(PrintStringUtil.padRight(name, oneLength, fontSize, gbkSize)).append(PrintStringUtil.padLeft(unitS, unitMaxSize, fontSize, gbkSize));
            info.append("\n");
        }
        PrintDataItem tabPrint1 = new PrintDataItem();
        tabPrint1.dataFormat = PrintDataItem.FORMAT_TXT;
        tabPrint1.textAlign = PrintDataItem.ALIGN_LEFT;
        tabPrint1.fontsize = fontSize;
        tabPrint1.text = info.toString();
        tabPrint1.fontColor = fontColor;
        this.data.add(tabPrint1);
    }

    public void addItemNameWithUnitV2(String itemName, String unit, int fontSize, int fontColor, int stretchType) {
        if (forceSize1) {
            fontSize = 1;
        }
        StringBuilder info = new StringBuilder();
        if (fontSize <= 0) {
            fontSize = 1;
        }
        if (itemName == null) {
            itemName = "";
        }
        if (unit == null) {
            unit = "";
        }
        List<String> itemArr; //菜名数组
        List<String> unitArr; //单位数组---如果单位很长,分多行显示
        int unitMaxSize; //一行单位的最大占位长度
        if (PrintStringUtil.getStringLength(unit, gbkSize, fontSize) > 10 * fontSize) {//最多九个字符：11.11/汉字，普通字符是1个字符，1个汉字2/1.5个字符，所有字符需要乘以fontsize
            unitMaxSize = 8 * fontSize + 2;
            unitArr = processToList(unit, unitMaxSize - 2, fontSize);
        } else {
            unitArr = new ArrayList<>();
            unitArr.add(unit);
            unitMaxSize = PrintStringUtil.getStringLength(unit, gbkSize, fontSize) + 2;
        }
        int oneLength = this.charSize - unitMaxSize;
        COL4_ITEMNAME = oneLength;
        itemArr = processToList(itemName, oneLength, fontSize);
        for (int i = 0; i < (itemArr.size() > unitArr.size() ? itemArr.size() : unitArr.size()); i++) {
            String name = i < itemArr.size() ? itemArr.get(i) : "";
            String unitS = i < unitArr.size() ? unitArr.get(i) : "";
            info.append(PrintStringUtil.padRight(name, oneLength, fontSize, gbkSize)).append(PrintStringUtil.padLeft(unitS, unitMaxSize, fontSize, gbkSize));
            info.append("\n");
        }
        PrintDataItem tabPrint1 = new PrintDataItem();
        tabPrint1.dataFormat = PrintDataItem.FORMAT_TXT;
        tabPrint1.textAlign = PrintDataItem.ALIGN_LEFT;
        tabPrint1.fontsize = fontSize;
        tabPrint1.stretchType = stretchType;
        tabPrint1.text = info.toString();
        tabPrint1.fontColor = fontColor;
        this.data.add(tabPrint1);
    }

    public void addItemNameWithUnitV2(String itemName, String unit, int fontSize, int stretchType) {
        addItemNameWithUnitV2(itemName, unit, fontSize, 0, stretchType);
    }

    /**
     * 将字符串分段
     *
     * @param sourceData
     * @param length     每一段字符串最大长度
     * @param fontSize   字号
     * @return
     */
    private List<String> processToList(String sourceData, int length, int fontSize) {
        List<String> result = new ArrayList<>();
        char[] strArr = sourceData.toCharArray();
        StringBuilder sb = new StringBuilder();
        int remainder;
        try {
            for (char a : strArr) {
                remainder = length - PrintStringUtil.getStringLength(sb.toString(), gbkSize, fontSize);
                if (PrintStringUtil.getCharLength(a, gbkSize, fontSize) > remainder) {
                    result.add(sb.toString());
                    sb.delete(0, sb.length());
                }
                sb.append(a);
            }
        } catch (Exception e) {
            PrintLog.e("", "", e);
        }
        if (!TextUtils.isEmpty(sb.toString())) {
            result.add(sb.toString());
        }
        return result;

    }


    public List<PrintDataItem> getData() {
        return data;
    }

    public void setData(List<PrintDataItem> data) {
        this.data = data;
    }
}
