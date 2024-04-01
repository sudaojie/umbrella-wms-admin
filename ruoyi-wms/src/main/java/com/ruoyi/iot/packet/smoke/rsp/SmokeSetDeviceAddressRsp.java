package com.ruoyi.iot.packet.smoke.rsp;

import lombok.Data;

/**
 * 修改烟雾传感器地址 Rsp
 */
@Data
public class SmokeSetDeviceAddressRsp {

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
     * 设备地址
     */
    private Integer deviceAddress;


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
    public static SmokeSetDeviceAddressRsp hexStrToObj(String hexStr) {
        SmokeSetDeviceAddressRsp smokeSetDeviceAddressRsp = new SmokeSetDeviceAddressRsp();
        smokeSetDeviceAddressRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        smokeSetDeviceAddressRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        smokeSetDeviceAddressRsp.setRegisterAddress(Integer.parseInt(hexStr.substring(4,8),16));
        smokeSetDeviceAddressRsp.setDeviceAddress(Integer.parseInt(hexStr.substring(8,12),16));
        smokeSetDeviceAddressRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(12,14),16));
        smokeSetDeviceAddressRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(14,16),16));
        return smokeSetDeviceAddressRsp;
    }

}
