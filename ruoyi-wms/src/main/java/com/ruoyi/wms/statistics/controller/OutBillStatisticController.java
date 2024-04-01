package com.ruoyi.wms.statistics.controller;

import java.util.List;

import com.ruoyi.wms.statistics.domain.OutBillStatistic;
import com.ruoyi.wms.statistics.service.OutBillStatisticService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 出库单信息Controller
 *
 * @author ruoyi
 * @date 2023-02-18
 */
@RestController
@RequestMapping("/statistic/outbillStatistic")
public class OutBillStatisticController extends BaseController {

    @Autowired
    private OutBillStatisticService outBillService;

    /**
     * 查询出库单信息列表
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody OutBillStatistic outBill) {
        logger.info("/outbound/outbill/list");
        startPage();
        List<OutBillStatistic> list = outBillService.selectOutBillList(outBill);
        return getDataTable(list);
    }

}
