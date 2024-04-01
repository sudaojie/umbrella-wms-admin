package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取通讯地址 Rsp
 */
@Data
public class ElectricReadAddressRsp {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 返回有效字节数
     */
    private Integer returnEvalByteCount;


    /**
     * 当前地址
     */
    private Integer currentAddress;


    /**
     * 校验码低位
     */
    private Integer checkCodeLow;


    /**
     * 校验码高位
     */
    private Integer checkCodeHigh;

    /**
     * 响应报文字节数
     */
    public static int RSPDATA_LENGTH = 14 / 2;


    /**
     * 16进制字符串解析成响应对象
     * @param  hexStr hexStr
     * @return electricReadAddressRsp electricReadAddressRsp
     */
    public static ElectricReadAddressRsp hexStrToObj(String hexStr) {
        ElectricReadAddressRsp electricReadAddressRsp = new ElectricReadAddressRsp();
        electricReadAddressRsp.setAddressCode(hexStr.substring(0, 2));
        electricReadAddressRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadAddressRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        electricReadAddressRsp.setCurrentAddress(Integer.parseInt(hexStr.substring(6, 10), 16));
        electricReadAddressRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(10, 12), 16));
        electricReadAddressRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(12, 14), 16));
        return electricReadAddressRsp;
    }

}
