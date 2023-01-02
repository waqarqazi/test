/*
 * Copyright (C) 2017 Suprema Inc.
 */

package com.sdkapp.fingerprintbiomini.biomini2s.suprema;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.sdkapp.fingerprintbiomini.biomini2s.suprema.hid.BioMiniHid;

public abstract class BioMiniHidFactory implements IUsbEventHandler {

    private final String TAG = "BioMiniSDK";
    private int mDeviceLast = -1;

    private UsbManager mUsbManager;
    private final Context mApplicationContext;
    private static final String ACTION_USB_PERMISSION = "com.suprema.USB_PERMISSION";

    private class BioMiniDeviceEnum {
        private int mDeviceID;
        private UsbDevice mUsbDevice;
        IBioMiniHid mBioMiniDevice = null;

        BioMiniDeviceEnum(UsbDevice dev) {
            mUsbDevice = dev;
            mDeviceID = (++mDeviceLast);
        }
        UsbDevice usbDevice() { return mUsbDevice; }
        public int deviceID() {
            return mDeviceID;
        }
        IBioMiniHid createDevice() {
            if(mBioMiniDevice == null) {

                mBioMiniDevice = new BioMiniHid(mUsbManager);

                if(!mBioMiniDevice.activate(mApplicationContext, mUsbDevice)) return null;

                return mBioMiniDevice;
            }
            else if(!mBioMiniDevice.isInUse()){
                mBioMiniDevice.activate(mApplicationContext, mUsbDevice);

                return mBioMiniDevice;
            }
            return null;
        }
        IBioMiniHid getDevice() {
            return mBioMiniDevice;
        }
    }
    private List<BioMiniDeviceEnum> mActiveList = new ArrayList<BioMiniDeviceEnum>();
    private List<BioMiniDeviceEnum> mAvailableList = new ArrayList<BioMiniDeviceEnum>();


    private interface IPermissionListener {
        void onPermissionDenied(UsbDevice d);
    }

    private PermissionReceiver mPermissionReceiver = new PermissionReceiver(
            new IPermissionListener() {
                @Override
                public void onPermissionDenied(UsbDevice d) {
                    Log.e(TAG, "Permission denied on " + d.getDeviceId());
                }
            });

    private class PermissionReceiver extends BroadcastReceiver {
        private final IPermissionListener mPermissionListener;

        public PermissionReceiver(IPermissionListener permissionListener) {
            mPermissionListener = permissionListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            if (mApplicationContext != null) {
                mApplicationContext.unregisterReceiver(this);
                String action = intent.getAction();
                switch (action) {
                    case ACTION_USB_PERMISSION:
                        if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            UsbDevice dev = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            mPermissionListener.onPermissionDenied(dev);
                            onDeviceChange(DeviceChangeEvent.DEVICE_PERMISSION_DENIED, dev);
                        } else {
                            Log.d(TAG, "Permission granted");
                            UsbDevice dev = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            if (dev != null) {
                                mAvailableList.add(0, new BioMiniDeviceEnum(dev));
                                onDeviceChange(DeviceChangeEvent.DEVICE_ATTACHED, dev);
                                Log.d(TAG, "Device added : " + dev);
                            } else {
                                Log.e(TAG, "BioMini device not present!");
                            }
                        }
                        break;
                }
            }
        }
    }

    private BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                init();
            }
        }
    };

    private BroadcastReceiver mUsbDetachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call your method that cleans up and closes communication with the device
                    for(BioMiniDeviceEnum d : mAvailableList) {
                        if(d == null || d.usbDevice() == null || d.getDevice() == null) {
                            mAvailableList.remove(d);
                            Log.d(TAG , String.format(Locale.ENGLISH,"removed device handle(BioMiniDeviceEnum) Available device List size %d" , mAvailableList.size()));
                            continue;
                        }
                        if(d.usbDevice().getDeviceName().equals(device.getDeviceName())) {
                           // d.getDevice().deactivate(DisconnectionCause.USB_UNPLUGGED);
                            mAvailableList.remove(d);
                            Log.d(TAG , String.format(Locale.ENGLISH,"removed device handle(BioMiniDeviceEnum) Available device List size %d" , mAvailableList.size()));
                            onDeviceChange(DeviceChangeEvent.DEVICE_DETACHED, d.usbDevice());
                        }
                    }
                }
            }
        }
    };

    public BioMiniHidFactory(Activity parentActivity) {
        mApplicationContext = parentActivity.getApplicationContext();
        mUsbManager = (UsbManager) mApplicationContext.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        mApplicationContext.registerReceiver(mUsbAttachReceiver , filter);
        filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mApplicationContext.registerReceiver(mUsbDetachReceiver , filter);

        try {
            init();
        } catch (Exception e) {
            Log.e(TAG, "ERROR: BioMiniFactory init caught an exception : " + e);
        }
    }

    public void close() {
        mApplicationContext.unregisterReceiver(mUsbAttachReceiver);
        mApplicationContext.unregisterReceiver(mUsbDetachReceiver);
    }

    private boolean isSupportedDevice(UsbDevice dev) {
        int vid = dev.getVendorId();
        int pid = dev.getProductId();
        return vid == 0x16d1 && (pid == 0x0420 || pid == 0x0421);
    }

    private void enumerate(IPermissionListener listener) {
        if (mUsbManager != null) {
            HashMap<String, UsbDevice> devlist = mUsbManager.getDeviceList();
            for (UsbDevice d : devlist.values()) {
                if(isSupportedDevice(d)) {
                    if (!mUsbManager.hasPermission(d)) {
                        Log.d(TAG, "No permission permitted. Trying to get permission...");
                        listener.onPermissionDenied(d);
                    } else {

                        mAvailableList.add(0, new BioMiniDeviceEnum(d));
                        Log.d(TAG , String.format(Locale.ENGLISH,"Added device handle(BioMiniDeviceEnum) Available device List size %d" , mAvailableList.size()));
                        onDeviceChange(DeviceChangeEvent.DEVICE_ATTACHED, d);
                        Log.d(TAG, "Permission already granted. : " + d);
                        return;
                    }
                    Log.d(TAG, "Device added : " + d);
                }
            }
        }
    }

    synchronized private boolean init()
    {
        enumerate(new IPermissionListener() {
            @Override
            public void onPermissionDenied(UsbDevice d) {
                if (mApplicationContext != null && mUsbManager != null) {
                    PendingIntent pi = PendingIntent.getBroadcast(mApplicationContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    mApplicationContext.registerReceiver(mPermissionReceiver, new IntentFilter(ACTION_USB_PERMISSION));
                    mUsbManager.requestPermission(d, pi);
                }
            }
        });
        return true;
    }

    public int getDeviceCount()
    {
        return mAvailableList.size();
    }

    public IBioMiniHid getDevice(int i)
    {
        try {
            if (mAvailableList.size() >= 1) {
                return mAvailableList.get(i).createDevice();
            }
        } catch (Exception e) {
            Log.e(TAG, "CRITICAL ERROR while IBioMiniDevice.createDevice...");
            // do nothing.
        }
        return null;
    }
}
