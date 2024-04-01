package com.ruoyi.wms.check.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.WmsWcsTaskTypeEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.api.domain.WmsToWcsTaskReq;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.service.WcsOperateTaskService;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.check.domain.Check;
import com.ruoyi.wms.check.domain.CheckDetail;
import com.ruoyi.wms.check.domain.CheckRealitygoods;
import com.ruoyi.wms.check.mapper.CheckRealitygoodsMapper;
import com.ruoyi.wms.check.mapper.WmsWarehouseCheckDetailMapper;
import com.ruoyi.wms.check.mapper.WmsWarehouseCheckMapper;
import com.ruoyi.wms.enums.CheckStatusEnum;
import com.ruoyi.wms.enums.LocationTypeEnum;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.mapper.InbillGoodsMapper;
import com.ruoyi.wms.wcstask.service.TasklogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存盘点实盘货物单Service接口
 *
 * @author ruoyi
 * @date 2023-03-20
 */
@Slf4j
@Service
public class CheckRealitygoodsService extends ServiceImpl<CheckRealitygoodsMapper, CheckRealitygoods> {

    @Autowired(required = false)
    private CheckRealitygoodsMapper checkRealitygoodsMapper;

    @Autowired(required = false)
    private TblstockMapper tblstockMapper;

    @Autowired(required = false)
    private WmsWarehouseCheckMapper checkMapper;

    @Autowired(required = false)
    private WmsWarehouseCheckDetailMapper checkDetailMapper;

    @Autowired
    private TasklogService tasklogService;

    @Autowired
    private WcsTaskApiService wcsTaskApiService;

    @Autowired
    protected Validator validator;

    @Autowired
    private InbillGoodsMapper InbillGoodsMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private WcsOperateTaskService wcsOperateTaskService;

