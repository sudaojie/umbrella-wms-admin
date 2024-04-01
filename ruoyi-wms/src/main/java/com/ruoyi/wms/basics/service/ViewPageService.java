package com.ruoyi.wms.basics.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wcs.api.domain.WmsToWcsTaskReq;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.*;
import com.ruoyi.wcs.domain.vo.WcsEnergyChartsVo;
import com.ruoyi.wcs.domain.vo.WcsStatisticsVo;
import com.ruoyi.wcs.enums.wcs.WcsDateTypeEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wcs.mapper.WcsOperateTaskMapper;
import com.ruoyi.wcs.service.WcsAssistantDecisionService;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wcs.service.WcsFreshAirDetailInfoService;
import com.ruoyi.wcs.service.WcsSmartLightingDetailInfoService;
import com.ruoyi.wms.basics.bo.EmptyLocationBo;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.Tray;
import com.ruoyi.wms.basics.dto.SunCureAreaDto;
import com.ruoyi.wms.basics.dto.TallyAreaDto;
import com.ruoyi.wms.basics.dto.UpdateTrayInfoDto;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.basics.mapper.ViewPageMapper;
import com.ruoyi.wms.basics.vo.*;
import com.ruoyi.wms.check.service.WmsWarehouseCheckDetailService;
import com.ruoyi.wms.enums.*;
import com.ruoyi.wms.global.WmsTaskConstant;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.utils.HumiditySmokeUtils;
import com.ruoyi.wms.utils.constant.DryLocationConstants;
import com.ruoyi.wms.utils.constant.LhqLocationConstants;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.mapper.InbillGoodsMapper;
import com.ruoyi.wms.wcstask.service.TasklogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * 库位基本信息Service接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Slf4j
@Service
public class ViewPageService extends ServiceImpl<LocationMapper, Location> {

    @Autowired(required = false)
    private ViewPageMapper viewPageMapper;

    @Autowired(required = false)
    private AreaMapper areaMapper;

    @Autowired(required = false)
    private TblstockMapper tblstockMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private InbillGoodsMapper inbillGoodsMapper;

    @Autowired
    private WcsAssistantDecisionService wcsAssistantDecisionService;

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    private WcsFreshAirDetailInfoService wcsFreshAirDetailInfoService;

    @Autowired
    private WcsSmartLightingDetailInfoService wcsSmartLightingDetailInfoService;


    @Autowired
    private TrayService trayService;

    @Autowired
    private WcsTaskApiService wcsTaskApiService;

    @Autowired(required = false)
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    @Autowired
    private WmsWarehouseCheckDetailService checkDetailService;

    @Autowired
    private LocationService locationService;

    @Autowired(required = false)
    private TrayMapper trayMapper;

    @Autowired
    private TasklogService tasklogService;

    @Autowired
    private WcsOperateTaskMapper wcsOperateTaskMapper;


    /**
     * 根据层号获取存储区货物
     *
     * @param location
     * @return
     */
    public LocationRespVo selectBylayerList(Location location) {
        location.setAreaType(AreaTypeEnum.CCQ.getCode());
        LocationRespVo locationRespVo = new LocationRespVo();
        locationRespVo.setAreaType(AreaTypeEnum.CCQ.getCode());
        locationRespVo.setLocationData(this.getViewPageByAreaType(location));
        return locationRespVo;
    }

    /**
     * 获取存储区所有库位信息
     *
     * @param location
     * @return
     */
    public LocationAllRespVo selectAllLocationList(Location location) {
        location.setAreaType(AreaTypeEnum.CCQ.getCode());
        LocationAllRespVo locationAllRespVo = new LocationAllRespVo();
        List<LocationAllVo> locationVos = new ArrayList<>();

        //获取所有库位信息
        QueryWrapper<ViewPageVo> queryLocationWrapper = new QueryWrapper<>();
        queryLocationWrapper.eq("wl.del_flag", DelFlagEnum.DEL_NO.getCode());
        queryLocationWrapper.groupBy("wl.location_code");
        queryLocationWrapper.orderByAsc("wl.location_code");
        List<ViewPageVo> locationList = viewPageMapper.selectLocationBylayer(queryLocationWrapper);

        //封装库位返回实体
        for (ViewPageVo viewPageVo : locationList) {
            LocationAllVo locationVo = new LocationAllVo();
            locationVo.setLocationCode(viewPageVo.getLocationCode());
            if (viewPageVo.getEnableStatus().equals(EnableStatus.ENABLE.getCode())) {
                //判断库位类型
                if (StringUtils.isEmpty(viewPageVo.getTrayCode())) {
                    locationVo.setLocationStatus(LocationStatusEnum.NO_TRAY.getCode());
                } else if (StringUtils.isNotEmpty(viewPageVo.getTrayCode()) && StringUtils.isEmpty(viewPageVo.getGoodsCode())) {
                    locationVo.setLocationStatus(LocationStatusEnum.HAVE_TRAY.getCode());
                } else {
                    locationVo.setLocationStatus(LocationStatusEnum.HAVE_GOODS.getCode());
                }
                if (StringUtils.isNotEmpty(viewPageVo.getTrayCode())) {
                    locationVo.setTrayCode(viewPageVo.getTrayCode());
                } else {
                    locationVo.setTrayCode("");
                }
            } else {
                locationVo.setLocationStatus(LocationStatusEnum.DISABLE.getCode());
            }

            locationVos.add(locationVo);
        }

        locationAllRespVo.setLocationList(locationVos);
        return locationAllRespVo;
    }

