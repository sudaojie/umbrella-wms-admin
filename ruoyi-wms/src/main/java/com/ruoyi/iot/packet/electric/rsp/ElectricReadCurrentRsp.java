package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;

/**
 * 读取电流 Rsp
 */
@Data
public class ElectricReadCurrentRsp {

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
     * 电流 IA
     */
    private Double currentA;


    /**
     * 电流 IB
     */
    private Double currentB;


    /**
     * 电流 IC
     */
    private Double currentC;


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
     * @return electricReadCurrentRsp electricReadCurrentRsp
     */
    public static ElectricReadCurrentRsp hexStrToObj(String hexStr,String dct) {
        ElectricReadCurrentRsp electricReadCurrentRsp = new ElectricReadCurrentRsp();
        electricReadCurrentRsp.setAddressCode(hexStr.substring(0, 2));
        electricReadCurrentRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        electricReadCurrentRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16));
        //2. 电流 IA、IB、IC、零序电流：
        //Val_s=Val_t×10＾（DCT-4），单位 安培 A，DCT 从 0023H 低字节读出。
        //Val_s=Val_t×10＾（DPT-4），单位 伏 V，DPT 从 0023H 高字节读出。
        int hexIntDct = Integer.parseInt(dct.substring(8, 10), 16);
        int hexIntA  = Integer.parseInt(hexStr.substring(6, 10), 16);
        double hexIntAResult = hexIntA * Math.pow(10, (hexIntDct - 4));
        electricReadCurrentRsp.setCurrentA(hexIntAResult);
        int hexIntB = Integer.parseInt(hexStr.substring(10, 14), 16);
        double hexIntBResult = hexIntB * Math.pow(10, (hexIntDct - 4));
        electricReadCurrentRsp.setCurrentB(hexIntBResult);
        int hexIntC = Integer.parseInt(hexStr.substring(10, 14), 16);
        double hexIntCResult = hexIntC * Math.pow(10, (hexIntDct - 4));
        electricReadCurrentRsp.setCurrentC(hexIntCResult);
        electricReadCurrentRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(18, 20), 16));
        electricReadCurrentRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(20, 22), 16));
        return electricReadCurrentRsp;
    }

}
