package me.zhangpu.demo.print;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;

import me.zhangpu.demo.print.printer.PrintLog;

/**
 * Created by virgil on 2017/2/17.
 * @author virgil
 */

public class PrintPermissionRequestActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        checkNeedFinish();
    }

    private void checkNeedFinish() {
        Intent intent = getIntent();
        String action = intent.getAction();

        PrintLog.w("UsbConnector", "receiver action: " + action + ",boot elipsed=" + SystemClock.elapsedRealtime() / 1000);

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            //如果开机时间少于80秒，则返回
            if (SystemClock.elapsedRealtime() < 80 * 1000) {
                finish();
                return;
            }
            final UsbDevice device = intent.getParcelableExtra("device");
            UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            if (mUsbManager != null && mUsbManager.hasPermission(device)) {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }
}
