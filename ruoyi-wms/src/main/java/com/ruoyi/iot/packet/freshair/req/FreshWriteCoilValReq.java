package com.ruoyi.iot.packet.freshair.req;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 写单个线圈Req
 */
@Data
public class FreshWriteCoilValReq {

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
     * 控制方式 高字节 00-断开 FF-闭合  低字节-01
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


    public FreshWriteCoilValReq() {
        this.addressCode = "01";
        this.functionCode = "05";
        this.startAddress = "00";
        this.endAddress = "00";
        //FF00 开 0000 关
        this.controlMethod = "0000";
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
                this.controlMethod;
        return str + WcsCheckCrc16Util.getCRC(str);
    }
}
