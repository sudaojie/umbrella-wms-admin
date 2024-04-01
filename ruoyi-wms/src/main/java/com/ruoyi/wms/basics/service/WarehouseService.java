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
import com.ruoyi.wms.basics.domain.Warehouse;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.WarehouseMapper;
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
 * 仓库基本信息Service接口
 *
 * @author ruoyi
 * @date 2023-01-30
 */
@Slf4j
@Service
public class WarehouseService extends ServiceImpl<WarehouseMapper, Warehouse> {

    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询仓库基本信息
     *
     * @param id 仓库基本信息主键
     * @return 仓库基本信息
     */
    public Warehouse selectWarehouseById(String id){
        QueryWrapper<Warehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return warehouseMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询仓库基本信息
     *
     * @param ids 仓库基本信息 IDs
     * @return 仓库基本信息
     */
    public List<Warehouse> selectWarehouseByIds(String[] ids) {
        QueryWrapper<Warehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return warehouseMapper.selectList(queryWrapper);
    }

    /**
     * 查询仓库基本信息列表
     *
     * @param warehouse 仓库基本信息
     * @return 仓库基本信息集合
     */
    public List<Warehouse> selectWarehouseList(Warehouse warehouse){
        QueryWrapper<Warehouse> queryWrapper = getQueryWrapper(warehouse);
        return warehouseMapper.select(queryWrapper);
    }

    /**
     * 新增仓库基本信息
     *
     * @param warehouse 仓库基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Warehouse insertWarehouse(Warehouse warehouse){
        warehouse.setId(IdUtil.simpleUUID());
        warehouse.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        warehouseMapper.insert(warehouse);
        return warehouse;
    }

    /**
     * 修改仓库基本信息
     *
     * @param warehouse 仓库基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Warehouse updateWarehouse(Warehouse warehouse){
        warehouseMapper.updateById(warehouse);
        return warehouse;
    }

    /**
     * 批量删除仓库基本信息
     *
     * @param ids 需要删除的仓库基本信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteWarehouseByIds(String[] ids){
        List<Warehouse> warehouses = new ArrayList<>();
        for (String id : ids) {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(id);
            warehouse.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            warehouses.add(warehouse);
        }
        //查询全部数据
        List<Warehouse> warehouseOld = warehouseMapper.selectBatchIds(Arrays.asList(ids));
        for (Warehouse warehouse : warehouseOld) {
            int count = areaMapper.selectDataByWarehouseCode(warehouse.getWarehouseCode());
            if (count > 0) {
                throw new ServiceException(warehouse.getWarehouseName() + "该数据已被库区引用无法删除");
            }
        }
        return AjaxResult.success(super.updateBatchById(warehouses) ? 1 : 0);
    }

    /**
     * 删除仓库基本信息信息
     *
     * @param id 仓库基本信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWarehouseById(String id) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return warehouseMapper.updateById(warehouse);
    }

    public QueryWrapper<Warehouse> getQueryWrapper(Warehouse warehouse) {
        QueryWrapper<Warehouse> queryWrapper = new QueryWrapper<>();
        if (warehouse != null) {
            warehouse.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", warehouse.getDelFlag());
            //仓库编码
            if (StrUtil.isNotEmpty(warehouse.getWarehouseCode())) {
                queryWrapper.like("warehouse_code", warehouse.getWarehouseCode());
            }
            //仓库名称
            if (StrUtil.isNotEmpty(warehouse.getWarehouseName())) {
                queryWrapper.like("warehouse_name", warehouse.getWarehouseName());
            }
            //仓库容量(m³)
            if (warehouse.getTotalCapacity() != null) {
                queryWrapper.eq("total_capacity", warehouse.getTotalCapacity());
            }
            //可用容量(m³)
            if (warehouse.getAvailableCapacity() != null) {
                queryWrapper.eq("available_capacity", warehouse.getAvailableCapacity());
            }
        }
        return queryWrapper;
    }

    public AjaxResult checkData(Warehouse warehouse) {

        //验证模板编码唯一性
        //根据id和tempId判断是否修改模板id，修改了模板id判断是否存在该模板id
        int count = warehouseMapper.checkCode(warehouse);
        if(count>0){
            throw new ServiceException("该仓库编码已存在，请保证仓库编码唯一");
        }
        count = warehouseMapper.checkName(warehouse);
        if(count>0){
            throw new ServiceException("该仓库名称已存在，请保证仓库名称唯一");
        }
        return AjaxResult.success(true);
    }

    /**
     * 获取仓库信息
     *
     * @return 【{label：xx，value：xx}】
     */
    public List getWarehouseData() {
        return warehouseMapper.getWarehouseData();
    }

    /**
     * 数据导入实现
     *
     * @param warehouseList 导入的数据
     * @param updateSupport 是否更新已存在数据
     * @param operName      操作人
     * @return
     */
    public String importData(List<Warehouse> warehouseList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(warehouseList) || warehouseList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Warehouse warehouse : warehouseList) {
            try {
                if(StringUtils.isEmpty(warehouse.getWarehouseCode())){
                    throw new ServiceException("导入的仓库编码不能为空");
                }else if(warehouse.getWarehouseCode().length()>20){
                    throw new ServiceException("导入的仓库编码长度不能超过20");
                }
                if(StringUtils.isEmpty(warehouse.getWarehouseName())){
                    throw new ServiceException("导入的仓库名称不能为空");
                }else if(warehouse.getWarehouseName().length()>20){
                    throw new ServiceException("导入的仓库名称长度不能超过20");
                }

                // 验证是否存在仓库
                Warehouse u = warehouseMapper.selectDataByCode(warehouse.getWarehouseCode());
                if (StringUtils.isNull(u)) {
                    int count = warehouseMapper.checkName(warehouse);
                    if(count>0){
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、仓库 " + warehouse.getWarehouseName() + "的名称 已存在");
                    }
                    BeanValidators.validateWithException(validator, warehouse);
                    warehouse.setId(IdUtil.simpleUUID());
                    warehouse.setCreateBy(operName);
                    warehouse.setCreateTime(new Date());
                    warehouse.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    warehouseMapper.insert(warehouse);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、仓库 " + warehouse.getWarehouseName() + " 导入成功");
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, warehouse);
                    warehouse.setId(u.getId());
                    int count = warehouseMapper.checkCode(warehouse);
                    if (count > 0) {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、仓库 " + warehouse.getWarehouseName() + "的编码 已存在");
                    } else {
                        count = warehouseMapper.checkName(warehouse);
                        if(count>0){
                            failureNum++;
                            failureMsg.append("<br/>" + failureNum + "、仓库 " + warehouse.getWarehouseName() + "的名称 已存在");
                        }else{
                            warehouse.setUpdateBy(operName);
                            warehouseMapper.updateById(warehouse);
                            successNum++;
                            successMsg.append("<br/>" + successNum + "、仓库 " + warehouse.getWarehouseName() + " 更新成功");
                        }
                    }
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、仓库 " + warehouse.getWarehouseName() + "的编码 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、仓库 " + warehouse.getWarehouseName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
