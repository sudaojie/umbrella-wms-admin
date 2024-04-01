package com.ruoyi.wms.stock.service;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.wms.stock.domain.WmsAccount;
import com.ruoyi.wms.stock.mapper.WmsAccountMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * 库存台账Service接口
 *
 * @author ruoyi
 * @date 2023-03-15
 */
@Slf4j
@Service
public class WmsAccountService extends ServiceImpl<WmsAccountMapper, WmsAccount> {

    @Autowired(required = false)
    private WmsAccountMapper wmsAccountMapper;

    /**
     * 查询库存台账列表
     *
     * @param wmsWarehouseAccount 库存台账
     * @return 库存台账集合
     */
    public List<WmsAccount> selectWmsWarehouseAccountList(WmsAccount wmsWarehouseAccount){
        QueryWrapper<WmsAccount> queryWrapper = getQueryWrapper(wmsWarehouseAccount);
        return wmsAccountMapper.select(queryWrapper);
    }

    public QueryWrapper<WmsAccount> getQueryWrapper(WmsAccount wmsWarehouseAccount) {
        QueryWrapper<WmsAccount> queryWrapper = new QueryWrapper<>();
        if (wmsWarehouseAccount != null) {
            wmsWarehouseAccount.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsWarehouseAccount.getDelFlag());
            //单据编号
            if (StrUtil.isNotEmpty(wmsWarehouseAccount.getAccountCode())) {
                queryWrapper.like("account_code",wmsWarehouseAccount.getAccountCode());
            }
            //单据类型(0.入库单 1.出库单 2.无单上架 3.无单下架)
            if (StrUtil.isNotEmpty(wmsWarehouseAccount.getCodeType())) {
                queryWrapper.eq("code_type",wmsWarehouseAccount.getCodeType());
            }
            //批次号
            if (StrUtil.isNotEmpty(wmsWarehouseAccount.getCharg())) {
                queryWrapper.like("charg",wmsWarehouseAccount.getCharg());
            }
            //货物编码
            if (StrUtil.isNotEmpty(wmsWarehouseAccount.getGoodsCode())) {
                queryWrapper.like("goods_code",wmsWarehouseAccount.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(wmsWarehouseAccount.getGoodsName())) {
                queryWrapper.like("goods_name",wmsWarehouseAccount.getGoodsName());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }


}
