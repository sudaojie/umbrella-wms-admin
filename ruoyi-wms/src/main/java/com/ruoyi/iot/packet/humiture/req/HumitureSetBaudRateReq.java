package com.ruoyi.iot.packet.humiture.req;

import lombok.Data;

/**
 * 修改设备波特率 Req
 */
@Data
public class HumitureSetBaudRateReq {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private String functionCode;

    /**
     * 寄存器地址
     */
    private String registerAddress;


    /**
     * 波特率值内容
     * 01 代表 1200,01 代表 2400,02 代表 4800,03 代表 9600，,04代表 14400，,05 代表 19200
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

    public HumitureSetBaudRateReq() {
        this.addressCode = "01";
        this.functionCode = "06";
        this.registerAddress = "0101";
        this.baudRateVal = "0001";
        this.checkCodeLow = "00";
        this.checkCodeHigh = "00";
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
