package com.ruoyi.wcs.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsElectricalEnergyCollectInfo;
import com.ruoyi.wcs.domain.WcsPowerCollectInfo;
import com.ruoyi.wcs.domain.WcsVoltageCurrentCollectInfo;
import com.ruoyi.wcs.domain.bo.WcsDateBo;
import com.ruoyi.wcs.domain.dto.WcsParamDto;
import com.ruoyi.wcs.domain.vo.*;
import com.ruoyi.wcs.enums.wcs.WcsDeviceAreaEnum;
import com.ruoyi.wcs.enums.wcs.WcsEnergyTreeTypeEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsEnergyConsumeMonitorMapper;
import com.ruoyi.wcs.util.WcsDateUtil;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Warehouse;
import com.ruoyi.wms.basics.service.AreaService;
import com.ruoyi.wms.basics.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.common.utils.PageUtils.startPage;

/**
 * WCS能耗监控统计接口
 *
 * @author hewei
 * @date 2023-04-10
 */
@Slf4j
@Service
public class WcsEnergyConsumeMonitorService {

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private WcsEnergyConsumeMonitorMapper wcsEnergyConsumeMonitorMapper;

    /**
     * 构造左侧设备树
     */
    public WcsTreeVo listSideDeviceInfoTree() {
        String warehouseName = "";
        List<Warehouse> warehouseList = warehouseService.selectWarehouseList(new Warehouse());
        if (CollUtil.isNotEmpty(warehouseList)) {
            warehouseName = warehouseList.get(0).getWarehouseName();
        }
        // 电表设备列表
        List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
        WcsTreeVo wcsTreeVo = new WcsTreeVo().setId("0")
                .setLabel(StrUtil.isNotEmpty(warehouseName) ? warehouseName : "仓库")
                .setType(WcsEnergyTreeTypeEnum.WAREHOUSE.getCode());
        List<WcsTreeVo> childList = new ArrayList<>();

        WcsTreeVo talleyVo = new WcsTreeVo().setId("1").setLabel("理货区").setType(WcsEnergyTreeTypeEnum.AREA.getCode());
        WcsTreeVo dryOutVo = new WcsTreeVo().setId("2").setLabel("晾晒区").setType(WcsEnergyTreeTypeEnum.AREA.getCode());
        WcsTreeVo storeVo = new WcsTreeVo().setId("3").setLabel("存储区").setType(WcsEnergyTreeTypeEnum.AREA.getCode());

        // 依次转为树结构
        List<WcsTreeVo> dryList = this.transferToTreeVo(deviceBaseInfoList, WcsDeviceAreaEnum.DRY.getCode());
        List<WcsTreeVo> storeList = this.transferToTreeVo(deviceBaseInfoList, WcsDeviceAreaEnum.STORAGE.getCode());
        List<WcsTreeVo> tallyList = this.transferToTreeVo(deviceBaseInfoList, WcsDeviceAreaEnum.TALLY.getCode());

        dryOutVo.setChildren(dryList);
        storeVo.setChildren(storeList);
        talleyVo.setChildren(tallyList);

        childList.add(dryOutVo);
        childList.add(storeVo);
        childList.add(talleyVo);

        wcsTreeVo.setChildren(childList);
        return wcsTreeVo;
    }

