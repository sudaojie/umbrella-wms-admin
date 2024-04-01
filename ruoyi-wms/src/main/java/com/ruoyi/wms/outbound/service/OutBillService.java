package com.ruoyi.wms.outbound.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysFileService;
import com.ruoyi.wms.basics.domain.Temp;
import com.ruoyi.wms.basics.service.TempService;
import com.ruoyi.wms.enums.OutBillEnum;
import com.ruoyi.wms.enums.OutBillNoPrefixEnum;
import com.ruoyi.wms.outbound.domain.OutBill;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import com.ruoyi.wms.outbound.dto.OutBillPrintDto;
import com.ruoyi.wms.outbound.mapper.OutBillMapper;
import com.ruoyi.wms.outbound.mapper.OutbillGoodsMapper;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.utils.SerialCodeUtils;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 出库单信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-07
 */
@Slf4j
@Service
public class OutBillService extends ServiceImpl<OutBillMapper, OutBill> {

    @Autowired
    protected Validator validator;
    @Autowired
    private OutBillMapper outBillMapper;
    @Autowired
    private TblstockMapper tblstockMapper;
    @Autowired
    private OutbillGoodsService outbillGoodsService;
    @Autowired
    private OutbillGoodsMapper outbillGoodsMapper;
    @Autowired
    private SerialCodeUtils serialCodeUtils;
    @Autowired
    private ISysFileService sysFileService;
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private TempService tempService;
    /**
     * 查询出库单信息
     *
     * @param id 出库单信息主键
     * @return 出库单信息
     */
    public OutBill selectOutBillById(String id) {
        QueryWrapper<OutBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        OutBill outBill = outBillMapper.selectOne(queryWrapper);
        QueryWrapper<OutbillGoods> queryGoodsWrapper = new QueryWrapper<>();
        queryGoodsWrapper.eq("out_bill_code",outBill.getOutBillCode());
        queryGoodsWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        List<OutbillGoods> goodsList = outbillGoodsService.list(queryGoodsWrapper);
        outBill.setOutbillGoodsList(goodsList);
        return outBill;
    }
    /**
     * 查询出库单打印信息
     *
     * @param id 出库单信息主键
     * @return 出库单信息
     */
    public OutBill getPrintData(String id) {
        OutBill outBill = outBillMapper.selectData(id);
        String img = IdUtil.randomUUID();
        String text = "code=" + outBill.getOutBillNo()+";type=outbill";
        String filePath = "/qrcode/" + img + ".png";
        try {
            CodeGeneratorUtil.generateQRCodeImage(text, 100, 100, RuoYiConfig.getProfile() + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outBill.setImgCode(img + ".png");
        outBill.setUrl("/profile"+filePath);
        List<OutbillGoods> goodsList = outbillGoodsService.listByOutBillCode(outBill.getOutBillCode());
        outBill.setOutbillGoodsList(goodsList);
        return outBill;
    }


    /**
     * 根据ids查询出库单信息
     *
     * @param ids 出库单信息 IDs
     * @return 出库单信息
     */
    public List<OutBill> selectOutBillByIds(String[] ids) {
        QueryWrapper<OutBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return outBillMapper.selectList(queryWrapper);
    }

    /**
     * 查询出库单信息列表
     *
     * @param outBill 出库单信息
     * @return 出库单信息集合
     */
    public List<OutBill> selectOutBillList(OutBill outBill) {
        QueryWrapper<OutBill> queryWrapper = getQueryWrapper(outBill);
        return outBillMapper.select(queryWrapper);
    }

    /**
     * 新增出库单信息
     *
     * @param outBill 出库单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public OutBill insertOutBill(OutBill outBill) {
        outBill.setId(IdUtil.simpleUUID());
        outBill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        outBill.setOutBillStatus(OutBillEnum.WAIT.getCode());
        outBillMapper.insert(outBill);
        return outBill;
    }

    /**
     * 修改出库单信息
     *
     * @param outBill 出库单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public OutBill updateOutBill(OutBill outBill) {
        OutBill old = outBillMapper.selectById(outBill.getId());
        if(!old.getOutBillStatus().equals(OutBillEnum.WAIT.getCode())){
            throw new ServiceException("该出库单状态已更改，不可再进行数据维护");
        }
        outBillMapper.updateById(outBill);
        if(OutBillEnum.CANCEL.getCode().equals(outBill.getOutBillStatus())){
            OutbillGoods goods = new OutbillGoods();
            goods.setOutBillCode(outBill.getOutBillCode());
            List<OutbillGoods> goodsList = outbillGoodsService.selectOutbillGoodsList(goods);
            List<String> onlyCodes = goodsList.stream().map(outbillGoods -> outbillGoods.getOnlyCode()).collect(Collectors.toList());
            //解锁库存数据
            LambdaUpdateWrapper<Tblstock> tblstockLambdaUpdate = new LambdaUpdateWrapper<>();
            tblstockLambdaUpdate.set(Tblstock::getLockStatus, LockEnum.NOTLOCK.getCode())
                    .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(Tblstock::getOnlyCode,onlyCodes);
            tblstockMapper.update(null,tblstockLambdaUpdate);
            //删除数据
            LambdaUpdateWrapper<OutbillGoods> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(OutbillGoods::getDelFlag,DelFlagEnum.DEL_YES.getCode())
                    .eq(OutbillGoods::getOutBillCode,outBill.getOutBillCode());
            outbillGoodsMapper.update(null,updateWrapper);
        }else  if(StringUtils.isNotEmpty(outBill.getOutbillGoodsList())){
            if(CollectionUtil.isNotEmpty(outBill.getIds())){
                outbillGoodsService.deleteOutbillGoodsByIds(outBill.getIds().toArray(new String[outBill.getIds().size()]));
            }
            List<OutbillGoods> oldList = new ArrayList<>();
            List<OutbillGoods> newList = new ArrayList<>();
            for(OutbillGoods goods :outBill.getOutbillGoodsList()){
                if(StringUtils.isEmpty(goods.getId())){
                    goods.setId(IdUtils.simpleUUID());
                    goods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    goods.setOutBillCode(outBill.getOutBillCode());
                    goods.setCreateBy(SecurityUtils.getUsername());
                    goods.setCreateTime(new Date());
                    goods.setOutBillStatus(OutBillEnum.WAIT.getCode());
                    newList.add(goods);
                }else{
                    goods.setUpdateBy(SecurityUtils.getUsername());
                    goods.setUpdateTime(new Date());
                    oldList.add(goods);
                }
            }
            if(newList.size()>0){
                List<String> onlyCode = newList.stream().map(goods -> goods.getOnlyCode()).collect(Collectors.toList());
                QueryWrapper<Tblstock> tblstockQueryWrapper = new QueryWrapper<>();
                tblstockQueryWrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode())
                                .eq("lock_status",LockEnum.LOCKED.getCode())
                                .in("only_code",onlyCode);
                List<Tblstock> tblstocks = tblstockMapper.selectList(tblstockQueryWrapper);
                if(tblstocks.size()>0){
                    String codes = tblstocks.stream().map(tblstock -> tblstock.getOnlyCode()).collect(Collectors.joining(","));
                    throw new RuntimeException("唯一码为【"+codes+"】的货物已被占用");
                }
                outbillGoodsService.saveBatch(newList);
            }
            if(oldList.size()>0){
                outbillGoodsService.updateBatchById(oldList);
            }

            if(StringUtils.isNotEmpty(outBill.getOutbillGoodsList())){
                List<String> onlyCodes = outBill.getOutbillGoodsList().stream().map(outbillGoods -> outbillGoods.getOnlyCode()).collect(Collectors.toList());
                if(!onlyCodes.isEmpty()){
                    //锁定库存数据
                    LambdaUpdateWrapper<Tblstock> tblstockLambdaUpdate = new LambdaUpdateWrapper<>();
                    tblstockLambdaUpdate.set(Tblstock::getLockStatus, LockEnum.LOCKED.getCode())
                            .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                            .in(Tblstock::getOnlyCode,onlyCodes);
                    tblstockMapper.update(null,tblstockLambdaUpdate);
                }
            }
        }
        return outBill;
    }

    /**
     * 批量删除出库单信息
     *
     * @param ids 需要删除的出库单信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteOutBillByIds(String[] ids) {
        QueryWrapper<OutBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        List<OutBill> old = outBillMapper.selectList(queryWrapper);
        for (OutBill obj:old) {
            if(!obj.getOutBillStatus().equals(OutBillEnum.WAIT.getCode())){
                throw new ServiceException("出库单"+obj.getOutBillCode()+"的状态已更改，不可进行删除");
            }
        }
        List<OutBill> outBills = new ArrayList<>();
        for (String id : ids) {
            OutBill outBill = new OutBill();
            outBill.setId(id);
            outBill.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            outBills.add(outBill);
        }
        List<OutBill> outBillList = outBillMapper.selectBatchIds(Arrays.asList(ids));
        for(OutBill outBill:outBillList){
            UpdateWrapper<OutbillGoods> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("del_flag",DelFlagEnum.DEL_YES.getCode())
                    .eq("del_flag",DelFlagEnum.DEL_NO.getCode())
                    .eq("out_bill_code",outBill.getOutBillCode());
            outbillGoodsService.update(updateWrapper);
        }
        return super.updateBatchById(outBills) ? 1 : 0;
    }

    /**
     * 删除出库单信息信息
     *
     * @param id 出库单信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteOutBillById(String id) {
        OutBill outBill = new OutBill();
        outBill.setId(id);
        outBill.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return outBillMapper.updateById(outBill);
    }

    public QueryWrapper<OutBill> getQueryWrapper(OutBill outBill) {
        QueryWrapper<OutBill> queryWrapper = new QueryWrapper<>();
        if (outBill != null) {
            outBill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", outBill.getDelFlag());
            //出库单号
            if (StrUtil.isNotEmpty(outBill.getOutBillCode())) {
                queryWrapper.like("out_bill_code", outBill.getOutBillCode());
            }
            //出库时间
            if (StringUtils.isNotEmpty(outBill.getParams())&&StringUtils.isNotNull(outBill.getParams().get("beginOutBillTime"))) {
                String begin = outBill.getParams().get("beginOutBillTime")+" 00:00:00";
                String end = outBill.getParams().get("endOutBillTime")+" 23:59:59";
                queryWrapper.between("out_bill_time",begin,end);
            }
            //制单时间
            if (StringUtils.isNotEmpty(outBill.getParams())&&StringUtils.isNotNull(outBill.getParams().get("beginCreateTime"))) {
                String begin = outBill.getParams().get("beginCreateTime")+" 00:00:00";
                String end = outBill.getParams().get("endCreateTime")+" 23:59:59";
                queryWrapper.between("create_time",begin,end);
            }
            //出库状态;(1.待拣货  2.拣货中  3.已出库 4.已作废 !34.状态不为3和4)
            if (StrUtil.isNotEmpty(outBill.getOutBillStatus())) {
                if("!34".equals(outBill.getOutBillStatus())){
                    queryWrapper.ne("out_bill_status", "3");
                    queryWrapper.ne("out_bill_status", "4");
                }else{
                    queryWrapper.eq("out_bill_status", outBill.getOutBillStatus());
                }
            }
            //出库类别;(1.正常出库 2.调拨出库  3.报损出库  4.盘亏出库)
            if (StrUtil.isNotEmpty(outBill.getOutBillCategory())) {
                queryWrapper.eq("out_bill_category", outBill.getOutBillCategory());
            }
            //运货车牌号
            if (StrUtil.isNotEmpty(outBill.getFreightVehicleNo())) {
                queryWrapper.eq("freight_vehicle_no", outBill.getFreightVehicleNo());
            }
            //收货地址
            if (StrUtil.isNotEmpty(outBill.getReceiveAddress())) {
                queryWrapper.eq("receive_address", outBill.getReceiveAddress());
            }
            //发付单位
            if (StrUtil.isNotEmpty(outBill.getIssuingUnit())) {
                queryWrapper.eq("issuing_unit", outBill.getIssuingUnit());
            }
            //运输方式
            if (StrUtil.isNotEmpty(outBill.getShippingType())) {
                queryWrapper.eq("shipping_type", outBill.getShippingType());
            }
            //接收单位
            if (StrUtil.isNotEmpty(outBill.getReceivingUnit())) {
                queryWrapper.eq("receiving_unit", outBill.getReceivingUnit());
            }
            //发付依据
            if (StrUtil.isNotEmpty(outBill.getIssuingBasis())) {
                queryWrapper.eq("issuing_basis", outBill.getIssuingBasis());
            }
            //到站
            if (StrUtil.isNotEmpty(outBill.getAddress())) {
                queryWrapper.eq("address", outBill.getAddress());
            }
            //出库文号
            if (StrUtil.isNotEmpty(outBill.getOutBillNo())) {
                queryWrapper.eq("out_bill_no", outBill.getOutBillNo());
            }

        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param outBillList   模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName      操作人姓名
     * @return
     */
    public String importData(List<OutBill> outBillList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(outBillList) || outBillList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (OutBill outBill : outBillList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                OutBill u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, outBill);
                    outBill.setId(IdUtil.simpleUUID());
                    outBill.setCreateBy(operName);
                    outBill.setCreateTime(new Date());
                    outBill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    outBillMapper.insert(outBill);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, outBill);
                    //todo 验证
                    //int count = outBillMapper.checkCode(outBill);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    outBill.setId(u.getId());
                    outBill.setUpdateBy(operName);
                    outBill.setUpdateTime(new Date());
                    outBillMapper.updateById(outBill);
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
     * 创建出库单信息
     *
     * @param outBill 入库单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public String createOutBill(@NotNull OutBill outBill) {
        String outBillCode = serialCodeUtils.getOrderNo(OutBillNoPrefixEnum.getPrefix(outBill.getOutBillCategory()));//出库单号
        List<OutbillGoods> outbillGoodsList = outBill.getOutbillGoodsList();
        if(StringUtils.isNotEmpty(outbillGoodsList)){
            List<String> onlyCodes = outbillGoodsList.stream().map(outbillGoods -> outbillGoods.getOnlyCode()).collect(Collectors.toList());
            if(!onlyCodes.isEmpty()){
                QueryWrapper<Tblstock> tblstockQueryWrapper = new QueryWrapper<>();
                tblstockQueryWrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode())
                        .eq("lock_status",LockEnum.LOCKED.getCode())
                        .in("only_code",onlyCodes);
                List<Tblstock> tblstocks = tblstockMapper.selectList(tblstockQueryWrapper);
                if(tblstocks.size()>0){
                    String codes = tblstocks.stream().map(tblstock -> tblstock.getOnlyCode()).collect(Collectors.joining(","));
                    throw new RuntimeException("唯一码为【"+codes+"】的货物已被占用");
                }
                //锁定库存数据
                LambdaUpdateWrapper<Tblstock> tblstockLambdaUpdate = new LambdaUpdateWrapper<>();
                tblstockLambdaUpdate.set(Tblstock::getLockStatus, LockEnum.LOCKED.getCode())
                        .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .in(Tblstock::getOnlyCode,onlyCodes);
                tblstockMapper.update(null,tblstockLambdaUpdate);
            }
            //保存详情
            for (OutbillGoods outbillGoods : outbillGoodsList) {
                outbillGoods.setOutBillCode(outBillCode);
                outbillGoods.setOutBillStatus(OutBillEnum.WAIT.getCode());
                outbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            }
            outbillGoodsService.saveBatch(outbillGoodsList);
        }
        //保存抬头
        outBill.setOutBillCode(outBillCode);
        OutBill outBill1 = insertOutBill(outBill);
        return "成功";
    }

    public void printData(OutBillPrintDto dto, HttpServletResponse responseBody) {
        Map map = BeanUtil.beanToMap(dto);
        String tempId = configService.selectConfigByKey("wms.outbound.outbill.tempId");
        Temp temp = tempService.selectWmsWarehouseTempByTempId(tempId);
        SysFile file =  sysFileService.selectSysFileById(temp.getFileKey());
        map.put("imgCode",ServletUtils.getRequest().getHeader("Origin") + dto.getUrl());
        PdfUtil.createPDF(file.getPath(),map,responseBody);
    }
}
