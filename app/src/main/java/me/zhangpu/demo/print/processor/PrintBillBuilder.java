package me.zhangpu.demo.print.processor;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.zhangpu.demo.print.base.PrinterConfig;
import me.zhangpu.demo.print.printer.PrintLog;

public class PrintBillBuilder extends PrintBuilder {

    private final static int FIXED_COL4_SPACE = 1;
    public static int FIXED_COL4_PRICE = 8; //in case of 48 dots width, QTY col = 10dots
    public static int FIXED_COL4_QTY = 8; //in case of 48 dots width, QTY col = 10dots
    public static int FIXED_COL4_TOTAL = 8; //in case of 48 dots width, QTY col = 10dots


    public PrintBillBuilder(PrinterConfig printer) {
        super(printer);
    }

    /**
     * 四列=====菜名、价格、数量、总计----
     *
     * @param col1Title String
     * @param col2Title String
     * @param col3Title String
     * @param col4Title String
     * @return String
     */
    public String getFourColHeader(String col1Title, String col2Title, String col3Title, String col4Title) {
        if (col1Title == null) {
            col1Title = "";
        }
        if (col2Title == null) {
            col2Title = "";
        }
        if (col3Title == null) {
            col3Title = "";
        }
        if (col4Title == null) {
            col4Title = "";
        }

        StringBuilder ret = new StringBuilder();
        COL4_ITEMNAME = this.charSize - PrintBillBuilder.FIXED_COL4_PRICE - PrintBillBuilder.FIXED_COL4_QTY - PrintBillBuilder.FIXED_COL4_TOTAL;
        if (this.charSize == SIZE_MIN || charSize == SIZE_SMALL) {
            COL4_ITEMNAME = this.charSize - PrintBillBuilder.FIXED_COL4_QTY - PrintBillBuilder.FIXED_COL4_TOTAL; //多减1是为了行末不顶线显示
        }
        String title1 = PrintStringUtil.padRight(col1Title, COL4_ITEMNAME, gbkSize);
        String title2 = PrintStringUtil.padLeft(col2Title, PrintBillBuilder.FIXED_COL4_PRICE, gbkSize);
        String title3 = PrintStringUtil.padLeft(col3Title, PrintBillBuilder.FIXED_COL4_QTY, gbkSize);
        String title4 = PrintStringUtil.padLeft(col4Title, PrintBillBuilder.FIXED_COL4_TOTAL, gbkSize);
        if (this.charSize == SIZE_MIN || charSize == SIZE_SMALL) {
            ret.append(title1).append(title3).append(title4).append("\n");
        } else {
            ret.append(title1).append(title2).append(title3).append(title4).append("\n");
        }

        return ret.toString();
    }

