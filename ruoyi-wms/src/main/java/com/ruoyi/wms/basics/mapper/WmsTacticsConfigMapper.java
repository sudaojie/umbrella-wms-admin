package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.WmsTacticsConfig;
import com.ruoyi.wms.move.domain.WmsMoveList;
import com.ruoyi.wms.nolist.domain.NolistWait;
import com.ruoyi.wms.outbound.domain.OutBill;
import com.ruoyi.wms.stock.domain.DryOutbill;
import com.ruoyi.wms.warehousing.domain.InBill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 货物类型托盘取盘回盘策略配置Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-24
 */
public interface WmsTacticsConfigMapper extends BaseMapper<WmsTacticsConfig> {

    /**
     * 查询货物类型托盘取盘回盘策略配置列表
     *
     * @param wmsTacticsConfig 货物类型托盘取盘回盘策略配置
     * @return 货物类型托盘取盘回盘策略配置集合
     */
    List<WmsTacticsConfig> select(@Param("ew") QueryWrapper<WmsTacticsConfig> wmsTacticsConfig);

    /**
     * 校验入库单状态，不存在（待收货，已收货，上架中）
     * @param queryWrapper
     * @return
     */
    int selectInbillStatus(@Param("ew") QueryWrapper<InBill> queryWrapper);

    /**
     * 校验出库单状态，不存在（待拣货，拣货中）
     * @param queryOutbillWrapper
     * @return
     */
    int selectOutbillStatus(@Param("ew") QueryWrapper<OutBill> queryOutbillWrapper);

    /**
     * 校验移库单状态，不存在（未移库, 移库中）
     * @param queryMovebillWrapper
     * @return
     */
    int selectMovebillStatus(@Param("ew") QueryWrapper<WmsMoveList> queryMovebillWrapper);

    /**
     * 校验晾晒出库单状态，不存在（待出库，出库中）
     * @param queryDryOutbillWrapper
     * @return
     */
    int selectDryOutbillStatus(@Param("ew") QueryWrapper<DryOutbill> queryDryOutbillWrapper);

    /**
     * 无单上下架，不存在未删除
     * @param queryNoListWrapper
     * @return
     */
    int selectNoListStatus(@Param("ew") QueryWrapper<NolistWait> queryNoListWrapper);
}
