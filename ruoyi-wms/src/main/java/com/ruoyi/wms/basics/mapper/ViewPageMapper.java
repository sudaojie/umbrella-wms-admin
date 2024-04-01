package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsDeviceEarlyWarningInfo;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.domain.WcsTemplatureHumidityCollectInfo;
import com.ruoyi.wcs.domain.vo.WcsDryOutHourVo;
import com.ruoyi.wcs.domain.vo.WcsEnergyChartsVo;
import com.ruoyi.wcs.domain.vo.WcsStatisticsVo;
import com.ruoyi.wms.basics.vo.GoodsDetailVo;
import com.ruoyi.wms.basics.vo.ViewPageVo;
import com.ruoyi.wms.basics.vo.ViewPageGoodsVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 地址基本信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
public interface ViewPageMapper extends BaseMapper<ViewPageVo> {



    /**
     * 根据货位号获取货物信息
     * @param queryGoodsInfoWrapper
     * @return
     */
    ViewPageGoodsVo selectGoodsByLocCodeList(@Param("ew") QueryWrapper<ViewPageGoodsVo> queryGoodsInfoWrapper);

    /**
     * 根据货位号获取货物详细信息
     * @param queryGoodsInfoWrapper
     * @return
     */
    List<GoodsDetailVo> selectGoodsByLocCodeDetailList(@Param("ew") QueryWrapper<ViewPageGoodsVo> queryGoodsInfoWrapper);


    /**
     *
     * @param queryNoTrayWrapper
     * @return
     */
    List<ViewPageVo> selectLocationBylayer(@Param("ew") QueryWrapper<ViewPageVo> queryNoTrayWrapper);

    /**
     * 获取排序后的库区编码
     * @param areaType 库区类型
     * @return
     */
    List<String> selectAreaCodeByType(@Param("areaType") String areaType);

    /**
     * 查询库区类型下的总库位
     * @param areaType
     * @return
     */
    @Select("select count(1) from wms_warehouse_location where del_flag = '0'" +
            " and area_id in (select area_code from wms_warehouse_area where del_flag = '0' and area_type = #{areaType})")
    Long selectLocationCount(@Param("areaType") String areaType);

    /**
     * 查询库区类型下的有货库位数量
     * @param areaType
     * @return
     */
    @Select("select count(1)" +
            " from wms_warehouse_location a" +
            " left join wms_warehouse_tray b on a.tray_code = b.tray_code" +
            " left join wms_warehouse_area c on a.area_id = c.area_code" +
            " where b.enable_status = '0'" +
            " and b.empty_status = '1'" +
            " and a.enable_status = '0'" +
            " and a.del_flag = '0'" +
            " and b.del_flag = '0'" +
            " and c.area_type = #{areaType}")
    Long selectLocationGoodsCount(@Param("areaType") String areaType);

    /**
     * 查询库区类型下的移库库位数量
     * @param areaType
     * @return
     */
    @Select("select count(1)" +
            " from wms_warehouse_location a" +
            " left join wms_warehouse_area c on a.area_id = c.area_code" +
            " where a.location_name like '%移库%'" +
            " and a.del_flag = '0'" +
            " and c.area_type = #{areaType}")
    Long selectLocationMoveCount(@Param("areaType") String areaType);

    /**
     * 查询库区类型下的禁用库位数量
     * @param areaType
     * @return
     */
    @Select("select count(1)" +
            " from wms_warehouse_location a" +
            " left join wms_warehouse_area c on a.area_id = c.area_code" +
            " where a.enable_status = '1'" +
            " and a.del_flag = '0'" +
            " and c.area_type = #{areaType}")
    Long selectLocationEnableCount(@Param("areaType") String areaType);

    /**
     * 查询今日入库数量
     * @return
     */
    @Select("select ifnull(sum(a.in_bill_num),0.000)" +
            " from wms_inbill_detail a" +
            " left join wms_in_bill b on a.in_bill_code = b.in_bill_code" +
            " where a.del_flag = '0'" +
            " and b.del_flag = '0'" +
            " and b.in_bill_status = '4'" +
            " and to_days(b.update_time)=to_days(now())")
    BigDecimal selectToDayInBillCount();

    /**
     * 查询今日出库数量
     * @return
     */
    @Select("select ifnull(sum(a.out_bill_num),0.000)" +
            " from wms_outbill_goods a" +
            " left join wms_out_bill b on a.out_bill_code = b.out_bill_code" +
            " where a.del_flag = '0'" +
            " and b.del_flag = '0'" +
            " and to_days(b.out_bill_time)=to_days(now())")
    BigDecimal selectToDayOutBillCount();

    /**
     * 设备总数
     * @return
     */
    @Select("select ifnull(count(d.id), 0) as num from wcs_device_base_info d where d.del_flag = 0")
    int selectDeviceNum();

    /**
     * 正常设备数
     * @return
     */
    @Select("select ifnull(count(d.id), 0) as num from wcs_device_base_info d where d.del_flag = 0 and d.enable_status = 0")
    int selectNormalDeviceNum();

