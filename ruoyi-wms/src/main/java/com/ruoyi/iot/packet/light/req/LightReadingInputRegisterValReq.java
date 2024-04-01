package com.ruoyi.iot.packet.light.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 读取输入寄存器Req
 */
@Data
public class LightReadingInputRegisterValReq {

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
     * 校验码低位
     */
    private String checkCodeLow;


    /**
     * 校验码高位
     */
    private String checkCodeHigh;


    public LightReadingInputRegisterValReq() {
        this.addressCode = "01";
        this.functionCode = "04";
        this.startAddress = "00";
        this.endAddress = "64";
        this.registerCount = "0003";
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
                this.registerCount;
        return str + WcsCheckCrc16Util.getCRC(str);
    }
}
