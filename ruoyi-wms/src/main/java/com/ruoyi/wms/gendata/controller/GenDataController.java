package com.ruoyi.wms.gendata.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.wms.gendata.dto.GenDto;
import com.ruoyi.wms.gendata.service.GenDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Administrator
 * @create 2023-06-17 17:30
 */
@RestController
@RequestMapping("/gen/data")
public class GenDataController extends BaseController {

    @Autowired
    private GenDataService genDataService;

    /**
     * 生成入库流程信息
     * @param genDto 入库参数
     * @return GoodsInfo
     */
    @PostMapping(value = "/genData")
    public AjaxResult genData(@RequestBody GenDto genDto) {
        logger.info("gen/data/genData/inBillCode");
        return success(genDataService.genData(genDto));
    }

    /**
     * 判断agv是否开启
     * @return
     */
    @GetMapping(value = "/boolAgv")
    public AjaxResult boolAgv() {
        logger.info("gen/data/genData/boolAgv");
        return success(genDataService.boolAgv());
    }

}
