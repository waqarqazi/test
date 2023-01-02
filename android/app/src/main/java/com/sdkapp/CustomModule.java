package com.sdkapp;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class CustomModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactApplicationContext;
    private DeviceEventManagerModule.RCTDeviceEventEmitter mEmitter=null;
    CustomModule(ReactApplicationContext reactContext){
        super(reactContext);
        reactApplicationContext=reactContext;
    }
    @NonNull
    @Override
    public String getName() {
        return "SdkBio";
    }

    @ReactMethod
    public void goToNextScreen(){
        Intent intent=new Intent(reactApplicationContext, FpScanner.class);
        getCurrentActivity().startActivity(intent);
    }
    @ReactMethod
    public void sendEvent(String eventName,String meassage){
        WritableMap params= Arguments.createMap();
        params.putString("message",meassage);
        if(mEmitter==null){
            mEmitter=getReactApplicationContext().getJSModule((DeviceEventManagerModule.RCTDeviceEventEmitter.class));
        }
        if(mEmitter!=null){
           mEmitter.emit(eventName,params);
        }
    }
    @ReactMethod
    public void test(){
    sendEvent("waqar","waqar");
    }

}
