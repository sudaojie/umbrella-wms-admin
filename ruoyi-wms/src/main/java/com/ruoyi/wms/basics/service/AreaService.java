package com.ruoyi.wms.basics.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.enums.wcs.WcsDeviceAreaEnum;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.enums.AreaTypeEnum;
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
 * 库区基本信息Service接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Slf4j
@Service
public class AreaService extends ServiceImpl<AreaMapper, Area> {

    @Autowired
    protected Validator validator;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private WcsTaskApiService wcsTaskApiService;

    /**
     * 查询库区基本信息
     *
     * @param id 库区基本信息主键
     * @return 库区基本信息
     */
    public Area selectAreaById(String id) {
        QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return areaMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询库区基本信息
     *
     * @param ids 库区基本信息 IDs
     * @return 库区基本信息
     */
    public List<Area> selectAreaByIds(String[] ids) {
        QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return areaMapper.selectList(queryWrapper);
    }

    /**
     * 查询库区基本信息列表
     *
     * @param area 库区基本信息
     * @return 库区基本信息集合
     */
    public List<Area> selectAreas(Area area) {
        QueryWrapper<Area> queryWrapper = getQueryWrapper(area);
        return areaMapper.select(queryWrapper);
    }


    /**
     * 查询库区基本信息列表
     *
     * @param area 库区基本信息
     * @return 库区基本信息集合
     */
    public List<Area> selectAreaList(Area area) {
        QueryWrapper<Area> queryWrapper = getQueryWrapper(area);
        List<Area> selectArea = areaMapper.select(queryWrapper);
        boolean isEqualToOne = selectArea.stream().anyMatch(areaInfo -> WcsDeviceAreaEnum.STORAGE.getCode().equals(areaInfo.getAreaType()));
        if(isEqualToOne){
            List<Integer> enableStacker = wcsTaskApiService.getEnableStacker();
            if(CollUtil.isEmpty(enableStacker)){
                throw new ServiceException("堆垛机目前都处于手动模式，取盘失败");
            }
            List<String> areaIds = wcsTaskApiService.getStoreAreaIdByEnableStackerId(enableStacker);
            selectArea = selectArea.parallelStream().filter(item -> areaIds.contains(item.getAreaCode())).collect(Collectors.toList());
        }

        return selectArea;
    }

    /**
     * 新增库区基本信息
     *
     * @param area 库区基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Area insertArea(Area area) {
        area.setId(IdUtil.simpleUUID());
        area.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        areaMapper.insert(area);
        return area;
    }

    /**
     * 修改库区基本信息
     *
     * @param area 库区基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Area updateArea(Area area) {
        areaMapper.updateById(area);
        return area;
    }

    /**
     * 批量删除库区基本信息
     *
     * @param ids 需要删除的库区基本信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteAreaByIds(String[] ids) {
        List<Area> areas = new ArrayList<>();
        for (String id : ids) {
            Area area = new Area();
            area.setId(id);
            area.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            areas.add(area);
        }
        //查询全部数据
        List<Area> areaOld = areaMapper.selectBatchIds(Arrays.asList(ids));
        for (Area area : areaOld) {
            int count = locationMapper.selectDataByAreaCode(area.getAreaCode());
            if (count > 0) {
                throw new ServiceException(area.getAreaName() + "该数据已被库位引用无法删除");
            }
        }
        return AjaxResult.success(super.updateBatchById(areas) ? 1 : 0);
    }

    /**
     * 删除库区基本信息信息
     *
     * @param id 库区基本信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteAreaById(String id) {
        Area area = new Area();
        area.setId(id);
        area.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return areaMapper.updateById(area);
    }

    public QueryWrapper<Area> getQueryWrapper(Area area) {
        QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
        if (area != null) {
            area.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", area.getDelFlag());
            //库区编码
            if (StrUtil.isNotEmpty(area.getAreaCode())) {
                queryWrapper.like("area_code", area.getAreaCode());
            }
            //库区名称
            if (StrUtil.isNotEmpty(area.getAreaName())) {
                queryWrapper.like("area_name", area.getAreaName());
            }
            //库区类型
            if (StrUtil.isNotEmpty(area.getAreaType())) {
                queryWrapper.eq("area_type",area.getAreaType());
            }
            //所属仓库编号
            if (StrUtil.isNotEmpty(area.getWarehouseId())) {
                queryWrapper.eq("warehouse_id", area.getWarehouseId());
            }
            //库区容量(m³)
            if (area.getTotalCapacity() != null) {
                queryWrapper.eq("total_capacity", area.getTotalCapacity());
            }
            //可用容量(m³)
            if (area.getAvailableCapacity() != null) {
                queryWrapper.eq("available_capacity", area.getAvailableCapacity());
            }
        }
        return queryWrapper;
    }

    public AjaxResult checkData(Area area) {
        //验证库区数据唯一性
        int count = areaMapper.checkCode(area);
        if(count>0){
            throw new ServiceException("该库区编码在仓库"+area.getWarehouseId()+"中已存在，请重新输入");
        }
        count = areaMapper.checkName(area);
        if(count>0){
            throw new ServiceException("该库区名称在仓库"+area.getWarehouseId()+"中已存在，请重新输入");
        }
        return AjaxResult.success(true);
    }

    /**
     * 获取库区信息（）
     *
     * @return 【{label：xx，value：xx}】
     */
    public List getAreaData(Area area) {
        return areaMapper.getAreaData(area);
    }

    /**
     * 数据导入实现
     *
     * @param areaList      导入的数据
     * @param updateSupport 是否更新已存在数据
     * @param operName      操作人
     * @param warehouseId   选择的仓库
     * @return
     */
    public String importData(List<Area> areaList, boolean updateSupport, String operName, String warehouseId) {
        if (StringUtils.isNull(areaList) || areaList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Area area : areaList) {
            area.setWarehouseId(warehouseId);
            if (StringUtils.isEmpty(area.getAreaType())){
                throw new ServiceException("库区类型选择有误！请选择库区编号:0.存储区 1.晾晒区 2.理货区");
            }
            try {
                // 验证是否存在这个库区
                Area u = areaMapper.selectDataByCode(area.getAreaCode());
                if (StringUtils.isNull(u)) {
                    int count = areaMapper.checkName(area);
                    if(count>0){
                        failureNum++;
//                        failureMsg.append("<br/>" + failureNum + "、仓库"+warehouseId+"下库区 " + area.getAreaName() + "的名称 已存在");
                        failureMsg.append("<br/>" + failureNum + "、库区 " + area.getAreaName() + "的名称 已存在");
                    }
                    BeanValidators.validateWithException(validator, area);
                    area.setId(IdUtil.simpleUUID());
                    area.setCreateBy(operName);
                    area.setCreateTime(new Date());
                    area.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    areaMapper.insert(area);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、库区 " + area.getAreaName() + " 导入成功");
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, area);
                    area.setId(u.getId());
                    int count = areaMapper.checkCode(area);
                    if (count > 0) {
                        failureNum++;
//                        failureMsg.append("<br/>" + failureNum + "、仓库"+warehouseId+"下库区 " + area.getAreaName() + "的编码 已存在");
                        failureMsg.append("<br/>" + failureNum + "、库区 " + area.getAreaName() + "的编码 已存在");
                    } else {
                        count = areaMapper.checkName(area);
                        if(count>0){
                            failureNum++;
//                            failureMsg.append("<br/>" + failureNum + "、仓库"+warehouseId+"下库区 " + area.getAreaName() + "的名称 已存在");
                            failureMsg.append("<br/>" + failureNum + "、库区 " + area.getAreaName() + "的名称 已存在");
                        }else{
                            area.setUpdateBy(operName);
                            areaMapper.updateById(area);
                            successNum++;
                            successMsg.append("<br/>" + successNum + "、库区 " + area.getAreaName() + " 更新成功");
                        }
                    }
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、库区 " + area.getAreaName() + "的编码 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、库区 " + area.getAreaName() + " 导入失败：";
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

    /**
     * wms参数配置，初始化获取库区信息
     * @return
     */
    public List<Area> findAreaData() {
        Area area = new Area();
        List<String> areaTypeList = new ArrayList<>();
        areaTypeList.add(AreaTypeEnum.CCQ.getCode());
        areaTypeList.add(AreaTypeEnum.LSQ.getCode());
        return areaMapper.findAreaData(areaTypeList);
    }

    public List<String> selectAreaCodeByType(String code) {
        return areaMapper.selectAreaCodeByType(code);
    }


    /**
     * 获取移库库位编号集合
     * @return
     */
    public List<String> getMoveLocationList(){
        //查询移库库位
        List<String> moveList = areaMapper.selectList(new QueryWrapper<Area>()
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("area_type", AreaTypeEnum.CCQ.getCode())
                .isNotNull("move_location_code")
        ).stream().map(Area::getMoveLocationCode).collect(Collectors.toList());
        return moveList;
    }

    /**
     * 根据类型获取区域编码
     * @param type
     * @return
     */
    public List<String> getAreaCodeListByType(String type){
        List<String> codeList = areaMapper.selectList(new QueryWrapper<Area>()
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("area_type", type)
        ).stream().map(Area::getAreaCode).collect(Collectors.toList());
        return codeList;
    }
}
