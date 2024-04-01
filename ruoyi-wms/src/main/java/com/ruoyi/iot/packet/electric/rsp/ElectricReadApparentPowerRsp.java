package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取相视在功率 Rsp
 */
@Data
public class ElectricReadApparentPowerRsp {

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
     * A 相视在功率
     */
    private Integer apparentPowerA;

    /**
     * B 相视在功率
     */
    private Integer apparentPowerB;

    /**
     * C 相视在功率
     */
    private Integer apparentPowerC;

    /**
     * 总相视在功率
     */
    private Integer totalApparentPower;

    /**
     * 频率F
     */
    private Integer totalApparentF;


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
    public static int RSPDATA_LENGTH = 30 / 2;


    /**
     * 16进制字符串解析成响应对象
     * @param  hexStr hexStr
     * @return electricReadApparentPowerRsp electricReadApparentPowerRsp
     */
    public static ElectricReadApparentPowerRsp hexStrToObj(String hexStr) {
        ElectricReadApparentPowerRsp electricReadApparentPowerRsp = new ElectricReadApparentPowerRsp();
        electricReadApparentPowerRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2), 16));
        electricReadApparentPowerRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadApparentPowerRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        electricReadApparentPowerRsp.setApparentPowerA(Integer.parseInt(hexStr.substring(6, 10), 16));
        electricReadApparentPowerRsp.setApparentPowerB(Integer.parseInt(hexStr.substring(10, 14), 16));
        electricReadApparentPowerRsp.setApparentPowerC(Integer.parseInt(hexStr.substring(14, 18), 16));
        electricReadApparentPowerRsp.setTotalApparentPower(Integer.parseInt(hexStr.substring(18, 22), 16));
        electricReadApparentPowerRsp.setTotalApparentF(Integer.parseInt(hexStr.substring(22, 26), 16));
        electricReadApparentPowerRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(26, 28), 16));
        electricReadApparentPowerRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(28, 30), 16));
        return electricReadApparentPowerRsp;
    }

}
