package com.ruoyi.wms.basics.vo;

import com.ruoyi.wcs.domain.vo.WcsStatisticsVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 首页类基本信息对象
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
@Accessors(chain = true)
public class ViewPageVo {

    /**
     * 库区类型
     */
    private String areaType;

    /**
     * 库区编码
     */
    private String areaId;

    /**
     * 库位编码
     */
    private String locationCode;

    /**
     * 货物编码
     */
    private String goodsCode;

    /**
     * 托盘编码
     */
    private String trayCode;

    /**
     * 第几层
     */
    private Integer layer;

    /**
     * 第几排
     */
    private String platoon;

    /**
     * 第几列
     */
    private String columnNum;

    /**
     * 启用状态(0.启用  1.禁用)
     */
    private String enableStatus;

    /**
     * 获取无托盘库位
     */
    private List<String> noTraylist;

    /**
     * 获取托盘无货物
     */
    private List<String> noGoodslist;

    /**
     * 获取托盘货物
     */
    private List<String> goodslist;

    /**
     * 当前同比
     */
    private String currentYearNum;

    /**
     * 当前环比
     */
    private String currentMonthNum;

    /**
     * 历史同比
     */
    private String historicalYearNum;

    /**
     * 历史环比
     */
    private String historicalMonthNum;

    /**
     * 用电量
     */
    private String powerConsumption;

    /**
     * 昨日/上月/去年用电量
     */
    private String lastPowerConsumption;

    /**
     * 比率
     */
    private String ratio;

    /**
     * 图表统计
     */
    private List<WcsStatisticsVo> list;

    /**
     * 上升或下降 标识 up down
     */
    private String flag;
}
