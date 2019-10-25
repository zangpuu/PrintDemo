package me.zhangpu.demo.print.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.zhangpu.demo.print.printer.PrintLog;

public class PrintStringUtil {
    public static String padRight(String txt, int len, float unitSize) {
        return padRight(txt, len, 1, unitSize);
    }

    public static String padRight(String txt, int len, int fontSize, float unitSize) {
        int length = 0;
        if (txt == null) {
            txt = "";
        }
        StringBuilder ret = new StringBuilder(txt);
        length = getStringLength(txt, unitSize, fontSize);
        if (len > length) {
            int pad = (len - length) / fontSize;
            if (pad > 0) {
                String padchars = String.format("%1$" + pad + "s", "");
                ret.append(padchars);
            }
        }
        return (ret.toString());
    }

    public static String padLeft(String txt, int len, float unitSize) {
        return padLeft(txt, len, 1, unitSize);
    }

    public static String padLeft(String txt, int len, int fontSize, float unitSize) {
        int length = 0;
        if (txt == null) {
            txt = "";
        }
        StringBuilder ret = new StringBuilder(txt);
        length = getStringLength(txt, unitSize, fontSize);
        if (len > length) {
            int pad = (len - length) / fontSize;
            if (pad > 0) {
                String padchars = String.format("%1$" + pad + "s", "");
                ret.insert(0, padchars);
            }
        }
        return (ret.toString());
    }

