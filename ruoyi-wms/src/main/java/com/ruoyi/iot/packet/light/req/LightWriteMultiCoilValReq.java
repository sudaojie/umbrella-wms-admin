package com.ruoyi.iot.packet.light.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * 写多个线圈Req
 */
@Data
@Accessors(chain = true)
@Slf4j
public class LightWriteMultiCoilValReq {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private String functionCode;

    /**
     * 线圈数量
     */
    private String coilAddress;

    /**
     * 控制方式 00-全部断开 0F-全部闭合
     */
    private String controlMethod;

    /**
     * 校验码低位
     */
    private String checkCodeLow;


    /**
     * 校验码高位
     */
    private String checkCodeHigh;

    //A4050000FF00
    // 每个线圈的状态对应一个 bit 的数据，8 个线圈刚好对应一个字节的数据，如果单次写入 9-16
    //个线圈的数据，字节数就是 2，以此类推。数据 0x05 的二进制表示为 00000101，表示 DO0 和 DO2 状态为 1，其余 DO 状态为 0
    public LightWriteMultiCoilValReq() {
        this.addressCode = "A4";
        this.functionCode = "05";
        this.coilAddress = "0004";
        this.controlMethod = "FF00";
        this.checkCodeLow = "";
        this.checkCodeHigh = "";
    }

    public LightWriteMultiCoilValReq(String addressCode, String functionCode,String coilAddress, String controlMethod,
                                      String checkCodeLow, String checkCodeHigh) {
        this.addressCode = addressCode;
        this.functionCode = functionCode;
        this.coilAddress = coilAddress;
        this.controlMethod = controlMethod;
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
                this.coilAddress +
                this.controlMethod;
        String message = str + WcsCheckCrc16Util.getCRC(str);
        log.info("写单个线圈-控制照明开关 send message: {}", message);
        return message;
    }
}
