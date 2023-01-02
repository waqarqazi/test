/*
 * Copyright (C) 2017 Suprema Inc.
 */

package com.sdkapp.fingerprintbiomini.biomini2s.suprema;

public interface IUsbEventHandler {
    enum DeviceChangeEvent {
        DEVICE_ATTACHED, DEVICE_DETACHED, DEVICE_PERMISSION_DENIED
    }
    enum DisconnectionCause {
        USB_UNPLUGGED, SLEEP_MODE, DEACTIVATED
    }
    /**
     * Interface that should be implemented by the end-user of SDK
     * @param event
     * @param dev
     */
    void onDeviceChange(DeviceChangeEvent event, Object dev);
}
