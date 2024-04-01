package com.ruoyi.wms.basics.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.TrayModel;
import org.apache.ibatis.annotations.Param;

/**
 * 托盘规格Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-21
 */
public interface TrayModelMapper extends BaseMapper<TrayModel> {


    /**
     * 查询托盘规格列表
     *
     * @param sysTrayModel 托盘规格
     * @return 托盘规格集合
     */
    List<TrayModel> select(@Param("ew") QueryWrapper<TrayModel> sysTrayModel);

    /**
     * 获取托盘规格信息
     *
     * @return
     */
    List<Map> getTrayModelData();

    /**
     * 根据规格编号查重
     * @return
     */
    List<TrayModel> selectSysTrayModelByCode(@Param("ew") TrayModel s);

    /**
     * 校验修改托盘规格信息是否重复
     * @param trayModel
     * @return
     */
    List<TrayModel> checkData(@Param("ew") TrayModel trayModel);
}
