package com.ruoyi.wms.basics.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.Tray;
import com.ruoyi.wms.basics.domain.Warehouse;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.move.service.WmsMoveDetailService;
import com.ruoyi.wms.move.vo.WmsMoveDetailVo;
import com.ruoyi.wms.nolist.service.NolistWaitService;
import com.ruoyi.wms.outbound.service.OutbillGoodsService;
import com.ruoyi.wms.stock.service.DryOutbillGoodsService;
import com.ruoyi.wms.stock.service.WmsDryInbillGoodsService;
import com.ruoyi.wms.warehousing.mapper.ListingDetailMapper;
import com.ruoyi.wms.warehousing.service.ListingDetailService;
import com.ruoyi.wms.wcstask.service.WaittaskService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 库位基本信息Service接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Slf4j
@Service
public class LocationService extends ServiceImpl<LocationMapper, Location> {

    @Autowired
    protected Validator validator;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ListingDetailMapper listingDetailMapper;
    @Autowired
    private ListingDetailService listingDetailService;
    @Autowired
    private TrayMapper trayMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private AreaService areaService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private WmsMoveDetailService wmsMoveDetailService;
    @Autowired
    private OutbillGoodsService outbillGoodsService;
    @Autowired
    private DryOutbillGoodsService dryOutbillGoodsService;
    @Autowired
    private WmsDryInbillGoodsService dryInbillGoodsService;
    @Autowired
    private WaittaskService waittaskService;
    @Autowired
    private NolistWaitService nolistWaitService;
    @Autowired
    private TrayService trayService;


    /**
     * 查询库位基本信息
     *
     * @param id 库位基本信息主键
     * @return 库位基本信息
     */
    public Location selectLocationById(String id) {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return locationMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询库位基本信息
     *
     * @param ids 库位基本信息 IDs
     * @return 库位基本信息
     */
    public List<Location> selectLocationByIds(String[] ids) {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return locationMapper.selectList(queryWrapper);
    }

    /**
     * 查询库位基本信息列表
     *
     * @param location 库位基本信息
     * @return 库位基本信息集合
     */
    public List<Location> selectLocationList(Location location) {
        QueryWrapper<Location> queryWrapper = getQueryWrapper(location);
        return locationMapper.select(queryWrapper);
    }
    /**
     * 获取可选择的库位列表
     *
     * @param location 库位基本信息
     * @param areaIds 库位基本信息
     * @return 库位基本信息集合
     */
    public List<Location> listByParams(Location location,List<String> areaIds) {
        location.setLockStatus(LockEnum.NOTLOCK.getCode());
        QueryWrapper<Location> queryWrapper = getQueryWrapperNew(location,areaIds,"have");
        return locationMapper.listByParams(queryWrapper);
    }
    /**
     * 获取可选择的库位列表
     *
     * @param location 库位基本信息
     * @param areaIds 库位基本信息
     * @return 库位基本信息集合
     */
    public List<Location> listsByParams(Location location,List<String> areaIds) {
        location.setLockStatus(LockEnum.NOTLOCK.getCode());
        QueryWrapper<Location> queryWrapper = getQueryWrapperNew(location,areaIds,null);
        return locationMapper.listByParams(queryWrapper);
    }

    /**
     * 新增库位基本信息
     *
     * @param location 库位基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Location insertLocation(Location location) {
        location.setId(IdUtil.simpleUUID());
        location.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        location.setLockStatus(LockEnum.NOTLOCK.getCode());
        locationMapper.insert(location);
        updateData();
        return location;
    }

    /**
     * 修改库位基本信息
     *
     * @param location 库位基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Location updateLocation(Location location) {
        if (StrUtil.isNotEmpty(location.getId())) {
            Location item = locationMapper.selectById(location.getId());
            if (ObjectUtil.isNotNull(item) && StrUtil.isNotEmpty(item.getTrayCode()) && EnableStatus.DISABLE.getCode().equals(item.getEnableStatus())) {
                Tray tray = trayMapper.selectOne(new QueryWrapper<Tray>().eq("tray_code", item.getTrayCode()));
                if (ObjectUtil.isNotNull(tray) && StrUtil.isNotEmpty(tray.getGoodsCode())) {
                    throw new ServiceException("当前库位上托盘中有货物，无法禁用");
                }
            }
            locationMapper.updateById(location);
            updateData();
            return location;
        } else {
            throw new ServiceException("库位编号缺失");
        }
    }

    /**
     * 批量删除库位基本信息
     *
     * @param ids 需要删除的库位基本信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteLocationByIds(String[] ids) {
        List<Location> locations = new ArrayList<>();
        for (String id : ids) {
            Location location = new Location();
            location.setId(id);
            location.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            locations.add(location);
        }
        List<Location> locationList = locationMapper.selectBatchIds(Arrays.asList(ids));
        //查询托盘与库位关联表
        /*for (Location location : locationList) {
            int count = trayMapper.selectDataByLocationCode(location.getLocationCode());
            if (count > 0) {
                throw new ServiceException(location.getLocationName() + "该数据已被托盘引用无法删除");
            }
        }*/
        int result = super.updateBatchById(locations) ? 1 : 0;
        if (result > 0) {
            updateData();
        }
        return AjaxResult.success(result);
    }

