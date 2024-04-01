package com.ruoyi.iot.packet.light.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 写单个保持寄存器Req
 */
@Data
public class LightWriteSingleHoldingRegisterValReq {

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


    public LightWriteSingleHoldingRegisterValReq() {
        this.addressCode = "01";
        this.functionCode = "06";
        this.startAddress = "00";
        this.endAddress = "C8";
        this.writeVal = "0001";
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
                this.writeVal;
        return str + WcsCheckCrc16Util.getCRC(str);
    }
}
