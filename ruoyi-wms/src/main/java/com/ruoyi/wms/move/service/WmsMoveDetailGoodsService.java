package com.ruoyi.wms.move.service;

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
import com.ruoyi.wms.move.domain.WmsMoveDetailGoods;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.move.mapper.WmsMoveDetailGoodsMapper;
import com.ruoyi.wms.move.domain.WmsMoveDetailGoods;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 移库单详情货物Service接口
 *
 * @author nf
 * @date 2023-03-01
 */
@Slf4j
@Service
public class WmsMoveDetailGoodsService extends ServiceImpl<WmsMoveDetailGoodsMapper, WmsMoveDetailGoods> {

    @Autowired
    private WmsMoveDetailGoodsMapper wmsMoveDetailGoodsMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询移库单详情货物
     *
     * @param id 移库单详情货物主键
     * @return 移库单详情货物
     */
    public WmsMoveDetailGoods selectWmsMoveDetailGoodsById(String id){
        QueryWrapper<WmsMoveDetailGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsMoveDetailGoodsMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询移库单详情货物
     *
     * @param ids 移库单详情货物 IDs
     * @return 移库单详情货物
     */
    public List<WmsMoveDetailGoods> selectWmsMoveDetailGoodsByIds(String[] ids) {
        QueryWrapper<WmsMoveDetailGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsMoveDetailGoodsMapper.selectList(queryWrapper);
    }

    /**
     * 查询移库单详情货物列表
     *
     * @param wmsMoveDetailGoods 移库单详情货物
     * @return 移库单详情货物集合
     */
    public List<WmsMoveDetailGoods> selectWmsMoveDetailGoodsList(WmsMoveDetailGoods wmsMoveDetailGoods){
        QueryWrapper<WmsMoveDetailGoods> queryWrapper = getQueryWrapper(wmsMoveDetailGoods);
        return wmsMoveDetailGoodsMapper.select(queryWrapper);
    }

    /**
     * 新增移库单详情货物
     *
     * @param wmsMoveDetailGoods 移库单详情货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsMoveDetailGoods insertWmsMoveDetailGoods(WmsMoveDetailGoods wmsMoveDetailGoods){
        wmsMoveDetailGoods.setId(IdUtil.simpleUUID());
        wmsMoveDetailGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsMoveDetailGoodsMapper.insert(wmsMoveDetailGoods);
        return wmsMoveDetailGoods;
    }

    /**
     * 修改移库单详情货物
     *
     * @param wmsMoveDetailGoods 移库单详情货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsMoveDetailGoods updateWmsMoveDetailGoods(WmsMoveDetailGoods wmsMoveDetailGoods){
        wmsMoveDetailGoodsMapper.updateById(wmsMoveDetailGoods);
        return wmsMoveDetailGoods;
    }

    /**
     * 批量删除移库单详情货物
     *
     * @param ids 需要删除的移库单详情货物主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsMoveDetailGoodsByIds(String[] ids){
        List<WmsMoveDetailGoods> wmsMoveDetailGoodss = new ArrayList<>();
        for (String id : ids) {
            WmsMoveDetailGoods wmsMoveDetailGoods = new WmsMoveDetailGoods();
            wmsMoveDetailGoods.setId(id);
            wmsMoveDetailGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsMoveDetailGoodss.add(wmsMoveDetailGoods);
        }
        return super.updateBatchById(wmsMoveDetailGoodss) ? 1 : 0;
    }

    /**
     * 删除移库单详情货物信息
     *
     * @param id 移库单详情货物主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsMoveDetailGoodsById(String id){
        WmsMoveDetailGoods wmsMoveDetailGoods = new WmsMoveDetailGoods();
        wmsMoveDetailGoods.setId(id);
        wmsMoveDetailGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsMoveDetailGoodsMapper.updateById(wmsMoveDetailGoods);
    }

    public QueryWrapper<WmsMoveDetailGoods> getQueryWrapper(WmsMoveDetailGoods wmsMoveDetailGoods) {
        QueryWrapper<WmsMoveDetailGoods> queryWrapper = new QueryWrapper<>();
        if (wmsMoveDetailGoods != null) {
            wmsMoveDetailGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsMoveDetailGoods.getDelFlag());
            //移库单号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getMoveCode())) {
                queryWrapper.eq("move_code",wmsMoveDetailGoods.getMoveCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getGoodsCode())) {
                queryWrapper.like("goods_code",wmsMoveDetailGoods.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getGoodsName())) {
                queryWrapper.like("goods_name",wmsMoveDetailGoods.getGoodsName());
            }
            //规格型号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getModel())) {
                queryWrapper.eq("model",wmsMoveDetailGoods.getModel());
            }
            //计量单位
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getMeasureUnit())) {
                queryWrapper.eq("measure_unit",wmsMoveDetailGoods.getMeasureUnit());
            }
            //批次
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getCharg())) {
                queryWrapper.eq("charg",wmsMoveDetailGoods.getCharg());
            }
            //货物唯一码
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getOnlyCode())) {
                queryWrapper.eq("only_code",wmsMoveDetailGoods.getOnlyCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getMpCode())) {
                queryWrapper.eq("mp_code",wmsMoveDetailGoods.getMpCode());
            }
            //移出仓库编号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getOutWarehouseCode())) {
                queryWrapper.like("out_warehouse_code",wmsMoveDetailGoods.getOutWarehouseCode());
            }
            //移出仓库名称
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getOutWarehouseName())) {
                queryWrapper.like("out_warehouse_name",wmsMoveDetailGoods.getOutWarehouseName());
            }
            //移出库区编号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getOutAreaCode())) {
                queryWrapper.like("out_area_code",wmsMoveDetailGoods.getOutAreaCode());
            }
            //移出库区名称
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getOutAreaName())) {
                queryWrapper.like("out_area_name",wmsMoveDetailGoods.getOutAreaName());
            }
            //移出库位编号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getOutLocationCode())) {
                queryWrapper.like("out_location_code",wmsMoveDetailGoods.getOutLocationCode());
            }
            //移出库位名称
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getOutLocationName())) {
                queryWrapper.like("out_location_name",wmsMoveDetailGoods.getOutLocationName());
            }
            //移入仓库编号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getInWarehouseCode())) {
                queryWrapper.like("in_warehouse_code",wmsMoveDetailGoods.getInWarehouseCode());
            }
            //移入仓库名称
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getInWarehouseName())) {
                queryWrapper.like("in_warehouse_name",wmsMoveDetailGoods.getInWarehouseName());
            }
            //移入库区编号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getInAreaCode())) {
                queryWrapper.like("in_area_code",wmsMoveDetailGoods.getInAreaCode());
            }
            //移入库区名称
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getInAreaName())) {
                queryWrapper.like("in_area_name",wmsMoveDetailGoods.getInAreaName());
            }
            //移入库位编号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getInLocationCode())) {
                queryWrapper.like("in_location_code",wmsMoveDetailGoods.getInLocationCode());
            }
            //移入库位名称
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getInLocationName())) {
                queryWrapper.like("in_location_name",wmsMoveDetailGoods.getInLocationName());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(wmsMoveDetailGoods.getTrayCode())) {
                queryWrapper.like("tray_code",wmsMoveDetailGoods.getTrayCode());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param wmsMoveDetailGoodsList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<WmsMoveDetailGoods> wmsMoveDetailGoodsList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsMoveDetailGoodsList) || wmsMoveDetailGoodsList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WmsMoveDetailGoods wmsMoveDetailGoods : wmsMoveDetailGoodsList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                WmsMoveDetailGoods u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsMoveDetailGoods);
                    wmsMoveDetailGoods.setId(IdUtil.simpleUUID());
                    wmsMoveDetailGoods.setCreateBy(operName);
                    wmsMoveDetailGoods.setCreateTime(new Date());
                    wmsMoveDetailGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsMoveDetailGoodsMapper.insert(wmsMoveDetailGoods);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsMoveDetailGoods);
                    //todo 验证
                    //int count = wmsMoveDetailGoodsMapper.checkCode(wmsMoveDetailGoods);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        wmsMoveDetailGoods.setId(u.getId());
                        wmsMoveDetailGoods.setUpdateBy(operName);
                        wmsMoveDetailGoods.setUpdateTime(new Date());
                        wmsMoveDetailGoodsMapper.updateById(wmsMoveDetailGoods);
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
