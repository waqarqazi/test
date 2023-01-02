/*
 * Copyright (C) 2017 Suprema Inc.
 */

package com.sdkapp.fingerprintbiomini.biomini2s.suprema.usb;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbRequest;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.sdkapp.fingerprintbiomini.biomini2s.suprema.Hid;
import com.sdkapp.fingerprintbiomini.biomini2s.suprema.IBioMiniInterops;

public class UsbHandlerAndroid implements IUsbHandler {
    public static int BMSS_DEFAULT_PACKET_SIZE = 512;
    public static int BMS2S_DEFAULT_PACKET_SIZE = 64;
    public static final int HID_CMD_PACKET_SIZE = BMSS_DEFAULT_PACKET_SIZE;
    public static final int HID_DATA_PACKET_SIZE = BMSS_DEFAULT_PACKET_SIZE;
    public static final int HID_DATA_PACKET_SIZE_R = BMSS_DEFAULT_PACKET_SIZE;
    public static final int HID_HEADER_SIZE = 5;
    public static final int HID_POS_PACCODE = 1;
    public static final int HID_POS_PACCODE_R = 1;
    static final int HID_POS_CMDCODE = HID_POS_PACCODE + 1;
    static final int HID_POS_SUBCODE = HID_POS_CMDCODE + 1;
    static final int HID_POS_ERRCODE = HID_POS_SUBCODE + 2;
    static final int HID_POS_DATA = HID_POS_ERRCODE + 1;
    static final int HID_POS_CMDCODE_R = HID_POS_PACCODE_R + 1;
    static final int HID_POS_SUBCODE_R = HID_POS_CMDCODE_R + 1;
    static final int HID_POS_ERRCODE_R = HID_POS_SUBCODE_R + 2;
    static final int HID_POS_DATA_R = HID_POS_ERRCODE_R + 1;
    static final int HID_REPORT_ID_ECHO = 1;
    static final int HID_REPORT_ID_CMD = 2;
    static final int HID_REPORT_ID_DATAIN = 3;
    static final int HID_REPORT_ID_DATAOUT = 4;

    private String TAG = "BioMiniSDK";
    private int TRANSFER_TIMEOUT = 140; //bulk Timeout

    private IBioMiniInterops bioMini;
    private UsbRequest request[] = null;
    private UsbRequest requestWrite = null;
    private UsbRequest requestRead = null;
    private ByteBuffer dataQ[] = null;
    private int maxTimesToRead = 0;
    private int maxBytesToRead = 0;
    private int dstOffset = 0;
    private int hidPacketType = Hid.Extra.USB_PACKET_64.value();
    private int hidMaxTransferData;

    private final boolean useReservedQueue = true;
    private byte[] bufQ = new byte[16384 * 2];
    private byte[] hidPacket;
    private byte[] hidPacketFrag;

    public byte[] hidPacketEcho;
    public byte[] hidPacketEchoData;

    private UsbDeviceConnection usbConnection = null;
    private UsbEndpoint epIn = null;
    private UsbEndpoint epOut = null;

    private int mInitializedRequests = 0;
    private boolean mReading = false;

