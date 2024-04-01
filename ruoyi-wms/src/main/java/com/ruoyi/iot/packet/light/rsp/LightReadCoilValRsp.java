package com.ruoyi.iot.packet.light.rsp;

import com.ruoyi.wcs.util.WcsCheckCrc16Util;
import lombok.Data;

/**
 * 读取线圈测量值Rsp
 */
@Data
public class LightReadCoilValRsp {

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
     * 线圈状态
     */
    private String coilStatus;


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
    public static int RSPDATA_LENGTH = 12/2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @return
     */
    public static LightReadCoilValRsp hexStrToObj(String hexStr) {
        LightReadCoilValRsp lightReadCoilValRsp = new LightReadCoilValRsp();
        lightReadCoilValRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2),16));
        lightReadCoilValRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4),16));
        lightReadCoilValRsp.setDataLength(Integer.parseInt(hexStr.substring(4,6),16));
        lightReadCoilValRsp.setCoilStatus(WcsCheckCrc16Util.hexString2BinaryString(hexStr.substring(6,8)));
        lightReadCoilValRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(8,10),16));
        lightReadCoilValRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(10,12),16));
        return lightReadCoilValRsp;
    }

}
