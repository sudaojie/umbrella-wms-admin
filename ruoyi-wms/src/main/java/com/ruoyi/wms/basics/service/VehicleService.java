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
import com.ruoyi.wms.basics.domain.Vehicle;
import com.ruoyi.wms.basics.mapper.VehicleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;

/**
 * 车辆基本信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@Slf4j
@Service
public class VehicleService extends ServiceImpl<VehicleMapper, Vehicle> {

    @Autowired
    protected Validator validator;
    @Autowired
    private VehicleMapper vehicleMapper;

    /**
     * 查询车辆基本信息
     *
     * @param id 车辆基本信息主键
     * @return 车辆基本信息
     */
    public Vehicle selectVehicleById(String id) {
        QueryWrapper<Vehicle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return vehicleMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询车辆基本信息
     *
     * @param ids 车辆基本信息 IDs
     * @return 车辆基本信息
     */
    public List<Vehicle> selectVehicleByIds(String[] ids) {
        QueryWrapper<Vehicle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return vehicleMapper.selectList(queryWrapper);
    }

    /**
     * 查询车辆基本信息列表
     *
     * @param vehicle 车辆基本信息
     * @return 车辆基本信息集合
     */
    public List<Vehicle> selectVehicleList(Vehicle vehicle) {
        QueryWrapper<Vehicle> queryWrapper = getQueryWrapper(vehicle);
        return vehicleMapper.select(queryWrapper);
    }

    /**
     * 新增车辆基本信息
     *
     * @param vehicle 车辆基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Vehicle insertVehicle(Vehicle vehicle) {
        vehicle.setId(IdUtil.simpleUUID());
        vehicle.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        vehicleMapper.insert(vehicle);
        return vehicle;
    }

    /**
     * 修改车辆基本信息
     *
     * @param vehicle 车辆基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Vehicle updateVehicle(Vehicle vehicle) {
        vehicleMapper.updateById(vehicle);
        return vehicle;
    }

    /**
     * 批量删除车辆基本信息
     *
     * @param ids 需要删除的车辆基本信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteVehicleByIds(String[] ids) {
        List<Vehicle> vehicles = new ArrayList<>();
        for (String id : ids) {
            Vehicle vehicle = new Vehicle();
            vehicle.setId(id);
            vehicle.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            vehicles.add(vehicle);
        }
        return super.updateBatchById(vehicles) ? 1 : 0;
    }

    /**
     * 删除车辆基本信息信息
     *
     * @param id 车辆基本信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteVehicleById(String id) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return vehicleMapper.updateById(vehicle);
    }

    public QueryWrapper<Vehicle> getQueryWrapper(Vehicle vehicle) {
        QueryWrapper<Vehicle> queryWrapper = new QueryWrapper<>();
        if (vehicle != null) {
            vehicle.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", vehicle.getDelFlag());
            //车牌号
            if (StrUtil.isNotEmpty(vehicle.getVehicleNo())) {
                queryWrapper.like("vehicle_no", vehicle.getVehicleNo());
            }
            //车辆类型
            if (StrUtil.isNotEmpty(vehicle.getVehicleType())) {
                queryWrapper.eq("vehicle_type", vehicle.getVehicleType());
            }
            //车辆载重(kg)
            if (vehicle.getVehicleLoad() != null) {
                queryWrapper.eq("vehicle_load", vehicle.getVehicleLoad());
            }
            //司机姓名
            if (StrUtil.isNotEmpty(vehicle.getDriverName())) {
                queryWrapper.like("driver_name", vehicle.getDriverName());
            }
            //司机电话
            if (StrUtil.isNotEmpty(vehicle.getDriverPhone())) {
                queryWrapper.like("driver_phone", vehicle.getDriverPhone());
            }

        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param vehicleList   模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName      操作人姓名
     * @return
     */
    public String importData(List<Vehicle> vehicleList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(vehicleList) || vehicleList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Vehicle vehicle : vehicleList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                Vehicle u = vehicleMapper.selectDataByCode(vehicle.getVehicleNo());
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, vehicle);
                    vehicle.setId(IdUtil.simpleUUID());
                    vehicle.setCreateBy(operName);
                    vehicle.setCreateTime(new Date());
                    vehicle.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    vehicleMapper.insert(vehicle);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, vehicle);
                    int count = vehicleMapper.checkCode(vehicle);
                    if (count > 0) {//判断是否重复
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，车牌号重复；");
                    } else {
                        vehicle.setId(u.getId());
                        vehicle.setUpdateBy(operName);
                        vehicle.setUpdateTime(new Date());
                        vehicleMapper.updateById(vehicle);
                        successNum++;
                    }
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，车牌号重复；");
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

    public AjaxResult checkData(Vehicle vehicle) {
        //验证车牌号唯一性
        int count = vehicleMapper.checkCode(vehicle);
        if(count>0){
            throw new ServiceException("该车牌号已存在，请保证车牌号唯一");
        }
        return AjaxResult.success(true);
    }
    /**
     * 获取车辆信息
     *
     * @return 【{label：xx，value：xx}】
     */
    public List<Map> getVehicleList() {
        return vehicleMapper.getVehicleList();
    }
}
