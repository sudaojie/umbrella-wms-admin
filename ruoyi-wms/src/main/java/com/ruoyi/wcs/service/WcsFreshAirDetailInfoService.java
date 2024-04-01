package com.ruoyi.wcs.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.iot.global.NettyGlobalConstant;
import com.ruoyi.iot.packet.humiture.rsp.HumitureReadRsp;
import com.ruoyi.iot.task.ctrl.DehumidifierCtrlTask;
import com.ruoyi.iot.task.ctrl.FreshAirCtrlTask;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsDeviceEarlyWarningInfo;
import com.ruoyi.wcs.domain.WcsFreshAirDetailInfo;
import com.ruoyi.wcs.domain.bo.WcsHumidityBo;
import com.ruoyi.wcs.domain.dto.WcsFreshAirFormDto;
import com.ruoyi.wcs.domain.dto.WcsFreshAirParamDto;
import com.ruoyi.wcs.domain.vo.WcsFreshAirVo;
import com.ruoyi.wcs.enums.wcs.WcsSwitchStatusEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wcs.mapper.WcsFreshAirDetailInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 新风系统详情信息Service接口
 *
 * @author hewei
 * @date 2023-04-12
 */
@Slf4j
@Service
public class WcsFreshAirDetailInfoService extends ServiceImpl<WcsFreshAirDetailInfoMapper, WcsFreshAirDetailInfo> {

    @Resource
    private WcsFreshAirDetailInfoMapper wcsFreshAirDetailInfoMapper;

    @Resource
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    @Autowired
    private FreshAirCtrlTask freshAirCtrlTask;

    @Autowired
    private DehumidifierCtrlTask dehumidifierCtrlTask;

