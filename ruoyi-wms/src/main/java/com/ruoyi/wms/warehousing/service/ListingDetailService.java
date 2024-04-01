package com.ruoyi.wms.warehousing.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
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
import com.ruoyi.wms.basics.mapper.WarehouseMapper;
import com.ruoyi.wms.basics.service.TrayService;
import com.ruoyi.wms.enums.AccountEnum;
import com.ruoyi.wms.enums.InBillEnum;
import com.ruoyi.wms.enums.ListingEnum;
import com.ruoyi.wms.enums.TakeTrayStatusEnum;
import com.ruoyi.wms.exception.LocationNotException;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.domain.WmsAccount;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.stock.service.TblstockService;
import com.ruoyi.wms.stock.service.WmsAccountService;
import com.ruoyi.wms.warehousing.domain.InBill;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.domain.ListingDetail;
import com.ruoyi.wms.warehousing.dto.InBillDto;
import com.ruoyi.wms.warehousing.mapper.*;
import com.ruoyi.wms.warehousing.vo.ListingDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 上架单详情Service接口
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@Slf4j
@Service
public class ListingDetailService extends ServiceImpl<ListingDetailMapper, ListingDetail> {

    @Autowired
    protected Validator validator;
    @Autowired
    private ListingDetailMapper listingDetailMapper;
    @Autowired
    private InbillGoodsMapper inbillGoodsMapper;
    @Autowired
    private InBillMapper inBillMapper;
    @Autowired
    private InbillDetailMapper inbillDetailMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private TblstockMapper tblstockMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private TrayService trayService;
    @Autowired
    private TblstockService tblstockService;
    @Autowired
    private WmsAccountService wmsAccountService;