    /**
     * 查询库存盘点实盘货物单
     *
     * @param id 库存盘点实盘货物单主键
     * @return 库存盘点实盘货物单
     */
    public CheckRealitygoods selectCheckRealitygoodsById(String id) {
        QueryWrapper<CheckRealitygoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return checkRealitygoodsMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询库存盘点实盘货物单
     *
     * @param ids 库存盘点实盘货物单 IDs
     * @return 库存盘点实盘货物单
     */
    public List<CheckRealitygoods> selectCheckRealitygoodsByIds(String[] ids) {
        QueryWrapper<CheckRealitygoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return checkRealitygoodsMapper.selectList(queryWrapper);
    }

    /**
     * 查询库存盘点实盘货物单列表
     *
     * @param checkRealitygoods 库存盘点实盘货物单
     * @return 库存盘点实盘货物单集合
     */
    public List<CheckRealitygoods> selectCheckRealitygoodsList(CheckRealitygoods checkRealitygoods) {
        QueryWrapper<CheckRealitygoods> queryWrapper = getQueryWrapper(checkRealitygoods);
        return checkRealitygoodsMapper.select(queryWrapper);
    }

    /**
     * 新增库存盘点实盘货物单
     *
     * @param checkRealitygoods 库存盘点实盘货物单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckRealitygoods insertCheckRealitygoods(CheckRealitygoods checkRealitygoods) {
        checkRealitygoods.setId(IdUtil.simpleUUID());
        checkRealitygoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        checkRealitygoodsMapper.insert(checkRealitygoods);
        return checkRealitygoods;
    }

    /**
     * 修改库存盘点实盘货物单
     *
     * @param checkRealitygoods 库存盘点实盘货物单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckRealitygoods updateCheckRealitygoods(CheckRealitygoods checkRealitygoods) {
        checkRealitygoodsMapper.updateById(checkRealitygoods);
        return checkRealitygoods;
    }

    /**
     * 批量删除库存盘点实盘货物单
     *
     * @param ids 需要删除的库存盘点实盘货物单主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckRealitygoodsByIds(String[] ids) {
        List<CheckRealitygoods> checkRealitygoodss = new ArrayList<>();
        for (String id : ids) {
            CheckRealitygoods checkRealitygoods = new CheckRealitygoods();
            checkRealitygoods.setId(id);
            checkRealitygoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            checkRealitygoodss.add(checkRealitygoods);
        }
        return super.updateBatchById(checkRealitygoodss) ? 1 : 0;
    }

    /**
     * 删除库存盘点实盘货物单信息
     *
     * @param id 库存盘点实盘货物单主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckRealitygoodsById(String id) {
        CheckRealitygoods checkRealitygoods = new CheckRealitygoods();
        checkRealitygoods.setId(id);
        checkRealitygoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return checkRealitygoodsMapper.updateById(checkRealitygoods);
    }

    public QueryWrapper<CheckRealitygoods> getQueryWrapper(CheckRealitygoods checkRealitygoods) {
        QueryWrapper<CheckRealitygoods> queryWrapper = new QueryWrapper<>();
        if (checkRealitygoods != null) {
            checkRealitygoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", checkRealitygoods.getDelFlag());
            //盘点单号
            if (StrUtil.isNotEmpty(checkRealitygoods.getCheckBillCode())) {
                queryWrapper.eq("check_bill_code", checkRealitygoods.getCheckBillCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(checkRealitygoods.getPartsCode())) {
                queryWrapper.eq("parts_code", checkRealitygoods.getPartsCode());
            }
            //货物唯一码
            if (StrUtil.isNotEmpty(checkRealitygoods.getOnlyCode())) {
                queryWrapper.eq("only_code", checkRealitygoods.getOnlyCode());
            }
            //盘点托盘编号
            if (StrUtil.isNotEmpty(checkRealitygoods.getTrayCode())) {
                queryWrapper.eq("tray_code", checkRealitygoods.getTrayCode());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param checkRealitygoodsList 模板数据
     * @param updateSupport         是否更新已经存在的数据
     * @param operName              操作人姓名
     * @return
     */
    public String importData(List<CheckRealitygoods> checkRealitygoodsList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(checkRealitygoodsList) || checkRealitygoodsList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (CheckRealitygoods checkRealitygoods : checkRealitygoodsList) {
            if (null == checkRealitygoods) {
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                CheckRealitygoods u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, checkRealitygoods);
                    checkRealitygoods.setId(IdUtil.simpleUUID());
                    checkRealitygoods.setCreateBy(operName);
                    checkRealitygoods.setCreateTime(new Date());
                    checkRealitygoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    checkRealitygoodsMapper.insert(checkRealitygoods);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, checkRealitygoods);
                    //todo 验证
                    //int count = checkRealitygoodsMapper.checkCode(checkRealitygoods);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    checkRealitygoods.setId(u.getId());
                    checkRealitygoods.setUpdateBy(operName);
                    checkRealitygoods.setUpdateTime(new Date());
                    checkRealitygoodsMapper.updateById(checkRealitygoods);
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
     * pda盘点托盘
     *
     * @param list
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkTray(List<CheckRealitygoods> list) {
        //获取当前盘点单号
        String checkBillCode = "";
        LambdaQueryWrapper<Check> checkQueryWrapper = Wrappers.lambdaQuery();
        checkQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Check::getCheckStatus, CheckStatusEnum.ING.getCode());
        try {
            checkBillCode = checkMapper.selectOne(checkQueryWrapper).getCheckBillCode();
        } catch (Exception e) {
            throw new ServiceException("盘点数据异常");
        }
        CheckRealitygoods realitygoods = list.get(0);
        //本次盘点的托盘
        String trayCode = realitygoods.getTrayCode();

        //本次盘点该托盘的盘点入库任务数量
        int checkInTaskCount = wcsOperateTaskService.getBaseMapper().selectCount(
                new QueryWrapper<WcsOperateTask>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("in_bill_no", checkBillCode)
                        .eq("tray_no", trayCode)
                        .eq("task_type", WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode())
                        .eq("operate_type", WmsWcsTypeEnum.PUTTRAY.getCode())
        ).intValue();

        if(checkInTaskCount > 0){
            throw new ServiceException("托盘["+trayCode+"],已经盘点入库。请扫描传输带上的托盘,进行盘点入库。");
        }


        //获取盘点托盘信息
        LambdaQueryWrapper<CheckDetail> checkDetailQueryWrapper = Wrappers.lambdaQuery();
        checkDetailQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(CheckDetail::getCheckBillCode, checkBillCode)
                .eq(CheckDetail::getTrayCode, trayCode)
                .last("limit 1");
        CheckDetail checkDetail = checkDetailMapper.selectOne(checkDetailQueryWrapper);
        if (checkDetail == null) {
            throw new ServiceException(trayCode+"托盘号不在此次盘点任务中");
        }
        String checkNum = "";//盘点数量
        if (StringUtils.isNotEmpty(realitygoods.getPartsCode())) {
            List<String> partsCodeList = list.stream().map(CheckRealitygoods::getPartsCode).collect(Collectors.toList());
           // LambdaQueryWrapper<Tblstock> tblstockQueryWrapper = Wrappers.lambdaQuery();
           // tblstockQueryWrapper.select(Tblstock::getGoodsCode, Tblstock::getGoodsName, Tblstock::getModel, Tblstock::getMeasureUnit, Tblstock::getPartsCode)
           //         .in(Tblstock::getPartsCode, partsCodeList);
           // List<Tblstock> tblstockList = tblstockMapper.selectList(tblstockQueryWrapper);

            LambdaQueryWrapper<InbillGoods> inbillQueryWrapper = Wrappers.lambdaQuery();
            inbillQueryWrapper.in(InbillGoods::getPartsCode,partsCodeList);
            List<CheckRealitygoods> inbillList = InbillGoodsMapper.getGoodsInfo(inbillQueryWrapper);
           // List<String> inbillDetailId = inbillList.stream().map(InbillGoods::getId).collect(Collectors.toList());
           //
           // LambdaQueryWrapper<InbillDetail> inbillDetailQueryWrapper = Wrappers.lambdaQuery();
           // inbillDetailQueryWrapper.select(InbillDetail::getGoodsCode, InbillDetail::getGoodsName, InbillDetail::getModel, InbillDetail::getMeasureUnit)
           //         .in(InbillDetail::getId, inbillDetailId);
           // List<InbillDetail>  inbillDetailList = InbillDetailMapper.selectList(inbillDetailQueryWrapper);

            //保存实盘数据
            for (CheckRealitygoods checkRealitygoods : list) {
                List<CheckRealitygoods> collect = inbillList.stream().filter(goods -> Objects.equals(checkRealitygoods.getPartsCode(), goods.getPartsCode())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect)) {
                    checkRealitygoods.setGoodsCode(collect.get(0).getGoodsCode());
                    checkRealitygoods.setGoodsName(collect.get(0).getGoodsName());
                    checkRealitygoods.setModel(collect.get(0).getModel());
                    checkRealitygoods.setMeasureUnit(collect.get(0).getMeasureUnit());
                }
                checkRealitygoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                checkRealitygoods.setCheckBillCode(checkBillCode);
            }
            this.saveBatch(list);
            checkNum = String.valueOf(list.size());
        } else {
            checkNum = "0";
        }
        //修改盘点详情为已盘点
        checkDetail.setCheckStatus(CheckStatusEnum.ALREADY.getCode());
        checkDetail.setCheckNum(checkNum);
        checkDetailMapper.updateById(checkDetail);
        //组装回盘任务
        List<WmsWcsInfo> infoList = new ArrayList<>();

        WmsWcsInfo returnInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
        returnInfo.put(WmsWcsInfo.START_AREA_CODE, checkDetail.getAreaCode());
        returnInfo.put(WmsWcsInfo.DOC, checkBillCode);
        returnInfo.put(WmsWcsInfo.END_AREA_CODE, checkDetail.getAreaCode());
        List<WmsWcsInfo> returnChildInfoList = new ArrayList<>();//子任务list
        returnInfo.put(WmsWcsInfo.CHILD_INFO_LIST, returnChildInfoList);
        String moveLocationCode1 = areaMapper.selectMoveLocationCodeByAreaCode(checkDetail.getAreaCode());
        Location moveLocationObj = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", moveLocationCode1));
        Location checkLocation1 = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>().eq("location_code", checkDetail.getLocationCode()));
        // if(checkLocation1.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
        //     Location parentCheckLocation = locationService.getParentLocation(checkLocation1,checkLocation1.getAreaId());
        //     if(StrUtil.isNotEmpty(parentCheckLocation.getTrayCode())){
        //         WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
        //         childInfo.put(WmsWcsInfo.TRAY_CODE, parentCheckLocation.getTrayCode());
        //         childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentCheckLocation.getLocationCode());
        //         childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode1);
        //         childInfo.put(WmsWcsInfo.DOC, checkBillCode);
        //         returnChildInfoList.add(childInfo);
        //     }
        // }
        WmsWcsInfo returnInfoMain = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode());
        returnInfoMain.put(WmsWcsInfo.TRAY_CODE, trayCode);
        returnInfoMain.put(WmsWcsInfo.START_AREA_CODE, checkDetail.getAreaCode());
        returnInfoMain.put(WmsWcsInfo.END_AREA_CODE, checkDetail.getAreaCode());
        returnInfoMain.put(WmsWcsInfo.END_LOCATION_CODE, checkDetail.getLocationCode());
        returnInfoMain.put(WmsWcsInfo.DOC, checkBillCode);
        returnChildInfoList.add(returnInfoMain);

        if(checkLocation1.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
            Location parentCheckLocation = locationService.getParentLocation(checkLocation1,checkLocation1.getAreaId());
            if(StrUtil.isNotEmpty(moveLocationObj.getTrayCode())){
                WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                childInfo.put(WmsWcsInfo.TRAY_CODE, moveLocationObj.getTrayCode());

                childInfo.put(WmsWcsInfo.START_AREA_CODE, parentCheckLocation.getAreaId());
                childInfo.put(WmsWcsInfo.END_AREA_CODE, parentCheckLocation.getAreaId());

                childInfo.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationObj.getLocationCode());
                childInfo.put(WmsWcsInfo.END_LOCATION_CODE, parentCheckLocation.getLocationCode());
                childInfo.put(WmsWcsInfo.DOC, checkBillCode);
                returnChildInfoList.add(childInfo);
            }
        }
        infoList.add(returnInfo);

        //组装取盘任务
        QueryWrapper<CheckDetail> detailQueryWrapper = new QueryWrapper<>();

        detailQueryWrapper.eq("wc.del_flag",DelFlagEnum.DEL_NO.getCode());
        detailQueryWrapper.eq("wc.check_bill_code",checkBillCode);
        detailQueryWrapper.eq("wc.area_code",checkDetail.getAreaCode());
        detailQueryWrapper.eq("wc.check_status", CheckStatusEnum.NOT.getCode());
        detailQueryWrapper.isNotNull("wc.tray_code");
        detailQueryWrapper.orderByAsc("lc.column_num","lc.layer","lc.location_type","lc.order_num");
        detailQueryWrapper.last("limit 1");
        CheckDetail checkDetailNext = checkDetailMapper.getStartCheckOne(detailQueryWrapper);

        if (checkDetailNext != null) {

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


            WmsWcsInfo info2 = new WmsWcsInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode());
            info2.put(WmsWcsInfo.TRAY_CODE, checkDetailNext.getTrayCode());
            info2.put(WmsWcsInfo.START_AREA_CODE, checkDetailNext.getAreaCode());
            info2.put(WmsWcsInfo.END_AREA_CODE, checkDetailNext.getAreaCode());
            info2.put(WmsWcsInfo.START_LOCATION_CODE, checkDetailNext.getLocationCode());
            info2.put(WmsWcsInfo.END_LOCATION_CODE, "csd");
            info2.put(WmsWcsInfo.DOC, checkBillCode);
            childInfoList.add(info2);

            // if(checkLocation.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode())){
            //     Location parentCheckLocation = locationService.getParentLocation(checkLocation,checkLocation.getAreaId());
            //     if(StrUtil.isNotEmpty(parentCheckLocation.getTrayCode()) && !checkLocations.contains(parentCheckLocation.getLocationCode())){
            //         WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
            //         childInfo.put(WmsWcsInfo.TRAY_CODE, parentCheckLocation.getTrayCode());
            //         childInfo.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
            //         childInfo.put(WmsWcsInfo.END_LOCATION_CODE, parentCheckLocation.getLocationCode());
            //         childInfo.put(WmsWcsInfo.DOC, checkBillCode);
            //         childInfoList.add(childInfo);
            //     }
            // }
            infoList.add(allInfo);
        }
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
