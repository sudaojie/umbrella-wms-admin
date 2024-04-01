package com.ruoyi.wms.basics.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.basics.domain.GoodsInfo;
import com.ruoyi.wms.basics.mapper.GoodsInfoMapper;
import com.ruoyi.wms.enums.GoodsInfoEnum;
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
 * 货物信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@Slf4j
@Service
public class GoodsInfoService extends ServiceImpl<GoodsInfoMapper, GoodsInfo> {

    @Autowired
    protected Validator validator;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    /**
     * 查询货物信息
     *
     * @param id 货物信息主键
     * @return 货物信息
     */
    public GoodsInfo selectGoodsInfoById(String id) {
        QueryWrapper<GoodsInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return goodsInfoMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询货物信息
     *
     * @param ids 货物信息 IDs
     * @return 货物信息
     */
    public List<GoodsInfo> selectGoodsInfoByIds(String[] ids) {
        QueryWrapper<GoodsInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return goodsInfoMapper.selectGoodsInfoList(queryWrapper);
    }

    /**
     * 查询货物信息列表
     *
     * @param goodsInfo 货物信息
     * @return 货物信息集合
     */
    public List<GoodsInfo> selectGoodsInfoList(GoodsInfo goodsInfo) {
        QueryWrapper<GoodsInfo> queryWrapper = getQueryWrapper(goodsInfo);
        return goodsInfoMapper.select(queryWrapper);
    }

    /**
     * 新增货物信息
     *
     * @param goodsInfo 货物信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public GoodsInfo insertGoodsInfo(GoodsInfo goodsInfo) {
        goodsInfo.setId(IdUtil.simpleUUID());
        goodsInfo.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        goodsInfoMapper.insert(goodsInfo);
        return goodsInfo;
    }

    /**
     * 修改货物信息
     *
     * @param goodsInfo 货物信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateGoodsInfo(GoodsInfo goodsInfo) {
        GoodsInfo old = goodsInfoMapper.selectById(goodsInfo.getId());
        //启用时判断库区类型是否已存在使用
        if(StrUtil.isNotEmpty(goodsInfo.getEnableStatus())&&StringUtils.isNotNull(old)
                &&!old.getEnableStatus().equals(goodsInfo.getEnableStatus())&&GoodsInfoEnum.ENABLE.getCode().equals(goodsInfo.getEnableStatus())){
            int i = goodsInfoMapper.selectByAreaId(old);
            if(i>0){
                throw new ServiceException("当前启用数据的库区已被占用");
            }
        }
        goodsInfoMapper.updateById(goodsInfo);
        return AjaxResult.success(goodsInfo);
    }

    /**
     * 批量删除货物信息
     *
     * @param ids 需要删除的货物信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteGoodsInfoByIds(String[] ids) {
        List<GoodsInfo> goodsInfos = new ArrayList<>();
        for (String id : ids) {
            GoodsInfo goodsInfo = new GoodsInfo();
            goodsInfo.setId(id);
            goodsInfo.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            goodsInfos.add(goodsInfo);
        }
        return super.updateBatchById(goodsInfos) ? 1 : 0;
    }

    /**
     * 删除货物信息信息
     *
     * @param id 货物信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteGoodsInfoById(String id) {
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setId(id);
        goodsInfo.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return goodsInfoMapper.updateById(goodsInfo);
    }

    public QueryWrapper<GoodsInfo> getQueryWrapper(GoodsInfo goodsInfo) {
        QueryWrapper<GoodsInfo> queryWrapper = new QueryWrapper<>();
        if (goodsInfo != null) {
            goodsInfo.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", goodsInfo.getDelFlag());
            //入库-新建-货物选框筛选条件
            if (StrUtil.isNotEmpty(goodsInfo.getRkHwxk())) {
                queryWrapper.and(wrapper -> wrapper.like("goods_code", goodsInfo.getRkHwxk())
                        .or().like("goods_name", goodsInfo.getRkHwxk())
                        .or().like("model", goodsInfo.getRkHwxk()));
            }
            //货物编码
            if (StrUtil.isNotEmpty(goodsInfo.getGoodsCode())) {
                queryWrapper.like("goods_code", goodsInfo.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(goodsInfo.getGoodsName())) {
                queryWrapper.like("goods_name", goodsInfo.getGoodsName());
            }
            //货物简称
            if (StrUtil.isNotEmpty(goodsInfo.getGoodsSimpleName())) {
                queryWrapper.like("goods_simple_name", goodsInfo.getGoodsSimpleName());
            }

            //货物类别
            if (StrUtil.isNotEmpty(goodsInfo.getGoodsCategoryId())) {
                queryWrapper.eq("goods_category_id", goodsInfo.getGoodsCategoryId());
            }
            //计量单位
            if (StrUtil.isNotEmpty(goodsInfo.getMeasureUnit())) {
                queryWrapper.eq("measure_unit", goodsInfo.getMeasureUnit());
            }
            //包装方式
            if (StrUtil.isNotEmpty(goodsInfo.getPacking())) {
                queryWrapper.eq("packing", goodsInfo.getPacking());
            }
            //最高库存
            if (goodsInfo.getInventoryCountMax() != null) {
                queryWrapper.eq("inventory_count_max", goodsInfo.getInventoryCountMax());
            }
            //最低库存
            if (goodsInfo.getInventoryCountMin() != null) {
                queryWrapper.eq("inventory_count_min", goodsInfo.getInventoryCountMin());
            }
            //质保期
            if (goodsInfo.getWarranty() != null) {
                queryWrapper.eq("warranty", goodsInfo.getWarranty());
            }
            //启用状态
            if (StrUtil.isNotEmpty(goodsInfo.getEnableStatus())) {
                queryWrapper.eq("enable_status", goodsInfo.getEnableStatus());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param goodsInfoList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName      操作人姓名
     * @return
     */
    public String importData(List<GoodsInfo> goodsInfoList, boolean updateSupport, String operName, String goodsCategoryId, String supplierId) {
        if (StringUtils.isNull(goodsInfoList) || goodsInfoList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (GoodsInfo goodsInfo : goodsInfoList) {
            goodsInfo.setGoodsCategoryId(goodsCategoryId);
//            goodsInfo.setSupplierId(supplierId);
            try {
                //根据唯一属性获取对应数据（自己修改）
                GoodsInfo u = goodsInfoMapper.selectDataByCode(goodsInfo.getGoodsCode());
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, goodsInfo);
                    goodsInfo.setId(IdUtil.simpleUUID());
                    goodsInfo.setCreateBy(operName);
                    goodsInfo.setCreateTime(new Date());
                    goodsInfo.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    goodsInfo.setEnableStatus(GoodsInfoEnum.ENABLE.getCode());
                    goodsInfoMapper.insert(goodsInfo);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, goodsInfo);
                    int count = goodsInfoMapper.checkCode(goodsInfo);
                    if (count > 0) {//判断是否重复
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，货物编码重复；");
                    } else {
                        goodsInfo.setId(u.getId());
                        goodsInfo.setUpdateBy(operName);
                        goodsInfo.setUpdateTime(new Date());
                        goodsInfoMapper.updateById(goodsInfo);
                        successNum++;
                    }
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，货物编码重复；");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、第" + failureNum + "行数据导入失败：";
                failureMsg.append(msg + "数据类型不匹配或者长度太长");
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

    public AjaxResult checkData(GoodsInfo goodsInfo) {
        //验证货物编码唯一性
        //根据id和tempId判断是否修改模板id，修改了模板id判断是否存在该模板id
        int count = goodsInfoMapper.checkCode(goodsInfo);
        if (count > 0) {
            throw new ServiceException("该货物编码已存在，请保证货物编码唯一");
        }
        return AjaxResult.success(true);
    }

    /**
     * 根据货物编码查询货物信息
     * @param goodsCode 货物类型编码
     * @return GoodsInfo
     */
    public GoodsInfo getGoodsNum(String goodsCode){
        QueryWrapper<GoodsInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_code", goodsCode);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return goodsInfoMapper.selectOne(queryWrapper);
    }

}
