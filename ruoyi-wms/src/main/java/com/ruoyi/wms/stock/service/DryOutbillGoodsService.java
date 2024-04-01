package com.ruoyi.wms.stock.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.api.domain.WmsToWcsTaskReq;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsGateWayRealtion;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wms.basics.bo.EmptyLocationBo;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.basics.service.WmsTransferLocationService;
import com.ruoyi.wms.basics.vo.LocationMapVo;
import com.ruoyi.wms.enums.AreaTypeEnum;
import com.ruoyi.wms.enums.DryOutbillStatusTypeEnum;
import com.ruoyi.wms.enums.LocationTypeEnum;
import com.ruoyi.wms.global.WmsTaskConstant;
import com.ruoyi.wms.stock.domain.DryOutbill;
import com.ruoyi.wms.stock.domain.DryOutbillGoods;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.mapper.DryOutbillGoodsMapper;
import com.ruoyi.wms.stock.mapper.DryOutbillMapper;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.wcstask.service.TasklogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 晾晒出库单货物Service接口
 *
 * @author ruoyi
 * @date 2023-03-05
 */
@Slf4j
@Service
public class DryOutbillGoodsService extends ServiceImpl<DryOutbillGoodsMapper, DryOutbillGoods> {

    @Autowired(required = false)
    private DryOutbillGoodsMapper dryOutbillGoodsMapper;

    @Autowired(required = false)
    private DryOutbillMapper dryOutbillMapper;

    @Autowired(required = false)
    private AreaMapper areaMapper;

    @Autowired(required = false)
    private TrayMapper trayMapper;

    @Autowired(required = false)
    private LocationMapper locationMapper;

    @Autowired
    private TblstockMapper tblstockMapper;

    @Autowired(required = false)
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    @Autowired(required = false)
    private WcsTaskApiService wcsTaskApiService;

    @Autowired
    private TasklogService tasklogService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private WmsTransferLocationService wmsTransferLocationService;

    @Autowired
    protected Validator validator;

    /**
     * 初始化获取货区信息
     *
     * @return
     */
    public List<DryOutbillGoods> getAreaData() {
        return dryOutbillGoodsMapper.getAreaData();
    }