    /**
     * 预警信息列表
     * @return
     */
    @Select("select d.* from wcs_device_early_warning_info d where TO_DAYS(d.warning_time) = TO_DAYS(now()) order by d.warning_time desc")
    List<WcsDeviceEarlyWarningInfo> selectWarningList();

    /**
     * 任务信息列表
     * @return
     */
    @Select("select d.in_bill_no, d.task_type, d.task_status from wcs_operate_task d where TO_DAYS(d.create_time) = TO_DAYS(now()) and d.del_flag = 0 order by d.operate_begin_time desc limit 5")
    List<WcsOperateTask> selectTaskList();

    /**
     * 温湿度监测列表
     */
    @Select("select d.device_name, d.enable_status, d.device_type, ifnull(d.templature, 0.0) as templature, ifnull(d.humidity, 0.0) as humidity " +
            "from wcs_device_base_info d where d.del_flag = 0 and d.device_type = #{type} and d.device_area = #{deviceArea}")
    List<WcsDeviceBaseInfo> temperatureAndHumidityMonitorList(@Param("type") String type, @Param("deviceArea") String deviceArea);

    @Select("select d.device_name, d.enable_status, d.device_type, ifnull(d.templature, 0.0) as templature, ifnull(d.humidity, 0.0) as humidity " +
            "from wcs_device_base_info d where d.del_flag = 0 and d.device_type = #{type}")
    List<WcsDeviceBaseInfo> temperatureAndHumidityAllMonitorList(@Param("type") String type);

    /**
     * 获取今日温湿度监控
     * @param deviceType
     * @param deviceArea
     * @return
     */
    List<WcsTemplatureHumidityCollectInfo> getTemplatureHumidyMonitorList(@Param("deviceType") String deviceType, @Param("deviceArea") String deviceArea);

    /**
     * 获取昨日温湿度监控
     * @param deviceType
     * @param deviceArea
     * @return
     */
    List<WcsTemplatureHumidityCollectInfo> getYesterdayTemplatureHumidyMonitorList(@Param("deviceType") String deviceType, @Param("deviceArea") String deviceArea);

    /**
     * 温湿度分析
     */
    List<WcsTemplatureHumidityCollectInfo> selectTemplatureHumidityStatistics(@Param("deviceType") String deviceType, @Param("deviceArea") String deviceArea);

    /**
     * 晾晒时长分析
     */
    List<WcsDryOutHourVo> selectDryOutHourList();

    /**
     * 能耗与碳排放量分析 日
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionDayMonitor(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 月
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionMonthMonitor(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 周
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionWeekMonitor(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 本周
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionThisWeekMonitor(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 半月
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionHalfMonthMonitor(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 上一年
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionDayMonitorLast(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 周 上一年
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionWeekMonitorLast(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 月 上一年
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionMonthMonitorLast(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 半个月 上一年
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionHalfMonthMonitorLast(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 上一月的今天
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionDayMonitorLastMonth(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 上一月的近七天
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionWeekMonitorLastMonth(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 上一月的近一个月
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionMonthMonitorLastMonth(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 上一个月的近半个月
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionHalfMonthMonitorLastMonth(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 昨日
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionDayMonitorLastTotal(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 上周
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionMonitorLastWeek(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 上一个月
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionMonitorLastMonth(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 本年
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionYearMonitor(@Param("deviceArea") String deviceArea);

    /**
     * 能耗与碳排放量分析 日 上一年
     * @param deviceArea
     * @return
     */
    List<WcsStatisticsVo> carbonEmissionYearMonitorLastTotal(@Param("deviceArea") String deviceArea);

    /**
     * 查询温湿度烟感列表
     * @return
     */
    List<WcsDeviceBaseInfo> getInstrumentInformation(@Param("list") List<String> list);

    /**
     * 入库统计近七天
     * @return
     */
    List<WcsStatisticsVo> getInbillStatisticsWeek();

    /**
     * 入库统计近五天
     * @return
     */
    List<WcsStatisticsVo> getInbillStatisticsFiveDays();

    /**
     * 入库统计近三天
     * @return
     */
    List<WcsStatisticsVo> getInbillStatisticsThreeDays();

    /**
     * 入库统计近一天
     * @return
     */
    List<WcsStatisticsVo> getInbillStatisticsOneDay();

    /**
     * 出库统计近七天
     * @return
     */
    List<WcsStatisticsVo> getOutBillStatisticsWeek();

    /**
     * 出库统计近五天
     * @return
     */
    List<WcsStatisticsVo> getOutBillStatisticsFiveDays();

    /**
     * 出库统计近三天
     * @return
     */
    List<WcsStatisticsVo> getOutBillStatisticsThreeDays();

    /**
     * 出库统计近一天
     * @return
     */
    List<WcsStatisticsVo> getOutBillStatisticsOneDay();

    /**
     * 能耗排名近七天
     * @return
     */
    List<WcsEnergyChartsVo> getEnergyConsumptionRankingDay();

    /**
     * 能耗排名近15天
     * @return
     */
    List<WcsEnergyChartsVo> getEnergyConsumptionRankingFifteenDay();

    /**
     * 能耗排名近1月
     * @return
     */
    List<WcsEnergyChartsVo> getEnergyConsumptionRankingMonth();

}