    /**
     * 根据货位号获取货物信息
     *
     * @param location
     * @return
     */
    public ViewPageGoodsVo selectGoodsByLocCodeList(Location location) {
        if (StrUtil.isEmpty(location.getLocationCode())) {
            throw new ServiceException("库位编号不能为空");
        }
        QueryWrapper<ViewPageGoodsVo> queryGoodsInfoWrapper = getQueryGoodsInfoWrapper(location);
        ViewPageGoodsVo viewPageGoodsVo = viewPageMapper.selectGoodsByLocCodeList(queryGoodsInfoWrapper);
        if (ObjectUtil.isEmpty(viewPageGoodsVo)) {
            throw new ServiceException("输入库位不存在，请重新输入！");
        }
        if (ObjectUtil.isNotEmpty(viewPageGoodsVo)) {
            //判断库位状态
            if (viewPageGoodsVo.getEnableStatus().equals(EnableStatus.ENABLE.getCode())) {
                if (StringUtils.isNotEmpty(viewPageGoodsVo.getGoodsCode())) {
                    viewPageGoodsVo.setLocationStatus(LocationStatusEnum.HAVE_GOODS.getCode());
                } else {
                    if (StringUtils.isNotEmpty(viewPageGoodsVo.getTrayCode())) {
                        viewPageGoodsVo.setLocationStatus(LocationStatusEnum.HAVE_TRAY.getCode());
                    } else {
                        viewPageGoodsVo.setLocationStatus(LocationStatusEnum.NO_TRAY.getCode());
                    }
                }
            } else {
                viewPageGoodsVo.setLocationStatus(LocationStatusEnum.DISABLE.getCode());
            }
            List<GoodsDetailVo> goodsDetailVos = viewPageMapper.selectGoodsByLocCodeDetailList(queryGoodsInfoWrapper);
            if(ObjectUtil.isNotEmpty(goodsDetailVos)){
                viewPageGoodsVo.setGoodsDetailVoList(goodsDetailVos);
            }
        }
        return viewPageGoodsVo;
    }

    /**
     * 根据层号获取理货区货物
     *
     * @return
     */
    public LocalVo selectLhList() {
        LocalVo localVo = new LocalVo();
        TallyAreaDto tallyArea = new TallyAreaDto();
        Location location = new Location();
        //获取理货区数据
        location.setAreaType(AreaTypeEnum.LHQ.getCode());
        tallyArea.setLocationList(this.getLHViewPage(location));
        localVo.setTallyArea(tallyArea);
        //获取晾晒区数据
        location.setAreaType(AreaTypeEnum.LSQ.getCode());
        localVo.setSunCureArea(this.getLocationByAreaType(location));
        return localVo;
    }

    /**
     * 晾晒区库位列表数据，公共方法
     *
     * @param location
     * @return
     */
    public SunCureAreaDto getLocationByAreaType(Location location) {
        List<SunCureAreaDto> listSunCureDto = new ArrayList<>();
        List<ViewPageVo> locationList = new ArrayList<>();
        SunCureAreaDto sunCureArea = new SunCureAreaDto();
        QueryWrapper<ViewPageVo> queryLocationWrapper = getQueryLocationWrapper(location);
        locationList = viewPageMapper.selectLocationBylayer(queryLocationWrapper);

        SunCureAreaDto sunCureAreaDto = this.getCureArea(locationList);

        return sunCureAreaDto;
    }

    public SunCureAreaDto getCureArea(List<ViewPageVo> locationList) {
        SunCureAreaDto sunCureArea = new SunCureAreaDto();
        String[] strOnes = DryLocationConstants.tunnelOnes;
        String[] strTwos = DryLocationConstants.tunnelTwos;
        List<List<LocationVo>> upFine = new ArrayList<>();
        List<List<LocationVo>> downFine = new ArrayList<>();
        List<List<LocationVo>> formal = new ArrayList<>();
        LocationListVo locationListVo = new LocationListVo();

        //晾晒去排数
        List<String> platoonList = locationList.stream().map(obj -> obj.getPlatoon()).distinct().collect(Collectors.toList());

        for (String platoon : platoonList) {
            List<LocationVo> upFilelist = new ArrayList<>();
            List<LocationVo> downFileList = new ArrayList<>();
            List<LocationVo> formalList = new ArrayList<>();
            for (ViewPageVo locations : locationList) {
                LocationVo locationVo = new LocationVo();
                if (locations.getEnableStatus().equals(EnableStatus.ENABLE.getCode())) {
                    //判断库位类型
                    if (StringUtils.isEmpty(locations.getTrayCode())) {
                        locationVo.setLocationStatus(LocationStatusEnum.NO_TRAY.getCode());
                    } else if (StringUtils.isNotEmpty(locations.getTrayCode()) && StringUtils.isEmpty(locations.getGoodsCode())) {
                        locationVo.setLocationStatus(LocationStatusEnum.HAVE_TRAY.getCode());
                    } else {
                        locationVo.setLocationStatus(LocationStatusEnum.HAVE_GOODS.getCode());
                    }
                } else {
                    locationVo.setLocationStatus(LocationStatusEnum.DISABLE.getCode());
                }
                //根据每列获取库位信息
                if (Arrays.asList(strOnes).contains(locations.getLocationCode())
                        && platoon.equals(locations.getPlatoon())) {
                    locationVo.setLocationCode(locations.getLocationCode());
                    upFilelist.add(locationVo);
                } else if (Arrays.asList(strTwos).contains(locations.getLocationCode())
                        && platoon.equals(locations.getPlatoon())) {
                    locationVo.setLocationCode(locations.getLocationCode());
                    downFileList.add(locationVo);
                } else {
                    if (platoon.equals(locations.getPlatoon())) {
                        locationVo.setLocationCode(locations.getLocationCode());
                        formalList.add(locationVo);
                    }
                }
            }
            Collections.reverse(upFilelist);
            upFine.add(upFilelist);
            downFine.add(downFileList);
            formal.add(formalList);
        }
        locationListVo.setUpFine(upFine);
        locationListVo.setDownFine(downFine);
        locationListVo.setFormal(formal);
        sunCureArea.setLocationList(locationListVo);
        return sunCureArea;
    }

