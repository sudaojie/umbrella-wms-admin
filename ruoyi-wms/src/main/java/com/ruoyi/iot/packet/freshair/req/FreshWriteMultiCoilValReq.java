package com.ruoyi.iot.packet.freshair.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 写多个线圈Req
 */
@Data
public class FreshWriteMultiCoilValReq {

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
    public FreshWriteMultiCoilValReq() {
        this.addressCode = "A4";
        this.functionCode = "05";
        this.coilAddress = "0000";
        this.controlMethod = "FF00";
        this.checkCodeLow = "";
        this.checkCodeHigh = "";
    }

    public FreshWriteMultiCoilValReq(String addressCode, String functionCode, String coilAddress,
                                    String controlMethod, String checkCodeLow, String checkCodeHigh) {
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
        return str + WcsCheckCrc16Util.getCRC(str);
    }
}
