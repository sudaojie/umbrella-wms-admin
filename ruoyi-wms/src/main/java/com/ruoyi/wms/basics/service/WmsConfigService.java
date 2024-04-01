package com.ruoyi.wms.basics.service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wms.basics.domain.WmsConfig;
import com.ruoyi.wms.basics.mapper.WmsConfigMapper;
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
 * wms参数配置Service接口
 *
 * @author ruoyi
 * @date 2023-02-23
 */
@Slf4j
@Service
public class WmsConfigService extends ServiceImpl<WmsConfigMapper, WmsConfig> {

    @Autowired(required = false)
    private WmsConfigMapper wmsConfigMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询wms参数配置
     *
     * @param wmsConfigId wms参数配置主键
     * @return wms参数配置
     */
    public WmsConfig selectWmsConfigByWmsConfigId(String wmsConfigId){
        QueryWrapper<WmsConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", wmsConfigId);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsConfigMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询wms参数配置
     *
     * @param wmsConfigIds wms参数配置 IDs
     * @return wms参数配置
     */
    public List<WmsConfig> selectWmsConfigByIds(String[] wmsConfigIds) {
        QueryWrapper<WmsConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("wmsConfigId", Arrays.asList(wmsConfigIds));
        return wmsConfigMapper.selectList(queryWrapper);
    }

    /**
     * 查询wms参数配置列表
     *
     * @param wmsConfig wms参数配置
     * @return wms参数配置集合
     */
    public List<WmsConfig> selectWmsConfigList(WmsConfig wmsConfig){
        QueryWrapper<WmsConfig> queryWrapper = getQueryWrapper(wmsConfig);
        return wmsConfigMapper.select(queryWrapper);
    }

    /**
     * 新增wms参数配置
     *
     * @param wmsConfig wms参数配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsConfig insertWmsConfig(WmsConfig wmsConfig){
        wmsConfig.setId(IdUtil.simpleUUID());
        wmsConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsConfig.setEnableStatus(EnableStatus.ENABLE.getCode());
        wmsConfigMapper.insert(wmsConfig);
        return wmsConfig;
    }

    /**
     * 修改wms参数配置
     *
     * @param wmsConfig wms参数配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsConfig updateWmsConfig(WmsConfig wmsConfig){
        wmsConfigMapper.updateById(wmsConfig);
        return wmsConfig;
    }

    /**
     * 批量删除wms参数配置
     *
     * @param wmsConfigIds 需要删除的wms参数配置主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsConfigByWmsConfigIds(String[] wmsConfigIds){
        List<WmsConfig> wmsConfigs = new ArrayList<>();
        for (String wmsConfigId : wmsConfigIds) {
            WmsConfig wmsConfig = new WmsConfig();
            wmsConfig.setId(wmsConfigId);
            wmsConfig.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsConfigs.add(wmsConfig);
        }
        return super.updateBatchById(wmsConfigs) ? 1 : 0;
    }

    /**
     * 删除wms参数配置信息
     *
     * @param wmsConfigId wms参数配置主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsConfigByWmsConfigId(String wmsConfigId){
        WmsConfig wmsConfig = new WmsConfig();
        wmsConfig.setId(wmsConfigId);
        wmsConfig.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsConfigMapper.updateById(wmsConfig);
    }

    public QueryWrapper<WmsConfig> getQueryWrapper(WmsConfig wmsConfig) {
        QueryWrapper<WmsConfig> queryWrapper = new QueryWrapper<>();
        if (wmsConfig != null) {
            wmsConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsConfig.getDelFlag());
            //参数键名
            if (StrUtil.isNotEmpty(wmsConfig.getWmsConfigKey())) {
                queryWrapper.like("wms_config_key",wmsConfig.getWmsConfigKey());
            }
            //参数名称
            if (StrUtil.isNotEmpty(wmsConfig.getWmsConfigName())) {
                queryWrapper.like("wms_config_name",wmsConfig.getWmsConfigName());
            }
            //参数键值
            if (StrUtil.isNotEmpty(wmsConfig.getWmsConfigValue())) {
                queryWrapper.eq("wms_config_value",wmsConfig.getWmsConfigValue());
            }
            //是否启用(0：启用 1：未启用)
            if (StrUtil.isNotEmpty(wmsConfig.getEnableStatus())) {
                queryWrapper.eq("enable_status",wmsConfig.getEnableStatus());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param wmsConfigList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<WmsConfig> wmsConfigList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsConfigList) || wmsConfigList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WmsConfig wmsConfig : wmsConfigList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                WmsConfig u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsConfig);
                    wmsConfig.setId(IdUtil.simpleUUID());
                    wmsConfig.setCreateBy(operName);
                    wmsConfig.setCreateTime(new Date());
                    wmsConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsConfigMapper.insert(wmsConfig);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsConfig);
                    wmsConfig.setId(u.getId());
                    wmsConfig.setUpdateBy(operName);
                    wmsConfig.setUpdateTime(new Date());
                    wmsConfigMapper.updateById(wmsConfig);
                    successNum++;
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
     * 校验参数键名是否重复
     * @param wmsConfig
     * @return
     */
    public List<WmsConfig> checkData(WmsConfig wmsConfig) {
        return wmsConfigMapper.checkData(wmsConfig);
    }
}
