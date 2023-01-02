/*
 * Copyright (C) 2017 Suprema Inc.
 */

package com.sdkapp.fingerprintbiomini.biomini2s.suprema;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public final class Hid {

    ////////////////////////////////////
    // SECTION 1
    ////////////////////////////////////
    // hid packet types
    public enum Pac {
        PAC_NA(0x00),
        PAC_CMD(0x01),
        PAC_SET(0x02),
        PAC_GET(0x03),
        PAC_DATA_IN(0x05),
        PAC_DATA_OUT(0x06),
        PAC_ECHO(0x80);

        private int mValue;

        Pac(int value) {
            mValue = value;
        }

        public int value() {
            return mValue;
        }
    };

    ////////////////////////////////////
    // SECTION 2
    ////////////////////////////////////
    public enum Cmd {
	// hid command types
	CMT_NA(0x00),
	CMT_CAPTURE(0x01),
	CMT_DEVICE_CTRL(0x02),
	CMT_EXTRACT(0x03),
	CMT_SEND_DATA(0x05),
	CMT_RECEIVE_DATA(0x06),
	CMT_USER_CTRL(0x10),
	CMT_SET(0x11),
	CMT_GET(0x12),
	CMT_VERIFY(0x20),
	CMT_ENROLL(0x21),
	CMT_IDENTIFY(0x22),

	//hid data types
	DAT_NA(0x00),
	DAT_DATA_IN(0x10),
	DAT_DATA_OUT(0x20),

	// hid variable control(set/get) types
	VAT_NA(0x00),
	VAT_LED(0x01),
	VAT_ENCRYPT_OPT(0x02),
	VAT_IS_FINGER_ON(0x04),
	VAT_IS_TOUCH_ON(0x05),
	VAT_DEV_INFO(0x06),
	VAT_CAPTURE_OPT(0x07),
	VAT_TEMPLATE_OPT(0x08),
	VAT_VERIFIY_OPT(0x09),
	VAT_IMAGE_OPT(0x0A),
	VAT_PARAM_RESET(0x0C),
	VAT_IDENTIRY_OPT(0x0D),
	VAT_IS_STREAM_UPDATE(0x0E),
	VAT_RESET_DATA(0x0F),
	VAT_USB_TYPE(0x11),
	VAT_HID_PROTOCOL_VER(0x13),
	VAT_DB_OPT(0x14),
	VAT_LAST_STATUS(0x15),
	VAT_DB_INFO(0x16),
	VAT_DB_IMPORT_TYPE(0x17),
	VAT_DB_ENCRYPT_KEY(0x18);

        private int mValue;

        Cmd(int value) {
            mValue = value;
        }

        public int value() {
            return mValue;
        }
    }

    ////////////////////////////////////
    // SECTION 3 (2 bytes)
    ////////////////////////////////////
    public enum Sub {
        SUB_NA(0x00),

        // hid sub command types (capture)
        CCT_NA(0x00),
        CCT_SINGLE(0x01),
        CCT_LOOP(0x02),
        CCT_PREVIEW(0x03),
        CCT_STOP(0x10),

        // hid sub command types (send image type)
        // in case of receiving data, refer to DBI_* section
        CSI_RAW_IMAGE(0x0002),

        // hid sub command types (send template type)
        // in case of receiving data, refer to DBI_* section
        CST_SUPREMA_TEMPLATE(0x0001),
        CST_ENC_SUPREMA_TEMPLATE(0x1001),
        CST_ISO_TEMPLATE(0x0101),
        CST_ENC_ISO_TEMPLATE(0x1101),
        CST_ANSI_TEMPLATE(0x0201),
        CST_ENC_ANSI_TEMPLATE(0x1201),
	        
        //hid sub command types (hid device control)
        CDT_NA(0x00),
        CDT_RESET_DEVICE(0x02),
        CDT_SET_SLEEP_MODE(0x0C),
        CDT_GET_SLEEP_MODE(0x0D),
        CDT_START_FW_SEND(0x15),
        CDT_FINISH_FW_SEND(0x16),
        CDT_START_DB_SEND(0x1C),
        CDT_FINISH_DB_SEND(0x1D),
        
        // hid sub command types (transfer db image type)
        CTI_DB_IMAGE(0x0003),
        
        // hid sub command types (transfer fw image type)
        CTI_FW_IMAGE(0x0004),
        
        // hid sub command types (device buffer identifier)
        DBI_CAPTURED_TEMPLATE(0x01),
        DBI_TRANSFERED_TEMPLATE(0x02),
        DBI_CAPTURED_IMAGE(0x11),
        DBI_TRANSFERED_IMAGE(0x12),
        DBI_PREVIEW_IMAGE(0x13),
        
        DBI_CAPTURED_TEMPLATE_ENC(0x1001),
        DBI_TRANSFERED_TEMPLATE_ENC(0x1002),
        
        // hid sub command types (user data identifier)
        UDI_USER_LIST(0x21),
        UDI_TEMPLATE(0x22),
        
        // hid sub command types (Database image identifier)
        DII_DB_IMANGE(0x31),
        
        // hid sub command types (capture option variables)
        CVT_TIMEOUT(0x01),
        CVT_SENSITIVITY(0x02),
        CVT_LFD_LEVEL(0x03),
        CVT_EX_TRIGGER(0x04),
        
        // hid sub command types (template option variables)
        TVT_TEMPLATE_FORMAT(0x01),
        TVT_TEMPLATE_SIZE(0x02),
        
        // hid sub command types (verify option variables)
        VVT_SECURITY_LEVEL(0x01),
        
        // hid sub command types (identify option variables)
        IVT_AUTO_ROTATE(0x02),
        IVT_FAST_MODE(0x03),
        IVT_TIMEOUT(0x04),
        
        // hid sub command types (image option variables)
        IVT_IMAGE_FORMAT(0x01),
        IVT_COMPRESS_RATIO(0x02),
                
        // hid sub command types (last result variables)
        LVT_LFD_RESULT(0x02),
        
        // hid sub command types (usb type variables)
        UVT_USB_FULLSPEED(0x01),
        UVT_USB_HIGHSPEED(0x02),
        UVT_USB_FIDO(0x03),
        
        // hid sub command types (db import type variables)
        DVT_DB_IMPORT_NEW(0x01),
        DVT_DB_IMPORT_OVERWRITE(0x02),
        DVT_DB_IMPORT_MERGE(0x03),
        
        // hid sub command types (Reset Data Type)
        RVT_CAPTURE_IMG(0x01),
        RVT_TRANSFER_IMG(0x02),
        RVT_EXTRACT_TEMPLATE(0x03),
        RVT_TRANSFER_TEMPLATE(0x04),
                
        // hid sub command types (eNroll mode : template type)
        CNM_F1_DEFAULT(0x00),
        CNM_F1_SUPREMA_TEMPLATE(0x00),
        CNM_F1_ENC0_SUPREMA_TEMPLATE(0x10),
        CNM_F1_ENC2_SUPREMA_TEMPLATE(0x30),
        CNM_F1_ISO_TEMPLATE(0x01),
        CNM_F1_ENC0_ISO_TEMPLATE(0x11),
        CNM_F1_ENC2_ISO_TEMPLATE(0x31),
        CNM_F1_ANSI_TEMPLATE(0x02),
        CNM_F1_ENC0_ANSI_TEMPLATE(0x12),
        CNM_F1_ENC2_ANSI_TEMPLATE(0x32),
        CNM_F1_UNDEFINE_TEMPLATE(0x0F),
        CNM_F1_UNDEFINE_ENC0_TEMPLATE(0x1F),
        CNM_F1_UNDEFINE_ENC2_TEMPLATE(0x3F),

        // hid sub command types (eNroll mode)
        CNM_F2_LIVE_TEMPLATE(0x00),
        CNM_F2_TRANSFERED_TEMPLATE(0x01),
        CNM_F2_LAST_CAPTURE_DATA(0x02),
        CNM_F2_LAST_TEMPLATE(0x05),

        // hid sub command types (Verify mode)
        CVM_TRANSFERED_TEMPLATE(0x00),
        CVM_ENROLLED_TEMPLATE(0x01),
        CVM_ENROLLED_VS_TRANSFERED(0x02),
        
        // hid sub command types (Identify mode)
        CIM_LIVE_TEMPLATE(0x00),
        CIM_TRANSFERED_TEMPLATE(0x01),
        CIM_LAST_TEMPLATE(0x02),
        
        // hid sub command types
        CUC_LOGOUT(0x00),
        CUC_LOGIN(0x01), // default: use password
        CUC_CHANGE_PWD(0x02),
        CUC_CHECK_USER(0x03),
        CUC_ADD_USER(0x04),
        CUC_DELETE_USER(0x05), // default: one user

        // hid sub command types (subset of CUC_ADD_USER - including options)
        CUC_F1_ADD_NORMAL_USR(0x00),
        CUC_F1_ADD_POWER_USR(0x01),
        CUC_F2_ADD_USER(0x04),
        
        // hid sub command types (subset of CUC_DELETE_USER - including options)
        // bitwise options
        // --> [specific] [admin] [all power] [all normal] (e.g. Delete All Normal User + Admin (0x05)
        CUC_F1_DELETE_NA(0x00),
        CUC_F1_DELETE_SPECIFIC_USER(0x08),
        CUC_F1_DELETE_ADMIN(0x04),
        CUC_F1_DELETE_ALL_POWER_USER(0x02),
        CUC_F1_DELETE_ALL_NORMAL_USER(0x01),
        CUC_F2_DELETE_USER(0x05),
        
        // hid sub command types (Device Info)
        CDI_DEV_INFO(0x00),
        CDI_MODEL_NAME(0x01),
        CDI_MODULE_SN(0x02),
        CDI_PRODUCT_SN(0x03),
        
        // hid sub Command types (Encrypt Mode)
        CEM_UNIQ_N_AES(0x0101),
        CEM_UNIQ_N_RSA(0x0102),
        CEM_EACH_N_AES(0x0201),
        CEM_EACH_N_RSA(0x0202),
        CEM_SYNC_N_AES(0x0801),
        CEM_SYNC_N_RSA(0x0802),
        CEM_NA(0x0000),
        
        // hid sub Command types (dB Info)
        CBI_SUMMARY(0x01);

        final private int mValue;
        final private int mUserValue;

        Sub(int value) {
            mValue = value;
            mUserValue = value;
        }

        Sub(int value, int sec_value) {
            mValue = value;
            mUserValue = sec_value;
        }

        public int value() {
            return mValue;
        }

        public int userValue() {
            return mUserValue;
        }

        public int merge(Sub msb, Sub lsb) {
            return ((msb.value() << 8) & 0xff00) | (lsb.value() & 0xff);
        }

        public int compose(int msb, int lsb) {
            return ((msb << 8) & 0xff00) | (lsb & 0xff);
        }
    }

    public enum Error {
        CTRL_SUCCESS(0),
        CTRL_ERR_FAIL(-1),
        CTRL_ERR_UNKNOWN(-2),
        CTRL_ERR_INVALID_PARAM(-3),
        CTRL_ERR_UNSUPPORT_CMD(-4),
        CTRL_ERR_NOT_AUTHORIZED(-5),
        CTRL_ERR_INVALID_MODE(-6),
        CTRL_ERR_MEM_ALLOC(-7),
        CTRL_ERR_INVALID_SETTING(-8),
        CTRL_ERR_MEM_OUT(-9),
        CTRL_ERR_GET_DATA(-10),
        CTRL_ERR_NEED_REBOOT(-11),
        CTRL_ERR_SYSTEM_IS_NOT_LOADED(-12),
        CTRL_ERR_FILE_EMPTY(-13),
        // Errors in Processors
        CTRL_ERR_PROCESS_CORRUPT(-20),
        CTRL_ERR_CAPPROC_FAILED(-21),
        CTRL_ERR_CAPPROC_CANNOT_START(-22),
        CTRL_ERR_CAPPROC_CORRUPT(-23),
        // Login errors
        CTRL_ERR_INVALID_ID(-30),
        CTRL_ERR_INVALID_AUTH(-31),
        CTRL_ERR_INVALID_PWD(-32),
        CTRL_ERR_NO_LOGGED_USER(-34),
        CTRL_ERR_INVALID_TEMPLATE(-35),
        CTRL_ERR_ALREADY_USER_EXIST(-36),
        CTRL_ERR_NO_ENROLLED_USER(-37),
        // Capturing status
        CTRL_ERR_CAPTURE_TIMEOUT(-40),
        CTRL_ERR_IS_CAPTURING(-41),
        CTRL_ERR_BUFFER_IS_NOT_READY(-42),
        CTRL_ERR_FAKE_FINGER(-43),
        CTRL_ERR_NO_FINGER_FOUND(-44),
        CTRL_ERR_CAPTURE_IS_NOT_RUNNING(-45),
        CTRL_ERR_LFD_FAILED(-46),
        CTRL_ERR_CAPTURE_ABORTED(-47),
        CTRL_ERR_CHECK_STATUS(-48),
        // Functional errors
        CTRL_ERR_EXTRACTION_FAILED(-60),
        CTRL_ERR_TEMPLATE_ENCRYPT(-61),
        CTRL_ERR_TEMPLATE_DECRYPT(-62),
        CTRL_ERR_VERIFY_FAILED(-63),
        CTRL_ERR_VERIFY_NOT_MATCHED(-64),
        CTRL_ERR_GET_QUALITY(-65),
        CTRL_ERR_BASE64_ENCODING_FAILED(-66),
        CTRL_ERR_NO_ENROLLED_DATA(-67),
        CTRL_ERR_ENCRYPT_OPTION_IS_NOT_SET(-68),
        CTRL_ERR_TEMPLATE_TYPE_NOT_IDENTICAL(-69),
        CTRL_ERR_IMAGE_ENCODING_FAILED(-70),
        CTRL_ERR_FORMATING_FAILED(-71),
        CTRL_ERR_DB_ACCESS_FAIL(-72),
        CTRL_ERR_IDENTIFY_NOT_MATCHED(-73),
        CTRL_ERR_DB_INTERNAL_ERROR(-74),
	    CTRL_ERR_IDENTIFY_IS_NOT_RUNNING(-75),
        CTRL_ERR_WEB_READ_DATA(-85),
        CTRL_ERR_WEB_WRITE_DATA(-86),
        // Low-level system error
        CTRL_ERR_DEVICE_MALFUNCTIONING(-90),
        CTRL_ERR_CANNOT_FIND_PERIPHERAL(-91),
        CTRL_ERR_SYS_CONFIG_CORRUPT(-92),
        CTRL_ERR_PERIPHERAL_CTRL_FAILED(-93),
        CTRL_ERR_SET_CONFIG_VALUE(-101),
        CTRL_ERR_GET_CONFIG_VALUE(-102),
        
        ECH_ERR_GENERAL(-110),
        ECH_ERR_NACK(-111),
        ECH_ERR_NOT_RESPOND(-112),
        ECH_ERR_TIMEOUT(-113),
        ECH_ERR_NOT_INITIALIZED(-114),
        ECH_ERR_ABNORMAL_STATE(-115),
        ECH_ERR_INVALID_COMMAND(-116),
        ECH_ERR_INVALID_PARAMETER(-117),
        ECH_ERR_USB_IO(-118),
        ECH_ERR_INVALID_PROTOCOL(-119),
        ECH_ERR_PERMISSION_DENIED(-120),
        ECH_ERR_NO_DEVICE_FOUND(-121),
        ECH_WRN_GENERAL(110),
        ECH_WRN_ALREADY_DONE(111);
        
        private final int mValue;
        Error(int value) {
            mValue = value;
        }
        public int value() {
            return mValue;
        }
    }

    public enum Extra {
        USB_PACKET_64(64),
        USB_PACKET_512(512);

        private final int mValue;
        Extra(int value) {
            mValue = value;
        }
        public int value() {
            return mValue;
        }
    }
    public static byte[] byteToBytes(int short1) {
        byte[] re = new byte[1];
        re[0] = (byte) (short1 & 0xff);
        return re;
    }

    public static byte[] shortToBytes(int short1) {
        byte[] re = new byte[2];
        re[0] = (byte) ((short1 >> 8) & 0xff);
        re[1] = (byte) (short1 & 0xff);
        return re;
    }

    public static byte[] intToBytes(int int1) {
        byte[] re = new byte[4];
        re[0] = (byte) ((int1 >> 24) & 0xff);
        re[1] = (byte) ((int1 >> 16) & 0xff);
        re[2] = (byte) ((int1 >> 8) & 0xff);
        re[3] = (byte) (int1 & 0xff);
        return re;
    }

    public static byte[] stringToBytes(String str) {
        byte[] re = new byte[8];
        re = str.getBytes(Charset.forName("UTF-8"));
        return re;
    }
    
    public static void putByte(byte[] dest, int off, int byte1, int byte2) {
        dest[off] = (byte) byte1;
        dest[off + 1] = (byte) byte2;
    }

    public static void putShort(byte[] dest, int off, int short1) {
        dest[off] = (byte) ((short1 >> 8) & 0xff);
        dest[off + 1] = (byte) (short1 & 0xff);
    }

    public static void putInt(byte[] dest, int off, int int1) {
        dest[off] = (byte) ((int1 >> 24) & 0xff);
        dest[off + 1] = (byte) ((int1 >> 16) & 0xff);
        dest[off + 2] = (byte) ((int1 >> 8) & 0xff);
        dest[off + 3] = (byte) (int1 & 0xff);
    }

    public static void putReverseInt(byte[] dest, int off, int int1) {
        dest[off + 3] = (byte) ((int1 >> 24) & 0xff);
        dest[off + 2] = (byte) ((int1 >> 16) & 0xff);
        dest[off + 1] = (byte) ((int1 >> 8) & 0xff);
        dest[off] = (byte) (int1 & 0xff);
    }

    public static int retrieveInt(byte[] src, int off) {
        int s1 = (int) src[off] & 0xff;
        s1 <<= 24;
        int s2 = (int) src[off + 1] & 0xff;
        s2 <<= 16;
        int s3 = (int) src[off + 2] & 0xff;
        s3 <<= 8;
        int s4 = (int) src[off + 3] & 0xff;
        return s1 | s2 | s3 | s4;
    }

    public static int retrieveShort(byte[] src, int off) {
        int s1 = (int) src[off] & 0xff;
        s1 <<= 8;
        int s2 = (int) src[off + 1] & 0xff;
        return s1 | s2;
    }

    public static int retrieveByte(byte[] src, int off) {
        int s1 = (int) src[off] & 0xff;
        return s1;
    }
    
    public static String retrieveString(byte[] src, int off, int len) {
        byte[] ss = new byte[len];
        String str;
        System.arraycopy(src, off, ss, 0, len);
        try {
            str = new String(ss, "UTF-8").trim();
        } catch (UnsupportedEncodingException ex) {
            str = "";
        }
        return str;
    }

    public static String errString(Error err) {
        return err.toString();
    }

    public static String errString(int err) {
        for (Error error : Error.values()) {
            if(err == error.value()) {
                return error.toString();
            }
        }
        return "UnKnown";
    }
}
