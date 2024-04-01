package com.ruoyi.iot.packet.smoke.req;

import lombok.Data;

/**
 * 修改烟雾传感器波特率 Req
 */
@Data
public class SmokeSetBaudRateReq {

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
     * 波特率值
     * 0:1200, 1:2400 , 2:4800, 3:9600, 4:57600, 5:115200
     */
    private String baudRateVal;


    /**
     * 校验码低位
     */
    private String checkCodeLow;


    /**
     * 校验码高位
     */
    private String checkCodeHigh;

    public SmokeSetBaudRateReq() {
        this.addressCode = "01";
        this.functionCode = "06";
        this.registerAddress = "0101";
        this.baudRateVal = "0005";
        this.checkCodeLow = "F8";
        this.checkCodeHigh = "31";
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
                this.baudRateVal +
                this.checkCodeLow +
                this.checkCodeHigh;
    }

}
