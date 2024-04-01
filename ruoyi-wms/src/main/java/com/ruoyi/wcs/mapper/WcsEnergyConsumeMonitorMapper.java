package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wcs.domain.WcsElectricalEnergyCollectInfo;
import com.ruoyi.wcs.domain.WcsPowerCollectInfo;
import com.ruoyi.wcs.domain.WcsVoltageCurrentCollectInfo;
import com.ruoyi.wcs.domain.vo.WcsEnergyChartsVo;
import com.ruoyi.wcs.domain.vo.WcsPowerChartsVo;
import com.ruoyi.wcs.domain.vo.WcsVoltageCurrentChartsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * wcs能耗监控信息采集Mapper接口
 *
 * @author hewei
 * @date 2023-04-10
 */
@Mapper
public interface WcsEnergyConsumeMonitorMapper {

    /**
     * 查询能耗统计历史记录
     * @param ew 条件
     * @return List<WcsElectricalEnergyCollectInfo>
     */
    List<WcsElectricalEnergyCollectInfo> selectHistoryRecords(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> ew);

    /**
     * 获取累计用电量（kWh）
     * @param qw 条件
     * @return String
     */
    BigDecimal getTotalEnergy(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);

    /**
     * 获取最新时间
     * @param qw 条件
     * @return String
     */
    String getLeastTime(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);

    /**
     * 获取能耗最大值
     * @param qw 条件
     * @return String
     */
    BigDecimal getMaxEnergy(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);

    /**
     * 获取能耗最小值
     * @param qw 条件
     * @return String
     */
    BigDecimal getMinEnergy(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);

    /**
     * 获取能耗图表
     * @param qw 条件
     * @return List<WcsEnergyChartsVo>
     */
    List<WcsEnergyChartsVo> getEnergyChartData(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw, @Param("startTime")Date startTime,@Param("endTime")Date endTime);

    /**
     * 查询电压电流统计历史记录
     * @param qw 条件
     * @return List<WcsVoltageCurrentCollectInfo>
     */
    List<WcsVoltageCurrentCollectInfo> selectVoltageCurrentHistoryRecords(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);

    /**
     * 查询电压电流统计历史记录
     * @param qw 条件
     * @return List<WcsPowerCollectInfo>
     */
    List<WcsPowerCollectInfo> selectPowerHistoryRecords(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);

    /**
     * 获取电压电流图表
     * @param qw 条件
     * @return List<WcsVoltageCurrentChartsVo>
     */
    List<WcsVoltageCurrentChartsVo> getVoltageCurrentChartData(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw, @Param("startTime")Date startTime,@Param("endTime")Date endTime);

    /**
     * 获取功率图表
     * @param qw 条件
     * @return List<WcsPowerChartsVo>
     */
    List<WcsPowerChartsVo> getPowerChartData(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw, @Param("startTime")Date startTime,@Param("endTime")Date endTime);

    /**
     * 获取指定秒数内的列表 功率
     * @param deviceInfoId
     * @param date
     * @param deviceAddress
     * @return
     */
    List<WcsPowerCollectInfo> selectDiffList(@Param("deviceInfoId") String deviceInfoId, @Param("date") String date, @Param("deviceAddress") String deviceAddress);

    /**
     * 获取指定秒数内的列表 电压电流
     * @param deviceInfoId
     * @param date
     * @param deviceAddress
     * @return
     */
    List<WcsVoltageCurrentCollectInfo> selectDiffVcList(@Param("deviceInfoId") String deviceInfoId, @Param("date") String date, @Param("deviceAddress") String deviceAddress);

    /**
     * 本日用电量
     * @param qw
     * @return
     */
    BigDecimal getTotalEnergyDay(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);

    /**
     * 本周用电量
     * @param qw
     * @return
     */
    BigDecimal getTotalEnergyWeek(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);

    /**
     * 本月用电量
     * @param qw
     * @return
     */
    BigDecimal getTotalEnergyMonth(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);

    /**
     * 获取最近一条数据
     * @return
     */
    WcsElectricalEnergyCollectInfo getYesterdayData(@Param("ew") QueryWrapper<WcsElectricalEnergyCollectInfo> qw);
}
