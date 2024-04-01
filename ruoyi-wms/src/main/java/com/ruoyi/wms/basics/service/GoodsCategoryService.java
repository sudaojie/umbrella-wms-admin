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
import com.ruoyi.wms.basics.domain.GoodsCategory;
import com.ruoyi.wms.basics.mapper.GoodsCategoryMapper;
import com.ruoyi.wms.basics.mapper.GoodsInfoMapper;
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
 * 货物类别信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@Slf4j
@Service
public class GoodsCategoryService extends ServiceImpl<GoodsCategoryMapper, GoodsCategory> {

    @Autowired
    protected Validator validator;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    /**
     * 查询货物类别信息
     *
     * @param id 货物类别信息主键
     * @return 货物类别信息
     */
    public GoodsCategory selectGoodsCategoryById(String id) {
        QueryWrapper<GoodsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return goodsCategoryMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询货物类别信息
     *
     * @param ids 货物类别信息 IDs
     * @return 货物类别信息
     */
    public List<GoodsCategory> selectGoodsCategoryByIds(String[] ids) {
        QueryWrapper<GoodsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return goodsCategoryMapper.selectList(queryWrapper);
    }

    /**
     * 查询货物类别信息列表
     *
     * @param goodsCategory 货物类别信息
     * @return 货物类别信息集合
     */
    public List<GoodsCategory> selectGoodsCategoryList(GoodsCategory goodsCategory) {
        QueryWrapper<GoodsCategory> queryWrapper = getQueryWrapper(goodsCategory);
        return goodsCategoryMapper.select(queryWrapper);
    }

    /**
     * 新增货物类别信息
     *
     * @param goodsCategory 货物类别信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public GoodsCategory insertGoodsCategory(GoodsCategory goodsCategory) {
        goodsCategory.setId(IdUtil.simpleUUID());
        goodsCategory.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        goodsCategoryMapper.insert(goodsCategory);
        return goodsCategory;
    }

    /**
     * 修改货物类别信息
     *
     * @param goodsCategory 货物类别信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public GoodsCategory updateGoodsCategory(GoodsCategory goodsCategory) {
        goodsCategoryMapper.updateById(goodsCategory);
        return goodsCategory;
    }

    /**
     * 批量删除货物类别信息
     *
     * @param ids 需要删除的货物类别信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteGoodsCategoryByIds(String[] ids) {
        List<GoodsCategory> goodsCategorys = new ArrayList<>();
        for (String id : ids) {
            GoodsCategory goodsCategory = new GoodsCategory();
            goodsCategory.setId(id);
            goodsCategory.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            goodsCategorys.add(goodsCategory);
        }
        List<GoodsCategory> categories = goodsCategoryMapper.selectBatchIds(Arrays.asList(ids));
        for (GoodsCategory category : categories) {
            int count = goodsInfoMapper.selectDataByCategoryCode(category.getCategoryCode());
            if (count > 0) {
                throw new ServiceException(category.getCategoryName() + "该数据已被货物引用无法删除");
            }
        }
        return AjaxResult.success(super.updateBatchById(goodsCategorys) ? 1 : 0);
    }

    /**
     * 删除货物类别信息信息
     *
     * @param id 货物类别信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteGoodsCategoryById(String id) {
        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setId(id);
        goodsCategory.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return goodsCategoryMapper.updateById(goodsCategory);
    }

    public QueryWrapper<GoodsCategory> getQueryWrapper(GoodsCategory goodsCategory) {
        QueryWrapper<GoodsCategory> queryWrapper = new QueryWrapper<>();
        if (goodsCategory != null) {
            goodsCategory.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", goodsCategory.getDelFlag());
            //类别编码
            if (StrUtil.isNotEmpty(goodsCategory.getCategoryCode())) {
                queryWrapper.like("category_code", goodsCategory.getCategoryCode());
            }
            //类别名称
            if (StrUtil.isNotEmpty(goodsCategory.getCategoryName())) {
                queryWrapper.like("category_name", goodsCategory.getCategoryName());
            }

        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param goodsCategoryList 模板数据
     * @param updateSupport     是否更新已经存在的数据
     * @param operName          操作人姓名
     * @return
     */
    public String importData(List<GoodsCategory> goodsCategoryList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(goodsCategoryList) || goodsCategoryList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (GoodsCategory goodsCategory : goodsCategoryList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                GoodsCategory u = goodsCategoryMapper.selectDataByCode(goodsCategory.getCategoryCode());
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, goodsCategory);
                    goodsCategory.setId(IdUtil.simpleUUID());
                    goodsCategory.setCreateBy(operName);
                    goodsCategory.setCreateTime(new Date());
                    goodsCategory.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    goodsCategoryMapper.insert(goodsCategory);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, goodsCategory);
                    int count = goodsCategoryMapper.checkCode(goodsCategory);
                    if (count > 0) {//判断是否重复
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，数据重复；");
                    } else {
                        goodsCategory.setId(u.getId());
                        goodsCategory.setUpdateBy(operName);
                        goodsCategory.setUpdateTime(new Date());
                        goodsCategoryMapper.updateById(goodsCategory);
                        successNum++;
                    }
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

    public AjaxResult checkData(GoodsCategory goodsCategory) {
        //验证货物类别编码唯一性
        //根据id和tempId判断是否修改模板id，修改了模板id判断是否存在该模板id
        int count = goodsCategoryMapper.checkCode(goodsCategory);
        if (count > 0) {
            throw new ServiceException("该货物类别编码已存在，请保证货物类别编码唯一");
        }
        return AjaxResult.success(true);
    }

    public List getGoodscategoryData() {
        return goodsCategoryMapper.getGoodscategoryData();
    }
}
