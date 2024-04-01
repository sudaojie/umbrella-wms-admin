package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取侧零序电压电流 Rsp
 */
@Data
public class ElectricReadSideZeroVoltageCurrentRsp {

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
     * 零序电压
     */
    private Integer zeroSequenceVoltage;

    /**
     * 零序电流
     */
    private Integer zeroSequenceCurrent;

    /**
     * 电流百分比
     */
    private Integer currentPercentage;

    /**
     * 电压电流相序状态
     */
    private Integer voltageCurrentPhaseSequenceStatus;

    /**
     * 运行时间
     */
    private Integer runTime;

    /**
     * 日期时间
     */
    private Integer time;


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
    public static int RSPDATA_LENGTH = 34 / 2;


    /**
     * 16进制字符串解析成响应对象
     * @param  hexStr hexStr
     * @return electricReadSideZeroVoltageCurrentRsp electricReadSideZeroVoltageCurrentRsp
     */
    public static ElectricReadSideZeroVoltageCurrentRsp hexStrToObj(String hexStr) {
        ElectricReadSideZeroVoltageCurrentRsp electricReadSideZeroVoltageCurrentRsp = new ElectricReadSideZeroVoltageCurrentRsp();
        electricReadSideZeroVoltageCurrentRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2), 16));
        electricReadSideZeroVoltageCurrentRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadSideZeroVoltageCurrentRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        electricReadSideZeroVoltageCurrentRsp.setZeroSequenceVoltage(Integer.parseInt(hexStr.substring(6, 10), 16));
        electricReadSideZeroVoltageCurrentRsp.setZeroSequenceCurrent(Integer.parseInt(hexStr.substring(10, 14), 16));
        electricReadSideZeroVoltageCurrentRsp.setCurrentPercentage(Integer.parseInt(hexStr.substring(14, 18), 16));
        electricReadSideZeroVoltageCurrentRsp.setVoltageCurrentPhaseSequenceStatus(Integer.parseInt(hexStr.substring(18, 22), 16));
        electricReadSideZeroVoltageCurrentRsp.setRunTime(Integer.parseInt(hexStr.substring(22, 26), 16));
        electricReadSideZeroVoltageCurrentRsp.setTime(Integer.parseInt(hexStr.substring(26, 30), 16));
        electricReadSideZeroVoltageCurrentRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(30, 32), 16));
        electricReadSideZeroVoltageCurrentRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(32, 34), 16));
        return electricReadSideZeroVoltageCurrentRsp;
    }

}
