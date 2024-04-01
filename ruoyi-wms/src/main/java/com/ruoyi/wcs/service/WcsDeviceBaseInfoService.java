package com.ruoyi.wcs.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.iot.task.ctrl.GatewayCollectCtrl;
import com.ruoyi.wcs.config.CameraConfig;
import com.ruoyi.wcs.domain.*;
import com.ruoyi.wcs.domain.vo.WcsCameraPushStreamVo;
import com.ruoyi.wcs.enums.camera.WcsWvpApiEnum;
import com.ruoyi.wcs.enums.wcs.WcsDeviceAreaEnum;
import com.ruoyi.wcs.enums.wcs.WcsSwitchStatusEnum;
import com.ruoyi.wcs.enums.wcs.WcsSystemStatusEnum;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wcs.util.WcsWvpUtil;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * WCS设备基本信息Service接口
 *
 * @author yangjie
 * @date 2023-02-24
 */
@Slf4j
@Service
public class WcsDeviceBaseInfoService extends ServiceImpl<WcsDeviceBaseInfoMapper, WcsDeviceBaseInfo> {

    /**
     * 摄像头镜头移动速度
     */
    private static final Integer CAMERA_SPEED = 240;

    @Autowired
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    @Autowired
    private WcsGateWayRealtionService wcsGateWayRealtionService;

    @Autowired
    private WcsFreshAirThtbRealtionService wcsFreshAirThtbRealtionService;

    @Autowired
    private WcsFreshAirDetailInfoService wcsFreshAirDetailInfoService;

    @Autowired
    private WcsSmartLightingDetailInfoService wcsSmartLightingDetailInfoService;

    @Autowired
    private WcsMeterDeviceRealtionService wcsMeterDeviceRealtionService;

    @Autowired
    protected Validator validator;

    @Resource
    private CameraConfig cameraConfig;

    @Autowired
    private GatewayCollectCtrl gatewayCollectCtrl;

    @Autowired
    private LocationService locationService;

