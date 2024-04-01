package com.ruoyi.iot.packet.humiture.rsp;

import lombok.Data;

/**
 * 读取某个设备的温湿度 Rsp
 */
@Data
public class HumitureReadRsp {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 有效字节数
     */
    private Integer effectiveByteCount;


    /**
     * 湿度值
     */
    private Double humidityVal;

    /**
     * 温度值
     */
    private Double temperatureVal;

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
    public static int RSPDATA_LENGTH = 18/2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static HumitureReadRsp hexStrToObj(String hexStr) {
        HumitureReadRsp humitureReadRsp = new HumitureReadRsp();
        humitureReadRsp.setAddressCode(hexStr.substring(0, 2));
        humitureReadRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        humitureReadRsp.setEffectiveByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        humitureReadRsp.setHumidityVal(Integer.parseInt(hexStr.substring(6, 10), 16) / 10.0);
        humitureReadRsp.setTemperatureVal(Integer.parseInt(hexStr.substring(10, 14), 16) / 10.0);
        humitureReadRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(14, 16), 16));
        humitureReadRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(16, 18), 16));
        return humitureReadRsp;
    }

}
