package com.ruoyi.wms.check.service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Date;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.check.domain.CheckConfig;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.check.mapper.CheckConfigMapper;
import com.ruoyi.wms.check.domain.CheckConfig;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 盘点配置Service接口
 *
 * @author ruoyi
 * @date 2023-03-20
 */
@Slf4j
@Service
public class CheckConfigService extends ServiceImpl<CheckConfigMapper, CheckConfig> {

    @Autowired(required = false)
    private CheckConfigMapper checkConfigMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询盘点配置
     *
     * @param id 盘点配置主键
     * @return 盘点配置
     */
    public CheckConfig selectCheckConfigById(String id){
        QueryWrapper<CheckConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return checkConfigMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询盘点配置
     *
     * @param ids 盘点配置 IDs
     * @return 盘点配置
     */
    public List<CheckConfig> selectCheckConfigByIds(String[] ids) {
        QueryWrapper<CheckConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return checkConfigMapper.selectList(queryWrapper);
    }

    /**
     * 查询盘点配置列表
     *
     * @param checkConfig 盘点配置
     * @return 盘点配置集合
     */
    public List<CheckConfig> selectCheckConfigList(CheckConfig checkConfig){
        QueryWrapper<CheckConfig> queryWrapper = getQueryWrapper(checkConfig);
        return checkConfigMapper.select(queryWrapper);
    }

    /**
     * 新增盘点配置
     *
     * @param checkConfig 盘点配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckConfig insertCheckConfig(CheckConfig checkConfig){
        checkConfig.setId(IdUtil.simpleUUID());
        checkConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        checkConfigMapper.insert(checkConfig);
        return checkConfig;
    }

    /**
     * 修改盘点配置
     *
     * @param checkConfig 盘点配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckConfig updateCheckConfig(CheckConfig checkConfig){
        checkConfigMapper.updateById(checkConfig);
        return checkConfig;
    }

    /**
     * 批量删除盘点配置
     *
     * @param ids 需要删除的盘点配置主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckConfigByIds(String[] ids){
        List<CheckConfig> checkConfigs = new ArrayList<>();
        for (String id : ids) {
            CheckConfig checkConfig = new CheckConfig();
            checkConfig.setId(id);
            checkConfig.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            checkConfigs.add(checkConfig);
        }
        return super.updateBatchById(checkConfigs) ? 1 : 0;
    }

    /**
     * 删除盘点配置信息
     *
     * @param id 盘点配置主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckConfigById(String id){
        CheckConfig checkConfig = new CheckConfig();
        checkConfig.setId(id);
        checkConfig.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return checkConfigMapper.updateById(checkConfig);
    }

    public QueryWrapper<CheckConfig> getQueryWrapper(CheckConfig checkConfig) {
        QueryWrapper<CheckConfig> queryWrapper = new QueryWrapper<>();
        if (checkConfig != null) {
            checkConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",checkConfig.getDelFlag());
            //货物编号
            if (StrUtil.isNotEmpty(checkConfig.getGoodsCode())) {
                queryWrapper.eq("goods_code",checkConfig.getGoodsCode());
            }
            //盘点单号
            if (StrUtil.isNotEmpty(checkConfig.getCheckBillCode())) {
                queryWrapper.eq("check_bill_code",checkConfig.getCheckBillCode());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param checkConfigList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<CheckConfig> checkConfigList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(checkConfigList) || checkConfigList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (CheckConfig checkConfig : checkConfigList) {
            if(null==checkConfig){
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                CheckConfig u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, checkConfig);
                    checkConfig.setId(IdUtil.simpleUUID());
                    checkConfig.setCreateBy(operName);
                    checkConfig.setCreateTime(new Date());
                    checkConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    checkConfigMapper.insert(checkConfig);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, checkConfig);
                    //todo 验证
                    //int count = checkConfigMapper.checkCode(checkConfig);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        checkConfig.setId(u.getId());
                        checkConfig.setUpdateBy(operName);
                        checkConfig.setUpdateTime(new Date());
                        checkConfigMapper.updateById(checkConfig);
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
}
