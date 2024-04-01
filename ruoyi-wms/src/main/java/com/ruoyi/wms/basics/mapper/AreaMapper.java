package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.dto.AreaDto;
import com.ruoyi.wms.basics.vo.SelectedVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 库区基本信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
public interface AreaMapper extends BaseMapper<Area> {


    /**
     * 查询库区基本信息列表
     *
     * @param area 库区基本信息
     * @return 库区基本信息集合
     */
    List<Area> select(@Param("ew") QueryWrapper<Area> area);

    /**
     * 验证库区编码唯一性
     *
     * @param area（包含库区编码，id主键）
     * @return
     */
    int checkCode(@Param("object") Area area);

    /**
     * 验证库区名称唯一性
     *
     * @param area（包含库区名称，id主键）
     * @return
     */
    int checkName(@Param("object") Area area);

    List<SelectedVo> getAreaData(@Param("object") Area area);

    /**
     * 根据库区编码获取仓库信息
     */
    Area selectDataByCode(@Param("areaCode") String areaCode);

    int selectDataByWarehouseCode(@Param("warehouseCode") String warehouseCode);

    /**
     * 获取全部的存储区库区以及空库位数量
     * @param type 库区类型
     * @param areaCode 库区编码（有的话只查询对应库区的数据，没有查全部）
     * @return areaCode 库区编码，emptyNum 空库位数量
     */
    List<AreaDto> selectAllAreaByType(@Param("type") String type, @Param("areaCode")String areaCode);

    /**
     * wms参数配置，初始化获取库区信息
     * @param list
     * @return
     */
    List<Area> findAreaData(@Param("list") List<String> list);

    /**
     * 根据类型获取全部的库区编码
     * @param type
     * @return
     */
    List<String> selectAreaCodeByType(@Param("type")String type);

    /**
     * 获取库区的移库库位
     * @param areaCode
     * @return
     */
    String selectMoveLocationCodeByAreaCode(@Param("areaCode")String areaCode);

    /**
     * 获取库区的备份移库库位
     * @param areaCode
     * @return
     */
    String selectBackUpMoveLocationCodeByAreaCode(@Param("areaCode")String areaCode);


    /**
     * 获取库区的备份2移库库位
     * @param areaCode
     * @return
     */
    String selectBackUp2MoveLocationCodeByAreaCode(@Param("areaCode")String areaCode);
}
