package com.ruoyi.wms.move.service;

import cn.hutool.core.collection.CollectionUtil;
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
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.Warehouse;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.basics.mapper.WarehouseMapper;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.basics.vo.LocationMapVo;
import com.ruoyi.wms.enums.AreaTypeEnum;
import com.ruoyi.wms.enums.LocationTypeEnum;
import com.ruoyi.wms.enums.ManMadeEnum;
import com.ruoyi.wms.enums.MoveEnum;
import com.ruoyi.wms.move.domain.WmsMoveDetail;
import com.ruoyi.wms.move.domain.WmsMoveList;
import com.ruoyi.wms.move.mapper.WmsMoveDetailMapper;
import com.ruoyi.wms.move.mapper.WmsMoveListMapper;
import com.ruoyi.wms.move.vo.WmsMoveDetailVo;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.wcstask.service.TasklogService;
import com.ruoyi.wms.wcstask.service.WaittaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 移库单详情Service接口
 *
 * @author nf
 * @date 2023-03-01
 */
@Slf4j
@Service
public class WmsMoveDetailService extends ServiceImpl<WmsMoveDetailMapper, WmsMoveDetail> {

    @Autowired
    private WmsMoveDetailMapper wmsMoveDetailMapper;

    @Autowired
    private WmsMoveListMapper wmsMoveListMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private TrayMapper trayMapper;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private TblstockMapper tblstockMapper;

    @Autowired
    protected Validator validator;

    @Autowired
    private TasklogService tasklogService;

    @Autowired
    private WaittaskService waittaskService;

    @Autowired(required = false)
    private WcsTaskApiService wcsTaskApiService;

