package com.ruoyi.wms.check.service;

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
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.enums.WmsWcsTaskTypeEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.api.domain.WmsToWcsTaskReq;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.service.WcsOperateTaskService;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.basics.service.WmsTacticsConfigService;
import com.ruoyi.wms.basics.service.WmsTransferLocationService;
import com.ruoyi.wms.check.domain.*;
import com.ruoyi.wms.check.dto.CheckDetailVo;
import com.ruoyi.wms.check.mapper.*;
import com.ruoyi.wms.enums.*;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.wcstask.service.TasklogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 库存盘点详情Service接口
 *
 * @author ruoyi
 * @date 2023-03-20
 */
@Slf4j
@Service
public class WmsWarehouseCheckDetailService extends ServiceImpl<WmsWarehouseCheckDetailMapper, CheckDetail> {

    @Autowired
    private WmsWarehouseCheckDetailMapper wmsWarehouseCheckDetailMapper;
    @Autowired
    private WmsWarehouseCheckMapper checkMapper;
    @Autowired
    private CheckGoodsMapper checkGoodsMapper;
    @Autowired
    private CheckConfigMapper checkConfigMapper;
    @Autowired
    private CheckAdjustGoodsMapper checkAdjustGoodsMapper;
    @Autowired
    private CheckAdjustMapper checkAdjustMapper;
    @Autowired
    private TblstockMapper tblstockMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private WmsTransferLocationService wmsTransferLocationService;

    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private CheckAdjustDetailService checkAdjustDetailService;
    @Autowired
    private CheckAdjustGoodsService checkAdjustGoodsService;
    @Autowired
    private CheckGoodsService checkGoodsService;
    @Autowired
    protected Validator validator;
    @Autowired
    private TasklogService tasklogService;

    @Autowired
    private WcsTaskApiService wcsTaskApiService;

    @Autowired
    private WcsOperateTaskService wcsOperateTaskService;

    @Autowired
    private WmsTacticsConfigService wmsTacticsConfigService;