    /**
     * 存储区货物列表数据，封装公共方法
     *
     * @param location
     * @return
     */
    public List<LocationAreaVo> getViewPageByAreaType(Location location) {
        List<ViewPageVo> locationList = new ArrayList<>();
        List<String> strPlatoon = new ArrayList<>();
        List<LocationAreaVo> areaLocationList = new ArrayList<>();

        QueryWrapper<ViewPageVo> queryLocationWrapper = getQueryLocationWrapper(location);
        locationList = viewPageMapper.selectLocationBylayer(queryLocationWrapper);
        for (ViewPageVo obj : locationList) {
            if (!strPlatoon.contains(obj.getPlatoon())) {
                strPlatoon.add(obj.getPlatoon());
            }
        }
        //获取巷道
        List<String> areaCodeList = viewPageMapper.selectAreaCodeByType(location.getAreaType());
        for (String areaCode : areaCodeList) {
            LocationAreaVo locationAreaVo = new LocationAreaVo();
            locationAreaVo.setAreaCode(areaCode);
            List<List<LocationVo>> locationVoList = new ArrayList<>();
            for (String str : strPlatoon) {
                List<LocationVo> platoonList = new ArrayList<>();
                List<String> arr = new ArrayList<>();
                for (ViewPageVo locations : locationList) {
                    LocationVo locationVo = new LocationVo();
                    if (locations.getEnableStatus().equals(EnableStatus.ENABLE.getCode())) {
                        //判断库位类型
                        if (StringUtils.isEmpty(locations.getTrayCode())) {
                            locationVo.setLocationStatus(LocationStatusEnum.NO_TRAY.getCode());
                        } else if (StringUtils.isNotEmpty(locations.getTrayCode()) && StringUtils.isEmpty(locations.getGoodsCode())) {
                            locationVo.setLocationStatus(LocationStatusEnum.HAVE_TRAY.getCode());
                        } else {
                            locationVo.setLocationStatus(LocationStatusEnum.HAVE_GOODS.getCode());
                        }
                    } else {
                        locationVo.setLocationStatus(LocationStatusEnum.DISABLE.getCode());
                    }

                    if (arr.size() > 0 && arr.contains(locations.getColumnNum())) {
                        continue;
                    }
                    //根据每列获取库位信息
                    if (locations.getPlatoon().equals(str) && locations.getAreaId().equals(areaCode)) {
                        locationVo.setLocationCode(locations.getLocationCode());
                        arr.add(locations.getColumnNum());
                        platoonList.add(locationVo);
                    }
                }
                if (CollUtil.isNotEmpty(platoonList)) {
                    locationVoList.add(platoonList);
                }
            }
            locationAreaVo.setAreaCode(areaCode);
            locationAreaVo.setLocationList(locationVoList);
            areaLocationList.add(locationAreaVo);
        }

        return areaLocationList;

    }

    /**
     * 理货区首页展示货架信息
     *
     * @param location
     * @return
     */
    public List<List<LocationVo>> getLHViewPage(Location location) {
        List<List<LocationVo>> locationVoList = new ArrayList<>();
        List<ViewPageVo> locationList = new ArrayList<>();
        List<String> strPlatoon = new ArrayList<>();
        QueryWrapper<ViewPageVo> queryLocationWrapper = getQueryLocationWrapper(location);
        locationList = viewPageMapper.selectLocationBylayer(queryLocationWrapper);
        strPlatoon = locationList.stream().map(obj -> obj.getPlatoon()).distinct().collect(Collectors.toList());
        for (String str : strPlatoon) {
            List<LocationVo> platoonList = new ArrayList<>();
            List<String> arr = new ArrayList<>();
            for (ViewPageVo locations : locationList) {
                LocationVo locationVo = new LocationVo();
                if (locations.getEnableStatus().equals(EnableStatus.ENABLE.getCode())) {
                    //判断库位类型
                    if (StringUtils.isEmpty(locations.getTrayCode())) {
                        locationVo.setLocationStatus(LocationStatusEnum.NO_TRAY.getCode());
                    } else if (StringUtils.isNotEmpty(locations.getTrayCode()) && StringUtils.isEmpty(locations.getGoodsCode())) {
                        locationVo.setLocationStatus(LocationStatusEnum.HAVE_TRAY.getCode());
                    } else {
                        locationVo.setLocationStatus(LocationStatusEnum.HAVE_GOODS.getCode());
                    }
                } else {
                    locationVo.setLocationStatus(LocationStatusEnum.DISABLE.getCode());
                }

                //根据每列获取库位信息
                if (locations.getPlatoon().equals(str)) {
                    locationVo.setLocationCode(locations.getLocationCode());
                    arr.add(locations.getColumnNum());
                    platoonList.add(locationVo);
                }
            }
            if (CollUtil.isNotEmpty(platoonList)) {
                locationVoList.add(platoonList);
            }
        }
        return locationVoList;
    }


    public QueryWrapper<ViewPageVo> getQueryLocationWrapper(Location location) {
        QueryWrapper<ViewPageVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wl.del_flag", DelFlagEnum.DEL_NO.getCode());
        if (location != null) {
            if (StrUtil.isNotEmpty(location.getAreaType())) {
                queryWrapper.eq("wa.area_type", location.getAreaType());
            }
            if (location.getLayer() != null) {
                queryWrapper.eq("wl.layer", location.getLayer());
            }
        }
        queryWrapper.orderByDesc("wl.column_num desc,wl.platoon");
        return queryWrapper;
    }


    public QueryWrapper<ViewPageGoodsVo> getQueryGoodsInfoWrapper(Location location) {
        QueryWrapper<ViewPageGoodsVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wl.del_flag", DelFlagEnum.DEL_NO.getCode());
        if (location != null && StringUtils.isNotEmpty(location.getLocationCode())) {
            queryWrapper.eq("wl.location_code", location.getLocationCode());
        }
        return queryWrapper;
    }


    /**
     * 查看库位情况
     *
     * @return
     */
    public LocationInfoVo selectLocationInfo() {
        LocationInfoVo vo = new LocationInfoVo();
        //获取所有库位
        Long allCount = viewPageMapper.selectLocationCount(AreaTypeEnum.CCQ.getCode());
        vo.setTotalCount(allCount);
        //有货的库位
        Long goodsCount = viewPageMapper.selectLocationGoodsCount(AreaTypeEnum.CCQ.getCode());
        //查询移库库位
        Long aLong = viewPageMapper.selectLocationMoveCount(AreaTypeEnum.CCQ.getCode());
        //查询被禁用库位
        Long aLong1 = viewPageMapper.selectLocationEnableCount(AreaTypeEnum.CCQ.getCode());
        vo.setInUseCount(goodsCount + aLong1);
        vo.setSpareCount(allCount - vo.getInUseCount());
        return vo;
    }

    /**
     * 查看今日库存数量信息
     *
     * @return
     */
    public TblstockCountVo selectTblstockCount() {
        TblstockCountVo vo = new TblstockCountVo();
        vo.setTotdayInCount(viewPageMapper.selectToDayInBillCount());
        vo.setTotdayOutCount(viewPageMapper.selectToDayOutBillCount());
        LambdaQueryWrapper<Tblstock> tblstockQueryWrapper = Wrappers.lambdaQuery();
        tblstockQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        //库存数据数量
        Long aLong = tblstockMapper.selectCount(tblstockQueryWrapper);
        vo.setInTotalCount(new BigDecimal(aLong.toString()));
        return vo;
    }

