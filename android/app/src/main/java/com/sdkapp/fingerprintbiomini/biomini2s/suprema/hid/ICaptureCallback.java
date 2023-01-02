package com.sdkapp.fingerprintbiomini.biomini2s.suprema.hid;

public interface ICaptureCallback {
    void onCapture(byte[] img, int width, int height, int resolution, boolean fingerOn);
    void onError(String msg);
}
