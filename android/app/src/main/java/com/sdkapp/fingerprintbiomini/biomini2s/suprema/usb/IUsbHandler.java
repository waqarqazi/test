/*
 * Copyright (C) 2017 Suprema Inc.
 */
package com.sdkapp.fingerprintbiomini.biomini2s.suprema.usb;


import com.sdkapp.fingerprintbiomini.biomini2s.suprema.Hid;

public interface IUsbHandler {
    boolean enumerate();
    int getDeviceCount();
    void setCommandTimeout(int _timeout);
    boolean open(int idx);
    void close();
    String getPath();

    boolean isEqual(Object dev);

    boolean initRead(int len, int skipPackets, boolean updateAlways);
    boolean read(byte[] buf, int len, int skipPackets, byte fillExtra);
    boolean write(byte[] buf, int len);
    boolean writeHid(byte[] buf, byte id, int len);
    boolean readSmall(byte[] buf, int len);
    boolean controlRx(int cmd, byte[] ctrlBuf, int len);
    boolean controlTx(int cmd, byte[] ctrlBuf, int len);

    int hidCommand(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub);
    int hidCommand(int pac, int cmd, int sub);
    int hidCommand(int pac, int cmd, int sub_1, int sub_2);
    int hidCommand(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub, byte[] extra);
    int hidCommand(Hid.Pac pac, Hid.Cmd cmd, int sub_1, int sub_2, byte[] extra);
    int hidCommand(int pac, int cmd, int sub_1, int sub_2, byte[] extra);
    int hidCommand(int pac, int cmd, int sub, byte[] extra);
    int echo(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub);
    int echo(int pac, int cmd, int sub);
    byte[] getLastEcho();
    byte[] getLastEchoData();
    int hidReceiveSize();
    boolean hidReceive(byte[] buf);
    byte[] hidReceive();
    boolean hidSend(byte[] buf, int data_len);
}
