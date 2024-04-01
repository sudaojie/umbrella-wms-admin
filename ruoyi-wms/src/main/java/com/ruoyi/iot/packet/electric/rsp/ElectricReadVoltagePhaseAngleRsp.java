package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取电压相角 Rsp
 */
@Data
public class ElectricReadVoltagePhaseAngleRsp {

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
     * 电压 UA 相角
     */
    private double voltageUaPhaseAngle;

    /**
     * 电压 UB 相角
     */
    private double voltageUbPhaseAngle;

    /**
     * 电压 UC 相角
     */
    private double voltageUcPhaseAngle;


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
     * @return electricReadVoltagePhaseAngleRsp electricReadVoltagePhaseAngleRsp
     */
    public static ElectricReadVoltagePhaseAngleRsp hexStrToObj(String hexStr) {
        ElectricReadVoltagePhaseAngleRsp electricReadVoltagePhaseAngleRsp = new ElectricReadVoltagePhaseAngleRsp();
        electricReadVoltagePhaseAngleRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2), 16));
        electricReadVoltagePhaseAngleRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadVoltagePhaseAngleRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        electricReadVoltagePhaseAngleRsp.setVoltageUaPhaseAngle(Integer.parseInt(hexStr.substring(6, 10), 16) / 10.0);
        electricReadVoltagePhaseAngleRsp.setVoltageUbPhaseAngle(Integer.parseInt(hexStr.substring(10, 14), 16) / 10.0);
        electricReadVoltagePhaseAngleRsp.setVoltageUcPhaseAngle(Integer.parseInt(hexStr.substring(14, 18), 16) / 10.0);
        electricReadVoltagePhaseAngleRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(18, 20), 16));
        electricReadVoltagePhaseAngleRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(20, 22), 16));
        return electricReadVoltagePhaseAngleRsp;
    }

}
