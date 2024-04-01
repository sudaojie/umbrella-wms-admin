package com.ruoyi.iot.packet.humiture.rsp;

import lombok.Data;

/**
 * 读取设备波特率 Rsp
 */
@Data
public class HumitureReadBaudRateRsp {

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
     * 当前波特率
     * 01 代表 1200,01 代表 2400,02 代表 4800,03 代表 9600，,04代表 14400，,05 代表 19200
     */
    private Integer baudRateVal;


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
    public static HumitureReadBaudRateRsp hexStrToObj(String hexStr) {
        HumitureReadBaudRateRsp humitureReadBaudRateRsp = new HumitureReadBaudRateRsp();
        humitureReadBaudRateRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        humitureReadBaudRateRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        humitureReadBaudRateRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4,6),16));
        humitureReadBaudRateRsp.setBaudRateVal(Integer.parseInt(hexStr.substring(6,10),16));
        humitureReadBaudRateRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(10,12),16));
        humitureReadBaudRateRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(12,14),16));
        return humitureReadBaudRateRsp;
    }

}
