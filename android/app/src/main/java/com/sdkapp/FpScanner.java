package com.sdkapp;
import android.app.PendingIntent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.suprema.BioMiniFactory;
import com.suprema.IBioMiniDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.devkit.api.Misc;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.suprema.CaptureResponder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
public class FpScanner extends ReactActivity {
    private Button buttonScan;
    private Button submitScan;
    private static ReactApplicationContext reactApplicationContext;
    private DeviceEventManagerModule.RCTDeviceEventEmitter mEmitter=null;
    public static final boolean mbUsbExternalUSBManager = false;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static  String test = null;
    private UsbManager mUsbManager = null;
    private PendingIntent mPermissionIntent = null;
    private static BioMiniFactory mBioMiniFactory = null;
    public static final int REQUEST_WRITE_PERMISSION = 786;
    public IBioMiniDevice mCurrentDevice = null;
    private FpScanner mainContext;
    public final static String TAG = "BioMini Sample";
    private EditText mLogView;
    private ScrollView mScrollLog = null;
    private static final int VERI=1;
    private static final int CAPT=2;
    private int flag=0;
    private static ReactApplicationContext reactContext;
    public static  String base64=null;
    private IBioMiniDevice.CaptureOption mCaptureOptionDefault = new IBioMiniDevice.CaptureOption();
    public static int getSDKVersionCode() {
        return Build.VERSION.SDK_INT;
    }
    synchronized public void printRev(final String msg) {
        log(msg);
    }


