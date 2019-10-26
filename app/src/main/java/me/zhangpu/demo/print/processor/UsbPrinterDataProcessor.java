package me.zhangpu.demo.print.processor;

import java.util.ArrayList;
import java.util.List;

public class UsbPrinterDataProcessor {

    public static List<byte[]> divideImageData(int singlePartLength, byte[] source) {
        List<byte[]> resultList = new ArrayList<>();

        int numberOfCopies = (source.length / singlePartLength) + 1;

        for (int i = 0; i < numberOfCopies; i++) {

            if (i + 1 != numberOfCopies) {
                byte[] eachPart = new byte[singlePartLength];
                System.arraycopy(source, i * singlePartLength, eachPart, 0, singlePartLength);
                resultList.add(eachPart);
            } else {
                int lastPartLength = source.length - (singlePartLength * i);
                byte[] eachPart = new byte[lastPartLength];
                System.arraycopy(source, i * singlePartLength, eachPart, 0, lastPartLength);
                resultList.add(eachPart);
            }

        }

        return resultList;
    }
}