    /**
     * 预警信息列表
     *
     * @return
     */
    public List<WcsDeviceEarlyWarningInfo> getWarningList() {
        return viewPageMapper.selectWarningList();
    }

    /**
     * 任务列表
     *
     * @return
     */
    public List<WcsOperateTask> getTaskList() {
        return viewPageMapper.selectTaskList();
    }

    /**
     * 设备情况
     *
     * @return
     */
    public DeviceStatusVo getDeviceStatus() {
        DeviceStatusVo deviceStatusVo = new DeviceStatusVo();
        int deviceNum = viewPageMapper.selectDeviceNum();
        int normalDeviceNum = viewPageMapper.selectNormalDeviceNum();
        deviceStatusVo.setDeviceNum(deviceNum);
        deviceStatusVo.setNormalDeviceNum(normalDeviceNum);
        deviceStatusVo.setAbNormalDeviceNum(deviceNum - normalDeviceNum);
        return deviceStatusVo;
    }

    /**
     * 用电量统计
     *
     * @param dateType
     * @return
     */
    @SuppressWarnings("all")
    public ViewPageVo electricityConsumptionStatistics(String dateType) {
        ViewPageVo viewPageVo = new ViewPageVo();
        // 能耗与碳排放量分析列表
        List<WcsStatisticsVo> list = wcsAssistantDecisionService.analysisOfEnergyConsumptionAndCarbonEmissions("", dateType);
        double powerConsumption = 0.00;
        if (CollUtil.isNotEmpty(list)) {
            // 总和
            powerConsumption = list.stream().mapToDouble(e -> Double.parseDouble(e.getNum())).sum();
        }
        viewPageVo.setPowerConsumption(String.valueOf(powerConsumption));
        List<WcsStatisticsVo> result = new ArrayList<>();
        if (StrUtil.isNotEmpty(dateType)) {
            if (WcsDateTypeEnum.THIS_WEEK.getCode().equals(dateType)) {
                result = wcsAssistantDecisionService.analysisOfEnergyConsumptionAndCarbonEmissions("", WcsDateTypeEnum.THIS_WEEK.getCode());
            }
            if (WcsDateTypeEnum.MONTH.getCode().equals(dateType)) {
                result = wcsAssistantDecisionService.analysisOfEnergyConsumptionAndCarbonEmissions("", WcsDateTypeEnum.MONTH.getCode());
            }
            if (WcsDateTypeEnum.YEAR.getCode().equals(dateType)) {
                result = wcsAssistantDecisionService.analysisOfEnergyConsumptionAndCarbonEmissions("", WcsDateTypeEnum.YEAR.getCode());
            }
            viewPageVo.setList(result);
        } else {
            viewPageVo.setList(Collections.emptyList());
        }
        double powerConsumptionLast = 0.00;
        // 去年 上一个月 上周的碳排放量监测
        List<WcsStatisticsVo> arr = wcsAssistantDecisionService.analysisOfEnergyConsumptionAndCarbonEmissionsLastTotal("", dateType);
        if (CollUtil.isNotEmpty(arr)) {
            powerConsumptionLast = arr.stream().mapToDouble(e -> Double.parseDouble(e.getNum())).sum();
        }
        viewPageVo.setLastPowerConsumption(String.valueOf(powerConsumptionLast));
        if (powerConsumption >= powerConsumptionLast) {
            if (powerConsumptionLast == 0.00) {
                powerConsumptionLast = 1.00;
            }
            viewPageVo.setRatio(String.valueOf((powerConsumption - powerConsumptionLast) / powerConsumptionLast * 100));
            viewPageVo.setFlag("up");
        } else {
            if (powerConsumptionLast == 0.00) {
                powerConsumptionLast = 1.00;
            }
            viewPageVo.setRatio(String.valueOf((powerConsumptionLast - powerConsumption) / powerConsumptionLast * 100));
            viewPageVo.setFlag("down");
        }
        return viewPageVo;
    }

    /**
     * 能耗分析
     *
     * @return
     */
    public ViewPageVo energyConsumptionAnalysis(String dateType) {
        ViewPageVo viewPageVo = new ViewPageVo();
        //当前
        List<WcsStatisticsVo> list = wcsAssistantDecisionService.analysisOfEnergyConsumptionAndCarbonEmissions("", dateType);
        double currentYearNum = 0.00;
        if (CollUtil.isNotEmpty(list)) {
            currentYearNum = list.stream().mapToDouble(e -> Double.parseDouble(e.getNum())).sum();
        }

        //同比：去年
        List<WcsStatisticsVo> lastYearList = wcsAssistantDecisionService.analysisOfEnergyConsumptionAndCarbonEmissionsLastYear("", dateType);
        double historicalYearNum = 0.00;
        if (CollUtil.isNotEmpty(lastYearList)) {
            historicalYearNum = lastYearList.stream().mapToDouble(e -> Double.parseDouble(e.getNum())).sum();
        }

        //环比 上月
        List<WcsStatisticsVo> lastMonthList = wcsAssistantDecisionService.analysisOfEnergyConsumptionAndCarbonEmissionsLastMonth("", dateType);
        double historicalMonthNum = 0.00;
        if (CollUtil.isNotEmpty(lastMonthList)) {
            historicalMonthNum = lastMonthList.stream().mapToDouble(e -> Double.parseDouble(e.getNum())).sum();
        }

        viewPageVo.setCurrentYearNum(String.valueOf(currentYearNum));
        viewPageVo.setHistoricalYearNum(String.valueOf(historicalYearNum));
        viewPageVo.setCurrentMonthNum(String.valueOf(currentYearNum));
        viewPageVo.setHistoricalMonthNum(String.valueOf(historicalMonthNum));
        return viewPageVo;
    }

    /**
     * 查询温湿度烟感列表
     *
     * @return
     */
    public Map<String, List<WcsDeviceBaseInfo>> getInstrumentInformation() {
        Map<String, List<WcsDeviceBaseInfo>> result = new HashMap<>();
        List<WcsDeviceBaseInfo> list = viewPageMapper.getInstrumentInformation(Arrays.asList(WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode(), WcsTaskDeviceTypeEnum.SMOKE.getCode()));
        result.put("templature", list.stream().filter(e -> WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode().equals(e.getDeviceType())).collect(Collectors.toList()));
        result.put("humidity", list.stream().filter(e -> WcsTaskDeviceTypeEnum.SMOKE.getCode().equals(e.getDeviceType())).collect(Collectors.toList()));
        return result;
    }

