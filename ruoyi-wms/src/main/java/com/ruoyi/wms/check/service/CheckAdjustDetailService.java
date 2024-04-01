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
import com.ruoyi.wms.check.domain.CheckAdjustDetail;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.check.mapper.CheckAdjustDetailMapper;
import com.ruoyi.wms.check.domain.CheckAdjustDetail;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 库存盘点调整单详情Service接口
 *
 * @author nf
 * @date 2023-03-23
 */
@Slf4j
@Service
public class CheckAdjustDetailService extends ServiceImpl<CheckAdjustDetailMapper, CheckAdjustDetail> {

    @Autowired
    private CheckAdjustDetailMapper checkAdjustDetailMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询库存盘点调整单详情
     *
     * @param id 库存盘点调整单详情主键
     * @return 库存盘点调整单详情
     */
    public CheckAdjustDetail selectCheckAdjustDetailById(String id){
        QueryWrapper<CheckAdjustDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return checkAdjustDetailMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询库存盘点调整单详情
     *
     * @param ids 库存盘点调整单详情 IDs
     * @return 库存盘点调整单详情
     */
    public List<CheckAdjustDetail> selectCheckAdjustDetailByIds(String[] ids) {
        QueryWrapper<CheckAdjustDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return checkAdjustDetailMapper.selectList(queryWrapper);
    }

    /**
     * 查询库存盘点调整单详情列表
     *
     * @param checkAdjustDetail 库存盘点调整单详情
     * @return 库存盘点调整单详情集合
     */
    public List<CheckAdjustDetail> selectCheckAdjustDetailList(CheckAdjustDetail checkAdjustDetail){
        QueryWrapper<CheckAdjustDetail> queryWrapper = getQueryWrapper(checkAdjustDetail);
        return checkAdjustDetailMapper.select(queryWrapper);
    }

    /**
     * 新增库存盘点调整单详情
     *
     * @param checkAdjustDetail 库存盘点调整单详情
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckAdjustDetail insertCheckAdjustDetail(CheckAdjustDetail checkAdjustDetail){
        checkAdjustDetail.setId(IdUtil.simpleUUID());
        checkAdjustDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        checkAdjustDetailMapper.insert(checkAdjustDetail);
        return checkAdjustDetail;
    }

    /**
     * 修改库存盘点调整单详情
     *
     * @param checkAdjustDetail 库存盘点调整单详情
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckAdjustDetail updateCheckAdjustDetail(CheckAdjustDetail checkAdjustDetail){
        if(StringUtils.isEmpty(checkAdjustDetail.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        checkAdjustDetailMapper.updateById(checkAdjustDetail);
        return checkAdjustDetail;
    }

    /**
     * 批量删除库存盘点调整单详情
     *
     * @param ids 需要删除的库存盘点调整单详情主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckAdjustDetailByIds(String[] ids){
        List<CheckAdjustDetail> checkAdjustDetails = new ArrayList<>();
        for (String id : ids) {
            CheckAdjustDetail checkAdjustDetail = new CheckAdjustDetail();
            checkAdjustDetail.setId(id);
            checkAdjustDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            checkAdjustDetails.add(checkAdjustDetail);
        }
        return super.updateBatchById(checkAdjustDetails) ? 1 : 0;
    }

    /**
     * 删除库存盘点调整单详情信息
     *
     * @param id 库存盘点调整单详情主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckAdjustDetailById(String id){
        CheckAdjustDetail checkAdjustDetail = new CheckAdjustDetail();
        checkAdjustDetail.setId(id);
        checkAdjustDetail.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return checkAdjustDetailMapper.updateById(checkAdjustDetail);
    }

    public QueryWrapper<CheckAdjustDetail> getQueryWrapper(CheckAdjustDetail checkAdjustDetail) {
        QueryWrapper<CheckAdjustDetail> queryWrapper = new QueryWrapper<>();
        if (checkAdjustDetail != null) {
            checkAdjustDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",checkAdjustDetail.getDelFlag());
            //盘点单号
            if (StrUtil.isNotEmpty(checkAdjustDetail.getCheckBillCode())) {
                queryWrapper.eq("check_bill_code",checkAdjustDetail.getCheckBillCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(checkAdjustDetail.getGoodsCode())) {
                queryWrapper.like("goods_code",checkAdjustDetail.getGoodsCode());
            }
            //库区编码
            if (StrUtil.isNotEmpty(checkAdjustDetail.getAreaCode())) {
                queryWrapper.like("area_code",checkAdjustDetail.getAreaCode());
            }
            //库位编码
            if (StrUtil.isNotEmpty(checkAdjustDetail.getLocationCode())) {
                queryWrapper.like("location_code",checkAdjustDetail.getLocationCode());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(checkAdjustDetail.getTrayCode())) {
                queryWrapper.like("tray_code",checkAdjustDetail.getTrayCode());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param checkAdjustDetailList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<CheckAdjustDetail> checkAdjustDetailList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(checkAdjustDetailList) || checkAdjustDetailList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (CheckAdjustDetail checkAdjustDetail : checkAdjustDetailList) {
            if(null==checkAdjustDetail){
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                CheckAdjustDetail u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, checkAdjustDetail);
                    checkAdjustDetail.setId(IdUtil.simpleUUID());
                    checkAdjustDetail.setCreateBy(operName);
                    checkAdjustDetail.setCreateTime(new Date());
                    checkAdjustDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    checkAdjustDetailMapper.insert(checkAdjustDetail);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, checkAdjustDetail);
                    //todo 验证
                    //int count = checkAdjustDetailMapper.checkCode(checkAdjustDetail);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        checkAdjustDetail.setId(u.getId());
                        checkAdjustDetail.setUpdateBy(operName);
                        checkAdjustDetail.setUpdateTime(new Date());
                        checkAdjustDetailMapper.updateById(checkAdjustDetail);
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
