package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取电能 Rsp
 */
@Data
public class ElectricReadEnergyRsp {

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
     * 吸收有功电能二次侧
     */
    private Integer absorbingActiveSecondarySide;

    /**
     * 释放有功电能二次侧
     */
    private Integer releaseActiveSecondarySide;

    /**
     * 感性无功电能二次侧
     */
    private Integer secondarySideInductiveReactiveEnergy;

    /**
     * 容性无功电能二次侧
     */
    private Integer secondarySideCapacitiveReactiveEnergy;

    /**
     * 吸收有功电能一次侧
     */
    private Integer absorbingActiveFirstSide;

    /**
     * 释放有功电能一次侧
     */
    private Integer releaseActiveFirstSide;

    /**
     * 感性无功电能一次侧
     */
    private Integer firstSideInductiveReactiveEnergy;

    /**
     * 容性无功电能一次侧
     */
    private Integer firstSideCapacitiveReactiveEnergy;

    /**
     * 最大需量
     */
    private Integer maxDemand;

    /**
     * 最大需量发生时间
     */
    private Integer maxDemandTime;


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
    public static int RSPDATA_LENGTH = 86 / 2;


    /**
     * 16进制字符串解析成响应对象
     * @param  hexStr hexStr
     * @return electricReadEnergyRsp electricReadEnergyRsp
     */
    public static ElectricReadEnergyRsp hexStrToObj(String hexStr) {
        ElectricReadEnergyRsp electricReadEnergyRsp = new ElectricReadEnergyRsp();
        electricReadEnergyRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2), 16));
        electricReadEnergyRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadEnergyRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));

        electricReadEnergyRsp.setAbsorbingActiveSecondarySide(Integer.parseInt(hexStr.substring(6, 14), 16));
        electricReadEnergyRsp.setReleaseActiveSecondarySide(Integer.parseInt(hexStr.substring(14, 22), 16));
        electricReadEnergyRsp.setSecondarySideInductiveReactiveEnergy(Integer.parseInt(hexStr.substring(22, 30), 16));
        electricReadEnergyRsp.setSecondarySideCapacitiveReactiveEnergy(Integer.parseInt(hexStr.substring(30, 38), 16));

        electricReadEnergyRsp.setAbsorbingActiveFirstSide(Integer.parseInt(hexStr.substring(38, 46), 16));
        electricReadEnergyRsp.setReleaseActiveFirstSide(Integer.parseInt(hexStr.substring(46, 54), 16));
        electricReadEnergyRsp.setFirstSideInductiveReactiveEnergy(Integer.parseInt(hexStr.substring(54, 62), 16));
        electricReadEnergyRsp.setFirstSideCapacitiveReactiveEnergy(Integer.parseInt(hexStr.substring(62, 70), 16));

        electricReadEnergyRsp.setMaxDemand(Integer.parseInt(hexStr.substring(70, 74), 16));
        electricReadEnergyRsp.setMaxDemandTime(Integer.parseInt(hexStr.substring(74, 82), 16));

        electricReadEnergyRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(82, 84), 16));
        electricReadEnergyRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(84, 86), 16));
        return electricReadEnergyRsp;
    }

}