    public UsbHandlerAndroid(IBioMiniInterops bioMiniInterface, UsbDeviceConnection conn, UsbEndpoint ep_in, UsbEndpoint ep_out, int maxBulkSize) {
        maxTimesToRead = (maxBulkSize + 16383) / 16384;
        maxBytesToRead = maxTimesToRead * 16384;
        request = new UsbRequest[maxTimesToRead];
        requestWrite = new UsbRequest();
        requestRead = new UsbRequest();
        dataQ = new ByteBuffer[maxTimesToRead];
        epIn = ep_in;
        epOut = ep_out;
        usbConnection = conn;

        for (int i = 0; i < mInitializedRequests; i++) {
            request[i].close();
        }
        mInitializedRequests = 0;

        for (int i = 0; i < maxTimesToRead; i++) {
            dataQ[i] = ByteBuffer.allocate(16384);
            request[i] = new UsbRequest();
        }

        try {
            requestWrite.initialize(usbConnection, epOut);
            requestRead.initialize(usbConnection, epIn);
        } catch (Exception e) {
            Log.e(TAG,"BioMini SDK Device initializing..... " + e.getCause() + " / " + e.getMessage());
        }

        byte[] cmd_packet = new byte[Hid.Extra.USB_PACKET_64.value()];
        byte[] echo_packet = new byte[Hid.Extra.USB_PACKET_64.value()];
        Arrays.fill(cmd_packet, 0, Hid.Extra.USB_PACKET_64.value(), (byte)0);

        cmd_packet[0] = HID_REPORT_ID_CMD;
        cmd_packet[HID_POS_PACCODE] = (byte) Hid.Pac.PAC_GET.value();
        cmd_packet[HID_POS_CMDCODE] = (byte) Hid.Cmd.VAT_USB_TYPE.value();
        if (write(cmd_packet, Hid.Extra.USB_PACKET_64.value())) {
            if(readSmall(echo_packet, Hid.Extra.USB_PACKET_64.value())) {
                if (cmd_packet[HID_POS_PACCODE] == (echo_packet[HID_POS_PACCODE] & 0x7f) && cmd_packet[HID_POS_CMDCODE] == echo_packet[HID_POS_CMDCODE]) {
                    if (echo_packet[HID_POS_ERRCODE] == Hid.Error.CTRL_SUCCESS.value()) {
                        if(echo_packet[HID_POS_DATA] == Hid.Sub.UVT_USB_FULLSPEED.value()) {
                            hidPacketType = 64;
                        } else {
                            hidPacketType = 512;
                        }
                    }
                }
            }
        }

        hidPacket = new byte[hidPacketType];
        hidPacketFrag = new byte[hidPacketType];
        hidPacketEcho = new byte[hidPacketType];
        hidPacketEchoData = new byte[hidPacketType - HID_HEADER_SIZE - HID_POS_PACCODE_R];
        hidMaxTransferData = (hidPacketType - HID_HEADER_SIZE - 1) * 0x3fff;
    }

    public void resize(int maxBulkSize) {
        int _maxTimesToRead = (maxBulkSize + 16383) / 16384;
        if (_maxTimesToRead > maxTimesToRead) {
            maxTimesToRead = _maxTimesToRead;
            maxBytesToRead = maxTimesToRead * 16384;
            for (int i = 0; i < mInitializedRequests; i++) {
                request[i].close();
            }
            mInitializedRequests = 0;
            usbConnection.close();
            request = new UsbRequest[maxTimesToRead];
            dataQ = new ByteBuffer[maxTimesToRead];

            for (int i = 0; i < maxTimesToRead; i++) {
                dataQ[i] = ByteBuffer.allocate(16384);
                request[i] = new UsbRequest();
            }
        }
    }

    @Override
    public boolean enumerate() {
        return false;
    }

    @Override
    public int getDeviceCount() {
        return 0;
    }

    @Override
    public void setCommandTimeout(int _timeout) {

    }

    @Override
    public boolean open(int idx) {
        return false;
    }

