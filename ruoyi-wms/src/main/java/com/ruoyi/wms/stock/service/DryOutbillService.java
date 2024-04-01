package com.ruoyi.wms.stock.service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.enums.*;
import com.ruoyi.wms.stock.domain.DryOutbill;
import com.ruoyi.wms.stock.domain.DryOutbillGoods;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.mapper.DryOutbillGoodsMapper;
import com.ruoyi.wms.stock.mapper.DryOutbillMapper;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.utils.SerialCodeUtils;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.bean.BeanValidators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 晾晒出库单Service接口
 *
 * @author ruoyi
 * @date 2023-03-03
 */
@Slf4j
@Service
public class DryOutbillService extends ServiceImpl<DryOutbillMapper, DryOutbill> {

    @Autowired(required = false)
    private DryOutbillMapper wmsDryOutbillMapper;
    @Autowired
    protected Validator validator;
    @Autowired
    private SerialCodeUtils serialCodeUtils;
    @Autowired
    private DryOutbillGoodsService dryOutbillGoodsService;
    @Autowired
    private DryOutbillGoodsMapper dryOutbillGoodsMapper;
    @Autowired
    private TblstockService tblstockService;
    @Autowired
    private TblstockMapper tblstockMapper;
    @Autowired
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private AreaMapper areaMapper;
    /**
     * 查询晾晒出库单
     *
     * @param id 晾晒出库单主键
     * @return 晾晒出库单
     */
    public DryOutbill selectWmsDryOutbillById(String id){
        QueryWrapper<DryOutbill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        DryOutbill dryOutbill = wmsDryOutbillMapper.selectById(id);
        dryOutbill.setWmsDryOutbillGoodsList(dryOutbillGoodsService.selectByCode(dryOutbill.getDryOutbillCode()));
        return dryOutbill;
    }


    /**
     * 根据ids查询晾晒出库单
     *
     * @param ids 晾晒出库单 IDs
     * @return 晾晒出库单
     */
    public List<DryOutbill> selectWmsDryOutbillByIds(String[] ids) {
        QueryWrapper<DryOutbill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsDryOutbillMapper.selectList(queryWrapper);
    }

    /**
     * 查询晾晒出库单列表
     *
     * @param wmsDryOutbill 晾晒出库单
     * @return 晾晒出库单集合
     */
    public List<DryOutbill> selectWmsDryOutbillList(DryOutbill wmsDryOutbill){
        QueryWrapper<DryOutbill> queryWrapper = getQueryWrapper(wmsDryOutbill);
        return wmsDryOutbillMapper.selectByAreaCode(queryWrapper);
    }

    /**
     * 查询未创建入库单的晾晒出库单列表
     * @return 晾晒出库单集合
     */
    public List<DryOutbill> getDryOutbillList(){
        LambdaQueryWrapper<DryOutbillGoods> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DryOutbillGoods::getLockStatus,LockEnum.NOTLOCK.getCode())
                .eq(DryOutbillGoods::getDryOutbillStatus,DryOutbillStatusTypeEnum.END.getCode())
                .eq(DryOutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
        List<DryOutbillGoods> goodsList = dryOutbillGoodsMapper.selectList(queryWrapper);
        List<String> dryOutbillCodes = goodsList.stream().map(dryOutbillGoods -> dryOutbillGoods.getDryOutbillCode()).distinct().collect(Collectors.toList());
        List<DryOutbill> outbills = new ArrayList<>();
        DryOutbill dryOutbill = null;
        for (String code:dryOutbillCodes) {
            dryOutbill = new DryOutbill();
            dryOutbill.setDryOutbillCode(code);
            List<DryOutbillGoods> outbillGoodsList = new ArrayList<>();
            for (DryOutbillGoods goods:goodsList) {
                if(goods.getDryOutbillCode().equals(code)){
                    outbillGoodsList.add(goods);
                }
            }
            dryOutbill.setWmsDryOutbillGoodsList(outbillGoodsList);
            outbills.add(dryOutbill);
        }
        return outbills;
    }

