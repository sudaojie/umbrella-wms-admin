package com.ruoyi.wms.stock.controller;

import java.util.List;
import com.ruoyi.wms.stock.domain.WmsAccount;
import com.ruoyi.wms.stock.service.WmsAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 库存台账Controller
 *
 * @author ruoyi
 * @date 2023-03-15
 */
@RestController
@RequestMapping("/stock/account")
public class WmsAccountController extends BaseController {

    @Autowired
    private WmsAccountService wmsAccountService;

    /**
     * 查询库存台账列表
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsAccount wmsWarehouseAccount) {
        logger.info("/system/account/list");
        startPage();
        List<WmsAccount> list = wmsAccountService.selectWmsWarehouseAccountList(wmsWarehouseAccount);
        return getDataTable(list);
    }

}