    public static String getExceptionInfo(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;

        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException var9) {
                    PrintLog.e("", "", e);
                }
            }

            if (pw != null) {
                pw.close();
            }

        }

        return sw.toString();
    }

    public static String padMiddle(String header, String tail, int len, float unitSize) {
        int length = 0;
        StringBuilder ret = null;
        int headerLength = getStringLength(header, unitSize, 1);
        int tailLength = getStringLength(tail, unitSize, 1);
        length = headerLength + tailLength;
        ret = new StringBuilder(header);
        if (len > length) {
            int pad = len - length;
            String padchars = String.format("%1$" + pad + "s", "");
            ret.append(padchars);
        }
        ret.append(tail);
        return (ret.toString());
    }

    public static String padCenter(String txt, int len, float unitSize) {
        return padCenter(txt, len, 1, false, " ", unitSize);
    }

    public static String padCenter(String txt, int len, int fontSize, boolean autoLine, float unitSize) {
        return padCenter(txt, len, fontSize, autoLine, " ", unitSize);
    }

    /**
     * 将文字:"一二三四五六七八九十张王李"
     * 格式化为:
     * -----一二三四五-----
     * -----六七八九十-----
     * -------张王李-------
     * 的形式(横线是空格的意思)
     *
     * @param txt      String | 待格式化的文本
     * @param len      int | 一行的长度
     * @param fontSize int | 文本的字体大小
     * @param autoLine boolean | 是否自动换行
     * @return String
     */
    public static String padCenter(String txt, int len, int fontSize, boolean autoLine, String dash, float unitSize) {
        int length = 0;
        StringBuilder ret = new StringBuilder();
        try {
            String[] list = formatLn(len, txt, fontSize, unitSize);
            for (int i = 0; i < list.length; i++) {
                if (i == (list.length - 1)) {
                    StringBuilder last = new StringBuilder();
                    length = getStringLength(list[i], unitSize, fontSize);

                    int padlen = (len - length) / 2 / fontSize;
                    String padchars = "z";
                    if (padlen == 0) {
                        padchars = "";
                    } else {
                        padchars = String.format("%1$" + padlen + "s", dash);
                    }
                    padchars = padchars.replace(" ", dash);
                    last.append(padchars);
                    last.append(list[i]);
                    last.append(padchars);
                    ret.append(last);
                } else {
                    ret.append(list[i]);
                }
                if (autoLine) {
                    ret.append("\n");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    /**
     * 重载,{@link #padCenterWithDash(String, int, int, float)}
     * 默认字号1
     */
    public static String padCenterWithDash(String txt, int len, float unitSize) {
        return padCenterWithDash(txt, len, 1, unitSize);
    }

    /**
     * 居中,并填充"-"
     *
     * @param txt      String | 文本
     * @param len      int | 一行的长度
     * @param fontSize int | 字号
     * @return String
     */
    public static String padCenterWithDash(String txt, int len, int fontSize, float unitSize) {
        return padCenter(txt, len, fontSize, false, "-", unitSize);
    }

    /**
     * @param text
     * @param lineLength
     * @param unitSize
     * @param mappingModel 居中样式 0：左； 1：右
     * @return
     */
    public static ArrayList<String> splitEqually(String text, int lineLength, float unitSize, int fontSize, int mappingModel) {
        int length = text.length();
        byte[] data = text.getBytes();

        try {
            length = getStringLength(text, unitSize, 1);
            data = text.trim().getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ArrayList<String> ret = new ArrayList<>((length + lineLength - 1) / lineLength);

        for (int start = 0; start < length; start += lineLength) {
            if (data.length < start) {
                //LogUtil.logError("字符切割异常 [" + text + "," + lineLength + "," + unitSize + "," + fontSize + "," + mappingModel + "]");
                continue;
            }
            //TODO 刘秀秀-这里可以改成System.arrayCopy，目前这种写法，会出现数组长度异常
            byte[] tmp = new byte[lineLength];

            if (data.length - start < lineLength) {
                tmp = new byte[data.length - start];
            }
//            System.arraycopy(data,start,tmp,0,data.length - start);
            for (int m = 0; m < lineLength; m++) {
                if (start + m < data.length) {
                    tmp[m] = data[start + m];
                }
            }
            if (tmp.length > 0) {
                if (tmp[tmp.length - 1] == 0) {
                    tmp[tmp.length - 1] = 32;
                }
            }
            String splitted = null;
            try {
                splitted = new String(tmp, "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            ret.add(splitted);
        }

        if (ret.size() > 0) {
            String lastValue = ret.get(ret.size() - 1);
            if (mappingModel == 0) {
                lastValue = PrintStringUtil.padRight(lastValue, lineLength, fontSize, unitSize);
            } else {
                lastValue = PrintStringUtil.padLeft(lastValue, lineLength, fontSize, unitSize);
            }
            ret.remove(ret.size() - 1);
            ret.add(lastValue);
        }
        return ret;
    }

    public static int nearestTen(double num) {
        return (int) Math.ceil((num / 5d) * 5);
    }


    public static boolean isChinese(String args) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(args);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg;
    }

    public static String getFormatLengthString(int size, String orginStr, int fontSize, float unitSize) {
        try {
            String[] list = formatLn(size, orginStr, fontSize, unitSize);
            StringBuilder sb = new StringBuilder();
            for (String temp : list) {
                sb.append(temp).append("\n");
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String[] formatLn(int size, String orginStr, float unitSize)
            throws UnsupportedEncodingException {
        return formatLn(size, orginStr, 1, unitSize);
    }

    public static String[] formatLn(int size, String orginStr, int fontSize, float unitSize)
            throws UnsupportedEncodingException {
        if (fontSize <= 0) {
            fontSize = 1;
        }
        StringBuilder sb = new StringBuilder();
//        List<String> list = Arrays.asList(orginStr.replaceAll("\\s{2,}", " ")
//                .split(" "));
        List<String> list = new ArrayList<>();
        list.add(orginStr);
        int newSize = 0;
        for (String str : list) {
            if (getStringLength(str, unitSize, fontSize) > size) {
                if (sb.length() != 0) {
                    sb.append("\n");
                }
//				stringBuffer.append("\n").append(str).append("\n");
                char[] Characterlist = str.toCharArray();
                float newChatSize = 0;
                for (char c : Characterlist) {
                    newChatSize += getCharLength(c, unitSize, fontSize);
                    if (newChatSize <= size) {
                        sb.append(c);
                    } else {
                        sb.append("\n").append(c);
                        newChatSize = getCharLength(c, unitSize, fontSize);
                    }
                }
//                sb.append(" ");
                newSize = (int) (newChatSize + 1);
                continue;
            }
            newSize += getStringLength(str, unitSize, fontSize);
            if (newSize <= size) {
                if (list.indexOf(str) == list.size() - 1) {
                    sb.append(str);
                } else {
                    sb.append(str).append(" ");
                    newSize++;
                }
            } else {
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\n").append(str).append(" ");
                newSize = getStringLength(str, unitSize, fontSize) + 1;
            }
        }
        return sb.toString().split("\n");
    }

    public static String getStr(String[] strs) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < strs.length; i++) {
            str.append(strs[i] + "\n");
        }
        return str.toString();
    }

    public static int getStringLength(String str, float unit, int fontSize) {
        float length = 0;
        for (int i = 0; i < str.length(); i++) {
            int temp = str.codePointAt(i);
            if (temp >= 0 && temp <= 128) {
                length += fontSize;
            } else {
                length += unit * fontSize;
            }
        }
        int result = (int) length;
        if ((length - result) > 0.4) {
            result = result + 1;
        }
        return result;
    }

    public static float getCharLength(char str, float unit, int fontSize) {
        float length = 0;
        int temp = (int) str;
        if (temp >= 0 && temp <= 128) {
            length += fontSize;
        } else {
            length += unit * fontSize;
        }
        return length;
    }
}