    /**
     * 新增晾晒出库单
     *
     * @param wmsDryOutbill 晾晒出库单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public DryOutbill insertWmsDryOutbill(DryOutbill wmsDryOutbill){
        wmsDryOutbill.setId(IdUtil.simpleUUID());
        //生成晾晒出库单号
        String dryOutBillCode = serialCodeUtils.getOrderNo(OutBillNoPrefixEnum.getPrefix(OutBillNoPrefixEnum.LSCK.getCode()));//晾晒出库单号
        wmsDryOutbill.setDryOutbillCode(dryOutBillCode);
        wmsDryOutbill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsDryOutbill.setDryOutbillStatus(DryOutbillStatusTypeEnum.WAIT.getCode());//晾晒出库状态;(0、待出库 1、出库中 2、已出库)
        List<DryOutbillGoods> dryOutbillDetailList = wmsDryOutbill.getWmsDryOutbillGoodsList();
        List<String> trayCodes = new ArrayList<>();
        if (dryOutbillDetailList.size()>0){
            //保存详情
            for (DryOutbillGoods dryOutbillGoods : dryOutbillDetailList){
                dryOutbillGoods.setDryOutbillCode(wmsDryOutbill.getDryOutbillCode());
                dryOutbillGoods.setGoodsNum("1");
                dryOutbillGoods.setDryOutbillStatus(DryOutbillStatusTypeEnum.WAIT.getCode());
                //通过trayCode将货物锁住
                trayCodes.add(dryOutbillGoods.getTrayCode());
            }
            //验证同托盘是否有被锁定的货物
            LambdaQueryWrapper<Tblstock> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                    .in(Tblstock::getTrayCode,trayCodes);
            List<Tblstock> data = tblstockMapper.selectList(queryWrapper);
            List<String> trayCodeList = data.stream().map(tblstock->tblstock.getTrayCode()).distinct().collect(Collectors.toList());
            if(data.size()>0){
                String msg = "托盘【";
                for (String trayCode:trayCodeList) {
                    msg+= trayCode+"，";
                }
                msg = msg.substring(0,msg.length()-1);
                msg +="】存在数据被使用，请重新选择数据";
                throw new RuntimeException(msg);
            }
            dryOutbillGoodsService.saveBatch(dryOutbillDetailList);
            LambdaUpdateWrapper<Tblstock> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                    .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(Tblstock::getTrayCode,trayCodes);
            tblstockMapper.update(null,updateWrapper);
        }
        wmsDryOutbillMapper.insert(wmsDryOutbill);
        return wmsDryOutbill;
    }

    /**
     * 修改晾晒出库单
     *
     * @param wmsDryOutbill 晾晒出库单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public DryOutbill updateWmsDryOutbill(DryOutbill wmsDryOutbill){
        if(CollectionUtil.isNotEmpty(wmsDryOutbill.getIds())){
            delDryOutbillGoodsByIds(wmsDryOutbill.getIds().toArray(new String[wmsDryOutbill.getIds().size()]));
        }
        wmsDryOutbillMapper.updateById(wmsDryOutbill);
        List<DryOutbillGoods> goodsList = new ArrayList<>();
        List<String> trayCodes = new ArrayList<>();
        //修改出库单货物表数据
        for (DryOutbillGoods dryOutbillGoods:wmsDryOutbill.getWmsDryOutbillGoodsList()){
            if(StringUtils.isEmpty(dryOutbillGoods.getId())){
                dryOutbillGoods.setId(IdUtils.simpleUUID());
                dryOutbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                dryOutbillGoods.setDryOutbillCode(wmsDryOutbill.getDryOutbillCode());
                dryOutbillGoods.setGoodsNum("1");
                dryOutbillGoods.setDryOutbillStatus(DryOutbillStatusTypeEnum.WAIT.getCode());
                goodsList.add(dryOutbillGoods);
                trayCodes.add(dryOutbillGoods.getTrayCode());
            }
        }
        if(goodsList.size()>0){
            dryOutbillGoodsService.saveBatch(goodsList);
            //验证同托盘是否有被锁定的货物
            LambdaQueryWrapper<Tblstock> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                    .in(Tblstock::getTrayCode,trayCodes);
            List<Tblstock> data = tblstockMapper.selectList(queryWrapper);
            List<String> trayCodeList = data.stream().map(tblstock->tblstock.getTrayCode()).distinct().collect(Collectors.toList());
            if(data.size()>0){
                String msg = "托盘【";
                for (String trayCode:trayCodeList) {
                    msg+= trayCode+"，";
                }
                msg = msg.substring(0,msg.length()-1);
                msg +="】存在数据被使用，请重新选择数据";
                throw new RuntimeException(msg);
            }
            LambdaUpdateWrapper<Tblstock> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                    .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(Tblstock::getTrayCode,trayCodes);
            tblstockMapper.update(null,updateWrapper);
        }
        return wmsDryOutbill;
    }

    /**
     * 批量删除晾晒出库单
     *
     * @param ids 需要删除的晾晒出库单主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsDryOutbillByIds(String[] ids){
        List<DryOutbill> wmsDryOutbills = new ArrayList<>();
        for (String id : ids) {
            DryOutbill wmsDryOutbill = new DryOutbill();
            wmsDryOutbill.setId(id);
            wmsDryOutbill.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsDryOutbills.add(wmsDryOutbill);
        }
        LambdaQueryWrapper<DryOutbill> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DryOutbill::getDelFlag,DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in(DryOutbill::getId,ids);
        //通过partsCode将货物开启
        List<DryOutbill> dryOutbills = wmsDryOutbillMapper.selectList(queryWrapper);
        List<String> dryOutbillCode = dryOutbills.stream().map(outbill -> outbill.getDryOutbillCode()).collect(Collectors.toList());
        QueryWrapper<DryOutbillGoods> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("del_flag",DelFlagEnum.DEL_NO.getCode()).in("dry_outbill_code",dryOutbillCode);
        List<DryOutbillGoods> goodsList = wmsDryOutbillMapper.getDryOutbillGoods(queryWrapper1);
        if(CollectionUtil.isNotEmpty(goodsList)){
            List<String> trayCodes = goodsList.stream().map(goods -> goods.getTrayCode()).distinct().collect(Collectors.toList());
            LambdaUpdateWrapper<Tblstock> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(Tblstock::getLockStatus,LockEnum.NOTLOCK.getCode())
                    .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(Tblstock::getTrayCode,trayCodes);
            tblstockMapper.update(null,updateWrapper);
        }
        return super.updateBatchById(wmsDryOutbills) ? 1 : 0;
    }

    /**
     * 删除晾晒出库单信息
     *
     * @param id 晾晒出库单主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsDryOutbillById(String id){
        DryOutbill wmsDryOutbill = new DryOutbill();
        wmsDryOutbill.setId(id);
        wmsDryOutbill.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsDryOutbillMapper.updateById(wmsDryOutbill);
    }

    public QueryWrapper<DryOutbill> getQueryWrapper(DryOutbill wmsDryOutbill) {
        QueryWrapper<DryOutbill> queryWrapper = new QueryWrapper<>();
        if (wmsDryOutbill != null) {
            wmsDryOutbill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("o.del_flag",wmsDryOutbill.getDelFlag());
            //晾晒出库单号
            if (StrUtil.isNotEmpty(wmsDryOutbill.getDryOutbillCode())) {
                queryWrapper.like("o.dry_outbill_code",wmsDryOutbill.getDryOutbillCode());
            }
            //晾晒出库类型;(1、待出库 2、出库中 3、已出库)
            if (StrUtil.isNotEmpty(wmsDryOutbill.getDryOutbillStatus())) {
                queryWrapper.eq("o.dry_outbill_status",wmsDryOutbill.getDryOutbillStatus());
            }
            //晾晒出库货物所属库区
            if (StrUtil.isNotEmpty(wmsDryOutbill.getAreaCode())) {
                queryWrapper.eq("g.area_code",wmsDryOutbill.getAreaCode());
            }
            //出库时间
            if (wmsDryOutbill.getParams().containsKey("beginDryOutbillTime")&&wmsDryOutbill.getParams().containsKey("endDryOutbillTime")) {
               queryWrapper.between("o.dry_outbill_time",
                       wmsDryOutbill.getParams().get("beginDryOutbillTime")+" 00:00:00",
                       wmsDryOutbill.getParams().get("endDryOutbillTime")+" 23:59:59");
            }
            //创建时间
            if (wmsDryOutbill.getParams().containsKey("beginCreateTime")&&wmsDryOutbill.getParams().containsKey("endCreateTime")) {
               queryWrapper.between("o.create_time",
                       wmsDryOutbill.getParams().get("beginCreateTime")+" 00:00:00",
                       wmsDryOutbill.getParams().get("endCreateTime")+" 23:59:59");
            }
        }
        return queryWrapper;
    }

    public QueryWrapper<Tblstock> getQueryGoodsWrapper(Tblstock tblstock) {
        QueryWrapper<Tblstock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode());
        queryWrapper.eq("lock_status", LockEnum.NOTLOCK.getCode())
                .eq("area_type",AreaTypeEnum.CCQ.getCode());

        if(StringUtils.isNotEmpty(tblstock.getPartsCode())){
            queryWrapper.like("parts_code", tblstock.getPartsCode());
        }
        if(StringUtils.isNotEmpty(tblstock.getLocationCode())){
            queryWrapper.like("location_code", tblstock.getLocationCode());
        }
        if(StringUtils.isNotEmpty(tblstock.getGoodsName())){
            queryWrapper.like("goods_name", tblstock.getGoodsName());
        }
        //入库时间
        if (tblstock.getParams().containsKey("beginInBillDate")&&tblstock.getParams().containsKey("endInBillDate")) {
            queryWrapper.between("InBillDate",
                    tblstock.getParams().get("beginInBillDate")+" 00:00:00",
                    tblstock.getParams().get("endInBillDate")+" 23:59:59");
        }
        //上次晾晒时间
        if (tblstock.getParams().containsKey("beginLastDryDate")&&tblstock.getParams().containsKey("endLastDryDate")) {
            queryWrapper.between("LastDryDate",
                    tblstock.getParams().get("beginLastDryDate")+" 00:00:00",
                    tblstock.getParams().get("endLastDryDate")+" 23:59:59");
        }
        queryWrapper.orderByAsc("location_code","tray_code","id");
        return queryWrapper;
    }

    public QueryWrapper<DryOutbillGoods> getQueryDetailWrapper(DryOutbillGoods dryOutbillGoods) {
        QueryWrapper<DryOutbillGoods> queryWrapper = new QueryWrapper<>();
        if (dryOutbillGoods != null) {
            dryOutbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",dryOutbillGoods.getDelFlag());
            //晾晒出库单号
            if (StringUtils.isNotEmpty(dryOutbillGoods.getDryOutbillCode())) {
                queryWrapper.like("dry_outbill_code",dryOutbillGoods.getDryOutbillCode());
            }
            //机件号
            if (StringUtils.isNotEmpty(dryOutbillGoods.getPartsCode() )) {
                queryWrapper.like("parts_code", dryOutbillGoods.getPartsCode());
            }
            //货物名称
            if (StringUtils.isNotEmpty(dryOutbillGoods.getGoodsName())) {
                queryWrapper.like("goods_name", dryOutbillGoods.getGoodsName());
            }
            //货物名称
            if (StringUtils.isNotEmpty(dryOutbillGoods.getGoodsCode())) {
                queryWrapper.like("goods_code", dryOutbillGoods.getGoodsCode());
            }
            //出库状态
            if (StringUtils.isNotEmpty(dryOutbillGoods.getDryOutbillStatus())) {
                queryWrapper.like("dry_outbill_status", dryOutbillGoods.getDryOutbillStatus());
            }
            //出库状态
            if (StringUtils.isNotEmpty(dryOutbillGoods.getLockStatus())) {
                queryWrapper.eq("lock_status", dryOutbillGoods.getLockStatus());
            }
            //出库时间
            if (StringUtils.isNotEmpty(dryOutbillGoods.getParams())&&StringUtils.isNotNull(dryOutbillGoods.getParams().get("beginDryOutbillTime"))) {
                String begin = dryOutbillGoods.getParams().get("beginDryOutbillTime")+" 00:00:00";
                String end = dryOutbillGoods.getParams().get("endDryOutbillTime")+" 23:59:59";
                queryWrapper.between("dry_outbill_time",begin,end);
            }
            if(CollectionUtil.isNotEmpty(dryOutbillGoods.getIds())){
                queryWrapper.notIn("id",dryOutbillGoods.getIds());
            }
            queryWrapper.orderByDesc("dry_outbill_code");
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param wmsDryOutbillList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<DryOutbill> wmsDryOutbillList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsDryOutbillList) || wmsDryOutbillList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (DryOutbill wmsDryOutbill : wmsDryOutbillList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                DryOutbill u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsDryOutbill);
                    wmsDryOutbill.setId(IdUtil.simpleUUID());
                    wmsDryOutbill.setCreateBy(operName);
                    wmsDryOutbill.setCreateTime(new Date());
                    wmsDryOutbill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsDryOutbillMapper.insert(wmsDryOutbill);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsDryOutbill);
                    //todo 验证
                    wmsDryOutbill.setId(u.getId());
                    wmsDryOutbill.setUpdateBy(operName);
                    wmsDryOutbill.setUpdateTime(new Date());
                    wmsDryOutbillMapper.updateById(wmsDryOutbill);
                    successNum++;
                    //}
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、第"+failureNum+"行数据导入失败：";
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
     * 获取库存总览货物信息信息
     * @return
     */
    public List<Tblstock> getGoodsList(Tblstock tblstock) {
        QueryWrapper<Tblstock> queryGoodsWrapper = getQueryGoodsWrapper(tblstock);
        List<Tblstock> listGoods = wmsDryOutbillMapper.getGoodsList(queryGoodsWrapper);
        return listGoods;
    }

