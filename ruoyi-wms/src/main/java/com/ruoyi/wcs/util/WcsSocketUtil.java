package com.ruoyi.wcs.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * socket工具类
 */
public class WcsSocketUtil {

    public static void closeStream(DataOutputStream os, DataInputStream is) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] receive(DataInputStream is) throws Exception {
        try {
            byte[] inputData = new byte[is.available()];
            is.read(inputData);
            return inputData;
        } catch (Exception exception) {
            throw exception;
        }
    }
}
