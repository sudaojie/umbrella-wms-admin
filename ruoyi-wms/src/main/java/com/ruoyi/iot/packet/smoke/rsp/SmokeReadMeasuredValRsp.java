package com.ruoyi.iot.packet.smoke.rsp;

import lombok.Data;

/**
 * 读取变送器测量值Rsp
 */
@Data
public class SmokeReadMeasuredValRsp {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 数据长度
     */
    private Integer dataLength;


    /**
     * 感烟报警值
     */
    private Integer smokeAlarmVal;


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
    public static int RSPDATA_LENGTH = 14/2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static SmokeReadMeasuredValRsp hexStrToObj(String hexStr) {
        SmokeReadMeasuredValRsp smokeReadMeasuredValRsp = new SmokeReadMeasuredValRsp();
        smokeReadMeasuredValRsp.setAddressCode(hexStr.substring(0,2));
        smokeReadMeasuredValRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2,4),16));
        smokeReadMeasuredValRsp.setDataLength(Integer.parseInt(hexStr.substring(4,6),16));
        smokeReadMeasuredValRsp.setSmokeAlarmVal(Integer.parseInt(hexStr.substring(6,10),16));
        smokeReadMeasuredValRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(10,12),16));
        smokeReadMeasuredValRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(12,14),16));
        return smokeReadMeasuredValRsp;
    }

}
