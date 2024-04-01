package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.wms.basics.domain.Tray;
import com.ruoyi.wms.basics.dto.AreaDto;
import com.ruoyi.wms.basics.vo.LocationMapVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 托盘基本信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
public interface TrayMapper extends BaseMapper<Tray> {


    /**
     * 查询托盘基本信息列表
     *
     * @param tray 托盘基本信息
     * @return 托盘基本信息集合
     */
    List<Tray> select(@Param("ew") QueryWrapper<Tray> tray);

    /**
     * 根据托盘编码获取
     *
     * @param trayCode
     * @return
     */
    Tray selectDataByCode(@Param("trayCode") String trayCode);

    /**
     * 检查托盘编码是否重发
     *
     * @param tray
     * @return
     */
    int checkCode(@Param("object") Tray tray);

    /**
     * 根据库位编码查询有多少条托盘数据
     *
     * @param locationCode
     * @return
     */
    int selectDataByLocationCode(@Param("locationCode") String locationCode);

    /**
     * 根据库位编码对托盘容量进行统计
     *
     * @param locationCode
     * @return
     */
    BigDecimal countCapacityByLocation(@Param("locationCode") String locationCode);

    /**
     * 根据库位编码对托盘限重进行统计
     *
     * @param locationCode
     * @return
     */
    BigDecimal countWeightByLocation(@Param("locationCode") String locationCode);

    /**
     * 根据库区编码对托盘容量进行统计
     *
     * @param areaCode
     * @return
     */
    BigDecimal countCapacityByArea(@Param("areaCode") String areaCode);

    /**
     * 根据库位编码对托盘可用容量进行统计
     *
     * @param areaCode
     * @return
     */
    BigDecimal countAvailableCapacityByArea(@Param("areaCode") String areaCode);

    /**
     * 根据仓库编码对托盘容量进行统计
     *
     * @param code
     * @return
     */
    BigDecimal countCapacityByHouse(@Param("warehouseCode") String code);

    /**
     * 根据仓库编码对可用容量进行统计
     *
     * @param code
     * @return
     */
    BigDecimal countAvailableCapacityByHouse(@Param("warehouseCode") String code);


    /**
     * 批量修改是否在库位状态
     *
     * @param username
     * @param onLocation
     * @param trayCodeList
     * @return
     */
    int updateOnLocation(@Param("username") String username, @Param("onLocation") String onLocation, @Param("trayCodeList") List<String> trayCodeList);

    /**
     * 获取空托盘
     *
     * @param areaCode 库区编码
     * @param limit    获取数
     * @return trayCode 空托盘, locationCode 空托盘所在的库位
     */
    List<LocationMapVo> getEmptyTray(@Param("areaCode") String areaCode, @Param("limit") Integer limit);

    /**
     * 获取库区内空托盘数量
     *
     * @param areaType 库区类型
     * @return areaCode 库区编码, emptyCount 库区内空托盘数量
     */
    List<AreaDto> getEmptyTrayCountByAreaType(@Param("areaType") String areaType);

    /**
     * 获取库区内货物类型托盘数量
     *
     * @param areaType 库区类型
     * @param goodsCode 货物编码
     * @return areaCode 库区编码, goodsCount 库区内该货物类型托盘数量
     */
    @MapKey("areaCode")
    List<AreaDto> getGoodsTrayCountByAreaType(@Param("areaType") String areaType, @Param("goodsCode") String goodsCode);


    /**
     * 获取托盘位置信息
     *
     * @param trayCodeList
     * @return trayCode 托盘编号， locationCode 托盘所在库位编号， areaCode 托盘所属库区编号， goodsCode 托盘上的货物类型
     */
    List<LocationMapVo> getTrayInfoInCode(@Param("areaCode") String areaCode, @Param("trayCodeList") List<String> trayCodeList);

    /**
     * 获取同一库区类型里可取用的托盘的位置信息
     *
     * @param trayCodeList
     * @return trayCode 托盘编号， locationCode 托盘所在库位编号， areaCode 托盘所属库区编号
     */
    List<LocationMapVo> getTrayInfoInTrayCode(@Param("areaType") String areaType,@Param("trayCodeList") List<String> trayCodeList);


    /**
     * 判断是否为空盘
     * @param trayCode 托盘编码
     * @return boolean
     */
    boolean isEmptyTray(@Param("trayCode") String trayCode);


    /**
     * 增加入库单详情已取托盘数
     * @param num
     * @return
     */
    int updateTakedTrayCount(@Param("num") Integer num, @Param("id") String id);

    /**
     * 更新托盘上的货物编码（如果传入的托盘编码在库存总览表中没有未删除货物，更新托盘的goods——code为null）
     * @param trayCodes
     */
    void updateTrayByCodes(@Param("trayCodes") List<String> trayCodes);

    /**
     * 获取是空托盘的托盘信息
     * @param trayCodeList
     */
    List<Tray> havEmptyTray(@Param("trayCodes") List<String> trayCodeList);


    /**
     * 选中有托盘无货库位取盘
     *
     * @param locationCode 库位
     * @return
     */
    LocationMapVo getEmptyTraylocationCode(@Param("locationCode") String locationCode);
}
