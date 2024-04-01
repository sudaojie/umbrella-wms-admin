package com.ruoyi.iot.packet.light.rsp;

import lombok.Data;

/**
 * 写多个保持寄存器Rsp
 */
@Data
public class LightWriteMultiHoldingRegisterValRsp {

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
     * 寄存器数量
     */
    private Integer registerCount;

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
    public static LightWriteMultiHoldingRegisterValRsp hexStrToObj(String hexStr) {
        LightWriteMultiHoldingRegisterValRsp lightWriteMultiHoldingRegisterValRsp = new LightWriteMultiHoldingRegisterValRsp();
        lightWriteMultiHoldingRegisterValRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        lightWriteMultiHoldingRegisterValRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        lightWriteMultiHoldingRegisterValRsp.setStartAddress(Integer.parseInt(hexStr.substring(4,6),16));
        lightWriteMultiHoldingRegisterValRsp.setEndAddress(Integer.parseInt(hexStr.substring(6,8),16));
        lightWriteMultiHoldingRegisterValRsp.setRegisterCount(Integer.parseInt(hexStr.substring(8,12),16));
        lightWriteMultiHoldingRegisterValRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(12,14),16));
        lightWriteMultiHoldingRegisterValRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(14,16),16));
        return lightWriteMultiHoldingRegisterValRsp;
    }

}
