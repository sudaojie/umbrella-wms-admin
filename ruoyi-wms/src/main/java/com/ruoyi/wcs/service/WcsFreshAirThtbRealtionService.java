package com.ruoyi.wcs.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsFreshAirThtbRealtion;
import com.ruoyi.wcs.domain.WcsGateWayRealtion;
import com.ruoyi.wcs.mapper.WcsFreshAirThtbRealtionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * WCS新风温湿度传感器关联关系Service接口
 *
 * @author yangjie
 * @date 2023-03-31
 */
@Slf4j
@Service
public class WcsFreshAirThtbRealtionService extends ServiceImpl<WcsFreshAirThtbRealtionMapper, WcsFreshAirThtbRealtion> {

    @Autowired
    private WcsFreshAirThtbRealtionMapper wcsFreshAirThtbRealtionMapper;

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    protected Validator validator;

    /**
     * 查询WCS新风温湿度传感器关联关系
     *
     * @param id WCS新风温湿度传感器关联关系主键
     * @return WCS新风温湿度传感器关联关系
     */
    public WcsFreshAirThtbRealtion selectWcsFreshAirThtbRealtionById(String id) {
        QueryWrapper<WcsFreshAirThtbRealtion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wcsFreshAirThtbRealtionMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询WCS新风温湿度传感器关联关系
     *
     * @param ids WCS新风温湿度传感器关联关系 IDs
     * @return WCS新风温湿度传感器关联关系
     */
    public List<WcsFreshAirThtbRealtion> selectWcsFreshAirThtbRealtionByIds(String[] ids) {
        QueryWrapper<WcsFreshAirThtbRealtion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wcsFreshAirThtbRealtionMapper.selectList(queryWrapper);
    }

    /**
     * 查询WCS新风温湿度传感器关联关系列表
     *
     * @param wcsFreshAirThtbRealtion WCS新风温湿度传感器关联关系
     * @return WCS新风温湿度传感器关联关系集合
     */
    public List<WcsFreshAirThtbRealtion> selectWcsFreshAirThtbRealtionList(WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion) {
        QueryWrapper<WcsFreshAirThtbRealtion> queryWrapper = getQueryWrapper(wcsFreshAirThtbRealtion);
        return wcsFreshAirThtbRealtionMapper.select(queryWrapper);
    }

    /**
     * 新增WCS新风温湿度传感器关联关系
     *
     * @param wcsFreshAirThtbRealtion WCS新风温湿度传感器关联关系
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WcsFreshAirThtbRealtion insertWcsFreshAirThtbRealtion(WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion) {
        if (StrUtil.isNotEmpty(wcsFreshAirThtbRealtion.getFreshAirDeviceNo())) {
            if (StrUtil.isNotEmpty(wcsFreshAirThtbRealtion.getOldDeviceNo())) {
                wcsFreshAirThtbRealtionMapper.delete(new QueryWrapper<WcsFreshAirThtbRealtion>()
                        .eq("fresh_air_device_no", wcsFreshAirThtbRealtion.getOldDeviceNo()));
            }
            List<String>  thtbDeviceNos = new ArrayList<>();
            List<WcsFreshAirThtbRealtion> list = new ArrayList<>();
            if (StrUtil.isNotEmpty(wcsFreshAirThtbRealtion.getThtbDeviceNo())) {
                thtbDeviceNos = Arrays.asList(wcsFreshAirThtbRealtion.getThtbDeviceNo().split(","));
            }
            if (CollUtil.isNotEmpty(thtbDeviceNos)) {
                thtbDeviceNos.forEach(item -> {
                    WcsFreshAirThtbRealtion relation = new WcsFreshAirThtbRealtion();
                    relation.setFreshAirDeviceNo(wcsFreshAirThtbRealtion.getFreshAirDeviceNo());
                    relation.setThtbDeviceNo(item);
                    relation.setCreateBy(SecurityUtils.getUsername());
                    relation.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    relation.setRemark(wcsFreshAirThtbRealtion.getRemark());
                    list.add(relation);
                });
            }
            if (CollUtil.isNotEmpty(list)) {
                this.saveOrUpdateBatch(list);
            }
        } else {
            throw new ServiceException("未选择新风设备");
        }
        return wcsFreshAirThtbRealtion;
    }

    /**
     * 修改WCS新风温湿度传感器关联关系
     *
     * @param wcsFreshAirThtbRealtion WCS新风温湿度传感器关联关系
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WcsFreshAirThtbRealtion updateWcsFreshAirThtbRealtion(WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion) {
        if (StringUtils.isEmpty(wcsFreshAirThtbRealtion.getId())) {
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        wcsFreshAirThtbRealtionMapper.updateById(wcsFreshAirThtbRealtion);
        return wcsFreshAirThtbRealtion;
    }

    /**
     * 批量删除WCS新风温湿度传感器关联关系
     *
     * @param ids 需要删除的WCS新风温湿度传感器关联关系主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWcsFreshAirThtbRealtionByIds(String[] ids) {
        QueryWrapper<WcsFreshAirThtbRealtion> qw = new QueryWrapper<>();
        qw.in("fresh_air_device_no", Arrays.asList(ids));
        return this.getBaseMapper().delete(qw);
    }

    /**
     * 删除WCS新风温湿度传感器关联关系信息
     *
     * @param id WCS新风温湿度传感器关联关系主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWcsFreshAirThtbRealtionById(String id) {
        WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion = new WcsFreshAirThtbRealtion();
        wcsFreshAirThtbRealtion.setId(id);
        wcsFreshAirThtbRealtion.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wcsFreshAirThtbRealtionMapper.updateById(wcsFreshAirThtbRealtion);
    }

    public QueryWrapper<WcsFreshAirThtbRealtion> getQueryWrapper(WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion) {
        QueryWrapper<WcsFreshAirThtbRealtion> queryWrapper = new QueryWrapper<>();
        if (wcsFreshAirThtbRealtion != null) {
            wcsFreshAirThtbRealtion.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("wfatr.del_flag", wcsFreshAirThtbRealtion.getDelFlag());
            queryWrapper.groupBy("wfatr.fresh_air_device_no");
            //新风设备编号
            if (StrUtil.isNotEmpty(wcsFreshAirThtbRealtion.getFreshAirDeviceNo())) {
                queryWrapper.eq("wfatr.fresh_air_device_no", wcsFreshAirThtbRealtion.getFreshAirDeviceNo());
            }
            //温湿度传感器设备编号
            if (StrUtil.isNotEmpty(wcsFreshAirThtbRealtion.getThtbDeviceNo())) {
                queryWrapper.eq("wfatr.thtb_device_no", wcsFreshAirThtbRealtion.getThtbDeviceNo());
            }
        }
        queryWrapper.orderByDesc("wfatr.create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param wcsFreshAirThtbRealtionList 模板数据
     * @param updateSupport               是否更新已经存在的数据
     * @param operName                    操作人姓名
     * @return
     */
    public String importData(List<WcsFreshAirThtbRealtion> wcsFreshAirThtbRealtionList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wcsFreshAirThtbRealtionList) || wcsFreshAirThtbRealtionList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion : wcsFreshAirThtbRealtionList) {
            if (null == wcsFreshAirThtbRealtion) {
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                WcsFreshAirThtbRealtion u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wcsFreshAirThtbRealtion);
                    wcsFreshAirThtbRealtion.setId(IdUtil.simpleUUID());
                    wcsFreshAirThtbRealtion.setCreateBy(operName);
                    wcsFreshAirThtbRealtion.setCreateTime(new Date());
                    wcsFreshAirThtbRealtion.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wcsFreshAirThtbRealtionMapper.insert(wcsFreshAirThtbRealtion);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wcsFreshAirThtbRealtion);
                    //todo 验证
                    //int count = wcsFreshAirThtbRealtionMapper.checkCode(wcsFreshAirThtbRealtion);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    wcsFreshAirThtbRealtion.setId(u.getId());
                    wcsFreshAirThtbRealtion.setUpdateBy(operName);
                    wcsFreshAirThtbRealtion.setUpdateTime(new Date());
                    wcsFreshAirThtbRealtionMapper.updateById(wcsFreshAirThtbRealtion);
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
     * 获取设备信息下拉列表
     * @return WCS设备基本信息集合
     */
    public List<WcsDeviceBaseInfo> listTypeDeviceInfos(String deviceType) {
        if (StrUtil.isNotEmpty(deviceType)) {
            return wcsFreshAirThtbRealtionMapper.listTypeDeviceInfos(deviceType);
        } else {
            throw new ServiceException("获取设备信息下拉列表参数缺失");
        }
    }

    /**
     * 回显关联关系
     * @param id id
     * @return map map
     */
    public Map<String, Object> queryRelationById(String id) {
        Map<String, Object> result = new HashMap<>();
        List<WcsDeviceBaseInfo> deviceBaseInfos = new ArrayList<>();
        if (StrUtil.isNotEmpty(id)) {
            List<WcsFreshAirThtbRealtion> list = this.getBaseMapper().selectList(new QueryWrapper<WcsFreshAirThtbRealtion>().
                    eq("fresh_air_device_no", id).eq("del_flag", DelFlagEnum.DEL_NO.getCode()));
            result.put("relationList", list);
            List<String> ids = list.stream().map(WcsFreshAirThtbRealtion::getThtbDeviceNo).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(ids)) {
                deviceBaseInfos = wcsDeviceBaseInfoService.getBaseMapper().selectList(new QueryWrapper<WcsDeviceBaseInfo>().in("device_no", ids));
            }
            result.put("deviceBaseInfos", deviceBaseInfos);
        } else {
            throw new ServiceException("缺失必要参数");
        }
        return result;
    }
}
