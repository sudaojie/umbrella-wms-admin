package com.ruoyi.iot.packet.humiture.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 读取某个设备的温湿度 Req
 */
@Data
@Slf4j
public class HumitureReadReq {

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

    public HumitureReadReq() {
        this.addressCode = "06";
        this.functionCode = "03";
        this.startAddress = "00";
        this.endAddress = "00";
        this.dataLength = "0002";
        this.checkCodeLow = "";
        this.checkCodeHigh = "";
    }

    public HumitureReadReq(String addressCode, String functionCode, String startAddress, String endAddress,
                           String dataLength, String checkCodeLow, String checkCodeHigh) {
        this.addressCode = addressCode;
        this.functionCode = functionCode;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.dataLength = dataLength;
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
                this.dataLength;
        String message = str + WcsCheckCrc16Util.getCRC(str);
        log.info("读取某个设备的温湿度 send message: {}", message);
        return message;
    }


}
