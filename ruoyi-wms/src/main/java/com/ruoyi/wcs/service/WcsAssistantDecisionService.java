package com.ruoyi.wcs.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsElectricalEnergyCollectInfo;
import com.ruoyi.wcs.domain.WcsTemplatureHumidityCollectInfo;
import com.ruoyi.wcs.domain.dto.WcsParamDto;
import com.ruoyi.wcs.domain.vo.WcsDryOutHourVo;
import com.ruoyi.wcs.domain.vo.WcsStatisticsVo;
import com.ruoyi.wcs.domain.vo.WcsTemplatureHumidityMonitorVo;
import com.ruoyi.wcs.enums.wcs.WcsDateTypeEnum;
import com.ruoyi.wcs.enums.wcs.WcsDeviceAreaEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsEnergyConsumeMonitorMapper;
import com.ruoyi.wcs.util.WcsDateUtil;
import com.ruoyi.wcs.util.WcsPastUtil;
import com.ruoyi.wms.basics.mapper.ViewPageMapper;
import com.ruoyi.wms.enums.AreaTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 辅助决策Service接口
 *
 * @author hewei
 * @date 2023-04-17
 */
@Slf4j
@Service
public class WcsAssistantDecisionService {

    @Autowired
    private ViewPageMapper viewPageMapper;

    @Autowired
    private WcsEnergyConsumeMonitorService wcsEnergyConsumeMonitorService;

    @Autowired
    private WcsEnergyConsumeMonitorMapper wcsEnergyConsumeMonitorMapper;

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    /**
     * 温湿度监测
     *
     * @return
     */
    public WcsTemplatureHumidityMonitorVo temperatureHumidityMonitor(String deviceArea) {
        WcsTemplatureHumidityMonitorVo wcsTemplatureHumidityMonitorVo = new WcsTemplatureHumidityMonitorVo();
        // 分别查询出 今日以及昨日对应区域中的温湿度传感器的列表
        List<WcsTemplatureHumidityCollectInfo> infos = viewPageMapper.getTemplatureHumidyMonitorList(WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode(), deviceArea);
        List<WcsTemplatureHumidityCollectInfo> yesterdayInfos = viewPageMapper.getYesterdayTemplatureHumidyMonitorList(WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode(), deviceArea);

        double averageYesterdayTemplature = 0.00;
        double averageYesterdayHumidity = 0.00;
        // 根据列表求出昨日的温湿度平均值
        if (CollUtil.isNotEmpty(yesterdayInfos)) {
            averageYesterdayTemplature = yesterdayInfos.stream().collect(Collectors.averagingDouble(e -> Double.parseDouble(e.getTemplature())));
            averageYesterdayHumidity = yesterdayInfos.stream().collect(Collectors.averagingDouble(e -> Double.parseDouble(e.getHumidity())));
        }

        double averageTemplature = 0.00;
        double averageHumidity = 0.00;
        // 根据列表求出今日的温湿度平均值
        if (CollUtil.isNotEmpty(infos)) {
            averageTemplature = infos.stream().collect(Collectors.averagingDouble(e -> Double.parseDouble(e.getTemplature())));
            averageHumidity = infos.stream().collect(Collectors.averagingDouble(e -> Double.parseDouble(e.getHumidity())));
        }

        // 温度同占比计算
        if (averageTemplature >= averageYesterdayTemplature) {
            if (averageYesterdayTemplature == 0.00) {
                wcsTemplatureHumidityMonitorVo.setTemperatureRatio("0");
            } else {
                wcsTemplatureHumidityMonitorVo.setTemperatureRatio(String.valueOf((averageTemplature - averageYesterdayTemplature) / averageYesterdayTemplature * 100));
            }
            wcsTemplatureHumidityMonitorVo.setTemplatureFlag("up");
        } else {
            if (averageYesterdayTemplature == 0.00) {
                wcsTemplatureHumidityMonitorVo.setTemperatureRatio("0");
            } else {
                wcsTemplatureHumidityMonitorVo.setTemperatureRatio(String.valueOf((averageYesterdayTemplature - averageTemplature) / averageYesterdayTemplature * 100));
            }
            wcsTemplatureHumidityMonitorVo.setTemplatureFlag("down");
        }

        // 湿度同占比计算
        if (averageHumidity >= averageYesterdayHumidity) {
            if (averageYesterdayHumidity == 0.00) {
                wcsTemplatureHumidityMonitorVo.setHumidityRatio("0");
            } else {
                wcsTemplatureHumidityMonitorVo.setHumidityRatio(String.valueOf((averageHumidity - averageYesterdayHumidity) / averageYesterdayHumidity * 100));
            }
            wcsTemplatureHumidityMonitorVo.setHumidityFlag("up");
        } else {
            if (averageYesterdayHumidity == 0.00) {
                wcsTemplatureHumidityMonitorVo.setHumidityRatio("0");
            } else {
                wcsTemplatureHumidityMonitorVo.setHumidityRatio(String.valueOf((averageYesterdayHumidity - averageHumidity) / averageYesterdayHumidity * 100));
            }
            wcsTemplatureHumidityMonitorVo.setHumidityFlag("down");
        }

        wcsTemplatureHumidityMonitorVo.setAverageTemplature(String.valueOf(averageTemplature));
        wcsTemplatureHumidityMonitorVo.setAverageHumidity(String.valueOf(averageHumidity));
        return wcsTemplatureHumidityMonitorVo;
    }

