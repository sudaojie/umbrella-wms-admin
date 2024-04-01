package com.ruoyi.wms.stock.service;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wms.enums.GoodsInfoEnum;
import com.ruoyi.wms.enums.ValidityStatusEnum;
import com.ruoyi.wms.enums.WarningConfigEnum;
import com.ruoyi.wms.stock.domain.WarehouseWarning;
import com.ruoyi.wms.stock.domain.WmsWarningConfig;
import com.ruoyi.wms.stock.mapper.WarehouseWarningMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Validator;

/**
 * 库存预警Service接口
 *
 * @author ruoyi
 * @date 2023-03-10
 */
@Slf4j
@Service
public class WarehouseWarningService extends ServiceImpl<WarehouseWarningMapper, WarehouseWarning> {

    @Autowired(required = false)
    private WarehouseWarningMapper warehouseWarningMapper;
    @Autowired
    protected Validator validator;
    //默认有效期时间
    private static String defaultValidityTime = "30";
    //默认滞压时间
    private static String defaultDetainedTime = "30";

    /**
     * 查询库存总览
     *
     * @param id 库存总览主键
     * @return 库存总览
     */
    public WarehouseWarning selectWmsWarehouseTblstockById(String id){
        QueryWrapper<WarehouseWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return warehouseWarningMapper.selectOne(queryWrapper);
    }

    /**
     * 查询库存总览列表
     *
     * @param warehouseWarning 库存总览
     * @return 库存总览集合
     */
    public List<WarehouseWarning> selectWmsWarehouseTblstockList(WarehouseWarning warehouseWarning){
        QueryWrapper<WarehouseWarning> queryWrapper = getQueryWrapper(warehouseWarning);
        List<WarehouseWarning> warningList = warehouseWarningMapper.select(queryWrapper);
        for (WarehouseWarning warning:warningList){
            warning.setLockStatus(LockEnum.NOTLOCK.getCode());
            QueryWrapper wrapper = getQueryWrapper(warning);
            //可用数量查询
            int count = warehouseWarningMapper.selectByGoodsCode(wrapper);
            warning.setAvailableNum(String.valueOf(count));
            //计算缺货量
            int lack = Integer.parseInt(warning.getInventoryCountMin().subtract(warning.getGoodsNum()).toString());
            if (lack > 0){
                warning.setLackNum(lack);
            }
            //计算超储量
            int over = Integer.parseInt(warning.getGoodsNum().subtract(warning.getInventoryCountMax()).toString());
            if (over > 0){
                warning.setOverNum(over);
            }
        }
        return warningList;
    }

    /**
     * 查询库存有效期列表
     * @param warehouseWarning
     * @return
     */
    public List<WarehouseWarning> selectWmsValidityList(WarehouseWarning warehouseWarning) {
        //获取有效期预警列表
        QueryWrapper wrapper = getQueryValidityWrapper(warehouseWarning);
        List<WarehouseWarning> warningList = warehouseWarningMapper.selectWmsValidityList(wrapper);
        for (WarehouseWarning validity: warningList){
            if (validity.getOverTime() < 0){
                validity.setStatus(ValidityStatusEnum.EXPIRED.getCode());
            }else {
                validity.setStatus(ValidityStatusEnum.WAIT.getCode());
            }
        }
        return warningList;
    }

    /**
     * 查询库存滞压预警列表
     * @param warehouseWarning
     * @return
     */
    public List<WarehouseWarning> selectDetainedList(WarehouseWarning warehouseWarning) {
        //获取策略中有效期配置
        WmsWarningConfig wmsWarningConfig = new WmsWarningConfig();
        wmsWarningConfig.setConfigKey(WarningConfigEnum.DETAINED.getCode());
        QueryWrapper queryWrapper = getQueryConfigWrapper(wmsWarningConfig);
        WmsWarningConfig config = warehouseWarningMapper.selectConfig(queryWrapper);
        if (config != null){
            String value = config.getConfigValue();
            warehouseWarning.setWarningProxy(value);
        }else {
            warehouseWarning.setWarningProxy(String.valueOf(defaultDetainedTime));
        }
        //查询滞压预警列表
        QueryWrapper wrapper = getQueryDetainedWrapper(warehouseWarning);
        List<WarehouseWarning> warehouseWarningList = warehouseWarningMapper.selectDetainedList(wrapper);
        return warehouseWarningList;
    }