    /**
     * 初始化获取货区信息
     * @return
     */
    public List<DryOutbillGoods> getAreaData() {
        return dryOutbillGoodsService.getAreaData();
    }

    /**
     * 查询晾晒出库单详情列表
     * @param dryOutbillGoods
     * @return
     */
    public List<DryOutbill> selectGoodsDetailList(DryOutbillGoods dryOutbillGoods) {
        QueryWrapper<DryOutbill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", dryOutbillGoods.getId());
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        //根据id获取订单编号
        DryOutbill dryOutbill = wmsDryOutbillMapper.selectById(dryOutbillGoods.getId());
        dryOutbillGoods.setDryOutbillCode(dryOutbill.getDryOutbillCode());
        dryOutbill.setWmsDryOutbillGoodsList(dryOutbillGoodsService.selectGoodsDetailList(dryOutbillGoods));
        List<DryOutbill> listDryOutbill = new ArrayList<>();
        listDryOutbill.add(dryOutbill);
        return listDryOutbill;
    }

    /**
     * 点击开始按钮，晾晒出库
     * @param dryOutbill
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean clickStart(DryOutbill dryOutbill) {
        dryOutbill.setDryOutbillStatus(DryOutbillStatusTypeEnum.IN.getCode());
        //改变订单状态
        wmsDryOutbillMapper.updateById(dryOutbill);
        LambdaQueryWrapper<DryOutbillGoods> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(DryOutbillGoods::getId)
                .eq(DryOutbillGoods::getDryOutbillCode,dryOutbill.getDryOutbillCode())
                .eq(DryOutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
        int i = dryOutbillGoodsMapper.selectCount(queryWrapper).intValue();
        if(i == 0){
            throw new RuntimeException("请维护晾晒出库货物信息");
        }
        AjaxResult result = dryOutbillGoodsService.startDryTask(dryOutbill);
        //改变货物状态
        return result.get(AjaxResult.CODE_TAG).equals(200);
    }

    /**
     * 查询晾晒出库单货物数据详细
     * @param dryOutbillGoods
     * @return
     */
    public List<DryOutbillGoods> getDryOutbillGoods(DryOutbillGoods dryOutbillGoods) {
        QueryWrapper<DryOutbillGoods> queryWrapper = getQueryDetailWrapper(dryOutbillGoods);
        return wmsDryOutbillMapper.getDryOutbillGoods(queryWrapper);
    };