    /**
     * 能耗排名
     *
     * @return
     */
    public List<WcsEnergyChartsVo> energyConsumptionRanking(String dateType) {
        List<WcsEnergyChartsVo> list = new ArrayList<>();
        //近七天
        if (WcsDateTypeEnum.WEEK.getCode().equals(dateType)) {
            list = viewPageMapper.getEnergyConsumptionRankingDay();
        }
        //近15天
        if (WcsDateTypeEnum.HALF_MONTH.getCode().equals(dateType)) {
            list = viewPageMapper.getEnergyConsumptionRankingFifteenDay();
        }
        //近一月
        if (WcsDateTypeEnum.MONTH.getCode().equals(dateType)) {
            list = viewPageMapper.getEnergyConsumptionRankingMonth();
        }
        return list;
    }

    /**
     * 生成温湿度、烟感信息
     *
     * @return
     */
    public void generateSmokeAndHumidity() {
        List<WcsDeviceBaseInfo> list = HumiditySmokeUtils.generate();
        wcsDeviceBaseInfoService.saveOrUpdateBatch(list);
    }

    /**
     * 查看月度库存周转率
     *
     * @return
     */
    public TblstockRatioVo getInventoryTurnover() {

        TblstockRatioVo vo = new TblstockRatioVo();
        //本月期初库存
        String openingInventoryOfThisMonth = tblstockMapper.getOpeningInventoryOfThisMonth();
        //本月期末库存
        String endingInventoryOfThisMonth = tblstockMapper.getEndingInventoryOfThisMonth();
        //本月出库数量
        String outGoodsNumOfThisMonth = tblstockMapper.getOutGoodsNumOfThisMonth();

        //上月期初库存
        String openingInventoryOfLastMonth = tblstockMapper.getOpeningInventoryOfLastMonth();
        //上月期末库存
        String endingInventoryOfLastMonth = tblstockMapper.getEndingInventoryOfLastMonth();
        //上月出库数量
        String outGoodsNumOfLastMonth = tblstockMapper.getOutGoodsNumOfLastMonth();

        vo.setOpeningInventoryOfThisMonth(openingInventoryOfThisMonth)
                .setEndingInventoryOfThisMonth(endingInventoryOfThisMonth)
                .setOutGoodsNumOfThisMonth(outGoodsNumOfThisMonth)
                .setOpeningInventoryOfLastMonth(openingInventoryOfLastMonth)
                .setEndingInventoryOfLastMonth(endingInventoryOfLastMonth)
                .setOutGoodsNumOfLastMonth(outGoodsNumOfLastMonth);

        return vo;
    }

    /**
     * 出入库统计
     *
     * @return
     */
    public Map<String, List<WcsStatisticsVo>> getInboundAndOutboundStatistics(String dateType) {
        Map<String, List<WcsStatisticsVo>> result = new HashMap<>();
        if (WcsDateTypeEnum.WEEK.getCode().equals(dateType)) {
            result.put("inbill", viewPageMapper.getInbillStatisticsWeek());
            result.put("outbill", viewPageMapper.getOutBillStatisticsWeek());
        }
        if (WcsDateTypeEnum.FIVE_DAYS.getCode().equals(dateType)) {
            result.put("inbill", viewPageMapper.getInbillStatisticsFiveDays());
            result.put("outbill", viewPageMapper.getOutBillStatisticsFiveDays());
        }
        if (WcsDateTypeEnum.THREE_DAYS.getCode().equals(dateType)) {
            result.put("inbill", viewPageMapper.getInbillStatisticsThreeDays());
            result.put("outbill", viewPageMapper.getOutBillStatisticsThreeDays());
        }
        if (WcsDateTypeEnum.ONE_DAY.getCode().equals(dateType)) {
            result.put("inbill", viewPageMapper.getInbillStatisticsOneDay());
            result.put("outbill", viewPageMapper.getOutBillStatisticsOneDay());
        }
        return result;
    }

    /**
     * 根据设备编号获取相关信息
     *
     * @param deviceNo
     * @return
     */
    public WcsDeviceBaseInfo getInstrumentInformationByDeviceNo(String deviceNo) {
        if (StrUtil.isNotEmpty(deviceNo)) {
            QueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQueryWrapper = new QueryWrapper<>();
            wcsDeviceBaseInfoQueryWrapper.eq("device_no", deviceNo);
            WcsDeviceBaseInfo deviceBaseInfo = wcsDeviceBaseInfoService.getBaseMapper().selectOne(wcsDeviceBaseInfoQueryWrapper);
            if (ObjectUtil.isNotNull(deviceBaseInfo)) {
                // 新风 照明运行状态处理
                if (WcsTaskDeviceTypeEnum.FRESHAIR.getCode().equals(deviceBaseInfo.getDeviceType())) {
                    WcsFreshAirDetailInfo wcsFreshAirDetailInfo = wcsFreshAirDetailInfoService.getBaseMapper()
                            .selectOne(new QueryWrapper<WcsFreshAirDetailInfo>().eq("device_info_id", deviceBaseInfo.getId()));
                    if (ObjectUtil.isNotNull(wcsFreshAirDetailInfo)) {
                        deviceBaseInfo.setFreshAirStatus(wcsFreshAirDetailInfo.getSwitchStatus());
                    }
                }
                if (WcsTaskDeviceTypeEnum.LIGHT.getCode().equals(deviceBaseInfo.getDeviceType())) {
                    WcsSmartLightingDetailInfo wcsSmartLightingDetailInfo = wcsSmartLightingDetailInfoService.getBaseMapper()
                            .selectOne(new QueryWrapper<WcsSmartLightingDetailInfo>().eq("device_info_id", deviceBaseInfo.getId()));
                    if (ObjectUtil.isNotNull(wcsSmartLightingDetailInfo)) {
                        deviceBaseInfo.setLightStatus(wcsSmartLightingDetailInfo.getSwitchStatus());
                    }
                }
                return deviceBaseInfo;
            } else {
                throw new ServiceException("系统暂未录入当前设备信息");
            }
        } else {
            throw new ServiceException("设备编号缺失");
        }
    }