    @Autowired
    private WcsDeviceEarlyWarningInfoService wcsDeviceEarlyWarningInfoService;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private WcsAlarmLightService wcsAlarmLightService;
    /**
     * 查询WCS新风系统基本信息列表
     *
     * @param wcsFreshAirParamDto wcsFreshAirParamDto
     * @return list
     */
    public List<WcsFreshAirVo> queryList(WcsFreshAirParamDto wcsFreshAirParamDto) {
        List<WcsFreshAirVo> list;
        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        qw.eq("t.device_type", WcsTaskDeviceTypeEnum.FRESHAIR.getCode());
        qw.eq("t.enable_status", DelFlagEnum.DEL_NO.getCode());
        qw.eq("t.del_flag", DelFlagEnum.DEL_NO.getCode());
        if (ObjectUtil.isNotNull(wcsFreshAirParamDto)) {
            if (StrUtil.isNotEmpty(wcsFreshAirParamDto.getDeviceNo())) {
                qw.like("t.device_no", wcsFreshAirParamDto.getDeviceNo());
            }
            if (StrUtil.isNotEmpty(wcsFreshAirParamDto.getDeviceArea())) {
                qw.eq("t.device_area", wcsFreshAirParamDto.getDeviceArea());
            }
        }
        list = wcsFreshAirDetailInfoMapper.query(qw);
        // Map<String, HumitureReadRsp> resultMap = NettyGlobalConstant.humidityMap;
        // List<WcsHumidityBo> humidityBos = new ArrayList<>();
        // if (CollUtil.isNotEmpty(list)) {
        //     Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> freshAirMap = NettyGlobalConstant.freshAirMappingMap;
        //     if (CollUtil.isNotEmpty(freshAirMap)) {
        //         freshAirMap.forEach((k, v) -> {
        //             if (CollUtil.isNotEmpty(v)) {
        //                 v.forEach(item -> {
        //                     WcsHumidityBo bo = new WcsHumidityBo();
        //                     bo.setHumidityVal(ObjectUtil.isNotNull(resultMap) && ObjectUtil.isNotNull(resultMap.get(item.getDeviceAddress())) ?
        //                             resultMap.get(item.getDeviceAddress()).getHumidityVal() : 0.00);
        //                     bo.setTemperatureVal(ObjectUtil.isNotNull(resultMap) && ObjectUtil.isNotNull(resultMap.get(item.getDeviceAddress())) ?
        //                             resultMap.get(item.getDeviceAddress()).getTemperatureVal() : 0.00);
        //                     bo.setId(item.getId());
        //                     humidityBos.add(bo);
        //                 });
        //             }
        //         });
        //     }
        //
        //     for (WcsFreshAirDetailInfo detailInfo : list) {
        //         freshAirMap.forEach((k, v) -> {
        //             if (k.getId().equals(detailInfo.getDeviceInfoId())) {
        //                 List<String> ids = v.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
        //                 if (CollUtil.isNotEmpty(ids)) {
        //                     // 湿度平均值
        //                     double humidityNum = humidityBos.stream().filter(e -> ids.contains(e.getId()))
        //                             .map(WcsHumidityBo::getHumidityVal)
        //                             .mapToDouble(Double::doubleValue).sum();
        //                     BigDecimal humidity = new BigDecimal(String.valueOf(humidityNum))
        //                             .divide(new BigDecimal(ids.size()), 2, RoundingMode.HALF_UP);
        //                     if (ObjectUtil.isNotNull(humidity) && humidity.compareTo(new BigDecimal("0")) != 0) {
        //                         detailInfo.setHumidity(String.valueOf(humidity));
        //                     }
        //
        //                     // 温度平均值
        //                     double templatureNum = humidityBos.stream().filter(e -> ids.contains(e.getId()))
        //                             .map(WcsHumidityBo::getTemperatureVal)
        //                             .mapToDouble(Double::doubleValue).sum();
        //                     BigDecimal templature = new BigDecimal(String.valueOf(templatureNum))
        //                             .divide(new BigDecimal(ids.size()), 2, RoundingMode.HALF_UP);
        //                     if (ObjectUtil.isNotNull(templature) && templature.compareTo(new BigDecimal("0")) != 0) {
        //                         detailInfo.setTemplature(String.valueOf(templature));
        //                     }
        //
        //                     if (ObjectUtil.isNotNull(detailInfo.getHumidityHigh()) && ObjectUtil.isNotNull(detailInfo.getHumidityLow())
        //                             && ObjectUtil.isNotNull(detailInfo.getTemplatureHigh()) && ObjectUtil.isNotNull(detailInfo.getTemplatureLow())) {
        //                         // 温湿度过高开启新风系统 过低关闭
        //                         WcsDeviceBaseInfo openBaseInfo = wcsDeviceBaseInfoMapper.selectById(detailInfo.getDeviceInfoId());
        //                         List<WcsDeviceBaseInfo> infos = new ArrayList<>();
        //                         if (ObjectUtil.isNotNull(openBaseInfo)) {
        //                             infos.add(openBaseInfo);
        //                         }
        //                         if (new BigDecimal(detailInfo.getHumidityHigh()).compareTo(humidity) > 0 && new BigDecimal(detailInfo.getHumidityLow()).compareTo(humidity) < 0
        //                                 && new BigDecimal(detailInfo.getTemplatureHigh()).compareTo(templature) > 0 && new BigDecimal(detailInfo.getHumidityLow()).compareTo(templature) < 0) {
        //                             freshAirCtrlTask.closeFreshAir(infos);
        //                             // dehumidifierCtrlTask.setCloseDehumidifierDevices(infos);
        //                             // 关闭新风、除湿
        //                             detailInfo.setSwitchStatus(Integer.parseInt(WcsSwitchStatusEnum.CLOSE.getCode()));
        //                             wcsFreshAirDetailInfoMapper.updateById(detailInfo);
        //                         } else {
        //                             freshAirCtrlTask.openFreshAir(infos);
        //                             // dehumidifierCtrlTask.setOpenDehumidifierDevices(infos);
        //                             // 开启新风、除湿
        //                             detailInfo.setSwitchStatus(Integer.parseInt(WcsSwitchStatusEnum.OPEN.getCode()));
        //                             wcsFreshAirDetailInfoMapper.updateById(detailInfo);
        //                         }
        //                     }
        //                 }
        //             }
        //         });
        //     }
        // }
        return list;
    }


