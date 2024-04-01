package com.ruoyi.iot.packet.humiture.req;

import lombok.Data;

/**
 * 读温度校准值 Req
 */
@Data
public class HumitureReadTempCorrectReq {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private String functionCode;


    /**
     * 开始地址
     */
    private String startAddress;


    /**
     * 结束地址
     */
    private String endAddress;


    /**
     * 数据长度
     */
    private String dataLength;


    /**
     * 校验码低位
     */
    private String checkCodeLow;


    /**
     * 校验码高位
     */
    private String checkCodeHigh;


    public HumitureReadTempCorrectReq() {
        this.addressCode = "01";
        this.functionCode = "03";
        this.startAddress = "01";
        this.endAddress = "04";
        this.dataLength = "0001";
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
                this.startAddress +
                this.endAddress +
                this.dataLength +
                this.checkCodeLow +
                this.checkCodeHigh;
    }


}
