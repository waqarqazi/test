package com.sdkapp.fingerprintbiomini.biomini2s.suprema.hid;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.suprema.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import com.sdkapp.fingerprintbiomini.biomini2s.suprema.Hid;
import com.sdkapp.fingerprintbiomini.biomini2s.suprema.IBioMiniHid;
import com.sdkapp.fingerprintbiomini.biomini2s.suprema.IBioMiniInterops;
import com.sdkapp.fingerprintbiomini.biomini2s.suprema.usb.UsbHandlerAndroid;

public class BioMiniHid implements IBioMiniInterops, IBioMiniHid {
    private final static String TAG = "BioMini SDK";

    private boolean mIsInUse = false;

    // Utility Configurations
    private final static boolean g_bPrintLog = false;
    private final static boolean g_bPrintTimetags = false;

    // Device specific parameters
    private final static int SLIMS_IMAGE_WIDTH = 320;
    private final static int SLIMS_IMAGE_HEIGHT = 480;
    private final static int SLIM2S_IMAGE_WIDTH = 300;
    private final static int SLIM2S_IMAGE_HEIGHT = 400;

    // General constants
    private  final static int TRUE = 1;
    private  final static int FALSE = 0;

    // Permission controls
    private static final String ACTION_USB_PERMISSION = "com.supremahq.samples.USB_PERMISSION";

    // Android resources
    private final Context mApplicationContext;
    private UsbManager mUsbManager = null;
    private UsbDevice mDevice = null;
    private UsbHandlerAndroid mUsbHandler = null;

    // Buffers
    private final static int IMG_BUF_MAX = SLIMS_IMAGE_WIDTH * SLIMS_IMAGE_HEIGHT;
    private final static int IMG_INT_BUF_MAX = SLIMS_IMAGE_WIDTH * SLIMS_IMAGE_HEIGHT;

    private final byte[] m_ImageLast = new byte[IMG_INT_BUF_MAX];

    private boolean bIsAfterAbortCpaturing = true;
    private ICaptureCallback mCallbackHandler;
    private boolean bInitialized = false;
    private boolean bUninitSet = false;
    private boolean mDeviceFound = false;
    private boolean bUSBisdominated = false;
    private int mCurrentPID = 0;
    private Runnable mSLoop;
    private Runnable mAutoLoop;
    private Thread mUsbThread;
    private boolean bThreadFlag;
    private boolean bAbortCapturing = false;
    private boolean isCaptured = false;

    private boolean bHidProtocolv10 = false;

    private PermissionReceiver mPermissionReceiver = new PermissionReceiver(
            new IPermissionListener() {
                @Override
                public void onPermissionDenied(UsbDevice d) {
                    l("Permission denied on " + d.getDeviceId());
                }
            });

    public BioMiniHid(Activity parentActivity) {
        mApplicationContext = parentActivity.getApplicationContext();
        mUsbManager = (UsbManager) mApplicationContext.getSystemService(Context.USB_SERVICE);
    }

    public BioMiniHid(UsbManager usbManager) {
        mApplicationContext = null;
        mUsbManager = usbManager;
    }

    private void l(Object msg) {
        if (g_bPrintLog) {
            Log.d(TAG, ">==< " + msg.toString() + " >==<");
        }
    }

    private void printTimeTag(String msg) {
        if(g_bPrintTimetags) Log.v("TimeTag", "" + SystemClock.uptimeMillis() + ", " + msg);
    }

