package com.ruoyi.iot.packet.smoke.req;

import lombok.Data;

/**
 * 修改烟雾传感器地址 Req
 */
@Data
public class SmokeSetDeviceAddressReq {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private String functionCode;

    /**
     * 寄存器起始地址
     */
    private String registerAddress;

    /**
     * 设备地址
     */
    private String deviceAddress;


    /**
     * 校验码低位
     */
    private String checkCodeLow;


    /**
     * 校验码高位
     */
    private String checkCodeHigh;

    public SmokeSetDeviceAddressReq() {
        this.addressCode = "01";
        this.functionCode = "06";
        this.registerAddress = "0101";
        this.deviceAddress = "0002";
        this.checkCodeLow = "09";
        this.checkCodeHigh = "F7";
    }

    /**
     * 对象属性拼接成，16进制字符串
     *
     * @return
     */
    @Override
    public String toString() {
        return this.addressCode +
                this.functionCode +
                this.registerAddress +
                this.deviceAddress +
                this.checkCodeLow +
                this.checkCodeHigh;
    }

}