    public QueryWrapper<WarehouseWarning> getQueryWrapper(WarehouseWarning wmsWarehouseTblstock) {
        QueryWrapper<WarehouseWarning> queryWrapper = new QueryWrapper<>();
        if (wmsWarehouseTblstock != null) {
            wmsWarehouseTblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode(  ));
            queryWrapper.eq("t.del_flag",wmsWarehouseTblstock.getDelFlag());
            queryWrapper.eq("g.del_flag",wmsWarehouseTblstock.getDelFlag());
            //货物编码
            if (StrUtil.isNotEmpty(wmsWarehouseTblstock.getGoodsCode())) {
                queryWrapper.like("t.goods_code",wmsWarehouseTblstock.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(wmsWarehouseTblstock.getGoodsName())) {
                queryWrapper.like("t.goods_name",wmsWarehouseTblstock.getGoodsName());
            }
            //锁定状态
            if (StrUtil.isNotEmpty(wmsWarehouseTblstock.getLockStatus())) {
                queryWrapper.eq("t.lock_status",wmsWarehouseTblstock.getLockStatus());
            }
        }
        return queryWrapper;
    }

    public QueryWrapper<WarehouseWarning> getQueryValidityWrapper(WarehouseWarning wmsWarehouseTblstock) {
        QueryWrapper<WarehouseWarning> queryWrapper = new QueryWrapper<>();
        if (wmsWarehouseTblstock != null) {
            wmsWarehouseTblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("t.del_flag", wmsWarehouseTblstock.getDelFlag());
            queryWrapper.le("t.overTime",wmsWarehouseTblstock.getWarningProxy());
            //货物编码
            if (StrUtil.isNotEmpty(wmsWarehouseTblstock.getGoodsCode())) {
                queryWrapper.like("t.goods_code",wmsWarehouseTblstock.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(wmsWarehouseTblstock.getGoodsName())) {
                queryWrapper.like("t.goods_name",wmsWarehouseTblstock.getGoodsName());
            }
            //有效日期
            if (StrUtil.isNotEmpty(wmsWarehouseTblstock.getLockStatus())) {
                queryWrapper.eq("t.lock_status",wmsWarehouseTblstock.getLockStatus());
            }
        }
        return queryWrapper;
    }

    public QueryWrapper<WmsWarningConfig> getQueryConfigWrapper(WmsWarningConfig warningConfig) {
        QueryWrapper<WmsWarningConfig> queryWrapper = new QueryWrapper<>();
        if (warningConfig != null) {
            warningConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",warningConfig.getDelFlag());
            warningConfig.setConfigType(GoodsInfoEnum.ENABLE.getCode());
            queryWrapper.eq("config_type",warningConfig.getConfigType());
            //参数键名
            if (StrUtil.isNotEmpty(warningConfig.getConfigKey())) {
                queryWrapper.eq("config_key",warningConfig.getConfigKey());
            }
        }
        return queryWrapper;
    }

    public QueryWrapper<WarehouseWarning> getQueryDetainedWrapper(WarehouseWarning warehouseWarning) {
        QueryWrapper<WarehouseWarning> queryWrapper = new QueryWrapper<>();
        if (warehouseWarning != null) {
            warehouseWarning.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",warehouseWarning.getDelFlag());
            //滞压策略
            queryWrapper.gt("listingDate", warehouseWarning.getWarningProxy());
            //货物编码
            if (StrUtil.isNotEmpty(warehouseWarning.getGoodsCode())) {
                queryWrapper.like("goods_code", warehouseWarning.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(warehouseWarning.getGoodsName())) {
                queryWrapper.like("goods_name", warehouseWarning.getGoodsName());
            }
        }
        return queryWrapper;
    }

    public WmsWarningConfig getConfig() {
        //获取策略中有效期配置
        WmsWarningConfig wmsWarningConfig = new WmsWarningConfig();
        wmsWarningConfig.setConfigKey(WarningConfigEnum.VALIDITY.getCode());
        QueryWrapper queryWrapper = getQueryConfigWrapper(wmsWarningConfig);
        wmsWarningConfig = warehouseWarningMapper.selectConfig(queryWrapper);
        if (wmsWarningConfig != null){
            return wmsWarningConfig;
        }else {
            throw new ServiceException("未配置有效期预警阈值，请先配置");
        }
    }
}