    /**
     * 根据类型和内容进行筛选定位库位信息
     *
     * @param searchInput 搜索内容
     * @param searchType  搜索类型(1.库位号  2.托盘号  3.机件号)
     * @return
     */
    public ViewPageGoodsVo searchLocationByParams(String searchInput, String searchType) {

        Location location = new Location();

        if (StrUtil.isEmpty(searchType)) {
            throw new ServiceException("请选择查询类型");
        }

        if (searchType.equals(MainCtrlSearchTypeEnum.LOCATION_CODE_TYPE.getCode())) {
            if (StrUtil.isEmpty(searchInput)) {
                throw new ServiceException("库位号不能为空");
            }
            location = new Location() {{
                setLocationCode(searchInput);
            }};
        } else if (searchType.equals(MainCtrlSearchTypeEnum.TRAY_CODE_TYPE.getCode())) {
            if (StrUtil.isEmpty(searchInput)) {
                throw new ServiceException("托盘号不能为空");
            }
            location = locationMapper.selectOne(
                    new QueryWrapper<Location>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("tray_code", searchInput)
            );
            if (ObjectUtil.isEmpty(location)) {
                throw new ServiceException("输入托盘号不存在，请重新输入！");
            }
        } else if (searchType.equals(MainCtrlSearchTypeEnum.PART_CODE_TYPE.getCode())) {
            if (StrUtil.isEmpty(searchInput)) {
                throw new ServiceException("机件号不能为空");
            }

            InbillGoods inbillGoods = inbillGoodsMapper.selectOne(
                    new QueryWrapper<InbillGoods>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("out_status", OutStatusEnum.NOT_OUT.getCode())
                            .eq("parts_code", searchInput)
            );
            if (ObjectUtil.isEmpty(inbillGoods)) {
                throw new ServiceException("输入机件号不存在，请重新输入！");
            }
            location = locationMapper.selectOne(
                    new QueryWrapper<Location>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("tray_code", inbillGoods.getTrayCode())
            );
        }


        QueryWrapper<ViewPageGoodsVo> queryGoodsInfoWrapper = getQueryGoodsInfoWrapper(location);
        ViewPageGoodsVo viewPageGoodsVo = viewPageMapper.selectGoodsByLocCodeList(queryGoodsInfoWrapper);
        if (ObjectUtil.isEmpty(viewPageGoodsVo)) {
            throw new ServiceException("输入库位不存在，请重新输入！");
        }
        if (ObjectUtil.isNotEmpty(viewPageGoodsVo)) {
            //判断库位状态
            if (viewPageGoodsVo.getEnableStatus().equals(EnableStatus.ENABLE.getCode())) {
                if (StringUtils.isNotEmpty(viewPageGoodsVo.getGoodsCode())) {
                    viewPageGoodsVo.setLocationStatus(LocationStatusEnum.HAVE_GOODS.getCode());
                } else {
                    if (StringUtils.isNotEmpty(viewPageGoodsVo.getTrayCode())) {
                        viewPageGoodsVo.setLocationStatus(LocationStatusEnum.HAVE_TRAY.getCode());
                    } else {
                        viewPageGoodsVo.setLocationStatus(LocationStatusEnum.NO_TRAY.getCode());
                    }
                }
            } else {
                viewPageGoodsVo.setLocationStatus(LocationStatusEnum.DISABLE.getCode());
            }
            List<GoodsDetailVo> goodsDetailVos = viewPageMapper.selectGoodsByLocCodeDetailList(queryGoodsInfoWrapper);
            if (goodsDetailVos.stream().allMatch(e -> e == null)) {
                viewPageGoodsVo.setGoodsDetailVoList(null);
            } else {
                viewPageGoodsVo.setGoodsDetailVoList(goodsDetailVos);
            }
        }
        return viewPageGoodsVo;
    }


