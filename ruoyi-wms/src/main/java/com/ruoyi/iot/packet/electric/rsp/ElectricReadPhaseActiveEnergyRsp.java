package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取总有功电能 Rsp
 */
@Data
public class ElectricReadPhaseActiveEnergyRsp {

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
     * 总有功电能
     */
    private Double totalActiveEnergy;


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
    public static int RSPDATA_LENGTH = 18 / 2;


    /**
     * 16进制字符串解析成响应对象
     * @param  hexStr hexStr
     * @return electricReadPhaseActivePowerRsp electricReadPhaseActivePowerRsp
     */
    public static ElectricReadPhaseActiveEnergyRsp hexStrToObj(String hexStr,String hexStrPt) {
        ElectricReadPhaseActiveEnergyRsp electricReadPhaseActivePowerRsp = new ElectricReadPhaseActiveEnergyRsp();
        electricReadPhaseActivePowerRsp.setAddressCode(hexStr.substring(0, 2));
        electricReadPhaseActivePowerRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadPhaseActivePowerRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        int hexInt  = Integer.parseInt(hexStr.substring(6, 10), 16);
        int hexInt40  = Integer.parseInt(hexStr.substring(10, 14), 16);
        //pt/ct
        int hexPt =  Integer.parseInt(hexStrPt.substring(6, 10), 16);
        int hexCt =  Integer.parseInt(hexStrPt.substring(10, 14), 16);
        //电能通讯读出值 Val_t＝第一个 word×65536+第二个 word
        //电能量一次侧值 Val_s＝Val_t/1000×PT×CT，有功电能单位：千瓦时（kWh），无
        int hexIntTotal  = hexInt * 65536 + hexInt40 / 1000 * hexPt * hexCt;
        electricReadPhaseActivePowerRsp.setTotalActiveEnergy((double)hexIntTotal);
        electricReadPhaseActivePowerRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(14, 16), 16));
        electricReadPhaseActivePowerRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(16, 18), 16));
        return electricReadPhaseActivePowerRsp;
    }

}
