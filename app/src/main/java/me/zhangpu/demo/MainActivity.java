package me.zhangpu.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.zhangpu.demo.print.JsonUtil;
import me.zhangpu.demo.print.base.PaperType;
import me.zhangpu.demo.print.base.PrinterConfig;
import me.zhangpu.demo.print.encoding.Lang;
import me.zhangpu.demo.print.printer.PrintLog;
import me.zhangpu.demo.print.printer.ipPrinter.IpPrinterConfig;
import me.zhangpu.demo.print.printer.ipPrinter.utils.IpPrinterConts;
import me.zhangpu.demo.print.processor.PrintBillBuilder;
import me.zhangpu.demo.print.processor.PrintDataItem;
import me.zhangpu.demo.print.processor.PrintResult;
import me.zhangpu.demo.print.processor.PrintResultCode;
import me.zhangpu.demo.print.processor.PrinterManager;

public class MainActivity extends AppCompatActivity {

    private int PAPER_TYPE = PaperType.Thermal;
    private int PAPER_SIZE = PrintBillBuilder.SIZE_BIG;
    private String PRINTER_COMMAND = "ESC";
    private int lang = Lang.CHINESE_SIMPLIE;
    private PrintBillBuilder billPrint;
    private List<PrintDataItem> escDataList = new ArrayList<>();

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextPrintIP = findViewById(R.id.ip);
                final String printIP = editTextPrintIP.getText().toString();
                if(TextUtils.isEmpty(printIP)) {
                    PrintLog.e(TAG, "Please input IP");
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        IpPrinterConfig config = new IpPrinterConfig();
                        config.ip = printIP;
                        config.port = 9100;
                        config.timeOut = 4;
                        config.paperSize = PAPER_SIZE;
                        config.paperType = PAPER_TYPE;
                        config.commandType = PRINTER_COMMAND;
                        initData(config);


                        try {
                            config.connect();
//                            config.write("ESC @".getBytes("GBK"));
//                            config.write("GS ! 2".getBytes("GBK"));
//
//                            config.write("测试打印\n".getBytes("GBK"));
//                            config.write("测试打印\n".getBytes("GBK"));
//                            config.write("测试打印\n".getBytes("GBK"));
//                            config.write("测试打印\n".getBytes("GBK"));
//
//                            config.write("GS V 42 0".getBytes("GBK"));
//                            config.write("ESC @".getBytes("GBK"));

                            //config.write("皆さん、こんにちは、このプリンターは調整しにくいですね。\n\n\n\n\n".getBytes("utf-8"));
//                            config.write(new byte[]{0x4E, 0x00});
                            config.write("逢葵茜\n\n\n\n\n".getBytes("shift-jis"));
                            config.write("しにくいですね\n\n\n\n\n".getBytes("JIS"));



                            config.closeConnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        PrinterManager printer1 = new PrinterManager(config, false);
//                        printer1.setData(getPrintData());
//
//                        //printer1.setData(templetProcessor.buildCommand(config).billPrint.data);-
//
//                        analazyPrintResult(printer1.start(""));

                    }
                }).start();


            }
        });



    }

    public void initData(PrinterConfig config) {
        JSONObject ob = new JSONObject();

        try {
            JSONObject shop = new JSONObject();

            shop.put("fsShopName", "测试小票打印(测试分店)超长的门店名称再额外加长并且更长再长");
            ob.put("Shop", shop);


            JSONObject sell = new JSONObject();
            sell.put("fsselldate", "2016-08-08");
            sell.put("shiftname", "全天大班别");
            sell.put("fssellno", "201608080001");
            sell.put("fsMealNumber", "1");
            sell.put("cashiername", "李店长");
            sell.put("fscheckendtime", "20:35");
            sell.put("fddiscountamt", "0");
            sell.put("fdexpamt", "9999.99");
            sell.put("fsnote", "这里是备注,整单加辣,加鸡腿,加火腿,加各种好东西");
            sell.put("fiBillKind", "8");
            ob.put("Sell", sell);

            JSONArray sellOrder = new JSONArray();
            for (int i = 0; i < 3; i++) {
                JSONObject orderItem = new JSONObject();
                if (i == 2) {
                    orderItem.put("fsitemname", "检测菜品超长的名称能否正常");
                    orderItem.put("fdsettleprice", 999);
                    orderItem.put("qty", 223);
                    orderItem.put("fsOrderUint", "千克");
                    orderItem.put("fdsubtotal", 9999);
                } else {
                    orderItem.put("fsitemname", "测试菜品一");
                    orderItem.put("fdsettleprice", "1");
                    orderItem.put("qty", 323);
                    orderItem.put("fsOrderUint", "公斤");
                    orderItem.put("fdsubtotal", "999");
                }

                JSONArray SLIT = new JSONArray();
                if (i == 2) {
                    for (int j = 0; j < 1; j++) {
                        JSONObject orderSLITItem = new JSONObject();
                        orderSLITItem.put("fsitemname", "京酱肉丝");
                        orderSLITItem.put("qty", "1");
                        SLIT.put(orderSLITItem);
                    }
                }
                orderItem.put("SLIT", SLIT);
                sellOrder.put(orderItem);
            }
            ob.put("sellOrder", sellOrder);

            JSONObject sub = new JSONObject();
            sub.put("qty", "9999");
            sub.put("total", "99999.99");
            ob.put("Sub", sub);

            JSONArray sellReceive = new JSONArray();
            JSONObject orderItem = new JSONObject();
            orderItem.put("paymentname", "现金");
            orderItem.put("fdpaymoney", "100");

            sellReceive.put(orderItem);
            orderItem = new JSONObject();
            orderItem.put("paymentname", "找零");
            orderItem.put("fdpaymoney", "20");
            sellReceive.put(orderItem);
            ob.put("SellReceive", sellReceive);

        } catch (JSONException e) {
            e.printStackTrace();
        }
//
        billPrint = new PrintBillBuilder(config);
//
//        billPrint.addLogo(BitmapFactory.decodeResource(getResources(), R.drawable.test));
////        billPrint.addLogo(BitmapFactory.decodeResource(getResources(), R.mipmap.icon));
//
//        billPrint.addTitle(ob.optJSONObject("Shop").optString("fsShopName"));
//        billPrint.addBlankLine();
//        billPrint.addTitle("结账单");
////        billPrint.addBlankLine();
//        JSONObject sell = ob.optJSONObject("Sell");
////                billPrint.addOrderNoAndBillNo(sell);
//        String orderID = JsonUtil.getInfo(sell, "fssellno", String.class);
//        orderID = orderID.substring(orderID.length() - 4, orderID.length());
//        String orderStr = "单号:" + orderID;
//        String mealNO = JsonUtil.getInfo(sell, "fsMealNumber", String.class);
//        int mealNoInt = StringUtil.toInt(mealNO, -1);
//        String mealNoStr = "牌号:" + mealNO;
//        try {

//            if (mealNoInt > -1) {
////                billPrint.addText(orderStr , 1, PrintDataItem.ALIGN_LEFT, 0);
//
//                billPrint.addText(PrintStringUtil.padRight(orderStr, (int) (billPrint.charSize - PrintStringUtil.getStringLength(mealNoStr, billPrint.gbkSize, 2)), billPrint.gbkSize), 1, PrintDataItem.ALIGN_LEFT, 0, 1);
//                billPrint.addText(mealNoStr + "\n", 2, PrintDataItem.ALIGN_RIGHT, 0, 1);
//            } else {
//                billPrint.addText(orderStr + "\n", 2, PrintDataItem.ALIGN_LEFT, 1, 0);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        billPrint.addBlankLine();
//        String orderType = "堂食";
//        String fillKind = JsonUtil.getInfo(sell, "fiBillKind", String.class);
//        switch (fillKind) {
//            case "8":
//                orderType = "打包";
//                break;
//            case "9":
//                orderType = "外卖";
//                break;
//        }
//        billPrint.addLeftWithRight("日期:" + JsonUtil.getInfo(sell, "fsselldate", String.class), "类型:" + orderType);
//        billPrint.addBlankLine();
//        billPrint.addLeftWithRight("收银:" + JsonUtil.getInfo(sell, "cashiername", String.class), "结账时间:" + JsonUtil.getInfo(sell, "fscheckendtime", String.class));
//        billPrint.addHortionalLine();
////        billPrint.addCenterTextPaddingWithDash("居中");
//        billPrint.addOrderItem(
//                GlobalCache.getContext().getString(R.string.item),
//                "金额",
////                "",
//                GlobalCache.getContext().getResources().getString(R.string.qty),
//                GlobalCache.getContext().getResources().getString(R.string.total), 2);// ("Item Name", "QTY");
//        billPrint.addHortionalLine();

        JSONArray list = ob.optJSONArray("sellOrder");
//        for (int i = 0; i < list.length(); i++) {
//            JSONObject item = list.optJSONObject(i);
//            billPrint.addOrderItem(item.optString("fsitemname"),
//                    item.optString("fdsettleprice"),
////                    "",
//                    item.optString("qty") + "/" + item.optString("fsOrderUint"),
//                    item.optString("fdsubtotal"),
//                    2);
//            JSONArray slit = JsonUtil.getInfo(item, "SLIT", JSONArray.class);
//            if (slit != null && slit.length() > 0) {
//                for (int m = 0; m < slit.length(); m++) {
//                    JSONObject itemS = slit.optJSONObject(m);
//                    billPrint.addOrderModifier("-" + JsonUtil.getInfo(itemS, "fsitemname", String.class) + "*" + JsonUtil.getInfo(itemS, "qty", String.class), 1);
//                }
//            }
//            billPrint.addBlankLine(1);
//        }
//        billPrint.addHortionalLine();
//        JSONObject sub = ob.optJSONObject("Sub");
//        billPrint.addSub("消费合计", sub.optString("qty"), "￥" + sub.optString("total"));
//        billPrint.addSub("折扣", "", "￥" + sell.optString("fddiscountamt"));
//        billPrint.addSub("应收", "", sell.optString("fdexpamt"));
//        billPrint.addHortionaDoublelLine();
//
//        JSONArray payList = JsonUtil.getInfo(ob, "SellReceive", JSONArray.class);
//        for (int i = 0; i < payList.length(); i++) {
//            JSONObject item = payList.optJSONObject(i);
//            BigDecimal payAmount = JsonUtil.getInfo(item, "fdpaymoney", BigDecimal.class);
//            billPrint.addRedText(JsonUtil.getInfo(item, "paymentname", String.class) + ":￥" + payAmount + "\r\n");
//        }
//        billPrint.addHortionaDoublelLine();
//        billPrint.addBlankLine();
//        billPrint.addQRcode("http://qr.mwee.9now.net/qr/9rg70996ylu935", PrintDataItem.ALIGN_RIGHT);
//
//        billPrint.addBarCod("122*1134302*0003", PrintDataItem.ALIGN_CENTRE);
//        billPrint.addBarCod("122*1134302*105537", PrintDataItem.ALIGN_CENTRE);
//        billPrint.addCut();

//        billPrint.addBeep();
        billPrint.addBlankLine(5);
        billPrint.addTitle("制作单");
//        billPrint.addBarCod("122*1134302*0003", PrintDataItem.ALIGN_CENTRE);
//        billPrint.addBarCod("122*1134302*105537", PrintDataItem.ALIGN_CENTRE);
//        billPrint.addQRcode("1224324353addFoodBoxBarcode",PrintDataItem.ALIGN_CENTRE);
        billPrint.addHortionaDoublelLine();
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.optJSONObject(i);
            String left = JsonUtil.getInfo(item, "fsItemName", String.class);
            String right = JsonUtil.getInfo(item, "qty", String.class) + "/" + JsonUtil.getInfo(item, "fsOrderUint", String.class);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addBlankLine(1);

            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);
            billPrint.addItemNameWithUnit(left, right, 2);

            String note = JsonUtil.getInfo(item, "fsNote", String.class);
            if (!TextUtils.isEmpty(note)) {
                billPrint.addOrderModifier(note, 1);
            }
            String ingredientNote = JsonUtil.getInfo(item, "ingredientNotes", String.class);
            if (!TextUtils.isEmpty(ingredientNote)) {
                String[] ingredientArr = ingredientNote.split("\n");
                for (String ingredient : ingredientArr) {
                    billPrint.addOrderModifier(ingredient.replace("\n", ""), 1);
                }
            }
            billPrint.addBlankLine(1);
        }
        billPrint.addHortionalLine();
        billPrint.addCut();

        escDataList = billPrint.data;

//        JSONObject object = new JSONObject();
//        try {
//            object.put("shopname", "奶茶店(喫演示店)");
//            object.put("time", DateUtil.getCurrentDate("HH:mm"));
//            object.put("id", "单号:918");
//            object.put("num", "1大杯");
//            object.put("sequence", "1/1");
//            object.put("itemname", "珍珠奶茶(演示)");
//            object.put("takeaway", "外带");
////            object.put("itemnote", "五分糖;去冰;加珍珠;礼品包装;加奶");
//
//            object.put("itemnote", "五分糖;去冰;加珍珠;");
//
//            object.put("price", "13元");
//            object.put("phone", "电话：4008166477");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            tscDataList = buildPrintItem(object);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private List<PrintDataItem> getPrintData() {
        return billPrint.data;
    }

    private void analazyPrintResult(final PrintResult result) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    analazyPrintResult(result);
                }
            });
            return;
        }
        if (result != null && result.result != PrintResultCode.SUCCESS) {
            //
        }
    }
}