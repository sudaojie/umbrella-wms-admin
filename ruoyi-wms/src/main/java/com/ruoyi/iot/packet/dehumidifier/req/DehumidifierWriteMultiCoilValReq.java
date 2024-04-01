package com.ruoyi.iot.packet.dehumidifier.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 写多个线圈Req
 */
@Data
public class DehumidifierWriteMultiCoilValReq {

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
     * 线圈数量
     */
    private String coilAddress;

    /**
     * 写入字节数
     */
    private String writeBytesLength;

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


    public DehumidifierWriteMultiCoilValReq() {
        this.addressCode = "01";
        this.functionCode = "0F";
        this.startAddress = "00";
        this.endAddress = "00";
        this.coilAddress = "0008";
        this.writeBytesLength = "01";
        this.controlMethod = "00";
        this.checkCodeLow = "";
        this.checkCodeHigh = "";
    }

    public DehumidifierWriteMultiCoilValReq(String addressCode, String functionCode, String startAddress, String endAddress, String coilAddress,
                                            String writeBytesLength, String controlMethod, String checkCodeLow, String checkCodeHigh) {
        this.addressCode = addressCode;
        this.functionCode = functionCode;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.coilAddress = coilAddress;
        this.controlMethod = controlMethod;
        this.writeBytesLength = writeBytesLength;
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
                this.coilAddress +
                this.writeBytesLength +
                this.controlMethod;
        return str + WcsCheckCrc16Util.getCRC(str);
    }
}