    /**
     * 填充日期
     *
     * @param type type
     */
    private WcsParamDto fillDate(String type) {
        WcsParamDto wcsParamDto = new WcsParamDto();
        if (WcsDateTypeEnum.DAY.getCode().equals(type)) {
            // 按日
            wcsParamDto.setStartTime(WcsDateUtil.dateToStr(new Date(), Locale.CHINA) + " 00:00:00");
            wcsParamDto.setEndTime(WcsDateUtil.dateToStr(new Date(), Locale.CHINA) + " 23:59:59");
        }
        if (WcsDateTypeEnum.WEEK.getCode().equals(type)) {
            // 按周
            wcsParamDto.setStartTime(DateUtil.format(WcsDateUtil.getThisWeekMonday(), "yyyy-MM-dd") + " 00:00:00");
            wcsParamDto.setEndTime(WcsDateUtil.dateToStr(new Date(), Locale.CHINA) + " 23:59:59");
        }
        if (WcsDateTypeEnum.MONTH.getCode().equals(type)) {
            // 按月
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date time = cal.getTime();
            wcsParamDto.setStartTime(new SimpleDateFormat("yyyy-MM-dd").format(time) + " 00:00:00");
            wcsParamDto.setEndTime(WcsDateUtil.dateToStr(new Date(), Locale.CHINA) + " 23:59:59");
        }
        return wcsParamDto;
    }

