package com.ruoyi.iot.packet.humiture.rsp;

import lombok.Data;

/**
 * 读温度校准值 Rsp
 */
@Data
public class HumitureReadTempCorrectRsp {

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
     * 温度校准值
     */
    private Integer temperatureCorrectVal;


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
    public static HumitureReadTempCorrectRsp hexStrToObj(String hexStr) {
        HumitureReadTempCorrectRsp humitureReadCorrectRsp = new HumitureReadTempCorrectRsp();
        humitureReadCorrectRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        humitureReadCorrectRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        humitureReadCorrectRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4,6),16));
        humitureReadCorrectRsp.setTemperatureCorrectVal(Integer.parseInt(hexStr.substring(6,10),16));
        humitureReadCorrectRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(10,12),16));
        humitureReadCorrectRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(12,14),16));
        return humitureReadCorrectRsp;
    }

}
