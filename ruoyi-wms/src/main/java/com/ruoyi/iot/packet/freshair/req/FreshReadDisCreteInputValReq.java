package com.ruoyi.iot.packet.freshair.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 读取离散量（DI）状态
 */
@Data
public class FreshReadDisCreteInputValReq {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private String functionCode;


    /**
     * 线圈地址
     */
    private String startAddress;


    /**
     * 读取离散量数量
     */
    private String endAddress;

    /**
     * 校验码低位
     */
    private String checkCodeLow;


    /**
     * 校验码高位
     */
    private String checkCodeHigh;


    public FreshReadDisCreteInputValReq() {
        this.addressCode = "01";
        this.functionCode = "02";
        this.startAddress = "0000";
        this.endAddress = "0008";
        this.checkCodeLow = "";
        this.checkCodeHigh = "";
    }

    public FreshReadDisCreteInputValReq(String addressCode, String functionCode, String startAddress, String endAddress, String checkCodeLow, String checkCodeHigh) {
        this.addressCode = addressCode;
        this.functionCode = functionCode;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
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
                this.endAddress;
        return str + WcsCheckCrc16Util.getCRC(str);
    }
}
