package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取线电压 Rsp
 */
@Data
public class ElectricReadLineVoltageRsp {

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
     * 线电压 UAB
     */
    private Integer abLineVoltage;

    /**
     * 线电压 UBC
     */
    private Integer bcLineVoltage;

    /**
     * 线电压 UAC
     */
    private Integer acLineVoltage;


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
    public static int RSPDATA_LENGTH = 22 / 2;


    /**
     * 16进制字符串解析成响应对象
     * @param  hexStr hexStr
     * @return electricReadLineVoltageRsp electricReadLineVoltageRsp
     */
    public static ElectricReadLineVoltageRsp hexStrToObj(String hexStr) {
        ElectricReadLineVoltageRsp electricReadLineVoltageRsp = new ElectricReadLineVoltageRsp();
        electricReadLineVoltageRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2), 16));
        electricReadLineVoltageRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadLineVoltageRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        electricReadLineVoltageRsp.setAbLineVoltage(Integer.parseInt(hexStr.substring(6, 10), 16));
        electricReadLineVoltageRsp.setBcLineVoltage(Integer.parseInt(hexStr.substring(10, 14), 16));
        electricReadLineVoltageRsp.setAcLineVoltage(Integer.parseInt(hexStr.substring(14, 18), 16));
        electricReadLineVoltageRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(18, 20), 16));
        electricReadLineVoltageRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(20, 22), 16));
        return electricReadLineVoltageRsp;
    }

}
