package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.bo.EmptyLocationBo;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 库位基本信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
public interface LocationMapper extends BaseMapper<Location> {


    /**
     * 查询库位基本信息列表
     *
     * @param location 库位基本信息
     * @return 库位基本信息集合
     */
    List<Location> select(@Param("ew") QueryWrapper<Location> location);

    /**
     * 验证库位编码唯一性
     *
     * @param location（包含库位编码，id主键）
     * @return
     */
    int checkCode(@Param("object") Location location);

    /**
     * 验证库位编码唯一性
     *
     * @param location（包含库位编码，id主键）
     * @return
     */
    int checkName(@Param("object") Location location);

    Location selectDataByCode(@Param("locationCode") String locationCode);

    List<Map> getLocationData(@Param("object") Location location);

    int selectDataByAreaCode(@Param("areaCode") String areaCode);


    /**
     * 根据库区编码对托盘容量进行统计
     * @param areaCode
     * @return
     */
    BigDecimal countCapacityByArea(@Param("areaCode") String areaCode);
    /**
     * 根据库位编码对托盘可用容量进行统计
     * @param areaCode
     * @return
     */
    BigDecimal countAvailableCapacityByArea(@Param("areaCode") String areaCode);
    /**
     * 根据仓库编码对托盘容量进行统计
     * @param code
     * @return
     */
    BigDecimal countCapacityByHouse(@Param("warehouseCode") String code);
    /**
     * 根据仓库编码对可用容量进行统计
     * @param code
     * @return
     */
    BigDecimal countAvailableCapacityByHouse(@Param("warehouseCode") String code);

    /**
     * 获取库区空库位
     * @param areaCode 库区编码
     * @param limit 获取个数
     * @return locationCode 库位编码
     */
    List<EmptyLocationBo> getEmptyLocation(@Param("areaCode") String areaCode, @Param("limit") Integer limit);

    /**
     * 获取库区空库位
     * @param areaCode 库区编码
     * @param limit 获取个数
     * @return locationCode 库位编码
     */
    List<EmptyLocationBo> getEmptyLocationlhq(@Param("locationCodes") String[] locationCodes, @Param("areaCode") String areaCode, @Param("limit") Integer limit);


    /**
     * 获取晾晒区库区空库位
     * @param areaCode 库区编码
     * @param limit 获取个数
     * @return locationCode 库位编码
     */
    List<EmptyLocationBo> getDryAreaEmptyLocation(@Param("areaCode") String areaCode, @Param("limit") Integer limit);



    int lockLocation(@Param("locations")List<String> sureLocations);

    /**
     * 获取可选择的库位列表
     * @param queryWrapper
     * @return
     */
    List<Location> listByParams(@Param("ew") QueryWrapper<Location> queryWrapper);

    /**
     * 在传入的库位编码中查询锁定库位信息
     * @param locations
     * @return 库位基本信息集合
     */
    List<Location> isLocked(@Param("locations") List<String> locations);

    /**
     * 验证同区下排序值是否存在
     * @param location
     * @return
     */
    int checkOrder(@Param("object") Location location);
}
