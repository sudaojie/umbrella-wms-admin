package com.ruoyi.wms.stock.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.enums.DryInbillStatusEnum;
import com.ruoyi.wms.enums.DryOutbillStatusTypeEnum;
import com.ruoyi.wms.enums.InBillNoPrefixEnum;
import com.ruoyi.wms.stock.domain.DryOutbillGoods;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.domain.WmsDryInbill;
import com.ruoyi.wms.stock.domain.WmsDryInbillGoods;
import com.ruoyi.wms.stock.mapper.DryOutbillGoodsMapper;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.stock.mapper.WmsDryInbillGoodsMapper;
import com.ruoyi.wms.stock.mapper.WmsDryInbillMapper;
import com.ruoyi.wms.stock.dto.PartsCode;
import com.ruoyi.wms.stock.dto.WmsDryInbillGoodsVo;
import com.ruoyi.wms.stock.vo.WmsDryOutbillGoodsVo;
import com.ruoyi.wms.stock.vo.WmsDryOutbillVo;
import com.ruoyi.wms.utils.SerialCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 晾晒入库单Service接口
 *
 * @author nf
 * @date 2023-03-10
 */
@Slf4j
@Service
public class WmsDryInbillService extends ServiceImpl<WmsDryInbillMapper, WmsDryInbill> {


