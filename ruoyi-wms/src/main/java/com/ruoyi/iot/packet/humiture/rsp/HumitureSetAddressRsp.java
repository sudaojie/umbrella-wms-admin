package com.ruoyi.iot.packet.humiture.rsp;

import lombok.Data;

/**
 * 修改设备地址 Rsp
 */
@Data
public class HumitureSetAddressRsp {

    /**
     * 地址码
     */
    private Integer addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 寄存器地址
     */
    private Integer registerAddress;


    /**
     * 温度校准值内容
     */
    private Integer tempCorrectionVal;


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
    public static int RSPDATA_LENGTH = 16/2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static HumitureSetAddressRsp hexStrToObj(String hexStr) {
        HumitureSetAddressRsp humitureReadRsp = new HumitureSetAddressRsp();
        humitureReadRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        humitureReadRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        humitureReadRsp.setRegisterAddress(Integer.parseInt(hexStr.substring(4,8),16));
        humitureReadRsp.setTempCorrectionVal(Integer.parseInt(hexStr.substring(8,12),16));
        humitureReadRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(12,14),16));
        humitureReadRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(14,16),16));
        return humitureReadRsp;
    }

}
