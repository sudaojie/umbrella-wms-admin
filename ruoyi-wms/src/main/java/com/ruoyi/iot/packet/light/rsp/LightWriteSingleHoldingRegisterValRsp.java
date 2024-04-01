package com.ruoyi.iot.packet.light.rsp;

import lombok.Data;

/**
 * 写单个寄存器Rsp
 */
@Data
public class LightWriteSingleHoldingRegisterValRsp {

    /**
     * 地址码
     */
    private Integer addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 开始地址
     */
    private Integer startAddress;

    /**
     * 结束地址
     */
    private Integer endAddress;

    /**
     * 写入值
     */
    private Integer writeVal;

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
    public static int RSPDATA_LENGTH = 16/2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static LightWriteSingleHoldingRegisterValRsp hexStrToObj(String hexStr) {
        LightWriteSingleHoldingRegisterValRsp lightWriteSingleHoldingRegisterValRsp = new LightWriteSingleHoldingRegisterValRsp();
        lightWriteSingleHoldingRegisterValRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        lightWriteSingleHoldingRegisterValRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        lightWriteSingleHoldingRegisterValRsp.setStartAddress(Integer.parseInt(hexStr.substring(4,6),16));
        lightWriteSingleHoldingRegisterValRsp.setEndAddress(Integer.parseInt(hexStr.substring(6,8),16));
        lightWriteSingleHoldingRegisterValRsp.setWriteVal(Integer.parseInt(hexStr.substring(8, hexStr.length() - 5),16));
        lightWriteSingleHoldingRegisterValRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(hexStr.length() - 5, hexStr.length() - 3),16));
        lightWriteSingleHoldingRegisterValRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(hexStr.length() - 3),16));
        return lightWriteSingleHoldingRegisterValRsp;
    }

}
