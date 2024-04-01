package com.ruoyi.iot.packet.humiture.rsp;

import lombok.Data;

/**
 * 修改设备波特率 Rsp
 */
@Data
public class HumitureSetBaudRateRsp {

    /**
     * 地址码
     */
    private Integer addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 寄存器地址
     */
    private Integer registerAddress;


    /**
     * 波特率值内容
     * 01 代表 1200,01 代表 2400,02 代表 4800,03 代表 9600，,04代表 14400，,05 代表 19200
     */
    private Integer baudRateVal;


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
    public static int RSPDATA_LENGTH = 16/2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static HumitureSetBaudRateRsp hexStrToObj(String hexStr) {
        HumitureSetBaudRateRsp humitureBaudRateRsp = new HumitureSetBaudRateRsp();
        humitureBaudRateRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        humitureBaudRateRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        humitureBaudRateRsp.setRegisterAddress(Integer.parseInt(hexStr.substring(4,8),16));
        humitureBaudRateRsp.setBaudRateVal(Integer.parseInt(hexStr.substring(8,12),16));
        humitureBaudRateRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(12,14),16));
        humitureBaudRateRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(14,16),16));
        return humitureBaudRateRsp;
    }

}
