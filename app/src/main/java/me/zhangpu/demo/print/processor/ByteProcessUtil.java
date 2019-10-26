package me.zhangpu.demo.print.processor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.zhangpu.demo.print.printer.PrinterConts;
import me.zhangpu.demo.print.printer.ipPrinter.model.ReceiptCmdModel;
import me.zhangpu.demo.print.printer.ipPrinter.utils.IpPrinterConts;

public class ByteProcessUtil {

    public static final int INT_LEGAL_MULTIPLES = 4;//内容字符串
    public static final int MAX_LENGTH_FOR_PACKAGE = 500;//内容字符串

    //Gsrn命令，用来监控内容是否打完
    private static final byte[] GSRN_CMD = {0x1d, 0x72, 0x01};
    private static final byte[] EPSON_Bill_CONTROL_CMD = {0x1d, 0x28, 0x48, 0x06, 0x00, 0x30, 0x30};

    /**
     * 四字节补全
     *
     * @param content
     * @return
     */
    public static byte[] completeTo4Multiple(byte[] content) {

        if (content == null) {
            return null;
        }

        int length = content.length;

        int remainder = length % INT_LEGAL_MULTIPLES;

        //若是4的倍数直接返回
        if (remainder == 0) {
            return content;
        }

        int completeDigits = INT_LEGAL_MULTIPLES - remainder;

        byte[] content4Time = new byte[length + completeDigits];

        for (int i = 0; i < content4Time.length; i++) {
            if (i < length) {
                content4Time[i] = content[i];
            } else {
                content4Time[i] = 0x00;
            }
        }

        return content4Time;

    }

    /**
     * 按照int大端计算word字节累加和
     */
    public static byte[] calcWordAccumulate(byte[] source) {

        byte[] data = completeTo4Multiple(source);

        int count = data.length / INT_LEGAL_MULTIPLES;
        int calTotal = 0;

        for (int i = 0; i < count; i++) {

            byte[] intTempBytes = {data[4 * i], data[4 * i + 1], data[4 * i + 2], data[4 * i + 3]};
            calTotal = calTotal + byteArrayToIntBE(intTempBytes);
        }

        byte[] result = int2bytesBE(calTotal);

        return result;

    }

    /**
     * int 转 byte数组
     * 大端模式
     *
     * @param num
     * @return
     */
    public static byte[] int2bytesBE(int num) {
        byte[] result = new byte[4];
        result[3] = (byte) ((num >>> 24) & 0xff);//说明一
        result[2] = (byte) ((num >>> 16) & 0xff);
        result[1] = (byte) ((num >>> 8) & 0xff);
        result[0] = (byte) ((num >>> 0) & 0xff);
        return result;
    }

