package com.ruoyi.iot.packet.light.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 读离散输入Req
 */
@Data
public class LightReadDisCreteInputValReq {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private String functionCode;


    /**
     * 开始离散地址
     */
    private String startAddress;


    /**
     * 结束离散地址
     */
    private String endAddress;

    /**
     * 读取数量
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


    public LightReadDisCreteInputValReq() {
        this.addressCode = "01";
        this.functionCode = "02";
        this.startAddress = "00";
        this.endAddress = "00";
        this.dataLength = "0008";
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
                this.dataLength;
        return str + WcsCheckCrc16Util.getCRC(str);
    }
}