    /* Four columns layout (Width = 48dots)
     * |item Name   Dynamical | Price |  |2| QTY 10/scale  | Total|
     *
     **/
    public String getFourColContent(String col1Content, String col2Content,
                                    String col3Content, String col4Content, int fontSize) {

        StringBuilder result = new StringBuilder();

//        col1Content = col1Content;
//        col2Content = col2Content.trim();
//        col3Content = col3Content.trim();
//        col4Content = col4Content.trim();
        if (fontSize > 1) {
            if (this.charSize != SIZE_BIG) {
                fontSize = 1;
            } else if (forceSize1) {
                fontSize = 1;
            }
        }
        int length1 = 0;
        int length2 = PrintBillBuilder.FIXED_COL4_PRICE;
        int length3 = PrintBillBuilder.FIXED_COL4_QTY;
        int length4 = PrintBillBuilder.FIXED_COL4_TOTAL;
        if (TextUtils.isEmpty(col2Content) || this.charSize != SIZE_BIG) {
            if (TextUtils.isEmpty(col2Content) && this.charSize == SIZE_BIG) {
                length3 += length2 / 2;
                length4 += length2 - length2 / 2;
            }
            length2 = 0;
        }
        int col1Lines = 1;
        int col2Lines = 1;
        int col3Lines = 1;
        int col4Lines = 1;
        COL4_ITEMNAME = (this.charSize - length4 - length3 - length2) - (length2 == 0 ? 2 : 3) * PrintBillBuilder.FIXED_COL4_SPACE;
        length1 = COL4_ITEMNAME;
        int ln1 = 1;
        String[] splitedcontents = {col1Content};

        try {
            splitedcontents = PrintStringUtil.formatLn(length1, col1Content, fontSize, gbkSize);
            ln1 = splitedcontents.length;
        } catch (UnsupportedEncodingException e) {
            PrintLog.e("", "", e);
        }
        col1Lines = splitedcontents.length;
        //String col1PadContent = PrintStringUtil.padRight(col1Content, col1Lines*COL4_ITEMNAME);
        //ArrayList<String> splittedCol1Content = PrintStringUtil.splitEqually(col1PadContent, COL4_ITEMNAME);

        ArrayList<String> splittedCol2Content = null;
        if (length2 > 0) {
            double ln2 = (col2Content.length()) / (length2 * 1.0 / fontSize);
            col2Lines = PrintStringUtil.nearestTen(ln2);
            String col2PadContent = PrintStringUtil.padLeft(col2Content, col2Lines * length2, fontSize, gbkSize);
            if (!TextUtils.isEmpty(col2PadContent)) {
                splittedCol2Content = PrintStringUtil.splitEqually( col2PadContent, length2 / fontSize, gbkSize, fontSize, 1);
            }
        }

        double ln3 = (col3Content.length()) / (length3 * 1.0 / fontSize);
        col3Lines = PrintStringUtil.nearestTen(ln3);
        String col3PadContent = PrintStringUtil.padLeft(col3Content, col3Lines * (length3), fontSize, gbkSize);
        ArrayList<String> splittedCol3Content = PrintStringUtil.splitEqually( col3PadContent, (length3 / fontSize), gbkSize, fontSize, 1);

        double ln4 = (col4Content.length()) / (length4 * 1.0 / fontSize);
        col4Lines = PrintStringUtil.nearestTen(ln4);
        String col4PadContent = PrintStringUtil.padLeft(col4Content, col4Lines * (length4), fontSize, gbkSize);
        ArrayList<String> splittedCol4Content = PrintStringUtil.splitEqually( col4PadContent, (length4 / fontSize), gbkSize, fontSize, 1);


        for (int i = 0; i < Math.max(Math.max(col1Lines, col2Lines), Math.max(col3Lines, col4Lines)); i++) {
            if (i < col1Lines) {
                result.append(PrintStringUtil.padRight(splitedcontents[i], length1, fontSize, gbkSize));
            } else {
                result.append(PrintStringUtil.padRight(" ", length1, fontSize, gbkSize));
            }
            //padding
            if (fontSize == 1) {
                result.append(PrintStringUtil.padRight(" ", PrintBillBuilder.FIXED_COL4_SPACE, gbkSize));
            }
            if (length2 != 0) {
                if (i < col2Lines) {
                    result.append(PrintStringUtil.padLeft(splittedCol2Content.get(i), (length2), fontSize, gbkSize));
                } else {
                    result.append(PrintStringUtil.padLeft(" ", (length2), fontSize, gbkSize));
                }
                result.append(PrintStringUtil.padRight(" ", PrintBillBuilder.FIXED_COL4_SPACE, 1));
            }

            if (i < col3Lines) {
                result.append(PrintStringUtil.padLeft(splittedCol3Content.get(i), length3, fontSize, gbkSize));
            } else {
                result.append(PrintStringUtil.padLeft(" ", (length3), fontSize, gbkSize));
            }
            //padding
            result.append(PrintStringUtil.padRight(" ", PrintBillBuilder.FIXED_COL4_SPACE, gbkSize));

            if (i < col4Lines) {
                result.append(PrintStringUtil.padLeft(splittedCol4Content.get(i), (length4), fontSize, gbkSize));
            } else {
                result.append(PrintStringUtil.padLeft(" ", (length4), fontSize, gbkSize));
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * @param contentList   List<String> | 内容列表
     * @param type          int | 长度类型：1，按比例；2，固定长度，此时长度为-1时，表示别的元素的总长度只和剩下的元素
     * @param fixedLength
     * @param col1Content
     * @param col2Content
     * @param col3Content
     * @param col4Content
     * @param fontSize
     * @return
     */
    public String getColContent(List<String> originList, int type, int[] originLength, String col1Content, String col2Content,
                                String col3Content, String col4Content, int fontSize) {
        List<String> contentList = new ArrayList<>();
        int[] fixedLength = new int[originLength.length];
        StringBuilder result = new StringBuilder();

//        col1Content = col1Content;
//        col2Content = col2Content.trim();
//        col3Content = col3Content.trim();
//        col4Content = col4Content.trim();
        if (fontSize > 1) {
            if (this.charSize != SIZE_BIG) {
                fontSize = 1;
            } else if (forceSize1) {
                fontSize = 1;
            }
        }
        int columnTotalSize = this.charSize - (contentList.size() - 1) * PrintBillBuilder.FIXED_COL4_SPACE;
        int elementLength = 0;
        if (type == 1) {
            int totalWeight = 0;
            for (int i = 0; i < fixedLength.length; i++) {
                if (fixedLength[i] > 0) {
                    totalWeight += fixedLength[i];
                }
            }
            elementLength = columnTotalSize / totalWeight;
            for (int i = 0; i < fixedLength.length; i++) {
                if (i == 0) {
                    fixedLength[i] = elementLength * fixedLength[i] + (columnTotalSize % totalWeight);
                } else {
                    fixedLength[i] = elementLength * fixedLength[i];
                }
            }
        } else {
            int currentLength = 0;
            for (int i = 0; i < fixedLength.length; i++) {
                if (fixedLength[i] > 0) {
                    currentLength += fixedLength[i];
                }
            }
            for (int i = 0; i < fixedLength.length; i++) {
                if (fixedLength[i] < 0) {
                    fixedLength[i] = columnTotalSize - currentLength;
                    break;
                }
            }
        }
        int length1 = 0;
        int length2 = PrintBillBuilder.FIXED_COL4_PRICE;
        int length3 = PrintBillBuilder.FIXED_COL4_QTY;
        int length4 = PrintBillBuilder.FIXED_COL4_TOTAL;
        if (fixedLength.length > 1) {
            length2 = fixedLength[1];
        }
        if (fixedLength.length > 2) {
            length3 = fixedLength[2];
        }
        if (fixedLength.length > 3) {
            length4 = fixedLength[3];
        }
        int col1Lines = 1;
        int col2Lines = 1;
        int col3Lines = 1;
        int col4Lines = 1;
        length1 = fixedLength[0];
        COL4_ITEMNAME = length1;
        int ln1 = 1;
        String[] splitedcontents = {col1Content};

        try {
            splitedcontents = PrintStringUtil.formatLn(length1, col1Content, fontSize, gbkSize);
            ln1 = splitedcontents.length;
        } catch (UnsupportedEncodingException e) {
            PrintLog.e("", "", e);
        }
        col1Lines = splitedcontents.length;
        //String col1PadContent = PrintStringUtil.padRight(col1Content, col1Lines*COL4_ITEMNAME);
        //ArrayList<String> splittedCol1Content = PrintStringUtil.splitEqually(col1PadContent, COL4_ITEMNAME);

        ArrayList<String> splittedCol2Content = null;
        if (length2 > 0) {
            double ln2 = (col2Content.length()) / (length2 * 1.0 / fontSize);
            col2Lines = PrintStringUtil.nearestTen(ln2);
            String col2PadContent = PrintStringUtil.padLeft(col2Content, col2Lines * length2, fontSize, gbkSize);
            if (!TextUtils.isEmpty(col2PadContent)) {
                splittedCol2Content = PrintStringUtil.splitEqually(col2PadContent, length2 / fontSize, gbkSize, fontSize, 1);
            }
        }

        double ln3 = (col3Content.length()) / (length3 * 1.0 / fontSize);
        col3Lines = PrintStringUtil.nearestTen(ln3);
        String col3PadContent = PrintStringUtil.padLeft(col3Content, col3Lines * (length3), fontSize, gbkSize);
        ArrayList<String> splittedCol3Content = PrintStringUtil.splitEqually( col3PadContent, (length3 / fontSize), gbkSize, fontSize, 1);

        double ln4 = (col4Content.length()) / (length4 * 1.0 / fontSize);
        col4Lines = PrintStringUtil.nearestTen(ln4);
        String col4PadContent = PrintStringUtil.padLeft(col4Content, col4Lines * (length4), fontSize, gbkSize);
        ArrayList<String> splittedCol4Content = PrintStringUtil.splitEqually( col4PadContent, (length4 / fontSize), gbkSize, fontSize, 1);


        for (int i = 0; i < Math.max(Math.max(col1Lines, col2Lines), Math.max(col3Lines, col4Lines)); i++) {
            if (i < col1Lines) {
                result.append(PrintStringUtil.padRight(splitedcontents[i], length1, fontSize, gbkSize));
            } else {
                result.append(PrintStringUtil.padRight(" ", length1, fontSize, gbkSize));
            }
            //padding
            if (fontSize == 1) {
                result.append(PrintStringUtil.padRight(" ", PrintBillBuilder.FIXED_COL4_SPACE, gbkSize));
            }
            if (length2 != 0) {
                if (i < col2Lines) {
                    result.append(PrintStringUtil.padLeft(splittedCol2Content.get(i), (length2), fontSize, gbkSize));
                } else {
                    result.append(PrintStringUtil.padLeft(" ", (length2), fontSize, gbkSize));
                }
                result.append(PrintStringUtil.padRight(" ", PrintBillBuilder.FIXED_COL4_SPACE, 1));
            }

            if (i < col3Lines) {
                result.append(PrintStringUtil.padLeft(splittedCol3Content.get(i), length3, fontSize, gbkSize));
            } else {
                result.append(PrintStringUtil.padLeft(" ", (length3), fontSize, gbkSize));
            }
            //padding
            result.append(PrintStringUtil.padRight(" ", PrintBillBuilder.FIXED_COL4_SPACE, gbkSize));

            if (i < col4Lines) {
                result.append(PrintStringUtil.padLeft(splittedCol4Content.get(i), (length4), fontSize, gbkSize));
            } else {
                result.append(PrintStringUtil.padLeft(" ", (length4), fontSize, gbkSize));
            }
            result.append("\n");
        }
        return result.toString();
    }

    public void addContentListHeader(String itemName, String price, String qty, String total) {
        PrintDataItem header = new PrintDataItem();
        header.dataFormat = (PrintDataItem.FORMAT_TXT);
        header.text = (this.getFourColHeader( itemName, price, qty, total));
        this.data.add(header);
        addHortionalLine();
    }

    public void addOrderItem(String itemName, String price, String qty, String total, int fontSize) {
        add4Column(itemName, price, qty, total, fontSize);
    }

    public void add4Column(String itemName, String price, String qty, String total, int fontSize) {
        add4Column(itemName, price, qty, total, fontSize, 0);
    }

    public void add4Column(String itemName, String price, String qty, String total, int fontSize, int fontColor) {
        PrintDataItem order = new PrintDataItem();
        if (forceSize1) {
            fontSize = 1;
        }
        if (fontSize > 1) {
            if (this.charSize != SIZE_BIG) {
                fontSize = 1;
            } else if (forceSize1) {
                fontSize = 1;
            }
        }
        order.dataFormat = (PrintDataItem.FORMAT_TXT);
        order.fontsize = (fontSize);
        order.language = (PrintDataItem.LANG_CN);
        order.marginTop = (0);
        order.text = (this.getFourColContent( itemName, price, qty, total, fontSize));
        order.textAlign = (PrintDataItem.ALIGN_LEFT);
        order.fontColor = fontColor;
        this.data.add(order);
    }

    public void addRightText(String text) {
        addText(text, 1, PrintDataItem.ALIGN_RIGHT, 20);
    }

    public void addOrderModifier(String itemName, int fontSize) {
        addTextWithFixSizeAndSymbol(itemName, COL4_ITEMNAME - 2, "-", fontSize, 0, 0);
    }

    public void addTextWithFixSizeAndSymbol(String itemName, String symbol, int fontSize, int margin, int fontColor) {
        addTextWithFixSizeAndSymbol(itemName, COL4_ITEMNAME - 2, symbol, fontSize, margin, fontColor);
    }

    public void addTextWithFixSizeAndSymbol(String itemName, int totalLength, String symbol, int fontSize, int margin, int fontColor) {
        if (TextUtils.isEmpty(symbol)) {
            symbol = " ";
        }
        PrintDataItem orderMod = new PrintDataItem();
        if (forceSize1) {
            fontSize = 1;
        }
        orderMod.dataFormat = (PrintDataItem.FORMAT_TXT);
        orderMod.fontsize = (fontSize);
        StringBuilder sb = new StringBuilder();
        try {
            String[] list = PrintStringUtil.formatLn(totalLength, itemName, 1, gbkSize);
            sb.append("  ").append(symbol);
            for (int i = 0; i < list.length; i++) {
                String temp = list[i];
                if (i != 0) {
                    sb.append("   ");
                }
                sb.append(temp).append("\n");
            }
        } catch (UnsupportedEncodingException e) {
            PrintLog.e("", "", e);
        }
        orderMod.text = sb.toString();
        orderMod.textAlign = (PrintDataItem.ALIGN_LEFT);
        orderMod.marginTop = margin;
        orderMod.fontColor = fontColor;
        this.data.add(orderMod);
    }

    public void addOrderModifier2(String itemName, int fontSize) {
        if (itemName == null) {
            itemName = "";
        }
        PrintDataItem orderMod = new PrintDataItem();
        orderMod.dataFormat = (PrintDataItem.FORMAT_TXT);
        if (forceSize1) {
            fontSize = 1;
        }
        orderMod.fontsize = (fontSize);
        StringBuilder sb = new StringBuilder();
        try {
            String[] list = PrintStringUtil.formatLn(COL4_ITEMNAME - 2, itemName, 1, gbkSize);
            sb.append("  ");
            for (int i = 0; i < list.length; i++) {
                String temp = list[i];
                if (i != 0) {
                    sb.append("   ");
                }
                sb.append(temp).append("\n");
            }
        } catch (UnsupportedEncodingException e) {
            PrintLog.e("", "", e);
        }
        orderMod.text = sb.toString();
        orderMod.textAlign = (PrintDataItem.ALIGN_LEFT);
        this.data.add(orderMod);
    }

    public void addSub(String name, String second, String third) {
        addSub(name, second, third, 1, 0);
    }

    public void addSub(String name, String second, String third, int size, int fontColor) {
        if (name == null) {
            name = "";
        }
        if (second == null) {
            second = "";
        }
        if (third == null) {
            third = "";
        }
        StringBuilder info = new StringBuilder();
        int size1 = (this.charSize / 3) * 2;
        int size2 = (this.charSize / 8);
        info.append(PrintStringUtil.padLeft(name, size1, gbkSize));
        if (charSize != SIZE_MIN && charSize != SIZE_SMALL) {
            info.append(PrintStringUtil.padLeft(second, size2, gbkSize));
        } else {
            size2 = 0;
        }
        info.append(PrintStringUtil.padLeft(third, charSize - size1 - size2, gbkSize));

        PrintDataItem mod = new PrintDataItem();
        mod.dataFormat = (PrintDataItem.FORMAT_TXT);
        mod.fontsize = (1);
        mod.language = (PrintDataItem.LANG_CN);
        mod.textAlign = (PrintDataItem.ALIGN_LEFT);
        mod.text = info.toString() + "\n";
        mod.fontsize = size;
        mod.fontColor = fontColor;
        this.data.add(mod);
    }

    public void addLeftBigWithRight(String leftSmall, String leftBig, String rightSmall) {
        addLeftBigWithRight(leftSmall, leftBig, rightSmall, 0);
    }

    public void addLeftBigWithRight(String leftSmall, String leftBig, String rightSmall, int fontColor) {
        try {
            this.addTextV2(leftSmall, 1, PrintDataItem.ALIGN_LEFT, 0, fontColor,0,PrintDataItem.LING_SPACE_BIG);
            this.addTextV2(leftBig, 2, PrintDataItem.ALIGN_LEFT, 0, fontColor,0,PrintDataItem.LING_SPACE_BIG);

            this.addTextV2(PrintStringUtil.padLeft(rightSmall, this.charSize - leftSmall.getBytes("GBK").length - leftBig.getBytes("GBK").length * 2, this.gbkSize) + "\n", 1, PrintDataItem.ALIGN_RIGHT, 0,0,0,PrintDataItem.LING_SPACE_BIG);

        } catch (UnsupportedEncodingException e) {
            PrintLog.e("", "", e);
        }
    }

    public void addLeftWithRightBig(String leftString, String rightSmall, String rightBig) {
        addLeftWithRightBig(leftString, rightSmall, rightBig, 0);
    }

    public void addLeftWithRightBig(String leftString, String rightSmall, String rightBig, int fontColor) {
        rightBig = rightBig.replace("\n", "");
        try {
            this.addTextV2(PrintStringUtil.padRight(leftString, this.charSize - rightSmall.getBytes("GBK").length - rightBig.getBytes("GBK").length * 2, this.gbkSize), 1, 0, 0, fontColor,0,PrintDataItem.LING_SPACE_BIG);
            this.addTextV2(rightSmall, 1, PrintDataItem.ALIGN_RIGHT, 0, fontColor,0,PrintDataItem.LING_SPACE_BIG);
            this.addTextV2(rightBig + "\n", 2, PrintDataItem.ALIGN_RIGHT, 0, fontColor,0,PrintDataItem.LING_SPACE_BIG);
        } catch (UnsupportedEncodingException e) {
            PrintLog.e("", "", e);
        }
    }

    public void setData(ArrayList<PrintDataItem> data) {
        this.data = data;
    }
}