    /**
     * 根据温湿度判断WCS新风系统
     *
     * @param wcsFreshAirParamDto wcsFreshAirParamDto
     * @return list
     */
    public List<WcsFreshAirVo> isEnabledWcsFreshAir(WcsFreshAirParamDto wcsFreshAirParamDto) {
        List<WcsFreshAirVo> list;
        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        qw.eq("t.device_type", WcsTaskDeviceTypeEnum.FRESHAIR.getCode());
        qw.eq("t.enable_status", DelFlagEnum.DEL_NO.getCode());
        qw.eq("t.del_flag", DelFlagEnum.DEL_NO.getCode());
        if (ObjectUtil.isNotNull(wcsFreshAirParamDto)) {
            if (StrUtil.isNotEmpty(wcsFreshAirParamDto.getDeviceNo())) {
                qw.like("t.device_no", wcsFreshAirParamDto.getDeviceNo());
            }
            if (StrUtil.isNotEmpty(wcsFreshAirParamDto.getDeviceArea())) {
                qw.eq("t.device_area", wcsFreshAirParamDto.getDeviceArea());
            }
        }
        list = wcsFreshAirDetailInfoMapper.query(qw);
        Map<String, HumitureReadRsp> resultMap = NettyGlobalConstant.humidityMap;
        List<WcsHumidityBo> humidityBos = new ArrayList<>();
        if (CollUtil.isNotEmpty(list)) {
            Map<WcsDeviceBaseInfo, List<WcsDeviceBaseInfo>> freshAirMap = NettyGlobalConstant.freshAirMappingMap;
            if (CollUtil.isNotEmpty(freshAirMap)) {
                freshAirMap.forEach((k, v) -> {
                    if (CollUtil.isNotEmpty(v)) {
                        v.forEach(item -> {
                            WcsHumidityBo bo = new WcsHumidityBo();
                            bo.setHumidityVal(ObjectUtil.isNotNull(resultMap) && ObjectUtil.isNotNull(resultMap.get(item.getDeviceAddress())) ?
                                    resultMap.get(item.getDeviceAddress()).getHumidityVal() : 0.00);
                            bo.setTemperatureVal(ObjectUtil.isNotNull(resultMap) && ObjectUtil.isNotNull(resultMap.get(item.getDeviceAddress())) ?
                                    resultMap.get(item.getDeviceAddress()).getTemperatureVal() : 0.00);
                            bo.setId(item.getId());
                            humidityBos.add(bo);
                        });
                    }
                });
            }

            for (WcsFreshAirDetailInfo detailInfo : list) {
                freshAirMap.forEach((k, v) -> {
                    if (k.getId().equals(detailInfo.getDeviceInfoId())) {
                        List<String> ids = v.stream().map(WcsDeviceBaseInfo::getId).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(ids)) {
                            // 湿度平均值
                            double humidityNum = humidityBos.stream().filter(e -> ids.contains(e.getId()))
                                    .map(WcsHumidityBo::getHumidityVal)
                                    .mapToDouble(Double::doubleValue).sum();
                            BigDecimal humidity = new BigDecimal(String.valueOf(humidityNum))
                                    .divide(new BigDecimal(ids.size()), 2, RoundingMode.HALF_UP);
                            if (ObjectUtil.isNotNull(humidity) && humidity.compareTo(new BigDecimal("0")) != 0) {
                                detailInfo.setHumidity(String.valueOf(humidity));
                            }

                            // 温度平均值
                            double templatureNum = humidityBos.stream().filter(e -> ids.contains(e.getId()))
                                    .map(WcsHumidityBo::getTemperatureVal)
                                    .mapToDouble(Double::doubleValue).sum();
                            BigDecimal templature = new BigDecimal(String.valueOf(templatureNum))
                                    .divide(new BigDecimal(ids.size()), 2, RoundingMode.HALF_UP);
                            if (ObjectUtil.isNotNull(templature) && templature.compareTo(new BigDecimal("0")) != 0) {
                                detailInfo.setTemplature(String.valueOf(templature));
                            }

                            if (ObjectUtil.isNotNull(detailInfo.getHumidityHigh()) && ObjectUtil.isNotNull(detailInfo.getHumidityLow())
                                    && ObjectUtil.isNotNull(detailInfo.getTemplatureHigh()) && ObjectUtil.isNotNull(detailInfo.getTemplatureLow())) {
                                // 温湿度过高开启新风系统 过低关闭
                                WcsDeviceBaseInfo openBaseInfo = wcsDeviceBaseInfoMapper.selectById(detailInfo.getDeviceInfoId());
                                List<WcsDeviceBaseInfo> infos = new ArrayList<>();
                                if (ObjectUtil.isNotNull(openBaseInfo)) {
                                    infos.add(openBaseInfo);
                                }
                                //根据设备id查询新风详细内容
                                LambdaQueryWrapper<WcsFreshAirDetailInfo> freshAirQw = Wrappers.lambdaQuery();
                                freshAirQw.eq(WcsFreshAirDetailInfo::getDeviceInfoId,detailInfo.getId());
                                WcsFreshAirDetailInfo wcsFreshAirDetailInfo = wcsFreshAirDetailInfoMapper.selectOne(freshAirQw);

                                if (new BigDecimal(detailInfo.getHumidityHigh()).compareTo(humidity) > 0 && new BigDecimal(detailInfo.getHumidityLow()).compareTo(humidity) < 0
                                        && new BigDecimal(detailInfo.getTemplatureHigh()).compareTo(templature) > 0 && new BigDecimal(detailInfo.getHumidityLow()).compareTo(templature) < 0) {
                                    freshAirCtrlTask.closeFreshAir(infos);
                                    // dehumidifierCtrlTask.setCloseDehumidifierDevices(infos);
                                    // 关闭新风、除湿
                                    wcsFreshAirDetailInfo.setSwitchStatus(Integer.parseInt(WcsSwitchStatusEnum.CLOSE.getCode()));
                                    wcsFreshAirDetailInfoMapper.updateById(wcsFreshAirDetailInfo);
                                } else {
                                    AjaxResult ajaxResult = freshAirCtrlTask.openFreshAir(infos);
                                    //不成功
                                    if(!ajaxResult.isSuccess()){
                                        List<String> deviceId = (List<String>) ajaxResult.get(AjaxResult.DATA_TAG);
                                        if(CollUtil.isNotEmpty(deviceId)){
                                            //根据设备id 查询设备信息
                                            LambdaQueryWrapper<WcsDeviceBaseInfo> deviceInfoQw = Wrappers.lambdaQuery();
                                            deviceInfoQw.eq(WcsDeviceBaseInfo::getId,deviceId.get(0));
                                            WcsDeviceBaseInfo wcsDeviceBaseInfo = wcsDeviceBaseInfoMapper.selectOne(deviceInfoQw);
                                            //设备预警信息
                                            WcsDeviceEarlyWarningInfo wcsDeviceEarlyWarningInfo = new WcsDeviceEarlyWarningInfo();
                                            wcsDeviceEarlyWarningInfo.setId(IdUtil.fastSimpleUUID());
                                            wcsDeviceEarlyWarningInfo.setDeviceInfoId(wcsDeviceBaseInfo.getDeviceNo());
                                            wcsDeviceEarlyWarningInfo.setWarningContent(ajaxResult.get(AjaxResult.MSG_TAG).toString());
                                            wcsDeviceEarlyWarningInfo.setWarningTime(new Date());
                                            wcsDeviceEarlyWarningInfoService.saveOrUpdate(wcsDeviceEarlyWarningInfo);
                                        }
                                    }else{
                                        // dehumidifierCtrlTask.setOpenDehumidifierDevices(infos);
                                        // 开启新风、除湿
                                        wcsFreshAirDetailInfo.setSwitchStatus(Integer.parseInt(WcsSwitchStatusEnum.OPEN.getCode()));
                                        wcsFreshAirDetailInfoMapper.updateById(wcsFreshAirDetailInfo);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        return list;
    }

    /**
     * 保存WCS新风系统基本信息
     *
     * @param wcsFreshAirFormDto wcsFreshAirFormDto
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveData(WcsFreshAirFormDto wcsFreshAirFormDto) {
        if (ObjectUtil.isNotNull(wcsFreshAirFormDto)) {
            if (StrUtil.isNotEmpty(wcsFreshAirFormDto.getDeviceInfoId())) {
                WcsFreshAirDetailInfo detailInfo = new WcsFreshAirDetailInfo();
                detailInfo.setId(StrUtil.isNotEmpty(wcsFreshAirFormDto.getId()) ? wcsFreshAirFormDto.getId() : IdUtil.fastSimpleUUID())
                        .setDeviceInfoId(wcsFreshAirFormDto.getDeviceInfoId())
                        .setHumidityHigh(wcsFreshAirFormDto.getHumidityHigh())
                        .setHumidityLow(wcsFreshAirFormDto.getHumidityLow())
                        .setSwitchStatus(wcsFreshAirFormDto.getSwitchStatus())
                        .setSystemStatus(wcsFreshAirFormDto.getSystemStatus())
                        .setTemplatureHigh(wcsFreshAirFormDto.getTemplatureHigh())
                        .setTemplatureLow(wcsFreshAirFormDto.getTemplatureLow());
                return this.saveOrUpdate(detailInfo);
            }
        }
        return false;
    }

    /**
     * 开启WCS新风系统
     *
     * @param id id
     */
    public AjaxResult start(String id){
        AjaxResult ajaxResult = new AjaxResult();
        if (StrUtil.isNotEmpty(id)) {
            List<String> ids = Arrays.asList(id.split(","));
            List<WcsFreshAirDetailInfo> list = new ArrayList<>();
            if (CollUtil.isNotEmpty(ids)) {
                ids.forEach(item -> {
                    WcsFreshAirDetailInfo wcsFreshAirDetailInfo = new WcsFreshAirDetailInfo();
                    wcsFreshAirDetailInfo.setId(item);
                    wcsFreshAirDetailInfo.setSwitchStatus(Integer.parseInt(WcsSwitchStatusEnum.OPEN.getCode()));
                    list.add(wcsFreshAirDetailInfo);
                });
                ajaxResult = giveControlFreshAir(ids, "1");
                if(!ajaxResult.isSuccess()){
                    List<String> deviceId = (List<String>) ajaxResult.get(AjaxResult.DATA_TAG);
                    if(CollUtil.isNotEmpty(deviceId)){
                        LambdaQueryWrapper<WcsFreshAirDetailInfo> qw = new LambdaQueryWrapper<>();
                        qw.in(WcsFreshAirDetailInfo::getDeviceInfoId,deviceId);
                        List<WcsFreshAirDetailInfo> wcsFreshAirDetailInfos = wcsFreshAirDetailInfoMapper.selectList(qw);
                        try {
                            //c 将JSON字符串转换为集合对象
                            // ObjectMapper objectMapper = new ObjectMapper();
                            // String jsonString = objectMapper.writeValueAsString(data);
                            // List<WcsFreshAirDetailInfo> collection = objectMapper.readValue(jsonString, new TypeReference<List<WcsFreshAirDetailInfo>>() {});
                            Iterator<WcsFreshAirDetailInfo> iterator = list.iterator();
                            while (iterator.hasNext()) {
                                WcsFreshAirDetailInfo listItem = iterator.next();
                                for (WcsFreshAirDetailInfo wcsFreshAirInfo : wcsFreshAirDetailInfos) {
                                    if (wcsFreshAirInfo.getId().equals(listItem.getId())) {
                                        iterator.remove();
                                        break; // 删除后立即跳出内层循环
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (CollUtil.isNotEmpty(list)) {
                 this.saveOrUpdateBatch(list);
            }
        } else {
            throw new ServiceException("启动新风系统缺失必要参数");
        }
        return ajaxResult;
    }

    /**
     * 关闭WCS新风系统
     *
     * @param id id
     */
    public boolean pause(String id) {
        if (StrUtil.isNotEmpty(id)) {
            List<String> ids = Arrays.asList(id.split(","));
            List<WcsFreshAirDetailInfo> list = new ArrayList<>();
            if (CollUtil.isNotEmpty(ids)) {
                ids.forEach(item -> {
                    WcsFreshAirDetailInfo wcsFreshAirDetailInfo = new WcsFreshAirDetailInfo();
                    wcsFreshAirDetailInfo.setId(item);
                    wcsFreshAirDetailInfo.setSwitchStatus(Integer.parseInt(WcsSwitchStatusEnum.CLOSE.getCode()));
                    list.add(wcsFreshAirDetailInfo);
                });
                giveControlFreshAir(ids, "2");
            }
            if (CollUtil.isNotEmpty(list)) {
                return this.saveOrUpdateBatch(list);
            }
        } else {
            throw new ServiceException("关闭新风系统缺失必要参数");
        }
        return false;
    }

    /**
     * 查询爆闪灯
     * @return 爆闪灯列表
     */
    public List<WcsFreshAirVo> queryExplosiveFlashList() {
        return wcsFreshAirDetailInfoMapper.queryExplosiveFlashList();
    }


    /**
     * 保存爆闪灯 温湿度的报警范围
     * @param wcsFreshAirFormDto 数据
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveExplosiveFlash(WcsFreshAirFormDto wcsFreshAirFormDto){

        if(StrUtil.isNotEmpty(wcsFreshAirFormDto.getTemplatureLow())){
             sysConfigMapper.updateConfigValueAndConfigKey("75",wcsFreshAirFormDto.getTemplatureLow());
        }
        if(StrUtil.isNotEmpty(wcsFreshAirFormDto.getTemplatureHigh())){
             sysConfigMapper.updateConfigValueAndConfigKey("77",wcsFreshAirFormDto.getTemplatureHigh());
        }
        if(StrUtil.isNotEmpty(wcsFreshAirFormDto.getHumidityLow())){
            sysConfigMapper.updateConfigValueAndConfigKey("79",wcsFreshAirFormDto.getHumidityLow());
        }
        if(StrUtil.isNotEmpty(wcsFreshAirFormDto.getHumidityHigh())){
            sysConfigMapper.updateConfigValueAndConfigKey("81",wcsFreshAirFormDto.getHumidityHigh());
        }
        return  true;
    }


    /**
     * 判断温湿度是否开始爆闪灯
     */
    public void isEnableExplosiveFlash(){

        Map<String, List<HumitureReadRsp>> resultMap = NettyGlobalConstant.humiditylistMap;
        resultMap.forEach((k, v) -> {
            if(CollUtil.isNotEmpty(v)){
                //湿度平均值
                double asHumidityVal = v.stream()
                        .mapToDouble(HumitureReadRsp::getHumidityVal)
                        .average()
                        .getAsDouble();
                BigDecimal humidityVal = new BigDecimal(String.valueOf(asHumidityVal)).setScale(2, RoundingMode.HALF_UP);

                //温度度平均值
                double asTemperatureVal = v.stream()
                        .mapToDouble(HumitureReadRsp::getTemperatureVal)
                        .average()
                        .getAsDouble();

                BigDecimal temperatureVal = new BigDecimal(String.valueOf(asTemperatureVal)).setScale(2, RoundingMode.HALF_UP);
                List<WcsFreshAirVo> wcsFreshAirVos = wcsFreshAirDetailInfoMapper.queryExplosiveFlashList();
                wcsFreshAirVos.forEach(e->{

                    if((new BigDecimal(e.getHumidityHigh()).compareTo(humidityVal) > 0 && new BigDecimal(e.getHumidityLow()).compareTo(humidityVal)<0) ||
                            (new BigDecimal(e.getTemplatureHigh()).compareTo(temperatureVal) > 0 && new BigDecimal(e.getTemplatureLow()).compareTo(temperatureVal)<0)
                    ){
                        log.info("爆闪灯关闭");
                        wcsAlarmLightService.close();
                    }else{
                        log.info("爆闪灯开启");
                        wcsAlarmLightService.open();
                    }

                });
            }
        });
    }
    /**
     * 向新风发送开启或者关闭指令
     *
     * @param ids  设备编号
     * @param flag flag
     */
    private AjaxResult giveControlFreshAir(List<String> ids, String flag) {
        AjaxResult ajaxResult = new AjaxResult();
        if (CollUtil.isNotEmpty(ids)) {
            List<WcsFreshAirDetailInfo> list = wcsFreshAirDetailInfoMapper.selectList(new QueryWrapper<WcsFreshAirDetailInfo>().in("id", ids));
            if (CollUtil.isNotEmpty(list)) {
                List<String> deviceInfoIds = list.stream().map(WcsFreshAirDetailInfo::getDeviceInfoId).collect(Collectors.toList());
                List<WcsDeviceBaseInfo> deviceBaseInfos = new ArrayList<>();
                if (CollUtil.isNotEmpty(deviceInfoIds)) {
                    deviceBaseInfos = wcsDeviceBaseInfoMapper.selectList(new QueryWrapper<WcsDeviceBaseInfo>().in("id", deviceInfoIds));
                }

                if (CollUtil.isNotEmpty(deviceBaseInfos)) {
                    if ("1".equals(flag)) {
                        ajaxResult =  freshAirCtrlTask.openFreshAir(deviceBaseInfos);
                    }
                    if ("2".equals(flag)) {
                        ajaxResult = freshAirCtrlTask.closeFreshAir(deviceBaseInfos);
                    }
                }
            }
        }
        return ajaxResult;
    }

}
