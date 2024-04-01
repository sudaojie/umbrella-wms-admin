package com.ruoyi.iot.packet.electric.rsp;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 读取极大值 Rsp
 */
@Data
@Accessors(chain = true)
public class ElectricReadMaximumRsp {

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
     * A 相电压极大值
     */
    private Integer maximumAphaseVoltage;

    /**
     * A 相电压极大值发生年月 高位：年，低位：月
     */
    private String yearMaximumAphaseVoltage;

    /**
     * A 相电压极大值发生日时  高位：日，低位：时
     */
    private String dayMaximumAphaseVoltage;

    /**
     * A 相电压极大值发生分秒   高位：分，低位：秒
     */
    private String minuteMaximumAphaseVoltage;

    /**
     * B 相电压极大值
     */
    private Integer maximumBphaseVoltage;

    /**
     * B 相电压极大值发生年月 高位：年，低位：月
     */
    private String yearMaximumBphaseVoltage;

    /**
     * B 相电压极大值发生日时  高位：日，低位：时
     */
    private String dayMaximumBphaseVoltage;

    /**
     * B 相电压极大值发生分秒   高位：分，低位：秒
     */
    private String minuteMaximumBphaseVoltage;

    /**
     * C 相电压极大值
     */
    private Integer maximumCphaseVoltage;

    /**
     * C 相电压极大值发生年月 高位：年，低位：月
     */
    private String yearMaximumCphaseVoltage;

    /**
     * C 相电压极大值发生日时  高位：日，低位：时
     */
    private String dayMaximumCphaseVoltage;

    /**
     * C 相电压极大值发生分秒   高位：分，低位：秒
     */
    private String minuteMaximumCphaseVoltage;

    /**
     * A 线电压极大值
     */
    private Integer maximumAlineVoltage;

    /**
     * A 线电压极大值发生年月 高位：年，低位：月
     */
    private String yearMaximumAlineVoltage;

    /**
     * A 线电压极大值发生日时  高位：日，低位：时
     */
    private String dayMaximumAlineVoltage;

    /**
     * A 线电压极大值发生分秒   高位：分，低位：秒
     */
    private String minuteMaximumAlineVoltage;

    /**
     * B 线电压极大值
     */
    private Integer maximumBlineVoltage;

    /**
     * B 线电压极大值发生年月 高位：年，低位：月
     */
    private String yearMaximumBlineVoltage;

    /**
     * B 线电压极大值发生日时  高位：日，低位：时
     */
    private String dayMaximumBlineVoltage;

    /**
     * B 线电压极大值发生分秒   高位：分，低位：秒
     */
    private String minuteMaximumBlineVoltage;

    /**
     * C 线电压极大值
     */
    private Integer maximumClineVoltage;

    /**
     * C 线电压极大值发生年月 高位：年，低位：月
     */
    private String yearMaximumClineVoltage;

    /**
     * C 线电压极大值发生日时  高位：日，低位：时
     */
    private String dayMaximumClineVoltage;

    /**
     * C 线电压极大值发生分秒   高位：分，低位：秒
     */
    private String minuteMaximumClineVoltage;

    /**
     * A 线电流极大值
     */
    private Integer maximumAphaseCurrent;

    /**
     * A 线电流极大值发生年月 高位：年，低位：月
     */
    private String yearMaximumAphaseCurrent;

    /**
     * A 线电流极大值发生日时  高位：日，低位：时
     */
    private String dayMaximumAphaseCurrent;

    /**
     * A 线电流极大值发生日秒  高位：时，低位：秒
     */
    private String minuteMaximumAphaseCurrent;

    /**
     * B 线电流极大值
     */
    private Integer maximumBphaseCurrent;

    /**
     * B 相电流极大值发生年月 高位：年，低位：月
     */
    private String yearMaximumBphaseCurrent;

    /**
     * B 相电流极大值发生日时  高位：日，低位：时
     */
    private String dayMaximumBphaseCurrent;

    /**
     * B 相电流极大值发生分秒  高位：分，低位：秒
     */
    private String minuteMaximumBphaseCurrent;

    /**
     * C 线电流极大值
     */
    private Integer maximumCphaseCurrent;

    /**
     * C 相电流极大值发生年月 高位：年，低位：月
     */
    private String yearMaximumCphaseCurrent;