    //关联修改库区、仓库
    @Transactional(rollbackFor = Exception.class)
    public void updateData() {
        QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode());
        //查询所有库区
        List<Area> areaList = areaMapper.selectList(queryWrapper);
        for (Area area: areaList) {
            //修改库区
            BigDecimal totalAreaCapacity = locationMapper.countCapacityByArea(area.getAreaCode());
            area.setTotalCapacity(totalAreaCapacity);
        }
        areaService.updateBatchById(areaList);
        QueryWrapper<Warehouse> warehouseQueryWrapper = new QueryWrapper<>();
        warehouseQueryWrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode());
        //查询所有库区
        List<Warehouse> warehouseList = warehouseService.list(warehouseQueryWrapper);
        for (Warehouse warehouse:warehouseList) {
            //修改仓库
            BigDecimal totalHouseCapacity = locationMapper.countCapacityByHouse(warehouse.getWarehouseCode());
            warehouse.setTotalCapacity(totalHouseCapacity);
        }
        warehouseService.updateBatchById(warehouseList);
    }


    /**
     * 删除库位基本信息信息
     *
     * @param id 库位基本信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteLocationById(String id) {
        Location location = new Location();
        location.setId(id);
        location.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return locationMapper.updateById(location);
    }

    public QueryWrapper<Location> getQueryWrapper(Location location) {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        if (location != null) {
            location.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", location.getDelFlag());
            //库位编码
            if (StrUtil.isNotEmpty(location.getLocationCode())) {
                queryWrapper.like("location_code", location.getLocationCode());
            }
            //库位名称
            if (StrUtil.isNotEmpty(location.getLocationName())) {
                queryWrapper.like("location_name", location.getLocationName());
            }
            //库位名称
            if (StrUtil.isNotEmpty(location.getLocationName())) {
                queryWrapper.like("location_name", location.getLocationName());
            }
            //所属仓库编号
            if (StrUtil.isNotEmpty(location.getWarehouseId())) {
                queryWrapper.eq("warehouse_id", location.getWarehouseId());
            }
            //所属库区编号
            if (StrUtil.isNotEmpty(location.getAreaId())) {
                queryWrapper.eq("area_id", location.getAreaId());
            }
            //是否锁定
            if (StrUtil.isNotEmpty(location.getLockStatus())) {
                queryWrapper.eq("lock_status", location.getLockStatus());
            }
            //是否启用
            if (StrUtil.isNotEmpty(location.getEnableStatus())) {
                queryWrapper.eq("enable_status", location.getEnableStatus());
            }
            String[] s = {"warehouse_id","area_id","location_code"};
            queryWrapper.orderByDesc(Arrays.asList(s));
        }
        return queryWrapper;
    }
    public QueryWrapper<Location> getQueryWrapperNew(Location location,List<String> areaIds,String trayCode) {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        if (location != null) {
            location.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", location.getDelFlag());
            queryWrapper.eq("enable_status", EnableStatus.ENABLE.getCode());
            //库位编码
            if (StrUtil.isNotEmpty(location.getLocationCode())) {
                queryWrapper.like("location_code", location.getLocationCode());
            }
            //库位名称
            if (StrUtil.isNotEmpty(location.getLocationName())) {
                queryWrapper.like("location_name", location.getLocationName());
            }
            //库位名称
            if (StrUtil.isNotEmpty(location.getLocationName())) {
                queryWrapper.like("location_name", location.getLocationName());
            }
            //所属仓库编号
            if (StrUtil.isNotEmpty(location.getWarehouseId())) {
                queryWrapper.eq("warehouse_id", location.getWarehouseId());
            }
            //所属库区编号
            if (StrUtil.isNotEmpty(location.getAreaId())) {
                queryWrapper.eq("area_id", location.getAreaId());
            }
            //是否锁定
            if (StrUtil.isNotEmpty(location.getLockStatus())) {
                queryWrapper.eq("lock_status", location.getLockStatus());
            }
            //排除数据
            if (StringUtils.isNotEmpty(location.getLocationCodes())) {
                queryWrapper.notIn("location_code", location.getLocationCodes());
            }
        }
        if(StringUtils.isNotNull(areaIds)){
            queryWrapper.in("area_id", areaIds);
        }
        if(StringUtils.isNotEmpty(trayCode)){
            queryWrapper.isNotNull("tray_code");
        }
        queryWrapper.notLike("location_name","移库");
        return queryWrapper;
    }

    public AjaxResult checkData(Location location) {
        //验证库位数据唯一性
        int count = locationMapper.checkCode(location);
        if (count > 0) {
            throw new ServiceException("该库位编码已存在，请保证库位编码唯一");
        }
        count = locationMapper.checkName(location);
        if (count > 0) {
            throw new ServiceException("该库位名称已存在，请保证库位名称唯一");
        }
        count = locationMapper.checkOrder(location);
        if (count > 0) {
            throw new ServiceException("该库位排序值在同库区下已存在，请保证库位排序值唯一");
        }
        return AjaxResult.success(true);
    }

    /**
     * 数据导入实现
     *
     * @param locationList  导入的数据
     * @param updateSupport 是否更新已存在数据
     * @param operName      操作人
     * @param warehouseId   选择的仓库
     * @param areaId        选择的库区
     * @return
     */
    public String importData(List<Location> locationList, boolean updateSupport, String operName, String warehouseId, String areaId) {
        if (StringUtils.isNull(locationList) || locationList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int i=1;
        List<String> locationCode = new ArrayList<>();
        List<String> locationNanme = new ArrayList<>();
        List<Integer> locationOrder = new ArrayList<>();
        for (Location tray:locationList) {
            if(locationCode.contains(tray.getLocationCode())){
                throw new ServiceException("导入数据中的第"+(i+1)+"行的库位编码有重复");
            }else{
                locationCode.add(tray.getLocationCode());
            }
            if(locationNanme.contains(tray.getLocationName())){
                throw new ServiceException("导入数据中的第"+(i+1)+"行的库位名称有重复");
            }else{
                locationNanme.add(tray.getLocationName());
            }
            if(locationOrder.contains(tray.getOrderNum())){
                throw new ServiceException("导入数据中的第"+(i+1)+"行的库位排序值有重复");
            }else{
                locationOrder.add(tray.getOrderNum());
            }
            i++;
        }
        LambdaQueryWrapper<Location> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Location::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .in(Location::getLocationCode,locationCode);
        List<Location> locations = locationMapper.selectList(queryWrapper);
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        List<Location> locationDatas = new ArrayList<>();
        for (Location location : locationList) {
            if(null==location){
                throw new ServiceException("导入数据模板不正确，请重新选择");
            }
            location.setWarehouseId(warehouseId);
            location.setAreaId(areaId);
            if(StringUtils.isEmpty(location.getLocationCode())){
                throw new ServiceException("导入的库位编码不能为空");
            }else if(location.getLocationCode().length()>20){
                throw new ServiceException("导入的库位编码长度不能超过20");
            }
            if(StringUtils.isEmpty(location.getLocationName())){
                throw new ServiceException("导入的库位名称不能为空");
            }else if(location.getLocationName().length()>20){
                throw new ServiceException("导入的库位名称长度不能超过20");
            }
            if(StringUtils.isNull(location.getTotalCapacity())){
                throw new ServiceException("导入的库位容量不能为空");
            }else if(location.getTotalCapacity().compareTo(BigDecimal.ZERO)<=0||location.getTotalCapacity().compareTo(BigDecimal.valueOf(9999999.999))>0){
                throw new ServiceException("导入的库位容量要在0到9999999.999之间");
            }
            if(StringUtils.isNull(location.getTolerableWeight())){
                throw new ServiceException("导入的可容重量不能为空");
            }else if(location.getTolerableWeight().compareTo(BigDecimal.ZERO)<=0||location.getTolerableWeight().compareTo(BigDecimal.valueOf(9999999.999))>0){
                throw new ServiceException("导入的可容重量要在0到9999999.999之间");
            }
            if(StringUtils.isNull(location.getPlatoon())){
                throw new ServiceException("导入的库位排数不能为空");
            }else if(location.getPlatoon()<=0||location.getPlatoon()>999){
                throw new ServiceException("导入的库位排数要在0到999之间");
            }
            if(StringUtils.isNull(location.getLayer())){
                throw new ServiceException("导入的库位层数不能为空");
            }else if(location.getLayer()<=0||location.getLayer()>999){
                throw new ServiceException("导入的库位层数要在0到999之间");
            }
            if(StringUtils.isNull(location.getColumnNum())){
                throw new ServiceException("导入的库位列数不能为空");
            }else if(location.getColumnNum()<=0||location.getColumnNum()>999){
                throw new ServiceException("导入的库位列数要在0到999之间");
            }
            if(StringUtils.isNull(location.getOrderNum())){
                throw new ServiceException("导入的库位排序值不能为空");
            }else if(location.getColumnNum()<=0||location.getColumnNum()>9999){
                throw new ServiceException("导入的库位排序值要在0到9999之间");
            }

            try {
                // 验证是否存在这个模板
                Location u = null;
                endFor:
                for (Location l :locations) {
                    if(location.getLocationCode().equals(l.getLocationCode())){
                        u = l;
                        break endFor;
                    }
                }
                if (StringUtils.isNull(u)) {
                    int count = locationMapper.checkName(location);
                    if(count>0){
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、库位 " + location.getLocationName() + "的名称 已存在");
                    }else{
                        count = locationMapper.checkOrder(location);
                        if (count > 0) {
                            throw new ServiceException("库位排序值【"+location.getOrderNum()+"】在同库区下已存在，请保证库位排序值唯一");
                        }else{
                            BeanValidators.validateWithException(validator, location);
                            location.setId(IdUtil.simpleUUID());
                            location.setCreateBy(operName);
                            location.setCreateTime(new Date());
                            location.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                            successNum++;
                            successMsg.append("<br/>" + successNum + "、库位 " + location.getLocationName() + " 导入成功");
                            locationDatas.add(location);
                        }
                    }

                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, location);
                    location.setId(u.getId());
                    int count = locationMapper.checkCode(location);
                    if (count > 0) {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、库位 " + location.getLocationName() + "的编码 已存在");
                    } else {
                        count = locationMapper.checkName(location);
                        if (count > 0) {
                            failureNum++;
                            failureMsg.append("<br/>" + failureNum + "、库位 " + location.getLocationName() + "的名称 已存在");
                        } else {
                            count = locationMapper.checkOrder(location);
                            if (count > 0) {
                                throw new ServiceException("库位排序值【"+location.getOrderNum()+"】在同库区下已存在，请保证库位排序值唯一");
                            }else{
                                location.setUpdateBy(operName);
                                successNum++;
                                successMsg.append("<br/>" + successNum + "、库位 " + location.getLocationName() + " 更新成功");
                                locationDatas.add(location);
                            }
                        }
                    }
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、库位 " + location.getLocationName() + "的编码 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、库位 " + location.getLocationName() + " 导入失败：";
                failureMsg.append(msg + "数据类型不匹配或者长度太长");
                log.error(msg, e);
            }
        }
        if(locationDatas.size()>0){
            locationService.saveOrUpdateBatch(locationDatas);
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据导入失败，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        updateData();
        return successMsg.toString();
    }

    /**
     * 获取库位信息（）
     *
     * @return 【{label：xx，value：xx}】
     */
    public List getLocationData(Location location) {
        return locationMapper.getLocationData(location);
    }


    /**
     * 堆垛机信息接口
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsWcsInfo stackerInfo(@NotNull WmsWcsInfo wmsWcsInfo) {
        String type = (String) wmsWcsInfo.get(WmsWcsInfo.TYPE);//交互类型
        if (WmsWcsTypeEnum.TAKETRAY.getCode().equals(type)) {//取盘
            //任务类型为盘点出库时，不做操作
            if (WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                return wmsWcsInfo;
            }
            //解锁start库位，将托盘从start库位移开
            String startLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.START_LOCATION_CODE);//start库位编码
            LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
            locationUpdate.set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                    .set(Location::getTrayCode, null)
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getLocationCode, startLocationCode);
            locationMapper.update(null, locationUpdate);
            //更新库存
            outbillGoodsService.takeUpdateTblstock(wmsWcsInfo);
            //任务类型为晾晒出库时，解锁库存，修改晾晒出库状态
            if (WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                dryOutbillGoodsService.dryingOutEnd(wmsWcsInfo);
            }
        } else if (WmsWcsTypeEnum.PUTTRAY.getCode().equals(type)) {//回盘
            //任务类型为盘点入库时，不做操作
            if (WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                return wmsWcsInfo;
            }
            String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编码
            //解锁end库位，将托盘放在end库位上
            String endLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE);//end库位编码
            LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
            locationUpdate.set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                    .set(Location::getTrayCode, trayCode)
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getLocationCode, endLocationCode);
            locationMapper.update(null, locationUpdate);


            //解锁start库位，晾晒区库位和托盘解绑
            if (WmsWcsTaskTypeEnum.DRY_STORAGE.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                LambdaUpdateWrapper<Location> dryInLocationUpdate = Wrappers.lambdaUpdate();
                dryInLocationUpdate
                        .set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                        .set(Location::getTrayCode, null)
                        .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                        .eq(Location::getAreaId, "LSQ01")
                        .eq(Location::getTrayCode, trayCode);
                locationMapper.update(null, dryInLocationUpdate);
            }

            //解锁start库位，理货区库位和托盘解绑
            if (WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE)) || WmsWcsTaskTypeEnum.NORMAL_EMPTYTRAY.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                LambdaUpdateWrapper<Location> tallyAreaLocationUpdate = Wrappers.lambdaUpdate();
                tallyAreaLocationUpdate
                        .set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                        .set(Location::getTrayCode, null)
                        .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                        .eq(Location::getAreaId, "LHQ01")
                        .eq(Location::getTrayCode, trayCode);
                locationMapper.update(null, tallyAreaLocationUpdate);
            }


            //判断是空盘还是上架
            boolean isEmpty = trayMapper.isEmptyTray(trayCode);
            //1、非空盘回
            if(!isEmpty){
                if(StringUtils.isNotEmpty(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE)+"")&& WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                    //2、上架，库存总览新增
                    listingDetailService.putOnTray(wmsWcsInfo);
                }else if(StringUtils.isNotEmpty(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE)+"")&& WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                    //3、出库后回盘（有货）更新库存总览的部分字段以及主数据状态
                    outbillGoodsService.putOnTray(wmsWcsInfo);
                }
                //else if(StringUtils.isNotEmpty(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE)+"")&& WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                //    //4、移库回盘,更新库存总览的部分字段以及主数据状态
                //    wmsMoveDetailService.putOnTray(wmsWcsInfo);
                //}
                //else if(StringUtils.isNotEmpty(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE)+"")&& WmsWcsTaskTypeEnum.DRY_STORAGE.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                //    //5、晾晒回盘,更新库存总览的部分字段以及主数据状态
                //}
                else if(StringUtils.isNotEmpty(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE)+"")&& WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                    //6、盘点回盘,更新库存总览的部分字段以及主数据状态
                }else{
                    //更新托盘上的货物库存位置
                    outbillGoodsService.putOnTray(wmsWcsInfo);
                }
            }
            //任务类型为无单时,删除待上架列表、解锁库存
            if (WmsWcsTaskTypeEnum.NO_ORDER.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                nolistWaitService.putOnTray(trayCode);
            }
            //任务类型为移库时,解锁库位，修改移库状态
            if (WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                String doc = (String) wmsWcsInfo.get(WmsWcsInfo.DOC);//移库单详情id
                String id = (String) wmsWcsInfo.get(WmsWcsInfo.SERVICE_ID);
                if (StringUtils.isNotEmpty(doc) && doc.startsWith("YK") && StrUtil.isNotEmpty(id)){
                    wmsMoveDetailService.moveEnd(id);
                }
            }
            //任务类型为晾晒入库时，修改晾晒入库状态为已入库
            if (WmsWcsTaskTypeEnum.DRY_STORAGE.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
                dryInbillGoodsService.dryingInEnd(wmsWcsInfo);
            }
            //回盘后消费待执行的取盘任务
            //waittaskService.consumeTask();
        //（同库区）移库
        }else if(WmsWcsTypeEnum.RELOCATION.getCode().equals(type)){
            String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编码
            //将托盘从start库位移开
            String startLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.START_LOCATION_CODE);//start库位编码
            LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
            locationUpdate.set(Location::getTrayCode, null)
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .eq(Location::getLocationCode, startLocationCode);
            locationMapper.update(null, locationUpdate);
            //将托盘放在end库位上
            String endLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE);//end库位编码
            locationUpdate.clear();
            locationUpdate.set(Location::getTrayCode, trayCode)
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .eq(Location::getLocationCode, endLocationCode);
            locationMapper.update(null, locationUpdate);
            String doc = (String) wmsWcsInfo.get(WmsWcsInfo.DOC);//移库单详情id
            String id = (String) wmsWcsInfo.get(WmsWcsInfo.SERVICE_ID);

            Object moveLast = wmsWcsInfo.get(WmsWcsInfo.MOVE_LAST);
            if(moveLast != null){
                if((moveLast+"").equals("true")){
                    LambdaUpdateWrapper<Location> locationUpdate1 = Wrappers.lambdaUpdate();
                    locationUpdate1.set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                            .in(Location::getLocationCode, endLocationCode);
                    locationService.getBaseMapper().update(null, locationUpdate1);
                }
            }
            if (StringUtils.isNotEmpty(doc) && doc.startsWith("YK") && StrUtil.isNotEmpty(id)){
                wmsMoveDetailService.moveEnd(id);
            }
            outbillGoodsService.putOnTray(wmsWcsInfo);
        }
        wmsWcsInfo.put(WmsWcsInfo.CODE,HttpStatus.SUCCESS);

        return wmsWcsInfo;
    }

    /**
     * AGV信息接口
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsWcsInfo agvInfo(WmsWcsInfo wmsWcsInfo) {
        String type = (String) wmsWcsInfo.get(WmsWcsInfo.TYPE);//交互类型
        if (WmsWcsTypeEnum.TAKETRAY.getCode().equals(type)) {//取盘
            //解锁end库位，将托盘放在end库位上
            String endLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE);//end库位编码
            String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编码
            LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
            locationUpdate.set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                    .set(Location::getTrayCode, trayCode)
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .eq(Location::getLocationCode, endLocationCode);
            locationMapper.update(null, locationUpdate);
            //任务类型为移库时
            if(WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode().equals(wmsWcsInfo.get(WmsWcsInfo.TASKTYPE))){
               // trayService.putTray(wmsWcsInfo);
                String id = (String) wmsWcsInfo.get(WmsWcsInfo.SERVICE_ID);//移库单详情id
                if (StringUtils.isNotEmpty(id)){//创建回盘任务
                    WmsMoveDetailVo map = new WmsMoveDetailVo();
                    map.setId(id);
                    map.setStartAreaCode((String) wmsWcsInfo.get(WmsWcsInfo.END_AREA_CODE));
                    wmsMoveDetailService.putTray(map);
                }
            }
        } else if (WmsWcsTypeEnum.PUTTRAY.getCode().equals(type)) {//回盘
            String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编码
            if(StringUtils.isEmpty(trayCode)){
                throw new ServiceException("参数托盘编码缺失");
            }
            //解锁start库位，理货区或晾晒区库位和托盘解绑
            String strLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.START_LOCATION_CODE);//开始库位编码
            LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
            locationUpdate.set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                    .set(Location::getTrayCode, null)
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .eq(Location::getLocationCode, strLocationCode);
            locationMapper.update(null, locationUpdate);
        }
        wmsWcsInfo.put(WmsWcsInfo.CODE,HttpStatus.SUCCESS);
        return wmsWcsInfo;
    }


    /**
     * 根据子库位和区域id查询对应的父库位信息
     * @param childLocation
     * @param areaId
     * @return
     */
    public Location getParentLocation(Location childLocation,String areaId){
        Integer parentOrderNum = childLocation.getOrderNum()-1;
        Location parentLocation = locationMapper.selectOne(new QueryWrapper<Location>()
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("enable_status", EnableStatus.ENABLE.getCode())
                .eq("area_id", areaId)
                .eq("order_num", parentOrderNum)
        );
        return parentLocation;
    }

}