    /**
     * 删除货物并修改库存锁状态
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int delDryOutbillGoodsByIds(String[] ids) {
        List<DryOutbillGoods> goodsList = new ArrayList<>();
        List<String> trayCodes = new ArrayList<>();
        for (String id : ids){
            DryOutbillGoods dryOutbillGoods = new DryOutbillGoods();
            dryOutbillGoods.setId(id);
            dryOutbillGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            goodsList.add(dryOutbillGoods);
        }
        boolean b = dryOutbillGoodsService.updateBatchById(goodsList);
        LambdaQueryWrapper<DryOutbillGoods> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(DryOutbillGoods::getId,ids);
        goodsList = dryOutbillGoodsMapper.selectList(queryWrapper);

        queryWrapper.clear();
        queryWrapper.eq(DryOutbillGoods::getDryOutbillCode,goodsList.get(0).getDryOutbillCode())
                .eq(DryOutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
        //需要判断是否同托盘数据都删除（然后解锁）
        List<DryOutbillGoods> allGoodsList = dryOutbillGoodsMapper.selectList(queryWrapper);
        for (DryOutbillGoods goodsInfo: goodsList){
            boolean isHav = false;
            for (DryOutbillGoods goods: allGoodsList) {
                if(goods.getTrayCode().equals(goodsInfo.getTrayCode())&&!goods.getId().equals(goodsInfo.getId())){
                    isHav = true;
                }
            }
            if(!isHav){
                trayCodes.add(goodsInfo.getTrayCode());
            }
        }
        if(trayCodes.size()>0){
            LambdaUpdateWrapper<Tblstock> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(Tblstock::getLockStatus,LockEnum.NOTLOCK.getCode())
                    .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(Tblstock::getTrayCode,trayCodes);
            tblstockMapper.update(null,updateWrapper);
        }

        return b?1:0;
    }

    /**
     * 校验晾晒库区是否能容纳选择的托盘
     * @return true可以容纳，false不可以
     */
    public boolean checkData(DryOutbill wmsDryOutbill) {
        LambdaQueryWrapper<Area> areaLambdaQueryWrapper = Wrappers.lambdaQuery();
        areaLambdaQueryWrapper.eq(Area::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .eq(Area::getAreaType,AreaTypeEnum.LSQ.getCode());
        List<Area> areaList = areaMapper.selectList(areaLambdaQueryWrapper);
        List<String> areaCodes = areaList.stream().map(area -> area.getAreaCode()).collect(Collectors.toList());
        //获取理货区agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .in(WcsDeviceBaseInfo::getWarehouseAreaCode,areaCodes)
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        //AGV启用
        if(wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery)>0){
            //1、查询晾晒区空库位（未使用，未锁定）
            LambdaQueryWrapper<Location> locationLambdaQueryWrapper = Wrappers.lambdaQuery();
            locationLambdaQueryWrapper.eq(Location::getLockStatus,LockEnum.NOTLOCK.getCode())
                    .eq(Location::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .in(Location::getAreaId,areaCodes)
                    .isNull(Location::getTrayCode);
            List<Location> locations = locationMapper.selectList(locationLambdaQueryWrapper);
            if(CollectionUtil.isEmpty(locations)){
                return false;
            }
            //2、获取当前晾晒出库任务待执行数据托盘数
            LambdaQueryWrapper<DryOutbillGoods> outbillGoodsLambdaQueryWrapper = Wrappers.lambdaQuery();
            outbillGoodsLambdaQueryWrapper.eq(DryOutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(DryOutbillGoods::getDryOutbillStatus,DryOutbillStatusTypeEnum.WAIT.getCode())
                    .groupBy(DryOutbillGoods::getTrayCode);
            List<DryOutbillGoods> outbillGoodsList = dryOutbillGoodsMapper.selectList(outbillGoodsLambdaQueryWrapper);
            List<DryOutbillGoods> newAdd = new ArrayList<>();//新选择数据
            List<String> trayCodes = new ArrayList<>();//当前出库涉及的托盘
            if(CollectionUtil.isNotEmpty(wmsDryOutbill.getWmsDryOutbillGoodsList())){
                for (DryOutbillGoods goods:wmsDryOutbill.getWmsDryOutbillGoodsList()) {
                    if(StringUtils.isEmpty(goods.getId())){
                        newAdd.add(goods);
                    }
                }
                if(CollectionUtil.isNotEmpty(newAdd)){
                    //验证是否被别人选择过
                    List<String> codes = newAdd.stream().map(goods->goods.getPartsCode()).collect(Collectors.toList());
                    LambdaQueryWrapper<Tblstock> tblstockLambdaQueryWrapper = Wrappers.lambdaQuery();
                    tblstockLambdaQueryWrapper.eq(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                            .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                            .in(Tblstock::getPartsCode,codes);
                    List<Tblstock> tblstocks = tblstockMapper.selectList(tblstockLambdaQueryWrapper);
                    if(CollectionUtil.isNotEmpty(tblstocks)){
                        throw new ServiceException("机件号为【"+tblstocks.stream().map(tblstock -> tblstock.getPartsCode()).collect(Collectors.joining(","))+"】的货物已被使用，请重新查询货物数据");
                    }
                }
                trayCodes = newAdd.stream().map(goods->goods.getTrayCode()).distinct().collect(Collectors.toList());
            }
            if(CollectionUtil.isNotEmpty(outbillGoodsList)){
                if(CollectionUtil.isNotEmpty(trayCodes)){
                    for (DryOutbillGoods goods:outbillGoodsList) {
                        trayCodes.add(goods.getTrayCode());
                    }
                }else{
                    trayCodes = outbillGoodsList.stream().map(goods->goods.getTrayCode()).distinct().collect(Collectors.toList());
                }
            }
            //3、对比空库位和需要的托盘数量
            if(trayCodes.size()>0&&trayCodes.size()>locations.size()){
                return false;
            }
        }
        return true;
    }

}
