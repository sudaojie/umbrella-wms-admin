package com.ruoyi.wms.check.service;

import java.math.BigDecimal;
import java.util.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.enums.PrintStatusEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.PdfUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysFileService;
import com.ruoyi.wms.basics.domain.*;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.basics.service.GoodsInfoService;
import com.ruoyi.wms.basics.service.TempService;
import com.ruoyi.wms.check.domain.CheckAdjustDetail;
import com.ruoyi.wms.check.domain.CheckAdjustGoods;
import com.ruoyi.wms.check.dto.CheckAdjustDto;
import com.ruoyi.wms.check.mapper.CheckAdjustDetailMapper;
import com.ruoyi.wms.check.mapper.CheckAdjustGoodsMapper;
import com.ruoyi.wms.enums.*;
import com.ruoyi.wms.outbound.domain.OutBill;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import com.ruoyi.wms.outbound.mapper.OutBillMapper;
import com.ruoyi.wms.outbound.service.OutbillGoodsService;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.stock.service.TblstockService;
import com.ruoyi.wms.utils.SerialCodeUtils;
import com.ruoyi.wms.warehousing.domain.InBill;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.mapper.InBillMapper;
import com.ruoyi.wms.warehousing.service.InbillDetailService;
import com.ruoyi.wms.warehousing.service.InbillGoodsService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.check.domain.CheckAdjust;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.check.mapper.CheckAdjustMapper;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 库存盘点调整单Service接口
 *
 * @author nf
 * @date 2023-03-23
 */
@Slf4j
@Service
public class CheckAdjustService extends ServiceImpl<CheckAdjustMapper, CheckAdjust> {

