package com.ruoyi.iot.packet.light.rsp;

import lombok.Data;

/**
 * 写单个线圈Rsp
 */
@Data
public class LightWriteCoilValRsp {

    /**
     * 地址码
     */
    private Integer addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 线圈地址
     */
    private Integer coilAddress;


    /**
     * 控制方式
     */
//    private Integer controlMethod;


    /**
     * 校验码低位
     */
    private Integer checkCodeLow;


    /**
     * 校验码高位
     */
    private Integer checkCodeHigh;

    /**
     * 响应报文字节数
     */
    public static int RSPDATA_LENGTH = 12/2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static LightWriteCoilValRsp hexStrToObj(String hexStr) {
        LightWriteCoilValRsp lightWriteCoilValRsp = new LightWriteCoilValRsp();
        lightWriteCoilValRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        lightWriteCoilValRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        lightWriteCoilValRsp.setCoilAddress(Integer.parseInt(hexStr.substring(4,8),16));
//        lightWriteCoilValRsp.setControlMethod(Integer.parseInt(hexStr.substring(8,12),16));
        lightWriteCoilValRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(8,10),16));
        lightWriteCoilValRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(10,12),16));
        return lightWriteCoilValRsp;
    }

}
