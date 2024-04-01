package com.ruoyi.wms.statistics.controller;

import java.util.List;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.wms.statistics.domain.InBillStatistic;
import com.ruoyi.wms.statistics.service.InBillStatisticService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;

/**
 * 入库单信息统计Controller
 *
 * @author ruoyi
 * @date 2023-02-17
 */
@RestController
@RequestMapping("/statistics/inbillStatistics")
public class InBillStatisticController extends BaseController {

    @Autowired
    private InBillStatisticService inBillStatisticService;

    /**
     * 查询入库单信息列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody InBillStatistic inBillStatistic) {
        logger.info("/warehousing/inbill/list");
        startPage();
        List<InBillStatistic> list = inBillStatisticService.selectInBillList(inBillStatistic);
        return getDataTable(list);
    }

}
