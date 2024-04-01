package com.ruoyi.iot.packet.electric.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 读取无功功率 Req
 */
@Data
@Slf4j
public class ElectricReadReactivePowerReq {

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


    public ElectricReadReactivePowerReq() {
        this.addressCode = "01";
        this.functionCode = "03";
        this.startAddress = "00";
        this.endAddress = "32";
        this.dataLength = "0004";
        this.checkCodeLow = "";
        this.checkCodeHigh = "";
    }

    public ElectricReadReactivePowerReq(String addressCode, String functionCode, String startAddress, String endAddress,
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
        String message =  str + WcsCheckCrc16Util.getCRC(str);
        log.info("无功功率 send message: {}", message);
        return message;
    }

}
