package com.sdkapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ReactActivity {
  public static  String test = null;
  DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(null);

  }



  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "sdkApp";
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. There the RootView is created and
   * you can specify the renderer you wish to use - the new renderer (Fabric) or the old renderer
   * (Paper).
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new ReactActivityDelegate(this, getMainComponentName()) {
      @Override
      protected Bundle getLaunchOptions() {
        Bundle initialProperties = new Bundle();
//        ArrayList<String> imageList = new ArrayList<String>(Arrays.asList(
//                "http://foo.com/bar1.png",
//                "http://foo.com/bar2.png"
//        ));
 //       initialProperties.putStringArrayList("images", imageList);
//        Intent myIntent = getIntent(); // gets the previously created intent
//        String firstKeyName = myIntent.getStringExtra("firstKeyName");
        Log.d("waqar", "getLaunchOptions: ");

        String imageList=test;
        initialProperties.putString("images", imageList);
        return initialProperties;
      }
    };
  }

  public static class MainActivityDelegate extends ReactActivityDelegate {
    public MainActivityDelegate(ReactActivity activity, String mainComponentName) {
      super(activity, mainComponentName);
    }

    @Override
    protected ReactRootView createRootView() {
      ReactRootView reactRootView = new ReactRootView(getContext());
      // If you opted-in for the New Architecture, we enable the Fabric Renderer.
      reactRootView.setIsFabric(BuildConfig.IS_NEW_ARCHITECTURE_ENABLED);
      return reactRootView;
    }

    @Override
    protected boolean isConcurrentRootEnabled() {
      // If you opted-in for the New Architecture, we enable Concurrent Root (i.e. React 18).
      // More on this on https://reactjs.org/blog/2022/03/29/react-v18.html
      return BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
    }
  }
}