    /**
     *usb permission request
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            if (mBioMiniFactory == null) return;
                            mBioMiniFactory.addDevice(device);
                            log(String.format(Locale.ENGLISH, "Initialized device count- BioMiniFactory (%d)", mBioMiniFactory.getDeviceCount()));
                        }
                    } else {
                        Log.e(TAG, "permission denied for device" + device);
                    }
                }
            }
        }
    };
    synchronized public void printState(final CharSequence str) {
        log(str.toString());

    }
    private CaptureResponder mCaptureResponseDefault = new CaptureResponder() {
        @Override
        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                   final IBioMiniDevice.FingerState fingerState) {
            flag=1;
            log("onCapture : Capture successful!");
            printState(getResources().getText(R.string.capture_single_ok));
            byte[] raw = mCurrentDevice.getCaptureImageAsRAW_8();
            int quailty = mCurrentDevice.getFPQuality(raw,capturedImage.getWidth(),capturedImage.getHeight(),0);
            log("image NFIQ :"+quailty);
            log(((IBioMiniDevice) context).popPerformanceLog());
            runOnUiThread((Runnable) () -> {
                if (capturedImage != null) {
                    ImageView iv = (ImageView) findViewById(R.id.imagePreview);
                    if (iv != null) {
                        iv.setImageBitmap(capturedImage);
                    }
                }
            });

            return true;
        }

        @Override
        public void onCaptureError(Object contest, int errorCode, String error) {
            log("onCaptureError : " + error);
            if (errorCode != IBioMiniDevice.ErrorCode.OK.value())
                printState(getResources().getText(R.string.capture_single_fail));
        }
    };
    // check the finger print module
    public void checkDevice() {
        if (mUsbManager == null) return;
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        log("checkDevice..szie"+deviceList.size());
        Iterator<UsbDevice> deviceIter = deviceList.values().iterator();
        while (deviceIter.hasNext()) {
            UsbDevice _device = deviceIter.next();
            Log.e(TAG," device vid:"+_device.getVendorId()+" pid:"+_device.getProductId()+" name:"+_device.getDeviceName());
            if (_device.getVendorId() == 0x16d1) {
                //Suprema vendor ID
                Log.e(TAG,"find Fingerprint module bioslim2 : "+0x16d1);
                mUsbManager.requestPermission(_device, mPermissionIntent);
                if (mBioMiniFactory == null) return;
                mBioMiniFactory.addDevice(_device);
                mCurrentDevice=mBioMiniFactory.getDevice(0);
            } else {
            }
        }

    }

    private void switchPower(boolean isOpen) {
        Log.i(TAG, "switchPower: "+getSDKVersionCode());
        if (getSDKVersionCode()==30){
            Log.i(TAG,"android 11");
            com.ekemp.m8hub.Misc.nativeUsb(isOpen);
            com.ekemp.m8hub.Misc.nativeFP(isOpen);
            //com.ekemp.m8hub.Misc.nativeHost(isOpen);
            // com.ekemp.m8hub.Misc.nativeOtg(isOpen);
        }else {
            Log.i(TAG,"android 8");
            Misc.nativeUsbMode(isOpen ? 1 : 0);
            Misc.fingerEnable(isOpen);
        }
    }
    synchronized public void log(final String msg) {
        Log.e(TAG, msg);
        runOnUiThread(() -> {
            if (mLogView != null) {
                //mLogView.getText().clear();
                mLogView.append(msg + "\n");
                if (mScrollLog != null) {
                    //mScrollLog.scrollTo(0, mScrollLog.getBottom());
                    mScrollLog.fullScroll(ScrollView.FOCUS_DOWN);
                }
            } else {
                Log.e("", msg);
            }
        });
    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switchPower(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fp_scanner);
        mainContext = FpScanner.this;
        mCaptureOptionDefault.frameRate = IBioMiniDevice.FrameRate.SHIGH;
        buttonScan= (Button) findViewById(R.id.buttonEnroll) ;
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enroll(view);
            }
        });

        submitScan= (Button) findViewById(R.id.buttonExportBmp) ;
        submitScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToReactScreen(view);
            }
        });
      //  findViewById(R.id.buttonEnroll).setOnClickListener(this);
        if (mBioMiniFactory != null) {
            mBioMiniFactory.close();
        }

        if (mbUsbExternalUSBManager) {
            mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            mBioMiniFactory = new BioMiniFactory(mainContext, mUsbManager) {
                @Override
                public void onDeviceChange(DeviceChangeEvent event, Object dev) {

                    log("----------------------------------------");
                    log("onDeviceChange : " + event + " using external usb-manager");
                    log("----------------------------------------");
                    if (event == DeviceChangeEvent.DEVICE_ATTACHED && mCurrentDevice == null) {
                        new Thread(() -> {
                            int cnt = 0;
                            while (mBioMiniFactory == null && cnt < 20) {
                                SystemClock.sleep(1000);
                                cnt++;
                            }
                            if (mBioMiniFactory != null) {
                                mCurrentDevice = mBioMiniFactory.getDevice(0);
                                Log.e(TAG, "mCurrentDevice attached : " + mCurrentDevice);
                                if (mCurrentDevice != null) {
                                    log(" DeviceName : " + mCurrentDevice.getDeviceInfo().deviceName);
                                    log("         SN : " + mCurrentDevice.getDeviceInfo().deviceSN);
                                    log("SDK version : " + mCurrentDevice.getDeviceInfo().versionSDK);
                                }
                            }
                        }).start();
                    } else if (mCurrentDevice != null && event == DeviceChangeEvent.DEVICE_DETACHED && mCurrentDevice.isEqual(dev)) {
                        Log.e(TAG, "mCurrentDevice removed : " + mCurrentDevice);
                        mCurrentDevice = null;
                    }
                }
            };
            //
            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);
            checkDevice();


        } else {
            mBioMiniFactory = new BioMiniFactory(mainContext) {
                @Override
                public void onDeviceChange(DeviceChangeEvent event, Object dev) {
                    log("----------------------------------------");
                    log("onDeviceChange : " + event);
                    log("----------------------------------------");
                    if (event == DeviceChangeEvent.DEVICE_ATTACHED && mCurrentDevice == null) {
                        new Thread(() -> {
                            int cnt = 0;
                            while (mBioMiniFactory == null && cnt < 20) {
                                SystemClock.sleep(1000);
                                cnt++;
                            }
                            if (mBioMiniFactory != null) {
                                mCurrentDevice = mBioMiniFactory.getDevice(0);
                                Log.e(TAG, "mCurrentDevice attached : " + mCurrentDevice);
                                if (mCurrentDevice != null) {
                                    log(" DeviceName : " + mCurrentDevice.getDeviceInfo().deviceName);
                                    log("         SN : " + mCurrentDevice.getDeviceInfo().deviceSN);
                                    log("SDK version : " + mCurrentDevice.getDeviceInfo().versionSDK);
                                }
                            }
                        }).start();
                    } else if (mCurrentDevice != null && event == DeviceChangeEvent.DEVICE_DETACHED && mCurrentDevice.isEqual(dev)) {
                        Log.e(TAG, "mCurrentDevice removed : " + mCurrentDevice);
                        mCurrentDevice = null;
                    }
                }
            };
        }
        mBioMiniFactory.setTransferMode(IBioMiniDevice.TransferMode.MODE2);
        printRev("" + mBioMiniFactory.getSDKInfo());
    }
    @Override
    protected void onDestroy() {
        if (mBioMiniFactory != null) {
            mBioMiniFactory.close();
            mBioMiniFactory = null;
        }
        switchPower(false); //power off the scanner
        super.onDestroy();
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            log("permission granted");

        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        requestPermission();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (mBioMiniFactory != null) {
            mBioMiniFactory.close();
            mBioMiniFactory = null;
        }
        switchPower(false);
        super.onBackPressed();
    }




    public void reCapture(){
        mCurrentDevice.abortCapturing();
        flag=0;
        if(mCurrentDevice.isCapturing()){
            Log.e(TAG,"device captuing busy");
            return;
        }
/*        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        ((ImageView) findViewById(R.id.imagePreview)).setImageBitmap(null);
        mBioMiniFactory.setTransferMode(IBioMiniDevice.TransferMode.MODE2);
        if(mCurrentDevice != null) {
            mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TIMEOUT, 15000));
            int outtime=  (int)mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
            log(" Recapture, time out value:"+outtime/1000);
            Log.e(TAG,"Recapture start to capture");
            //mCaptureOptionDefault.captureTimeout = (int)mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
            mCurrentDevice.captureSingle(mCaptureOptionDefault, mCaptureResponseDefault, true);
            // mHandler.sendEmptyMessageDelayed(CAPT,2000);
        }
    }

    public void abortCapture(){
        if(mCurrentDevice != null) {
            new Thread(() -> {
                mCurrentDevice.abortCapturing();
                int nRetryCount =0;
                while(mCurrentDevice != null && mCurrentDevice.isCapturing()){
                    SystemClock.sleep(10);
                    nRetryCount++;
                }
                Log.d("AbortCapturing" , String.format(Locale.ENGLISH , "IsCapturing return false.(Abort-lead time: %dms) " ,
                        nRetryCount* 10));
            }).start();
        }
    }


    //enroll a finger print (capture and get a fingerprint template and store it into t ArrayList)
    public void enroll(View view){
        if (mCurrentDevice != null) {
            //mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TEMPLATE_TYPE, 2002));
            ((ImageView) findViewById(R.id.imagePreview)).setImageBitmap(null);
            IBioMiniDevice.CaptureOption option = new IBioMiniDevice.CaptureOption();
            mBioMiniFactory.setTransferMode(IBioMiniDevice.TransferMode.MODE2);
            mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TIMEOUT, 15000));
            option.frameRate = IBioMiniDevice.FrameRate.MID;
            option.captureTemplate = true;
            // capture fingerprint image
            mCurrentDevice.captureSingle(option,
                    new CaptureResponder() {
                        @Override
                        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                                   final IBioMiniDevice.FingerState fingerState) {
                            runOnUiThread((Runnable) () -> {
                                if (capturedImage != null) {
                                    ImageView iv = (ImageView) findViewById(R.id.imagePreview);
                                    if (iv != null) {
                                        iv.setImageBitmap(capturedImage);
                                    }
                                }
                            });
                            if (capturedTemplate != null) {
                                base64=convertBase(capturedImage);
                            } else {
                                log("<<ERROR>> Template is not extracted...");
                            }
                            log(((IBioMiniDevice) context).popPerformanceLog());

                            return true;
                        }

                        @Override
                        public void onCaptureError(Object context, int errorCode, String error) {
                            log("onCaptureError : " + error);
                            printState(getResources().getText(R.string.enroll_fail));
                        }
                    }, true);
        }
    }

    public static String convertBase(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }


    //convert a file to  byte[]
    public static byte[] toByteArray2(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }
        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
                // System.out.println("reading");
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(channel!=null){
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }






    //save the image buffer from scanner as bmp to the storage
    public void navigateToReactScreen(View view){
        WritableMap params=Arguments.createMap();
        params.putString("name",base64);
        getReactInstanceManager().getCurrentReactContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("eventA",params);

        Intent i= new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

    }
}