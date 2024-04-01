package com.ruoyi.iot.packet.freshair.rsp;

import lombok.Data;

/**
 * 读取保持寄存器Rsp
 */
@Data
public class FreshReadHoldingRegisterValRsp {

    /**
     * 地址码
     */
    private Integer addressCode;

    /**
     * 功能码
     */
    private Integer functionCode;

    /**
     * 数据长度
     */
    private Integer dataLength;


    /**
     * 寄存器数据
     */
    private String registerVal;


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
    public static int RSPDATA_LENGTH = 22/2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static FreshReadHoldingRegisterValRsp hexStrToObj(String hexStr) {
        FreshReadHoldingRegisterValRsp freshReadHoldingRegisterValRsp = new FreshReadHoldingRegisterValRsp();
        freshReadHoldingRegisterValRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        freshReadHoldingRegisterValRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        freshReadHoldingRegisterValRsp.setDataLength(Integer.parseInt(hexStr.substring(4,6),16));
        freshReadHoldingRegisterValRsp.setRegisterVal(hexStr.substring(6,18));
        freshReadHoldingRegisterValRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(18,20),16));
        freshReadHoldingRegisterValRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(20,22),16));
        return freshReadHoldingRegisterValRsp;
    }

}
