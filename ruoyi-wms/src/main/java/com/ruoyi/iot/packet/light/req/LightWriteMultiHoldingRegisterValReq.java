package com.ruoyi.iot.packet.light.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 写多个保持寄存器Req
 */
@Data
public class LightWriteMultiHoldingRegisterValReq {

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
     * 寄存器数量
     */
    private String registerCount;

    /**
     * 写入字节数
     */
    private String dataLength;

    /**
     * 写入值
     */
    private String writeVal;

    /**
     * 校验码低位
     */
    private String checkCodeLow;

    /**
     * 校验码高位
     */
    private String checkCodeHigh;


    public LightWriteMultiHoldingRegisterValReq() {
        this.addressCode = "01";
        this.functionCode = "10";
        this.startAddress = "00";
        this.endAddress = "C8";
        this.registerCount = "0002";
        this.dataLength = "04";
        this.writeVal = "00010023";
        this.checkCodeLow = "";
        this.checkCodeHigh = "";
    }

    /**
     * 对象属性拼接成，16进制字符串
     *
     * @return
     */
    @Override
    public String toString() {
        String str = this.addressCode +
                this.functionCode +
                this.startAddress +
                this.endAddress +
                this.registerCount +
                this.dataLength +
                this.writeVal;
        return str + WcsCheckCrc16Util.getCRC(str);
    }
}
