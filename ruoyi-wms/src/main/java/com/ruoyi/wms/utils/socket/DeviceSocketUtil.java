package com.ruoyi.wms.utils.socket;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class DeviceSocketUtil {


    /**
     * 构建输入流和输出流对象
     *
     * @param socket
     * @return
     */
    public static DeviceDataStream buildDataStream(Socket socket) {
        try {
            DeviceDataStream dataStream = new DeviceDataStream();
            DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dataStream.setInput(is);
            dataStream.setOut(os);
            return dataStream;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }


    /**
     * 写入数据
     *
     * @param out    输出流对象
     * @param hexMsg 16进制字符串
     * @return
     */
    public static void writeData(DataOutputStream out, String hexMsg) {
        try {
            // 写入
            out.write(HexUtil.decodeHex(hexMsg));
            Thread.sleep(200);
            out.flush();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }


    /**
     * 读取数据
     *
     * @param in 输入流对象
     * @return
     */
    public static String readData(DataInputStream in) {
        try {
            while (true) {
                // 接收数据
                byte[] inputData = new byte[in.available()];
                in.read(inputData);
                String responseData = HexUtil.encodeHexStr(inputData);
                if (StrUtil.isNotEmpty(responseData)) {
                    log.info("读取到数据,{}", responseData);
                    return responseData;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }


    /**
     * 读取数据
     *
     * @param is  输入流对象
     * @param out 输出流对象
     * @return
     */
    public static void close(DataInputStream is, DataOutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

}
