package com.ruoyi.wms.stock.service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wms.stock.domain.WmsWarningConfig;
import com.ruoyi.wms.stock.mapper.WmsWarningConfigMapper;
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
 * 库存预警策略Service接口
 *
 * @author ruoyi
 * @date 2023-03-13
 */
@Slf4j
@Service
public class WmsWarningConfigService extends ServiceImpl<WmsWarningConfigMapper, WmsWarningConfig> {

    @Autowired(required = false)
    private WmsWarningConfigMapper wmsWarningConfigMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询库存预警策略
     *
     * @param id 库存预警策略主键
     * @return 库存预警策略
     */
    public WmsWarningConfig selectWmsWarningConfigByID(String id){
        QueryWrapper<WmsWarningConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsWarningConfigMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询库存预警策略
     *
     * @param IDs 库存预警策略 IDs
     * @return 库存预警策略
     */
    public List<WmsWarningConfig> selectWmsWarningConfigByIds(String[] IDs) {
        QueryWrapper<WmsWarningConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("ID", Arrays.asList(IDs));
        return wmsWarningConfigMapper.selectList(queryWrapper);
    }

    /**
     * 查询库存预警策略列表
     *
     * @param wmsWarningConfig 库存预警策略
     * @return 库存预警策略集合
     */
    public List<WmsWarningConfig> selectWmsWarningConfigList(WmsWarningConfig wmsWarningConfig){
        QueryWrapper<WmsWarningConfig> queryWrapper = getQueryWrapper(wmsWarningConfig);
        return wmsWarningConfigMapper.select(queryWrapper);
    }

    /**
     * 新增库存预警策略
     *
     * @param wmsWarningConfig 库存预警策略
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsWarningConfig insertWmsWarningConfig(WmsWarningConfig wmsWarningConfig){
        QueryWrapper queryWrapper = getQueryConfigWrapper(wmsWarningConfig);

        int count = wmsWarningConfigMapper.selectConfigByValue(queryWrapper);
        if (count > 0){
            throw new RuntimeException("当前策略已存在！");
        }
        wmsWarningConfig.setId(IdUtil.simpleUUID());
        wmsWarningConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsWarningConfigMapper.insert(wmsWarningConfig);
        return wmsWarningConfig;
    }

    /**
     * 修改库存预警策略
     *
     * @param wmsWarningConfig 库存预警策略
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsWarningConfig updateWmsWarningConfig(WmsWarningConfig wmsWarningConfig){
        wmsWarningConfigMapper.updateById(wmsWarningConfig);
        return wmsWarningConfig;
    }

    /**
     * 批量删除库存预警策略
     *
     * @param IDs 需要删除的库存预警策略主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWarningConfigByIDs(String[] IDs){
        List<WmsWarningConfig> wmsWarningConfigs = new ArrayList<>();
        for (String ID : IDs) {
            WmsWarningConfig wmsWarningConfig = new WmsWarningConfig();
            wmsWarningConfig.setId(ID);
            wmsWarningConfig.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsWarningConfigs.add(wmsWarningConfig);
        }
        return super.updateBatchById(wmsWarningConfigs) ? 1 : 0;
    }

    /**
     * 删除库存预警策略信息
     *
     * @param ID 库存预警策略主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWarningConfigByID(String ID){
        WmsWarningConfig wmsWarningConfig = new WmsWarningConfig();
        wmsWarningConfig.setId(ID);
        wmsWarningConfig.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsWarningConfigMapper.updateById(wmsWarningConfig);
    }

    public QueryWrapper<WmsWarningConfig> getQueryWrapper(WmsWarningConfig wmsWarningConfig) {
        QueryWrapper<WmsWarningConfig> queryWrapper = new QueryWrapper<>();
        if (wmsWarningConfig != null) {
            wmsWarningConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsWarningConfig.getDelFlag());
            //参数名称
            if (StrUtil.isNotEmpty(wmsWarningConfig.getConfigName())) {
                queryWrapper.like("config_name",wmsWarningConfig.getConfigName());
            }
            //参数键名
            if (StrUtil.isNotEmpty(wmsWarningConfig.getConfigKey())) {
                queryWrapper.like("config_key",wmsWarningConfig.getConfigKey());
            }
            //参数键值
            if (StrUtil.isNotEmpty(wmsWarningConfig.getConfigValue())) {
                queryWrapper.like("config_value",wmsWarningConfig.getConfigValue());
            }
            //是否开启（0.开启 1.关闭）
            if (StrUtil.isNotEmpty(wmsWarningConfig.getConfigType())) {
                queryWrapper.eq("config_type",wmsWarningConfig.getConfigType());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    public QueryWrapper<WmsWarningConfig> getQueryConfigWrapper(WmsWarningConfig wmsWarningConfig) {
        QueryWrapper<WmsWarningConfig> queryWrapper = new QueryWrapper<>();
        if (wmsWarningConfig != null) {
            //参数键名
            if (StrUtil.isNotEmpty(wmsWarningConfig.getConfigKey())) {
                queryWrapper.eq("config_key",wmsWarningConfig.getConfigKey());
            }
        }
        return queryWrapper;
    }

}