    @Autowired
    private WmsDryInbillMapper wmsDryInbillMapper;
    @Autowired
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;
    @Autowired
    private TblstockMapper tblstockMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private WmsDryInbillGoodsMapper wmsDryInbillGoodsMapper;
    @Autowired
    private DryOutbillGoodsMapper wmsDryOutbillGoodsMapper;
    @Autowired
    private WmsDryInbillGoodsService wmsDryInbillGoodsService;
    @Autowired
    private SerialCodeUtils serialCodeUtils;
    @Autowired
    protected Validator validator;
    /**
     * 查询晾晒入库单
     *
     * @param id 晾晒入库单主键
     * @return 晾晒入库单
     */
    public WmsDryInbill selectWmsDryInbillById(String id){
        QueryWrapper<WmsDryInbill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsDryInbillMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询晾晒入库单
     *
     * @param ids 晾晒入库单 IDs
     * @return 晾晒入库单
     */
    public List<WmsDryInbill> selectWmsDryInbillByIds(String[] ids) {
        QueryWrapper<WmsDryInbill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsDryInbillMapper.selectList(queryWrapper);
    }

    /**
     * 查询晾晒入库单列表
     *
     * @param wmsDryInbill 晾晒入库单
     * @return 晾晒入库单集合
     */
    public List<WmsDryInbill> selectWmsDryInbillList(WmsDryInbill wmsDryInbill){
        QueryWrapper<WmsDryInbill> queryWrapper = getQueryWrapper(wmsDryInbill);
        return wmsDryInbillMapper.select(queryWrapper);
    }

    /**
     * 新增晾晒入库单
     *
     * @param wmsDryInbill 晾晒入库单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsDryInbill insertWmsDryInbill(WmsDryInbill wmsDryInbill){
        wmsDryInbill.setId(IdUtil.simpleUUID());
        wmsDryInbill.setDryInbillCode(serialCodeUtils.getOrderNo(InBillNoPrefixEnum.getPrefix(InBillNoPrefixEnum.LSRK.getCode())));
        wmsDryInbill.setDryInbillStatus(DryInbillStatusEnum.WAIT.getCode());
        wmsDryInbill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsDryInbillMapper.insert(wmsDryInbill);
        takeDetailList(wmsDryInbill);
        return wmsDryInbill;
    }
    /**
     * 开始执行晾晒入库单
     *
     * @param wmsDryInbill 晾晒入库单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsDryInbill startTask(WmsDryInbill wmsDryInbill){
        wmsDryInbill.setDryInbillStatus(DryInbillStatusEnum.GROUPIN.getCode());
        wmsDryInbillMapper.updateById(wmsDryInbill);
        LambdaUpdateWrapper<WmsDryInbillGoods> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(WmsDryInbillGoods::getDryInbillStatus,DryInbillStatusEnum.GROUPIN.getCode())
                .eq(WmsDryInbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .eq(WmsDryInbillGoods::getDryInbillCode,wmsDryInbill.getDryInbillCode());
        wmsDryInbillGoodsMapper.update(null,updateWrapper);
        return wmsDryInbill;
    }

    /**
     * 修改晾晒入库单
     *
     * @param wmsDryInbill 晾晒入库单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsDryInbill updateWmsDryInbill(WmsDryInbill wmsDryInbill){
        if(StringUtils.isEmpty(wmsDryInbill.getId())){
            throw new ServiceException("数据主键缺失");
        }
        wmsDryInbillMapper.updateById(wmsDryInbill);
        takeDetailList(wmsDryInbill);
        return wmsDryInbill;
    }

    public void takeDetailList(WmsDryInbill wmsDryInbill){
        if(StringUtils.isNotEmpty(wmsDryInbill.getWmsDryInbillGoodsList())){
            List<WmsDryInbillGoods> newList = new ArrayList<>();
            List<WmsDryInbillGoods> oldList = new ArrayList<>();
            List<String> partsCodeList = new ArrayList<>();
            for (WmsDryInbillGoods detail :wmsDryInbill.getWmsDryInbillGoodsList()) {
                if(StringUtils.isEmpty(detail.getId())){
                    detail.setDryInbillStatus(DryInbillStatusEnum.WAIT.getCode());
                    detail.setDryInbillCode(wmsDryInbill.getDryInbillCode());
                    newList.add(detail);
                    partsCodeList.add(detail.getPartsCode());
                }else{
                    oldList.add(detail);
                }
            }
            if(newList.size()>0){
                /*LambdaQueryWrapper<DryOutbillGoods> queryWrapper = Wrappers.lambdaQuery();
                queryWrapper.eq(DryOutbillGoods::getLockStatus, LockEnum.LOCKED.getCode())
                        .eq(DryOutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .in(DryOutbillGoods::getPartsCode,partsCodeList);
                List<DryOutbillGoods> goodsList = wmsDryOutbillGoodsMapper.selectList(queryWrapper);
                if(CollectionUtil.isNotEmpty(goodsList)){
                    String code = goodsList.stream().map(goods->goods.getPartsCode()).collect(Collectors.joining(","));
                    throw new ServiceException("机件号为【"+code+"】的数据已被使用，请重新选择数据");
                }*/
                wmsDryInbillGoodsService.saveBatch(newList);
                LambdaUpdateWrapper<DryOutbillGoods> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.set(DryOutbillGoods::getLockStatus, LockEnum.LOCKED.getCode())
                            .eq(DryOutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                            .in(DryOutbillGoods::getPartsCode,partsCodeList);
                wmsDryOutbillGoodsMapper.update(null,updateWrapper);
            }
        }
    }

    /**
     * 批量删除晾晒入库单
     *
     * @param ids 需要删除的晾晒入库单主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsDryInbillByIds(String[] ids){
        List<WmsDryInbill> wmsDryInbills = new ArrayList<>();
        for (String id : ids) {
            WmsDryInbill wmsDryInbill = new WmsDryInbill();
            wmsDryInbill.setId(id);
            wmsDryInbill.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsDryInbills.add(wmsDryInbill);
        }
        List<WmsDryInbillGoods> goodsList = wmsDryInbillMapper.selectPartsCodeByIds(Arrays.asList(ids));
        if(CollectionUtil.isNotEmpty(goodsList)){
            List<String> partsCodeList = goodsList.stream().map(goods-> goods.getPartsCode()).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(partsCodeList)){
                LambdaUpdateWrapper<DryOutbillGoods> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.set(DryOutbillGoods::getLockStatus, LockEnum.NOTLOCK.getCode())
                        .eq(DryOutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .in(DryOutbillGoods::getPartsCode,partsCodeList);
                wmsDryOutbillGoodsMapper.update(null,updateWrapper);
            }
            goodsList.stream().forEach(goods->goods.setDelFlag(DelFlagEnum.DEL_YES.getCode()));
            wmsDryInbillGoodsService.updateBatchById(goodsList);
        }
        return super.updateBatchById(wmsDryInbills) ? 1 : 0;
    }

    /**
     * 删除晾晒入库单信息
     *
     * @param id 晾晒入库单主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsDryInbillById(String id){
        WmsDryInbill wmsDryInbill = new WmsDryInbill();
        wmsDryInbill.setId(id);
        wmsDryInbill.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsDryInbillMapper.updateById(wmsDryInbill);
    }

    public QueryWrapper<WmsDryInbill> getQueryWrapper(WmsDryInbill wmsDryInbill) {
        QueryWrapper<WmsDryInbill> queryWrapper = new QueryWrapper<>();
        if (wmsDryInbill != null) {
            wmsDryInbill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsDryInbill.getDelFlag());
            //晾晒入库单号
            if (StrUtil.isNotEmpty(wmsDryInbill.getDryInbillCode())) {
                queryWrapper.like("dry_inbill_code",wmsDryInbill.getDryInbillCode());
            }
            //晾晒入库状态
            if (StrUtil.isNotEmpty(wmsDryInbill.getDryInbillStatus())) {
                queryWrapper.in("dry_inbill_status", Arrays.asList(wmsDryInbill.getDryInbillStatus().split(",")));
            }
            //入库时间
            if (StringUtils.isNotEmpty(wmsDryInbill.getParams())&&StringUtils.isNotNull(wmsDryInbill.getParams().get("beginDryInbillTime"))) {
                String begin = wmsDryInbill.getParams().get("beginDryInbillTime")+" 00:00:00";
                String end = wmsDryInbill.getParams().get("endDryInbillTime")+" 23:59:59";
                queryWrapper.between("dry_inbill_time",begin,end);
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param wmsDryInbillList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<WmsDryInbill> wmsDryInbillList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsDryInbillList) || wmsDryInbillList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WmsDryInbill wmsDryInbill : wmsDryInbillList) {
            if(null==wmsDryInbill){
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                WmsDryInbill u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsDryInbill);
                    wmsDryInbill.setId(IdUtil.simpleUUID());
                    wmsDryInbill.setCreateBy(operName);
                    wmsDryInbill.setCreateTime(new Date());
                    wmsDryInbill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsDryInbillMapper.insert(wmsDryInbill);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsDryInbill);
                    //todo 验证
                    //int count = wmsDryInbillMapper.checkCode(wmsDryInbill);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        wmsDryInbill.setId(u.getId());
                        wmsDryInbill.setUpdateBy(operName);
                        wmsDryInbill.setUpdateTime(new Date());
                        wmsDryInbillMapper.updateById(wmsDryInbill);
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
     * PDA获取晾晒入库单详细信息
     * @param id
     * @return
     */
    public List<WmsDryInbillGoodsVo> getDataById(String id) {
        List<WmsDryInbillGoodsVo> voList = new ArrayList<>();
        WmsDryInbill wmsDryInbill = wmsDryInbillMapper.selectById(id);
        if(StringUtils.isNull(wmsDryInbill)||
                (  !wmsDryInbill.getDryInbillStatus().equals(DryInbillStatusEnum.GROUPIN.getCode())
                    && !wmsDryInbill.getDryInbillStatus().equals(DryInbillStatusEnum.ING.getCode())
                )
        ){
            throw new ServiceException("未查询到对应数据，请确认晾晒入库单状态");
        }else{
            LambdaQueryWrapper<WmsDryInbillGoods> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(WmsDryInbillGoods::getDryInbillCode,wmsDryInbill.getDryInbillCode())
                    .in(WmsDryInbillGoods::getDryInbillStatus,DryInbillStatusEnum.GROUPIN.getCode(),DryInbillStatusEnum.ING.getCode())
                    .eq(WmsDryInbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
            List<WmsDryInbillGoods> goodsList = wmsDryInbillGoodsMapper.selectList(queryWrapper);
            List<String> trayCodes = goodsList.stream().map(goods->goods.getTrayCode()).distinct().collect(Collectors.toList());
            List<String> partsCode = goodsList.stream().map(goods->goods.getPartsCode()).distinct().collect(Collectors.toList());
            QueryWrapper<Tblstock> tblstockLambdaQueryWrapper = new QueryWrapper<>();
            tblstockLambdaQueryWrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode())
                    .in("tray_code",trayCodes)
                    .orderByAsc("SUBSTR(only_code,5,LENGTH(only_code))");
            List<Tblstock> tblstocks = tblstockMapper.selectList(tblstockLambdaQueryWrapper);

            WmsDryInbillGoodsVo vo = null;
            for (String trayCode:trayCodes) {
                vo = new WmsDryInbillGoodsVo();
                vo.setDryInbillCode(wmsDryInbill.getDryInbillCode());
                vo.setTrayCode(trayCode);
                List<PartsCode> partsCodeList = new ArrayList<>();
                PartsCode partsCode1 = null;
                String status = "全部";
                for (Tblstock tblstock:tblstocks) {
                    if(tblstock.getTrayCode().equals(trayCode)){
                        partsCode1 = new PartsCode();
                        partsCode1.setPartsCode(tblstock.getPartsCode());
                        if(partsCode.contains(tblstock.getPartsCode())){
                            partsCode1.setStatus(true);
                        }else{
                            status = "部分";
                            partsCode1.setStatus(false);
                        }
                        partsCodeList.add(partsCode1);
                    }
                }
                vo.setPartsCodeList(partsCodeList);
                vo.setStatus(status);
                voList.add(vo);
            }
        }
        return voList;
    }

    /**
     * PDA获取晾晒出库单详细信息
     * @return
     */
    public List<WmsDryOutbillVo> getOutData() {
        List<WmsDryOutbillVo> voList = new ArrayList<>();
        LambdaQueryWrapper<DryOutbillGoods> dryOutbillGoodsQueryWrapper = Wrappers.lambdaQuery();
        dryOutbillGoodsQueryWrapper.select(DryOutbillGoods::getDryOutbillCode,DryOutbillGoods::getPartsCode,DryOutbillGoods::getTrayCode)
                .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .eq(DryOutbillGoods::getDryOutbillStatus, DryOutbillStatusTypeEnum.END.getCode())
                .eq(DryOutbillGoods::getLockStatus,LockEnum.NOTLOCK.getCode());
        //已出库未创建入库单的晾晒出库单信息
        List<DryOutbillGoods> dryOutbillGoodList = wmsDryOutbillGoodsMapper.selectList(dryOutbillGoodsQueryWrapper);
        if(CollUtil.isNotEmpty(dryOutbillGoodList)){
            //所有的托盘号
            List<String> trayCodes = dryOutbillGoodList.stream().map(goods->goods.getTrayCode()).distinct().collect(Collectors.toList());
            //所有的出库单号
            List<String> dryOutbillCodes = dryOutbillGoodList.stream().map(goods->goods.getDryOutbillCode()).distinct().collect(Collectors.toList());
            LambdaQueryWrapper<Tblstock> tblstockLambdaQueryWrapper = Wrappers.lambdaQuery();
            tblstockLambdaQueryWrapper.eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(Tblstock::getTrayCode,trayCodes);
            List<Tblstock> tblstocks = tblstockMapper.selectList(tblstockLambdaQueryWrapper);
            //拼接返回数据
            for (String outbill:dryOutbillCodes) {
                //出库单vo
                WmsDryOutbillVo outbillVo = new WmsDryOutbillVo();
                outbillVo.setDryOutbillCode(outbill);
                outbillVo.setTrayList(groupDryOutbill(outbill,dryOutbillGoodList,tblstocks));
                voList.add(outbillVo);
            }
        }
        return voList;
    }

    private List<WmsDryOutbillGoodsVo> groupDryOutbill(String outBill,List<DryOutbillGoods> goodsList,List<Tblstock> tblstockList){
        List<WmsDryOutbillGoodsVo> trayVoList = new ArrayList<>();
        //出库单下所有的托盘
        List<String> trayCodeList = goodsList.stream().filter(out -> outBill.equals(out.getDryOutbillCode())).map(DryOutbillGoods::getTrayCode).distinct().collect(Collectors.toList());
        for (String trayCode:trayCodeList) {
            WmsDryOutbillGoodsVo trayVo = new WmsDryOutbillGoodsVo();
            trayVo.setTrayCode(trayCode);
            //库存里该托盘上的机件信息
            List<String> tblPartsCodeList = tblstockList.stream().filter(t -> trayCode.equals(t.getTrayCode())).map(Tblstock::getPartsCode).collect(Collectors.toList());
            //该托盘上实际需晾晒的机件信息
            List<String> dryPartsCodeList = goodsList.stream().filter(g -> trayCode.equals(g.getTrayCode())).map(DryOutbillGoods::getPartsCode).collect(Collectors.toList());
            trayVo.setStatus(tblPartsCodeList.size()==dryPartsCodeList.size()?"全部":"部分");
            trayVo.setPartsCodeList(tblPartsCodeList.stream().map(code -> {
                PartsCode partsCode = new PartsCode();
                partsCode.setPartsCode(code);
                partsCode.setStatus(dryPartsCodeList.contains(code));
                return partsCode ;
            }).collect(Collectors.toList()));
            trayVoList.add(trayVo);
        }
        return trayVoList;
    }

}
