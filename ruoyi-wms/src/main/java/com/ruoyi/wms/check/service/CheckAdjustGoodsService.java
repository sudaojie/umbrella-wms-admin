package com.ruoyi.wms.check.service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Date;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.check.domain.CheckAdjustGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.check.mapper.CheckAdjustGoodsMapper;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 库存盘点调整详情货物Service接口
 *
 * @author nf
 * @date 2023-03-23
 */
@Slf4j
@Service
public class CheckAdjustGoodsService extends ServiceImpl<CheckAdjustGoodsMapper, CheckAdjustGoods> {

    @Autowired
    private CheckAdjustGoodsMapper checkAdjustGoodsMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询库存盘点调整详情货物
     *
     * @param id 库存盘点调整详情货物主键
     * @return 库存盘点调整详情货物
     */
    public CheckAdjustGoods selectCheckAdjustGoodsById(String id){
        QueryWrapper<CheckAdjustGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return checkAdjustGoodsMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询库存盘点调整详情货物
     *
     * @param ids 库存盘点调整详情货物 IDs
     * @return 库存盘点调整详情货物
     */
    public List<CheckAdjustGoods> selectCheckAdjustGoodsByIds(String[] ids) {
        QueryWrapper<CheckAdjustGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return checkAdjustGoodsMapper.selectList(queryWrapper);
    }

    /**
     * 查询库存盘点调整详情货物列表
     *
     * @param checkAdjustGoods 库存盘点调整详情货物
     * @return 库存盘点调整详情货物集合
     */
    public List<CheckAdjustGoods> selectCheckAdjustGoodsList(CheckAdjustGoods checkAdjustGoods){
        QueryWrapper<CheckAdjustGoods> queryWrapper = getQueryWrapper(checkAdjustGoods);
        return checkAdjustGoodsMapper.select(queryWrapper);
    }

    /**
     * 新增库存盘点调整详情货物
     *
     * @param checkAdjustGoods 库存盘点调整详情货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckAdjustGoods insertCheckAdjustGoods(CheckAdjustGoods checkAdjustGoods){
        checkAdjustGoods.setId(IdUtil.simpleUUID());
        checkAdjustGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        checkAdjustGoodsMapper.insert(checkAdjustGoods);
        return checkAdjustGoods;
    }

    /**
     * 修改库存盘点调整详情货物
     *
     * @param checkAdjustGoods 库存盘点调整详情货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckAdjustGoods updateCheckAdjustGoods(CheckAdjustGoods checkAdjustGoods){
        if(StringUtils.isEmpty(checkAdjustGoods.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        checkAdjustGoodsMapper.updateById(checkAdjustGoods);
        return checkAdjustGoods;
    }

    /**
     * 批量删除库存盘点调整详情货物
     *
     * @param ids 需要删除的库存盘点调整详情货物主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckAdjustGoodsByIds(String[] ids){
        List<CheckAdjustGoods> checkAdjustGoodss = new ArrayList<>();
        for (String id : ids) {
            CheckAdjustGoods checkAdjustGoods = new CheckAdjustGoods();
            checkAdjustGoods.setId(id);
            checkAdjustGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            checkAdjustGoodss.add(checkAdjustGoods);
        }
        return super.updateBatchById(checkAdjustGoodss) ? 1 : 0;
    }

    /**
     * 删除库存盘点调整详情货物信息
     *
     * @param id 库存盘点调整详情货物主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckAdjustGoodsById(String id){
        CheckAdjustGoods checkAdjustGoods = new CheckAdjustGoods();
        checkAdjustGoods.setId(id);
        checkAdjustGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return checkAdjustGoodsMapper.updateById(checkAdjustGoods);
    }

    public QueryWrapper<CheckAdjustGoods> getQueryWrapper(CheckAdjustGoods checkAdjustGoods) {
        QueryWrapper<CheckAdjustGoods> queryWrapper = new QueryWrapper<>();
        if (checkAdjustGoods != null) {
            checkAdjustGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",checkAdjustGoods.getDelFlag());
            //盘点调整详情主键
            if (StrUtil.isNotEmpty(checkAdjustGoods.getCheckAdjustDetail())) {
                queryWrapper.eq("check_adjust_detail",checkAdjustGoods.getCheckAdjustDetail());
            }
            //货物名称
            if (StrUtil.isNotEmpty(checkAdjustGoods.getGoodsName())) {
                queryWrapper.like("goods_name",checkAdjustGoods.getGoodsName());
            }
            //机件号
            if (StrUtil.isNotEmpty(checkAdjustGoods.getPartsCode())) {
                queryWrapper.eq("parts_code",checkAdjustGoods.getPartsCode());
            }
            //货物唯一码
            if (StrUtil.isNotEmpty(checkAdjustGoods.getOnlyCode())) {
                queryWrapper.eq("only_code",checkAdjustGoods.getOnlyCode());
            }
            //盘点托盘编号
            if (StrUtil.isNotEmpty(checkAdjustGoods.getTrayCode())) {
                queryWrapper.eq("tray_code",checkAdjustGoods.getTrayCode());
            }
            //盘点托盘名称
            if (StrUtil.isNotEmpty(checkAdjustGoods.getTrayName())) {
                queryWrapper.like("tray_name",checkAdjustGoods.getTrayName());
            }
            //盘点库位编号
            if (StrUtil.isNotEmpty(checkAdjustGoods.getLocationCode())) {
                queryWrapper.eq("location_code",checkAdjustGoods.getLocationCode());
            }
            //规格型号
            if (StrUtil.isNotEmpty(checkAdjustGoods.getModel())) {
                queryWrapper.eq("model",checkAdjustGoods.getModel());
            }
            //计量单位
            if (StrUtil.isNotEmpty(checkAdjustGoods.getMeasureUnit())) {
                queryWrapper.eq("measure_unit",checkAdjustGoods.getMeasureUnit());
            }
            //盘亏数量
            if (StrUtil.isNotEmpty(checkAdjustGoods.getLossNum())) {
                queryWrapper.eq("loss_num",checkAdjustGoods.getLossNum());
            }
            //盘盈数量
            if (StrUtil.isNotEmpty(checkAdjustGoods.getProfitNum())) {
                queryWrapper.eq("profit_num",checkAdjustGoods.getProfitNum());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param checkAdjustGoodsList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<CheckAdjustGoods> checkAdjustGoodsList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(checkAdjustGoodsList) || checkAdjustGoodsList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (CheckAdjustGoods checkAdjustGoods : checkAdjustGoodsList) {
            if(null==checkAdjustGoods){
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                CheckAdjustGoods u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, checkAdjustGoods);
                    checkAdjustGoods.setId(IdUtil.simpleUUID());
                    checkAdjustGoods.setCreateBy(operName);
                    checkAdjustGoods.setCreateTime(new Date());
                    checkAdjustGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    checkAdjustGoodsMapper.insert(checkAdjustGoods);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, checkAdjustGoods);
                    //todo 验证
                    //int count = checkAdjustGoodsMapper.checkCode(checkAdjustGoods);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        checkAdjustGoods.setId(u.getId());
                        checkAdjustGoods.setUpdateBy(operName);
                        checkAdjustGoods.setUpdateTime(new Date());
                        checkAdjustGoodsMapper.updateById(checkAdjustGoods);
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