    public void close() {
        if (mReading) {
            Log.e(TAG,"Ooops!! Some reading thread would be corrupted!!");
        }
        for (int i = 0; i < mInitializedRequests; i++) {
            request[i].close();
        }
        usbConnection.close();
        usbConnection = null;
        mInitializedRequests = 0;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public boolean isEqual(Object dev) {
        return false;
    }

    @Override
    public boolean writeHid(byte[] buf, byte id, int len) {
        return false;
    }

    public void resetBulkPipe() {
        //Log.d(TAG,"resetBulkPipe()=======================");
        for (int i = 0; i < mInitializedRequests; i++) {
            boolean ret = request[i].cancel();
            //Log.d(TAG, i + " : " + ret);
        }
        //Log.d(TAG, "resetBulkPipe()=======================");
    }

    public void setDstOffset(int offset) {
        dstOffset = offset;
    }

    @Override
    public boolean initRead(int len, int skipPackets, boolean updateAlways) {
        boolean reqres = false;
        int nTimesToRead = (len + 16383) / 16384;
        nTimesToRead = nTimesToRead - skipPackets;
        if( updateAlways || mInitializedRequests != nTimesToRead ) { // close and re-init always -> plus2 often lose connection
            for (int i = 0; i < mInitializedRequests; i++) {
                request[i].close(); // close and re-init always -> plus2 often lose connection
            }
            if(nTimesToRead <= request.length) {
                for (int i = 0; i < nTimesToRead; i++) {
                    request[i] = new UsbRequest();
                    reqres = request[i].initialize(usbConnection, epIn);
                    if (!reqres) {
                        //Log.d("D", "request initialize return fail");
                        return false;
                    }
                    mInitializedRequests = i + 1;
                }
            }
            else { return false; }
        }
        return true;
    }

    @Override
    public synchronized boolean read(byte[] buf, int len, int skipPackets, byte fillExtra) {

        mReading = true;
        int nBytesToRead = len;
        int nRemainingBytes = 0;
        int nToRead = (len + 16383) / 16384;
        int nToEnqueue = len / 16384;
        int nQueuesReady = nToRead; // ignore remaining
        boolean reqres = false;
        long CHECKING_TIME = 0;

        nRemainingBytes = len % 16384; // for real data last packet.
        nToRead = nToRead - skipPackets;
        if (skipPackets != 0) {
            nRemainingBytes = 0;
            nBytesToRead = nToRead * 16384;
        }
        nToEnqueue = nToRead;   // T.T

        CHECKING_TIME = System.currentTimeMillis();

        //Log.d("D", "nToRead = " + nToRead + ", mInitializedRequests = " + mInitializedRequests);
        //Log.d(TAG, "UsbHandlerAndroid number of USB reads : " + nToRead + ", available USB request handles : " + request.length);
        for (int i = 0; i < nToEnqueue; i++) {  // enqueue but, do not dequeue
            request[i].setClientData(this);
            dataQ[i].position(0);
            if (i == nToEnqueue - 1 && nRemainingBytes > 0) { // last packet
                reqres = request[i].queue(dataQ[i], nRemainingBytes);
            } else {
                reqres = request[i].queue(dataQ[i], 16384);
            }

            if (!reqres) {
                Log.e(TAG, "UsbHandlerAndroid result of USB request  : " + reqres);
                mReading = false;
                return false;
            }
        }
        //Log.d("D", "nToRead : " + nToRead);
        for (int i = 0; i < nToRead; i++) {
            //Log.d(TAG, "UsbHandlerAndroid requestWait idx: " + i);
            if(usbConnection != null)
            {
                try{
                    usbConnection.requestWait();
                }catch(Exception e ){
                    e.printStackTrace();
                }
            }

            //Log.d(TAG, "UsbHandlerAndroid requestWait done");
        }
        //Log.d(TAG, "UsbHandlerAndroid USB transfer time : " + (System.currentTimeMillis() - CHECKING_TIME));

        //Log.d("D", "len = " + len + ", nBytesToRead = " + nBytesToRead);
        //Log.d("D", "buf.len = " + buf.length);
        for (int i = 0; i < nBytesToRead; i = i + 16384) {
            if (!useReservedQueue) {
                byte[] temp = dataQ[i / 16384].array();
                System.arraycopy(temp, 0, buf, i, 16384);
            } else {
                int posBuf = dataQ[i / 16384].position();

                dataQ[i / 16384].position(0);
                dataQ[i / 16384].get(bufQ, 0, posBuf);

                //Log.d(TAG, "UsbHandlerAndroid Data in queue : " + posBuf);
                //Log.d("D", "i + dstOffset = " + i + dstOffset);
                System.arraycopy(bufQ, 0, buf, i + dstOffset, posBuf);
            }
        }

        Arrays.fill(buf, nBytesToRead, len, fillExtra);

        mReading = false;
        return true;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean write(byte[] buf, int len) {
        //Log.d(TAG, String.format("UsbHandlerAndroid write(%d, %02x %02x)", len, buf[0], buf[1]));
        requestWrite.setClientData(this);

        if (!requestWrite.queue(ByteBuffer.wrap(buf), len)) {
            return false;
        }

        usbConnection.requestWait();

        return true;
    }

    @SuppressLint("DefaultLocale")
    public boolean readSmall(byte[] buf, int len) {
        requestRead.setClientData(this);

        if (!requestRead.queue(ByteBuffer.wrap(buf), len)) {
            return false;
        }

        usbConnection.requestWait();

        return true;
    }

    @Override
    public boolean controlRx(int cmd, byte[] ctrlBuf, int len) {
        int re = usbConnection.controlTransfer(UsbConstants.USB_DIR_IN,
                cmd, 0, 0, ctrlBuf, len, TRANSFER_TIMEOUT);
        //Log.d(TAG, "UsbHandlerAndroid controlRx(" + cmd + ") got " + re + " bytes");

        //for( int idx=0; idx< mInitializedRequests ; idx++){
        //    request[idx].cancel();
        //}
        return re == len;
    }

    @Override
    public boolean controlTx(int cmd, byte[] ctrlBuf, int len) {
        int re = usbConnection.controlTransfer(UsbConstants.USB_DIR_OUT,
                cmd, 0, 0, ctrlBuf, len, TRANSFER_TIMEOUT);
        return re != -1;
    }


    // USB Control Commands
    private final static int CMD_LED_CTRL = 0xc2;
    private final static int CMD_READ_FRAME_A = 0xe2;
    private final static int CMD_READ_FRAME_DONE = 0xef;
    private final static int CMD_SET_EEPROM = 0xD6;
    private final static int CMD_GET_EEPROM = 0xD7;
    private final static int CMD_SENSOR_EEPROM_GET_ADDR = 0xDE;// send 1 byte 0 return
    private final static int CMD_SENSOR_EEPROM_GET_DATA = 0xDF;// send 0 byte 1 byte return
    private final static int CMD_EEPROM_WP_ENABLE = 0xC9;

    public final static int OV_IIC_EEPROM_ADDR = (0xAE);

    public boolean readEEPROM(int addr, int len, byte[] buffer) {
        byte[] buf = new byte[64];
        int r;
        buf[0] = 1;
        r = usbConnection.controlTransfer(UsbConstants.USB_DIR_OUT, CMD_EEPROM_WP_ENABLE, 0, 0, buf, 1, 0);
        if (r == -1) return false;

        buf[0] = (byte) OV_IIC_EEPROM_ADDR;
        buf[1] = (byte) ((addr >> 8) & 0xff);
        buf[2] = (byte) (addr & 0xff);
        buf[3] = (byte) len;

        r = usbConnection.controlTransfer(UsbConstants.USB_DIR_OUT, CMD_SET_EEPROM, 0, 0, buf, 4, 0);
        if(r != -1) r = usbConnection.controlTransfer(UsbConstants.USB_DIR_IN, CMD_GET_EEPROM, 0, 0, buffer, len, 0);
        if(r == -1) return false;

        buf[0] = 0;
        r = usbConnection.controlTransfer(UsbConstants.USB_DIR_OUT, CMD_EEPROM_WP_ENABLE, 0, 0, buf, 1, 0);

        return r != -1;
    }

    private boolean readSensorEEPROMOneByte(int addr, byte[] buffer) {
        boolean ret = false;
        byte[] buf = new byte[64];
        buf[0] = (byte) addr;
        int len = 64;
        if( !controlTx(CMD_SENSOR_EEPROM_GET_ADDR, buf, 1) ) {
            if(controlRx(CMD_SENSOR_EEPROM_GET_DATA, buf, len)) {
                buffer[0] = buf[0];
                ret = true;
            }
        }
        SystemClock.sleep(10);
        return ret;
    }

    public boolean readSensorEEPROM(int addr, int len, byte[] buffer) {
        boolean ret = false;
        byte[] buf = new byte[2];
        if (len > 64) {
            ret = false;
        }
        for (int i = 0; i < len; i++) {
            if (readSensorEEPROMOneByte(addr + i, buf)) {
                buffer[i] = buf[0];
                ret = true;
            } else {
                ret = false;
                break;
            }
        }
        return ret;
    }

    @Override
    public int hidCommand(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub) {
        return hidCommand(pac.value(), cmd.value(), sub.value(), null);
    }

    @Override
    public int hidCommand(int pac, int cmd, int sub) {
        return hidCommand(pac, cmd, sub, null);
    }

    @Override
    public int hidCommand(int pac, int cmd, int sub_1, int sub_2) {
        return hidCommand(pac, cmd, ((sub_1 & 0xff) << 8) | (sub_2 & 0xff), null);
    }

    @Override
    public int hidCommand(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub, byte[] extra) {
        return hidCommand(pac.value(), cmd.value(), sub.value(), extra);
    }

    @Override
    public int hidCommand(Hid.Pac pac, Hid.Cmd cmd, int sub_1, int sub_2, byte[] extra) {
        return hidCommand(pac.value(), cmd.value(), sub_1, sub_2, extra);
    }

    @Override
    public int hidCommand(int pac, int cmd, int sub_1, int sub_2, byte[] extra) {
        return hidCommand(pac, cmd, ((sub_1 & 0xff) << 8) | (sub_2 & 0xff), extra);
    }

    @Override
    synchronized
    public int hidCommand(int pac, int cmd, int sub, byte[] extra) {
        Arrays.fill(hidPacket, 0, hidPacketType, (byte) 0);

        hidPacket[0] = HID_REPORT_ID_CMD;   // maybe obsolete, but... nothing to lose!
        hidPacket[HID_POS_PACCODE] = (byte) pac;
        hidPacket[HID_POS_CMDCODE] = (byte) cmd;
        Hid.putShort(hidPacket, HID_POS_SUBCODE, sub);
        if (extra != null) {
            System.arraycopy(extra, 0, hidPacket, HID_POS_DATA,
                    Math.min(extra.length, hidPacket.length - HID_POS_DATA));
        }
        if (write(hidPacket, hidPacketType)) {
            return echo(pac, cmd, sub);
        }
        return -1;
    }

    @Override
    public int echo(Hid.Pac pac, Hid.Cmd cmd, Hid.Sub sub) {
        return echo(pac.value(), cmd.value(), sub.value());
    }

    @SuppressLint("DefaultLocale")
    @Override
    synchronized
    public int echo(int pac, int cmd, int sub) {
        if (readSmall(hidPacketEcho, hidPacketType)) {
            //Log.d(TAG, String.format("pac = %x/%x, cmd = %x/%x, err = %d", pac,
            //        hidPacketEcho[HID_POS_PACCODE], cmd, hidPacketEcho[HID_POS_CMDCODE], hidPacketEcho[HID_POS_ERRCODE]));
            if (pac == (hidPacketEcho[HID_POS_PACCODE] & 0x7f) && cmd == hidPacketEcho[HID_POS_CMDCODE]) {
                return hidPacketEcho[HID_POS_ERRCODE];
            }
            //Log.d(TAG, "UsbHandlerAndroid echo check failed");
        } else {
            //Log.d(TAG, "UsbHandlerAndroid echo read failed");
        }
        return -1;
    }

    @Override
    public byte[] getLastEcho() {
        return hidPacketEcho;
    }

    @Override
    public byte[] getLastEchoData() {
        System.arraycopy(hidPacketEcho, HID_HEADER_SIZE + HID_POS_PACCODE_R, hidPacketEchoData, 0, hidPacketEchoData.length);
        return hidPacketEchoData;
    }

    @Override
    public int hidReceiveSize() {
        int re = Hid.retrieveInt(hidPacketEcho, HID_POS_DATA_R);
        if(re < 0) { re = 0; }
        if(re > hidMaxTransferData) { re = hidMaxTransferData; }
        return re;
    }

    @SuppressLint("DefaultLocale")
    @Override
    synchronized
    public boolean hidReceive(byte[] buf) {
        int offset = 0;
        boolean lastPacket;
        boolean incompleteTransfer;
        int data_idx;
        int data_len = Hid.retrieveInt(hidPacketEcho, HID_POS_DATA);
        int fragSize = hidPacketType - HID_POS_PACCODE;
        int fragDataSize = fragSize - HID_HEADER_SIZE;

        if (buf.length < data_len || fragDataSize <= 0 || data_len <= 0) {
            return false;
        }
        do {
            readSmall(hidPacketFrag, hidPacketType);
            System.arraycopy(hidPacketFrag, HID_POS_DATA, buf, offset, Math.min(fragDataSize, data_len));
            offset += fragDataSize;
            data_idx = Hid.retrieveInt(hidPacketFrag, HID_POS_SUBCODE);
            //l(String.format("data_idx = %x/%d - %x %x %x %x", data_idx, data_len, hidPacketFrag[2], hidPacketFrag[3], hidPacketFrag[4], hidPacketFrag[5]));
            lastPacket = (data_idx & 0x40000000) != 0;
            incompleteTransfer = (data_idx & 0x80000000) != 0;
            data_len -= fragDataSize;
        } while (hidPacketFrag[HID_POS_ERRCODE] == 0 && !lastPacket && data_len > 0);
        //Log.d(TAG, String.format("UsbHandlerAndroid lastPacket = %s, %d, %s", lastPacket, data_len, incompleteTransfer));
        return lastPacket /*&& (!incompleteTransfer)*/ && data_len <= 0;
    }

    @SuppressLint("DefaultLocale")
    @Override
    synchronized
    public byte[] hidReceive() {
        int offset = 0;
        boolean lastPacket;
        boolean incompleteTransfer;
        int data_idx;
        int data_len = Hid.retrieveInt(hidPacketEcho, HID_POS_DATA);
        int fragSize = hidPacketType - HID_POS_PACCODE;
        int fragDataSize = fragSize - HID_HEADER_SIZE;
        byte[] buf = new byte[data_len];

        if (fragDataSize <= 0 || data_len <= 0) {
            return null;
        }

        do {
            readSmall(hidPacketFrag, hidPacketType);
            System.arraycopy(hidPacketFrag, HID_POS_DATA, buf, offset, Math.min(fragDataSize, data_len));
            offset += fragDataSize;
            data_idx = Hid.retrieveInt(hidPacketFrag, HID_POS_SUBCODE);
            lastPacket = (data_idx & 0x40000000) != 0;
            incompleteTransfer = (data_idx & 0x80000000) != 0;
            data_len -= fragDataSize;
        } while (hidPacketFrag[HID_POS_ERRCODE] == 0 && !lastPacket && data_len > 0);
        if (lastPacket /*&& (!incompleteTransfer)*/ && data_len <= 0) {
            return buf;
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean hidSend(byte[] buf, int data_len) {
        int offset = 0;
        int data_idx = 0;
        boolean lastPacket = false;
        int fragSize = hidPacketType - HID_POS_PACCODE;
        int fragDataSize = fragSize - HID_HEADER_SIZE;
        int transferSize = fragDataSize;

        if (fragDataSize <= 0 || data_len <= 0) {
            return false;
        }

        do {
            hidPacket[0] = HID_REPORT_ID_DATAOUT;
            hidPacket[HID_POS_PACCODE] = (byte) Hid.Pac.PAC_DATA_OUT.value();
            hidPacket[HID_POS_CMDCODE] = (byte) 0;
            if ((offset + fragDataSize) >= data_len) {
                data_idx = data_idx | 0x4000;
                lastPacket = true;
                transferSize = data_len - offset;
            }
            Hid.putShort(hidPacket, HID_POS_SUBCODE, data_idx);
            System.arraycopy(buf, offset, hidPacket, HID_POS_DATA, transferSize);
            if (!write(hidPacket, hidPacketType)) {
                break;
            }

            data_idx++;
            offset += transferSize;
        } while (data_idx < 0x4000);
        return lastPacket && (data_len == offset);
    }
}