    /**
     * C 相电流极大值发生日时  高位：日，低位：时
     */
    private String dayMaximumCphaseCurrent;

    /**
     * C 相电流极大值发生分秒  高位：分，低位：秒
     */
    private String minuteMaximumCphaseCurrent;

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
    public static ElectricReadMaximumRsp hexStrToObj(String hexStr) {
        ElectricReadMaximumRsp maxiumVoltageRsp = new ElectricReadMaximumRsp();
        maxiumVoltageRsp.setAddressCode(Integer.parseInt(hexStr.substring(0, 2), 16));
        maxiumVoltageRsp.setFunctionCode(Integer.parseInt(hexStr.substring(2, 4), 16));
        maxiumVoltageRsp.setReturnEvalByteCount(Integer.parseInt(hexStr.substring(4, 6), 16))
                .setMaximumAphaseVoltage(Integer.parseInt(hexStr.substring(6, 10), 16))
                .setYearMaximumAphaseVoltage(hexStr.substring(10, 14))
                .setDayMaximumAphaseVoltage(hexStr.substring(14, 18))
                .setMinuteMaximumAphaseVoltage(hexStr.substring(18, 22))
                .setMaximumBphaseVoltage(Integer.parseInt(hexStr.substring(22, 26)))
                .setYearMaximumBphaseVoltage(hexStr.substring(26, 30))
                .setDayMaximumBphaseVoltage(hexStr.substring(30, 34))
                .setMinuteMaximumBphaseVoltage(hexStr.substring(34, 38))
                .setMaximumCphaseVoltage(Integer.parseInt(hexStr.substring(38, 42)))
                .setYearMaximumCphaseVoltage(hexStr.substring(42, 46))
                .setDayMaximumCphaseVoltage(hexStr.substring(46, 50))
                .setMinuteMaximumCphaseVoltage(hexStr.substring(50, 54))
                .setMaximumAlineVoltage(Integer.parseInt(hexStr.substring(54, 58)))
                .setYearMaximumAlineVoltage(hexStr.substring(58, 62))
                .setDayMaximumAlineVoltage(hexStr.substring(62, 66))
                .setMinuteMaximumAlineVoltage(hexStr.substring(66, 70))
                .setMaximumBlineVoltage(Integer.parseInt(hexStr.substring(70, 74)))
                .setYearMaximumBlineVoltage(hexStr.substring(74, 78))
                .setDayMaximumBlineVoltage(hexStr.substring(78, 82))
                .setMinuteMaximumBlineVoltage(hexStr.substring(82, 86))
                .setMaximumClineVoltage(Integer.parseInt(hexStr.substring(86, 90)))
                .setYearMaximumClineVoltage(hexStr.substring(90, 94))
                .setDayMaximumClineVoltage(hexStr.substring(94, 98))
                .setMinuteMaximumClineVoltage(hexStr.substring(98, 102))
                .setMaximumAphaseCurrent(Integer.parseInt(hexStr.substring(102, 106)))
                .setYearMaximumAphaseCurrent(hexStr.substring(106, 110))
                .setDayMaximumAphaseCurrent(hexStr.substring(110, 114))
                .setMinuteMaximumAphaseCurrent(hexStr.substring(114, 118))
                .setMaximumBphaseCurrent(Integer.parseInt(hexStr.substring(118, 122)))
                .setYearMaximumBphaseCurrent(hexStr.substring(122, 126))
                .setDayMaximumBphaseCurrent(hexStr.substring(126, 130))
                .setMinuteMaximumBphaseCurrent(hexStr.substring(130, 134))
                .setMaximumCphaseCurrent(Integer.parseInt(hexStr.substring(134, 138)))
                .setYearMaximumCphaseCurrent(hexStr.substring(138, 142))
                .setDayMaximumCphaseCurrent(hexStr.substring(142, 146))
                .setMinuteMaximumCphaseCurrent(hexStr.substring(146, 150));
        maxiumVoltageRsp.setCheckCodeLow(Integer.parseInt(hexStr.substring(150, 152), 16));
        maxiumVoltageRsp.setCheckCodeHigh(Integer.parseInt(hexStr.substring(152, 154), 16));
        return maxiumVoltageRsp;
    }

}