    @Autowired
    private CheckAdjustMapper checkAdjustMapper;
    @Autowired
    private CheckAdjustDetailMapper checkAdjustDetailMapper;
    @Autowired
    private CheckAdjustGoodsMapper checkAdjustGoodsMapper;
    @Autowired
    private GoodsInfoService goodsInfoService;
    @Autowired
    protected Validator validator;
    @Autowired
    private ISysFileService sysFileService;
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private TempService tempService;
    @Autowired
    private SerialCodeUtils serialCodeUtils;
    @Autowired
    private InBillMapper inBillMapper;
    @Autowired
    private InbillDetailService inbillDetailService;
    @Autowired
    private InbillGoodsService inbillGoodsService;
    @Autowired
    private OutBillMapper outBillMapper;
    @Autowired
    private OutbillGoodsService outbillGoodsService;
    @Autowired
    private TblstockMapper tblstockMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private TblstockService tblstockService;
    @Autowired
    private TrayMapper trayMapper;
    /**
     * 查询库存盘点调整单
     *
     * @param id 库存盘点调整单主键
     * @return 库存盘点调整单
     */
    public CheckAdjust selectCheckAdjustById(String id){
        QueryWrapper<CheckAdjust> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        CheckAdjust checkAdjust = checkAdjustMapper.selectOne(queryWrapper);
        LambdaQueryWrapper<CheckAdjustDetail> queryWrapper1 = Wrappers.lambdaQuery();
        queryWrapper1.eq(CheckAdjustDetail::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .eq(CheckAdjustDetail::getCheckBillCode,checkAdjust.getCheckBillCode());
        List<CheckAdjustDetail> details = checkAdjustDetailMapper.selectList(queryWrapper1);
        checkAdjust.setCheckDetailList(details);
        return checkAdjust;
    }

    /**
     * 查询库存盘点调整单列表
     *
     * @param checkAdjust 库存盘点调整单
     * @return 库存盘点调整单集合
     */
    public List<CheckAdjust> selectCheckAdjustList(CheckAdjust checkAdjust){
        QueryWrapper<CheckAdjust> queryWrapper = getQueryWrapper(checkAdjust);
        return checkAdjustMapper.select(queryWrapper);
    }


    /**
     * 修改库存盘点调整单（只有处理功能，改状态并生成对应的出入库单据）
     *
     * @param checkAdjust 库存盘点调整单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckAdjust updateCheckAdjust(CheckAdjust checkAdjust){

        if(StringUtils.isEmpty(checkAdjust.getId())){
            throw new ServiceException("处理失败，数据主键id缺失");
        }
        QueryWrapper<CheckAdjust> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", checkAdjust.getId());
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        CheckAdjust adjust = checkAdjustMapper.selectOne(queryWrapper);
        if(adjust.getAdjustStatus().equals(AdjustStatusEnum.end.getCode())){
            throw new ServiceException("该数据已处理，不可重复处理");
        }
        //查询生成单据的数据
        //调整单详情数据
        LambdaQueryWrapper<CheckAdjustDetail> detailLambdaQueryWrapper = Wrappers.lambdaQuery();
        detailLambdaQueryWrapper.eq(CheckAdjustDetail::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .eq(CheckAdjustDetail::getCheckBillCode,adjust.getCheckBillCode());
        List<CheckAdjustDetail> detailList = checkAdjustDetailMapper.selectList(detailLambdaQueryWrapper);
        //调整单数据详情的id集合
        List<String> detailIdList = detailList.stream().map(detail->detail.getId()).collect(Collectors.toList());
        //调整单详情货物数据
        LambdaQueryWrapper<CheckAdjustGoods> goodsLambdaQueryWrapper = Wrappers.lambdaQuery();
        goodsLambdaQueryWrapper.eq(CheckAdjustGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .in(CheckAdjustGoods::getCheckAdjustDetail,detailIdList);
        List<CheckAdjustGoods> goodsList = checkAdjustGoodsMapper.selectList(goodsLambdaQueryWrapper);

        if(CollectionUtil.isNotEmpty(goodsList)){
            List<String> partsCodes = goodsList.stream().map(goods->goods.getPartsCode()).collect(Collectors.toList());
            GoodsInfo goodsInfo = new GoodsInfo();
            goodsInfo.setEnableStatus(EnableStatus.ENABLE.getCode());
            List<GoodsInfo> goodsInfos = goodsInfoService.selectGoodsInfoList(goodsInfo);
            //生成出入库单据
            String inBillCode = serialCodeUtils.getOrderNo(InBillNoPrefixEnum.getPrefix(InBillNoPrefixEnum.PYRK.getCode()));
            String outBillCode = serialCodeUtils.getOrderNo(OutBillNoPrefixEnum.getPrefix(OutBillNoPrefixEnum.PKCK.getCode()));
            String charg = serialCodeUtils.getCharg();
            InBill inBill = new InBill(IdUtil.simpleUUID(),inBillCode,charg, InBillEnum.PUTONED.getCode(),InBillNoPrefixEnum.PYRK.getCode(),
                    DelFlagEnum.DEL_NO.getCode(), SecurityUtils.getUsername());
            List<InbillDetail> inbillDetails = new ArrayList<>();
            List<InbillGoods> inbillGoods = new ArrayList<>();
            OutBill outBill = new OutBill(IdUtil.simpleUUID(),outBillCode,new Date(),OutBillEnum.ALREADY.getCode(),OutBillNoPrefixEnum.PKCK.getCode());
            List<OutbillGoods> outbillGoods = new ArrayList<>();
            //根据机件号查询库存数据
            LambdaQueryWrapper<Tblstock> tblstockLambdaQueryWrapper = Wrappers.lambdaQuery();
            tblstockLambdaQueryWrapper.in(Tblstock::getPartsCode,partsCodes);
            List<Tblstock> tblstocks =tblstockMapper.selectList(tblstockLambdaQueryWrapper);
            //盘盈数据
            List<Tblstock> profitList = new ArrayList<>();
            //盘亏数据
            List<Tblstock> lossList = new ArrayList<>();
            //入库详情单生成
            for (CheckAdjustDetail detail:detailList) {
                for (GoodsInfo goods: goodsInfos) {
                    List<String> goodsCodes = inbillDetails.stream().map(inbillDetail->inbillDetail.getGoodsCode()).collect(Collectors.toList());
                    if(Double.valueOf(detail.getProfitNum())>0&&detail.getGoodsCode().equals(goods.getGoodsCode())&&!goodsCodes.contains(detail.getGoodsCode())){
                        InbillDetail inbillDetail = new InbillDetail(IdUtil.simpleUUID(),inBillCode,detail.getGoodsCode(),detail.getGoodsName(),goods.getMeasureUnit(),goods.getWeight()
                                ,goods.getVolume(),goods.getModel(),null,null,Long.valueOf(goods.getWarranty()+""));
                        inbillDetail.setInBillNum(BigDecimal.ZERO);
                        inbillDetail.setReportNum(BigDecimal.ZERO);
                        inbillDetails.add(inbillDetail);
                        break;
                    }
                }
            }
            List<String> oldPartsCodes = tblstocks.stream().map(tbl->tbl.getPartsCode()).collect(Collectors.toList());
            //货物出入库详情货物生成(带机件号的)
            for (CheckAdjustGoods goods : goodsList) {
                //盘盈的情况
                if(Double.valueOf(goods.getProfitNum())>0){
                    //原有数据
                    if(CollectionUtil.isNotEmpty(tblstocks)){
                        for (Tblstock tblstock:tblstocks) {
                            if(tblstock.getPartsCode().equals(goods.getPartsCode())){
                                tblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                                tblstock.setCharg(null);
                                Location location = locationMapper.selectDataByCode(goods.getLocationCode());
                                Area area = areaMapper.selectDataByCode(location.getAreaId());
                                tblstock.setWarehouseCode(location.getWarehouseId());
                                tblstock.setAreaCode(location.getAreaId());
                                tblstock.setAreaName(area.getAreaName());
                                tblstock.setLocationCode(location.getLocationCode());
                                tblstock.setLocationName(location.getLocationName());
                                tblstock.setTrayCode(goods.getTrayCode());
                                profitList.add(tblstock);
                            }
                        }
                        //部分没有的数据
                        if(!oldPartsCodes.contains(goods.getPartsCode())){
                            Tblstock tblstock = new Tblstock();
                            tblstock.setOnlyCode(goods.getOnlyCode());
                            tblstock.setPartsCode(goods.getPartsCode());
                            tblstock.setGoodsCode(goods.getGoodsCode());
                            tblstock.setGoodsName(goods.getGoodsName());
                            tblstock.setModel(goods.getModel());
                            tblstock.setMeasureUnit(goods.getMeasureUnit());
                            tblstock.setCharg(charg);
                            tblstock.setSupplierCode(null);
                            tblstock.setSupplierName(null);
                            Location location = locationMapper.selectDataByCode(goods.getLocationCode());
                            Area area = areaMapper.selectDataByCode(location.getAreaId());
                            tblstock.setWarehouseCode(location.getWarehouseId());
                            tblstock.setAreaCode(area.getAreaCode());
                            tblstock.setAreaName(area.getAreaName());
                            tblstock.setLocationCode(location.getLocationCode());
                            tblstock.setLocationName(location.getLocationName());
                            tblstock.setTrayCode(goods.getTrayCode());
                            tblstock.setProduceTime(new Date());
                            tblstock.setWarranty(0L);
                            tblstock.setLockStatus(LockEnum.NOTLOCK.getCode());
                            tblstock.setPeriodValidity(new Date());
                            tblstock.setListingTime(new Date());
                            tblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                            profitList.add(tblstock);
                        }
                    //原来没有的数据
                    }else{
                        Tblstock tblstock = new Tblstock();
                        tblstock.setOnlyCode(goods.getOnlyCode());
                        tblstock.setPartsCode(goods.getPartsCode());
                        tblstock.setGoodsCode(goods.getGoodsCode());
                        tblstock.setGoodsName(goods.getGoodsName());
                        tblstock.setModel(goods.getModel());
                        tblstock.setMeasureUnit(goods.getMeasureUnit());
                        tblstock.setCharg(charg);
                        tblstock.setSupplierCode(null);
                        tblstock.setSupplierName(null);
                        Location location = locationMapper.selectDataByCode(goods.getLocationCode());
                        Area area = areaMapper.selectDataByCode(location.getAreaId());
                        tblstock.setWarehouseCode(location.getWarehouseId());
                        tblstock.setAreaCode(area.getAreaCode());
                        tblstock.setAreaName(area.getAreaName());
                        tblstock.setLocationCode(location.getLocationCode());
                        tblstock.setLocationName(location.getLocationName());
                        tblstock.setTrayCode(goods.getTrayCode());
                        tblstock.setProduceTime(new Date());
                        tblstock.setWarranty(0L);
                        tblstock.setLockStatus(LockEnum.NOTLOCK.getCode());
                        tblstock.setPeriodValidity(new Date());
                        tblstock.setListingTime(new Date());
                        tblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                        profitList.add(tblstock);
                    }
                    for (InbillDetail inbillDetail:inbillDetails) {
                        if(inbillDetail.getGoodsCode().equals(goods.getGoodsCode())){
                            InbillGoods inbillGood = new InbillGoods(IdUtil.simpleUUID(),inBillCode,inbillDetail.getId(),goods.getOnlyCode(),goods.getPartsCode()
                                    ,goods.getLocationCode(),goods.getTrayCode(),PrintStatusEnum.PRINT_YES.getCode(),BigDecimal.ONE,new Date(),new Date());
                            inbillDetail.setReportNum(inbillDetail.getReportNum().add(BigDecimal.ONE));
                            inbillDetail.setInBillNum(inbillDetail.getInBillNum().add(BigDecimal.ONE));
                            LambdaQueryWrapper<InbillGoods> inbillGoodsWrapper = new LambdaQueryWrapper<>();
                            inbillGoodsWrapper.eq(InbillGoods::getOnlyCode,goods.getOnlyCode());
                            inbillGoodsWrapper.eq(InbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                            inbillGoodsWrapper.eq(InbillGoods::getPrintStatus,PartsEnum.NOT.getCode());
                            InbillGoods inbillGoods1 = inbillGoodsService.getBaseMapper().selectOne(inbillGoodsWrapper);

                            LambdaQueryWrapper<InbillDetail> lambdaInbillDetailWrapper = new LambdaQueryWrapper<>();
                            lambdaInbillDetailWrapper.eq(InbillDetail::getId,inbillGoods1.getInbillDetailId());
                            lambdaInbillDetailWrapper.eq(InbillDetail::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                            InbillDetail inbillDetail1 = inbillDetailService.getBaseMapper().selectOne(lambdaInbillDetailWrapper);

                            inbillDetail.setSupplierCode(inbillDetail1.getSupplierCode());
                            inbillDetail.setSupplierName(inbillDetail1.getSupplierName());
                            inBill.setWeight(inbillDetail.getWeight().multiply(inbillDetail.getReportNum()));
                            inBill.setVolume(inbillDetail.getVolume().multiply(inbillDetail.getReportNum()));
                            inbillGoods.add(inbillGood);
                        }
                    }
                //盘亏的情况
                }else if(Double.valueOf(goods.getLossNum())>0){
                    for (Tblstock tblstock:tblstocks) {
                        if(tblstock.getPartsCode().equals(goods.getPartsCode())){
                            Tblstock lossStock = new Tblstock();
                            BeanUtil.copyProperties(tblstock,lossStock);
                            lossStock.setDelFlag(DelFlagEnum.DEL_YES.getCode());
                            lossList.add(lossStock);
                        }
                    }
                    OutbillGoods outbillGood = new OutbillGoods(IdUtil.simpleUUID(),outBillCode,goods.getOnlyCode(),goods.getPartsCode(),goods.getGoodsCode(),goods.getGoodsName()
                            ,goods.getModel(),goods.getMeasureUnit(), charg,null,null,goods.getTrayCode(),BigDecimal.ONE,OutBillGoodsEnum.PICKED.getCode()
                            ,new Date());
                    outbillGoods.add(outbillGood);
                }
            }

            if(outbillGoods.size()>0){
                outBillMapper.insert(outBill);
                outbillGoodsService.saveOrUpdateBatch(outbillGoods);
                if(lossList.size()>0){
                    tblstockService.updateBatchById(lossList);
                    List<String> trayCodes = lossList.stream().map(tblstock -> tblstock.getTrayCode()).distinct().collect(Collectors.toList());
                    trayMapper.updateTrayByCodes(trayCodes);
                }
            }

            //执行单据存储以及更新库存总览
            if(inbillGoods.size()>0){
                inBillMapper.insert(inBill);
                inbillDetailService.saveOrUpdateBatch(inbillDetails);
                inbillGoodsService.saveOrUpdateBatch(inbillGoods);
                if(profitList.size()>0){
                    tblstockService.saveOrUpdateBatch(profitList);
                }
            }

        }
        checkAdjust.setAdjustStatus(AdjustStatusEnum.end.getCode());
        checkAdjustMapper.updateById(checkAdjust);
        return checkAdjust;
    }




    public QueryWrapper<CheckAdjust> getQueryWrapper(CheckAdjust checkAdjust) {
        QueryWrapper<CheckAdjust> queryWrapper = new QueryWrapper<>();
        if (checkAdjust != null) {
            checkAdjust.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",checkAdjust.getDelFlag());
            //盘点单号
            if (StrUtil.isNotEmpty(checkAdjust.getCheckBillCode())) {
                queryWrapper.like("check_bill_code",checkAdjust.getCheckBillCode());
            }
            //调整状态
            if (StrUtil.isNotEmpty(checkAdjust.getAdjustStatus())) {
                queryWrapper.eq("adjust_status",checkAdjust.getAdjustStatus());
            }
            //盘点人
            if (StrUtil.isNotEmpty(checkAdjust.getCheckBy())) {
                queryWrapper.like("check_by",checkAdjust.getCheckBy());
            }
            //创建人
            if (StrUtil.isNotEmpty(checkAdjust.getCreateBy())) {
                queryWrapper.like("create_by",checkAdjust.getCreateBy());
            }
            //创建时间
            if (StringUtils.isNotEmpty(checkAdjust.getParams())&&StringUtils.isNotNull(checkAdjust.getParams().get("beginCreateTime"))) {
                String begin = checkAdjust.getParams().get("beginCreateTime")+" 00:00:00";
                String end = checkAdjust.getParams().get("endCreateTime")+" 23:59:59";
                queryWrapper.between("create_time",begin,end);
            }
            //处理人
            if (StrUtil.isNotEmpty(checkAdjust.getUpdateBy())) {
                queryWrapper.like("update_by",checkAdjust.getUpdateBy());
            }
            //处理时间
            if (checkAdjust.getUpdateTime() != null) {
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param checkAdjustDto 模板数据
     * @param responseBody 请求响应
     * @return
     */
    public void downloadPDF(CheckAdjustDto checkAdjustDto, HttpServletResponse responseBody) {
        Map checkAdjust = BeanUtil.beanToMap(checkAdjustDto);
        String tempId = configService.selectConfigByKey("wms.checkAdjust.tempId");
        Temp temp = tempService.selectWmsWarehouseTempByTempId(tempId);
        if(temp == null){
            throw new ServiceException("未查询到PDF模板文件");
        }
        SysFile file =  sysFileService.selectSysFileById(temp.getFileKey());
        PdfUtil.createPDF(file.getPath(),checkAdjust,responseBody);
    }
}
