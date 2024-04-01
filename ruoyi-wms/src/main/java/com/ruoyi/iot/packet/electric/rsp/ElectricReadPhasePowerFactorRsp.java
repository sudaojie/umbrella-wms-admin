package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取相功率因数 Rsp
 */
@Data
public class ElectricReadPhasePowerFactorRsp {

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
     * A相功率因数
     */
    private Integer phasePowerFactorA;

    /**
     * B相功率因数
     */
    private Integer phasePowerFactorB;

    /**
     * C相功率因数
     */
    private Integer phasePowerFactorC;

    /**
     * 总功率因数
     */
    private Integer totalPhasePowerFactor;


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
    public static int RSPDATA_LENGTH = 26 / 2;


    /**
     * 16进制字符串解析成响应对象
     * @param  hexStr hexStr
     * @return electricReadPhasePowerFactorRsp electricReadPhasePowerFactorRsp
     */
    public static ElectricReadPhasePowerFactorRsp hexStrToObj(String hexStr) {
        ElectricReadPhasePowerFactorRsp electricReadPhasePowerFactorRsp = new ElectricReadPhasePowerFactorRsp();
        electricReadPhasePowerFactorRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2), 16));
        electricReadPhasePowerFactorRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadPhasePowerFactorRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        electricReadPhasePowerFactorRsp.setPhasePowerFactorA(Integer.parseInt(hexStr.substring(6, 10), 16));
        electricReadPhasePowerFactorRsp.setPhasePowerFactorB(Integer.parseInt(hexStr.substring(10, 14), 16));
        electricReadPhasePowerFactorRsp.setPhasePowerFactorC(Integer.parseInt(hexStr.substring(14, 18), 16));
        electricReadPhasePowerFactorRsp.setTotalPhasePowerFactor(Integer.parseInt(hexStr.substring(18, 22)));
        electricReadPhasePowerFactorRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(22, 24), 16));
        electricReadPhasePowerFactorRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(24, 26), 16));
        return electricReadPhasePowerFactorRsp;
    }

}
