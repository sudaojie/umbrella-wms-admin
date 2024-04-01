package com.ruoyi.wms.warehousing.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.visitor.functions.If;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wcs.mapper.WcsOperateTaskMapper;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.Tray;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.basics.mapper.ViewPageMapper;
import com.ruoyi.wms.basics.service.TrayService;
import com.ruoyi.wms.basics.vo.ViewPageGoodsVo;
import com.ruoyi.wms.enums.InBillEnum;
import com.ruoyi.wms.enums.IsEmptyEnum;
import com.ruoyi.wms.enums.ListingNoPrefixEnum;
import com.ruoyi.wms.enums.TakeTrayStatusEnum;
import com.ruoyi.wms.utils.SerialCodeUtils;
import com.ruoyi.wms.warehousing.domain.*;
import com.ruoyi.wms.warehousing.dto.GroupDiskDto;
import com.ruoyi.wms.warehousing.dto.ReleaseTrayDto;
import com.ruoyi.wms.warehousing.mapper.InBillMapper;
import com.ruoyi.wms.warehousing.mapper.InbillDetailMapper;
import com.ruoyi.wms.warehousing.mapper.InbillGoodsMapper;
import com.ruoyi.wms.warehousing.mapper.ListingDetailMapper;
import com.ruoyi.wms.warehousing.vo.OnlyCodeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 入库单详情信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-01
 */
@Slf4j
@Service
public class InbillDetailService extends ServiceImpl<InbillDetailMapper, InbillDetail> {

    @Autowired
    private InbillDetailMapper inbillDetailMapper;
    @Autowired
    private InBillMapper inBillMapper;
    @Autowired
    private InbillGoodsMapper inbillGoodsMapper;
    @Autowired
    private TrayMapper trayMapper;
    @Autowired
    private InbillGoodsService inbillGoodsService;
    @Autowired
    private ListingListService listingListService;
    @Autowired
    private ListingDetailService listingDetailService;
    @Autowired
    private ListingDetailMapper listingDetailMapper;
    @Autowired
    private PartsService partsService;
    @Autowired
    private TrayService trayService;
    @Autowired
    private SerialCodeUtils serialCodeUtils;
    @Autowired
    private WcsOperateTaskMapper wcsOperateTaskMapper;
    @Autowired
    private LocationMapper locationMapper;

