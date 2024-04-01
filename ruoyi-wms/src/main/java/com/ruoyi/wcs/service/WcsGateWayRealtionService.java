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
import com.ruoyi.iot.task.ctrl.GatewayCollectCtrl;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsGateWayRealtion;
import com.ruoyi.wcs.mapper.WcsGateWayRealtionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * WCS网关设备关联关系Service接口
 *
 * @author hewei
 * @date 2023-03-31
 */
@Slf4j
@Service
public class WcsGateWayRealtionService extends ServiceImpl<WcsGateWayRealtionMapper, WcsGateWayRealtion> {

    @Autowired
    private WcsGateWayRealtionMapper wcsGateWayRealtionMapper;

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    protected Validator validator;

    @Autowired
    private GatewayCollectCtrl gatewayCollectCtrl;

    /**
     * 查询WCS新风温湿度传感器关联关系
     *
     * @param id WCS新风温湿度传感器关联关系主键
     * @return WCS新风温湿度传感器关联关系
     */
    public WcsGateWayRealtion selectWcsGateWayRealtionById(String id) {
        QueryWrapper<WcsGateWayRealtion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wcsGateWayRealtionMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询WCS新风温湿度传感器关联关系
     *
     * @param ids WCS新风温湿度传感器关联关系 IDs
     * @return WCS新风温湿度传感器关联关系
     */
    public List<WcsGateWayRealtion> selectWcsGateWayRealtionByIds(String[] ids) {
        QueryWrapper<WcsGateWayRealtion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wcsGateWayRealtionMapper.selectList(queryWrapper);
    }

    /**
     * 查询WCS新风温湿度传感器关联关系列表
     *
     * @param wcsGateWayRealtion WCS新风温湿度传感器关联关系
     * @return WCS新风温湿度传感器关联关系集合
     */
    public List<WcsGateWayRealtion> selectWcsGateWayRealtionList(WcsGateWayRealtion wcsGateWayRealtion) {
        QueryWrapper<WcsGateWayRealtion> queryWrapper = getQueryWrapper(wcsGateWayRealtion);
        return wcsGateWayRealtionMapper.select(queryWrapper);
    }

    /**
     * 新增WCS网关采集器关联关系
     *
     * @param wcsGateWayRealtion WCS网关采集器关联关系
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WcsGateWayRealtion insertWcsGateWayRealtion(WcsGateWayRealtion wcsGateWayRealtion) {
        if (StrUtil.isNotEmpty(wcsGateWayRealtion.getGateWayDeviceNo())) {
            if (StrUtil.isNotEmpty(wcsGateWayRealtion.getOldDeviceNo())) {
                wcsGateWayRealtionMapper.delete(new QueryWrapper<WcsGateWayRealtion>()
                        .eq("gate_way_device_no", wcsGateWayRealtion.getOldDeviceNo()));
            }
            List<String> noIpDeviceNos = new ArrayList<>();
            List<WcsGateWayRealtion> list = new ArrayList<>();
            if (StrUtil.isNotEmpty(wcsGateWayRealtion.getNoIpDeviceNo())) {
                noIpDeviceNos = Arrays.asList(wcsGateWayRealtion.getNoIpDeviceNo().split(","));
            }
            if (CollUtil.isNotEmpty(noIpDeviceNos)) {
                int size = noIpDeviceNos.size();
                Set set = new HashSet(noIpDeviceNos);
                int setSize = set.size();
                if (size > setSize) {
                    throw new ServiceException("网关关联的设备之间存在重复地址码");
                }
                noIpDeviceNos.forEach(item -> {
                    WcsGateWayRealtion relation = new WcsGateWayRealtion();
                    relation.setGateWayDeviceNo(wcsGateWayRealtion.getGateWayDeviceNo());
                    relation.setNoIpDeviceNo(item);
                    relation.setCreateBy(SecurityUtils.getUsername());
                    relation.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    relation.setRemark(wcsGateWayRealtion.getRemark());
                    list.add(relation);
                });
            }
            if (CollUtil.isNotEmpty(list)) {
                this.saveOrUpdateBatch(list);
            }
        } else {
            throw new ServiceException("未选择网关采集器");
        }

        // 重新加载网关与设备映射关系
        gatewayCollectCtrl.initGateWayRelationData();

        return wcsGateWayRealtion;
    }

    /**
     * 修改WCS新风温湿度传感器关联关系
     *
     * @param wcsGateWayRealtion WCS新风温湿度传感器关联关系
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WcsGateWayRealtion updateWcsGateWayRealtion(WcsGateWayRealtion wcsGateWayRealtion) {
        if (StringUtils.isEmpty(wcsGateWayRealtion.getId())) {
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        wcsGateWayRealtionMapper.updateById(wcsGateWayRealtion);
        return wcsGateWayRealtion;
    }

    /**
     * 批量删除WCS新风温湿度传感器关联关系
     *
     * @param ids 需要删除的WCS新风温湿度传感器关联关系主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWcsGateWayRealtionByIds(String[] ids) {
        QueryWrapper<WcsGateWayRealtion> qw = new QueryWrapper<>();
        qw.in("gate_way_device_no", Arrays.asList(ids));
        return this.getBaseMapper().delete(qw);
    }

    /**
     * 删除WCS新风温湿度传感器关联关系信息
     *
     * @param id WCS新风温湿度传感器关联关系主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWcsGateWayRealtionById(String id) {
        WcsGateWayRealtion wcsGateWayRealtion = new WcsGateWayRealtion();
        wcsGateWayRealtion.setId(id);
        wcsGateWayRealtion.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wcsGateWayRealtionMapper.updateById(wcsGateWayRealtion);
    }

    public QueryWrapper<WcsGateWayRealtion> getQueryWrapper(WcsGateWayRealtion wcsGateWayRealtion) {
        QueryWrapper<WcsGateWayRealtion> queryWrapper = new QueryWrapper<>();
        if (wcsGateWayRealtion != null) {
            wcsGateWayRealtion.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("wgwr.del_flag", wcsGateWayRealtion.getDelFlag());
            //网关采集器设备编号
            if (StrUtil.isNotEmpty(wcsGateWayRealtion.getGateWayDeviceNo())) {
                queryWrapper.eq("wgwr.gate_way_device_no", wcsGateWayRealtion.getGateWayDeviceNo());
            }
            //设备编号
            if (StrUtil.isNotEmpty(wcsGateWayRealtion.getNoIpDeviceNo())) {
                queryWrapper.eq("wgwr.no_ip_device_no", wcsGateWayRealtion.getNoIpDeviceNo());
            }
        }
        queryWrapper.orderByDesc("wgwr.create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param wcsGateWayRealtionList 模板数据
     * @param updateSupport               是否更新已经存在的数据
     * @param operName                    操作人姓名
     * @return
     */
    public String importData(List<WcsGateWayRealtion> wcsGateWayRealtionList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wcsGateWayRealtionList) || wcsGateWayRealtionList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WcsGateWayRealtion wcsGateWayRealtion : wcsGateWayRealtionList) {
            if (null == wcsGateWayRealtion) {
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                WcsGateWayRealtion u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wcsGateWayRealtion);
                    wcsGateWayRealtion.setId(IdUtil.simpleUUID());
                    wcsGateWayRealtion.setCreateBy(operName);
                    wcsGateWayRealtion.setCreateTime(new Date());
                    wcsGateWayRealtion.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wcsGateWayRealtionMapper.insert(wcsGateWayRealtion);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wcsGateWayRealtion);
                    //todo 验证
                    //int count = wcsGateWayRealtionMapper.checkCode(wcsGateWayRealtion);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    wcsGateWayRealtion.setId(u.getId());
                    wcsGateWayRealtion.setUpdateBy(operName);
                    wcsGateWayRealtion.setUpdateTime(new Date());
                    wcsGateWayRealtionMapper.updateById(wcsGateWayRealtion);
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
     * 获取WCS网关采集器-设备关联关系详细信息
     * @param id id
     * @return map map
     */
    public Map<String, Object> queryRelationById(String id) {
        Map<String, Object> result = new HashMap<>();
        List<WcsDeviceBaseInfo> deviceBaseInfos = new ArrayList<>();
        if (StrUtil.isNotEmpty(id)) {
            List<WcsGateWayRealtion> list = this.getBaseMapper().selectList(new QueryWrapper<WcsGateWayRealtion>().
                    eq("gate_way_device_no", id).eq("del_flag", DelFlagEnum.DEL_NO.getCode()));
            result.put("relationList", list);
            List<String> ids = list.stream().map(WcsGateWayRealtion::getNoIpDeviceNo).collect(Collectors.toList());
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
