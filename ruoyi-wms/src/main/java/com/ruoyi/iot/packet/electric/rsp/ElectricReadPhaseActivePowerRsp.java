package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取有功功率 Rsp
 */
@Data
public class ElectricReadPhaseActivePowerRsp {

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
     * A相有功功率
     */
    private Double phaseActivePowerA;

    /**
     * B相有功功率
     */
    private Double phaseActivePowerB;

    /**
     * C相有功功率
     */
    private Double phaseActivePowerC;

    /**
     * 总相有功功率
     */
    private Double totalPhaseActivePower;


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
     * @return electricReadPhaseActivePowerRsp electricReadPhaseActivePowerRsp
     */
    public static ElectricReadPhaseActivePowerRsp hexStrToObj(String hexStr,String dpq) {
        ElectricReadPhaseActivePowerRsp electricReadPhaseActivePowerRsp = new ElectricReadPhaseActivePowerRsp();
        electricReadPhaseActivePowerRsp.setAddressCode(hexStr.substring(0, 2));
        electricReadPhaseActivePowerRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadPhaseActivePowerRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        //3. 功率 PA、PB、PC、P 总、QA、QB、QC、Q 总：
        //Val_s=Val_t×10＾（DPQ-4），有功功率单位 瓦 W,无功功率单位 乏 var，DPQ 从 0024H 高字节读出，有功功
        //率和无功功率的符号从 0024H 低字节（从高到低位依次为 Q、Qc、Qb、Qa、P、Pc、Pb、Pa）读出。
        int hexIntDpq = Integer.parseInt(dpq.substring(10, 12), 16); //要修改
        int hexIntA  = Integer.parseInt(hexStr.substring(6, 10), 16);
        double hexIntAResult = hexIntA * Math.pow(10, (hexIntDpq - 4));
        electricReadPhaseActivePowerRsp.setPhaseActivePowerA(hexIntAResult/1000);
        int hexIntB = Integer.parseInt(hexStr.substring(10, 14), 16);
        double hexIntBResult = hexIntB * Math.pow(10, (hexIntDpq - 4));
        electricReadPhaseActivePowerRsp.setPhaseActivePowerB(hexIntBResult/1000);
        int hexIntC = Integer.parseInt(hexStr.substring(10, 14), 16);
        double hexIntCResult = hexIntC * Math.pow(10, (hexIntDpq - 4));
        electricReadPhaseActivePowerRsp.setPhaseActivePowerC(hexIntCResult/1000);
        int hexIntTotal = Integer.parseInt(hexStr.substring(18, 22), 16);
        double hexIntTotalResult = hexIntTotal * Math.pow(10, (hexIntDpq - 4));
        electricReadPhaseActivePowerRsp.setTotalPhaseActivePower(hexIntTotalResult/1000);
        electricReadPhaseActivePowerRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(22, 24), 16));
        electricReadPhaseActivePowerRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(24, 26), 16));
        return electricReadPhaseActivePowerRsp;
    }

}
