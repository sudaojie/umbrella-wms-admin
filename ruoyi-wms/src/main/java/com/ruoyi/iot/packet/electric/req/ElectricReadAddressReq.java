package com.ruoyi.iot.packet.electric.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 读取通讯地址 Req
 */
@Data
public class ElectricReadAddressReq {

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


    public ElectricReadAddressReq() {
        this.addressCode = "01";
        this.functionCode = "03";
        this.startAddress = "00";
        this.endAddress = "01";
        this.dataLength = "0001";
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