    /**
     * 碳排量监测
     */
    public List<WcsStatisticsVo> carbonEmissionMonitor(String type) {
        if (StrUtil.isEmpty(type)) {
            throw new ServiceException("碳排量监测缺失必要业务参数");
        }
        List<WcsStatisticsVo> list = new ArrayList<>();
        // 查出可用电表设备列表
        List<WcsDeviceBaseInfo> deviceBaseInfoList = wcsEnergyConsumeMonitorService.getAmmeterList();
        // 存储区电表设备列表
        List<String> storageList = deviceBaseInfoList.stream()
                .filter(e -> WcsDeviceAreaEnum.STORAGE.getCode().equals(e.getDeviceArea())).map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(storageList)) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            qw.in("t.device_info_id", storageList);
            BigDecimal total = new BigDecimal("0.00");
            // 计算出按天 周 月求出累计用电量
            if (WcsDateTypeEnum.DAY.getCode().equals(type)) {
                total = wcsEnergyConsumeMonitorMapper.getTotalEnergyDay(qw);
            }
            if (WcsDateTypeEnum.WEEK.getCode().equals(type)) {
                total = wcsEnergyConsumeMonitorMapper.getTotalEnergyWeek(qw);
            }
            if (WcsDateTypeEnum.MONTH.getCode().equals(type)) {
                total = wcsEnergyConsumeMonitorMapper.getTotalEnergyMonth(qw);
            }
            WcsStatisticsVo vo = new WcsStatisticsVo();
            vo.setDeviceArea("存储区");
            //根据碳排放量为用电量的0.75倍计算公式求出碳排放量
            vo.setNum(total.multiply(new BigDecimal("0.75")).toString());
            list.add(vo);
        } else {
            WcsStatisticsVo vo = new WcsStatisticsVo();
            vo.setDeviceArea("存储区");
            vo.setNum("0");
            list.add(vo);
        }

        // 晾晒区电表设备列表
        List<String> dryList = deviceBaseInfoList.stream()
                .filter(e -> WcsDeviceAreaEnum.DRY.getCode().equals(e.getDeviceArea())).map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(dryList)) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            qw.in("t.device_info_id", dryList);
            BigDecimal total = new BigDecimal("0.00");
            // 计算出按天 周 月求出累计用电量
            if (WcsDateTypeEnum.DAY.getCode().equals(type)) {
                total = wcsEnergyConsumeMonitorMapper.getTotalEnergy(qw);
            }
            if (WcsDateTypeEnum.WEEK.getCode().equals(type)) {
                total = wcsEnergyConsumeMonitorMapper.getTotalEnergyWeek(qw);
            }
            if (WcsDateTypeEnum.MONTH.getCode().equals(type)) {
                total = wcsEnergyConsumeMonitorMapper.getTotalEnergyMonth(qw);
            }
            WcsStatisticsVo vo = new WcsStatisticsVo();
            vo.setDeviceArea("晾晒区");
            //根据碳排放量为用电量的0.75倍计算公式求出碳排放量
            vo.setNum(total.multiply(new BigDecimal("0.75")).toString());
            list.add(vo);
        } else {
            WcsStatisticsVo vo = new WcsStatisticsVo();
            vo.setDeviceArea("晾晒区");
            vo.setNum("0");
            list.add(vo);
        }

        // 理货区电表设备列表
        List<String> tallyList = deviceBaseInfoList.stream()
                .filter(e -> WcsDeviceAreaEnum.TALLY.getCode().equals(e.getDeviceArea())).map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(tallyList)) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            qw.in("t.device_info_id", tallyList);
            BigDecimal total = new BigDecimal("0.00");
            // 计算出按天 周 月求出累计用电量
            if (WcsDateTypeEnum.DAY.getCode().equals(type)) {
                total = wcsEnergyConsumeMonitorMapper.getTotalEnergy(qw);
            }
            if (WcsDateTypeEnum.WEEK.getCode().equals(type)) {
                total = wcsEnergyConsumeMonitorMapper.getTotalEnergyWeek(qw);
            }
            if (WcsDateTypeEnum.MONTH.getCode().equals(type)) {
                total = wcsEnergyConsumeMonitorMapper.getTotalEnergyMonth(qw);
            }
            WcsStatisticsVo vo = new WcsStatisticsVo();
            vo.setDeviceArea("理货区");
            //根据碳排放量为用电量的0.75倍计算公式求出碳排放量
            vo.setNum(total.multiply(new BigDecimal("0.75")).toString());
            list.add(vo);
        } else {
            WcsStatisticsVo vo = new WcsStatisticsVo();
            vo.setDeviceArea("理货区");
            vo.setNum("0");
            list.add(vo);
        }
        return list;
    }

    /**
     * 温湿度与晾晒时长分析
     */
    public WcsStatisticsVo analysisOfTemperatureHumidityDryingTime() {
        WcsStatisticsVo wcsStatisticsVo = new WcsStatisticsVo();
        List<WcsTemplatureHumidityCollectInfo> templatureHumidityCollectInfos = new ArrayList<>();
        List<WcsDryOutHourVo> dryOutHourVos = new ArrayList<>();
        // 近七天的温湿度监测 每天的温湿度设备列表求出的平均温湿度值
        List<WcsTemplatureHumidityCollectInfo> list = viewPageMapper.selectTemplatureHumidityStatistics(WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode(), AreaTypeEnum.LSQ.getCode());
        // 近七天的晾晒单 根据 每天的晾晒单数目以及每天的晾晒时长求出平均每天的晾晒时长
        List<WcsDryOutHourVo> result = viewPageMapper.selectDryOutHourList();
        // 默认过去一周
        List<String> days = WcsPastUtil.getDaysBetween(7);
        List<String> existDays = list.stream().map(e -> new SimpleDateFormat("yyyy-MM-dd").format(e.getCollectTime())).collect(Collectors.toList());
        List<String> dryOurDays = result.stream().map(WcsDryOutHourVo::getTime).collect(Collectors.toList());
        // 根据统计出的温湿度结果集 对X轴列表日期进行补全
        if (CollUtil.isNotEmpty(days)) {
            if (CollUtil.isNotEmpty(existDays)) {
                days.forEach(e -> {
                    if (!existDays.contains(e)) {
                        templatureHumidityCollectInfos.add(new WcsTemplatureHumidityCollectInfo("0.00", "0.00", DateUtil.parse(e)));
                    } else {
                        List<WcsTemplatureHumidityCollectInfo> arr = list.stream().filter(item -> new SimpleDateFormat("yyyy-MM-dd").format(item.getCollectTime()).equals(e)).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(arr)) {
                            templatureHumidityCollectInfos.add(arr.get(0));
                        } else {
                            templatureHumidityCollectInfos.add(new WcsTemplatureHumidityCollectInfo("0.00", "0.00", DateUtil.parse(e)));
                        }
                    }
                });
            } else {
                days.forEach(e -> {
                    templatureHumidityCollectInfos.add(new WcsTemplatureHumidityCollectInfo("0.00", "0.00", DateUtil.parse(e)));
                });
            }
            // 根据统计出的晾晒分析结果集 对X轴列表日期进行补全
            if (CollUtil.isNotEmpty(dryOurDays)) {
                days.forEach(e -> {
                    if (!dryOurDays.contains(e)) {
                        dryOutHourVos.add(new WcsDryOutHourVo("0.00", e));
                    } else {
                        List<WcsDryOutHourVo> arr = result.stream().filter(item -> item.getTime().equals(e))
                                .collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(arr)) {
                            dryOutHourVos.add(arr.get(0));
                        } else {
                            dryOutHourVos.add(new WcsDryOutHourVo("0.00", e));
                        }
                    }
                });
            } else {
                days.forEach(e -> {
                    dryOutHourVos.add(new WcsDryOutHourVo("0.00", e));
                });
            }
        }
        wcsStatisticsVo.setList(templatureHumidityCollectInfos);
        wcsStatisticsVo.setResult(dryOutHourVos);
        return wcsStatisticsVo;
    }

    /**
     * 能耗与碳排放量分析
     */
    public List<WcsStatisticsVo> analysisOfEnergyConsumptionAndCarbonEmissions(String deviceArea, String dateType) {
        List<WcsStatisticsVo> list = new ArrayList<>();
        if (WcsDateTypeEnum.DAY.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionDayMonitor(deviceArea);
        }
        if (WcsDateTypeEnum.WEEK.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionWeekMonitor(deviceArea);
        }
        //本周
        if (WcsDateTypeEnum.THIS_WEEK.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionThisWeekMonitor(deviceArea);
        }
        if (WcsDateTypeEnum.MONTH.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionMonthMonitor(deviceArea);
        }
        if (WcsDateTypeEnum.HALF_MONTH.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionHalfMonthMonitor(deviceArea);
        }
        if (WcsDateTypeEnum.YEAR.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionYearMonitor(deviceArea);
        }
        return list;
    }

    /**
     * 温湿度监测列表
     */
    public List<WcsDeviceBaseInfo> temperatureAndHumidityMonitorList(String deviceArea) {
        // 对应库区的温湿度列表
        if (StrUtil.isNotEmpty(deviceArea)) {
            return viewPageMapper.temperatureAndHumidityMonitorList(WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode(), deviceArea);
        } else {
            return viewPageMapper.temperatureAndHumidityAllMonitorList(WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode());
        }
    }

    /**
     * 设备监控
     *
     * @return
     */
    public List<WcsDeviceBaseInfo> deviceMonitor() {
        return wcsDeviceBaseInfoService.getBaseMapper().selectList(new QueryWrapper<WcsDeviceBaseInfo>()
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode()).eq("enable_status", EnableStatus.DISABLE.getCode()).select("id", "device_no",
                        "device_name", "enable_status" ));
    }

    //能耗分析 同比
    public List<WcsStatisticsVo> analysisOfEnergyConsumptionAndCarbonEmissionsLastYear(String deviceArea, String dateType) {
        List<WcsStatisticsVo> list = new ArrayList<>();
        if (WcsDateTypeEnum.DAY.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionDayMonitorLast(deviceArea);
        }
        if (WcsDateTypeEnum.WEEK.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionWeekMonitorLast(deviceArea);
        }
        if (WcsDateTypeEnum.MONTH.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionMonthMonitorLast(deviceArea);
        }
        if (WcsDateTypeEnum.HALF_MONTH.getCode().equals(dateType)) {
            list = viewPageMapper.carbonEmissionHalfMonthMonitorLast(deviceArea);
        }
        return list;
    }

    //能耗分析 环比
    public List<WcsStatisticsVo> analysisOfEnergyConsumptionAndCarbonEmissionsLastMonth(String deviceArea, String dateType) {
        List<WcsStatisticsVo> list = new ArrayList<>();
        if (WcsDateTypeEnum.DAY.getCode().equals(dateType)) {
            // 当日能耗分析
            list = viewPageMapper.carbonEmissionDayMonitorLastMonth(deviceArea);
        }
        if (WcsDateTypeEnum.WEEK.getCode().equals(dateType)) {
            // 本周能耗分析
            list = viewPageMapper.carbonEmissionWeekMonitorLastMonth(deviceArea);
        }
        if (WcsDateTypeEnum.MONTH.getCode().equals(dateType)) {
            // 本月能耗分析
            list = viewPageMapper.carbonEmissionMonthMonitorLastMonth(deviceArea);
        }
        if (WcsDateTypeEnum.HALF_MONTH.getCode().equals(dateType)) {
            // 近十五天能耗分析
            list = viewPageMapper.carbonEmissionHalfMonthMonitorLastMonth(deviceArea);
        }
        return list;
    }

    public List<WcsStatisticsVo> analysisOfEnergyConsumptionAndCarbonEmissionsLastTotal(String deviceArea, String dateType) {
        List<WcsStatisticsVo> list = new ArrayList<>();
        if (WcsDateTypeEnum.DAY.getCode().equals(dateType)) {
            // 昨日碳排放量
            list = viewPageMapper.carbonEmissionDayMonitorLastTotal(deviceArea);
        }
        if (WcsDateTypeEnum.THIS_WEEK.getCode().equals(dateType)) {
            // 上周碳排放量
            list = viewPageMapper.carbonEmissionMonitorLastWeek(deviceArea);
        }
        if (WcsDateTypeEnum.MONTH.getCode().equals(dateType)) {
            // 上月碳排放量
            list = viewPageMapper.carbonEmissionMonitorLastMonth(deviceArea);
        }
        if (WcsDateTypeEnum.YEAR.getCode().equals(dateType)) {
            // 去年碳排放量
            list = viewPageMapper.carbonEmissionYearMonitorLastTotal(deviceArea);
        }
        return list;
    }
}