    /**
     * 查询晾晒出库单货物
     *
     * @param id 晾晒出库单货物主键
     * @return 晾晒出库单货物
     */
    public DryOutbillGoods selectWmsDryOutbillGoodsById(String id) {
        QueryWrapper<DryOutbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return dryOutbillGoodsMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询晾晒出库单货物
     *
     * @param ids 晾晒出库单货物 IDs
     * @return 晾晒出库单货物
     */
    public List<DryOutbillGoods> selectWmsDryOutbillGoodsByIds(String[] ids) {
        QueryWrapper<DryOutbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return dryOutbillGoodsMapper.selectList(queryWrapper);
    }

    /**
     * 查询晾晒出库单货物列表
     *
     * @param wmsDryOutbillGoods 晾晒出库单货物
     * @return 晾晒出库单货物集合
     */
    public List<DryOutbillGoods> selectWmsDryOutbillGoodsList(DryOutbillGoods wmsDryOutbillGoods) {
        QueryWrapper<DryOutbillGoods> queryWrapper = getQueryWrapper(wmsDryOutbillGoods);
        return dryOutbillGoodsMapper.select(queryWrapper);
    }

    /**
     * 新增晾晒出库单货物
     *
     * @param wmsDryOutbillGoods 晾晒出库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public DryOutbillGoods insertWmsDryOutbillGoods(DryOutbillGoods wmsDryOutbillGoods) {
        wmsDryOutbillGoods.setId(IdUtil.simpleUUID());
        wmsDryOutbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        dryOutbillGoodsMapper.insert(wmsDryOutbillGoods);
        return wmsDryOutbillGoods;
    }

    /**
     * 批量新增晾晒出库单详情信息
     *
     * @param dryOutbillGoodsList 入库单详情信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<DryOutbillGoods> dryOutbillGoodsList) {
        List<DryOutbillGoods> collect = dryOutbillGoodsList.stream().map(dryOutbillGoods -> {
            dryOutbillGoods.setId(IdUtil.simpleUUID());
            dryOutbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            return dryOutbillGoods;
        }).collect(Collectors.toList());
        return this.saveBatch(collect, collect.size());
    }

    /**
     * 修改晾晒出库单货物
     *
     * @param wmsDryOutbillGoods 晾晒出库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public DryOutbillGoods updateWmsDryOutbillGoods(DryOutbillGoods wmsDryOutbillGoods) {
        dryOutbillGoodsMapper.updateById(wmsDryOutbillGoods);
        return wmsDryOutbillGoods;
    }

    /**
     * 批量删除晾晒出库单货物
     *
     * @param ids 需要删除的晾晒出库单货物主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsDryOutbillGoodsByIds(String[] ids) {
        List<DryOutbillGoods> wmsDryOutbillGoodss = new ArrayList<>();
        for (String id : ids) {
            DryOutbillGoods wmsDryOutbillGoods = new DryOutbillGoods();
            wmsDryOutbillGoods.setId(id);
            wmsDryOutbillGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsDryOutbillGoodss.add(wmsDryOutbillGoods);
        }
        return super.updateBatchById(wmsDryOutbillGoodss) ? 1 : 0;
    }

    /**
     * 删除晾晒出库单货物信息
     *
     * @param id 晾晒出库单货物主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsDryOutbillGoodsById(String id) {
        DryOutbillGoods wmsDryOutbillGoods = new DryOutbillGoods();
        wmsDryOutbillGoods.setId(id);
        wmsDryOutbillGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return dryOutbillGoodsMapper.updateById(wmsDryOutbillGoods);
    }

    public QueryWrapper<DryOutbillGoods> getQueryWrapper(DryOutbillGoods wmsDryOutbillGoods) {
        QueryWrapper<DryOutbillGoods> queryWrapper = new QueryWrapper<>();
        if (wmsDryOutbillGoods != null) {
            wmsDryOutbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", wmsDryOutbillGoods.getDelFlag());
            //晾晒出库单号
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getDryOutbillCode())) {
                queryWrapper.eq("dry_outbill_code", wmsDryOutbillGoods.getDryOutbillCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getPartsCode())) {
                queryWrapper.eq("parts_code", wmsDryOutbillGoods.getPartsCode());
            }
            //货物编号
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getGoodsCode())) {
                queryWrapper.eq("goods_code", wmsDryOutbillGoods.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getGoodsName())) {
                queryWrapper.like("goods_name", wmsDryOutbillGoods.getGoodsName());
            }
            //计量单位
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getMeasureUnit())) {
                queryWrapper.eq("measure_unit", wmsDryOutbillGoods.getMeasureUnit());
            }
            //单位规格
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getModel())) {
                queryWrapper.eq("model", wmsDryOutbillGoods.getModel());
            }
            //出库数量
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getGoodsNum())) {
                queryWrapper.eq("dry_outbill_num", wmsDryOutbillGoods.getGoodsNum());
            }
            //库区编号
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getAreaCode())) {
                queryWrapper.eq("area_code", wmsDryOutbillGoods.getAreaCode());
            }
            //库区名称
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getAreaName())) {
                queryWrapper.like("area_name", wmsDryOutbillGoods.getAreaName());
            }
            //库位编号
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getLocationCode())) {
                queryWrapper.eq("location_code", wmsDryOutbillGoods.getLocationCode());
            }
            //库位名称
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getLocationName())) {
                queryWrapper.like("location_name", wmsDryOutbillGoods.getLocationName());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(wmsDryOutbillGoods.getTrayCode())) {
                queryWrapper.eq("tray_code", wmsDryOutbillGoods.getTrayCode());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param wmsDryOutbillGoodsList 模板数据
     * @param updateSupport          是否更新已经存在的数据
     * @param operName               操作人姓名
     * @return
     */
    public String importData(List<DryOutbillGoods> wmsDryOutbillGoodsList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsDryOutbillGoodsList) || wmsDryOutbillGoodsList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (DryOutbillGoods wmsDryOutbillGoods : wmsDryOutbillGoodsList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                DryOutbillGoods u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsDryOutbillGoods);
                    wmsDryOutbillGoods.setId(IdUtil.simpleUUID());
                    wmsDryOutbillGoods.setCreateBy(operName);
                    wmsDryOutbillGoods.setCreateTime(new Date());
                    wmsDryOutbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    dryOutbillGoodsMapper.insert(wmsDryOutbillGoods);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsDryOutbillGoods);
                    //todo 验证
                    //int count = wmsDryOutbillGoodsMapper.checkCode(wmsDryOutbillGoods);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    wmsDryOutbillGoods.setId(u.getId());
                    wmsDryOutbillGoods.setUpdateBy(operName);
                    wmsDryOutbillGoods.setUpdateTime(new Date());
                    dryOutbillGoodsMapper.updateById(wmsDryOutbillGoods);
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
     * 根据dry_outbill_code获取出库单货物信息
     *
     * @param dryOutbillCode
     * @return
     */
    public List<DryOutbillGoods> selectByCode(String dryOutbillCode) {
        return dryOutbillGoodsMapper.selectByCode(dryOutbillCode);
    }