    @Autowired(required = false)
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;
    /**
     * 查询移库单详情
     *
     * @param id 移库单详情主键
     * @return 移库单详情
     */
    public WmsMoveDetail selectWmsMoveDetailById(String id) {
        QueryWrapper<WmsMoveDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsMoveDetailMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询移库单详情
     *
     * @param ids 移库单详情 IDs
     * @return 移库单详情
     */
    public List<WmsMoveDetail> selectWmsMoveDetailByIds(String[] ids) {
        QueryWrapper<WmsMoveDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsMoveDetailMapper.selectList(queryWrapper);
    }

    /**
     * 查询移库单详情列表
     *
     * @param wmsMoveDetail 移库单详情
     * @return 移库单详情集合
     */
    public List<WmsMoveDetail> selectWmsMoveDetailList(WmsMoveDetail wmsMoveDetail) {
        QueryWrapper<WmsMoveDetail> queryWrapper = getQueryWrapper(wmsMoveDetail);
        return wmsMoveDetailMapper.select(queryWrapper);
    }

    /**
     * 新增移库单详情
     *
     * @param wmsMoveDetail 移库单详情
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsMoveDetail insertWmsMoveDetail(WmsMoveDetail wmsMoveDetail) {
        wmsMoveDetail.setId(IdUtil.simpleUUID());
        wmsMoveDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsMoveDetailMapper.insert(wmsMoveDetail);
        return wmsMoveDetail;
    }

    /**
     * 修改移库单详情
     *
     * @param wmsMoveDetail 移库单详情
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsMoveDetail updateWmsMoveDetail(WmsMoveDetail wmsMoveDetail) {
        wmsMoveDetailMapper.updateById(wmsMoveDetail);
        return wmsMoveDetail;
    }

    /**
     * 批量删除移库单详情
     *
     * @param ids 需要删除的移库单详情主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsMoveDetailByIds(String[] ids) {
        List<WmsMoveDetail> wmsMoveDetails = new ArrayList<>();
        for (String id : ids) {
            WmsMoveDetail wmsMoveDetail = new WmsMoveDetail();
            wmsMoveDetail.setId(id);
            wmsMoveDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsMoveDetails.add(wmsMoveDetail);
        }
        List<WmsMoveDetail> oldDetail = selectWmsMoveDetailByIds(ids);
        List<String> locations = new ArrayList<>();
        for (WmsMoveDetail detail : oldDetail) {
            locations.add(detail.getOutLocationCode());
            locations.add(detail.getInLocationCode());
        }
        if (locations.size() > 0) {
            LambdaUpdateWrapper<Location> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                    .eq(Location::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .in(Location::getLocationCode, locations);
            locationService.getBaseMapper().update(null, updateWrapper);
            //根据库位解锁库存数据
            LambdaUpdateWrapper<Tblstock> updateWrapper1=Wrappers.lambdaUpdate();
            updateWrapper1.set(Tblstock::getLockStatus,LockEnum.NOTLOCK.getCode())
                    .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(Tblstock::getLocationCode,locations);
            tblstockMapper.update(null,updateWrapper1);
        }
        return super.updateBatchById(wmsMoveDetails) ? 1 : 0;
    }

    /**
     * 删除移库单详情信息
     *
     * @param id 移库单详情主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsMoveDetailById(String id) {
        WmsMoveDetail wmsMoveDetail = new WmsMoveDetail();
        wmsMoveDetail.setId(id);
        wmsMoveDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsMoveDetailMapper.updateById(wmsMoveDetail);
    }

    public QueryWrapper<WmsMoveDetail> getQueryWrapper(WmsMoveDetail wmsMoveDetail) {
        QueryWrapper<WmsMoveDetail> queryWrapper = new QueryWrapper<>();
        if (wmsMoveDetail != null) {
            wmsMoveDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", wmsMoveDetail.getDelFlag());
            //移库单号
            if (StrUtil.isNotEmpty(wmsMoveDetail.getMoveCode())) {
                queryWrapper.like("move_code", wmsMoveDetail.getMoveCode());
            }
            //移库状态
            if (StrUtil.isNotEmpty(wmsMoveDetail.getMoveStatus())) {
                queryWrapper.eq("move_status", wmsMoveDetail.getMoveStatus());
            }
            //移出仓库编号
            if (StrUtil.isNotEmpty(wmsMoveDetail.getOutWarehouseCode())) {
                queryWrapper.like("out_warehouse_code", wmsMoveDetail.getOutWarehouseCode());
            }
            //移出库区编号
            if (StrUtil.isNotEmpty(wmsMoveDetail.getOutAreaCode())) {
                queryWrapper.like("out_area_code", wmsMoveDetail.getOutAreaCode());
            }
            //移出库位编号
            if (StrUtil.isNotEmpty(wmsMoveDetail.getOutLocationCode())) {
                queryWrapper.like("out_location_code", wmsMoveDetail.getOutLocationCode());
            }
            //移入仓库编号
            if (StrUtil.isNotEmpty(wmsMoveDetail.getInWarehouseCode())) {
                queryWrapper.like("in_warehouse_code", wmsMoveDetail.getInWarehouseCode());
            }
            //移入库区编号
            if (StrUtil.isNotEmpty(wmsMoveDetail.getInAreaCode())) {
                queryWrapper.like("in_area_code", wmsMoveDetail.getInAreaCode());
            }
            //移入库位编号
            if (StrUtil.isNotEmpty(wmsMoveDetail.getInLocationCode())) {
                queryWrapper.like("in_location_code", wmsMoveDetail.getInLocationCode());
            }
            //临时数据过滤（删除）
            if(CollectionUtil.isNotEmpty(wmsMoveDetail.getIds())){
                queryWrapper.notIn("id",wmsMoveDetail.getIds());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param wmsMoveDetailList 模板数据
     * @param updateSupport     是否更新已经存在的数据
     * @param operName          操作人姓名
     * @return
     */
    public String importData(List<WmsMoveDetail> wmsMoveDetailList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsMoveDetailList) || wmsMoveDetailList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WmsMoveDetail wmsMoveDetail : wmsMoveDetailList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                WmsMoveDetail u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsMoveDetail);
                    wmsMoveDetail.setId(IdUtil.simpleUUID());
                    wmsMoveDetail.setCreateBy(operName);
                    wmsMoveDetail.setCreateTime(new Date());
                    wmsMoveDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsMoveDetailMapper.insert(wmsMoveDetail);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsMoveDetail);
                    //todo 验证
                    //int count = wmsMoveDetailMapper.checkCode(wmsMoveDetail);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    wmsMoveDetail.setId(u.getId());
                    wmsMoveDetail.setUpdateBy(operName);
                    wmsMoveDetail.setUpdateTime(new Date());
                    wmsMoveDetailMapper.updateById(wmsMoveDetail);
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
     * PDA-获取移库任务
     *
     * @param wmsMoveDetail 移库单详情
     * @return 移库单详情集合
     */
    public List<WmsMoveDetail> selectMoveTask(WmsMoveDetail wmsMoveDetail) {
        LambdaQueryWrapper<WmsMoveDetail> wmsMoveDetailQueryWrapper = Wrappers.lambdaQuery();
        wmsMoveDetailQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WmsMoveDetail::getMoveStatus, MoveEnum.executing.getCode())
                .last("and out_area_code <> in_area_code");
        return wmsMoveDetailMapper.selectList(wmsMoveDetailQueryWrapper);
    }

    /**
     * pda-人工移库回盘
     *
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult manMadeMove(WmsMoveDetailVo map) {
        String id = map.getId();//移库详情主键
        String startAreaCode = map.getStartAreaCode();//起始库区编码
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(startAreaCode)) {
            throw new ServiceException("参数不全");
        }
        LambdaQueryWrapper<WmsMoveDetail> wmsMoveDetailQueryWrapper = Wrappers.lambdaQuery();
        wmsMoveDetailQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WmsMoveDetail::getManMadeStatus, ManMadeEnum.NOT.getCode())
                .eq(WmsMoveDetail::getMoveStatus, MoveEnum.executing.getCode())
                .eq(WmsMoveDetail::getId, id);
        //入库详情
        WmsMoveDetail wmsMoveDetail = wmsMoveDetailMapper.selectOne(wmsMoveDetailQueryWrapper);
        if (StringUtils.isNull(wmsMoveDetail)) {
            throw new ServiceException("移库任务找不到");
        }
        //组装回盘任务数据
        List<WmsWcsInfo> infoList = new ArrayList<>();

        WmsWcsInfo allInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
        allInfo.put(WmsWcsInfo.START_AREA_CODE,startAreaCode);
        allInfo.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
        allInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
        List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
        allInfo.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
        String inTrayCode = wmsMoveDetail.getInTrayCode();
        Location outLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", wmsMoveDetail.getOutLocationCode()));
        Location inLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", wmsMoveDetail.getInLocationCode()));

        //两个托盘移库，通过移库库位移库
        if (StringUtils.isNotEmpty(inTrayCode)) {
            if (outLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())) {
                Location parentOutLocation = locationService.getParentLocation(outLocation, outLocation.getAreaId());
                if (StrUtil.isNotEmpty(parentOutLocation.getTrayCode())) {
                    String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                    WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo.put(WmsWcsInfo.TRAY_CODE, parentOutLocation.getTrayCode());
                    childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentOutLocation.getLocationCode());
                    childInfo.put(WmsWcsInfo.END_LOCATION_CODE, backMoveLocationCode);
                    childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                    childInfoList.add(childInfo);
                }
            }

            WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
            info.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getInTrayCode());
            info.put(WmsWcsInfo.START_AREA_CODE, startAreaCode);
            info.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getOutAreaCode());
            info.put(WmsWcsInfo.END_LOCATION_CODE, wmsMoveDetail.getOutLocationCode());
            info.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
            info.put(WmsWcsInfo.SERVICE_ID, wmsMoveDetail.getId());
            info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
            childInfoList.add(info);

            if (outLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())) {
                Location parentOutLocation = locationService.getParentLocation(outLocation, outLocation.getAreaId());
                if (StrUtil.isNotEmpty(parentOutLocation.getTrayCode())) {
                    String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                    WmsWcsInfo childInfo2 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo2.put(WmsWcsInfo.TRAY_CODE, parentOutLocation.getTrayCode());
                    childInfo2.put(WmsWcsInfo.START_LOCATION_CODE, backMoveLocationCode);
                    childInfo2.put(WmsWcsInfo.END_LOCATION_CODE, parentOutLocation.getLocationCode());
                    childInfo2.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                    childInfoList.add(childInfo2);
                }
            }

            infoList.add(allInfo);


            WmsWcsInfo allInfo1 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
            allInfo1.put(WmsWcsInfo.START_AREA_CODE, startAreaCode);
            allInfo1.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
            allInfo1.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getOutAreaCode());
            List<WmsWcsInfo> childInfoList1 = new ArrayList<>();//子任务list
            allInfo1.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList1);

            if (StringUtils.isNotEmpty(wmsMoveDetail.getInTrayCode())) {

                if (inLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())) {
                    Location parentInLocation = locationService.getParentLocation(inLocation, inLocation.getAreaId());
                    if (StrUtil.isNotEmpty(parentInLocation.getTrayCode())) {
                        String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getInAreaCode());
                        WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                        childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentInLocation.getLocationCode());
                        childInfo.put(WmsWcsInfo.END_LOCATION_CODE, backMoveLocationCode);
                        childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                        childInfoList1.add(childInfo);
                    }
                }


                WmsWcsInfo info2 = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                info2.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getOutTrayCode());
                info2.put(WmsWcsInfo.START_AREA_CODE, startAreaCode);
                info2.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                info2.put(WmsWcsInfo.END_LOCATION_CODE, wmsMoveDetail.getInLocationCode());
                info2.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                info2.put(WmsWcsInfo.SERVICE_ID, wmsMoveDetail.getId());
                info2.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
                childInfoList1.add(info2);

                if (inLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())) {
                    Location parentInLocation = locationService.getParentLocation(inLocation, inLocation.getAreaId());
                    if (StrUtil.isNotEmpty(parentInLocation.getTrayCode())) {
                        String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getInAreaCode());
                        WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                        childInfo.put(WmsWcsInfo.START_LOCATION_CODE, backMoveLocationCode);
                        childInfo.put(WmsWcsInfo.END_LOCATION_CODE, parentInLocation.getLocationCode());
                        childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                        childInfoList1.add(childInfo);
                    }
                }
                infoList.add(allInfo1);
            }

        }else{
            //info.put(WmsWcsInfo.DOC, wmsMoveDetail.getId());

            WmsWcsInfo allInfo1 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
            allInfo1.put(WmsWcsInfo.START_AREA_CODE, startAreaCode);
            allInfo1.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
            allInfo1.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getOutAreaCode());
            List<WmsWcsInfo> childInfoList1 = new ArrayList<>();//子任务list
            allInfo1.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList1);

            if (inLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())) {
                Location parentInLocation = locationService.getParentLocation(inLocation, inLocation.getAreaId());
                if (StrUtil.isNotEmpty(parentInLocation.getTrayCode())) {
                    String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getInAreaCode());
                    WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                    childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentInLocation.getLocationCode());
                    childInfo.put(WmsWcsInfo.END_LOCATION_CODE, backMoveLocationCode);
                    childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                    childInfoList1.add(childInfo);
                }
            }


            WmsWcsInfo info2 = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
            info2.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getOutTrayCode());
            info2.put(WmsWcsInfo.START_AREA_CODE, startAreaCode);
            info2.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
            info2.put(WmsWcsInfo.END_LOCATION_CODE, wmsMoveDetail.getInLocationCode());
            info2.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
            info2.put(WmsWcsInfo.SERVICE_ID, wmsMoveDetail.getId());
            info2.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
            childInfoList1.add(info2);

            if (inLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())) {
                Location parentInLocation = locationService.getParentLocation(inLocation, inLocation.getAreaId());
                if (StrUtil.isNotEmpty(parentInLocation.getTrayCode())) {
                    String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getInAreaCode());
                    WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                    childInfo.put(WmsWcsInfo.START_LOCATION_CODE, backMoveLocationCode);
                    childInfo.put(WmsWcsInfo.END_LOCATION_CODE, parentInLocation.getLocationCode());
                    childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                    childInfoList1.add(childInfo);
                }
            }

            infoList.add(allInfo1);
        }
        //保存任务日志
        tasklogService.saveBatch(infoList);
        //把任务的数据给wcs
        List<WmsToWcsTaskReq> collect = infoList.stream().map(info2 -> {
            String jsonStr = JSONObject.toJSONString(info2);
            return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
        }).collect(Collectors.toList());
        wcsTaskApiService.agvLinkAgeStacker(collect);
        //修改人工操作为已执行
        WmsMoveDetail detailUpdate = new WmsMoveDetail();
        detailUpdate.setManMadeStatus(ManMadeEnum.ALREADY.getCode());
        detailUpdate.setId(id);
        wmsMoveDetailMapper.updateById(detailUpdate);
        return AjaxResult.success("成功");
    }

    /**
     * agv启用时，创建回盘任务
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult putTray(WmsMoveDetailVo map) {
        String id = map.getId();//移库详情主键
        String startAreaCode = map.getStartAreaCode();//起始库区编码
        LambdaQueryWrapper<WmsMoveDetail> wmsMoveDetailQueryWrapper = Wrappers.lambdaQuery();
        wmsMoveDetailQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WmsMoveDetail::getManMadeStatus, ManMadeEnum.NOT.getCode())
                .eq(WmsMoveDetail::getMoveStatus, MoveEnum.executing.getCode())
                .eq(WmsMoveDetail::getId, id);
        //入库详情
        WmsMoveDetail wmsMoveDetail = wmsMoveDetailMapper.selectOne(wmsMoveDetailQueryWrapper);
        if (StringUtils.isNull(wmsMoveDetail)) {
            throw new RuntimeException("创建回盘任务失败，移库任务异常");
        }
        //获取托盘所在库位
        List<String> trayList = new ArrayList<>();
        trayList.add(wmsMoveDetail.getOutTrayCode());
        if (StringUtils.isNotEmpty(wmsMoveDetail.getInTrayCode())) {
            trayList.add(wmsMoveDetail.getInTrayCode());
        }
        List<LocationMapVo> trayInfoInCode = trayMapper.getTrayInfoInCode(startAreaCode, trayList);
        //组装回盘任务数据
        List<WmsWcsInfo> infoList = new ArrayList<>();
        WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
        info.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getOutTrayCode());
        info.put(WmsWcsInfo.START_AREA_CODE, startAreaCode);
        List<LocationMapVo> trayCode = trayInfoInCode.stream()
                .filter(trayInfo -> wmsMoveDetail.getOutTrayCode().equals((String) trayInfo.getTrayCode())).collect(Collectors.toList());
        info.put(WmsWcsInfo.START_LOCATION_CODE, trayCode.get(0).getLocationCode());
        info.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
        info.put(WmsWcsInfo.END_LOCATION_CODE, wmsMoveDetail.getInLocationCode());
        info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
        infoList.add(info);
        if (StringUtils.isNotEmpty(wmsMoveDetail.getInTrayCode())) {
            WmsWcsInfo info2 = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
            info2.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getInTrayCode());
            info2.put(WmsWcsInfo.START_AREA_CODE, startAreaCode);
            List<LocationMapVo> trayCode2 = trayInfoInCode.stream()
                    .filter(trayInfo -> wmsMoveDetail.getInTrayCode().equals((String) trayInfo.getTrayCode())).collect(Collectors.toList());
            info2.put(WmsWcsInfo.START_LOCATION_CODE, trayCode2.get(0).getLocationCode());
            info2.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getOutAreaCode());
            info2.put(WmsWcsInfo.END_LOCATION_CODE, wmsMoveDetail.getOutLocationCode());
            info2.put(WmsWcsInfo.DOC, wmsMoveDetail.getId());
            info2.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
            infoList.add(info2);
        }else{
            info.put(WmsWcsInfo.DOC, wmsMoveDetail.getId());
        }
        //保存任务日志
        tasklogService.saveBatch(infoList);
        //把任务的数据给wcs
        List<WmsToWcsTaskReq> collect = infoList.stream().map(info2 -> {
            String jsonStr = JSONObject.toJSONString(info2);
            return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
        }).collect(Collectors.toList());
        wcsTaskApiService.agvLinkAgeStacker(collect);
        //修改人工操作为已执行
        WmsMoveDetail detailUpdate = new WmsMoveDetail();
        detailUpdate.setManMadeStatus(ManMadeEnum.ALREADY.getCode());
        detailUpdate.setId(id);
        wmsMoveDetailMapper.updateById(detailUpdate);
        return AjaxResult.success("成功");
    }

    /**
     * 开始移库任务
     *
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult startMoveTake(WmsMoveDetailVo map) {
        //移库单号
        String moveCode = map.getMoveCode();
        LambdaQueryWrapper<Area> areaQueryWrapper = Wrappers.lambdaQuery();
        areaQueryWrapper.select(Area::getAreaCode)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Area::getAreaType, AreaTypeEnum.LHQ.getCode())
                .last("limit 1");
        Area area = areaMapper.selectOne(areaQueryWrapper);
        //结束库区(理货区)
        String endAreaCode = area.getAreaCode();
        LambdaQueryWrapper<WmsMoveDetail> wmsMoveDetailQueryWrapper = Wrappers.lambdaQuery();
        wmsMoveDetailQueryWrapper.eq(WmsMoveDetail::getMoveCode, moveCode)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WmsMoveDetail::getMoveStatus, MoveEnum.unexecuted.getCode());
        //移库任务详情
        List<WmsMoveDetail> wmsMoveDetailList = wmsMoveDetailMapper.selectList(wmsMoveDetailQueryWrapper);
        //组装取盘任务数据
        List<WmsWcsInfo> infoList = new ArrayList<>();//取盘任务
        List<WmsWcsInfo> stackerInfoList = new ArrayList<>();//堆垛机移库任务

        //获取理货区agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, "LHQ01")
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        int tallyAreaCount = wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery).intValue();
        for (WmsMoveDetail wmsMoveDetail : wmsMoveDetailList) {
            if (!wmsMoveDetail.getOutAreaCode().equals(wmsMoveDetail.getInAreaCode())) {
                if (tallyAreaCount > 0) {
                    throw new ServiceException("理货区AGV启用，不能进行不同巷道的移库操作");
                }
            }
            if(StrUtil.isEmpty(wmsMoveDetail.getOutTrayCode())){
                throw new ServiceException(wmsMoveDetail.getOutLocationCode()+"移库库位不可没有托盘");
            }
        }

        for (WmsMoveDetail wmsMoveDetail : wmsMoveDetailList) {
            if (wmsMoveDetail.getOutAreaCode().equals(wmsMoveDetail.getInAreaCode())) {//相同巷道，操作堆垛机移货
                WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                info.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                info.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                info.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
                String inTrayCode = wmsMoveDetail.getInTrayCode();
                //两个托盘移库，通过移库库位移库
                if (StringUtils.isNotEmpty(inTrayCode)) {
                    //获取移库库位
                    String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                    if (StringUtils.isNotEmpty(moveLocationCode)) {//由堆垛机交换库位

                        Location outLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", wmsMoveDetail.getOutLocationCode()));
                        Location inLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", wmsMoveDetail.getInLocationCode()));

                        if(outLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                            Location parentOutLocation = locationService.getParentLocation(outLocation,outLocation.getAreaId());
                            if(StrUtil.isNotEmpty(parentOutLocation.getTrayCode())){
                                String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                                //将移出库位的母库位  搬运到 备份移库库位
                                WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                childInfo.put(WmsWcsInfo.TRAY_CODE, parentOutLocation.getTrayCode());
                                childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentOutLocation.getLocationCode());
                                childInfo.put(WmsWcsInfo.END_LOCATION_CODE, backMoveLocationCode);
                                childInfo.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                                childInfo.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                                childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                                childInfoList.add(childInfo);
                            }
                        }


                        //将移出库位搬运到 移库库位
                        WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getOutTrayCode());
                        childInfo.put(WmsWcsInfo.START_LOCATION_CODE, wmsMoveDetail.getOutLocationCode());
                        childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                        childInfo.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                        childInfo.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                        childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                        childInfoList.add(childInfo);

                        if(inLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                            Location parentInLocation = locationService.getParentLocation(inLocation,outLocation.getAreaId());
                            if(StrUtil.isNotEmpty(parentInLocation.getTrayCode())){
                                String back2MoveLocationCode = areaMapper.selectBackUp2MoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                                //将移入库位的母库位  搬运到 备份2移库库位
                                WmsWcsInfo parentInLocationChild = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                parentInLocationChild.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                                parentInLocationChild.put(WmsWcsInfo.START_LOCATION_CODE, parentInLocation.getLocationCode());
                                parentInLocationChild.put(WmsWcsInfo.END_LOCATION_CODE, back2MoveLocationCode);
                                parentInLocationChild.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                                parentInLocationChild.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                                parentInLocationChild.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                                childInfoList.add(parentInLocationChild);
                            }
                        }

                        WmsWcsInfo childInfo2 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo2.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getInTrayCode());
                        childInfo2.put(WmsWcsInfo.START_LOCATION_CODE, wmsMoveDetail.getInLocationCode());
                        childInfo2.put(WmsWcsInfo.END_LOCATION_CODE, wmsMoveDetail.getOutLocationCode());
                        childInfo2.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                        childInfo2.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                        childInfo2.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                        childInfoList.add(childInfo2);

                        WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo3.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getOutTrayCode());
                        childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                        childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, wmsMoveDetail.getInLocationCode());
                        childInfo3.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                        childInfo3.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                        childInfo3.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                        childInfo3.put(WmsWcsInfo.SERVICE_ID, wmsMoveDetail.getId());
                        childInfoList.add(childInfo3);

                        if(outLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                            Location parentOutLocation = locationService.getParentLocation(outLocation,outLocation.getAreaId());
                            if(StrUtil.isNotEmpty(parentOutLocation.getTrayCode())){
                                String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                                //将备份2移库库位 回到移出库位的母库位
                                WmsWcsInfo parentInLocationChild = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                parentInLocationChild.put(WmsWcsInfo.TRAY_CODE, parentOutLocation.getTrayCode());
                                parentInLocationChild.put(WmsWcsInfo.START_LOCATION_CODE, backMoveLocationCode);
                                parentInLocationChild.put(WmsWcsInfo.END_LOCATION_CODE, parentOutLocation.getLocationCode());
                                parentInLocationChild.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                                parentInLocationChild.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                                parentInLocationChild.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                                childInfoList.add(parentInLocationChild);
                            }
                        }

                        if(inLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                            Location parentInLocation = locationService.getParentLocation(inLocation,outLocation.getAreaId());
                            if(StrUtil.isNotEmpty(parentInLocation.getTrayCode())){
                                String back2MoveLocationCode = areaMapper.selectBackUp2MoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                                //将备份2移库库位 回到移入库位的母库位
                                WmsWcsInfo parentInLocationChild = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                parentInLocationChild.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                                parentInLocationChild.put(WmsWcsInfo.START_LOCATION_CODE, back2MoveLocationCode);
                                parentInLocationChild.put(WmsWcsInfo.END_LOCATION_CODE, parentInLocation.getLocationCode());
                                parentInLocationChild.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                                parentInLocationChild.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                                parentInLocationChild.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                                childInfoList.add(parentInLocationChild);
                            }
                        }
                        stackerInfoList.add(info);
                    } else {
                        //将托盘取到理货区
                        //addWmsWcsInfo(infoList, wmsMoveDetail, endAreaCode);
                        throw new ServiceException(moveLocationCode+"库区,请配置移库库位");
                    }
                } else {//一个托盘移库，直接将托盘移至指定库位
                    Location outLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", wmsMoveDetail.getOutLocationCode()));
                    Location inLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", wmsMoveDetail.getInLocationCode()));

                    if(outLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                        Location parentOutLocation = locationService.getParentLocation(outLocation,outLocation.getAreaId());
                        if(StrUtil.isNotEmpty(parentOutLocation.getTrayCode())){
                            String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                            //将移出库位的母库位  搬运到 备份移库库位
                            WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                            childInfo.put(WmsWcsInfo.TRAY_CODE, parentOutLocation.getTrayCode());
                            childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentOutLocation.getLocationCode());
                            childInfo.put(WmsWcsInfo.END_LOCATION_CODE, backMoveLocationCode);
                            childInfo.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                            childInfo.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                            childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                            childInfoList.add(childInfo);
                        }
                    }

                    if(inLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                        Location parentInLocation = locationService.getParentLocation(inLocation,outLocation.getAreaId());
                        if(StrUtil.isNotEmpty(parentInLocation.getTrayCode())){
                            String back2MoveLocationCode = areaMapper.selectBackUp2MoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                            //将移入库位的母库位  搬运到 备份2移库库位
                            WmsWcsInfo parentInLocationChild = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                            parentInLocationChild.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                            parentInLocationChild.put(WmsWcsInfo.START_LOCATION_CODE, parentInLocation.getLocationCode());
                            parentInLocationChild.put(WmsWcsInfo.END_LOCATION_CODE, back2MoveLocationCode);
                            parentInLocationChild.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                            parentInLocationChild.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                            parentInLocationChild.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                            childInfoList.add(parentInLocationChild);
                        }
                    }

                    WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getOutTrayCode());
                    childInfo.put(WmsWcsInfo.START_LOCATION_CODE, wmsMoveDetail.getOutLocationCode());
                    childInfo.put(WmsWcsInfo.END_LOCATION_CODE, wmsMoveDetail.getInLocationCode());
                    childInfo.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                    childInfo.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                    childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                    childInfo.put(WmsWcsInfo.SERVICE_ID, wmsMoveDetail.getId());
                    childInfoList.add(childInfo);

                    if(outLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                        Location parentOutLocation = locationService.getParentLocation(outLocation,outLocation.getAreaId());
                        if(StrUtil.isNotEmpty(parentOutLocation.getTrayCode())){
                            String backMoveLocationCode = areaMapper.selectBackUpMoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                            //将备份2移库库位 回到移出库位的母库位
                            WmsWcsInfo parentInLocationChild = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                            parentInLocationChild.put(WmsWcsInfo.TRAY_CODE, parentOutLocation.getTrayCode());
                            parentInLocationChild.put(WmsWcsInfo.START_LOCATION_CODE, backMoveLocationCode);
                            parentInLocationChild.put(WmsWcsInfo.END_LOCATION_CODE, parentOutLocation.getLocationCode());
                            parentInLocationChild.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                            parentInLocationChild.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                            parentInLocationChild.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                            childInfoList.add(parentInLocationChild);
                        }
                    }

                    if(inLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                        Location parentInLocation = locationService.getParentLocation(inLocation,outLocation.getAreaId());
                        if(StrUtil.isNotEmpty(parentInLocation.getTrayCode())){
                            String back2MoveLocationCode = areaMapper.selectBackUp2MoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
                            //将备份2移库库位 回到移入库位的母库位
                            WmsWcsInfo parentInLocationChild = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                            parentInLocationChild.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                            parentInLocationChild.put(WmsWcsInfo.START_LOCATION_CODE, back2MoveLocationCode);
                            parentInLocationChild.put(WmsWcsInfo.END_LOCATION_CODE, parentInLocation.getLocationCode());
                            parentInLocationChild.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
                            parentInLocationChild.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getInAreaCode());
                            parentInLocationChild.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                            childInfoList.add(parentInLocationChild);
                        }
                    }

                    stackerInfoList.add(info);
                }
            } else {//不同巷道，取到理货区
                addWmsWcsInfo(infoList, wmsMoveDetail, endAreaCode);
            }
        }
        String msg = "成功";
        //取盘，分配库位
        if (infoList.size() > 0) {
            msg = "成功，跨库区移库"+waittaskService.takeTray(infoList);
        }
        //发送wcs任务
        if (stackerInfoList.size() > 0) {
            //记录给wcs任务的数据
            tasklogService.saveBatch(stackerInfoList);
            //把任务的数据给wcs
            List<WmsToWcsTaskReq> collect = stackerInfoList.stream().map(info -> {
                String jsonStr = JSONObject.toJSONString(info);
                return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
            }).collect(Collectors.toList());
            wcsTaskApiService.agvLinkAgeStacker(collect);
        }
        //修改抬头、详情为取盘中
        LambdaUpdateWrapper<WmsMoveList> wmsMoveListUpdateWrapper = Wrappers.lambdaUpdate();
        wmsMoveListUpdateWrapper.set(WmsMoveList::getMoveStatus, MoveEnum.executing.getCode())
                .eq(WmsMoveList::getMoveCode, moveCode);
        wmsMoveListMapper.update(null, wmsMoveListUpdateWrapper);
        LambdaUpdateWrapper<WmsMoveDetail> wmsMoveDetailUpdateWrapper = Wrappers.lambdaUpdate();
        wmsMoveDetailUpdateWrapper.set(WmsMoveDetail::getMoveStatus, MoveEnum.executing.getCode())
                .eq(WmsMoveDetail::getMoveCode, moveCode);
        wmsMoveDetailMapper.update(null, wmsMoveDetailUpdateWrapper);
        return AjaxResult.success(msg);
    }

    /**
     * 将托盘取到理货区
     *
     * @param infoList
     * @param wmsMoveDetail
     * @param endAreaCode
     */
    public void addWmsWcsInfo(List<WmsWcsInfo> infoList, WmsMoveDetail wmsMoveDetail, String endAreaCode) {

        //获取理货区agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .in(WcsDeviceBaseInfo::getWarehouseAreaCode,"LHQ01")
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        //AGV启用
        if(wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) == 0){
            endAreaCode = "csd";
        }

        WmsWcsInfo allInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
        allInfo.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
        allInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
        allInfo.put(WmsWcsInfo.END_AREA_CODE, wmsMoveDetail.getOutAreaCode());
        List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
        allInfo.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);

        String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(wmsMoveDetail.getOutAreaCode());
        String moveLocatioInCode = areaMapper.selectMoveLocationCodeByAreaCode(wmsMoveDetail.getInAreaCode());

        Location outLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", wmsMoveDetail.getOutLocationCode()));
        if(outLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())) {
            Location parentOutLocation = locationService.getParentLocation(outLocation,outLocation.getAreaId());
            if(StrUtil.isNotEmpty(parentOutLocation.getTrayCode())) {
                WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                childInfo.put(WmsWcsInfo.TRAY_CODE, parentOutLocation.getTrayCode());
                childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentOutLocation.getLocationCode());
                childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                childInfoList.add(childInfo);
            }
        }

        WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
        info.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getOutTrayCode());
        info.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getOutAreaCode());
        info.put(WmsWcsInfo.START_LOCATION_CODE, wmsMoveDetail.getOutLocationCode());
        info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
        info.put(WmsWcsInfo.SERVICE_ID, wmsMoveDetail.getId());
        info.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
        childInfoList.add(info);

        if(outLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())) {
            Location parentOutLocation = locationService.getParentLocation(outLocation,outLocation.getAreaId());
            if(StrUtil.isNotEmpty(parentOutLocation.getTrayCode())) {
                WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                childInfo.put(WmsWcsInfo.TRAY_CODE, parentOutLocation.getTrayCode());
                childInfo.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                childInfo.put(WmsWcsInfo.END_LOCATION_CODE, parentOutLocation.getLocationCode());
                childInfo.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                childInfoList.add(childInfo);
            }
        }

        infoList.add(allInfo);


        WmsWcsInfo allInfo1 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
        allInfo1.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getInAreaCode());
        allInfo1.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
        allInfo1.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
        List<WmsWcsInfo> childInfoList1 = new ArrayList<>();//子任务list
        allInfo1.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList1);

        if (StringUtils.isNotEmpty(wmsMoveDetail.getInTrayCode())) {
            Location inLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", wmsMoveDetail.getInLocationCode()));
            Location parentInLocation = locationService.getParentLocation(inLocation,inLocation.getAreaId());
            if(!Objects.isNull(parentInLocation)){
                if(StrUtil.isNotEmpty(parentInLocation.getTrayCode())){
                    WmsWcsInfo parentInLocationChild = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    parentInLocationChild.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                    parentInLocationChild.put(WmsWcsInfo.START_LOCATION_CODE, parentInLocation.getLocationCode());
                    parentInLocationChild.put(WmsWcsInfo.END_LOCATION_CODE, moveLocatioInCode);
                    parentInLocationChild.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                    childInfoList1.add(parentInLocationChild);
                }
            }


            WmsWcsInfo info2 = new WmsWcsInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
            info2.put(WmsWcsInfo.TRAY_CODE, wmsMoveDetail.getInTrayCode());
            info2.put(WmsWcsInfo.START_AREA_CODE, wmsMoveDetail.getInAreaCode());
            info2.put(WmsWcsInfo.START_LOCATION_CODE, wmsMoveDetail.getInLocationCode());
            info2.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
            info2.put(WmsWcsInfo.SERVICE_ID, wmsMoveDetail.getId());
            info2.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
            childInfoList1.add(info2);

            if(!Objects.isNull(parentInLocation)){
                if(StrUtil.isNotEmpty(parentInLocation.getTrayCode())){
                    WmsWcsInfo parentInLocationChild = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    parentInLocationChild.put(WmsWcsInfo.TRAY_CODE, parentInLocation.getTrayCode());
                    parentInLocationChild.put(WmsWcsInfo.START_LOCATION_CODE, moveLocatioInCode);
                    parentInLocationChild.put(WmsWcsInfo.END_LOCATION_CODE, parentInLocation.getLocationCode());
                    parentInLocationChild.put(WmsWcsInfo.DOC, wmsMoveDetail.getMoveCode());
                    childInfoList1.add(parentInLocationChild);
                }
            }
            infoList.add(allInfo1);
        }
        System.out.println(infoList.size());
    }

    /**
     * 移库单结束
     *
     * @param moveDetailId
     */
    @Transactional(rollbackFor = Exception.class)
    public void moveEnd(String moveDetailId) {
        WmsMoveDetail wmsMoveDetail = selectWmsMoveDetailById(moveDetailId);
        //修改详情移库状态完成
        wmsMoveDetail.setMoveStatus(MoveEnum.executed.getCode());
        wmsMoveDetailMapper.updateById(wmsMoveDetail);
        List<String> locationCode = new ArrayList<>();//移库库位
        locationCode.add(wmsMoveDetail.getOutLocationCode());
        locationCode.add(wmsMoveDetail.getInLocationCode());
        List<String> trayCode = new ArrayList<>();//移库托盘
        trayCode.add(wmsMoveDetail.getOutTrayCode());
        if (StringUtils.isNotEmpty(wmsMoveDetail.getInTrayCode())){
            trayCode.add(wmsMoveDetail.getInTrayCode());
        }
        //解锁库位
        LambdaUpdateWrapper<Location> locationUpdateWrapper = Wrappers.lambdaUpdate();
        locationUpdateWrapper.set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                .in(Location::getLocationCode, locationCode);
        locationService.getBaseMapper().update(null, locationUpdateWrapper);
        //解锁库存
        LambdaUpdateWrapper<Tblstock> tblstockUpdateWrapper = Wrappers.lambdaUpdate();
        tblstockUpdateWrapper.set(Tblstock::getLockStatus,LockEnum.NOTLOCK.getCode())
                .in(Tblstock::getTrayCode,trayCode);
        tblstockMapper.update(null,tblstockUpdateWrapper);
        //根据移库详情修改移库抬头
        LambdaQueryWrapper<WmsMoveDetail> wmsMoveDetailQueryWrapper = Wrappers.lambdaQuery();
        wmsMoveDetailQueryWrapper.eq(WmsMoveDetail::getMoveCode, wmsMoveDetail.getMoveCode())
                .ne(WmsMoveDetail::getMoveStatus, MoveEnum.executed.getCode())
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        if (wmsMoveDetailMapper.selectCount(wmsMoveDetailQueryWrapper) == 0) {//修改移库单抬头为已完成
            LambdaUpdateWrapper<WmsMoveList> wmsMoveListUpdateWrapper = Wrappers.lambdaUpdate();
            wmsMoveListUpdateWrapper.set(WmsMoveList::getMoveStatus, MoveEnum.executed.getCode())
                    .eq(WmsMoveList::getMoveCode, wmsMoveDetail.getMoveCode());
            wmsMoveListMapper.update(null, wmsMoveListUpdateWrapper);
        }
    }

    /**
     * 移库后上架
     * @param wmsWcsInfo
     */
    public WmsWcsInfo putOnTray(WmsWcsInfo wmsWcsInfo) {
        String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编号
        String endLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE);//end库位编号
        LambdaQueryWrapper<Location> locationQuery = Wrappers.lambdaQuery();
        locationQuery.select(Location::getLocationName,Location::getAreaId,Location::getWarehouseId)
                .eq(Location::getLocationCode,endLocationCode)
                .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                .last("limit 1");
        Location location = locationService.getBaseMapper().selectOne(locationQuery);//end库位
        String areaCode = location.getAreaId();//库区编码
        LambdaQueryWrapper<Area> areaQuery = Wrappers.lambdaQuery();
        areaQuery.select(Area::getAreaName)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Area::getAreaCode, areaCode)
                .last("limit 1");
        String areaName = areaMapper.selectOne(areaQuery).getAreaName();//库区名称
        String warehouseCode = location.getWarehouseId();//仓库编码
        LambdaQueryWrapper<Warehouse> warehouseQuery = Wrappers.lambdaQuery();
        warehouseQuery.select(Warehouse::getWarehouseName)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Warehouse::getWarehouseCode, warehouseCode)
                .last("limit 1");
        String warehouseName = warehouseMapper.selectOne(warehouseQuery).getWarehouseName();//仓库名称
        //查询托盘中剩余货物信息并更新数据的货位等信息
        LambdaUpdateWrapper<Tblstock> tblstockUpdate = Wrappers.lambdaUpdate();
        tblstockUpdate.set(Tblstock::getUpdateTime, new Date())
                .set(Tblstock::getLocationCode, endLocationCode)
                .set(Tblstock::getLocationName, location.getLocationName())
                .set(Tblstock::getAreaCode, areaCode)
                .set(Tblstock::getAreaName, areaName)
                .set(Tblstock::getWarehouseCode, warehouseCode)
                .set(Tblstock::getWarehouseName, warehouseName)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Tblstock::getTrayCode, trayCode);
        tblstockMapper.update(null, tblstockUpdate);
        //查询关联移动托盘信息
        LambdaQueryWrapper<WmsMoveDetail> wmsMoveDetailQueryWrapper = Wrappers.lambdaQuery();
        wmsMoveDetailQueryWrapper.eq(WmsMoveDetail::getMoveStatus, MoveEnum.executing.getCode())
                .eq(WmsMoveDetail::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .and( Wrappers ->{
                    Wrappers.eq(WmsMoveDetail::getOutTrayCode,trayCode).or();
                    Wrappers.eq(WmsMoveDetail::getInTrayCode,trayCode);
                } );
        List<WmsMoveDetail> details = wmsMoveDetailMapper.selectList(wmsMoveDetailQueryWrapper);
        if(details.size()==1){
            List<String> trays = new ArrayList<>();
            if(StringUtils.isNotEmpty(details.get(0).getInTrayCode())){
                trays.add(details.get(0).getInTrayCode());
            }
            trays.add(details.get(0).getOutTrayCode());
            //查询两个托盘是否都在存储区
            LambdaQueryWrapper<Area> areaLambdaQueryWrapper = Wrappers.lambdaQuery();
            areaLambdaQueryWrapper.select(Area::getAreaCode)
                    .eq(Area::getAreaType,AreaTypeEnum.CCQ.getCode())
                    .eq(Area::getDelFlag,DelFlagEnum.DEL_NO.getCode());
            List<Area> areaList = areaMapper.selectList(areaLambdaQueryWrapper);
            List<String> areaCodes = areaList.stream().map(area -> area.getAreaCode()).collect(Collectors.toList());
            LambdaQueryWrapper<Location> locationLambdaQueryWrapper = Wrappers.lambdaQuery();
            locationLambdaQueryWrapper.eq(Location::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .in(Location::getAreaId,areaCodes)
                    .in(Location::getTrayCode,trays);
            List<Location> locations = locationService.getBaseMapper().selectList(locationLambdaQueryWrapper);
            //托盘到位了，修改移库单详情数据状态
            if(locations.size()==trays.size()){
                details.get(0).setMoveStatus(MoveEnum.executed.getCode());
                wmsMoveDetailMapper.updateById(details.get(0));
                //查询移库单下的数据是否都是已完成
                LambdaQueryWrapper<WmsMoveDetail> detailLambdaQueryWrapper = Wrappers.lambdaQuery();
                detailLambdaQueryWrapper.eq(WmsMoveDetail::getMoveStatus,MoveEnum.executing.getCode())
                        .eq(WmsMoveDetail::getMoveCode,details.get(0).getMoveCode());
                if(wmsMoveDetailMapper.selectCount(detailLambdaQueryWrapper)==0){
                    //不存在执行中数据，修改主数据状态
                    LambdaUpdateWrapper<WmsMoveList> listLambdaUpdateWrapper = Wrappers.lambdaUpdate();
                    listLambdaUpdateWrapper.set(WmsMoveList::getMoveStatus,MoveEnum.executed.getCode())
                            .eq(WmsMoveList::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                            .eq(WmsMoveList::getMoveCode,details.get(0).getMoveCode());
                    wmsMoveListMapper.update(null,listLambdaUpdateWrapper);
                }
            }
        }else if(details.size()>1){
            throw new RuntimeException("移库单数据有问题，同一个库位被引用多次");
        }
        return wmsWcsInfo;
    }
}