    /**
     * 构造 List<WcsTreeVo>
     *
     * @param deviceBaseInfoList 设备列表
     * @param code               区域类别编码
     * @return list
     */
    private List<WcsTreeVo> transferToTreeVo(List<WcsDeviceBaseInfo> deviceBaseInfoList, String code) {
        List<WcsTreeVo> list = new ArrayList<>();
        List<WcsDeviceBaseInfo> filterList = deviceBaseInfoList.stream()
                .filter(e -> code.equals(e.getDeviceArea()))
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(filterList)) {
            filterList.forEach(e -> {
                WcsTreeVo wcsTreeVo = new WcsTreeVo();
                wcsTreeVo.setId(e.getId());
                wcsTreeVo.setLabel(e.getDeviceName());
                wcsTreeVo.setType(WcsEnergyTreeTypeEnum.DEVICE.getCode());
                list.add(wcsTreeVo);
            });
        }
        return list;
    }

    /**
     * 加载历史记录
     *
     * @param wcsParamDto wcsParamDto
     * @return List<WcsElectricalEnergyCollectInfo>
     */
    public List<WcsElectricalEnergyCollectInfo> loadHistoryRecords(WcsParamDto wcsParamDto) {
        if (ObjectUtil.isNull(wcsParamDto)) {
            throw new ServiceException("加载历史记录参数缺失");
        }
        List<WcsElectricalEnergyCollectInfo> list = new ArrayList<>();
        WcsDateBo wcsDateBo = fillDate(wcsParamDto);
        if (StrUtil.isNotEmpty(wcsParamDto.getId()) && StrUtil.isNotEmpty(wcsParamDto.getType())) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            // 根据开始和结束时间添加范围
            qw.ge("t.collect_time", wcsDateBo.getStart());
            qw.le("t.collect_time", wcsDateBo.getEnd());
            // 根据类型 仓库
            if (WcsEnergyTreeTypeEnum.WAREHOUSE.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                List<String> ids = deviceBaseInfoList.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(ids)) {
                    qw.in("t.device_info_id", ids);
                } else {
                    return Collections.emptyList();
                }
            }
            // 根据类型 库区
            if (WcsEnergyTreeTypeEnum.AREA.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                Area area = areaService.getById(wcsParamDto.getId());
                String areaType = area.getAreaType();
                if (StrUtil.isNotEmpty(areaType)) {
                    List<String> ids = deviceBaseInfoList.stream().filter(e -> areaType.equals(e.getDeviceArea()))
                            .map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(ids)) {
                        qw.in("t.device_info_id", ids);
                    } else {
                        return Collections.emptyList();
                    }
                } else {
                    throw new ServiceException("库区类型异常");
                }
            }
            // 根据类型 设备单点
            if (WcsEnergyTreeTypeEnum.DEVICE.getCode().equals(wcsParamDto.getType())) {
                qw.eq("t.device_info_id", wcsParamDto.getId());
            }
            qw.orderByDesc("t.collect_time");
            startPage();
            list = wcsEnergyConsumeMonitorMapper.selectHistoryRecords(qw);
        } else {
            throw new ServiceException("业务参数缺失");
        }
        return list;
    }

    /**
     * 获取电表设备列表
     *
     * @return List<WcsDeviceBaseInfo>
     */
    public List<WcsDeviceBaseInfo> getAmmeterList() {
        List<WcsDeviceBaseInfo> deviceBaseInfoList = wcsDeviceBaseInfoService.getBaseMapper()
                .selectList(new QueryWrapper<WcsDeviceBaseInfo>()
                        .select("id", "device_name", "device_area")
                        .eq("device_type", WcsTaskDeviceTypeEnum.AMMETER.getCode())
                        .eq("enable_status", DelFlagEnum.DEL_NO.getCode()));
        return deviceBaseInfoList;
    }

    /**
     * 获取能耗头部数据
     *
     * @return List<WcsEnergyHeaderVo>
     */
    public List<WcsEnergyHeaderVo> loadEnergyHeaderData(WcsParamDto wcsParamDto) {
        if (ObjectUtil.isNull(wcsParamDto)) {
            throw new ServiceException("获取能耗头部数据参数缺失");
        }
        List<WcsEnergyHeaderVo> list = new ArrayList<>();
        WcsDateBo wcsDateBo = fillDate(wcsParamDto);
        if (StrUtil.isNotEmpty(wcsParamDto.getId()) && StrUtil.isNotEmpty(wcsParamDto.getType())) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            // 根据开始和结束时间添加范围
            qw.ge("t.collect_time", wcsDateBo.getStart());
            qw.le("t.collect_time", wcsDateBo.getEnd());
            // 根据类型 仓库
            if (WcsEnergyTreeTypeEnum.WAREHOUSE.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                List<String> ids = deviceBaseInfoList.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(ids)) {
                    qw.in("t.device_info_id", ids);
                } else {
                    //如果该区域下没有设备列表，默认header指标数据为0
                    list.add(new WcsEnergyHeaderVo("00：00-00：00", "累计用电量（kWh）", "0.00", "同比", "0.0%"));
                    list.add(new WcsEnergyHeaderVo("00：00-00：00", "最大值", "0.00", "昨天最大值", "0.00"));
                    list.add(new WcsEnergyHeaderVo("00：00-00：00", "最小值", "0.00", "昨天最小值", "0.00"));
                    list.add(new WcsEnergyHeaderVo("00：00-00：00", "平均用电", "0.00", "同比", "0.00"));
                    return list;
                }
            }
            // 根据类型 库区
            if (WcsEnergyTreeTypeEnum.AREA.getCode().equals(wcsParamDto.getType())) {
                 List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                 Area area = areaService.getById(wcsParamDto.getId());
                String areaType = area.getAreaType();
                if (StrUtil.isNotEmpty(areaType)) {
                    List<String> ids = deviceBaseInfoList.stream().filter(e -> areaType.equals(e.getDeviceArea()))
                            .map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(ids)) {
                        qw.in("t.device_info_id", ids);
                    } else {
                        //如果该区域下没有设备列表，默认header指标数据为0
                        list.add(new WcsEnergyHeaderVo("00：00-00：00", "累计用电量（kWh）", "0.00", "同比", "0.0%"));
                        list.add(new WcsEnergyHeaderVo("00：00-00：00", "最大值", "0.00", "昨天最大值", "0.00"));
                        list.add(new WcsEnergyHeaderVo("00：00-00：00", "最小值", "0.00", "昨天最小值", "0.00"));
                        list.add(new WcsEnergyHeaderVo("00：00-00：00", "平均用电", "0.00", "同比", "0.00"));
                        return list;
                    }
                } else {
                    throw new ServiceException("库区类型异常");
                }
            }
            // 根据类型 设备单点
            if (WcsEnergyTreeTypeEnum.DEVICE.getCode().equals(wcsParamDto.getType())) {
                qw.eq("t.device_info_id", wcsParamDto.getId());
            }
            String time = "";
            long num = 1L;
            //当选择日期范围时表示 默认全天 所以time为24：00 否则表示小时数(当天)
            if (StrUtil.isNotEmpty(wcsParamDto.getStartTime()) && StrUtil.isNotEmpty(wcsParamDto.getEndTime())) {
                time = "24：00";
                num = Math.abs(WcsDateUtil.differentDaysByString(wcsParamDto.getStartTime(), wcsParamDto.getEndTime()));
            } else {
                time = wcsEnergyConsumeMonitorMapper.getLeastTime(qw);
                if (StrUtil.isNotEmpty(time)) {
                    num = WcsDateUtil.dateDiff("00:00", time, "HH:ss");
                } else {
                    num = 0;
                }
            }
            BigDecimal total = wcsEnergyConsumeMonitorMapper.getTotalEnergy(qw);
            WcsEnergyHeaderVo consumeVo = new WcsEnergyHeaderVo();
            consumeVo.setTime(StrUtil.format("00：00-{}", StrUtil.isNotEmpty(time) ? time : "00：00"));
            consumeVo.setNum(total.toString());
            consumeVo.setName("累计用电量（kWh）");
            consumeVo.setCompare("同比");
            consumeVo.setCompareNum("0.0%");

            WcsEnergyHeaderVo maxVo = new WcsEnergyHeaderVo();
            maxVo.setTime(StrUtil.format("00：00-{}", StrUtil.isNotEmpty(time) ? time : "00：00"));
            maxVo.setNum(wcsEnergyConsumeMonitorMapper.getMaxEnergy(qw).toString());
            maxVo.setName("最大值 ");
            maxVo.setCompare("昨天最大值");
            maxVo.setCompareNum("0.0");

            WcsEnergyHeaderVo minVo = new WcsEnergyHeaderVo();
            minVo.setTime(StrUtil.format("00：00-{}", StrUtil.isNotEmpty(time) ? time : "00：00"));
            minVo.setNum(wcsEnergyConsumeMonitorMapper.getMinEnergy(qw).toString());
            minVo.setName("最小值 ");
            minVo.setCompare("昨天最小值");
            minVo.setCompareNum("0.0");

            WcsEnergyHeaderVo averageVo = new WcsEnergyHeaderVo();
            averageVo.setTime(StrUtil.format("00：00-{}", StrUtil.isNotEmpty(time) ? time : "00：00"));
            if (num == 0) {
                num = 1;
            }
            averageVo.setNum(String.valueOf(Double.parseDouble(consumeVo.getNum()) / num));
            averageVo.setName("平均用电 ");
            averageVo.setCompare("同比");
            averageVo.setCompareNum("0.0%");

            list.add(consumeVo);
            list.add(maxVo);
            list.add(minVo);
            list.add(averageVo);
        } else {
            throw new ServiceException("业务参数缺失");
        }

        return list;
    }

    /**
     * 填充日期
     *
     * @param wcsParamDto wcsParamDto
     */
    private WcsDateBo fillDate(WcsParamDto wcsParamDto) {
        WcsDateBo wcsDateBo = new WcsDateBo();
        Date start;
        Date end;
        if (StrUtil.isNotEmpty(wcsParamDto.getStartTime()) && StrUtil.isNotEmpty(wcsParamDto.getEndTime())) {
            start =  DateUtil.parse(wcsParamDto.getStartTime() + " 00:00:00");
            end = DateUtil.parse(wcsParamDto.getEndTime()+ " 23:59:59");
        } else {
            start = WcsDateUtil.strToDateLong(WcsDateUtil.dateToStr(new Date(), Locale.CHINA) + " 00:00:00");
            end = WcsDateUtil.strToDateLong(WcsDateUtil.dateToStr(new Date(), Locale.CHINA) + " 23:59:59");
        }
        wcsDateBo.setStart(start);
        wcsDateBo.setEnd(end);
        return wcsDateBo;
    }


    /**
     * 获取能耗图表
     *
     * @param wcsParamDto wcsParamDto
     * @return WcsChartResultVo wcsChartResultVo
     */
    public WcsChartResultVo loadEnergyChartData(WcsParamDto wcsParamDto) {
        if (ObjectUtil.isNull(wcsParamDto)) {
            throw new ServiceException("参数缺失");
        }
        List<WcsEnergyChartsVo> list = new ArrayList<>();
        WcsChartResultVo wcsChartResultVo = new WcsChartResultVo();
        WcsDateBo wcsDateBo = fillDate(wcsParamDto);
        if (StrUtil.isNotEmpty(wcsParamDto.getId()) && StrUtil.isNotEmpty(wcsParamDto.getType())) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            qw.ge("t.collect_time", wcsDateBo.getStart());
            qw.le("t.collect_time", wcsDateBo.getEnd());
            if (WcsEnergyTreeTypeEnum.WAREHOUSE.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                List<String> ids = deviceBaseInfoList.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(ids)) {
                    qw.in("t.device_info_id", ids);
                } else {
                    return new WcsChartResultVo();
                }
            }
            if (WcsEnergyTreeTypeEnum.AREA.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                Area area = areaService.getById(wcsParamDto.getId());
                String areaType = area.getAreaType();
                if (StrUtil.isNotEmpty(areaType)) {
                    List<String> ids = deviceBaseInfoList.stream().filter(e -> areaType.equals(e.getDeviceArea()))
                            .map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(ids)) {
                        qw.in("t.device_info_id", ids);
                    } else {
                        return new WcsChartResultVo();
                    }
                } else {
                    throw new ServiceException("库区类型异常");
                }
            }
            if (WcsEnergyTreeTypeEnum.DEVICE.getCode().equals(wcsParamDto.getType())) {
                qw.eq("t.device_info_id", wcsParamDto.getId());
            }
            // 能耗监控
            list = wcsEnergyConsumeMonitorMapper.getEnergyChartData(qw,wcsDateBo.getStart(),wcsDateBo.getEnd());
            if (CollUtil.isNotEmpty(list)) {
                List<String> xAxisList = new ArrayList<>();
                List<String> yAxisList = new ArrayList<>();
                list.forEach(e -> {
                    xAxisList.add(e.getName());
                    double value = Double.parseDouble(e.getValue());
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    yAxisList.add(String.valueOf(decimalFormat.format(value)));
                });
                wcsChartResultVo.setXAxisList(xAxisList);
                wcsChartResultVo.setYAxisList(yAxisList);
            }
        } else {
            throw new ServiceException("业务参数缺失");
        }
        return wcsChartResultVo;
    }

    /**
     * 加载电压电流历史记录
     *
     * @param wcsParamDto wcsParamDto
     * @return List<WcsVoltageCurrentCollectInfo>
     */
        public List<WcsVoltageCurrentCollectInfo> loadVoltageCurrentRecords(WcsParamDto wcsParamDto) {
        if (ObjectUtil.isNull(wcsParamDto)) {
            throw new ServiceException("加载电压电流历史记录参数缺失");
        }
        List<WcsVoltageCurrentCollectInfo> list = new ArrayList<>();
        WcsDateBo wcsDateBo = fillDate(wcsParamDto);
        if (StrUtil.isNotEmpty(wcsParamDto.getId()) && StrUtil.isNotEmpty(wcsParamDto.getType())) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            qw.ge("t.collect_time", wcsDateBo.getStart());
            qw.le("t.collect_time", wcsDateBo.getEnd());
            if (WcsEnergyTreeTypeEnum.WAREHOUSE.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                List<String> ids = deviceBaseInfoList.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(ids)) {
                    qw.in("t.device_info_id", ids);
                } else {
                    return Collections.emptyList();
                }
            }
            if (WcsEnergyTreeTypeEnum.AREA.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                Area area = areaService.getById(wcsParamDto.getId());
                String areaType = area.getAreaType();
                if (StrUtil.isNotEmpty(areaType)) {
                    List<String> ids = deviceBaseInfoList.stream().filter(e -> areaType.equals(e.getDeviceArea()))
                            .map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(ids)) {
                        qw.in("t.device_info_id", ids);
                    } else {
                        return Collections.emptyList();
                    }
                } else {
                    throw new ServiceException("库区类型异常");
                }
            }
            if (WcsEnergyTreeTypeEnum.DEVICE.getCode().equals(wcsParamDto.getType())) {
                qw.eq("t.device_info_id", wcsParamDto.getId());
            }
            qw.orderByDesc("t.collect_time");
            startPage();
            list = wcsEnergyConsumeMonitorMapper.selectVoltageCurrentHistoryRecords(qw);
        } else {
            throw new ServiceException("业务参数缺失");
        }
        return list;
    }

    /**
     * 加载功率历史记录
     *
     * @param wcsParamDto wcsParamDto
     * @return List<WcsPowerCollectInfo>
     */
    public List<WcsPowerCollectInfo> loadPowerRecords(WcsParamDto wcsParamDto) {
        if (ObjectUtil.isNull(wcsParamDto)) {
            throw new ServiceException("加载历史记录参数缺失");
        }
        List<WcsPowerCollectInfo> list = new ArrayList<>();
        WcsDateBo wcsDateBo = fillDate(wcsParamDto);
        if (StrUtil.isNotEmpty(wcsParamDto.getId()) && StrUtil.isNotEmpty(wcsParamDto.getType())) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            qw.ge("t.collect_time", wcsDateBo.getStart());
            qw.le("t.collect_time", wcsDateBo.getEnd());
            if (WcsEnergyTreeTypeEnum.WAREHOUSE.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                List<String> ids = deviceBaseInfoList.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(ids)) {
                    qw.in("t.device_info_id", ids);
                } else {
                    return Collections.emptyList();
                }
            }
            if (WcsEnergyTreeTypeEnum.AREA.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                Area area = areaService.getById(wcsParamDto.getId());
                String areaType = area.getAreaType();
                if (StrUtil.isNotEmpty(areaType)) {
                    List<String> ids = deviceBaseInfoList.stream().filter(e -> areaType.equals(e.getDeviceArea()))
                            .map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(ids)) {
                        qw.in("t.device_info_id", ids);
                    } else {
                        return Collections.emptyList();
                    }
                } else {
                    throw new ServiceException("库区类型异常");
                }
            }
            if (WcsEnergyTreeTypeEnum.DEVICE.getCode().equals(wcsParamDto.getType())) {
                qw.eq("t.device_info_id", wcsParamDto.getId());
            }
            qw.orderByDesc("t.collect_time");
            startPage();
            list = wcsEnergyConsumeMonitorMapper.selectPowerHistoryRecords(qw);
        } else {
            throw new ServiceException("业务参数缺失");
        }
        return list;
    }

    /**
     * 加载电压电流图表数据
     *
     * @param wcsParamDto wcsParamDto
     * @return WcsChartResultVo
     */
    public WcsChartResultVo loadVoltageCurrentChartData(WcsParamDto wcsParamDto) {
        if (ObjectUtil.isNull(wcsParamDto)) {
            throw new ServiceException("参数缺失");
        }
        List<WcsVoltageCurrentChartsVo> list = new ArrayList<>();
        WcsChartResultVo wcsChartResultVo = new WcsChartResultVo();
        WcsDateBo wcsDateBo = fillDate(wcsParamDto);
        if (StrUtil.isNotEmpty(wcsParamDto.getId()) && StrUtil.isNotEmpty(wcsParamDto.getType())) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            qw.ge("t.collect_time", wcsDateBo.getStart());
            qw.le("t.collect_time", wcsDateBo.getEnd());
            //根据仓库
            if (WcsEnergyTreeTypeEnum.WAREHOUSE.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                List<String> ids = deviceBaseInfoList.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(ids)) {
                    qw.in("t.device_info_id", ids);
                } else {
                    return new WcsChartResultVo();
                }
            }
            //根据库区
            if (WcsEnergyTreeTypeEnum.AREA.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                Area area = areaService.getById(wcsParamDto.getId());
                String areaType = area.getAreaType();
                if (StrUtil.isNotEmpty(areaType)) {
                    List<String> ids = deviceBaseInfoList.stream().filter(e -> areaType.equals(e.getDeviceArea()))
                            .map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(ids)) {
                        qw.in("t.device_info_id", ids);
                    } else {
                        return new WcsChartResultVo();
                    }
                } else {
                    throw new ServiceException("库区类型异常");
                }
            }
            //根据设备单点
            if (WcsEnergyTreeTypeEnum.DEVICE.getCode().equals(wcsParamDto.getType())) {
                qw.eq("t.device_info_id", wcsParamDto.getId());
            }
            list = wcsEnergyConsumeMonitorMapper.getVoltageCurrentChartData(qw,wcsDateBo.getStart(),wcsDateBo.getEnd());
            if (CollUtil.isNotEmpty(list)) {
                List<String> xAxisList = new ArrayList<>();
                List<String> yAxisList = new ArrayList<>();
                List<String> secondaryAxisList = new ArrayList<>();
                List<String> thirdAxisList = new ArrayList<>();
                List<String> fourthAxisList = new ArrayList<>();
                List<String> fifthAxisList = new ArrayList<>();
                List<String> sixthAxisList = new ArrayList<>();
                list.forEach(e -> {
                    xAxisList.add(e.getName());
                    yAxisList.add(e.getPhaseVoltageA());
                    secondaryAxisList.add(e.getPhaseVoltageB());
                    thirdAxisList.add(e.getPhaseVoltageC());
                    fourthAxisList.add(e.getPhaseCurrentA());
                    fifthAxisList.add(e.getPhaseCurrentB());
                    sixthAxisList.add(e.getPhaseCurrentC());
                });
                wcsChartResultVo.setXAxisList(xAxisList);
                wcsChartResultVo.setYAxisList(yAxisList);
                wcsChartResultVo.setSecondaryAxisList(secondaryAxisList);
                wcsChartResultVo.setThirdAxisList(thirdAxisList);
                wcsChartResultVo.setFourthAxisList(fourthAxisList);
                wcsChartResultVo.setFifthAxisList(fifthAxisList);
                wcsChartResultVo.setSixthAxisList(sixthAxisList);
            }
        } else {
            throw new ServiceException("业务参数缺失");
        }
        return wcsChartResultVo;
    }

    /**
     * 加载功率图表数据
     *
     * @param wcsParamDto wcsParamDto
     * @return WcsChartResultVo
     */
    public WcsChartResultVo loadPowerChartData(WcsParamDto wcsParamDto) {
        if (ObjectUtil.isNull(wcsParamDto)) {
            throw new ServiceException("参数缺失");
        }
        List<WcsPowerChartsVo> list = new ArrayList<>();
        WcsChartResultVo wcsChartResultVo = new WcsChartResultVo();
        WcsDateBo wcsDateBo = fillDate(wcsParamDto);
        if (StrUtil.isNotEmpty(wcsParamDto.getId()) && StrUtil.isNotEmpty(wcsParamDto.getType())) {
            QueryWrapper<WcsElectricalEnergyCollectInfo> qw = new QueryWrapper<>();
            qw.ge("t.collect_time", wcsDateBo.getStart());
            qw.le("t.collect_time", wcsDateBo.getEnd());
            if (WcsEnergyTreeTypeEnum.WAREHOUSE.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                List<String> ids = deviceBaseInfoList.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(ids)) {
                    qw.in("t.device_info_id", ids);
                } else {
                    return new WcsChartResultVo();
                }
            }
            if (WcsEnergyTreeTypeEnum.AREA.getCode().equals(wcsParamDto.getType())) {
                List<WcsDeviceBaseInfo> deviceBaseInfoList = getAmmeterList();
                Area area = areaService.getById(wcsParamDto.getId());
                String areaType = area.getAreaType();
                if (StrUtil.isNotEmpty(areaType)) {
                    List<String> ids = deviceBaseInfoList.stream().filter(e -> areaType.equals(e.getDeviceArea()))
                            .map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(ids)) {
                        qw.in("t.device_info_id", ids);
                    } else {
                        return new WcsChartResultVo();
                    }
                } else {
                    throw new ServiceException("库区类型异常");
                }
            }
            if (WcsEnergyTreeTypeEnum.DEVICE.getCode().equals(wcsParamDto.getType())) {
                qw.eq("t.device_info_id", wcsParamDto.getId());
            }
            list = wcsEnergyConsumeMonitorMapper.getPowerChartData(qw,wcsDateBo.getStart(),wcsDateBo.getEnd());
            if (CollUtil.isNotEmpty(list)) {
                List<String> xAxisList = new ArrayList<>();
                List<String> yAxisList = new ArrayList<>();
                List<String> secondaryAxisList = new ArrayList<>();
                List<String> thirdAxisList = new ArrayList<>();
                List<String> fourthAxisList = new ArrayList<>();
                List<String> fifthAxisList = new ArrayList<>();
                List<String> sixthAxisList = new ArrayList<>();
                List<String> seventhAxisList = new ArrayList<>();
                List<String> eighthAxisList = new ArrayList<>();
                list.forEach(e -> {
                    xAxisList.add(e.getName());
                    yAxisList.add(e.getTotalActivePower());
                    secondaryAxisList.add(e.getPhaseActivePowerA());
                    thirdAxisList.add(e.getPhaseActivePowerB());
                    fourthAxisList.add(e.getPhaseActivePowerC());
                    fifthAxisList.add(e.getTotalReactivePower());
                    sixthAxisList.add(e.getPhaseReactivePowerA());
                    seventhAxisList.add(e.getPhaseReactivePowerB());
                    eighthAxisList.add(e.getPhaseReactivePowerC());
                });
                wcsChartResultVo.setXAxisList(xAxisList);
                wcsChartResultVo.setYAxisList(yAxisList);
                wcsChartResultVo.setSecondaryAxisList(secondaryAxisList);
                wcsChartResultVo.setThirdAxisList(thirdAxisList);
                wcsChartResultVo.setFourthAxisList(fourthAxisList);
                wcsChartResultVo.setFifthAxisList(fifthAxisList);
                wcsChartResultVo.setSixthAxisList(sixthAxisList);
                wcsChartResultVo.setSeventhAxisList(seventhAxisList);
                wcsChartResultVo.setEighthAxisList(eighthAxisList);
            }
        } else {
            throw new ServiceException("业务参数缺失");
        }
        return wcsChartResultVo;
    }
}
