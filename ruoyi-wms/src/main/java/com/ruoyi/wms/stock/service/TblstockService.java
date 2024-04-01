package com.ruoyi.wms.stock.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.service.AreaService;
import com.ruoyi.wms.enums.AreaTypeEnum;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.dto.TblstockDto;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存总览Service接口
 *
 * @author ruoyi
 * @date 2023-02-06
 */
@Slf4j
@Service
public class TblstockService extends ServiceImpl<TblstockMapper, Tblstock> {

    @Autowired
    protected Validator validator;

    @Autowired(required = false)
    private TblstockMapper tblstockMapper;

    @Autowired
    private AreaService areaService;

    /**
     * 查询库存总览
     *
     * @param id 库存总览主键
     * @return 库存总览
     */
    public Tblstock selectTblstockById(String id) {
        QueryWrapper<Tblstock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return tblstockMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询库存总览
     *
     * @param ids 库存总览 IDs
     * @return 库存总览
     */
    public List<Tblstock> selectTblstockByIds(String[] ids) {
        QueryWrapper<Tblstock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return tblstockMapper.selectList(queryWrapper);
    }

    /**
     * 查询库存总览列表
     *
     * @param tblstock 库存总览
     * @return 库存总览集合
     */
    public List<Tblstock> selectTblstockList(Tblstock tblstock) {
        QueryWrapper<Tblstock> queryWrapper = getQueryWrapper(tblstock);
        return tblstockMapper.select(queryWrapper);
    }

    /**
     * 查询可出库库存总览列表
     *
     * @param tblstock 库存总览
     * @return 库存总览集合
     */
    public List<Tblstock> canOutList(Tblstock tblstock) {
        QueryWrapper<Tblstock> queryWrapper = getQueryWrapper(tblstock);
        queryWrapper.isNotNull("location_code");
        List<String> ccqAreaCodeList = areaService.getAreaCodeListByType(AreaTypeEnum.CCQ.getCode());
        queryWrapper.in("area_code",ccqAreaCodeList);
        PageUtils.startPage();
        return tblstockMapper.select(queryWrapper);
    }


    /**
     * 新增库存总览
     *
     * @param tblstock 库存总览
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Tblstock insertTblstock(Tblstock tblstock) {
        tblstock.setId(IdUtil.simpleUUID());
        tblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        tblstockMapper.insert(tblstock);
        return tblstock;
    }
    /**
     * 批量新增库存总览
     *
     * @param tblstockList 库存总览
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Tblstock> tblstockList) {
        tblstockList = tblstockList.stream().map(tblstock -> {
            tblstock.setId(IdUtil.simpleUUID());
            tblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            return tblstock;
        }).collect(Collectors.toList());
        return this.saveBatch(tblstockList,tblstockList.size());
    }

    /**
     * 修改库存总览
     *
     * @param tblstock 库存总览
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Tblstock updateTblstock(Tblstock tblstock) {
        tblstockMapper.updateById(tblstock);
        return tblstock;
    }



    /**
     * 批量删除库存总览
     *
     * @param ids 需要删除的库存总览主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteTblstockByIds(String[] ids) {
        List<Tblstock> tblstocks = new ArrayList<>();
        for (String id : ids) {
            Tblstock tblstock = new Tblstock();
            tblstock.setId(id);
            tblstock.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            tblstocks.add(tblstock);
        }
        return super.updateBatchById(tblstocks) ? 1 : 0;
    }

    /**
     * 删除库存总览信息
     *
     * @param id 库存总览主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteTblstockById(String id) {
        Tblstock tblstock = new Tblstock();
        tblstock.setId(id);
        tblstock.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return tblstockMapper.updateById(tblstock);
    }

    public QueryWrapper<Tblstock> getQueryWrapper(Tblstock tblstock) {
        QueryWrapper<Tblstock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        if (tblstock != null) {
            //货物唯一码
            if (StrUtil.isNotEmpty(tblstock.getOnlyCode())) {
                queryWrapper.like("only_code", tblstock.getOnlyCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(tblstock.getPartsCode())) {
                queryWrapper.like("parts_code", tblstock.getPartsCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(tblstock.getGoodsCode())) {
                queryWrapper.like("goods_code", tblstock.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(tblstock.getGoodsName())) {
                queryWrapper.like("goods_name", tblstock.getGoodsName());
            }
            //规格型号
            if (StrUtil.isNotEmpty(tblstock.getModel())) {
                queryWrapper.eq("model", tblstock.getModel());
            }
            //计量单位
            if (StrUtil.isNotEmpty(tblstock.getMeasureUnit())) {
                queryWrapper.eq("measure_unit", tblstock.getMeasureUnit());
            }
            //锁定状态
            if (StrUtil.isNotEmpty(tblstock.getLockStatus())) {
                queryWrapper.eq("lock_status", tblstock.getLockStatus());
            }else{
                queryWrapper.eq("lock_status", LockEnum.NOTLOCK.getCode());
            }
            //批次
            if (StrUtil.isNotEmpty(tblstock.getCharg())) {
                queryWrapper.eq("charg", tblstock.getCharg());
            }
            //供应商编码
            if (StrUtil.isNotEmpty(tblstock.getSupplierCode())) {
                queryWrapper.eq("supplier_code", tblstock.getSupplierCode());
            }
            //供应商名称
            if (StrUtil.isNotEmpty(tblstock.getSupplierName())) {
                queryWrapper.like("supplier_name", tblstock.getSupplierName());
            }
            //仓库编号
            if (StrUtil.isNotEmpty(tblstock.getWarehouseCode())) {
                queryWrapper.eq("warehouse_code", tblstock.getWarehouseCode());
            }
            //仓库名称
            if (StrUtil.isNotEmpty(tblstock.getWarehouseName())) {
                queryWrapper.like("warehouse_name", tblstock.getWarehouseName());
            }
            //库区编号
            if (StrUtil.isNotEmpty(tblstock.getAreaCode())) {
                queryWrapper.eq("area_code", tblstock.getAreaCode());
            }
            //库区名称
            if (StrUtil.isNotEmpty(tblstock.getAreaName())) {
                queryWrapper.like("area_name", tblstock.getAreaName());
            }
            //库位编号
            if (StrUtil.isNotEmpty(tblstock.getLocationCode())) {
                queryWrapper.like("location_code", tblstock.getLocationCode());
            }
            //库位名称
            if (StrUtil.isNotEmpty(tblstock.getLocationName())) {
                queryWrapper.like("location_name", tblstock.getLocationName());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(tblstock.getTrayCode())) {
                queryWrapper.eq("tray_code", tblstock.getTrayCode());
            }
        }
        queryWrapper.orderByAsc("SUBSTR(only_code,5,LENGTH(only_code))");
        return queryWrapper;
    }

    public QueryWrapper<Tblstock> getQueryListWrapper(Tblstock tblstock) {
        QueryWrapper<Tblstock> queryWrapper = new QueryWrapper<>();
        if (tblstock != null) {
            tblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("w.del_flag", tblstock.getDelFlag());

            //货物名称
            if (StrUtil.isNotEmpty(tblstock.getGoodsName())) {
                queryWrapper.like("w.goods_name", tblstock.getGoodsName());
            }
            //仓库编号
            if (StrUtil.isNotEmpty(tblstock.getWarehouseCode())) {
                queryWrapper.eq("w.warehouse_code", tblstock.getWarehouseCode());
            }
            //仓库名称
            if (StrUtil.isNotEmpty(tblstock.getWarehouseName())) {
                queryWrapper.like("w.warehouse_name", tblstock.getWarehouseName());
            }
        }
        queryWrapper.groupBy("w.charg,w.goods_code,w.tray_code");
        queryWrapper.orderByAsc("SUBSTR(w.only_code,5,LENGTH(w.only_code))");
        return queryWrapper;
    }

    public QueryWrapper<Tblstock> QueryDetailWrapper(Tblstock tblstock) {
        QueryWrapper<Tblstock> queryWrapper = new QueryWrapper<>();
        if (tblstock != null) {
            tblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("w.del_flag", tblstock.getDelFlag());

            //货物唯一码
            if (StrUtil.isNotEmpty(tblstock.getOnlyCode())) {
                queryWrapper.like("w.only_code", tblstock.getOnlyCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(tblstock.getPartsCode())) {
                queryWrapper.like("w.parts_code", tblstock.getPartsCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(tblstock.getGoodsCode())) {
                queryWrapper.like("w.goods_code", tblstock.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(tblstock.getGoodsName())) {
                queryWrapper.like("w.goods_name", tblstock.getGoodsName());
            }
            //规格型号
            if (StrUtil.isNotEmpty(tblstock.getModel())) {
                queryWrapper.like("w.model", tblstock.getModel());
            }
            //计量单位
            if (StrUtil.isNotEmpty(tblstock.getMeasureUnit())) {
                queryWrapper.like("w.measure_unit", tblstock.getMeasureUnit());
            }
            //批次
            if (StrUtil.isNotEmpty(tblstock.getCharg())) {
                queryWrapper.eq("w.charg", tblstock.getCharg());
            }
            //托盘
            if (StrUtil.isNotEmpty(tblstock.getTrayCode())) {
                queryWrapper.eq("w.tray_code", tblstock.getTrayCode());
            }
        }
        queryWrapper.orderByAsc("SUBSTR(w.only_code,5,LENGTH(w.only_code))");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param tblstockList  模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName      操作人姓名
     * @return
     */
    public String importData(List<Tblstock> tblstockList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(tblstockList) || tblstockList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Tblstock tblstock : tblstockList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                Tblstock u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, tblstock);
                    tblstock.setId(IdUtil.simpleUUID());
                    tblstock.setCreateBy(operName);
                    tblstock.setCreateTime(new Date());
                    tblstock.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    tblstockMapper.insert(tblstock);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, tblstock);
                    tblstock.setId(u.getId());
                    tblstock.setUpdateBy(operName);
                    tblstock.setUpdateTime(new Date());
                    tblstockMapper.updateById(tblstock);
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
     * pc查询库存总览列表
     * @param tblstock
     * @return
     */
    public List<Tblstock> selectDetailList(Tblstock tblstock) {
        QueryWrapper<Tblstock> queryWrapper = getQueryListWrapper(tblstock);
        return tblstockMapper.selectTblstockList(queryWrapper);
    }