    /**
     * 查询上架单详情
     *
     * @param id 上架单详情主键
     * @return 上架单详情
     */
    public ListingDetail selectListingDetailById(String id) {
        QueryWrapper<ListingDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return listingDetailMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询上架单详情
     *
     * @param ids 上架单详情 IDs
     * @return 上架单详情
     */
    public List<ListingDetail> selectListingDetailByIds(String[] ids) {
        QueryWrapper<ListingDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return listingDetailMapper.selectList(queryWrapper);
    }

    /**
     * 查询上架单详情列表
     *
     * @param listingDetail 上架单详情
     * @return 上架单详情集合
     */
    public List<ListingDetail> selectListingDetailList(ListingDetail listingDetail) {
        QueryWrapper<ListingDetail> queryWrapper = getQueryWrapper(listingDetail);
        return listingDetailMapper.select(queryWrapper);
    }

    /**
     * 新增上架单详情
     *
     * @param listingDetail 上架单详情
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ListingDetail insertListingDetail(ListingDetail listingDetail) {
        listingDetail.setId(IdUtil.simpleUUID());
        listingDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        listingDetailMapper.insert(listingDetail);
        return listingDetail;
    }

    /**
     * 批量新增上架单详情
     *
     * @param listingDetailList 上架单详情
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBatch(List<ListingDetail> listingDetailList) {
        List<ListingDetail> collect = listingDetailList.stream().map(listingDetail -> {
            listingDetail.setId(IdUtil.simpleUUID());
            listingDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            listingDetail.setListingStatus(ListingEnum.NOT.getCode());
            return listingDetail;
        }).collect(Collectors.toList());
        return super.saveBatch(collect, collect.size());
    }

    /**
     * 修改上架单详情
     *
     * @param listingDetail 上架单详情
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ListingDetail updateListingDetail(ListingDetail listingDetail) {
        listingDetailMapper.updateById(listingDetail);
        return listingDetail;
    }

    /**
     * 批量删除上架单详情
     *
     * @param ids 需要删除的上架单详情主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteListingDetailByIds(String[] ids) {
        List<ListingDetail> listingDetails = new ArrayList<>();
        for (String id : ids) {
            ListingDetail listingDetail = new ListingDetail();
            listingDetail.setId(id);
            listingDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            listingDetails.add(listingDetail);
        }
        return super.updateBatchById(listingDetails) ? 1 : 0;
    }

    /**
     * 删除上架单详情信息
     *
     * @param id 上架单详情主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteListingDetailById(String id) {
        ListingDetail listingDetail = new ListingDetail();
        listingDetail.setId(id);
        listingDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return listingDetailMapper.updateById(listingDetail);
    }

    public QueryWrapper<ListingDetail> getQueryWrapper(ListingDetail listingDetail) {
        QueryWrapper<ListingDetail> queryWrapper = new QueryWrapper<>();
        if (listingDetail != null) {
            listingDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", listingDetail.getDelFlag());
            //上架单号
            if (StrUtil.isNotEmpty(listingDetail.getListingCode())) {
                queryWrapper.eq("listing_code", listingDetail.getListingCode());
            }
            //入库单号
            if (StrUtil.isNotEmpty(listingDetail.getInBillCode())) {
                queryWrapper.eq("in_bill_code", listingDetail.getInBillCode());
            }
            //货物唯一码
            if (StrUtil.isNotEmpty(listingDetail.getOnlyCode())) {
                queryWrapper.eq("only_code", listingDetail.getOnlyCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(listingDetail.getPartsCode())) {
                queryWrapper.eq("parts_code", listingDetail.getPartsCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(listingDetail.getGoodsCode())) {
                queryWrapper.eq("goods_code", listingDetail.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(listingDetail.getGoodsName())) {
                queryWrapper.like("goods_name", listingDetail.getGoodsName());
            }
            //规格型号
            if (StrUtil.isNotEmpty(listingDetail.getModel())) {
                queryWrapper.eq("model", listingDetail.getModel());
            }
            //计量单位
            if (StrUtil.isNotEmpty(listingDetail.getMeasureUnit())) {
                queryWrapper.eq("measure_unit", listingDetail.getMeasureUnit());
            }
            //供应商编码
            if (StrUtil.isNotEmpty(listingDetail.getSupplierCode())) {
                queryWrapper.eq("supplier_code", listingDetail.getSupplierCode());
            }
            //供应商名称
            if (StrUtil.isNotEmpty(listingDetail.getSupplierName())) {
                queryWrapper.like("supplier_name", listingDetail.getSupplierName());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(listingDetail.getTrayCode())) {
                queryWrapper.eq("tray_code", listingDetail.getTrayCode());
            }
            //上架状态;(0-未上架 1-已上架)
            if (StrUtil.isNotEmpty(listingDetail.getListingStatus())) {
                queryWrapper.eq("listing_status", listingDetail.getListingStatus());
            }
            //上架时间
            if (listingDetail.getListingTime() != null) {
                queryWrapper.eq("listing_time", listingDetail.getListingTime());
            }
            //生产日期
            if (listingDetail.getProduceTime() != null) {
                queryWrapper.eq("produce_time", listingDetail.getProduceTime());
            }
            //质保期;天
            if (listingDetail.getWarranty() != null) {
                queryWrapper.eq("warranty", listingDetail.getWarranty());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param listingDetailList 模板数据
     * @param updateSupport     是否更新已经存在的数据
     * @param operName          操作人姓名
     * @return
     */
    public String importData(List<ListingDetail> listingDetailList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(listingDetailList) || listingDetailList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (ListingDetail listingDetail : listingDetailList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                ListingDetail u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, listingDetail);
                    listingDetail.setId(IdUtil.simpleUUID());
                    listingDetail.setCreateBy(operName);
                    listingDetail.setCreateTime(new Date());
                    listingDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    listingDetailMapper.insert(listingDetail);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, listingDetail);
                    //todo 验证
                    //int count = listingDetailMapper.checkCode(listingDetail);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    listingDetail.setId(u.getId());
                    listingDetail.setUpdateBy(operName);
                    listingDetail.setUpdateTime(new Date());
                    listingDetailMapper.updateById(listingDetail);
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
     * 查询未上架上架单详情列表
     *
     * @param listingDetail
     * @return 上架单详情集合
     */
    public List<ListingDetail> selectNotListingDetailList(ListingDetail listingDetail) {
        LambdaQueryWrapper<ListingDetail> listingDetailQuery = Wrappers.lambdaQuery();
        listingDetailQuery.eq(ListingDetail::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(ListingDetail::getListingStatus, ListingEnum.NOT.getCode());
        //托盘编号
        if (StringUtils.isNotEmpty(listingDetail.getTrayCode())) {
            listingDetailQuery.eq(ListingDetail::getTrayCode, listingDetail.getTrayCode());
        }
        return listingDetailMapper.selectList(listingDetailQuery);
    }


    /**
     * 查看上架数据
     *
     * @param listingDetail
     * @return 结果
     */
    public List<ListingDetailVo> selectPutOnData(ListingDetail listingDetail) {
        QueryWrapper<ListingDetail> listingDetailQuery = new QueryWrapper<>();
        listingDetailQuery
                .eq("wld.in_bill_code", listingDetail.getInBillCode())
                .eq("wld.listing_status", listingDetail.getListingStatus())
                .eq("wld.del_flag", DelFlagEnum.DEL_NO.getCode()).groupBy("wld.tray_code").orderByDesc("wld.create_time");
        return listingDetailMapper.selectPutOnData(listingDetailQuery);
    }


    /**
     * 将托盘上架
     * @param wmsWcsInfo
     * @return
     */
    public WmsWcsInfo putOnTray(WmsWcsInfo wmsWcsInfo) {
        try {

            String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编号
            String endLocationCode = (String)wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE);//end库位编号
            LambdaQueryWrapper<Location> locationQuery = Wrappers.lambdaQuery();
            locationQuery.select(Location::getLocationName,Location::getAreaId,Location::getWarehouseId)
                    .eq(Location::getLocationCode,endLocationCode)
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .last("limit 1");
            Location location = locationMapper.selectOne(locationQuery);//end库位
            String areaCode = location.getAreaId();//库区编码
            LambdaQueryWrapper<Area> areaQuery = Wrappers.lambdaQuery();
            areaQuery.select(Area::getAreaName)
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Area::getAreaCode,areaCode)
                    .last("limit 1");
            String areaName = areaMapper.selectOne(areaQuery).getAreaName();//库区名称
            String warehouseCode = location.getWarehouseId();//仓库编码
            LambdaQueryWrapper<Warehouse> warehouseQuery = Wrappers.lambdaQuery();
            warehouseQuery.select(Warehouse::getWarehouseName)
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Warehouse::getWarehouseCode,warehouseCode)
                    .last("limit 1");
            String warehouseName = warehouseMapper.selectOne(warehouseQuery).getWarehouseName();//仓库名称
            Date listingTime = new Date();//上架时间
            //查询托盘上上架中的上架单详情
            LambdaQueryWrapper<ListingDetail> listingDetailQuery = Wrappers.lambdaQuery();
            listingDetailQuery.eq(ListingDetail::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(ListingDetail::getListingStatus, ListingEnum.ING.getCode())
                    .eq(ListingDetail::getTrayCode, trayCode);
            List<ListingDetail> listingDetailList = listingDetailMapper.selectList(listingDetailQuery);

            String inBillCode = null;//入库单号
            if(CollectionUtil.isNotEmpty(listingDetailList)){
                inBillCode = listingDetailList.get(0).getInBillCode();//入库单号
            }
            //添加库存数据
            List<Tblstock> tblstockList = new ArrayList<>();
            for (ListingDetail lD : listingDetailList) {
                Tblstock tblstock = new Tblstock();
                tblstock.setOnlyCode(lD.getOnlyCode());
                tblstock.setPartsCode(lD.getPartsCode());
                tblstock.setGoodsCode(lD.getGoodsCode());
                tblstock.setGoodsName(lD.getGoodsName());
                tblstock.setModel(lD.getModel());
                tblstock.setMeasureUnit(lD.getMeasureUnit());
                tblstock.setCharg(lD.getCharg());
                tblstock.setSupplierCode(lD.getSupplierCode());
                tblstock.setSupplierName(lD.getSupplierName());
                tblstock.setWarehouseCode(warehouseCode);
                tblstock.setWarehouseName(warehouseName);
                tblstock.setAreaCode(areaCode);
                tblstock.setAreaName(areaName);
                tblstock.setLocationCode(endLocationCode);
                tblstock.setLocationName(location.getLocationName());
                tblstock.setTrayCode(lD.getTrayCode());
                tblstock.setProduceTime(lD.getProduceTime());
                tblstock.setWarranty(lD.getWarranty());
                tblstock.setLockStatus(LockEnum.NOTLOCK.getCode());
                tblstock.setPeriodValidity(lD.getPeriodValidity());
                tblstock.setListingTime(listingTime);
                tblstockList.add(tblstock);
            }
            //新增库存数据
            if (tblstockList.size() > 0) {
                tblstockService.saveBatch(tblstockList);
            }
            //修改上架单详情上架状态为已上架
            List<String> listingDetailIdList = listingDetailList.stream().map(ListingDetail::getId).collect(Collectors.toList());
            LambdaUpdateWrapper<ListingDetail> listingDetailUpdate = Wrappers.lambdaUpdate();
            listingDetailUpdate.set(ListingDetail::getListingStatus, ListingEnum.ALREADY.getCode())
                    .set(ListingDetail::getListingTime, listingTime)
                    .set(ListingDetail::getLocationCode, wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE))
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(ListingDetail::getId, listingDetailIdList);
            listingDetailMapper.update(null, listingDetailUpdate);
            if(StrUtil.isNotEmpty(inBillCode)){
                //根据组盘、上架情况修改入库单抬头状态为已上架
                LambdaQueryWrapper<InbillGoods> inbillGoodsQuery = Wrappers.lambdaQuery();
                inbillGoodsQuery.eq(InbillGoods::getInBillCode, inBillCode)
                        .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .isNull(InbillGoods::getTrayCode);
                LambdaUpdateWrapper<InbillGoods> inbillGoodsLambdaUpdateWrapper = Wrappers.lambdaUpdate();
                inbillGoodsLambdaUpdateWrapper.set(InbillGoods::getLocationCode,endLocationCode)
                        .eq(InbillGoods::getTrayCode,trayCode)
                        .eq(InbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                inbillGoodsMapper.update(null,inbillGoodsLambdaUpdateWrapper);
                //不存在未组盘数据
                if (inbillGoodsMapper.selectCount(inbillGoodsQuery) == 0) {
                    //查询上架状态不为已上架的数据
                    listingDetailQuery.clear();
                    listingDetailQuery.eq(ListingDetail::getInBillCode, inBillCode)
                            .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                            .ne(ListingDetail::getListingStatus, ListingEnum.ALREADY.getCode());
                    //不存在没有上架的数据
                    if (listingDetailMapper.selectCount(listingDetailQuery) == 0) {//不存在没有上架的数据
                        //修改入库单抬头状态为已上架
                        LambdaUpdateWrapper<InBill> inBillUpdate = Wrappers.lambdaUpdate();
                        inBillUpdate.set(InBill::getInBillStatus, InBillEnum.PUTONED.getCode())
                                .set(InBill::getTakeTrayStatus, TakeTrayStatusEnum.ALREADY.getCode())
                                .set(BaseEntity::getUpdateTime,new Date())
                                .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                                .eq(InBill::getInBillCode, inBillCode);
                        inBillMapper.update(null, inBillUpdate);
                        //添加库存台账
                        LambdaQueryWrapper<InBill> inBillQueryWrapper = Wrappers.lambdaQuery();
                        inBillQueryWrapper.eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                                .eq(InBill::getInBillCode,inBillCode)
                                .last("limit 1");
                        //入库单抬头
                        InBill inBill = inBillMapper.selectOne(inBillQueryWrapper);
                        LambdaQueryWrapper<InbillDetail> inbillDetailQueryWrapper = Wrappers.lambdaQuery();
                        inbillDetailQueryWrapper.eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                                .eq(InbillDetail::getInBillCode,inBillCode);
                        //入库单详情
                        List<InbillDetail> inbillDetailList = inbillDetailMapper.selectList(inbillDetailQueryWrapper);
                        //新增台账
                        List<WmsAccount> accountList = new ArrayList<>();
                        for (InbillDetail inbillDetail:inbillDetailList) {
                            WmsAccount account = new WmsAccount();
                            account.setId(IdUtil.simpleUUID());
                            account.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                            account.setAccountCode(inBillCode);
                            account.setCodeType(AccountEnum.RKD.getCode());
                            account.setCharg(inBill.getCharg());
                            //本次变动数量
                            account.setChangeNum(String.valueOf(inbillDetail.getInBillNum().setScale( 0, BigDecimal.ROUND_DOWN )));
                            //结存量
                            LambdaQueryWrapper<Tblstock> tblstockQueryWrapper = Wrappers.lambdaQuery();
                            tblstockQueryWrapper.eq(Tblstock::getGoodsCode,inbillDetail.getGoodsCode())
                                    .eq(Tblstock::getCharg,inBill.getCharg())
                                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                            Long aLong = tblstockMapper.selectCount(tblstockQueryWrapper);
                            account.setStockNum(String.valueOf(aLong));
                            account.setGoodsCode(inbillDetail.getGoodsCode());
                            account.setGoodsName(inbillDetail.getGoodsName());
                            account.setModel(inbillDetail.getModel());
                            account.setMeasureUnit(inbillDetail.getMeasureUnit());
                            accountList.add(account);
                        }
                        if (CollUtil.isNotEmpty(accountList)){
                            wmsAccountService.saveBatch(accountList,accountList.size());
                        }


                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return wmsWcsInfo;
    }

    /**
     * 待上架列表
     */
    public List<InBillDto> waitListingList(InBill map) {
        List<InBillDto> returnList = new ArrayList<>();
        //入库单号
        String inBillCode = map.getInBillCode();
        LambdaQueryWrapper<InBill> inBillQuery = Wrappers.lambdaQuery();
        inBillQuery.select(InBill::getInBillCode,InBill::getInBillNum)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(InBill::getInBillStatus, InBillEnum.PUTONING.getCode());
        //根据入库单号筛选
        if (StringUtils.isNotEmpty(inBillCode)) {
            inBillQuery.like(InBill::getInBillCode, inBillCode);
        }
        List<InBill> inBillList = inBillMapper.selectList(inBillQuery);
        for (InBill inBill : inBillList) {
            InBillDto returnMap = new InBillDto();
            //入库单号
            returnMap.setInBillCode(inBill.getInBillCode());
            //入库验收数量
            returnMap.setInBillNum(inBill.getInBillNum());
            //查询入库单已上架数量
            LambdaQueryWrapper<ListingDetail> listingDetailQuery = Wrappers.lambdaQuery();
            listingDetailQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(ListingDetail::getInBillCode, inBill.getInBillCode())
                    .eq(ListingDetail::getListingStatus, ListingEnum.ALREADY.getCode());
            Long aLong = listingDetailMapper.selectCount(listingDetailQuery);
            //已上架数量
            returnMap.setListingNum(aLong);
            //未上架数量
            returnMap.setNotListingNum(inBill.getInBillNum().longValue() - aLong);
            returnList.add(returnMap);
        }
        return returnList;
    }

}

