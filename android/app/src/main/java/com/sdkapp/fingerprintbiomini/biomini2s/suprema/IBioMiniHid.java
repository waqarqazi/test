/*
 * Copyright (C) 2017 Suprema Inc.
 */

package com.sdkapp.fingerprintbiomini.biomini2s.suprema;

import android.hardware.usb.UsbDevice;

import com.suprema.IUsbEventHandler.DisconnectionCause;

import com.sdkapp.fingerprintbiomini.biomini2s.suprema.hid.ICaptureCallback;

/**
 * IBioMiniHid
 * @author Suprema
 */
public interface IBioMiniHid {
    boolean Init(int index);
    int GetDeviceCount();
    boolean isEqual(Object dev);
    boolean activate(Object appContext, Object device);
    boolean activate();
    boolean deactivate(DisconnectionCause reason);
    boolean isInUse();

    String GetDeviceName();
    String GetDeviceSN();
    String GetDeviceFWVersion();
    String GetDeviceHWVersion();
    String GetDevicePath();
    int GetHidProtocolVersion(byte[] nVersion);
    int GetImageWidth();
    int GetImageHeight();
    int GetDeviceType();

    boolean SetCommandTimeout(int timeout);

    int HidCommand(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub, byte[] data);
    int HidCommand(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub_f1, Hid.Sub sub_f2, byte[] data);
    int HidCommand(Hid.Pac pac, Hid.Cmd cmd, int sub_f1, int sub_f2, byte[] data);

    byte[] GetHidEcho();
    byte[] GetHidEchoData();
    String GetErrorString(int res);

    byte[] ReceiveData(Hid.Sub type, int interval, int delay);
    int SendData(Hid.Sub type, byte[] data);

    int Init();
    int Uninit();
    int SetDevice(UsbDevice device);
    String GetVersionString();

    int CaptureSingle(byte[] pImage);
    int SetParameter(String sName, int nValue);
    int GetParameter(String sName, int[] nValue);
    int StartCapturing();
    int AutoCapture();
    int AbortCapturing();
    int SetCaptureCallback(ICaptureCallback callbackHandler);
    boolean IsCapturing();
    byte[] ReceiveData(Hid.Sub type);
    int GetTemplateQuality(int[] nQuality);

    int SaveBufferInPictureDirectory(byte[] outputBuf, String fileName, boolean bOverWrite);

    int DEVICE_TYPE_BMSS = 0;
    int DEVICE_TYPE_BMS2S = 1;

    // Parameters
    int UFA_TEMPLATE_TYPE_SUPREMA = 2001;
    int UFA_TEMPLATE_TYPE_ISO19794_2 = 2002;
    int UFA_TEMPLATE_TYPE_ANSI378 = 2003;

    int UFA_IMAGE_TYPE_RAW = 0;
    int UFA_IMAGE_TYPE_BMP = 1;
    int UFA_IMAGE_TYPE_ISO = 2;
    int UFA_IMAGE_TYPE_WSQ = 3;

    int UFA_SLEEP_MODE = 1;
    int UFA_WAKEUP_MODE = 2;

    // finger index definition
    //   1111 1001 1111
    //
    //   left      right
    //   hand      hand
    //       thumbs
    int UFA_FIDX_LEFT_LITTLE    = 0x800;
    int UFA_FIDX_LEFT_RING      = 0x400;
    int UFA_FIDX_LEFT_MIDDLE    = 0x200;
    int UFA_FIDX_LEFT_INDEX     = 0x100;
    int UFA_FIDX_LEFT_THUMB     = 0x080;

    int UFA_FIDX_UNDEFINE_0     = 0x040;
    int UFA_FIDX_UNDEFINE_1     = 0x020;

    int UFA_FIDX_RIGHT_LITTLE   = 0x001;
    int UFA_FIDX_RIGHT_RING     = 0x002;
    int UFA_FIDX_RIGHT_MIDDLE   = 0x004;
    int UFA_FIDX_RIGHT_INDEX    = 0x008;
    int UFA_FIDX_RIGHT_THUMB    = 0x010;

    int UFA_NORMAL_USER = 0;
    int UFA_POWER_USER  = 1;

    // Upper Nibble
    int UFA_UN_TEMPLATE_NO_ENC  = 0x00;	// no encryption
    int UFA_UN_TEMPLATE_ENC0     = 0x01;	// default encryption
    int UFA_UN_TEMPLATE_ENC1     = 0x02;	// user password encryption
    int UFA_UN_TEMPLATE_ENC2     = 0x03;	// private key encryption

}
