package com.ruoyi.iot.packet.dehumidifier.rsp;

import lombok.Data;

/**
 * 写多个线圈Rsp
 */
@Data
public class DehumidifierWriteMultiCoilValRsp {

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
     * 线圈数量
     */
    private Integer coilAddress;


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
    public static int RSPDATA_LENGTH = 12 / 2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static DehumidifierWriteMultiCoilValRsp hexStrToObj(String hexStr) {
        DehumidifierWriteMultiCoilValRsp dehumidifierWriteMultiCoilValRsp = new DehumidifierWriteMultiCoilValRsp();
        dehumidifierWriteMultiCoilValRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2), 16));
        dehumidifierWriteMultiCoilValRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        dehumidifierWriteMultiCoilValRsp.setStartAddress(Integer.parseInt(hexStr.substring(4, 6), 16));
        dehumidifierWriteMultiCoilValRsp.setEndAddress(Integer.parseInt(hexStr.substring(6, 8), 16));
        dehumidifierWriteMultiCoilValRsp.setCoilAddress(Integer.parseInt(hexStr.substring(8, 12), 16));
//        freshWriteMultiCoilValRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(12, 14), 16));
//        freshWriteMultiCoilValRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(14, 16), 16));
        return dehumidifierWriteMultiCoilValRsp;
    }

}