    /**
     * 查询库存盘点详情
     *
     * @param id 库存盘点详情主键
     * @return 库存盘点详情
     */
    public CheckDetail selectWmsWarehouseCheckDetailById(String id) {
        QueryWrapper<CheckDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsWarehouseCheckDetailMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询库存盘点详情
     *
     * @param ids 库存盘点详情 IDs
     * @return 库存盘点详情
     */
    public List<CheckDetail> selectWmsWarehouseCheckDetailByIds(String[] ids) {
        QueryWrapper<CheckDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsWarehouseCheckDetailMapper.selectList(queryWrapper);
    }

    /**
     * 查询库存盘点详情列表
     *
     * @param wmsWarehouseCheckDetail 库存盘点详情
     * @return 库存盘点详情集合
     */
    public List<CheckDetail> selectWmsWarehouseCheckDetailList(CheckDetail wmsWarehouseCheckDetail) {
        QueryWrapper<CheckDetail> queryWrapper = getQueryWrapper(wmsWarehouseCheckDetail);
        return wmsWarehouseCheckDetailMapper.select(queryWrapper);
    }

    /**
     * 新增库存盘点详情
     *
     * @param wmsWarehouseCheckDetail 库存盘点详情
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckDetail insertWmsWarehouseCheckDetail(CheckDetail wmsWarehouseCheckDetail) {
        wmsWarehouseCheckDetail.setId(IdUtil.simpleUUID());
        wmsWarehouseCheckDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsWarehouseCheckDetailMapper.insert(wmsWarehouseCheckDetail);
        return wmsWarehouseCheckDetail;
    }

    /**
     * 修改库存盘点详情
     *
     * @param wmsWarehouseCheckDetail 库存盘点详情
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckDetail updateWmsWarehouseCheckDetail(CheckDetail wmsWarehouseCheckDetail) {
        wmsWarehouseCheckDetailMapper.updateById(wmsWarehouseCheckDetail);
        return wmsWarehouseCheckDetail;
    }

    /**
     * 批量删除库存盘点详情
     *
     * @param ids 需要删除的库存盘点详情主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWarehouseCheckDetailByIds(String[] ids) {
        List<CheckDetail> wmsWarehouseCheckDetails = new ArrayList<>();
        for (String id : ids) {
            CheckDetail wmsWarehouseCheckDetail = new CheckDetail();
            wmsWarehouseCheckDetail.setId(id);
            wmsWarehouseCheckDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsWarehouseCheckDetails.add(wmsWarehouseCheckDetail);
        }
        return super.updateBatchById(wmsWarehouseCheckDetails) ? 1 : 0;
    }

    /**
     * 删除库存盘点详情信息
     *
     * @param id 库存盘点详情主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWarehouseCheckDetailById(String id) {
        CheckDetail wmsWarehouseCheckDetail = new CheckDetail();
        wmsWarehouseCheckDetail.setId(id);
        wmsWarehouseCheckDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsWarehouseCheckDetailMapper.updateById(wmsWarehouseCheckDetail);
    }

    public QueryWrapper<CheckDetail> getQueryWrapper(CheckDetail wmsWarehouseCheckDetail) {
        QueryWrapper<CheckDetail> queryWrapper = new QueryWrapper<>();
        if (wmsWarehouseCheckDetail != null) {
            wmsWarehouseCheckDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", wmsWarehouseCheckDetail.getDelFlag());
            //盘点单号
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getCheckBillCode())) {
                queryWrapper.eq("check_bill_code", wmsWarehouseCheckDetail.getCheckBillCode());
            }
            //盘点货物编码
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getGoodsCode())) {
                queryWrapper.eq("goods_code", wmsWarehouseCheckDetail.getGoodsCode());
            }
            //盘点库区编号
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getAreaCode())) {
                queryWrapper.eq("area_code", wmsWarehouseCheckDetail.getAreaCode());
            }
            //盘点库区名称
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getAreaName())) {
                queryWrapper.like("area_name", wmsWarehouseCheckDetail.getAreaName());
            }
            //盘点库位编号
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getLocationCode())) {
                queryWrapper.eq("location_code", wmsWarehouseCheckDetail.getLocationCode());
            }
            //盘点库位名称
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getLocationName())) {
                queryWrapper.like("location_name", wmsWarehouseCheckDetail.getLocationName());
            }
            //盘点托盘编号
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getTrayCode())) {
                queryWrapper.eq("tray_code", wmsWarehouseCheckDetail.getTrayCode());
            }
            //账面数量
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getCurtainNum())) {
                queryWrapper.eq("curtain_num", wmsWarehouseCheckDetail.getCurtainNum());
            }
            //盘点数量
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getCheckNum())) {
                queryWrapper.eq("check_num", wmsWarehouseCheckDetail.getCheckNum());
            }
            //盘亏数量
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getLossNum())) {
                queryWrapper.eq("loss_num", wmsWarehouseCheckDetail.getLossNum());
            }
            //盘盈数量
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getProfitNum())) {
                queryWrapper.eq("profit_num", wmsWarehouseCheckDetail.getProfitNum());
            }
            //盘点状态(0.未开始  2.已完成 )
            if (StrUtil.isNotEmpty(wmsWarehouseCheckDetail.getCheckStatus())) {
                queryWrapper.eq("check_status", wmsWarehouseCheckDetail.getCheckStatus());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param wmsWarehouseCheckDetailList 模板数据
     * @param updateSupport               是否更新已经存在的数据
     * @param operName                    操作人姓名
     * @return
     */
    public String importData(List<CheckDetail> wmsWarehouseCheckDetailList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsWarehouseCheckDetailList) || wmsWarehouseCheckDetailList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (CheckDetail wmsWarehouseCheckDetail : wmsWarehouseCheckDetailList) {
            if (null == wmsWarehouseCheckDetail) {
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                CheckDetail u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsWarehouseCheckDetail);
                    wmsWarehouseCheckDetail.setId(IdUtil.simpleUUID());
                    wmsWarehouseCheckDetail.setCreateBy(operName);
                    wmsWarehouseCheckDetail.setCreateTime(new Date());
                    wmsWarehouseCheckDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsWarehouseCheckDetailMapper.insert(wmsWarehouseCheckDetail);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsWarehouseCheckDetail);
                    //todo 验证
                    //int count = wmsWarehouseCheckDetailMapper.checkCode(wmsWarehouseCheckDetail);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    wmsWarehouseCheckDetail.setId(u.getId());
                    wmsWarehouseCheckDetail.setUpdateBy(operName);
                    wmsWarehouseCheckDetail.setUpdateTime(new Date());
                    wmsWarehouseCheckDetailMapper.updateById(wmsWarehouseCheckDetail);
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
     * 开始盘点
     *
     * @param check
     */
    @Transactional(rollbackFor = Exception.class)
    public void startCheck(Check check) {
        //验证是否有正在进行的盘点任务
        if (haveChecking()) {
            throw new ServiceException("存在执行中的盘点任务，不可开始盘点");
        }
        //验证是否有正在执行的硬件任务
        if (wcsTaskApiService.haveInWcsTask()){
            throw new ServiceException("存在执行中的托盘任务，不可开始盘点");
        }
        //锁定库位与库存
        LambdaUpdateWrapper<Location> locationUpdateWrapper = Wrappers.lambdaUpdate();
        locationUpdateWrapper.set(Location::getLockStatus,LockEnum.LOCKED.getCode());
        locationService.getBaseMapper().update(null,locationUpdateWrapper);
        LambdaUpdateWrapper<Tblstock> tblstockUpdateWrapper = Wrappers.lambdaUpdate();
        tblstockUpdateWrapper.set(Tblstock::getLockStatus, LockEnum.LOCKED.getCode());
        tblstockMapper.update(null,tblstockUpdateWrapper);
        //盘点单号
        String checkBillCode = check.getCheckBillCode();
        LambdaQueryWrapper<Check> checkQueryWrapper = Wrappers.lambdaQuery();
        checkQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Check::getCheckStatus, CheckStatusEnum.NOT.getCode())
                .eq(Check::getCheckBillCode, checkBillCode);
        //盘点任务
        check = checkMapper.selectOne(checkQueryWrapper);
        if (check == null) {
            throw new ServiceException("盘点任务异常");
        }
        //盘点类型（0.库位 1.货物类型）
        String checkType = check.getCheckType();
        if (CheckTypeEnum.LOCATION.getCode().equals(checkType)) {
            //更新盘点详情托盘号、账面数量
            wmsWarehouseCheckDetailMapper.updateCheckDetailTrayInfo(checkBillCode);
            //修改空库位为已盘点
            LambdaUpdateWrapper<CheckDetail> checkDetailUpdateWrapper = Wrappers.lambdaUpdate();
            checkDetailUpdateWrapper.set(CheckDetail::getCheckStatus, CheckStatusEnum.ALREADY.getCode())
                    .eq(CheckDetail::getCheckBillCode, checkBillCode)
                    .isNull(CheckDetail::getTrayCode);
            wmsWarehouseCheckDetailMapper.update(null, checkDetailUpdateWrapper);
            log.info("更新盘点详情");
        } else if (CheckTypeEnum.GOODS.getCode().equals(checkType)) {
            //查询配置表
            LambdaQueryWrapper<CheckConfig> checkConfigQueryWrapper = Wrappers.lambdaQuery();
            checkConfigQueryWrapper.select(CheckConfig::getGoodsCode)
                    .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(CheckConfig::getCheckBillCode, checkBillCode);
            List<String> goodsCodeList = checkConfigMapper.selectObjs(checkConfigQueryWrapper)
                    .stream().map(String::valueOf).collect(Collectors.toList());
            if (goodsCodeList.size() == 0) {
                throw new ServiceException("盘点任务货物类型异常");
            }
            //新增盘点详情数据
            List<CheckDetail> checkDetailList = wmsWarehouseCheckDetailMapper.selectCheckDetailList(goodsCodeList);
            checkDetailList = checkDetailList.stream().map(checkDetail -> {
                checkDetail.setId(IdUtil.simpleUUID());
                checkDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                checkDetail.setCheckBillCode(checkBillCode);
                checkDetail.setCheckStatus(CheckStatusEnum.NOT.getCode());
                return checkDetail;
            }).collect(Collectors.toList());
            this.saveBatch(checkDetailList, checkDetailList.size());
            log.info("新增盘点详情");
        } else {
            throw new ServiceException("盘点类型异常");
        }
        log.info("新增盘点详情货物数据");
        List<CheckGoods> checkGoodsList = checkGoodsMapper.selectCheckGoodsList(checkBillCode);
        checkGoodsList = checkGoodsList.stream().map(checkGoods -> {
            checkGoods.setId(IdUtil.simpleUUID());
            checkGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            checkGoods.setCheckBillCode(checkBillCode);
            return checkGoods;
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(checkGoodsList)){{
            checkGoodsService.saveBatch(checkGoodsList, checkGoodsList.size());
        }}
        //查询盘点单要盘点的库区
        QueryWrapper<CheckDetail> checkDetailQuery = Wrappers.query();
        checkDetailQuery.select("distinct area_code")
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("check_bill_code", checkBillCode)
                .eq("check_status", CheckStatusEnum.NOT.getCode())
                .orderByAsc("area_code")
                .isNotNull("tray_code");
        List<String> areaCodeList = wmsWarehouseCheckDetailMapper.selectObjs(checkDetailQuery)
                .stream().map(String::valueOf).collect(Collectors.toList());


        LambdaQueryWrapper<CheckDetail> checkDetailLambdaQueryWrapper = Wrappers.lambdaQuery();
        checkDetailLambdaQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(CheckDetail::getCheckBillCode, checkBillCode)
                .eq(CheckDetail::getCheckStatus, CheckStatusEnum.NOT.getCode())
                .isNotNull(CheckDetail::getTrayCode)
                .orderByAsc(CheckDetail::getLocationCode);
        List<CheckDetail> checkAllDetails = wmsWarehouseCheckDetailMapper.selectList(checkDetailLambdaQueryWrapper);
        List<String> checkLocations = checkAllDetails.stream()
                .map(CheckDetail::getLocationCode).collect(Collectors.toList());

        //组装取盘任务
        List<WmsWcsInfo> infoList = new ArrayList<>();
        for (String areaCode : areaCodeList) {
            QueryWrapper<CheckDetail> checkDetailQueryWrapper = new QueryWrapper<>();
            checkDetailQueryWrapper.eq("wc.del_flag",DelFlagEnum.DEL_NO.getCode());
            checkDetailQueryWrapper.eq("wc.check_bill_code",checkBillCode);
            checkDetailQueryWrapper.eq("wc.area_code",areaCode);
            checkDetailQueryWrapper.eq("wc.check_status", CheckStatusEnum.NOT.getCode());
            checkDetailQueryWrapper.isNotNull("wc.tray_code");
            checkDetailQueryWrapper.orderByAsc("lc.column_num","lc.layer","lc.location_type","lc.order_num");
            checkDetailQueryWrapper.last("limit 1");
            CheckDetail checkDetailNext = wmsWarehouseCheckDetailMapper.getStartCheckOne(checkDetailQueryWrapper);


            WmsWcsInfo allInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
            allInfo.put(WmsWcsInfo.START_AREA_CODE, checkDetailNext.getAreaCode());
            allInfo.put(WmsWcsInfo.DOC, checkBillCode);
            allInfo.put(WmsWcsInfo.END_AREA_CODE, checkDetailNext.getAreaCode());
            List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
            allInfo.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);

            String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(checkDetailNext.getAreaCode());

            Location checkLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", checkDetailNext.getLocationCode()));


            if(checkLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                Location parentCheckLocation = locationService.getParentLocation(checkLocation,checkLocation.getAreaId());
                if(StrUtil.isNotEmpty(parentCheckLocation.getTrayCode())){
                    WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo.put(WmsWcsInfo.TRAY_CODE, parentCheckLocation.getTrayCode());

                    childInfo.put(WmsWcsInfo.START_AREA_CODE, parentCheckLocation.getAreaId());
                    childInfo.put(WmsWcsInfo.END_AREA_CODE, parentCheckLocation.getAreaId());

                    childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentCheckLocation.getLocationCode());
                    childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);

                    childInfo.put(WmsWcsInfo.DOC, checkBillCode);
                    childInfoList.add(childInfo);
                }
            }


            WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode());
            info.put(WmsWcsInfo.TRAY_CODE, checkDetailNext.getTrayCode());
            info.put(WmsWcsInfo.START_AREA_CODE, checkDetailNext.getAreaCode());
            info.put(WmsWcsInfo.END_AREA_CODE, checkDetailNext.getAreaCode());
            info.put(WmsWcsInfo.START_LOCATION_CODE, checkDetailNext.getLocationCode());
            info.put(WmsWcsInfo.END_LOCATION_CODE, "csd");
            info.put(WmsWcsInfo.DOC, checkBillCode);
            childInfoList.add(info);


            // if(checkLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
            //     Location parentCheckLocation = locationService.getParentLocation(checkLocation,checkLocation.getAreaId());
            //     if(StrUtil.isNotEmpty(parentCheckLocation.getTrayCode()) && !checkLocations.contains(parentCheckLocation.getLocationCode())){
            //         WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
            //         childInfo.put(WmsWcsInfo.TRAY_CODE, parentCheckLocation.getTrayCode());
            //         childInfo.put(WmsWcsInfo.START_AREA_CODE, parentCheckLocation.getAreaId());
            //         childInfo.put(WmsWcsInfo.END_AREA_CODE, parentCheckLocation.getAreaId());
            //
            //         childInfo.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
            //         childInfo.put(WmsWcsInfo.END_LOCATION_CODE, parentCheckLocation.getLocationCode());
            //
            //         childInfo.put(WmsWcsInfo.DOC, checkBillCode);
            //         childInfoList.add(childInfo);
            //     }
            // }

            infoList.add(allInfo);
        }
        if (infoList.size() > 0) {
            log.info("发送取盘命令");
            //保存任务日志
            tasklogService.saveBatch(infoList);
            //把任务的数据给wcs
            List<WmsToWcsTaskReq> collect = infoList.stream().map(info2 -> {
                String jsonStr = JSONObject.toJSONString(info2);
                return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
            }).collect(Collectors.toList());
            wcsTaskApiService.agvLinkAgeStacker(collect);
        }
        //修改盘点单状态为进行中
        check.setCheckStatus(CheckStatusEnum.ING.getCode());
        check.setStartTime(new Date());
        checkMapper.updateById(check);
    }


    /**
     * 结束盘点
     *
     * @param check
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult endCheck(Check check) {
        //盘点单号
        String checkBillCode = check.getCheckBillCode();
        //盘点状态（2-强制结束盘点）
        String checkStatus = check.getCheckStatus();
        //验证是否已盘点完毕
        LambdaQueryWrapper<CheckDetail> checkDetailQueryWrapper = Wrappers.lambdaQuery();
        checkDetailQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(CheckDetail::getCheckBillCode, checkBillCode)
                .eq(CheckDetail::getCheckStatus, CheckStatusEnum.NOT.getCode());
        if (wmsWarehouseCheckDetailMapper.selectCount(checkDetailQueryWrapper) > 0) {
            if (!CheckStatusEnum.ALREADY.getCode().equals(checkStatus)) {
                //没有确认强制完成，返回强制回盘确认提示
                return new AjaxResult(600,"盘点未结束，是否强制结束");
            } else {
                //强制完成，修改未盘点的盘点详情盘点数量为账面数量
                wmsWarehouseCheckDetailMapper.updateCheckDetailcheckNum(checkBillCode);
            }
        }
        LambdaQueryWrapper<Check> checkQueryWrapper = Wrappers.lambdaQuery();
        checkQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Check::getCheckStatus, CheckStatusEnum.ING.getCode())
                .eq(Check::getCheckBillCode, checkBillCode);
        //盘点任务
        check = checkMapper.selectOne(checkQueryWrapper);
        if (check == null) {
            throw new ServiceException("盘点任务异常");
        }
        //修改盘点单状态为结束
        check.setEndTime(new Date());
        check.setCheckStatus(CheckStatusEnum.ALREADY.getCode());
        checkMapper.updateById(check);
        //解锁库位与库存
        LambdaQueryWrapper<Area> areaQueryWrapper = Wrappers.lambdaQuery();
        areaQueryWrapper.select(Area::getMoveLocationCode)
                .isNotNull(Area::getMoveLocationCode);
        //移库库位不解除锁定
        List<String> collect = areaMapper.selectObjs(areaQueryWrapper).stream().map(String::valueOf).collect(Collectors.toList());
        List<String> moveLocationCodeList = new ArrayList<>();
        for (String s : collect) {
            String[] split = s.split(",");
            for (String moveLocationCode : split) {
                moveLocationCodeList.add(moveLocationCode);
            }
        }

        LambdaUpdateWrapper<Location> locationUpdateWrapper = Wrappers.lambdaUpdate();
        locationUpdateWrapper.set(Location::getLockStatus,LockEnum.NOTLOCK.getCode());
        locationUpdateWrapper.notIn(Location::getLocationCode,moveLocationCodeList);
        locationService.getBaseMapper().update(null,locationUpdateWrapper);
        LambdaUpdateWrapper<Tblstock> tblstockUpdateWrapper = Wrappers.lambdaUpdate();
        tblstockUpdateWrapper.set(Tblstock::getLockStatus, LockEnum.NOTLOCK.getCode());
        tblstockMapper.update(null,tblstockUpdateWrapper);
        //强制结束，不生成盘点调整单
        if (CheckStatusEnum.ALREADY.getCode().equals(checkStatus)){
            //强制结束盘点，组装自动回盘任务
            foreFinshCheckTrayAutoBack(checkBillCode);
            return AjaxResult.success("成功");
        }
        //查询盘盈数据、盘亏数据
        List<CheckAdjustGoods> profitList = checkAdjustGoodsMapper.selectProfitData(checkBillCode);
        List<CheckAdjustGoods> lossList = checkAdjustGoodsMapper.selectLossData(checkBillCode);
        if (CollUtil.isNotEmpty(profitList) || CollUtil.isNotEmpty(lossList)) {
            //存在盈亏数据，生成盘点调整单
            CheckAdjust checkAdjust = new CheckAdjust();
            checkAdjust.setId(IdUtil.simpleUUID());
            checkAdjust.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            checkAdjust.setCheckBillCode(checkBillCode);
            checkAdjust.setProfitNum(String.valueOf(profitList.size()));
            checkAdjust.setLossNum(String.valueOf(lossList.size()));
            checkAdjust.setStartTime(check.getStartTime());
            checkAdjust.setEndTime(check.getEndTime());
            checkAdjust.setCheckBy(check.getCheckBy());
            checkAdjust.setAdjustStatus(AdjustStatusEnum.wait.getCode());
            //查询账面数量、盘点数量
            CheckDetailVo numMap = wmsWarehouseCheckDetailMapper.selectNum(checkBillCode);
            String num = numMap.getCurtainNum();
            String curtainNum = num.replace(".0", "");
            checkAdjust.setCurtainNum(curtainNum);
            String num1 = numMap.getCheckNum();
            String checkNum = num1.replace(".0", "");
            checkAdjust.setCheckNum(checkNum);
            checkAdjustMapper.insert(checkAdjust);
            //生成盘点调整单详情
            //盘点单下有差异的托盘编号
            List<String> trayCodeList = Stream.concat(profitList.stream(), lossList.stream())
                    .map(CheckAdjustGoods::getTrayCode).distinct().collect(Collectors.toList());
            checkDetailQueryWrapper.clear();
            checkDetailQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(CheckDetail::getCheckBillCode, checkBillCode)
                    .in(CheckDetail::getTrayCode, trayCodeList);
            //有差异的盘点清单
            List<CheckDetail> checkDetailList = wmsWarehouseCheckDetailMapper.selectList(checkDetailQueryWrapper);
            //盘点调整详情
            List<CheckAdjustDetail> checkAdjustDetailList = new ArrayList<>();
            //盘点调整货物
            List<CheckAdjustGoods> checkAdjustGoodsList = new ArrayList<>();
            for (CheckDetail checkDetail : checkDetailList) {
                //新增调整详情
                CheckAdjustDetail checkAdjustDetail = new CheckAdjustDetail();
                BeanUtils.copyProperties(checkDetail, checkAdjustDetail);
                checkAdjustDetail.setId(IdUtil.simpleUUID());
                //新增调整货物
                List<CheckAdjustGoods> profitGoods = profitList.stream().filter(pro -> checkDetail.getTrayCode().equals(pro.getTrayCode())).collect(Collectors.toList());
                checkAdjustDetail.setProfitNum(String.valueOf(profitGoods.size()));
                checkDetail.setProfitNum(String.valueOf(profitGoods.size()));
                for (CheckAdjustGoods checkAdjustGoods : profitGoods) {
                    checkAdjustGoods.setId(IdUtil.simpleUUID());
                    checkAdjustGoods.setCheckAdjustDetail(checkAdjustDetail.getId());
                    checkAdjustGoods.setLocationCode(checkAdjustDetail.getLocationCode());
                    checkAdjustGoods.setProfitNum("1");
                    checkAdjustGoods.setLossNum("0");
                    checkAdjustGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    checkAdjustGoodsList.add(checkAdjustGoods);
                }
                List<CheckAdjustGoods> lossGoods = lossList.stream().filter(loss -> checkDetail.getTrayCode().equals(loss.getTrayCode())).collect(Collectors.toList());
                checkAdjustDetail.setLossNum(String.valueOf(lossGoods.size()));
                checkDetail.setLossNum(String.valueOf(lossGoods.size()));
                for (CheckAdjustGoods checkAdjustGoods : lossGoods) {
                    checkAdjustGoods.setId(IdUtil.simpleUUID());
                    checkAdjustGoods.setCheckAdjustDetail(checkAdjustDetail.getId());
                    checkAdjustGoods.setLocationCode(checkAdjustDetail.getLocationCode());
                    checkAdjustGoods.setProfitNum("0");
                    checkAdjustGoods.setLossNum("1");
                    checkAdjustGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    checkAdjustGoodsList.add(checkAdjustGoods);
                }
                checkAdjustDetailList.add(checkAdjustDetail);
                //给盘点调整单赋值货物编码、货物名称
                if (profitGoods.size()>0){
                    checkAdjustDetail.setGoodsCode(profitGoods.get(0).getGoodsCode());
                    checkAdjustDetail.setGoodsName(profitGoods.get(0).getGoodsName());
                }else if (lossGoods.size()>0){
                    checkAdjustDetail.setGoodsCode(lossGoods.get(0).getGoodsCode());
                    checkAdjustDetail.setGoodsName(lossGoods.get(0).getGoodsName());
                }
            }
            if (checkDetailList.size() > 0) {
                this.updateBatchById(checkDetailList,checkDetailList.size());
            }
            if (checkAdjustDetailList.size() > 0) {
                checkAdjustDetailService.saveBatch(checkAdjustDetailList,checkAdjustDetailList.size());
            }
            if (checkAdjustGoodsList.size() > 0) {
                checkAdjustGoodsService.saveBatch(checkAdjustGoodsList,checkAdjustGoodsList.size());
            }
        }
        return AjaxResult.success("成功");
    }

    /**
     * 强制结束盘点，自动回盘操作
     * @param checkBillCode
     */
    private void foreFinshCheckTrayAutoBack(String checkBillCode) {
        String csd1LocationCode = wmsTransferLocationService.getTransferLocationCodeByArea("CCQ01", TransferLocationArrowEnums.RIGHT.getCode());
        String csd2LocationCode = wmsTransferLocationService.getTransferLocationCodeByArea("CCQ02", TransferLocationArrowEnums.RIGHT.getCode());
        String csd3LocationCode = wmsTransferLocationService.getTransferLocationCodeByArea("CCQ03", TransferLocationArrowEnums.RIGHT.getCode());
        WcsOperateTask wcsOperateTask1 = wcsOperateTaskService.getBaseMapper().selectOne(
                new QueryWrapper<WcsOperateTask>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("in_bill_no", checkBillCode)
                        .eq("task_type", WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode())
                        .eq("operate_type", WmsWcsTypeEnum.TAKETRAY.getCode())
                        .eq("end_position", csd1LocationCode)
                        .orderByDesc("operate_end_time")
                        .last("limit 1")
        );

        WcsOperateTask wcsOperateTask2= wcsOperateTaskService.getBaseMapper().selectOne(
                new QueryWrapper<WcsOperateTask>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("in_bill_no", checkBillCode)
                        .eq("task_type", WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode())
                        .eq("operate_type", WmsWcsTypeEnum.TAKETRAY.getCode())
                        .eq("end_position", csd2LocationCode)
                        .orderByDesc("operate_end_time")
                        .last("limit 1")
        );

        WcsOperateTask wcsOperateTask3= wcsOperateTaskService.getBaseMapper().selectOne(
                new QueryWrapper<WcsOperateTask>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("in_bill_no", checkBillCode)
                        .eq("task_type", WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode())
                        .eq("operate_type", WmsWcsTypeEnum.TAKETRAY.getCode())
                        .eq("end_position", csd3LocationCode)
                        .orderByDesc("operate_end_time")
                        .last("limit 1")
        );

        List<WcsOperateTask> wcsOperateTaskList = new ArrayList<>();
        if(wcsOperateTask1 != null){
            WcsOperateTask backTask1 = wcsOperateTaskService.getBaseMapper().selectOne(
                    new QueryWrapper<WcsOperateTask>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("in_bill_no", checkBillCode)
                            .eq("task_type", WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode())
                            .eq("operate_type", WmsWcsTypeEnum.PUTTRAY.getCode())
                            .eq("end_position", wcsOperateTask1.getStartPosition())
                            .orderByDesc("operate_end_time")
                            .last("limit 1")
            );
            if(backTask1 == null){
                wcsOperateTaskList.add(wcsOperateTask1);
            }
        }
        if(wcsOperateTask2 != null){
            WcsOperateTask backTask2 = wcsOperateTaskService.getBaseMapper().selectOne(
                    new QueryWrapper<WcsOperateTask>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("in_bill_no", checkBillCode)
                            .eq("task_type", WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode())
                            .eq("operate_type", WmsWcsTypeEnum.PUTTRAY.getCode())
                            .eq("end_position", wcsOperateTask2.getStartPosition())
                            .orderByDesc("operate_end_time")
                            .last("limit 1")
            );
            if(backTask2 == null){
                wcsOperateTaskList.add(wcsOperateTask2);
            }
        }
        if(wcsOperateTask3 != null){
            WcsOperateTask backTask3 = wcsOperateTaskService.getBaseMapper().selectOne(
                    new QueryWrapper<WcsOperateTask>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("in_bill_no", checkBillCode)
                            .eq("task_type", WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode())
                            .eq("operate_type", WmsWcsTypeEnum.PUTTRAY.getCode())
                            .eq("end_position", wcsOperateTask3.getStartPosition())
                            .orderByDesc("operate_end_time")
                            .last("limit 1")
            );
            if(backTask3 == null){
                wcsOperateTaskList.add(wcsOperateTask3);
            }
        }

        if(CollUtil.isNotEmpty(wcsOperateTaskList)){
            for (WcsOperateTask wcsOperateTask : wcsOperateTaskList) {
                String areaCode = wmsTransferLocationService.getAreaCodeByTransferLocationCode(wcsOperateTask.getEndPosition());
                //组装回盘任务
                List<WmsWcsInfo> infoList = new ArrayList<>();

                WmsWcsInfo returnInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                returnInfo.put(WmsWcsInfo.START_AREA_CODE, areaCode);
                returnInfo.put(WmsWcsInfo.DOC, checkBillCode);
                returnInfo.put(WmsWcsInfo.END_AREA_CODE, areaCode);
                List<WmsWcsInfo> returnChildInfoList = new ArrayList<>();//子任务list
                returnInfo.put(WmsWcsInfo.CHILD_INFO_LIST, returnChildInfoList);
                String moveLocationCode1 = areaMapper.selectMoveLocationCodeByAreaCode(areaCode);

                Location moveLocationObj1 = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", moveLocationCode1));

                Location checkLocation1 = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", wcsOperateTask.getStartPosition()));
                if(checkLocation1.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                    Location parentCheckLocation = locationService.getParentLocation(checkLocation1,checkLocation1.getAreaId());
                    if(StrUtil.isNotEmpty(moveLocationObj1.getTrayCode())){
                        WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo.put(WmsWcsInfo.TRAY_CODE, moveLocationObj1.getTrayCode());
                        childInfo.put(WmsWcsInfo.START_AREA_CODE, parentCheckLocation.getAreaId());
                        childInfo.put(WmsWcsInfo.END_AREA_CODE, parentCheckLocation.getAreaId());

                        childInfo.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationObj1.getLocationCode());
                        childInfo.put(WmsWcsInfo.END_LOCATION_CODE, parentCheckLocation.getLocationCode());
                        childInfo.put(WmsWcsInfo.DOC, checkBillCode);
                        returnChildInfoList.add(childInfo);
                    }
                }
                WmsWcsInfo returnInfoMain = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode());
                returnInfoMain.put(WmsWcsInfo.TRAY_CODE, wcsOperateTask.getTrayNo());
                returnInfoMain.put(WmsWcsInfo.START_AREA_CODE, areaCode);
                returnInfoMain.put(WmsWcsInfo.END_AREA_CODE, areaCode);
                returnInfoMain.put(WmsWcsInfo.END_LOCATION_CODE, wcsOperateTask.getStartPosition());
                returnInfoMain.put(WmsWcsInfo.DOC, checkBillCode);
                returnChildInfoList.add(returnInfoMain);

                // if(checkLocation1.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
                //     Location parentCheckLocation = locationService.getParentLocation(checkLocation1,checkLocation1.getAreaId());
                //     if(StrUtil.isNotEmpty(parentCheckLocation.getTrayCode())){
                //         WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                //         childInfo.put(WmsWcsInfo.TRAY_CODE, parentCheckLocation.getTrayCode());
                //         childInfo.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode1);
                //         childInfo.put(WmsWcsInfo.END_LOCATION_CODE, parentCheckLocation.getLocationCode());
                //         childInfo.put(WmsWcsInfo.DOC, checkBillCode);
                //         returnChildInfoList.add(childInfo);
                //     }
                // }
                infoList.add(returnInfo);

                //保存任务日志
                tasklogService.saveBatch(infoList);
                //把任务的数据给wcs
                List<WmsToWcsTaskReq> collect = infoList.stream().map(info2 -> {
                    String jsonStr = JSONObject.toJSONString(info2);
                    return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
                }).collect(Collectors.toList());
                wcsTaskApiService.agvLinkAgeStacker(collect);
            }
        }
    }

    /**
     * 是否有正在进行的盘点任务
     * @return true 有，false 无
     */
    public boolean haveChecking(){
        //验证是否有正在进行的盘点任务
        LambdaQueryWrapper<Check> checkQueryWrapper = Wrappers.lambdaQuery();
        checkQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Check::getCheckStatus, CheckStatusEnum.ING.getCode());
        if (checkMapper.selectCount(checkQueryWrapper) > 0) {
            return true;
        }
        return false;
    }

}