    /**
     * 查询WCS设备基本信息
     *
     * @param id WCS设备基本信息主键
     * @return WCS设备基本信息
     */
    public WcsDeviceBaseInfo selectWcsDeviceBaseInfoById(String id) {
        QueryWrapper<WcsDeviceBaseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wcsDeviceBaseInfoMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询WCS设备基本信息
     *
     * @param ids WCS设备基本信息 IDs
     * @return WCS设备基本信息
     */
    public List<WcsDeviceBaseInfo> selectWcsDeviceBaseInfoByIds(String[] ids) {
        QueryWrapper<WcsDeviceBaseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wcsDeviceBaseInfoMapper.selectList(queryWrapper);
    }

    /**
     * 查询WCS设备基本信息列表
     *
     * @param wcsDeviceBaseInfo WCS设备基本信息
     * @return WCS设备基本信息集合
     */
    public List<WcsDeviceBaseInfo> selectWcsDeviceBaseInfoList(WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        QueryWrapper<WcsDeviceBaseInfo> queryWrapper = getQueryWrapper(wcsDeviceBaseInfo);
        return wcsDeviceBaseInfoMapper.select(queryWrapper);
    }

    /**
     * 新增WCS设备基本信息
     *
     * @param wcsDeviceBaseInfo WCS设备基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WcsDeviceBaseInfo insertWcsDeviceBaseInfo(WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        int count = wcsDeviceBaseInfoMapper.selectCount(
                new QueryWrapper<WcsDeviceBaseInfo>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("device_no", wcsDeviceBaseInfo.getDeviceNo())
                        .ne("device_type", WcsTaskDeviceTypeEnum.CAMERA.getCode())
        ).intValue();

        if (count > 0) {
            throw new ServiceException("设备编号已存在");
        }

        if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceAddress())) {
            count = wcsDeviceBaseInfoMapper.selectCount(
                    new QueryWrapper<WcsDeviceBaseInfo>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("device_address", wcsDeviceBaseInfo.getDeviceAddress())
            ).intValue();

//            if (count > 0) {
//                throw new ServiceException("设备地址码已存在");
//            }
            wcsDeviceBaseInfo.setDeviceIp(null);
            wcsDeviceBaseInfo.setDevicePort(null);
        }


        if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceIp()) && wcsDeviceBaseInfo.getDevicePort() != null) {
            count = wcsDeviceBaseInfoMapper.selectCount(
                    new QueryWrapper<WcsDeviceBaseInfo>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("device_ip", wcsDeviceBaseInfo.getDeviceIp())
                            .eq("device_port", wcsDeviceBaseInfo.getDevicePort())
            ).intValue();

            if (count > 0) {
                throw new ServiceException("设备IP:" + wcsDeviceBaseInfo.getDeviceIp() + ",端口:" + wcsDeviceBaseInfo.getDevicePort() + "已存在");
            }
            wcsDeviceBaseInfo.setDeviceAddress(null);
        }


        wcsDeviceBaseInfo.setId(IdUtil.simpleUUID());
        wcsDeviceBaseInfo.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wcsDeviceBaseInfoMapper.insert(wcsDeviceBaseInfo);

        // 照明设备及新风系统需额外处理详情表初始化

        if (WcsTaskDeviceTypeEnum.LIGHT.getCode().equals(wcsDeviceBaseInfo.getDeviceType())) {
            WcsSmartLightingDetailInfo wcsSmartLightingDetailInfo = new WcsSmartLightingDetailInfo();
            wcsSmartLightingDetailInfo.setId(IdUtil.fastSimpleUUID());
            wcsSmartLightingDetailInfo.setDeviceInfoId(wcsDeviceBaseInfo.getId());
            wcsSmartLightingDetailInfo.setSwitchStatus(Integer.valueOf(WcsSwitchStatusEnum.CLOSE.getCode()));
            wcsSmartLightingDetailInfo.setSystemStatus(Integer.valueOf(WcsSystemStatusEnum.NORMAL.getCode()));
            wcsSmartLightingDetailInfoService.saveOrUpdate(wcsSmartLightingDetailInfo);
        }

        if (WcsTaskDeviceTypeEnum.FRESHAIR.getCode().equals(wcsDeviceBaseInfo.getDeviceType())) {
            WcsFreshAirDetailInfo wcsFreshAirDetailInfo = new WcsFreshAirDetailInfo();
            wcsFreshAirDetailInfo.setId(IdUtil.fastSimpleUUID());
            wcsFreshAirDetailInfo.setSwitchStatus(Integer.valueOf(WcsSwitchStatusEnum.CLOSE.getCode()));
            wcsFreshAirDetailInfo.setSystemStatus(Integer.valueOf(WcsSystemStatusEnum.NORMAL.getCode()));
            wcsFreshAirDetailInfo.setTemplature("0");
            wcsFreshAirDetailInfo.setTemplatureLow("0");
            wcsFreshAirDetailInfo.setTemplatureHigh("0");
            wcsFreshAirDetailInfo.setHumidity("0");
            wcsFreshAirDetailInfo.setHumidityLow("0");
            wcsFreshAirDetailInfo.setHumidityHigh("0");
            wcsFreshAirDetailInfo.setDeviceInfoId(wcsDeviceBaseInfo.getId());
            wcsFreshAirDetailInfoService.saveOrUpdate(wcsFreshAirDetailInfo);
        }

        return wcsDeviceBaseInfo;
    }

    /**
     * 修改WCS设备基本信息
     *
     * @param wcsDeviceBaseInfo WCS设备基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WcsDeviceBaseInfo updateWcsDeviceBaseInfo(WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        int count = wcsDeviceBaseInfoMapper.selectCount(
                new QueryWrapper<WcsDeviceBaseInfo>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("device_no", wcsDeviceBaseInfo.getDeviceNo())
                        .ne("id", wcsDeviceBaseInfo.getId())
                        .ne("device_type", WcsTaskDeviceTypeEnum.CAMERA.getCode())
        ).intValue();

        if (count > 0) {
            throw new ServiceException("设备编号已存在");
        }


        if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceAddress())) {
            count = wcsDeviceBaseInfoMapper.selectCount(
                    new QueryWrapper<WcsDeviceBaseInfo>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("device_address", wcsDeviceBaseInfo.getDeviceAddress())
                            .ne("id", wcsDeviceBaseInfo.getId())
            ).intValue();

//            if (count > 0) {
//                throw new ServiceException("设备地址码已存在");
//            }
            wcsDeviceBaseInfo.setDeviceIp(null);
            wcsDeviceBaseInfo.setDevicePort(null);
        }


        if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceIp()) && wcsDeviceBaseInfo.getDevicePort() != null) {
            count = wcsDeviceBaseInfoMapper.selectCount(
                    new QueryWrapper<WcsDeviceBaseInfo>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("device_ip", wcsDeviceBaseInfo.getDeviceIp())
                            .eq("device_port", wcsDeviceBaseInfo.getDevicePort())
                            .ne("id", wcsDeviceBaseInfo.getId())
            ).intValue();

            if (count > 0) {
                throw new ServiceException("设备IP:" + wcsDeviceBaseInfo.getDeviceIp() + ",端口:" + wcsDeviceBaseInfo.getDevicePort() + "已存在");
            }
            wcsDeviceBaseInfo.setDeviceAddress(null);
        }
        //根据堆垛机是否异常状态锁定库位
        if(WcsTaskDeviceTypeEnum.STACKER.getCode().equals(wcsDeviceBaseInfo.getDeviceType())){
            //1号堆垛机
            if("stacker-1".equals(wcsDeviceBaseInfo.getDeviceNo())){
               if("1".equals(wcsDeviceBaseInfo.getRunningStatus())){
                   UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
                   updateWrapper.set("enable_status",EnableStatus.DISABLE.getCode());
                   updateWrapper.in("column_num","28","27","26","25","24");
                   updateWrapper.in("layer","1","2","3","4");
                   updateWrapper.eq("area_id","CCQ01");
                   locationService.update(updateWrapper);
               } else{
                    UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("enable_status",EnableStatus.ENABLE.getCode());
                    updateWrapper.in("column_num","28","27","26","25","24");
                    updateWrapper.in("layer","1","2","3","4");
                    updateWrapper.eq("area_id","CCQ01");
                    locationService.update(updateWrapper);
                }
            }
            if("stacker-3".equals(wcsDeviceBaseInfo.getDeviceNo())) {
                if ("1".equals(wcsDeviceBaseInfo.getRunningStatus())) {
                    UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("enable_status", EnableStatus.DISABLE.getCode());
                    updateWrapper.in("column_num", "1", "2", "3", "4", "5");
                    updateWrapper.in("layer", "1", "2", "3", "4");
                    updateWrapper.eq("area_id", "CCQ03");
                    locationService.update(updateWrapper);
                } else {
                    UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("enable_status", EnableStatus.ENABLE.getCode());
                    updateWrapper.in("column_num", "1", "2", "3", "4", "5");
                    updateWrapper.in("layer", "1", "2", "3", "4");
                    updateWrapper.eq("area_id", "CCQ03");
                    locationService.update(updateWrapper);
                }
            }
        }

        wcsDeviceBaseInfoMapper.updateById(wcsDeviceBaseInfo);

        // 重新加载网关与设备映射关系
        gatewayCollectCtrl.initGateWayRelationData();

        return wcsDeviceBaseInfo;
    }

    /**
     * 批量删除WCS设备基本信息
     *
     * @param ids 需要删除的WCS设备基本信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWcsDeviceBaseInfoByIds(String[] ids) {
        List<WcsDeviceBaseInfo> wcsDeviceBaseInfos = new ArrayList<>();
        for (String id : ids) {
            WcsDeviceBaseInfo info = this.getById(id);
            if (ObjectUtil.isNotNull(info)) {
                long gateWayCount = wcsGateWayRealtionService.getBaseMapper().selectCount(new QueryWrapper<WcsGateWayRealtion>().eq("gate_way_device_no", info.getDeviceNo()).or()
                        .eq("no_ip_device_no", info.getDeviceNo()));
                if (gateWayCount > 0) {
                    throw new ServiceException(StrUtil.format("编号为{}的设备在网关与设备关联中存在请进行解绑后再进行删除"));
                }
                long freshAirCount = wcsFreshAirThtbRealtionService.getBaseMapper().selectCount(new QueryWrapper<WcsFreshAirThtbRealtion>().eq("fresh_air_device_no", info.getDeviceNo()).or()
                        .eq("thtb_device_no", info.getDeviceNo()));
                if (freshAirCount > 0) {
                    throw new ServiceException(StrUtil.format("编号为{}的设备在新风与温湿度传感器关联中存在请进行解绑后再进行删除"));
                }
                long meterCount = wcsMeterDeviceRealtionService.getBaseMapper().selectCount(new QueryWrapper<WcsMeterDeviceRealtion>().eq("meter_device_no", info.getDeviceNo()).or()
                        .eq("device_no", info.getDeviceNo()));
                if (meterCount > 0) {
                    throw new ServiceException(StrUtil.format("编号为{}的设备在电表与设备关联中存在请进行解绑后再进行删除"));
                }
            }
            WcsDeviceBaseInfo wcsDeviceBaseInfo = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo.setId(id);
            wcsDeviceBaseInfo.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wcsDeviceBaseInfos.add(wcsDeviceBaseInfo);
        }
        return super.updateBatchById(wcsDeviceBaseInfos) ? 1 : 0;
    }

    /**
     * 删除WCS设备基本信息信息
     *
     * @param id WCS设备基本信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWcsDeviceBaseInfoById(String id) {
        WcsDeviceBaseInfo wcsDeviceBaseInfo = new WcsDeviceBaseInfo();
        wcsDeviceBaseInfo.setId(id);
        wcsDeviceBaseInfo.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wcsDeviceBaseInfoMapper.updateById(wcsDeviceBaseInfo);
    }

    public QueryWrapper<WcsDeviceBaseInfo> getQueryWrapper(WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        QueryWrapper<WcsDeviceBaseInfo> queryWrapper = new QueryWrapper<>();
        if (wcsDeviceBaseInfo != null) {
            wcsDeviceBaseInfo.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", wcsDeviceBaseInfo.getDelFlag());
            //设备编号
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceNo())) {
                queryWrapper.like("device_no", wcsDeviceBaseInfo.getDeviceNo());
            }
            //设备名称
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceName())) {
                queryWrapper.like("device_name", wcsDeviceBaseInfo.getDeviceName());
            }
            //设备点位
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDevicePosition())) {
                queryWrapper.eq("device_position", wcsDeviceBaseInfo.getDevicePosition());
            }
            //库区编号
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getWarehouseAreaCode())) {
                queryWrapper.eq("warehouse_area_code", wcsDeviceBaseInfo.getWarehouseAreaCode());
            }
            //设备类型
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceType())) {
                queryWrapper.eq("device_type", wcsDeviceBaseInfo.getDeviceType());
            }
            //设备区域
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceArea())) {
                queryWrapper.eq("device_area", wcsDeviceBaseInfo.getDeviceArea());
            }
            //设备地址码
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceAddress())) {
                queryWrapper.like("device_address", wcsDeviceBaseInfo.getDeviceAddress());
            }
            //设备IP
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceIp())) {
                queryWrapper.like("device_ip", wcsDeviceBaseInfo.getDeviceIp());
            }
            //设备端口
            if (wcsDeviceBaseInfo.getDevicePort() != null) {
                queryWrapper.eq("device_port", wcsDeviceBaseInfo.getDevicePort());
            }
            //设备规格
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceSize())) {
                queryWrapper.eq("device_size", wcsDeviceBaseInfo.getDeviceSize());
            }
            //设备厂家
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceProducer())) {
                queryWrapper.eq("device_producer", wcsDeviceBaseInfo.getDeviceProducer());
            }
            //启用状态
            if (StrUtil.isNotEmpty(wcsDeviceBaseInfo.getEnableStatus())) {
                queryWrapper.eq("enable_status", wcsDeviceBaseInfo.getEnableStatus());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param wcsDeviceBaseInfoList 模板数据
     * @param updateSupport         是否更新已经存在的数据
     * @param operName              操作人姓名
     * @return
     */
    public String importData(List<WcsDeviceBaseInfo> wcsDeviceBaseInfoList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wcsDeviceBaseInfoList) || wcsDeviceBaseInfoList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WcsDeviceBaseInfo wcsDeviceBaseInfo : wcsDeviceBaseInfoList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                WcsDeviceBaseInfo u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wcsDeviceBaseInfo);
                    wcsDeviceBaseInfo.setId(IdUtil.simpleUUID());
                    wcsDeviceBaseInfo.setCreateBy(operName);
                    wcsDeviceBaseInfo.setCreateTime(new Date());
                    wcsDeviceBaseInfo.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wcsDeviceBaseInfoMapper.insert(wcsDeviceBaseInfo);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wcsDeviceBaseInfo);
                    //todo 验证
                    //int count = wcsDeviceBaseInfoMapper.checkCode(wcsDeviceBaseInfo);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    wcsDeviceBaseInfo.setId(u.getId());
                    wcsDeviceBaseInfo.setUpdateBy(operName);
                    wcsDeviceBaseInfo.setUpdateTime(new Date());
                    wcsDeviceBaseInfoMapper.updateById(wcsDeviceBaseInfo);
                    successNum++;
                    //}
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，数据重复；");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、第" + failureNum + "行数据导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条。");
        }
        return successMsg.toString();
    }

    /**
     * @param wcsDeviceBaseInfoList
     * @return
     */
    @Transactional
    public WcsDeviceBaseInfo updateWcsDeviceBaseInfoList(List<WcsDeviceBaseInfo> wcsDeviceBaseInfoList) {
        for (WcsDeviceBaseInfo wcsDeviceBaseInfo : wcsDeviceBaseInfoList) {
            wcsDeviceBaseInfoMapper.updateById(wcsDeviceBaseInfo);
        }
        return wcsDeviceBaseInfoList.get(0);
    }

    /**
     * 获取传感器信息列表
     *
     * @return WcsDeviceBaseInfo
     */
    public List<WcsDeviceBaseInfo> getSensorInfoList(WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        //已被新风关联的设备
        List<String> holdRelatedSensorInfoIds = wcsDeviceBaseInfoMapper.getSensorInfoIds();
        //已被网关关联的设备
        List<String> holdRelatedDeviceInfoIds = wcsDeviceBaseInfoMapper.getGateWayDeviceInfoIds();
        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        if (CollUtil.isNotEmpty(holdRelatedSensorInfoIds)) {
            qw.notIn("device_no", holdRelatedSensorInfoIds);
        }
        if(StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceNo())){
            qw.like("device_no", wcsDeviceBaseInfo.getDeviceNo());
        }
        qw.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        qw.eq("device_type", WcsTaskDeviceTypeEnum.TEMPERATURE_AND_HUMIDITY.getCode());
        qw.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
        List<WcsDeviceBaseInfo> list = wcsDeviceBaseInfoMapper.selectList(qw);
        if (CollUtil.isNotEmpty(holdRelatedDeviceInfoIds)) {
            return list.stream().filter(e -> holdRelatedDeviceInfoIds.contains(e.getDeviceNo())).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 获取网关可绑定设备信息列表
     *
     * @return WcsDeviceBaseInfo
     */
    public List<WcsDeviceBaseInfo> getGateWayDeviceInfoList(WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        List<String> holdRelatedDeviceInfoIds = wcsDeviceBaseInfoMapper.getGateWayDeviceInfoIds();
        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        if (CollUtil.isNotEmpty(holdRelatedDeviceInfoIds)) {
            qw.notIn("device_no", holdRelatedDeviceInfoIds);
        }
        if(StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceNo())){
            qw.like("device_no", wcsDeviceBaseInfo.getDeviceNo());
        }
        qw.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        qw.notIn("device_type", Arrays.asList(WcsTaskDeviceTypeEnum.AVG.getCode(),
                WcsTaskDeviceTypeEnum.STACKER.getCode(),
                WcsTaskDeviceTypeEnum.GATEWAY.getCode(),
                WcsTaskDeviceTypeEnum.CAMERA.getCode()));
        qw.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
        return wcsDeviceBaseInfoMapper.selectList(qw);
    }

    /**
     * 获取指定区域的摄像头列表
     *
     * @param type 区域类型
     * @return WcsDeviceBaseInfo
     */
    public List<WcsDeviceBaseInfo> getCameraListByType(String type) {
        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(type)) {
            qw.eq("device_area", type);
        } else {
            throw new ServiceException("区域类型缺失");
        }
        qw.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        qw.eq("device_type", WcsTaskDeviceTypeEnum.CAMERA.getCode());
        qw.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
        return wcsDeviceBaseInfoMapper.selectList(qw);
    }


    /**
     * 获取指定摄像头信息
     *
     * @param id 编号
     * @return WcsDeviceBaseInfo
     */
    public WcsDeviceBaseInfo getCameraInfoById(String id) {
        if (StrUtil.isNotEmpty(id)) {
            return this.getById(id);
        } else {
            throw new ServiceException("摄像头编号缺失");
        }
    }

    /**
     * 指定摄像头播放
     *
     * @param ids 摄像头编号 可多个
     * @return List<WcsCameraPushStreamVo>
     */
    public List<WcsCameraPushStreamVo> holdStartPlayer(String ids) {
        JSONArray paramList = JSONUtil.createArray();

        List<WcsCameraPushStreamVo> retList = new ArrayList<>();
        QueryWrapper<WcsDeviceBaseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", Arrays.asList(ids.split(",")));
        queryWrapper.eq("device_type", WcsTaskDeviceTypeEnum.CAMERA.getCode());
        queryWrapper.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
        //查询需播放摄像头列表信息
        List<WcsDeviceBaseInfo> list = this.getBaseMapper().selectList(queryWrapper);
        List<WcsDeviceBaseInfo> deviceInfos = list.stream().filter(e -> DelFlagEnum.DEL_NO.getCode().equals(e.getEnableStatus())).collect(Collectors.toList());

        for (WcsDeviceBaseInfo deviceInfo : deviceInfos) {
            if (StrUtil.isNotBlank(deviceInfo.getDeviceNo()) && StrUtil.isNotBlank(deviceInfo.getChannelId())) {
                Map<String, String> paramMap = new HashMap<>(2);
                paramMap.put("deviceId", deviceInfo.getDeviceNo());
                paramMap.put("channelId", deviceInfo.getChannelId());
                paramList.add(paramMap);
            }
        }

        String result = HttpRequest.post(cameraConfig.getWvpPro().getUrl() + WcsWvpApiEnum.PLAY_START_ALL.getCode())
                .header(WcsWvpApiEnum.ACCESS_TOKEN.getCode(), WcsWvpUtil.getInstance().getLoginToken())
                .body(paramList.toJSONString(0)).execute(false).body();

        // 返回值
        JSONObject resultJson = JSONUtil.parseObj(result);
        String code = String.valueOf(resultJson.get("code"));
        if (WcsWvpApiEnum.ERROR500.getCode().equals(code)) {
            log.warn("异常");
            return null;
        }
        if (WcsWvpApiEnum.SUCCESS.getCode().equals(code)) {
            JSONArray objects = JSONUtil.parseArray(resultJson.get("data"));
            for (WcsDeviceBaseInfo deviceInfo : deviceInfos) {
                WcsCameraPushStreamVo ret = new WcsCameraPushStreamVo();
                ret.setId(deviceInfo.getId());
                String deviceNo = deviceInfo.getDeviceNo();
                for (Object object : objects) {
                    JSONObject jsonObject = JSONUtil.parseObj(object);
                    String resultCode = String.valueOf(jsonObject.get("code"));
                    if (WcsWvpApiEnum.SUCCESS.getCode().equals(resultCode)) {
                        Object data = jsonObject.get("data");
                        JSONObject dataJson = JSONUtil.parseObj(data);
                        Object deviceId = dataJson.get("deviceID");
                        ret.setApp(String.valueOf(dataJson.get("app")));
                        ret.setMediaServerId(String.valueOf(dataJson.get("mediaServerId")));
                        ret.setStream(String.valueOf(dataJson.get("stream")));
                        if (deviceNo.equals(deviceId)) {
                            Object wsFlv = dataJson.get("ws_flv");
                            Object wssFlv = dataJson.get("wss_flv");
                            ret.setWsFlv(String.valueOf(wsFlv));
                            ret.setWssFlv(String.valueOf(wssFlv));
                            break;
                        }
                    }
                }
                String url = String.valueOf(ret.getWsFlv());
                if (StrUtil.isBlank(url)) {
                    ret.setMessage(StrUtil.format("{}摄像头播放失败，请重试", deviceInfo.getDeviceName()));
                }
                retList.add(ret);
            }
        }

        return retList;
    }

    /**
     * 调整指定摄像头方向
     *
     * @param id      摄像头编号
     * @param command 移动方向
     */
    public void adjustCameraDirection(String id, String command) {
        if (StrUtil.isNotEmpty(id)) {

            QueryWrapper<WcsDeviceBaseInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            queryWrapper.eq("device_type", WcsTaskDeviceTypeEnum.CAMERA.getCode());
            queryWrapper.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
            //查询需播放摄像头列表信息
            WcsDeviceBaseInfo deviceBaseInfo = this.getBaseMapper().selectOne(queryWrapper);

            if (deviceBaseInfo != null) {
                if (!DelFlagEnum.DEL_NO.getCode().equals(deviceBaseInfo.getEnableStatus())) {
                    throw new ServiceException("当前摄像头未在线");
                }
            } else {
                throw new ServiceException("系统未检测到当前摄像头信息");
            }

            if (StrUtil.isNotEmpty(deviceBaseInfo.getDeviceNo()) && StrUtil.isNotEmpty(deviceBaseInfo.getChannelId())) {
                String result = HttpRequest.post(StrUtil.format(cameraConfig.getWvpPro().getUrl() + WcsWvpApiEnum.ADJUST_DIRECTION.getCode()
                                + "{}/{}?command={}&horizonSpeed={}&verticalSpeed={}&zoomSpeed={}",
                        deviceBaseInfo.getDeviceNo(), deviceBaseInfo.getChannelId(), command, CAMERA_SPEED, CAMERA_SPEED, CAMERA_SPEED))
                        .header(WcsWvpApiEnum.ACCESS_TOKEN.getCode(), WcsWvpUtil.getInstance().getLoginToken())
                        .execute(false).body();

                // 返回值
                JSONObject resultJson = JSONUtil.parseObj(result);
                String code = String.valueOf(resultJson.get("code"));
                if (WcsWvpApiEnum.ERROR500.getCode().equals(code) || WcsWvpApiEnum.ERROR100.getCode().equals(code) || WcsWvpApiEnum.ERROR401.getCode().equals(code)) {
                    log.warn("异常; 原因: {}", resultJson.get("msg"));
                    throw new ServiceException("调整摄像头方向异常");
                }
            } else {
                throw new ServiceException("请检查当前摄像头设备编号及通道号是否填写完整");
            }


        }
    }

    /**
     * 获取摄像头分组列表
     *
     * @return Map<String, List < WcsDeviceBaseInfo>> resultMap
     */
    public Map<String, List<WcsDeviceBaseInfo>> getCameraListGroup() {
        Map<String, List<WcsDeviceBaseInfo>> resultMap = new HashMap<>(3);
        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        qw.select("id", "enable_status", "device_name", "device_area");
        qw.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        qw.eq("device_type", WcsTaskDeviceTypeEnum.CAMERA.getCode());
        List<WcsDeviceBaseInfo> list = this.getBaseMapper().selectList(qw);

        List<WcsDeviceBaseInfo> storageList = new ArrayList<>();
        List<WcsDeviceBaseInfo> dryOutList = new ArrayList<>();
        List<WcsDeviceBaseInfo> tallyList = new ArrayList<>();

        if (CollUtil.isNotEmpty(list)) {
            list.forEach(e -> {
                // 存储
                if (WcsDeviceAreaEnum.STORAGE.getCode().equals(e.getDeviceArea())) {
                    storageList.add(e);
                }
                // 晾晒
                if (WcsDeviceAreaEnum.DRY.getCode().equals(e.getDeviceArea())) {
                    dryOutList.add(e);
                }
                // 理货
                if (WcsDeviceAreaEnum.TALLY.getCode().equals(e.getDeviceArea())) {
                    tallyList.add(e);
                }
            });
        }
        resultMap.put("storage", storageList);
        resultMap.put("dryOut", dryOutList);
        resultMap.put("tally", tallyList);
        return resultMap;
    }

    /**
     * 上传摄像头截图
     *
     * @param file file
     */
    public void uploadScreenShot(MultipartFile file) throws Exception {
        if (ObjectUtil.isNotNull(file)) {
            String uploadPath = "";
            if (isOSLinux()) {
                uploadPath = cameraConfig.getLinuxImageUploadPath();
            } else {
                uploadPath = cameraConfig.getWindowsImageUploadPath();
            }
            String path = FileUploadUtils.upload(uploadPath, file, MimeTypeUtils.IMAGE_EXTENSION);
            WcsDeviceBaseInfo wcsDeviceBaseInfo = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo.setId(file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".png")));
            wcsDeviceBaseInfo.setDeviceImagePath(path);
            this.updateById(wcsDeviceBaseInfo);
        }
    }

    /**
     * 判断是否是linux操作系统
     *
     * @return boolean
     */
    public static boolean isOSLinux() {
        Properties prop = System.getProperties();

        String os = prop.getProperty("os.name");
        if (os != null && os.toLowerCase().indexOf("linux") > -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询电表可关联的设备
     *
     * @return
     */
    public List<WcsDeviceBaseInfo> getMeterDeviceInfoList(WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        //被网关关联的并且除了电表外的设备
        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        qw.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        qw.ne("device_type", WcsTaskDeviceTypeEnum.AMMETER.getCode());
        qw.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
        if(StrUtil.isNotEmpty(wcsDeviceBaseInfo.getDeviceNo())){
            qw.like("device_no", wcsDeviceBaseInfo.getDeviceNo());
        }
        List<WcsDeviceBaseInfo> list = wcsDeviceBaseInfoMapper.selectList(qw);

        // 已被电表关联的设备
        List<String> holdRelatedInfoIds = wcsMeterDeviceRealtionService.getBaseMapper().selectMeterRelatedDevices();
        if (CollUtil.isNotEmpty(holdRelatedInfoIds)) {
            return list.stream().filter(e -> !holdRelatedInfoIds.contains(e.getDeviceNo())).collect(Collectors.toList());
        } else {
            return list;
        }

    }

    /**
     * 晾晒区AGV是否启用
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean dryAgvIsEnable() {
        //获取agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, "LSQ01")
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        return wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0;
    }

    /**
     * 理货区AGV是否启用
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean tallyAgvIsEnable() {
        //获取agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, "LHQ01")
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        return wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0;
    }

}