    /**
     * 修改库位托盘信息
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTrayInfo(UpdateTrayInfoDto updateTrayInfoDto) {
        Location location = locationMapper.selectOne(new QueryWrapper<Location>().eq("del_flag", DelFlagEnum.DEL_NO.getCode()).eq("location_code", updateTrayInfoDto.getLocationCode()));
        if (location == null) {
            throw new ServiceException("托盘号不存在，请检查托盘号");
        }

        if(location.getEnableStatus().equals(EnableStatus.DISABLE.getCode())){
            throw new ServiceException("库位已禁用，无法修改托盘信息");
        }

        if (StrUtil.isEmpty(updateTrayInfoDto.getTrayCode())) {
            location.setTrayCode(null);
        } else {
            Tray tray = trayService.getBaseMapper().selectOne(new QueryWrapper<Tray>()
                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                    .eq("tray_code", updateTrayInfoDto.getTrayCode())
            );
            if (tray == null) {
                throw new ServiceException("托盘号在托盘信息中未查询到，请重新填写");
            }

            Location findLocation = locationMapper.selectOne(new QueryWrapper<Location>().eq("del_flag", DelFlagEnum.DEL_NO.getCode()).eq("tray_code", updateTrayInfoDto.getTrayCode()));
            if(findLocation != null && !findLocation.getLocationCode().equals(updateTrayInfoDto.getLocationCode())){
                throw new ServiceException("托盘号:"+updateTrayInfoDto.getTrayCode()+"，已存在于库位:"+findLocation.getLocationCode());
            }
            location.setTrayCode(updateTrayInfoDto.getTrayCode());
        }
        location.setLockStatus(LockEnum.NOTLOCK.getCode());
        return locationMapper.updateById(location) > 0;
    }

    /**
     * 选中有托盘无货库位取盘
     * @param Location 库位信息
     * @return 取盘成功
     */
    public String locationRetrieval(Location Location) {
        //库位
        String locationCode = Location.getLocationCode();
        String startArea = "";
        String areaId =locationCode.substring(3, 5);
        if(areaId.equals("01")||areaId.equals("02")||areaId.equals("03")||areaId.equals("04")){
            startArea="CCQ01";
        }else if(areaId.equals("05")||areaId.equals("06")||areaId.equals("07")||areaId.equals("08")){
            startArea="CCQ02";
        }else{
            startArea="CCQ03";
        }
        List<Integer> enableStacker = wcsTaskApiService.getEnableStacker();
        if(CollUtil.isEmpty(enableStacker)){
            throw new ServiceException("堆垛机目前都处于手动模式，取盘失败");
        }
        if (checkDetailService.haveChecking()) {
            throw new ServiceException("盘点任务进行中，取盘失败");
        }
        List<String> areaIds = wcsTaskApiService.getStoreAreaIdByEnableStackerId(enableStacker);
        if(!areaIds.contains(startArea)){
            throw new ServiceException(startArea+"库区的堆垛机目前都处于手动模式，取盘失败");
        }
        LambdaQueryWrapper<WcsOperateTask> operateTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskType,WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getOperateType,WmsWcsTypeEnum.TAKETRAY.getCode());
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskDeviceType,'2');
        operateTaskLambdaQueryWrapper.in(WcsOperateTask::getTaskStatus, "0",'1');
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getStartAreaCode, startArea);
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getStartPosition,locationCode );
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getDelFlag,  EnableStatus.ENABLE.getCode());
        if(wcsOperateTaskMapper.selectCount(operateTaskLambdaQueryWrapper) > 0){
            throw new ServiceException("已存在当前库位取盘任务，不可重复下发！");
        }
        Location childLocationInfo = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("enable_status", EnableStatus.ENABLE.getCode())
                .eq("location_code", locationCode)
        );
        if(childLocationInfo.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
            //查询母库位信息
            Location parentLocation = locationService.getParentLocation(childLocationInfo, startArea);
            LambdaQueryWrapper<WcsOperateTask> operateTaskLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            operateTaskLambdaQueryWrapper1.eq(WcsOperateTask::getTaskType,WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
            operateTaskLambdaQueryWrapper1.eq(WcsOperateTask::getOperateType,WmsWcsTypeEnum.TAKETRAY.getCode());
            operateTaskLambdaQueryWrapper1.eq(WcsOperateTask::getTaskDeviceType,'2');
            operateTaskLambdaQueryWrapper1.in(WcsOperateTask::getTaskStatus, "0",'1');
            operateTaskLambdaQueryWrapper1.eq(WcsOperateTask::getStartAreaCode,startArea);
            operateTaskLambdaQueryWrapper1.eq(WcsOperateTask::getStartPosition,parentLocation.getLocationCode() );
            operateTaskLambdaQueryWrapper1.eq(WcsOperateTask::getDelFlag,  EnableStatus.ENABLE.getCode());
            if(wcsOperateTaskMapper.selectCount(operateTaskLambdaQueryWrapper1) > 0){
                throw new ServiceException("母库位取盘任务正在进行中，请稍后再试！");
            }
        }
        //获取agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, "LHQ01")
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {//agv启用，锁定end库区库位
            if (Objects.equals(startArea, "CCQ01")) {
                boolean noTask = WmsTaskConstant.TAKE_TRAY_TASK_141.get();
                if (noTask) {
                    throw new ServiceException("141号AGV取盘操作正在进行中，请稍后再试");
                }
                List<String> locationCodeListOne = locationMapper.getEmptyLocationlhq(LhqLocationConstants.stagingOnes,"LHQ01", 1)
                        .stream()
                        .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
                if(ObjectUtil.isEmpty(locationCodeListOne)){
                    throw new ServiceException("理货区库位不足,141号AGV理货区目前空余库位数量为:"+ locationCodeListOne.size());
                }
                //组装并下发任务
                startDeviceTread(locationCode,startArea,"LHQ01",WmsTaskConstant.TAKE_TRAY_TASK_141::compareAndSet);
            }else{
                List<String> locationCodeListTwo = locationMapper.getEmptyLocationlhq(LhqLocationConstants.stagingTwos,"LHQ01", 1)
                        .stream()
                        .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
                if(ObjectUtil.isEmpty(locationCodeListTwo)){
                    throw new ServiceException("理货区库位不足,140号AGV理货区目前空余库位数量为:"+ locationCodeListTwo.size());
                }
                //组装并下发任务
                startDeviceTread(locationCode,startArea,"LHQ01",WmsTaskConstant.TAKE_TRAY_TASK_140::compareAndSet);
            }
        }else{
            //堆垛机取盘
            boolean noTask = WmsTaskConstant.TAKE_TRAY_TASK.get();
            if (noTask) {
                throw new ServiceException("取盘操作正在进行中，请稍后再试");
            }
            //组装并下发任务
            startDeviceTread(locationCode,startArea,"LHQ01",WmsTaskConstant.TAKE_TRAY_TASK::compareAndSet);
        }
        return "取盘任务下发成功";
    }

    //组装下发任务
    public void startDeviceTread(String emptyTraylocationCode, String startArea, String endAreaCode, BiPredicate<Boolean, Boolean> task){
        try {
            new Thread(() -> {
                task.test(false, true);
                List<String> hasParentLocationCodes = new ArrayList<>();
                //组装数据，取盘
                WmsWcsInfo tempInfo = new WmsWcsInfo();
                //根据空托盘库位查询信息
                LocationMapVo emptyTraylocation = trayMapper.getEmptyTraylocationCode(emptyTraylocationCode);

                //锁定库位
                LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
                locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                        .in(Location::getLocationCode, emptyTraylocation.getLocationCode());
                locationService.getBaseMapper().update(null, locationUpdate);
                //交叠组装数据
                LocationMapVo emptyTray = emptyTraylocation;
                if(emptyTray.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode())){
                    WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
                    info.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                    info.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                    info.put(WmsWcsInfo.START_AREA_CODE, startArea);
                    info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                    hasParentLocationCodes.add(emptyTray.getLocationCode());
                    tempInfo = info;
                }else{

                    Location childLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("enable_status", EnableStatus.ENABLE.getCode())
                            .eq("location_code", emptyTray.getLocationCode())
                    );
                    Location parentLocation = locationService.getParentLocation(childLocation, startArea);
                    //如何hasParentLocationCodes包含母库位，则表示子母库位在本次任务一起，则正常进行取盘，反之移库取盘
                    if(hasParentLocationCodes.contains(parentLocation.getLocationCode()) || parentLocation.getTrayCode() == null){
                        WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
                        info.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                        info.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                        info.put(WmsWcsInfo.START_AREA_CODE,startArea);
                        info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                        tempInfo = info;
                    }else{
                        String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(startArea);

                        List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                        WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        info.put(WmsWcsInfo.TRAY_CODE, childLocation.getTrayCode());
                        info.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                        info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);

                        //1.母库位移动至移库库位
                        WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                        childInfo.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                        childInfo.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                        childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentLocation.getLocationCode());
                        childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                        childInfoList.add(childInfo);

                        //2.取盘具体任务
                        WmsWcsInfo moveInfo = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
                        moveInfo.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                        moveInfo.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                        moveInfo.put(WmsWcsInfo.START_AREA_CODE, startArea);
                        moveInfo.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                        childInfoList.add(moveInfo);


                        //移库库位回至母库位
                        WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo3.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                        childInfo3.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                        childInfo3.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                        childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                        childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, parentLocation.getLocationCode());
                        childInfoList.add(childInfo3);
                        info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
                        tempInfo = info;
                    }
                }
                //取盘
                String msg = takeEmptyTray(tempInfo);
                task.test(true, false);
            }).start();
        } catch (Exception e) {
            task.test(true, false);
        }
    }


    /**
     * 选中有托盘无货库位取盘
     *
     * @param info trayCode 托盘, startLocationCode 起始库位, startAreaCode 起始库区, endAreaCode 结束库区
     * @return 取盘消息
     */
    @Transactional(rollbackFor = Exception.class)
    public String takeEmptyTray( WmsWcsInfo info) {
        String msg = "已取托盘："+info.get(WmsWcsInfo.TRAY_CODE);//取盘消息
        info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
        //给wcs发送的数据
        String endAreaCode = (String) info.get(WmsWcsInfo.END_AREA_CODE);

        List<String> locationCodeList = new ArrayList<>();
        //获取agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, endAreaCode)
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {//agv启用，锁定end库区库位
            //CCQ01
            String startLocation  = (String)info.get(WmsWcsInfo.START_AREA_CODE);
            if("CCQ01".equals(startLocation)){
                List<EmptyLocationBo> emptyLocationlhq = locationMapper.getEmptyLocationlhq(LhqLocationConstants.stagingOnes, endAreaCode, LhqLocationConstants.stagingOnes.length);
                emptyLocationlhq = emptyLocationlhq.subList(emptyLocationlhq.size() - 1, emptyLocationlhq.size());
                // 根据顺序进行逆序排序
                emptyLocationlhq = emptyLocationlhq.stream()
                        .sorted(Comparator.comparing(EmptyLocationBo::getOrderNum).reversed())
                        .collect(Collectors.toList());
                //获取CCQ01结束库区空闲库位
                List<String> locationCodeListOne = emptyLocationlhq
                        .stream()
                        .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
                locationCodeList.addAll(locationCodeListOne);
            }else{
                //CCQ02、CCQ03
                //获取CCQ01结束库区空闲库位
                List<String> locationCodeListTwo = locationMapper.getEmptyLocationlhq(LhqLocationConstants.stagingTwos,endAreaCode,1)
                        .stream()
                        .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
                locationCodeList.addAll(locationCodeListTwo);
            }
            //锁定结束库区空闲库位
            LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
            locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                    .in(Location::getLocationCode, locationCodeList);
            locationMapper.update(null, locationUpdate);
            //被分配结束库位的托盘
//          List<WmsWcsInfo> infoYesList = infoList.subList(0, size);
            Object chidList = info.get(WmsWcsInfo.CHILD_INFO_LIST);
            if (chidList != null) {
                List<WmsWcsInfo> chids = (List<WmsWcsInfo>) chidList;
                for (WmsWcsInfo chid : chids) {
                    if (StrUtil.isEmptyOrUndefined(chid.get(WmsWcsInfo.END_LOCATION_CODE) + "")
                            && chid.get(WmsWcsInfo.TYPE).equals(WmsWcsTypeEnum.TAKETRAY.getCode())) {
                        chid.put(WmsWcsInfo.END_LOCATION_CODE, locationCodeList.get(0));
                    }
                }
            }
            info.put(WmsWcsInfo.END_LOCATION_CODE, locationCodeList.get(0));
            List<WmsWcsInfo> infoYesList = new ArrayList<>();
            infoYesList.add(info);
            //将分配了结束库位的托盘数据给wcs
            //记录给wcs任务的数据
            tasklogService.saveBatch(infoYesList);
            //把任务的数据给wcs
            List<WmsToWcsTaskReq> collect = infoYesList.stream().map(infos -> {
                String jsonStr = JSONObject.toJSONString(infos);
                return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
            }).collect(Collectors.toList());
            wcsTaskApiService.agvLinkAgeStacker(collect);
        }else {//agv不启用，直接将托盘数据给wcs
                info.put(WmsWcsInfo.END_LOCATION_CODE,"csd");
                Object chidList = info.get(WmsWcsInfo.CHILD_INFO_LIST);
                if(chidList != null){
                    List<WmsWcsInfo> chids = (List<WmsWcsInfo>) chidList;
                    for (WmsWcsInfo chid : chids) {
                        if(StrUtil.isEmptyOrUndefined(chid.get(WmsWcsInfo.END_LOCATION_CODE)+"")){
                            chid.put(WmsWcsInfo.END_LOCATION_CODE,"csd");
                        }
                    }
                }
            List<WmsWcsInfo> infoYesList = new ArrayList<>();
            infoYesList.add(info);
            //记录给wcs任务的数据
            tasklogService.saveBatch(infoYesList);
            //把任务的数据给wcs
            List<WmsToWcsTaskReq> collect = infoYesList.stream().map(infos -> {
                String jsonStr = JSONObject.toJSONString(infos);
                return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
            }).collect(Collectors.toList());
            wcsTaskApiService.agvLinkAgeStacker(collect);
        }
        return msg;
    }


    /**
     * 禁用库位
     * @param location
     * @return
     */
    public AjaxResult upadateLocationEnable(Location location){
        UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
        if(ObjectUtil.isNotEmpty(location)){
            if(location.getFlag()){
                updateWrapper.set("enable_status",EnableStatus.DISABLE.getCode());
            }else{
                updateWrapper.set("enable_status",EnableStatus.ENABLE.getCode());
            }
            updateWrapper.eq("area_id",location.getAreaId());
            if(location.getAreaId().equals("CCQ03")){
                updateWrapper.le("column_num","6");
            }else{
                updateWrapper.ge("column_num","25");
            }


        }
       locationMapper.update(null,updateWrapper);
        return AjaxResult.success();
    }
}