    @Autowired(required = false)
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    @Autowired(required = false)
    private ViewPageMapper viewPageMapper;
    /**
     * 查询入库单详情信息
     *
     * @param id 入库单详情信息主键
     * @return 入库单详情信息
     */
    public InbillDetail selectInbillDetailById(String id) {
        QueryWrapper<InbillDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return inbillDetailMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询入库单详情信息
     *
     * @param ids 入库单详情信息 IDs
     * @return 入库单详情信息
     */
    public List<InbillDetail> selectInbillDetailByIds(String[] ids) {
        QueryWrapper<InbillDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return inbillDetailMapper.selectList(queryWrapper);
    }

    /**
     * 查询入库单详情信息列表
     *
     * @param inbillDetail 入库单详情信息
     * @return 入库单详情信息集合
     */
    public List<InbillDetail> selectInbillDetailList(InbillDetail inbillDetail) {
        QueryWrapper<InbillDetail> queryWrapper = getQueryWrapper(inbillDetail);
        return inbillDetailMapper.select(queryWrapper);
    }

    /**
     * 查询入库单详情信息列表
     *
     * @param inbillDetail 入库单详情信息
     * @return 入库单详情信息集合
     */
    public List<InbillDetail> aiTakeTrayList(InbillDetail inbillDetail) {
        QueryWrapper<InbillDetail> queryWrapper = getQueryWrapper(inbillDetail);
        List<InbillDetail> list = inbillDetailMapper.select(queryWrapper);
        for (InbillDetail detail : list) {
            if (detail.getInBillNum() != null) {
                long inBillNum = detail.getInBillNum().setScale(0, BigDecimal.ROUND_UP).longValue();
                Long num = detail.getNum();
                Long takedTrayCount = detail.getTakedTrayCount();
                long trayCount = inBillNum / num;
                if (inBillNum % num > 0L) {
                    trayCount += 1L;
                }
                detail.setTrayCount((trayCount - takedTrayCount) > 0L ? trayCount - takedTrayCount : 0L);
            }
        }
        return list;
    }

    /**
     * 新增入库单详情信息
     *
     * @param inbillDetail 入库单详情信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public InbillDetail insertInbillDetail(InbillDetail inbillDetail) {
        inbillDetail.setId(IdUtil.simpleUUID());
        inbillDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        inbillDetail.setTakedTrayCount(0L);
        inbillDetailMapper.insert(inbillDetail);
        return inbillDetail;
    }

    /**
     * 批量新增入库单详情信息
     *
     * @param inbillDetailList 入库单详情信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<InbillDetail> inbillDetailList) {
        List<InbillDetail> collect = inbillDetailList.stream().map(inbillDetail -> {
            inbillDetail.setId(IdUtil.simpleUUID());
            inbillDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            inbillDetail.setTakedTrayCount(0L);
            return inbillDetail;
        }).collect(Collectors.toList());
        return this.saveBatch(collect, collect.size());
    }

    /**
     * 修改入库单详情信息
     *
     * @param inbillDetail 入库单详情信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public InbillDetail updateInbillDetail(InbillDetail inbillDetail) {
        inbillDetailMapper.updateById(inbillDetail);
        return inbillDetail;
    }

    /**
     * 批量删除入库单详情信息
     *
     * @param ids 需要删除的入库单详情信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteInbillDetailByIds(String[] ids) {
        List<InbillDetail> inbillDetails = new ArrayList<>();
        for (String id : ids) {
            InbillDetail inbillDetail = new InbillDetail();
            inbillDetail.setId(id);
            inbillDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            inbillDetails.add(inbillDetail);
        }
        BigDecimal weight = new BigDecimal("0.00");
        BigDecimal volume = new BigDecimal("0.00");
        InbillDetail detail1 = inbillDetailMapper.selectById(ids[0]);
        QueryWrapper<InBill> inBillQueryWrapper = new QueryWrapper<>();
        inBillQueryWrapper.eq("in_bill_code", detail1.getInBillCode());
        inBillQueryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        InBill inBill = inBillMapper.selectOne(inBillQueryWrapper);
        LambdaQueryWrapper<InbillDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InbillDetail::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(InbillDetail::getInBillCode, detail1.getInBillCode());
        List<InbillDetail> dataList = inbillDetailMapper.selectList(queryWrapper);
        boolean b = super.removeByIds(inbillDetails);
        for (InbillDetail detail : dataList) {
            //入库单货物重量累加
            weight = weight.add(detail.getReportNum().multiply(detail.getWeight()));
            //入库单体货物积累加
            volume = volume.add(detail.getReportNum().multiply(detail.getVolume()));
        }
        inBill.setVolume(volume);
        inBill.setWeight(weight);
        inBillMapper.updateById(inBill);
        return b ? 1 : 0;
    }

    /**
     * 删除入库单详情信息信息
     *
     * @param id 入库单详情信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteInbillDetailById(String id) {
        InbillDetail inbillDetail = new InbillDetail();
        inbillDetail.setId(id);
        inbillDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return inbillDetailMapper.updateById(inbillDetail);
    }

    public QueryWrapper<InbillDetail> getQueryWrapper(InbillDetail inbillDetail) {
        QueryWrapper<InbillDetail> queryWrapper = new QueryWrapper<>();
        if (inbillDetail != null) {
            //入库单号
            if (StrUtil.isNotEmpty(inbillDetail.getInBillCode())) {
                queryWrapper.eq("in_bill_code", inbillDetail.getInBillCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(inbillDetail.getGoodsCode())) {
//                queryWrapper.eq("goods_code", inbillDetail.getGoodsCode());
                queryWrapper.and(QueryWrapper -> {
                    QueryWrapper.like("goods_code", inbillDetail.getGoodsCode()).or();
                    QueryWrapper.like("goods_name", inbillDetail.getGoodsCode());
                });
            }
            //货物名称
            if (StrUtil.isNotEmpty(inbillDetail.getGoodsName())) {
                queryWrapper.like("goods_name", inbillDetail.getGoodsName());
            }
            //计量单位
            if (StrUtil.isNotEmpty(inbillDetail.getMeasureUnit())) {
                queryWrapper.eq("measure_unit", inbillDetail.getMeasureUnit());
            }
            //重量(kg)
            if (inbillDetail.getWeight() != null) {
                queryWrapper.eq("weight", inbillDetail.getWeight());
            }
            //体积(m³)
            if (inbillDetail.getVolume() != null) {
                queryWrapper.eq("volume", inbillDetail.getVolume());
            }
            //预报数量
            if (inbillDetail.getReportNum() != null) {
                queryWrapper.eq("report_num", inbillDetail.getReportNum());
            }
            //入库数量
            if (inbillDetail.getInBillNum() != null) {
                queryWrapper.eq("in_bill_num", inbillDetail.getInBillNum());
            }
            //质保期;天
            if (inbillDetail.getWarranty() != null) {
                queryWrapper.eq("warranty", inbillDetail.getWarranty());
            }
            //不显示的数据id过滤
            if (CollectionUtil.isNotEmpty(inbillDetail.getIds())) {
                queryWrapper.notIn("id", inbillDetail.getIds());
            }
        }
        return queryWrapper;
    }

    /**
     * 验收
     *
     * @param inbill 入库单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult checkGoods(InBill inbill) {
        //验证入库单是否未验收
        InBill inBill = inBillMapper.selectById(inbill.getId());
        if (null == inBill) {
            throw new ServiceException("入库单不存在");
        }
        if (!InBillEnum.WAIT.getCode().equals(inBill.getInBillStatus())) {
            throw new ServiceException("入库单" + inBill.getInBillCode() + "不可重复收货");
        }
        List<InbillDetail> inbillDetailList = inbill.getInbillDetailList();
        //新增入库货物数据,组装唯一码离线数据返回给前端
        List<OnlyCodeVo> voList = new ArrayList<>();
        List<InbillGoods> inbillGoodsList = new ArrayList<>();
        for (InbillDetail inbillDetail : inbillDetailList) {
            for (int i = 0; i < inbillDetail.getInBillNum().intValue(); i++) {
                //创建入库货物数据
                InbillGoods inbillGoods = new InbillGoods(inBill.getInBillCode(), inbillDetail.getId(),
                        serialCodeUtils.getOnlyCode(inBill.getInBillCode()), new BigDecimal("1.00"),inbillDetail.getGoodsCode());
                inbillGoodsList.add(inbillGoods);
                OnlyCodeVo vo = new OnlyCodeVo();
                vo.setInBillCode(inBill.getInBillCode());
                vo.setGoodsCode(inbillDetail.getGoodsCode());
                vo.setNum(inbillDetail.getNum());
                vo.setOnlyCode(inbillGoods.getOnlyCode());
                voList.add(vo);
            }
        }
        if(CollectionUtil.isNotEmpty(inbillGoodsList)){
            inbillGoodsService.saveBatch(inbillGoodsList);
        }
        //修改入库详情入库数量
        super.updateBatchById(inbillDetailList);
        //新增上架单抬头
        ListingList listing = new ListingList();
        listing.setListingCode(serialCodeUtils.getOrderNo(ListingNoPrefixEnum.getPrefix()));
        listing.setInBillCode(inBill.getInBillCode());
        listing.setCharg(inBill.getCharg());
        listingListService.insertListingList(listing);
        //修改入库单总重量、总体积、总入库数量、已验货
        BigDecimal weight = new BigDecimal("0.00");
        BigDecimal volume = new BigDecimal("0.00");
        BigDecimal inBillNumAll = new BigDecimal("0.00");
        for (InbillDetail inbillDetail : inbillDetailList) {
            BigDecimal inBillNum = inbillDetail.getInBillNum();
            weight = weight.add(inBillNum.multiply(inbillDetail.getWeight()));
            volume = volume.add(inBillNum.multiply(inbillDetail.getVolume()));
            inBillNumAll = inBillNumAll.add(inBillNum);
        }
        inbill.setWeight(weight);
        inbill.setVolume(volume);
        inbill.setInBillNum(inBillNumAll);
        inbill.setInBillStatus(InBillEnum.INSPECTED.getCode());
        inbill.setTakeTrayStatus(TakeTrayStatusEnum.ING.getCode());
        inBillMapper.updateById(inbill);
        return AjaxResult.success("成功", voList);
    }

    /**
     * 组盘扫码校验
     * @param map
     * @return
     */
    public AjaxResult groupDiskValidate(GroupDiskDto map) {
        //托盘号
        String trayCode = map.getTrayCode();
        List<String> partsCodeList = map.getPartsCodeList();

        //校验托盘是否合法，是否启用
        LambdaQueryWrapper<Tray> trayQueryWrapper = Wrappers.lambdaQuery();
        trayQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Tray::getEnableStatus, EnableStatus.ENABLE.getCode())
                .eq(Tray::getEmptyStatus, IsEmptyEnum.ISEMPTY.getCode())
                .eq(Tray::getTrayCode, trayCode);
        if (trayMapper.selectCount(trayQueryWrapper) == 0  && CollUtil.isEmpty(map.getPartsCodeList())) {
            throw new ServiceException("托盘" + trayCode + "不可使用,已组盘或已禁用");
        }
        //唯一码list
        List<String> onlyCodeList = map.getOnlyCodeList();
        LambdaQueryWrapper<InbillGoods> inbillGoodsQuery = Wrappers.lambdaQuery();
        inbillGoodsQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .in(InbillGoods::getOnlyCode, onlyCodeList);
        //唯一码对应的入库货物表
        List<InbillGoods> inbillGoodsList = inbillGoodsMapper.selectList(inbillGoodsQuery);
        //校验唯一码是否被组盘
        for (InbillGoods inbillGoods : inbillGoodsList) {
            if (StringUtils.isNotEmpty(inbillGoods.getTrayCode())) {
                throw new ServiceException("机件号" + inbillGoods.getPartsCode() + ",已被托盘["+inbillGoods.getTrayCode()+"]组盘");
            }
        }

        List<Parts> parts = partsService.getBaseMapper().findPartsList(
                new QueryWrapper<Parts>()
                        .eq("g.del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("d.del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("b.del_flag", DelFlagEnum.DEL_NO.getCode())
                        .in("parts_code", partsCodeList)
        );

        Map<String, Long> goodsTypeMap = parts.stream().collect(
                Collectors.groupingBy(Parts::getGoodsCode, Collectors.counting()));
        if(goodsTypeMap.size() > 1){
            throw new ServiceException("货物类型不一致,不能放在该托盘上");
        }


        //唯一码对应的入库详情id
        List<String> detailIdList = inbillGoodsList.stream().map(InbillGoods::getInbillDetailId).distinct().collect(Collectors.toList());
        if(CollUtil.isEmpty(detailIdList)){
            throw new ServiceException("扫描机件号不存在");
        }

        //校验唯一码是否属于同一入库单详情
        if (detailIdList.size() > 1) {
            throw new ServiceException("托盘只可以放置同一入库单下相同类型的货物");
        }

        //入库详情数据
        InbillDetail inbillDetail = selectInbillDetailById(detailIdList.get(0));
        //校验托盘是否可以容纳货物
        Long num = inbillDetail.getNum();
        if (onlyCodeList.size() > num) {
            throw new ServiceException("托盘放置该货物类型数量，超出数量上限，数量上限为：" + num);
        }
        return AjaxResult.success();
    }


    /**
     * 获取理货区下拉框
     * @return 理货区库位
     */
    public List<Location> getLocation(String trayCode){
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("location_code","location_name");
        queryWrapper.eq("area_id","LHQ01");
        queryWrapper.orderByAsc("column_num");
        List<Location> locations = locationMapper.selectList(queryWrapper);
        queryWrapper.clear();
        queryWrapper.select("location_code","location_name");
        queryWrapper.eq("area_id","LHQ01");
        queryWrapper.eq("tray_code",trayCode);
        Location location = locationMapper.selectOne(queryWrapper);
        //判断理货区是否有该托盘
        if (ObjectUtil.isNotEmpty(location)) {
            for (Location location1 : locations) {
                if(location1.getLocationCode().equals(location.getLocationCode())) {
                    location1.setFlag(true);
                    break;
                }
            }
        }
        return  locations;
    }

    /**
     * 组盘
     *
     * @param map 组盘数据
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult groupDisk(GroupDiskDto map) {
        //托盘号
        String trayCode = map.getTrayCode();
        //获取理货区agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, "LHQ01")
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {
            //库位号
            String locationCode = map.getLocationCode();
            if (StrUtil.isEmpty(locationCode)) {
                throw new ServiceException("库位编号不能为空");
            }
            QueryWrapper<ViewPageGoodsVo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("wl.del_flag", DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("wl.location_code", locationCode);
            ViewPageGoodsVo viewPageGoodsVo = viewPageMapper.selectGoodsByLocCodeList(queryWrapper);
            if (ObjectUtil.isNotEmpty(viewPageGoodsVo)) {
                if (StringUtils.isNotEmpty(viewPageGoodsVo.getGoodsCode())) {
                    throw new ServiceException(viewPageGoodsVo.getLocationName() + "库位,已有货物等待上架！");
                }
            }
            //根据托盘号查询库位
            LambdaQueryWrapper<Location> locationQueryWrapper = Wrappers.lambdaQuery();
            locationQueryWrapper.select(Location::getLocationCode)
                    .eq(Location::getTrayCode, trayCode)
                    .eq(Location::getAreaId, "LHQ01");
            Location location = locationMapper.selectOne(locationQueryWrapper);
            if (ObjectUtil.isNotEmpty(location)) {
                String originalTrayCode = location.getLocationCode();
                //修改该库位托盘号
                UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("tray_code",null);
                updateWrapper.eq("location_code",originalTrayCode);
                int update = locationMapper.update(new Location(), updateWrapper);
            }
            //修改该库位托盘号
            UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("tray_code",trayCode);
            updateWrapper.eq("location_code",locationCode);
            int update = locationMapper.update(new Location(), updateWrapper);
        }
        //校验托盘是否合法，是否启用
        LambdaQueryWrapper<Tray> trayQueryWrapper = Wrappers.lambdaQuery();
        trayQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Tray::getEnableStatus, EnableStatus.ENABLE.getCode())
                .eq(Tray::getEmptyStatus, IsEmptyEnum.ISEMPTY.getCode())
                .eq(Tray::getTrayCode, trayCode);
        if (trayMapper.selectCount(trayQueryWrapper) == 0) {
            throw new ServiceException("托盘" + trayCode + "不可使用,已组盘或已禁用");
        }
        //唯一码list
        List<String> onlyCodeList = map.getOnlyCodeList();
        LambdaQueryWrapper<InbillGoods> inbillGoodsQuery = Wrappers.lambdaQuery();
        inbillGoodsQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .in(InbillGoods::getOnlyCode, onlyCodeList);
        //唯一码对应的入库货物表
        List<InbillGoods> inbillGoodsList = inbillGoodsMapper.selectList(inbillGoodsQuery);
        //校验唯一码是否被组盘
        for (InbillGoods inbillGoods : inbillGoodsList) {
            if (StringUtils.isNotEmpty(inbillGoods.getTrayCode())) {
                throw new ServiceException("机件号" + inbillGoods.getPartsCode() + ",已被托盘["+inbillGoods.getTrayCode()+"]组盘");
            }
        }
        //唯一码对应的入库详情id
        List<String> detailIdList = inbillGoodsList.stream().map(InbillGoods::getInbillDetailId).distinct().collect(Collectors.toList());
        //校验唯一码是否属于同一入库单详情
        if (detailIdList.size() > 1) {
            throw new ServiceException("托盘只可以放置同一入库单下相同类型的货物");
        }
        //入库详情数据
        InbillDetail inbillDetail = selectInbillDetailById(detailIdList.get(0));
        //校验托盘是否可以容纳货物
        Long num = inbillDetail.getNum();
        if (onlyCodeList.size() > num) {
            throw new ServiceException("托盘放置该货物类型数量，超出数量上限，数量上限为：" + num);
        }
        //上架单抬头
        ListingList listingList = listingListService.selectByInBillCode(inbillDetail.getInBillCode());
        //组盘数据操作
        List<ListingDetail> listingDetailList = new ArrayList<>();
        for (InbillGoods inbillGoods : inbillGoodsList) {
            //新增上架单详情
            ListingDetail listingDetail = new ListingDetail();
            listingDetail.setListingCode(listingList.getListingCode());
            listingDetail.setInBillCode(inbillDetail.getInBillCode());
            listingDetail.setCharg(listingList.getCharg());
            listingDetail.setOnlyCode(inbillGoods.getOnlyCode());
            listingDetail.setPartsCode(inbillGoods.getPartsCode());
            listingDetail.setGoodsCode(inbillDetail.getGoodsCode());
            listingDetail.setGoodsName(inbillDetail.getGoodsName());
            listingDetail.setModel(inbillDetail.getModel());
            listingDetail.setMeasureUnit(inbillDetail.getMeasureUnit());
            listingDetail.setSupplierCode(inbillDetail.getSupplierCode());
            listingDetail.setSupplierName(inbillDetail.getSupplierName());
            listingDetail.setTrayCode(trayCode);
            listingDetail.setPeriodValidity(inbillGoods.getPeriodValidity());
            listingDetail.setProduceTime(inbillGoods.getProduceTime());
            listingDetail.setWarranty(inbillDetail.getWarranty());
            listingDetailList.add(listingDetail);
        }
        //新增上架单详情
        if (listingDetailList.size() > 0) {
            listingDetailService.saveBatch(listingDetailList);
        }
        //修改入库货物托盘号
        LambdaUpdateWrapper<InbillGoods> inbillGoodsUpdate = Wrappers.lambdaUpdate();
        inbillGoodsUpdate.set(InbillGoods::getTrayCode, trayCode)
                .in(InbillGoods::getOnlyCode, onlyCodeList);
        inbillGoodsMapper.update(null, inbillGoodsUpdate);
        //修改入库抬头为上架中
        LambdaUpdateWrapper<InBill> inBillUpdate = Wrappers.lambdaUpdate();
        inBillUpdate.set(InBill::getInBillStatus, InBillEnum.PUTONING.getCode())
                .eq(InBill::getInBillCode, inbillDetail.getInBillCode());
        inBillMapper.update(null, inBillUpdate);
        //修改托盘状态为非空盘,修改托盘货物类别
        LambdaUpdateWrapper<Tray> trayUpdate = Wrappers.lambdaUpdate();
        trayUpdate.set(Tray::getEmptyStatus, IsEmptyEnum.NOTEMPTY.getCode())
                .set(Tray::getGoodsCode, inbillDetail.getGoodsCode())
                .eq(Tray::getTrayCode, trayCode);
        trayMapper.update(null, trayUpdate);
        return AjaxResult.success("成功");
    }

    /**
     * 解盘
     *
     * @param dto 解盘数据
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult releaseTray(ReleaseTrayDto dto) {
        //托盘号
        List<String> trayCodeList = dto.getTrayCodeList();
        //入库单号
        String inBillCode = dto.getInBillCode();
        if (CollUtil.isEmpty(trayCodeList) || StrUtil.isEmpty(inBillCode)) {
            throw new ServiceException("参数异常");
        }
        //删除对应上架单详情数据
        LambdaUpdateWrapper<ListingDetail> listingDetailUpdateWrapper = Wrappers.lambdaUpdate();
        listingDetailUpdateWrapper.set(BaseEntity::getDelFlag, DelFlagEnum.DEL_YES.getCode())
                .eq(ListingDetail::getInBillCode, inBillCode)
                .in(ListingDetail::getTrayCode, trayCodeList);
        listingDetailMapper.update(null, listingDetailUpdateWrapper);
        //查询托盘上的唯一码数据
        LambdaQueryWrapper<InbillGoods> inbillGoodsQueryWrapper = Wrappers.lambdaQuery();
        inbillGoodsQueryWrapper.eq(InbillGoods::getInBillCode, inBillCode)
                .in(InbillGoods::getTrayCode, trayCodeList)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        //入库货物list
        List<InbillGoods> inbillGoodList = inbillGoodsMapper.selectList(inbillGoodsQueryWrapper);
        //入库详情主键集合
        String[] inbillDetailId = inbillGoodList.stream().map(InbillGoods::getInbillDetailId).distinct().toArray(String[]::new);
        List<InbillDetail> inbillDetailList = selectInbillDetailByIds(inbillDetailId);
        //拼接离线数据
        List<OnlyCodeVo> voList = new ArrayList<>();
        for (InbillDetail detail : inbillDetailList) {
            List<String> onlyCodeList = inbillGoodList.stream()
                    .filter(o -> detail.getId().equals(o.getInbillDetailId()))
                    .map(InbillGoods::getOnlyCode).collect(Collectors.toList());
            for (String onlyCode : onlyCodeList) {
                OnlyCodeVo vo = new OnlyCodeVo();
                vo.setInBillCode(inBillCode);
                vo.setGoodsCode(detail.getGoodsCode());
                vo.setNum(detail.getNum());
                vo.setOnlyCode(onlyCode);
                voList.add(vo);
            }
        }
        //修改对应入库货物托盘号为空
        LambdaUpdateWrapper<InbillGoods> inbillGoodsUpdate = Wrappers.lambdaUpdate();
        inbillGoodsUpdate.set(InbillGoods::getTrayCode, null)
                .eq(InbillGoods::getInBillCode, inBillCode)
                .in(InbillGoods::getTrayCode, trayCodeList)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        inbillGoodsMapper.update(null, inbillGoodsUpdate);
        //修改托盘状态为空盘,修改托盘货物类别
        LambdaUpdateWrapper<Tray> trayUpdate = Wrappers.lambdaUpdate();
        trayUpdate.set(Tray::getEmptyStatus, IsEmptyEnum.ISEMPTY.getCode())
                .set(Tray::getGoodsCode, null)
                .in(Tray::getTrayCode, trayCodeList);
        trayMapper.update(null, trayUpdate);

        return AjaxResult.success("成功", voList);
    }

    /**
     * 拉取离线数据
     *
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult pullData() {
        List<OnlyCodeVo> voList = new ArrayList<>();
        LambdaQueryWrapper<InBill> inBillQueryWrapper = Wrappers.lambdaQuery();
        inBillQueryWrapper.select(InBill::getInBillCode)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .and(wrapper -> {
                    wrapper.eq(InBill::getInBillStatus, InBillEnum.INSPECTED.getCode())
                            .or()
                            .eq(InBill::getInBillStatus, InBillEnum.PUTONING.getCode());
                });
        List<String> inBillCodeList = inBillMapper.selectObjs(inBillQueryWrapper).stream().map(String::valueOf).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(inBillCodeList)) {
            //查询入库单详情
            LambdaQueryWrapper<InbillDetail> inbillDetailQueryWrapper = Wrappers.lambdaQuery();
            inbillDetailQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .in(InbillDetail::getInBillCode, inBillCodeList);
            List<InbillDetail> inbillDetailList = inbillDetailMapper.selectList(inbillDetailQueryWrapper);
            //查询唯一码数据
            LambdaQueryWrapper<InbillGoods> inbillGoodsQueryWrapper = Wrappers.lambdaQuery();
            inbillGoodsQueryWrapper.in(InbillGoods::getInBillCode, inBillCodeList)
                    .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode());
            List<InbillGoods> inbillGoodList = inbillGoodsMapper.selectList(inbillGoodsQueryWrapper);
            //拼接离线数据
            for (InbillDetail detail : inbillDetailList) {
                List<String> onlyCodeList = inbillGoodList.stream()
                        .filter(o -> detail.getId().equals(o.getInbillDetailId()))
                        .map(InbillGoods::getOnlyCode).collect(Collectors.toList());
                for (String onlyCode : onlyCodeList) {
                    OnlyCodeVo vo = new OnlyCodeVo();
                    vo.setInBillCode(detail.getInBillCode());
                    vo.setGoodsCode(detail.getGoodsCode());
                    vo.setNum(detail.getNum());
                    vo.setOnlyCode(onlyCode);
                    voList.add(vo);
                }
            }
        }
        return AjaxResult.success("成功", voList);
    }


    /**
     * 获取已取托盘
     * @param billNo  单据号
     * @param type 操作类型(1.智能取盘  2.人工取盘)
     */
    public List<WcsOperateTask> getHaveTakenTrayInfo(String billNo,String type) {
        List<WcsOperateTask> dataList = new ArrayList<>();
        if(type.equals("1")){
            List<WcsOperateTask> wcsOperateTasks = wcsOperateTaskMapper.selectList(new QueryWrapper<WcsOperateTask>()
                    .select("distinct tray_no")
                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                    .eq("operate_type", WmsWcsTypeEnum.TAKETRAY.getCode())
                    .eq("in_bill_no", billNo).orderByDesc("create_time"));
            dataList = wcsOperateTasks.stream().collect(Collectors.toList());
        }else{
            List<WcsOperateTask> wcsOperateTasks = wcsOperateTaskMapper.selectList(new QueryWrapper<WcsOperateTask>()
                    .select("distinct tray_no")
                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                    .eq("operate_type", WmsWcsTypeEnum.TAKETRAY.getCode())
                    .isNull("in_bill_no").orderByDesc("create_time"));
            dataList = wcsOperateTasks.stream().collect(Collectors.toList());
        }
        return dataList;
    }
}
