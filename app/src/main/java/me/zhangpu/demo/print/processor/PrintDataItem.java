package me.zhangpu.demo.print.processor;

import android.graphics.Bitmap;

import java.io.Serializable;

public class PrintDataItem implements Serializable {

    public static final long serialVersionUID = 8607945962987886031L;
    //content format
    public final static int FORMAT_TXT = 10;//文本
    public final static int FORMAT_IMG = 11;//图片
    public final static int FORMAT_QR = 12;//二维码
    public final static int FORMAT_CUT = 13;//切纸
    public final static int FORMAT_DRAWER = 14;//钱箱
    public final static int FORMAT_FEED = 15;//进纸
    public final static int FORMAT_BARCODE = 16;//条形码
    public final static int FORMAT_BEEP = 18;//蜂鸣
    //alignment
    public final static int ALIGN_LEFT = 0;
    public final static int ALIGN_CENTRE = 1;
    public final static int ALIGN_RIGHT = 2;
    //Languate
    public static int LANG_EN = 0;
    public static int LANG_CN = 1;
    //image format
    public static int IMAGE_PNG = 20;
    public static int IMAGE_JPG = 21;
    //STRETCH
    public final static int STRETCH_TYPE_NONE = 0;
    public final static int STRETCH_TYPE_VERTICAL = 1;
    public final static int STRETCH_TYPE_HORIZONTAL = 2;
    //行间距
    public final static int LING_SPACE_SMALL = 64;
    public final static int LING_SPACE_BIG = 112;


    /**
     * 数据类型
     */
    public int dataFormat;
    /**
     * 字体
     */
    public int font;
    /**
     * 字体大小
     */
    public int fontsize;
    /**
     * 字体颜色：0，黑色；1，红色
     */
    public int fontColor = 0;
    /**
     * 内容的Align
     */
    public int textAlign;
    /**
     * 顶部的Margin
     */
    public int marginTop;
    /**
     * 是否粗体
     */
    public int textBold;
    /**
     * 下划线
     */
    public boolean underline;

    public int language;
    /**
     * 文本
     */
    public String text;
    public Bitmap bitmap = null;
    /**
     * TSC指令专用,下一个字段是否新起一行
     */
    public boolean tscNextLine = false;

    /**
     * 拉伸枚举
     * 0：无拉伸 1：垂直拉伸 2：横向拉伸
     */
    public int stretchType = 0;

    /**
     * 行间距
     */
    private int lineSpace = 0;

    public PrintDataItem() {
        super();
        this.dataFormat = -1;
        this.font = -1;
        this.fontsize = -1;
        this.text = null;
        this.textAlign = -1;
        this.marginTop = -1;
        this.textBold = -1;
        this.language = -1;
        this.underline = false;
    }

    public void addText(int format, String txt, int fontSize, int font) {
        this.dataFormat = format;
        this.text = txt;
        this.fontsize = fontSize;
        this.font = font;
    }

    /**
     * 细条形码
     *
     * @param txt String
     */
    public void addBarcode(String txt) {
        this.text = txt;
        this.dataFormat = PrintDataItem.FORMAT_BARCODE;
        this.textAlign = ALIGN_CENTRE;
    }

    /**
     * 二维码
     *
     * @param txt String
     */
    public void addQRcode(String txt) {
        this.text = txt;
        this.dataFormat = PrintDataItem.FORMAT_QR;
        this.textAlign = ALIGN_CENTRE;
    }

    public void setLineSpace(int lineSpace){
        this.lineSpace = lineSpace;
    }

    /**
     * 行间距
     * @return
     */
    public int getLineSpace(){
        if(lineSpace == 0){
            lineSpace = LING_SPACE_SMALL;
            if(fontsize == 2){
                lineSpace = LING_SPACE_BIG;
            }
        }
        return lineSpace;
    }
}
