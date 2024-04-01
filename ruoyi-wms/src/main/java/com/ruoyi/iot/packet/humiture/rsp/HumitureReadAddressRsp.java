package com.ruoyi.iot.packet.humiture.rsp;

import lombok.Data;

/**
 * 读取设备地址 Rsp
 */
@Data
public class HumitureReadAddressRsp {

    /**
     * 地址码
     */
    private Integer addressCode;

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
    public static int RSPDATA_LENGTH = 14/2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static HumitureReadAddressRsp hexStrToObj(String hexStr) {
        HumitureReadAddressRsp humitureReadAddressRsp = new HumitureReadAddressRsp();
        humitureReadAddressRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        humitureReadAddressRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        humitureReadAddressRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4,6),16));
        humitureReadAddressRsp.setCurrentAddress(Integer.parseInt(hexStr.substring(6,10),16));
        humitureReadAddressRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(10,12),16));
        humitureReadAddressRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(12,14),16));
        return humitureReadAddressRsp;
    }

}