    /////////////////////////////////////////////////
    // Private functions for H/W & buffer control
    private void init() {
        enumerate(new IPermissionListener() {
            @Override
            public void onPermissionDenied(UsbDevice d) {
                if(mApplicationContext != null && mUsbManager != null) {
                    PendingIntent pi = PendingIntent.getBroadcast(
                            mApplicationContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    mApplicationContext.registerReceiver(
                            mPermissionReceiver, new IntentFilter(ACTION_USB_PERMISSION));
                    mUsbManager.requestPermission(d, pi);
                }
            }
        });
    }

    private void enumerate(IPermissionListener listener) {
        l("enumerating");
        if(mUsbManager != null) {
            HashMap<String, UsbDevice> devlist = mUsbManager.getDeviceList();
            for (UsbDevice d : devlist.values()) {
                l("Found device: " + String.format("0x%04X:0x%04X", d.getVendorId(), d.getProductId()));

                if (d.getVendorId() == 0x16d1 && (d.getProductId() == 0x0420 || d.getProductId() == 0x0421))
                {
                    mCurrentPID = d.getProductId();
                    mDeviceFound = true;
                    l("Device under: " + d.getDeviceName()) ;

                    if (!mUsbManager.hasPermission(d)) {
                        l("onPermissionDenied called");
                        listener.onPermissionDenied(d);
                    } else {
                        l("startHandler_enumerate");
                        mDevice = d;
                        return;
                    }
                    break;
                }
            }
        }

        if (!mDeviceFound) {
            l("no device found");
        }
    }

    private interface IPermissionListener {
        void onPermissionDenied(UsbDevice d);
    }

    private class PermissionReceiver extends BroadcastReceiver {
        private final IPermissionListener mPermissionListener;

        public PermissionReceiver(IPermissionListener permissionListener) {
            mPermissionListener = permissionListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            l("onReceive");
            if(mApplicationContext != null) {
                mApplicationContext.unregisterReceiver(this);
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                    if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        mPermissionListener.onPermissionDenied(
                                (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE));
                    } else {
                        l("Permission granted");
                        UsbDevice dev = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        l("device not present!");
                    }
                }
            }
        }
    }

    private int getTargetWidth() {
        switch (GetProductId()) {
            case 0x0420:    // slim S
                return SLIMS_IMAGE_WIDTH;
            case 0x0421:
                return SLIM2S_IMAGE_WIDTH;
        }
        return -1;
    }

    private int getTargetHeight() {
        switch (GetProductId()) {
            case 0x0420:    // slim S
                return SLIMS_IMAGE_HEIGHT;
            case 0x0421:
                return SLIM2S_IMAGE_HEIGHT;
        }
        return -1;
    }

    /////////////////////////////////////////////////
    // Public functions for H/W & buffer control
    // TODO : should be re-implemented

    public int GetProductId() {
        return mCurrentPID;
    }

    public static boolean IsSupported(int vid, int pid) {
        if(vid == 0x16d1) {
            if(pid == 0x0420 || pid == 0x0421) {
                return true;
            }
        }
        return false;
    }

    public int SetDevice(UsbDevice device) {
        if(mDevice == device) return Hid.Error.CTRL_SUCCESS.value();

        mDevice = device;
        Uninit();

        if(device == null) {
            mDeviceFound = false;
            mCurrentPID = 0;
            return Hid.Error.CTRL_SUCCESS.value();
        }

        if (device.getVendorId() == 0x16d1 &&
                (device.getProductId() == 0x0420 || device.getProductId() == 0x0421)) {
            l("Device under: " + device.getDeviceName());
            if (!mUsbManager.hasPermission(device)) {
                l("Permission denied");
                mDeviceFound = false;
                return Hid.Error.ECH_ERR_PERMISSION_DENIED.value();
            } else {
                l("Device assigned " + device);
                mCurrentPID = device.getProductId();
                mDevice = device;
                mDeviceFound = true;
                return Hid.Error.CTRL_SUCCESS.value();
            }
        }
        return Hid.Error.ECH_ERR_NO_DEVICE_FOUND.value();
    }

    public int Init() {

        if (bInitialized) {
            l("Init >> Already initialized");
            return Hid.Error.ECH_WRN_ALREADY_DONE.value();
        }

        if (mDevice == null) {
            l("Init >> No device found");
            return Hid.Error.ECH_ERR_NO_DEVICE_FOUND.value();
        }

        UsbDeviceConnection conn = mUsbManager.openDevice(mDevice);
        UsbEndpoint epIN = null, epOUT = null;

        if (conn == null) {
            // no permission...
            Uninit();
            return Hid.Error.ECH_ERR_PERMISSION_DENIED.value();
        }

        if (!conn.claimInterface(mDevice.getInterface(0), true)) {
            l("Can not connect device");
            return Hid.Error.ECH_ERR_GENERAL.value();
        }

        if (mDevice.getInterfaceCount() <= 0) {
            l("Device has no interface");
            return Hid.Error.ECH_ERR_GENERAL.value();
        }

        UsbInterface usbIf = mDevice.getInterface(0);
        l("USB interface count: " + usbIf.getEndpointCount());
        for (int i = 0; i < usbIf.getEndpointCount(); i++) {
            if(usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_INT) {
                l("EndPoint No (interrupt) : " + usbIf.getEndpoint(i).getEndpointNumber() +
                        ", " + usbIf.getEndpoint(i));

                if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
                    epIN = usbIf.getEndpoint(i);
                else
                    epOUT = usbIf.getEndpoint(i);
            }
        }

        mUsbHandler = new UsbHandlerAndroid(this, conn, epIN, epOUT, IMG_BUF_MAX);

        bInitialized = true;

        byte[] nHidProtocol = new byte[2];
        int Ret = GetHidProtocolVersion( nHidProtocol );
        if ( Ret == Hid.Error.CTRL_SUCCESS.value() ) {
            if ( nHidProtocol[1] == 1 && nHidProtocol[0] == 0 ) {
                bHidProtocolv10 = true;
            }
            l( String.format(Locale.US, "HidProtocol Version (%d.%d)", nHidProtocol[1], nHidProtocol[0]) );
        }
        l(GetDeviceName() + " : " + GetDeviceSN());

        return Hid.Error.CTRL_SUCCESS.value();
    }

    public int Uninit() {
        l("Uninitializing...");
        if (!bInitialized)
            return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();

        if (bUninitSet)
            return Hid.Error.ECH_ERR_GENERAL.value();

        bUninitSet = true;

        if (!bAbortCapturing) {
            AbortCapturing();
            l("Abort Capturing");

        } else {
            l("Capture is already aborted");
        }
        while (bUSBisdominated)
            ;

        bInitialized = false;

        mDevice = null;
        mCurrentPID = 0;

        mUsbHandler.close();
        mUsbHandler = null;

        bUninitSet = false;

        return Hid.Error.CTRL_SUCCESS.value();
    }


    /////////////////////////////////////////////////
    // IBioMiniHid implementation

    @Override
    public boolean Init(int index) {
        return false;   // not for android, java only
    }

    @Override
    public int GetDeviceCount() {
        return 0;   // not for android, java only
    }

    @Override
    public boolean isEqual(Object dev) {
        if(mDevice == null) return false;
        UsbDevice _dev = (UsbDevice)dev;
        return mDevice.getDeviceName().equals(_dev.getDeviceName());
    }

    @Override
    public boolean activate(Object appContext, Object device) {
        if(!mIsInUse) {
            if(SetDevice((UsbDevice) device) == 0) {
                mIsInUse = Init() == 0;
                return mIsInUse;
            }
        }
        return false;
    }

    @Override
    public boolean activate() {
        if(!mIsInUse && mDevice != null && mUsbHandler != null) {
            mIsInUse = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean deactivate(IUsbEventHandler.DisconnectionCause reason) {
        if(mIsInUse) {
            mIsInUse = false;
            mDevice = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean isInUse() {
        return false;
    }

    @Override
    public String GetDeviceName() {
        String dev_info = null;
        l("GetDeviceName Request");
        if(mUsbHandler != null) {
            dev_info = "N/A";
            int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                    Hid.Cmd.VAT_DEV_INFO, Hid.Sub.CCT_NA, null);
            if(Ret == 0) {
                dev_info = new String(mUsbHandler.getLastEchoData(), 0, 16).trim();
            }
            else {
                l("GetDeviceName error : " + Hid.errString(Ret));
            }
        }

        return dev_info;
    }

    @Override
    public String GetDeviceSN() {
        String dev_info = null;
        l("GetDeviceSN Request");
        if(mUsbHandler != null) {
            dev_info = "N/A";
            int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                    Hid.Cmd.VAT_DEV_INFO, Hid.Sub.CCT_NA, null);
            if(Ret == 0) {
                dev_info = new String(mUsbHandler.getLastEchoData(), 16, 16).trim();
            }
            else {
                l("GetDeviceSN error : " + Hid.errString(Ret));
            }
        }

        return dev_info;
    }

    @Override
    public String GetDeviceFWVersion() {
        String dev_info = null;
        l("GetDeviceFWVersion Request");
        if(mUsbHandler != null) {
            dev_info = "N/A";
            int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                    Hid.Cmd.VAT_DEV_INFO, Hid.Sub.CCT_NA, null);
            if(Ret == 0) {
                dev_info = new String(mUsbHandler.getLastEchoData(), 32, 8).trim();
            }
            else {
                l("GetDeviceFWVersion error : " + Hid.errString(Ret));
            }
        }

        return dev_info;
    }

    @Override
    public String GetDeviceHWVersion() {
        String dev_info = null;
        l("GetDeviceHWVersion Request");
        if(mUsbHandler != null) {
            dev_info = "N/A";
            int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                    Hid.Cmd.VAT_DEV_INFO, Hid.Sub.CCT_NA, null);
            if(Ret == 0) {
                dev_info = new String(mUsbHandler.getLastEchoData(), 40, 8).trim();
            }
            else {
                l("GetDeviceHWVersion error : " + Hid.errString(Ret));
            }
        }

        return dev_info;
    }

    @Override
    public int GetHidProtocolVersion(byte[] nVersion)
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_HID_PROTOCOL_VER, Hid.Sub.SUB_NA);
        if ( Ret == Hid.Error.CTRL_SUCCESS.value() ) {
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, nVersion, 0, 2);
        }

        return Ret;
    }

    public int GetUsbPacketMode(int[] mode)
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_USB_TYPE, Hid.Sub.SUB_NA);
        if (Ret == Hid.Error.CTRL_SUCCESS.value()) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 1);
            mode[0] = temp[0];
        }
        return Ret;
    }

    @Override
    public String GetDevicePath() {
        return (mDevice != null) ? mDevice.getDeviceName() : null;
    }

    @Override
    public boolean SetCommandTimeout(int timeout) {
        return false;
    }

    @Override
    public int HidCommand(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub, byte[] data) {
        if(!bInitialized) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        if(mUsbHandler != null) {
            return mUsbHandler.hidCommand(pac, cmd, sub, data);
        }
        else {
            return Hid.Error.ECH_ERR_GENERAL.value();
        }
    }

    @Override
    public int HidCommand(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub_f1, Hid.Sub sub_f2, byte[] data) {
        if(!bInitialized) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        if(mUsbHandler != null) {
            return mUsbHandler.hidCommand(pac, cmd, sub_f1.value(), sub_f2.value(), data);
        }
        else {
            return Hid.Error.ECH_ERR_GENERAL.value();
        }
    }

    @Override
    public int HidCommand(Hid.Pac pac, Hid.Cmd cmd, int sub_f1, int sub_f2, byte[] data) {
        if(!bInitialized) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        if(mUsbHandler != null) {
            return mUsbHandler.hidCommand(pac, cmd, sub_f1, sub_f2, data);
        }
        else {
            return Hid.Error.ECH_ERR_GENERAL.value();
        }
    }

    @Override
    public byte[] GetHidEcho() {
        return mUsbHandler.getLastEcho();
    }

    @Override
    public byte[] GetHidEchoData() {
        return mUsbHandler.getLastEchoData();
    }

    @Override
    public byte[] ReceiveData(Hid.Sub type, int interval, int delay) {
        if(!bInitialized) return null;
        if(mUsbHandler != null) {
            byte[] extra = new byte[8];
            Hid.putInt(extra, 0, interval);
            Hid.putInt(extra, 4, delay);
            int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                    Hid.Cmd.CMT_RECEIVE_DATA, type, extra);
            if (Ret == 0) {
                byte[] buf = new byte[mUsbHandler.hidReceiveSize()];
                if (mUsbHandler.hidReceive(buf)) {
                    return buf;
                } else {
                    // Sometimes we lose some data while receiving...
                    // Most of lost data could recovered by 2nd try...
                    // TODO: you may need to check whole data which is not transferred
                    Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                            Hid.Cmd.CMT_RECEIVE_DATA, type, extra);
                    if (Ret == 0) {
                        mUsbHandler.hidReceive(buf);
                        return buf; // return anyway
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int SendData(Hid.Sub type, byte[] data) {
        if(!bInitialized) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        if(mUsbHandler != null) {

            byte[] info = new byte[64];
            Arrays.fill(info, 0, 64, (byte) 0);
            Hid.putInt( info, 0, data.length );
            int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                    Hid.Cmd.CMT_SEND_DATA, type, info);
            if(Ret == 0 && !mUsbHandler.hidSend(data, data.length)) {
                Ret = Hid.Error.ECH_ERR_USB_IO.value();
            }
            return Ret;
        }
        else {
            return Hid.Error.ECH_ERR_GENERAL.value();
        }
    }

    @Override
    public boolean isOnDestroying() {
        return bUninitSet;
    }

    @Override
    public int GetImageWidth() {
        return getTargetWidth();
    }

    @Override
    public int GetDeviceType() {
        switch (GetProductId()) {
            case 0x0420:
                return DEVICE_TYPE_BMSS;
            case 0x0421:
                return DEVICE_TYPE_BMS2S;
        }
        return -1;
    }

    @Override
    public int GetImageHeight() {
        return getTargetHeight();
    }

    @Override
    public String GetErrorString(int res) {
        return Hid.errString(res);
    }


    /////////////////////////////////////////////////
    // Android specific functions (function implementation shortcut)
    @Override
    public int CaptureSingle(byte[] pImage) {
        l("Capturing single image...");
        Arrays.fill(pImage, (byte)255);

        int nTargetWidth = getTargetWidth();
        int nTargetHeight = getTargetHeight();

        if (!bInitialized || mUsbHandler == null) {
            l("no init");
            return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        }

        if (bUSBisdominated) {
            l("handle busy");
            return Hid.Error.CTRL_ERR_IS_CAPTURING.value();
        }

        bUSBisdominated = true;
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                Hid.Cmd.CMT_CAPTURE, Hid.Sub.CCT_SINGLE);
        if(Ret == 0) {
            Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                    Hid.Cmd.CMT_RECEIVE_DATA, Hid.Sub.DBI_CAPTURED_IMAGE);
            if(Ret == 0 && !mUsbHandler.hidReceive(pImage)) {
                Ret = Hid.Error.ECH_ERR_USB_IO.value();
            }
        }
        else {
            l("CaptureSingle error : " + Hid.errString(Ret));
        }
        bUSBisdominated = false;
        return Ret;
    }

    public byte[] ReceiveData(Hid.Sub type) {
        return ReceiveData(type, 0, 0);
    }

    @Override
    public int SetParameter(String sName, int nValue) {
        if (!bInitialized)
            return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();

        if (bUSBisdominated)
            return Hid.Error.CTRL_ERR_CAPTURE_IS_NOT_RUNNING.value();

        switch (sName.toLowerCase()) {
            case "timeout":
                if (nValue < 0) nValue = 0;
                if ( nValue > 60000) nValue = 60000;
                return SetTimeout( nValue );
            case "sensitivity":
                if (nValue < 0) nValue = 0;
                if (nValue > 7) nValue = 7;
                return SetSensitivity( nValue );
            case "security_level":
                if (nValue < 0) nValue = 0;
                if (nValue > 7) nValue = 7;
                return SetSecurityLevel( nValue );
            case "wsq_comp_ratio":
                if (nValue < 10*0.1) nValue = (int) (10*0.1);
                if (nValue > 10*7.5) nValue = (int) (10*7.5);
                return SetWSQCompRatio( nValue );
            case "lfd_level":
                if (nValue < 0) nValue = 0;
                if (nValue > 7) nValue = 7;
                return SetLfdLevel( nValue );
            case "template_type":
                if ( nValue < UFA_TEMPLATE_TYPE_SUPREMA ) nValue = UFA_TEMPLATE_TYPE_SUPREMA;
                if ( nValue > UFA_TEMPLATE_TYPE_ANSI378 ) nValue = UFA_TEMPLATE_TYPE_ANSI378;
                return SetTemplateType( nValue );
            case "ext_trigger":
                boolean bExtOn;
                if (nValue == 0) bExtOn = false;
                else bExtOn = true;
                return SetExtTrigger(bExtOn);
            case "image_type":
                if (nValue < UFA_IMAGE_TYPE_RAW) nValue = UFA_IMAGE_TYPE_RAW;
                if (nValue > UFA_IMAGE_TYPE_WSQ) nValue = UFA_IMAGE_TYPE_WSQ;
                return SetImageType(nValue);
            case "sleep_mode":
                if (nValue == UFA_SLEEP_MODE || nValue == UFA_WAKEUP_MODE) return SetSleepMode(nValue);
                else return Hid.Error.ECH_ERR_INVALID_PARAMETER.value();
            default:
                break;
        }

        return Hid.Error.ECH_ERR_INVALID_PARAMETER.value();
    }

    @Override
    public int GetParameter(String sName, int[] nValue) {
        if (!bInitialized)
            return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();

        switch (sName.toLowerCase()) {
            case "timeout":
                return GetTimeout( nValue );
            case "sensitivity":
                return GetSensitivity( nValue );
            case "security_level":
                return GetSecurityLevel( nValue );
            case "wsq_comp_ratio":
                return GetWSQCompRatio( nValue );
            case "lfd_level":
                return GetLfdLevel( nValue );
            case "template_type":
                return GetTemplateType( nValue );
            case "ext_trigger":
                return GetExtTrigger( nValue );
            case "image_type":
                return GetImageType( nValue );
            case "sleep_mode":
                return GetSleepMode( nValue );
            default:
                break;
        }

        return Hid.Error.ECH_ERR_INVALID_PARAMETER.value();
    }

    /////////////////////////////////////////////////
    // Android specific functions (for preview)
    public int StartCapturing() {
        l("Start capturing...");

        if(GetProductId() == 0x0421) {
            return Hid.Error.ECH_ERR_INVALID_COMMAND.value();
        }

        if (IsCapturing()) {
            l("Cannot start capturing, another capturing is going...");
            return Hid.Error.CTRL_ERR_CAPTURE_IS_NOT_RUNNING.value();
        }

        if (!bInitialized || mUsbHandler == null) {
            l("Not initialized");
            return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        }

        if (bUSBisdominated) {
            l("Handle is busy");
            return Hid.Error.CTRL_ERR_CAPTURE_IS_NOT_RUNNING.value();
        }

        if ( bAbortCapturing ) {
            l("Abort Capturing");
            return Hid.Error.CTRL_ERR_FAIL.value();
        }

        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                Hid.Cmd.CMT_CAPTURE, Hid.Sub.CCT_PREVIEW);
        if(Ret == 0) {
            SystemClock.sleep(100);
            bThreadFlag = true;
            bUSBisdominated = true;
            bAbortCapturing = false;
            mSLoop = new CapturingHidLoop();
            mUsbThread = new Thread(mSLoop);
            mUsbThread.start();
        }
        else {
            l("StartCapturing error : " + Hid.errString(Ret));
        }
        return Ret;
    }

    public int AutoCapture() {
        l("Auto Capture start...");

        if (IsCapturing()) {
            l("Cannot auto capture, another capturing is going...");
            return Hid.Error.ECH_ERR_ABNORMAL_STATE.value();
        }

        if (!bInitialized || mUsbHandler == null) {
            l("Not initialized");
            return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        }

        if (bUSBisdominated) {
            l("Handle is busy");
            return Hid.Error.ECH_ERR_ABNORMAL_STATE.value();
        }

        if ( bAbortCapturing ) {
            l("Abort Capturing");
            return Hid.Error.CTRL_ERR_FAIL.value();
        }

        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                Hid.Cmd.CMT_CAPTURE, Hid.Sub.CCT_LOOP);
        if(Ret == 0) {
            SystemClock.sleep(100);
            bThreadFlag = true;
            bUSBisdominated = true;
            bAbortCapturing = false;
            mAutoLoop = new AutoCaptureHidLoop();
            mUsbThread = new Thread(mAutoLoop);
            mUsbThread.start();
        }
        else {
            l("AutoCapture error : " + Hid.errString(Ret));
        }
        return Ret;
    }


    public boolean IsCapturing() {

        return bUSBisdominated;
    }

    private class CapturingHidLoop implements Runnable {

        @Override
        public void run() {
            l("CapturingHidLoop Init ");
            int nTargetWidth = getTargetWidth();
            int nTargetHeight = getTargetHeight();

            while (bThreadFlag) {

                if (bAbortCapturing) {
                    break;
                }

                isCaptured = false;
                int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                        Hid.Cmd.CMT_RECEIVE_DATA, Hid.Sub.DBI_PREVIEW_IMAGE);
                if(Ret == 0) {
                    if(mUsbHandler.hidReceive(m_ImageLast)) {
                    }
                    else {
                        Ret = -1;
                    }
                }
                else {
                    l("Receiving preview data error : " + Hid.errString(Ret));
                }

                if(Ret == Hid.Error.CTRL_ERR_CAPTURE_IS_NOT_RUNNING.value()) {
                    l("Receiving captured image data error : " + Hid.errString(Ret) );
                    if (mCallbackHandler != null)
                        mCallbackHandler.onError("StartCapturing failed : Capture is not running");
                    break;
                }

                if (Ret == Hid.Error.CTRL_ERR_CAPTURE_TIMEOUT.value()) {
                    l("Receiving captured image data error : " + Hid.errString(Ret));
                    if (mCallbackHandler != null)
                        mCallbackHandler.onError("StartCapturing failed : Capture timeout");
                    break;
                }

                if (Ret== Hid.Error.CTRL_ERR_FAKE_FINGER.value()) {
                    l("Receiving captured image data error : " + Hid.errString(Ret));
                    if (mCallbackHandler != null)
                        mCallbackHandler.onError("StartCapturing failed : Fake finger");
                    break;
                }

                if (mCallbackHandler != null)
                    mCallbackHandler.onCapture(m_ImageLast, nTargetWidth, nTargetHeight, 500, isCaptured);

            } // end of while

            bThreadFlag = false;
            bAbortCapturing = false;
            //l("Capturing thread end");
            l("CapturingHidLoop end ");

            CaptureFrameStop();
        }
    }

    private class AutoCaptureHidLoop implements Runnable {

        @Override
        public void run() {
            l("AutoCaptureHidLoop Init ");
            int nTargetWidth = getTargetWidth();
            int nTargetHeight = getTargetHeight();

            while (bThreadFlag) {

                if (bAbortCapturing) {
                    break;
                }

                int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                        Hid.Cmd.VAT_IS_STREAM_UPDATE, Hid.Sub.SUB_NA);

                // need to sleep to prevent too much command calling
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if ( Ret == Hid.Error.CTRL_SUCCESS.value() ) {
                    byte[] temp = new byte[8];
                    System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 1);
                    if(temp[0] == 1) {
                        // last image updated (fingerprint captured!)
                        Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD, Hid.Cmd.CMT_RECEIVE_DATA, Hid.Sub.DBI_CAPTURED_IMAGE);

                        if(Ret == 0) {
                            if(mUsbHandler.hidReceive(m_ImageLast)) {
                                if (mCallbackHandler != null)
                                    mCallbackHandler.onCapture(m_ImageLast, nTargetWidth, nTargetHeight, 500, true);
                            }
                            else {
                                Ret = -1;
                            }
                        }
                        else {
                            l("Receiving last captured data error : " + Hid.errString(Ret));
                        }
                    }
                }



            } // end of while

            bThreadFlag = false;
            bAbortCapturing = false;
            //l("Capturing thread end");
            l("CapturingHidLoop end ");

            CaptureFrameStop();
        }
    }


    private void CaptureFrameStop() {
        l("Stops capturing...");

        if (mUsbThread != null) {
            mUsbThread.interrupt();
            mUsbThread = null;
        }

        mSLoop = null;
        mUsbThread = null;

        bUSBisdominated = false;
        l("Capture stopped");
    }

    public int AbortCapturing() {
        int nResult = Hid.Error.CTRL_SUCCESS.value();

        if (!bIsAfterAbortCpaturing)
            return Hid.Error.CTRL_SUCCESS.value();
        if (!bInitialized || mUsbHandler == null) {
            l("no init");
            return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        }
        if (!IsCapturing()) {
            return Hid.Error.CTRL_ERR_CAPTURE_IS_NOT_RUNNING.value();
        }

        if ( bAbortCapturing )
            return Hid.Error.CTRL_ERR_FAIL.value();

        bAbortCapturing = true;
        bIsAfterAbortCpaturing = true;
        SystemClock.sleep( 300 );

        nResult = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                Hid.Cmd.CMT_CAPTURE, Hid.Sub.CCT_STOP);
        if(nResult != Hid.Error.CTRL_SUCCESS.value()) {
            l("AbortCaptuging error : " + Hid.errString(nResult));
        }
        l("abort return");

        // 20141013
        if (mUsbThread != null) {
            l("interrupt");
            mUsbThread.interrupt();
            mUsbThread = null;
        }

        return nResult;
    }

    public int SetCaptureCallback(ICaptureCallback callbackHandler) {
        if (!bInitialized)
            return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();

        if (bUSBisdominated) {
            l("Device handle busy");
            return Hid.Error.CTRL_ERR_CAPTURE_IS_NOT_RUNNING.value();
        }

        mCallbackHandler = callbackHandler;

        return Hid.Error.CTRL_SUCCESS.value();
    }

    /////////////////////////////////////////////////
    // Android specific functions (for utility)
    public int ClearCaptureImageBuffer() {
        if (!bInitialized) {
            l("no init");
            return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        }

        Arrays.fill(m_ImageLast, 0, getTargetWidth() * getTargetHeight(), (byte) 255);
        isCaptured = false;
        l("set isCaptured false(ClearCaptureImageBuffer)");

        return Hid.Error.CTRL_SUCCESS.value();
    }

    @Override
    public String GetVersionString() {
        return "BioMini HID SDK for Android v1.0.009";
    }

    /////////////////////////////////////////////////
    // Utility functions
    @Override
    public int SaveBufferInPictureDirectory(byte[] outputBuf, String fileName, boolean bOverWrite) {

        int ufa_res = Hid.Error.CTRL_SUCCESS.value();
        if (outputBuf == null)
            return Hid.Error.ECH_ERR_INVALID_PARAMETER.value();

        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
        if (!f.exists() && !f.mkdir()) {
            return Hid.Error.ECH_ERR_GENERAL.value();
        }
        String fileNameNew = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + fileName;

        f = new File(fileNameNew);
        if (!f.exists()) {
            try {
                if (!f.createNewFile()) {
                    return Hid.Error.ECH_ERR_GENERAL.value();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return Hid.Error.ECH_ERR_GENERAL.value();
            }
        } else {
            if (bOverWrite) {
                try {
                    if (!f.delete() || !f.createNewFile()) {
                        return Hid.Error.ECH_ERR_PERMISSION_DENIED.value();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return Hid.Error.ECH_ERR_PERMISSION_DENIED.value();
            }
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(f);
            os.write(outputBuf);
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Hid.Error.ECH_ERR_GENERAL.value();
        } catch (Exception e) {
            return Hid.Error.ECH_ERR_GENERAL.value();
        }

        return ufa_res;
    }

    /////////////////////////////////////////////////
    // Parameter setting functions
    private int SetTimeout( int nTimeout )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        byte[] info = new byte[64];

        Arrays.fill(info, 0, 64, (byte) 0);
        Hid.putShort( info, 0, nTimeout );
        return mUsbHandler.hidCommand(Hid.Pac.PAC_SET,
                Hid.Cmd.VAT_CAPTURE_OPT, Hid.Sub.CVT_TIMEOUT, info);
    }

    private int SetLfdLevel( int nLevel )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        byte[] info = new byte[64];

        Arrays.fill(info, 0, 64, (byte) 0);
        info[0] = (byte)nLevel;
        return mUsbHandler.hidCommand(Hid.Pac.PAC_SET,
                Hid.Cmd.VAT_CAPTURE_OPT, Hid.Sub.CVT_LFD_LEVEL, info);
    }

    private int SetSecurityLevel( int nLevel )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        byte[] info = new byte[64];

        Arrays.fill(info, 0, 64, (byte) 0);
        info[0] = (byte)nLevel;
        return mUsbHandler.hidCommand(Hid.Pac.PAC_SET,
                Hid.Cmd.VAT_VERIFIY_OPT, Hid.Sub.VVT_SECURITY_LEVEL, info);
    }

    private int SetSensitivity(int nSensitivity )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        byte[] info = new byte[64];

        Arrays.fill(info, 0, 64, (byte) 0);
        info[0] = (byte)nSensitivity;
        return mUsbHandler.hidCommand(Hid.Pac.PAC_SET,
                Hid.Cmd.VAT_CAPTURE_OPT, Hid.Sub.CVT_SENSITIVITY, info);
    }

    /**
     * SetWSQCompRatio
     * @param nCompRatio WSQ compression ratio in integer (will be normalized by 256, 256 -> 1.0f eg.
     * @return error code
     */
    private int SetWSQCompRatio(int nCompRatio) {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        byte[] info = Arrays.copyOf(Float.toString((float)nCompRatio / 10.f).getBytes(Charset.forName("UTF-8")), 8);
        return mUsbHandler.hidCommand(Hid.Pac.PAC_SET,
                Hid.Cmd.VAT_IMAGE_OPT, Hid.Sub.IVT_COMPRESS_RATIO, info);
    }

    private int SetTemplateType(int nTemplateType )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        byte[] info = new byte[64];

        Arrays.fill(info, 0, 64, (byte) 0);
        Hid.putShort( info, 0, nTemplateType );
        return mUsbHandler.hidCommand(Hid.Pac.PAC_SET,
                Hid.Cmd.VAT_TEMPLATE_OPT, Hid.Sub.TVT_TEMPLATE_FORMAT, info);
    }

    private int SetImageType(int nImageType)
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        byte[] info = new byte[64];

        Arrays.fill(info, 0, 64, (byte)0);
        info[0] = (byte) nImageType;
        return mUsbHandler.hidCommand(Hid.Pac.PAC_SET,
                Hid.Cmd.VAT_IMAGE_OPT, Hid.Sub.IVT_IMAGE_FORMAT, info);
    }

    private int SetExtTrigger(boolean bOn)
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        byte[] info = new byte[64];

        Arrays.fill(info, 0, 64, (byte) 0);
        if(bOn) info[0] = 1;
        else info[0] = 0;
        return mUsbHandler.hidCommand(Hid.Pac.PAC_SET,
                Hid.Cmd.VAT_CAPTURE_OPT, Hid.Sub.CVT_EX_TRIGGER, info);
    }

    private int SetSleepMode(int nMode)
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        byte[] info = new byte[64];

        Arrays.fill(info, 0, 64, (byte)0);
        info[0] = (byte) nMode;
        return mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                Hid.Cmd.CMT_DEVICE_CTRL, Hid.Sub.CDT_SET_SLEEP_MODE, info);
    }

    private int GetTimeout(int[] nTimeout )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_CAPTURE_OPT, Hid.Sub.CVT_TIMEOUT);
        if ( Ret == Hid.Error.CTRL_SUCCESS.value() ) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 2);
            nTimeout[0] = Hid.retrieveShort(temp, 0 );
        }
        return Ret;
    }

    private int GetLfdLevel(int[] nLevel )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_CAPTURE_OPT, Hid.Sub.CVT_LFD_LEVEL);
        if ( Ret == Hid.Error.CTRL_SUCCESS.value() ) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 1);
            nLevel[0] = temp[0];
        }
        return Ret;
    }

    private int GetSecurityLevel(int[] nLevel )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_VERIFIY_OPT, Hid.Sub.VVT_SECURITY_LEVEL);
        if ( Ret == Hid.Error.CTRL_SUCCESS.value() ) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 1);
            nLevel[0] = temp[0];
        }
        return Ret;
    }

    private int GetSensitivity(int[] nSensitivity )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_CAPTURE_OPT, Hid.Sub.CVT_SENSITIVITY);
        if ( Ret == Hid.Error.CTRL_SUCCESS.value() ) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 1);
            nSensitivity[0] = temp[0];
        }
        return Ret;
    }

    /**
     * GetWSQCompRatio
     * @param nCompRatio WSQ compression ratio in integer (will be normalized by 256, 256 -> 1.0f eg.
     * @return error code
     */
    private int GetWSQCompRatio(int[] nCompRatio )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_IMAGE_OPT, Hid.Sub.IVT_COMPRESS_RATIO);
        if ( Ret == Hid.Error.CTRL_SUCCESS.value() ) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 8);
            try {
                String out_str = new String(temp, "US-ASCII").replaceAll("[^0-9.]", "");
                nCompRatio[0] = (int)(Float.parseFloat(out_str) * 10);
                l("nCompRatio[0] : " + nCompRatio[0] );
            }
            catch(Exception e){
                return Hid.Error.ECH_ERR_GENERAL.value();
            }
        }
        return Ret;
    }

    private int GetTemplateType(int[] nTemplateType )
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_TEMPLATE_OPT, Hid.Sub.TVT_TEMPLATE_FORMAT);
        if ( Ret == Hid.Error.CTRL_SUCCESS.value() ) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 2);
            nTemplateType[0] = Hid.retrieveShort(temp, 0 );
        }
        return Ret;
    }

    private int GetImageType(int[] nImageType)
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_IMAGE_OPT, Hid.Sub.IVT_IMAGE_FORMAT);
        if(Ret == Hid.Error.CTRL_SUCCESS.value()) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 1);
            nImageType[0] = temp[0];
        }
        return Ret;
    }

    private int GetExtTrigger(int[] bOn)
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_GET,
                Hid.Cmd.VAT_CAPTURE_OPT, Hid.Sub.CVT_EX_TRIGGER);
        if (Ret == Hid.Error.CTRL_SUCCESS.value()) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 1);
            bOn[0] = temp[0];
        }
        return Ret;
    }

    private int GetSleepMode(int[] nMode)
    {
        if(mUsbHandler == null) return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();
        int Ret = mUsbHandler.hidCommand(Hid.Pac.PAC_CMD,
                Hid.Cmd.CMT_DEVICE_CTRL, Hid.Sub.CDT_GET_SLEEP_MODE);
        if(Ret == Hid.Error.CTRL_SUCCESS.value()) {
            byte[] temp = new byte[8];
            System.arraycopy(mUsbHandler.getLastEchoData(), 0, temp, 0, 1);
            nMode[0] = temp[0];
        }
        return Ret;
    }

    public int GetTemplateQuality(int[] nQuality ) {
        if (!bInitialized || mUsbHandler == null)  return Hid.Error.ECH_ERR_NOT_INITIALIZED.value();;
        byte[] temp = new byte[8];
        System.arraycopy(mUsbHandler.getLastEcho(), 4, temp, 0, 1); // template quality is after data_size field (4bytes)
        nQuality[0] = (int) temp[0];
        return Hid.Error.CTRL_SUCCESS.value();
    }
}