    /**
     * pc查询库存详情列表
     * @param tblstock
     * @return
     */
    public List<Tblstock> showTblstockDetail(Tblstock tblstock) {
        QueryWrapper<Tblstock> queryWrapper = QueryDetailWrapper(tblstock);
        return tblstockMapper.showTblstockDetail(queryWrapper);
    }

    /**
     * 查询库存中托盘分组
     * @param map
     * @return
     */
    public List<Tblstock> selectTblstockTray(TblstockDto map){
        String param = map.getParam();//机件号、物品名称、仓位号、托盘号
        LambdaQueryWrapper<Area> areaQueryWrapper = Wrappers.lambdaQuery();
        areaQueryWrapper.select(Area::getAreaCode)
                .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .eq(Area::getAreaType, AreaTypeEnum.CCQ.getCode());
        List<String> collect = areaService.getBaseMapper().selectObjs(areaQueryWrapper).stream().map(String::valueOf).collect(Collectors.toList());
        QueryWrapper<Tblstock> tblstockQuery = Wrappers.query();
        tblstockQuery.select("distinct location_code locationCode, tray_code trayCode, goods_code goodsCode, goods_name goodsName")
                .eq("lock_status", LockEnum.NOTLOCK.getCode())
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .in("area_code",collect);
        if (StringUtils.isNotEmpty(param)){
            tblstockQuery.and(query -> {
                query.like("parts_code",param).or()
                        .like("goods_code",param).or()
                        .like("location_code",param).or()
                        .like("tray_code",param);
            });
        }
        tblstockQuery.orderByAsc("SUBSTR(only_code,5,LENGTH(only_code))");
        List<Tblstock> tblstockList = tblstockMapper.selectList(tblstockQuery);
        return tblstockList;
    }

    /**
     * 查询库存中托盘详情
     * @param map
     * @return
     */
    public AjaxResult selectTblstockTrayDetail(TblstockDto map){
        RuoYiConfig.getUploadPath();
        String trayCode = map.getTrayCode();//托盘号
        if (StringUtils.isEmpty(trayCode)){
            throw new RuntimeException("参数错误");
        }
        QueryWrapper<Tblstock> tblstockQuery = Wrappers.query();
        tblstockQuery.eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("tray_code",trayCode);
        tblstockQuery.orderByAsc("SUBSTR(only_code,5,LENGTH(only_code))");
        List<Tblstock> tblstockList = tblstockMapper.selectList(tblstockQuery);
        return AjaxResult.success("成功",tblstockList);
    }

}

