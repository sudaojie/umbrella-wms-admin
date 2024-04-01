package com.ruoyi.wms.statistics.service;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.OutBillStatusEnum;
import com.ruoyi.wms.statistics.domain.OutBillStatistic;
import com.ruoyi.wms.statistics.mapper.OutBillStatisticMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.ruoyi.wms.outbound.domain.OutBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
/**
 * 出库单信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-18
 */
@Slf4j
@Service
public class OutBillStatisticService extends ServiceImpl<OutBillStatisticMapper, OutBill> {

    @Autowired(required = false)
    private OutBillStatisticMapper outBillMapper;

    /**
     * 查询出库单信息列表
     *
     * @param outBill 出库单信息
     * @return 出库单信息集合
     */
    public List<OutBillStatistic> selectOutBillList(OutBillStatistic outBill){
        QueryWrapper<OutBillStatistic> queryWrapper = getQueryWrapper(outBill);
        return outBillMapper.selectOutBillList(queryWrapper);
    }


    public QueryWrapper<OutBillStatistic> getQueryWrapper(OutBillStatistic outBill) {
        QueryWrapper<OutBillStatistic> queryWrapper = new QueryWrapper<>();
        if (outBill != null) {
            outBill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("w.del_flag",outBill.getDelFlag());
            outBill.setOutBillStatus(OutBillStatusEnum.ENDSTATUS.getCode());
            queryWrapper.eq("w.out_bill_status",outBill.getOutBillStatus());
            //出库单号
            if (StrUtil.isNotEmpty(outBill.getOutBillCode())) {
                queryWrapper.like("w.out_bill_code",outBill.getOutBillCode());
            }
            //出库类别;(1.正常出库 2.调拨出库  3.报损出库  4.盘亏出库)
            if (StrUtil.isNotEmpty(outBill.getOutBillCategory())) {
                queryWrapper.eq("w.out_bill_category",outBill.getOutBillCategory());
            }
            //供应商
            if (StrUtil.isNotEmpty(outBill.getSupplierCode())) {
                queryWrapper.eq("wo.supplier_code",outBill.getSupplierCode());
            }
            //入库时间
            if (outBill.getParams().size()>0) {
                queryWrapper.gt("w.create_time",outBill.getParams().get("beginTime"));
                queryWrapper.lt("w.create_time",outBill.getParams().get("endTime"));
            }
            //货物名称
            if (StrUtil.isNotEmpty(outBill.getGoodsName())) {
                queryWrapper.eq("wo.goods_name",outBill.getGoodsName());
            }
        }
        return queryWrapper;
    }

}
