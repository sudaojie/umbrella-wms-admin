package com.ruoyi.wms.statistics.service;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.InBillStatusEnum;
import com.ruoyi.wms.statistics.domain.InBillStatistic;
import com.ruoyi.wms.statistics.mapper.InBillStatisticMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.warehousing.mapper.InBillMapper;
import javax.validation.Validator;
import java.util.List;

/**
 * 入库单信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-17
 */
@Slf4j
@Service
public class InBillStatisticService extends ServiceImpl<InBillStatisticMapper, InBillStatistic> {

    @Autowired(required = false)
    private InBillStatisticMapper inBillStatisticMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询入库单信息
     *
     * @param id 入库单信息主键
     * @return 入库单信息
     */
    public InBillStatistic selectInBillById(String id){
        QueryWrapper<InBillStatistic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return inBillStatisticMapper.selectOne(queryWrapper);
    }

    /**
     * 查询入库单信息列表
     *
     * @param inBill 入库单信息
     * @return 入库单信息集合
     */
    public List<InBillStatistic> selectInBillList(InBillStatistic inBill){
        QueryWrapper<InBillStatistic> queryWrapper = getQueryWrappers(inBill);
        return inBillStatisticMapper.selectInBillList(queryWrapper);
    }

    public QueryWrapper<InBillStatistic> getQueryWrappers(InBillStatistic inBill) {
        QueryWrapper<InBillStatistic> queryWrapper = new QueryWrapper<>();
        if (inBill != null) {
            inBill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("w.del_flag",inBill.getDelFlag());
            inBill.setInBillStatus(InBillStatusEnum.FOUR.getCode());
            queryWrapper.eq("w.in_bill_status",inBill.getInBillStatus());
            //入库单号
            if (StrUtil.isNotEmpty(inBill.getInBillCode())) {
                queryWrapper.like("w.in_bill_code",inBill.getInBillCode());
            }
            //供应商
            if (StrUtil.isNotEmpty(inBill.getSupplierCode())) {
                queryWrapper.eq("d.supplier_code",inBill.getSupplierCode());
            }
            //入库类别
            if (StrUtil.isNotEmpty(inBill.getInBillCategory())) {
                queryWrapper.eq("w.in_bill_category",inBill.getInBillCategory());
            }
            //入库时间
            if (inBill.getParams().size()>0) {
                queryWrapper.gt("w.create_time",inBill.getParams().get("beginTime"));
                queryWrapper.lt("w.create_time",inBill.getParams().get("endTime"));
            }
            //货物名称
            if (StrUtil.isNotEmpty(inBill.getGoodsName())) {
                queryWrapper.like("d.goods_name",inBill.getGoodsName());
            }
        }
        return queryWrapper;
    }

    public QueryWrapper<InBillStatistic> getQueryWrapper(InBillStatistic inBill) {
        QueryWrapper<InBillStatistic> queryWrapper = new QueryWrapper<>();
        if (inBill != null) {
            inBill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",inBill.getDelFlag());
            //入库单号
            if (StrUtil.isNotEmpty(inBill.getInBillCode())) {
                queryWrapper.like("in_bill_code",inBill.getInBillCode());
            }
            //批次
            if (StrUtil.isNotEmpty(inBill.getCharg())) {
                queryWrapper.like("charg",inBill.getCharg());
            }
            //入库单状态;(1.待收货  2.验货中  3.上架中   4.已上架  5.已作废)
            if (StrUtil.isNotEmpty(inBill.getInBillStatus())) {
                queryWrapper.eq("in_bill_status",inBill.getInBillStatus());
            }
            //入库类别;(1.期初入库 2.普通入库  3.盘盈入库 4.晾晒入库  5.其他入库)
            if (StrUtil.isNotEmpty(inBill.getInBillCategory())) {
                queryWrapper.eq("in_bill_category",inBill.getInBillCategory());
            }
            //重量(kg)
            if (inBill.getWeight() != null) {
                queryWrapper.eq("weight",inBill.getWeight());
            }
            //体积(m³)
            if (inBill.getVolume() != null) {
                queryWrapper.eq("volume",inBill.getVolume());
            }
            //根据文号
            if (StrUtil.isNotEmpty(inBill.getDocNo())) {
                queryWrapper.like("doc_no",inBill.getDocNo());
            }
        }
        return queryWrapper;
    }
}
