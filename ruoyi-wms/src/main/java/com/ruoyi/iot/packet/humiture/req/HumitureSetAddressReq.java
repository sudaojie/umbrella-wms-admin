package com.ruoyi.iot.packet.humiture.req;

import lombok.Data;

/**
 * 修改设备地址 Req
 */
@Data
public class HumitureSetAddressReq {

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
     * 温度校准值内容
     */
    private String tempCorrectionVal;


    /**
     * 校验码低位
     */
    private String checkCodeLow;


    /**
     * 校验码高位
     */
    private String checkCodeHigh;

    public HumitureSetAddressReq() {
        this.addressCode = "01";
        this.functionCode = "06";
        this.registerAddress = "0100";
        this.tempCorrectionVal = "0002";
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
                this.tempCorrectionVal +
                this.checkCodeLow +
                this.checkCodeHigh;
    }


}