    /**
     * int到byte[]
     * 小段模式
     *
     * @param i
     * @return
     */
    public static byte[] int2BytesSE(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * byte[]转int
     * 小端模式
     *
     * @param bytes
     * @return
     */
    public static int byteArrayToIntSE(byte[] bytes) {
        int value = 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    /**
     * byte[]转int
     * 大端模式
     *
     * @param bytes
     * @return
     */
    public static int byteArrayToIntBE(byte[] bytes) {
        int value = 0;
        //由高位到低位
        for (int i = 3; i >= 0; i--) {
            int shift = i * 8;
            value += (bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    /**
     * 字节数组顺序翻转
     *
     * @param Array
     * @return
     */
    private static byte[] reverseByteArray(byte[] Array) {
        byte[] new_array = new byte[Array.length];
        for (int i = 0; i < Array.length; i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            new_array[i] = Array[Array.length - i - 1];
        }
        return new_array;
    }

    /**
     * 字节累加
     *
     * @param bytes
     * @return
     */
    public static byte bytePlus(byte[] bytes) {
        byte resultByte = 0;

        for (byte item : bytes) {
            resultByte += item;
        }

        return resultByte;
    }

    /**
     * byte数组转十六进制显示
     *
     * @param b
     * @return
     */
    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
            ret += " ";

        }
        return ret;
    }


    /**
     * byte数组转十六进制显示
     *
     * @param b
     * @return
     */
    public static String bytes2HexStringNoSpace(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * byte数组转十六进制显示
     *
     * @param b
     * @return
     */
    public static String bytes2HexStringNSReversal(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[b.length - 1 - i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * byte转十六进制显示
     *
     * @param b
     * @return
     */
    public static String byte2HexString(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    /**
     * 数组累加，产生新的数组
     *
     * @param data1
     * @param data2
     * @return
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }

    /**
     * 数据分包
     * 433打印机使用
     *
     * @param byteList
     * @return
     */
    public static List<byte[]> process433CmdList(List<byte[]> byteList) {

        byte[] tempPackageBytes = new byte[0];
        List<byte[]> resultList = new ArrayList<>();
        boolean cmdBytesValid = true;

        for (int i = 0; i < byteList.size(); i++) {

            if (byteList.get(i).length > MAX_LENGTH_FOR_PACKAGE) {
                cmdBytesValid = false;
                break;
            }

            //判断是否超过单个包最大长度
            if ((tempPackageBytes.length + byteList.get(i).length) > MAX_LENGTH_FOR_PACKAGE) {
                resultList.add(tempPackageBytes);
                //重置temp对象
                tempPackageBytes = new byte[0];
            }

            tempPackageBytes = addBytes(tempPackageBytes, byteList.get(i));

        }

        if (!cmdBytesValid) {
            return null;
        }

        if (tempPackageBytes.length > 0) {
            resultList.add(tempPackageBytes);
        }

        return resultList;

    }

    /**
     * 数据分包
     * 433打印机使用
     *
     * @param byteList
     * @return
     */
    public static List<byte[]> processCmds(List<byte[]> byteList, int singleMaxLength) {

        byte[] tempPackageBytes = new byte[0];
        List<byte[]> resultList = new ArrayList<>();
        boolean cmdBytesValid = true;

        for (int i = 0; i < byteList.size(); i++) {

            //图片包数据处理逻辑
            if (isImageStartCmd(byteList.get(i))) {
                //判断当前为打印图片指令，若已存下的数据量小于单个包的一半，则将图片数据包与其组装在一起
                if (tempPackageBytes.length < (singleMaxLength / 2)) {
                    tempPackageBytes = addBytes(tempPackageBytes, byteList.get(i));
                    tempPackageBytes = addBytes(tempPackageBytes, byteList.get(i + 1));
                    resultList.add(tempPackageBytes);
                } else {
                    resultList.add(tempPackageBytes);
                    resultList.add(addBytes(byteList.get(i), byteList.get(i + 1)));
                }

                tempPackageBytes = new byte[0];
                //由于将图像的数据包和指令一起处理了，所以需要将处理指令往后推一个
                i = i + 1;
                continue;
            }
//            if (byteList.get(i).length > singleMaxLength) {
//                resultList.add(addBytes(tempPackageBytes, byteList.get(i)));
//                tempPackageBytes = new byte[0];
//                continue;
//            }
            //判断是否超过单个包最大长度
            if ((tempPackageBytes.length + byteList.get(i).length) > singleMaxLength) {
                resultList.add(tempPackageBytes);
                //重置temp对象
                tempPackageBytes = new byte[0];
            }

            tempPackageBytes = addBytes(tempPackageBytes, byteList.get(i));

        }

        if (!cmdBytesValid) {
            return null;
        }

        if (tempPackageBytes.length > 0) {
            resultList.add(tempPackageBytes);
        }

        return resultList;

    }

    public static List<byte[]> divideBytes(byte[] source, int singleMaxLength) {
        List<byte[]> divideDates = new ArrayList<>();
        int totalLength = source.length;
        int packageCount = (int) Math.ceil((double) totalLength / singleMaxLength);

        for (int i = 0; i < packageCount; i++) {
            byte[] subcontractedData;

            if ((totalLength - (i * singleMaxLength)) > singleMaxLength) {
                subcontractedData = new byte[singleMaxLength];
            } else {
                subcontractedData = new byte[totalLength - (i * singleMaxLength)];
            }

            System.arraycopy(source,i * singleMaxLength,subcontractedData,0,subcontractedData.length);
            divideDates.add(subcontractedData);
        }

        return divideDates;
    }

//    /**
//     * 从byte数组读取数据
//     *
//     * @param sourceBytes
//     * @param offset
//     * @param length
//     * @return
//     */
//    public static byte[] getBytesFromBytes(byte[] sourceBytes, int offset, int length) {
//
//        byte[] bytes = new byte[length];
//        for (int i = 0; i < length; i++) {
//
//            bytes[i] = sourceBytes[offset + i];
//        }
//
//        return bytes;
//    }

    /**
     * 数组转字符串
     *
     * @param byteArray
     * @return
     */
    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = null;
        try {
            str = new String(byteArray, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 字节数组转int
     * 传入长度需要在1至4之间方能生效
     * 大端模式
     *
     * @param bytes
     * @return
     */
    public static int bytes2IntBE(byte[] bytes) {

        int length = bytes.length;

        if (bytes == null || bytes.length == 0 || bytes.length > 4) {
            return 0;
        }

        int result = 0;

        for (int i = 0; i < length; i++) {
            result += ((bytes[length - i - 1] & 0xff) << (8 * i));

        }

        return result;

    }

    /**
     * 字节数组转int
     * 传入长度需要在1至4之间方能生效
     * 小端模式
     *
     * @param bytes
     * @return
     */
    public static int bytes2IntLE(byte[] bytes) {

        int length = bytes.length;

        if (bytes == null || bytes.length == 0 || bytes.length > 4) {
            return 0;
        }

        int result = 0;

        for (int i = 0; i < length; i++) {
            result += ((bytes[i] & 0xff) << (8 * i));
        }

        return result;

    }

    public static int getHeight4(byte data) {//获取高四位
        int height;
        height = ((data & 0xf0) >> 4);
        return height;
    }

    public static int getLow4(byte data) {//获取低四位
        int low;
        low = (data & 0x0f);
        return low;
    }

    /**
     * 16进制字符串转字节数组
     *
     * @param source 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2ByteArray(String source) {
        if (source == null || "".equals(source)) {
            return null;
        }

        String hexString = source.toLowerCase();

        //先把字符串转换为char[]，再转换为byte[]
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[length];
        String hexDigits = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            int pos = i * 2; // 两个字符对应一个byte
            int h = hexDigits.indexOf(hexChars[pos]) << 4; // 注1
            int l = hexDigits.indexOf(hexChars[pos + 1]); // 注2
            if (h == -1 || l == -1) { // 非16进制字符
                return null;
            }
            bytes[i] = (byte) (h | l);
        }
        return bytes;
    }

    private static boolean isImageStartCmd(byte[] cmd) {
        if (cmd.length != 8) {
            return false;
        }

        return cmd[0] == 0x1d && cmd[1] == 0x76 && cmd[2] == 0x30;
    }

    /**
     * 创建随机id
     *
     * @return
     */
    public static byte[] createRandomId() {
        Random random = new Random();
        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            int randomNum = random.nextInt((126 - 32) + 1) + 32;
            bytes[i] = (byte) randomNum;
        }
        return bytes;
    }

    /**
     * 构建优化指令
     * 根据不同模式构建需要的指令内容
     * （可优化）
     *
     * @param receiptMode
     * @return
     */
    public static ReceiptCmdModel buildReceiptCmd(String receiptMode) {
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
     * 构建优化指令
     * 根据不同模式构建需要的指令内容
     * （可优化）
     *
     * @param receiptMode
     * @return
     */
    public static ReceiptCmdModel buildReceiptCmd(int receiptMode) {
        ReceiptCmdModel receiptCmdModel = new ReceiptCmdModel();
        switch (receiptMode) {

            case PrinterConts.OPTIMIZATION_MODE_FLOW_CONTROL:
                receiptCmdModel.setBasicInstruction(GSRN_CMD);
                receiptCmdModel.setWholeInstruction(GSRN_CMD);
                return receiptCmdModel;

            case PrinterConts.OPTIMIZATION_MODE_PACKAGE_CONTROL:
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
     * 爱普生一票一控结果验证方法
     *
     * @param id
     * @param resultData
     * @return
     */
    public static boolean isEpsonReceiptVaild(byte[] id, byte[] resultData) {

        int idIntValue = ByteProcessUtil.byteArrayToIntBE(id);
        byte[] resultId = {resultData[0], resultData[1], resultData[2], resultData[3]};
        int resultIdINtValue = ByteProcessUtil.byteArrayToIntBE(resultId);

        return idIntValue == resultIdINtValue;
    }

}
