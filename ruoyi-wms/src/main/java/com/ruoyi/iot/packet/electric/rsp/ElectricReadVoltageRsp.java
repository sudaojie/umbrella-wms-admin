package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取相电压 Rsp
 */
@Data
public class ElectricReadVoltageRsp {

    /**
     * 地址码
     */
    private String addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 返回有效字节数
     */
    private Integer returnEvalByteCount;


    /**
     * 相电压UA
     */
    private Double phaseVoltageA;

    /**
     * 相电压UB
     */
    private Double phaseVoltageB;

    /**
     * 相电压UC
     */
    private Double phaseVoltageC;


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
     * @return electricReadVoltageRsp electricReadVoltageRsp
     */
    public static ElectricReadVoltageRsp hexStrToObj(String hexStr,String dpt) {
        ElectricReadVoltageRsp electricReadVoltageRsp = new ElectricReadVoltageRsp();
        electricReadVoltageRsp.setAddressCode(hexStr.substring(0, 2));
        electricReadVoltageRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadVoltageRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        //1. 相电压 UA、UB、UC、线电压 UAB、UBC、UCA、零序电压：
        //Val_s=Val_t×10＾（DPT-4），单位 伏 V，DPT 从 0023H 高字节读出。
        int hexIntDpt = Integer.parseInt(dpt.substring(6, 8), 16);
        int hexIntA  = Integer.parseInt(hexStr.substring(6, 10), 16);
        double hexIntAResult = hexIntA * Math.pow(10, (hexIntDpt - 4));
        electricReadVoltageRsp.setPhaseVoltageA(hexIntAResult);
        int hexIntB = Integer.parseInt(hexStr.substring(10, 14), 16);
        double hexIntBResult = hexIntB * Math.pow(10, (hexIntDpt - 4));
        electricReadVoltageRsp.setPhaseVoltageB(hexIntBResult);
        int hexIntC = Integer.parseInt(hexStr.substring(10, 14), 16);
        double hexIntCResult = hexIntC * Math.pow(10, (hexIntDpt - 4));
        electricReadVoltageRsp.setPhaseVoltageC(hexIntCResult);
        electricReadVoltageRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(18, 20), 16));
        electricReadVoltageRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(20, 22), 16));
        return electricReadVoltageRsp;
    }

}
