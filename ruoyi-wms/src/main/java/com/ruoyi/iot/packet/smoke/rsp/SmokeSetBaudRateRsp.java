package com.ruoyi.iot.packet.smoke.rsp;

import lombok.Data;

/**
 * 修改烟雾传感器波特率 Rsp
 */
@Data
public class SmokeSetBaudRateRsp {

    /**
     * 地址码
     */
    private Integer addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 寄存器起始地址
     */
    private Integer registerAddress;



    /**
     * 波特率值
     * 0:1200, 1:2400 , 2:4800, 3:9600, 4:57600, 5:115200
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
    public static SmokeSetBaudRateRsp hexStrToObj(String hexStr) {
        SmokeSetBaudRateRsp smokeSetBaudRateRsp = new SmokeSetBaudRateRsp();
        smokeSetBaudRateRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        smokeSetBaudRateRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        smokeSetBaudRateRsp.setRegisterAddress(Integer.parseInt(hexStr.substring(4,8),16));
        smokeSetBaudRateRsp.setBaudRateVal(Integer.parseInt(hexStr.substring(8,12),16));
        smokeSetBaudRateRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(12,14),16));
        smokeSetBaudRateRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(14,16),16));
        return smokeSetBaudRateRsp;
    }

}
