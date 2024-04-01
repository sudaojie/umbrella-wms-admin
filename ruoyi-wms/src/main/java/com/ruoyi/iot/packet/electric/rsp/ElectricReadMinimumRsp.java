package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 读取极小值 Rsp
 */
@Data
@Accessors(chain = true)
public class ElectricReadMinimumRsp {

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
     * A 相电压极小值
     */
    private Integer minimumAphaseVoltage;

    /**
     * A 相电压极小值发生年月 高位：年，低位：月
     */
    private String yearMinimumAphaseVoltage;

    /**
     * A 相电压极小值发生日时  高位：日，低位：时
     */
    private String dayMinimumAphaseVoltage;

    /**
     * A 相电压极小值发生分秒   高位：分，低位：秒
     */
    private String minuteMinimumAphaseVoltage;

    /**
     * B 相电压极小值
     */
    private Integer minimumBphaseVoltage;

    /**
     * B 相电压极小值发生年月 高位：年，低位：月
     */
    private String yearMinimumBphaseVoltage;

    /**
     * B 相电压极小值发生日时  高位：日，低位：时
     */
    private String dayMinimumBphaseVoltage;

    /**
     * B 相电压极小值发生分秒   高位：分，低位：秒
     */
    private String minuteMinimumBphaseVoltage;

    /**
     * C 相电压极小值
     */
    private Integer minimumCphaseVoltage;

    /**
     * C 相电压极小值发生年月 高位：年，低位：月
     */
    private String yearMinimumCphaseVoltage;

    /**
     * C 相电压极小值发生日时  高位：日，低位：时
     */
    private String dayMinimumCphaseVoltage;

    /**
     * C 相电压极小值发生分秒   高位：分，低位：秒
     */
    private String minuteMinimumCphaseVoltage;

    /**
     * A 线电压极小值
     */
    private Integer minimumAlineVoltage;

    /**
     * A 线电压极小值发生年月 高位：年，低位：月
     */
    private String yearMinimumAlineVoltage;

    /**
     * A 线电压极小值发生日时  高位：日，低位：时
     */
    private String dayMinimumAlineVoltage;

    /**
     * A 线电压极小值发生分秒   高位：分，低位：秒
     */
    private String minuteMinimumAlineVoltage;

    /**
     * B 线电压极小值
     */
    private Integer minimumBlineVoltage;

    /**
     * B 线电压极小值发生年月 高位：年，低位：月
     */
    private String yearMinimumBlineVoltage;

    /**
     * B 线电压极小值发生日时  高位：日，低位：时
     */
    private String dayMinimumBlineVoltage;

    /**
     * B 线电压极小值发生分秒   高位：分，低位：秒
     */
    private String minuteMinimumBlineVoltage;

    /**
     * C 线电压极小值
     */
    private Integer minimumClineVoltage;

    /**
     * C 线电压极小值发生年月 高位：年，低位：月
     */
    private String yearMinimumClineVoltage;

    /**
     * C 线电压极小值发生日时  高位：日，低位：时
     */
    private String dayMinimumClineVoltage;

    /**
     * C 线电压极小值发生分秒   高位：分，低位：秒
     */
    private String minuteMinimumClineVoltage;

    /**
     * A 线电流极小值
     */
    private Integer minimumAphaseCurrent;

    /**
     * A 线电流极小值发生年月 高位：年，低位：月
     */
    private String yearMinimumAphaseCurrent;

    /**
     * A 线电流极小值发生日时  高位：日，低位：时
     */
    private String dayMinimumAphaseCurrent;

    /**
     * A 线电流极小值发生日秒  高位：时，低位：秒
     */
    private String minuteMinimumAphaseCurrent;

    /**
     * B 线电流极小值
     */
    private Integer minimumBphaseCurrent;

    /**
     * B 相电流极小值发生年月 高位：年，低位：月
     */
    private String yearMinimumBphaseCurrent;

    /**
     * B 相电流极小值发生日时  高位：日，低位：时
     */
    private String dayMinimumBphaseCurrent;

    /**
     * B 相电流极小值发生分秒  高位：分，低位：秒
     */
    private String minuteMinimumBphaseCurrent;

    /**
     * C 线电流极小值
     */
    private Integer minimumCphaseCurrent;

