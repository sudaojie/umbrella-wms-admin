package com.ruoyi.wms.wcstask.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.wcstask.domain.WmsWcsCallbackInfo;
import com.ruoyi.wms.wcstask.mapper.WmsWcsCallbackInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 * WMS/WCS回调信息Service接口
 *
 * @author ruoyi
 * @date 2023-06-27
 */
@Slf4j
@Service
public class WmsWcsCallbackInfoService extends ServiceImpl<WmsWcsCallbackInfoMapper, WmsWcsCallbackInfo> {

    @Autowired
    private WmsWcsCallbackInfoMapper wmsWcsCallbackInfoMapper;

    @Autowired
    protected Validator validator;
    /**
     * 查询WMS/WCS回调信息
     *
     * @param id WMS/WCS回调信息主键
     * @return WMS/WCS回调信息
     */
    public WmsWcsCallbackInfo selectWmsWcsCallbackInfoById(String id){
        QueryWrapper<WmsWcsCallbackInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsWcsCallbackInfoMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询WMS/WCS回调信息
     *
     * @param ids WMS/WCS回调信息 IDs
     * @return WMS/WCS回调信息
     */
    public List<WmsWcsCallbackInfo> selectWmsWcsCallbackInfoByIds(String[] ids) {
        QueryWrapper<WmsWcsCallbackInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsWcsCallbackInfoMapper.selectList(queryWrapper);
    }

    /**
     * 查询WMS/WCS回调信息列表
     *
     * @param wmsWcsCallbackInfo WMS/WCS回调信息
     * @return WMS/WCS回调信息集合
     */
    public List<WmsWcsCallbackInfo> selectWmsWcsCallbackInfoList(WmsWcsCallbackInfo wmsWcsCallbackInfo){
        QueryWrapper<WmsWcsCallbackInfo> queryWrapper = getQueryWrapper(wmsWcsCallbackInfo);
        return wmsWcsCallbackInfoMapper.select(queryWrapper);
    }

    /**
     * 新增WMS/WCS回调信息
     *
     * @param wmsWcsCallbackInfo WMS/WCS回调信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsWcsCallbackInfo insertWmsWcsCallbackInfo(WmsWcsCallbackInfo wmsWcsCallbackInfo){
        wmsWcsCallbackInfo.setId(IdUtil.simpleUUID());
        wmsWcsCallbackInfo.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsWcsCallbackInfoMapper.insert(wmsWcsCallbackInfo);
        return wmsWcsCallbackInfo;
    }

    /**
     * 修改WMS/WCS回调信息
     *
     * @param wmsWcsCallbackInfo WMS/WCS回调信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsWcsCallbackInfo updateWmsWcsCallbackInfo(WmsWcsCallbackInfo wmsWcsCallbackInfo){
        if(StringUtils.isEmpty(wmsWcsCallbackInfo.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        wmsWcsCallbackInfoMapper.updateById(wmsWcsCallbackInfo);
        return wmsWcsCallbackInfo;
    }

    /**
     * 批量删除WMS/WCS回调信息
     *
     * @param ids 需要删除的WMS/WCS回调信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWcsCallbackInfoByIds(String[] ids){
        List<WmsWcsCallbackInfo> wmsWcsCallbackInfos = new ArrayList<>();
        for (String id : ids) {
            WmsWcsCallbackInfo wmsWcsCallbackInfo = new WmsWcsCallbackInfo();
            wmsWcsCallbackInfo.setId(id);
            wmsWcsCallbackInfo.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsWcsCallbackInfos.add(wmsWcsCallbackInfo);
        }
        return super.updateBatchById(wmsWcsCallbackInfos) ? 1 : 0;
    }

    /**
     * 删除WMS/WCS回调信息信息
     *
     * @param id WMS/WCS回调信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWcsCallbackInfoById(String id){
        WmsWcsCallbackInfo wmsWcsCallbackInfo = new WmsWcsCallbackInfo();
        wmsWcsCallbackInfo.setId(id);
        wmsWcsCallbackInfo.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsWcsCallbackInfoMapper.updateById(wmsWcsCallbackInfo);
    }

    public QueryWrapper<WmsWcsCallbackInfo> getQueryWrapper(WmsWcsCallbackInfo wmsWcsCallbackInfo) {
        QueryWrapper<WmsWcsCallbackInfo> queryWrapper = new QueryWrapper<>();
        if (wmsWcsCallbackInfo != null) {
            wmsWcsCallbackInfo.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsWcsCallbackInfo.getDelFlag());
            //任务类型(0.正常入库 1.正常出库 2.晾晒入库 3.晾晒出库 4.移库 5.无单 6.盘点入库 7.盘点出库 8.回空盘)
            if (StrUtil.isNotEmpty(wmsWcsCallbackInfo.getTaskType())) {
                queryWrapper.eq("task_type",wmsWcsCallbackInfo.getTaskType());
            }
            //操作库区类型(1晾晒区，2理货区)
            if (StrUtil.isNotEmpty(wmsWcsCallbackInfo.getAreaType())) {
                queryWrapper.eq("area_type",wmsWcsCallbackInfo.getAreaType());
            }
            //托盘编码
            if (StrUtil.isNotEmpty(wmsWcsCallbackInfo.getTrayCode())) {
                queryWrapper.eq("tray_code",wmsWcsCallbackInfo.getTrayCode());
            }
            //起始库区
            if (StrUtil.isNotEmpty(wmsWcsCallbackInfo.getStartAreaCode())) {
                queryWrapper.eq("start_area_code",wmsWcsCallbackInfo.getStartAreaCode());
            }
            //结束库区
            if (StrUtil.isNotEmpty(wmsWcsCallbackInfo.getEndAreaCode())) {
                queryWrapper.eq("end_area_code",wmsWcsCallbackInfo.getEndAreaCode());
            }
            //起始库位
            if (StrUtil.isNotEmpty(wmsWcsCallbackInfo.getStartLocationCode())) {
                queryWrapper.eq("start_location_code",wmsWcsCallbackInfo.getStartLocationCode());
            }
            //结束库位
            if (StrUtil.isNotEmpty(wmsWcsCallbackInfo.getEndLocationCode())) {
                queryWrapper.eq("end_location_code",wmsWcsCallbackInfo.getEndLocationCode());
            }
            //业务单据号
            if (StrUtil.isNotEmpty(wmsWcsCallbackInfo.getDoc())) {
                queryWrapper.eq("doc",wmsWcsCallbackInfo.getDoc());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param wmsWcsCallbackInfoList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<WmsWcsCallbackInfo> wmsWcsCallbackInfoList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsWcsCallbackInfoList) || wmsWcsCallbackInfoList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WmsWcsCallbackInfo wmsWcsCallbackInfo : wmsWcsCallbackInfoList) {
            if(null==wmsWcsCallbackInfo){
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                WmsWcsCallbackInfo u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsWcsCallbackInfo);
                    wmsWcsCallbackInfo.setId(IdUtil.simpleUUID());
                    wmsWcsCallbackInfo.setCreateBy(operName);
                    wmsWcsCallbackInfo.setCreateTime(new Date());
                    wmsWcsCallbackInfo.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsWcsCallbackInfoMapper.insert(wmsWcsCallbackInfo);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsWcsCallbackInfo);
                    //todo 验证
                    //int count = wmsWcsCallbackInfoMapper.checkCode(wmsWcsCallbackInfo);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        wmsWcsCallbackInfo.setId(u.getId());
                        wmsWcsCallbackInfo.setUpdateBy(operName);
                        wmsWcsCallbackInfo.setUpdateTime(new Date());
                        wmsWcsCallbackInfoMapper.updateById(wmsWcsCallbackInfo);
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
