package com.ruoyi.iot.packet.smoke.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 读取变送器测量值Req
 */
@Data
@Slf4j
public class SmokeReadMeasuredValReq {

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

    public SmokeReadMeasuredValReq() {
        this.addressCode = "03";
        this.functionCode = "03";
        this.startAddress = "02";
        this.endAddress = "00";
        this.registerCount = "0001";
        this.checkCodeLow = "";
        this.checkCodeHigh = "";
    }

    public SmokeReadMeasuredValReq(String addressCode, String functionCode, String startAddress,
                                   String endAddress, String registerCount, String checkCodeLow, String checkCodeHigh) {
        this.addressCode = addressCode;
        this.functionCode = functionCode;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.registerCount = registerCount;
        this.checkCodeLow = checkCodeLow;
        this.checkCodeHigh = checkCodeHigh;
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
        String message = str + WcsCheckCrc16Util.getCRC(str);
        log.info("读取烟感报警值 send message: {}", message);
        return message;
    }
}