    /**
     * C 相电流极小值发生年月 高位：年，低位：月
     */
    private String yearMinimumCphaseCurrent;

    /**
     * C 相电流极小值发生日时  高位：日，低位：时
     */
    private String dayMinimumCphaseCurrent;

    /**
     * C 相电流极小值发生分秒  高位：分，低位：秒
     */
    private String minuteMinimumCphaseCurrent;

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
    public static int RSPDATA_LENGTH = 154 / 2;


    /**
     * 16进制字符串解析成响应对象
     *
     * @param hexStr hexStr
     * @return electricReadAddressRsp electricReadAddressRsp
     */
    public static ElectricReadMinimumRsp hexStrToObj(String hexStr) {
        ElectricReadMinimumRsp miniumVoltageRsp = new ElectricReadMinimumRsp();
        miniumVoltageRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2), 16));
        miniumVoltageRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        miniumVoltageRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16))
                .setMinimumAphaseVoltage(Integer.parseInt(hexStr.substring(6, 10), 16))
                .setYearMinimumAphaseVoltage(hexStr.substring(10, 14))
                .setDayMinimumAphaseVoltage(hexStr.substring(14, 18))
                .setMinuteMinimumAphaseVoltage(hexStr.substring(18, 22))
                .setMinimumBphaseVoltage(Integer.parseInt(hexStr.substring(22, 26), 16))
                .setYearMinimumBphaseVoltage(hexStr.substring(26, 30))
                .setDayMinimumBphaseVoltage(hexStr.substring(30, 34))
                .setMinuteMinimumBphaseVoltage(hexStr.substring(34, 38))
                .setMinimumCphaseVoltage(Integer.parseInt(hexStr.substring(38, 42), 16))
                .setYearMinimumCphaseVoltage(hexStr.substring(42, 46))
                .setDayMinimumCphaseVoltage(hexStr.substring(46, 50))
                .setMinuteMinimumCphaseVoltage(hexStr.substring(50, 54))
                .setMinimumAlineVoltage(Integer.parseInt(hexStr.substring(54, 58), 16))
                .setYearMinimumAlineVoltage(hexStr.substring(58, 62))
                .setDayMinimumAlineVoltage(hexStr.substring(62, 66))
                .setMinuteMinimumAlineVoltage(hexStr.substring(66, 70))
                .setMinimumBlineVoltage(Integer.parseInt(hexStr.substring(70, 74), 16))
                .setYearMinimumBlineVoltage(hexStr.substring(74, 78))
                .setDayMinimumBlineVoltage(hexStr.substring(78, 82))
                .setMinuteMinimumBlineVoltage(hexStr.substring(82, 86))
                .setMinimumClineVoltage(Integer.parseInt(hexStr.substring(86, 90), 16))
                .setYearMinimumClineVoltage(hexStr.substring(90, 94))
                .setDayMinimumClineVoltage(hexStr.substring(94, 98))
                .setMinuteMinimumClineVoltage(hexStr.substring(98, 102))
                .setMinimumAphaseCurrent(Integer.parseInt(hexStr.substring(102, 106), 16))
                .setYearMinimumAphaseCurrent(hexStr.substring(106, 110))
                .setDayMinimumAphaseCurrent(hexStr.substring(110, 114))
                .setMinuteMinimumAphaseCurrent(hexStr.substring(114, 118))
                .setMinimumBphaseCurrent(Integer.parseInt(hexStr.substring(118, 122), 16))
                .setYearMinimumBphaseCurrent(hexStr.substring(122, 126))
                .setDayMinimumBphaseCurrent(hexStr.substring(126, 130))
                .setMinuteMinimumBphaseCurrent(hexStr.substring(130, 134))
                .setMinimumCphaseCurrent(Integer.parseInt(hexStr.substring(134, 138), 16))
                .setYearMinimumCphaseCurrent(hexStr.substring(138, 142))
                .setDayMinimumCphaseCurrent(hexStr.substring(142, 146))
                .setMinuteMinimumCphaseCurrent(hexStr.substring(146, 150));

        miniumVoltageRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(150, 152), 16));
        miniumVoltageRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(152, 154), 16));
        return miniumVoltageRsp;
    }

}
