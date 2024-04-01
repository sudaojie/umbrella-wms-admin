package com.ruoyi.iot.packet.electric.bak;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElectricChannelReadBak {

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        String message = (String) msg;
//        log.info("收到电表设备，服务端的消息内容:" + message);

//        ElectricReadAddressRsp electricReadAddressRsp = ElectricReadAddressRsp.hexStrToObj(message);
//        log.info("通讯地址：{}", electricReadAddressRsp.getAddressCode());

//        ElectricReadApparentPowerRsp electricReadApparentPowerRsp = ElectricReadApparentPowerRsp.hexStrToObj(message);
//        log.info("A 相视在功率：{}", electricReadApparentPowerRsp.getApparentPowerA());
//        log.info("B 相视在功率：{}", electricReadApparentPowerRsp.getApparentPowerB());
//        log.info("C 相视在功率：{}", electricReadApparentPowerRsp.getApparentPowerC());
//        log.info("总相视在功率：{}", electricReadApparentPowerRsp.getTotalApparentPower());
//        log.info("频率F：{}", electricReadApparentPowerRsp.getTotalApparentF());

//        ElectricReadCurrentRsp electricReadCurrentRsp = ElectricReadCurrentRsp.hexStrToObj(message);
//        log.info("电流A：{}", electricReadCurrentRsp.getCurrentA());
//        log.info("电流B：{}", electricReadCurrentRsp.getCurrentB());
//        log.info("电流C：{}", electricReadCurrentRsp.getCurrentC());

//        ElectricReadEnergyRsp electricReadEnergyRsp = ElectricReadEnergyRsp.hexStrToObj(message);
//        log.info("吸收有功电能二次侧：{}", electricReadEnergyRsp.getAbsorbingActiveSecondarySide());
//        log.info("释放有功电能二次侧：{}", electricReadEnergyRsp.getReleaseActiveSecondarySide());
//        log.info("感性无功电能二次侧：{}", electricReadEnergyRsp.getSecondarySideInductiveReactiveEnergy());
//        log.info("容性无功电能二次侧：{}", electricReadEnergyRsp.getSecondarySideCapacitiveReactiveEnergy());
//        log.info("吸收有功电能一次侧：{}", electricReadEnergyRsp.getAbsorbingActiveFirstSide());
//        log.info("释放有功电能一次侧：{}", electricReadEnergyRsp.getReleaseActiveFirstSide());
//        log.info("感性无功电能一次侧：{}", electricReadEnergyRsp.getFirstSideInductiveReactiveEnergy());
//        log.info("容性无功电能一次侧：{}", electricReadEnergyRsp.getFirstSideCapacitiveReactiveEnergy());
//        log.info("最大需量：{}", electricReadEnergyRsp.getMaxDemand());
//        log.info("最大需量发生时间：{}", electricReadEnergyRsp.getMaxDemandTime());

//        ElectricReadLineVoltageRsp electricReadLineVoltageRsp = ElectricReadLineVoltageRsp.hexStrToObj(message);
//        log.info("线电压 UAB：{}", electricReadLineVoltageRsp.getAbLineVoltage());
//        log.info("线电压 UBC：{}", electricReadLineVoltageRsp.getBcLineVoltage());
//        log.info("线电压 UAC：{}", electricReadLineVoltageRsp.getAcLineVoltage());
//
//        ElectricReadMaximumRsp electricReadMaximumRsp = ElectricReadMaximumRsp.hexStrToObj(message);
//        log.info("A 相电压极大值：{}", electricReadMaximumRsp.getMaximumAphaseVoltage());
//        log.info("A 相电压极大值发生年月 高位：年，低位：月：{}", electricReadMaximumRsp.getYearMaximumAphaseVoltage());
//        log.info("A 相电压极大值发生日时  高位：日，低位：时：{}", electricReadMaximumRsp.getDayMaximumAphaseVoltage());
//        log.info("A 相电压极大值发生分秒   高位：分，低位：秒：{}", electricReadMaximumRsp.getMinuteMaximumAphaseVoltage());
//        log.info("B 相电压极大值：{}", electricReadMaximumRsp.getMaximumBphaseVoltage());
//        log.info("B 相电压极大值发生年月 高位：年，低位：月：{}", electricReadMaximumRsp.getYearMaximumBphaseVoltage());
//        log.info("B 相电压极大值发生日时  高位：日，低位：时：{}", electricReadMaximumRsp.getDayMaximumBphaseVoltage());
//        log.info("B 相电压极大值发生分秒   高位：分，低位：秒：{}", electricReadMaximumRsp.getMinuteMaximumBphaseVoltage());
//        log.info("C 相电压极大值：{}", electricReadMaximumRsp.getMaximumCphaseVoltage());
//        log.info("C 相电压极大值发生年月 高位：年，低位：月：{}", electricReadMaximumRsp.getYearMaximumCphaseVoltage());
//        log.info("C 相电压极大值发生日时  高位：日，低位：时：{}", electricReadMaximumRsp.getDayMaximumCphaseVoltage());
//        log.info("C 相电压极大值发生分秒   高位：分，低位：秒：{}", electricReadMaximumRsp.getMinuteMaximumCphaseVoltage());
//        log.info("A 线电压极大值：{}", electricReadMaximumRsp.getMaximumAlineVoltage());
//        log.info("A 线电压极大值发生年月 高位：年，低位：月：{}", electricReadMaximumRsp.getYearMaximumAlineVoltage());
//        log.info("A 线电压极大值发生日时  高位：日，低位：时：{}", electricReadMaximumRsp.getDayMaximumAlineVoltage());
//        log.info("A 线电压极大值发生分秒   高位：分，低位：秒：{}", electricReadMaximumRsp.getMinuteMaximumAlineVoltage());
//        log.info("B 线电压极大值：{}", electricReadMaximumRsp.getMaximumBlineVoltage());
//        log.info("B 线电压极大值发生年月 高位：年，低位：月：{}", electricReadMaximumRsp.getYearMaximumBlineVoltage());
//        log.info("B 线电压极大值发生日时  高位：日，低位：时：{}", electricReadMaximumRsp.getDayMaximumBlineVoltage());
//        log.info("B 线电压极大值发生分秒   高位：分，低位：秒：{}", electricReadMaximumRsp.getMinuteMaximumBlineVoltage());
//        log.info("C 线电压极大值：{}", electricReadMaximumRsp.getMaximumClineVoltage());
//        log.info("C 线电压极大值发生年月 高位：年，低位：月：{}", electricReadMaximumRsp.getYearMaximumClineVoltage());
//        log.info("C 线电压极大值发生日时  高位：日，低位：时：{}", electricReadMaximumRsp.getDayMaximumClineVoltage());
//        log.info("C 线电压极大值发生分秒   高位：分，低位：秒：{}", electricReadMaximumRsp.getMinuteMaximumClineVoltage());
//        log.info("A 线电流极大值：{}", electricReadMaximumRsp.getMaximumAphaseCurrent());
//        log.info("A 线电流极大值发生年月 高位：年，低位：月：{}", electricReadMaximumRsp.getYearMaximumAphaseCurrent());
//        log.info("A 线电流极大值发生日时  高位：日，低位：时：{}", electricReadMaximumRsp.getDayMaximumAphaseCurrent());
//        log.info("A 线电流极大值发生日秒  高位：时，低位：秒：{}", electricReadMaximumRsp.getMinuteMaximumAphaseCurrent());
//        log.info("B 线电流极大值：{}", electricReadMaximumRsp.getMaximumBphaseCurrent());
//        log.info("B 相电流极大值发生年月 高位：年，低位：月：{}", electricReadMaximumRsp.getYearMaximumBphaseCurrent());
//        log.info("B 相电流极大值发生日时  高位：日，低位：时：{}", electricReadMaximumRsp.getDayMaximumBphaseCurrent());
//        log.info("B 相电流极大值发生分秒  高位：分，低位：秒：{}", electricReadMaximumRsp.getMinuteMaximumBphaseCurrent());
//        log.info("C 线电流极大值：{}", electricReadMaximumRsp.getMaximumCphaseCurrent());
//        log.info("C 相电流极大值发生年月 高位：年，低位：月：{}", electricReadMaximumRsp.getYearMaximumCphaseCurrent());
//        log.info("C 相电流极大值发生日时  高位：日，低位：时：{}", electricReadMaximumRsp.getDayMaximumCphaseCurrent());
//        log.info("C 相电流极大值发生分秒  高位：分，低位：秒：{}", electricReadMaximumRsp.getMinuteMaximumCphaseCurrent());

//        ElectricReadMinimumRsp electricReadMinimumRsp = ElectricReadMinimumRsp.hexStrToObj(message);
//        log.info("A 相电压极小值：{}", electricReadMinimumRsp.getMinimumAphaseVoltage());
//        log.info("A 相电压极小值发生年月 高位：年，低位：月：{}", electricReadMinimumRsp.getYearMinimumAphaseVoltage());
//        log.info("A 相电压极小值发生日时  高位：日，低位：时：{}", electricReadMinimumRsp.getDayMinimumAphaseVoltage());
//        log.info("A 相电压极小值发生分秒   高位：分，低位：秒：{}", electricReadMinimumRsp.getMinuteMinimumAphaseVoltage());
//        log.info("B 相电压极小值：{}", electricReadMinimumRsp.getMinimumBphaseVoltage());
//        log.info("B 相电压极小值发生年月 高位：年，低位：月：{}", electricReadMinimumRsp.getYearMinimumBphaseVoltage());
//        log.info("B 相电压极小值发生日时  高位：日，低位：时：{}", electricReadMinimumRsp.getDayMinimumBphaseVoltage());
//        log.info("B 相电压极小值发生分秒   高位：分，低位：秒：{}", electricReadMinimumRsp.getMinuteMinimumBphaseVoltage());
//        log.info("C 相电压极小值：{}", electricReadMinimumRsp.getMinimumCphaseVoltage());
//        log.info("C 相电压极小值发生年月 高位：年，低位：月：{}", electricReadMinimumRsp.getYearMinimumCphaseVoltage());
//        log.info("C 相电压极小值发生日时  高位：日，低位：时：{}", electricReadMinimumRsp.getDayMinimumCphaseVoltage());
//        log.info("C 相电压极小值发生分秒   高位：分，低位：秒：{}", electricReadMinimumRsp.getMinuteMinimumCphaseVoltage());
//        log.info("A 线电压极小值：{}", electricReadMinimumRsp.getMinimumAlineVoltage());
//        log.info("A 线电压极小值发生年月 高位：年，低位：月：{}", electricReadMinimumRsp.getYearMinimumAlineVoltage());
//        log.info("A 线电压极小值发生日时  高位：日，低位：时：{}", electricReadMinimumRsp.getDayMinimumAlineVoltage());
//        log.info("A 线电压极小值发生分秒   高位：分，低位：秒：{}", electricReadMinimumRsp.getMinuteMinimumAlineVoltage());
//        log.info("B 线电压极小值：{}", electricReadMinimumRsp.getMinimumBlineVoltage());
//        log.info("B 线电压极小值发生年月 高位：年，低位：月：{}", electricReadMinimumRsp.getYearMinimumBlineVoltage());
//        log.info("B 线电压极小值发生日时  高位：日，低位：时：{}", electricReadMinimumRsp.getDayMinimumBlineVoltage());
//        log.info("B 线电压极小值发生分秒   高位：分，低位：秒：{}", electricReadMinimumRsp.getMinuteMinimumBlineVoltage());
//        log.info("C 线电压极小值：{}", electricReadMinimumRsp.getMinimumClineVoltage());
//        log.info("C 线电压极小值发生年月 高位：年，低位：月：{}", electricReadMinimumRsp.getYearMinimumClineVoltage());
//        log.info("C 线电压极小值发生日时  高位：日，低位：时：{}", electricReadMinimumRsp.getDayMinimumClineVoltage());
//        log.info("C 线电压极小值发生分秒   高位：分，低位：秒：{}", electricReadMinimumRsp.getMinuteMinimumClineVoltage());
//        log.info("A 线电流极小值：{}", electricReadMinimumRsp.getMinimumAphaseCurrent());
//        log.info("A 线电流极小值发生年月 高位：年，低位：月：{}", electricReadMinimumRsp.getYearMinimumAphaseCurrent());
//        log.info("A 线电流极小值发生日时  高位：日，低位：时：{}", electricReadMinimumRsp.getDayMinimumAphaseCurrent());
//        log.info("A 线电流极小值发生日秒  高位：时，低位：秒：{}", electricReadMinimumRsp.getMinuteMinimumAphaseCurrent());
//        log.info("B 线电流极小值：{}", electricReadMinimumRsp.getMinimumBphaseCurrent());
//        log.info("B 相电流极小值发生年月 高位：年，低位：月：{}", electricReadMinimumRsp.getYearMinimumBphaseCurrent());
//        log.info("B 相电流极小值发生日时  高位：日，低位：时：{}", electricReadMinimumRsp.getDayMinimumBphaseCurrent());
//        log.info("B 相电流极小值发生分秒  高位：分，低位：秒：{}", electricReadMinimumRsp.getMinuteMinimumBphaseCurrent());
//        log.info("C 线电流极小值：{}", electricReadMinimumRsp.getMinimumCphaseCurrent());
//        log.info("C 相电流极小值发生年月 高位：年，低位：月：{}", electricReadMinimumRsp.getYearMinimumCphaseCurrent());
//        log.info("C 相电流极小值发生日时  高位：日，低位：时：{}", electricReadMinimumRsp.getDayMinimumCphaseCurrent());
//        log.info("C 相电流极小值发生分秒  高位：分，低位：秒：{}", electricReadMinimumRsp.getMinuteMinimumCphaseCurrent());

//        ElectricReadPhaseActiveEnergyRsp electricReadPhaseActiveEnergyRsp = ElectricReadPhaseActiveEnergyRsp.hexStrToObj(message);
//        log.info("总有功电能：{}", electricReadPhaseActiveEnergyRsp.getTotalActiveEnergy());

//        ElectricReadPhaseActivePowerRsp electricReadPhaseActivePowerRsp = ElectricReadPhaseActivePowerRsp.hexStrToObj(message);
//        log.info("A相有功功率：{}", electricReadPhaseActivePowerRsp.getPhaseActivePowerA());
//        log.info("B相有功功率：{}", electricReadPhaseActivePowerRsp.getPhaseActivePowerB());
//        log.info("C相有功功率：{}", electricReadPhaseActivePowerRsp.getPhaseActivePowerC());
//        log.info("总相有功功率：{}", electricReadPhaseActivePowerRsp.getTotalPhaseActivePower());

//        ElectricReadPhasePowerFactorRsp electricReadPhasePowerFactorRsp = ElectricReadPhasePowerFactorRsp.hexStrToObj(message);
//        log.info("A相功率因数：{}", electricReadPhasePowerFactorRsp.getPhasePowerFactorA());
//        log.info("B相功率因数：{}", electricReadPhasePowerFactorRsp.getPhasePowerFactorB());
//        log.info("C相功率因数：{}", electricReadPhasePowerFactorRsp.getPhasePowerFactorC());
//        log.info("总功率因数：{}", electricReadPhasePowerFactorRsp.getTotalPhasePowerFactor());

//        ElectricReadReactivePowerRsp electricReadReactivePowerRsp = ElectricReadReactivePowerRsp.hexStrToObj(message);
//        log.info("无功功率A：{}", electricReadReactivePowerRsp.getPhaseReactivePowerA());
//        log.info("无功功率B：{}", electricReadReactivePowerRsp.getPhaseReactivePowerB());
//        log.info("无功功率C：{}", electricReadReactivePowerRsp.getPhaseReactivePowerC());
//        log.info("总无功功率：{}", electricReadReactivePowerRsp.getTotalPhaseReactivePower());

//        ElectricReadSideZeroVoltageCurrentRsp electricReadSideZeroVoltageCurrentRsp = ElectricReadSideZeroVoltageCurrentRsp.hexStrToObj(message);
//        log.info("零序电压：{}", electricReadSideZeroVoltageCurrentRsp.getZeroSequenceVoltage());
//        log.info("零序电流：{}", electricReadSideZeroVoltageCurrentRsp.getZeroSequenceCurrent());
//        log.info("电流百分比：{}", electricReadSideZeroVoltageCurrentRsp.getCurrentPercentage());
//        log.info("电压电流相序状态：{}", electricReadSideZeroVoltageCurrentRsp.getVoltageCurrentPhaseSequenceStatus());
//        log.info("运行时间：{}", electricReadSideZeroVoltageCurrentRsp.getRunTime());
//        log.info("日期时间：{}", electricReadSideZeroVoltageCurrentRsp.getTime());

//        ElectricReadVoltagePhaseAngleRsp electricReadVoltagePhaseAngleRsp = ElectricReadVoltagePhaseAngleRsp.hexStrToObj(message);
//        log.info("电压 UA 相角：{}", electricReadVoltagePhaseAngleRsp.getVoltageUaPhaseAngle());
//        log.info("电压 UB 相角：{}", electricReadVoltagePhaseAngleRsp.getVoltageUbPhaseAngle());
//        log.info("电压 UC 相角：{}", electricReadVoltagePhaseAngleRsp.getVoltageUcPhaseAngle());

//        ElectricReadVoltageRsp electricReadVoltageRsp = ElectricReadVoltageRsp.hexStrToObj(message);
//        log.info("相电压UA：{}", electricReadVoltageRsp.getPhaseVoltageA());
//        log.info("相电压UB：{}", electricReadVoltageRsp.getPhaseVoltageB());
//        log.info("相电压UC：{}", electricReadVoltageRsp.getPhaseVoltageC());

//        super.channelRead(ctx, msg);
    }
}