    /**
     * 清除dry_outbill_code绑定
     *
     * @param wmsDryOutbill
     */
    public boolean deleteByCode(DryOutbill wmsDryOutbill) {
        return dryOutbillGoodsMapper.deleteByCode(wmsDryOutbill);
    }

    /**
     * 查询晾晒出库单详情列表
     *
     * @param dryOutbillGoods
     * @return
     */
    public List<DryOutbillGoods> selectGoodsDetailList(DryOutbillGoods dryOutbillGoods) {
        return dryOutbillGoodsMapper.selectGoodsDetailList(dryOutbillGoods);
    }


    /**
     * 开始晾晒任务
     *
     * @param outbill
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult startDryTask(DryOutbill outbill) {
        List<String> hasParentLocationCodes = new ArrayList<>();
        //晾晒出库单号
        String dryOutbillCode = outbill.getDryOutbillCode();
        LambdaQueryWrapper<Area> areaQueryWrapper = Wrappers.lambdaQuery();
        areaQueryWrapper.select(Area::getAreaCode)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Area::getAreaType, AreaTypeEnum.LSQ.getCode())
                .last("limit 1");
        Area area = areaMapper.selectOne(areaQueryWrapper);
        //结束库区(晾晒区)
        String endAreaCode = area.getAreaCode();
        QueryWrapper<DryOutbillGoods> dryQueryWrapper = Wrappers.query();
        dryQueryWrapper.select("distinct tray_code")
                .eq("dry_outbill_code", dryOutbillCode)
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("dry_outbill_status", DryOutbillStatusTypeEnum.WAIT.getCode());
        //要取的托盘
        List<String> trayCodeList = dryOutbillGoodsMapper.selectObjs(dryQueryWrapper).stream().map(String::valueOf).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(trayCodeList) && trayCodeList.size() > 0) {
            boolean noTask = WmsTaskConstant.TAKE_TRAY_TASK.compareAndSet(false, true);
            if (!noTask) {
                throw new ServiceException("晾晒出库操作正在进行中，请稍后再试");
            }
            //查询托盘是否可取用
            List<LocationMapVo> trayList = trayMapper.getTrayInfoInTrayCode(AreaTypeEnum.CCQ.getCode(), trayCodeList);
            if (trayCodeList.size() > trayList.size()) {//有不可取用的托盘
                List<String> noTrayCodeList = trayList.stream().map(tray -> (String) tray.getTrayCode()).collect(Collectors.toList());
                List<String> collect = trayCodeList.stream().filter(trayCode -> !noTrayCodeList.contains(trayCode)).collect(Collectors.toList());
                throw new ServiceException("以下托盘不可取：" + collect);
            }
            //获取agv是否启用
            LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
            wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, endAreaCode)
                    .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                    .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
            Iterator<String> iterator = null;//用来存储晾晒区空库位
            if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {//agv启用，锁定end库区库位
                //获取结束库区空闲库位
                List<String> locationCodeList = locationMapper.getDryAreaEmptyLocation(endAreaCode, trayCodeList.size())
                        .stream()
                        .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
                //结束库位的数量
                int size = locationCodeList.size();
                if (trayCodeList.size() != size) {
                    throw new ServiceException("晾晒区库位不足");
                } else {//锁定晾晒区库位
                    //锁定结束库区空闲库位
                    LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
                    locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                            .in(Location::getLocationCode, locationCodeList);
                    locationMapper.update(null, locationUpdate);
                    iterator = locationCodeList.iterator();
                }
            }
            //锁定存储区库位
            List<String> locationCodeList = trayList.stream().map(tray -> (String) tray.getLocationCode()).collect(Collectors.toList());
            LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
            locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                    .in(Location::getLocationCode, locationCodeList);
            locationMapper.update(null, locationUpdate);
            //库区编码列表
            List<String> areaCodeList = trayList.stream()
                    .map(tray -> (String) tray.getAreaCode()).distinct().collect(Collectors.toList());
            //库区个数
            int size = areaCodeList.size();
            //交叠组装数据，取盘
            List<WmsWcsInfo> infoList = new ArrayList<>();
            Map<Integer, WmsWcsInfo> infoMap = new LinkedHashMap<>();

            trayList = trayList.stream()
                    .sorted(Comparator.comparing(LocationMapVo::getLocationType)
                            .thenComparing(LocationMapVo::getOrderNum)
                            .thenComparing(LocationMapVo::getAreaCode)
                    ).collect(Collectors.toList());

            Map<String, List<LocationMapVo>> groupData = trayList.stream()
                    .collect(Collectors.groupingBy(LocationMapVo::getAreaCode, Collectors.toList()))
                    ;
            Map<String, List<LocationMapVo>> treepGroupData = new TreeMap<>(groupData);

            int maxSize = treepGroupData.entrySet()
                    .stream()
                    .max(Comparator.comparingInt(entry -> entry.getValue().size()))
                    .orElse(null).getValue().size();

            List<LocationMapVo> newTrayList = new ArrayList<>();

            int curIndex = 0;
            while (true){
                for (String areaCode : treepGroupData.keySet()) {
                    List<LocationMapVo> locationMapVos = treepGroupData.get(areaCode);
                    if(curIndex >= locationMapVos.size()){
                        continue;
                    }
                    newTrayList.add(locationMapVos.get(curIndex));
                }
                curIndex ++ ;
                if(curIndex >= maxSize){
                    break;
                }
            }

            for (int i = 0; i < newTrayList.size(); i++) {
                LocationMapVo tray = newTrayList.get(i);
                if (tray.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode())) {
                    WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode());
                    info.put(WmsWcsInfo.TRAY_CODE, (String) tray.getTrayCode());
                    info.put(WmsWcsInfo.START_LOCATION_CODE, (String) tray.getLocationCode());
                    info.put(WmsWcsInfo.START_AREA_CODE, (String) tray.getAreaCode());
                    info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                    if (iterator != null) {
                        info.put(WmsWcsInfo.END_LOCATION_CODE, iterator.next());
                    } else {
                        // String endLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(endAreaCode, LocationArrowEnum.LEFT_ARROW.getCode());
                        // info.put(WmsWcsInfo.END_LOCATION_CODE,endLocationCode);
                        info.put(WmsWcsInfo.END_LOCATION_CODE, "csd");
                    }
                    info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LSQ.getCode());
                    info.put(WmsWcsInfo.DOC, dryOutbillCode);
                    infoMap.put(i, info);
                    hasParentLocationCodes.add(tray.getLocationCode());
                } else {
                    Location childLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("enable_status", EnableStatus.ENABLE.getCode())
                            .eq("location_code", tray.getLocationCode())
                    );
                    Location parentLocation = locationService.getParentLocation(childLocation, tray.getAreaCode());

                    //如何hasParentLocationCodes包含母库位，则表示子母库位在本次任务一起，则正常进行取盘，反之移库取盘
                    if (hasParentLocationCodes.contains(parentLocation.getLocationCode()) || parentLocation.getTrayCode() == null) {
                        WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode());
                        info.put(WmsWcsInfo.TRAY_CODE, (String) tray.getTrayCode());
                        info.put(WmsWcsInfo.START_LOCATION_CODE, (String) tray.getLocationCode());
                        info.put(WmsWcsInfo.START_AREA_CODE, (String) tray.getAreaCode());
                        info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                        if (iterator != null) {
                            info.put(WmsWcsInfo.END_LOCATION_CODE, iterator.next());
                        } else {
                            // String endLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(endAreaCode, LocationArrowEnum.LEFT_ARROW.getCode());
                            // info.put(WmsWcsInfo.END_LOCATION_CODE,endLocationCode);
                            info.put(WmsWcsInfo.END_LOCATION_CODE, "csd");
                        }
                        info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LSQ.getCode());
                        info.put(WmsWcsInfo.DOC, dryOutbillCode);
                        infoMap.put(i, info);
                    } else {
                        String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(tray.getAreaCode());

                        List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                        WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        info.put(WmsWcsInfo.TRAY_CODE, childLocation.getTrayCode());
                        info.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                        info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                        info.put(WmsWcsInfo.DOC, dryOutbillCode);

                        //1.母库位移动至移库库位
                        WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                        childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentLocation.getLocationCode());
                        childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                        childInfo.put(WmsWcsInfo.START_AREA_CODE,parentLocation.getAreaId());
                        childInfo.put(WmsWcsInfo.END_AREA_CODE,parentLocation.getAreaId());
                        childInfo.put(WmsWcsInfo.DOC, dryOutbillCode);
                        childInfoList.add(childInfo);

                        //2.晾晒出库具体任务
                        WmsWcsInfo moveInfo = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode());
                        moveInfo.put(WmsWcsInfo.TRAY_CODE, (String) tray.getTrayCode());
                        moveInfo.put(WmsWcsInfo.START_LOCATION_CODE, (String) tray.getLocationCode());
                        moveInfo.put(WmsWcsInfo.START_AREA_CODE, (String) tray.getAreaCode());
                        moveInfo.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                        if (iterator != null) {
                            moveInfo.put(WmsWcsInfo.END_LOCATION_CODE, iterator.next());
                        } else {
                            // String endLocationCode = wmsTransferLocationService.getTransferLocationCodeByArea(endAreaCode, LocationArrowEnum.LEFT_ARROW.getCode());
                            // moveInfo.put(WmsWcsInfo.END_LOCATION_CODE,endLocationCode);
                            moveInfo.put(WmsWcsInfo.END_LOCATION_CODE, "csd");
                        }
                        moveInfo.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LSQ.getCode());
                        moveInfo.put(WmsWcsInfo.DOC, dryOutbillCode);
                        childInfoList.add(moveInfo);

                        //移库库位回至母库位
                        WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo3.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                        childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                        childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, parentLocation.getLocationCode());
                        childInfo3.put(WmsWcsInfo.START_AREA_CODE,parentLocation.getAreaId());
                        childInfo3.put(WmsWcsInfo.END_AREA_CODE,parentLocation.getAreaId());
                        childInfo3.put(WmsWcsInfo.DOC, dryOutbillCode);
                        childInfoList.add(childInfo3);

                        info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
                        infoMap.put(i, info);
                    }
                }
            }
            Set<Integer> integers = infoMap.keySet();
            List<Integer> collect = integers.stream().collect(Collectors.toList());
            for (Integer integer : collect) {
                infoList.add(infoMap.get(integer));
            }
            //记录给wcs任务的数据
            tasklogService.saveBatch(infoList);
            //把任务的数据给wcs
            List<WmsToWcsTaskReq> collect2 = infoList.stream().map(info -> {
                String jsonStr = JSONObject.toJSONString(info);
                return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
            }).collect(Collectors.toList());
            try {
                new Thread(() -> {
                    wcsTaskApiService.agvLinkAgeStacker(collect2);
                    WmsTaskConstant.TAKE_TRAY_TASK.compareAndSet(true, false);
                }).start();
            } catch (Exception e) {
                WmsTaskConstant.TAKE_TRAY_TASK.compareAndSet(true, false);
            }
            //修改晾晒抬头、详情状态为出库中
            LambdaUpdateWrapper<DryOutbill> dryOutbillUpdate = Wrappers.lambdaUpdate();
            dryOutbillUpdate.set(DryOutbill::getDryOutbillStatus, DryOutbillStatusTypeEnum.IN.getCode())
                    .eq(DryOutbill::getDryOutbillCode, dryOutbillCode);
            dryOutbillMapper.update(null, dryOutbillUpdate);
            LambdaUpdateWrapper<DryOutbillGoods> dryOutbillGoodsUpdate = Wrappers.lambdaUpdate();
            dryOutbillGoodsUpdate.set(DryOutbillGoods::getDryOutbillStatus, DryOutbillStatusTypeEnum.IN.getCode())
                    .eq(DryOutbillGoods::getDryOutbillCode, dryOutbillCode);
            dryOutbillGoodsMapper.update(null, dryOutbillGoodsUpdate);
        } else {
            throw new ServiceException("请维护晾晒单任务详情数据");
        }
        return AjaxResult.success("成功");
    }


    /**
     * 晾晒出库结束
     *
     * @param wmsWcsInfo
     */
    @Transactional(rollbackFor = Exception.class)
    public void dryingOutEnd(WmsWcsInfo wmsWcsInfo) {
        String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编号
        String doc = (String) wmsWcsInfo.get(WmsWcsInfo.DOC);//晾晒单号
        String startLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.START_LOCATION_CODE);//start库位
        //解锁库存
        LambdaUpdateWrapper<Tblstock> tblstockUpdate = Wrappers.lambdaUpdate();
        tblstockUpdate.set(Tblstock::getUpdateTime, new Date())
                .set(Tblstock::getLockStatus, LockEnum.NOTLOCK.getCode())
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Tblstock::getTrayCode, trayCode);
        tblstockMapper.update(null, tblstockUpdate);
        //修改晾晒详情任务已完成
        LambdaUpdateWrapper<DryOutbillGoods> dryOutbillGoodsUpdateWrapper = Wrappers.lambdaUpdate();
        dryOutbillGoodsUpdateWrapper.set(DryOutbillGoods::getDryOutbillStatus, DryOutbillStatusTypeEnum.END.getCode())
                .eq(DryOutbillGoods::getTrayCode, trayCode)
                .eq(DryOutbillGoods::getDryOutbillCode, doc);
        dryOutbillGoodsMapper.update(null, dryOutbillGoodsUpdateWrapper);
        //根据详情完成情况修改抬头晾晒状态
        LambdaQueryWrapper<DryOutbillGoods> dryOutbillGoodsQueryWrapper = Wrappers.lambdaQuery();
        dryOutbillGoodsQueryWrapper.eq(DryOutbillGoods::getDryOutbillCode, doc)
                .ne(DryOutbillGoods::getDryOutbillStatus, DryOutbillStatusTypeEnum.END.getCode())
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        if (dryOutbillGoodsMapper.selectCount(dryOutbillGoodsQueryWrapper) == 0) {//修改晾晒抬头为已完成
            LambdaUpdateWrapper<DryOutbill> dryOutbillUpdateWrapper = Wrappers.lambdaUpdate();
            dryOutbillUpdateWrapper.set(DryOutbill::getDryOutbillStatus, DryOutbillStatusTypeEnum.END.getCode())
                    .set(DryOutbill::getDryOutbillTime, new Date())
                    .eq(DryOutbill::getDryOutbillCode, doc);
            dryOutbillMapper.update(null, dryOutbillUpdateWrapper);
        }
    }


}
