package com.ruoyi.wms.check.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wms.check.dto.CheckAdjustDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.wms.check.domain.CheckAdjust;
import com.ruoyi.wms.check.service.CheckAdjustService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 库存盘点调整单Controller
 *
 * @author nf
 * @date 2023-03-23
 */
@RestController
@RequestMapping("/wms/adjust")
public class CheckAdjustController extends BaseController {

    @Autowired
    private CheckAdjustService checkAdjustService;

    /**
     * 查询库存盘点调整单列表
     */
    @PreAuthorize("@ss.hasPermi('wms:adjust:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody CheckAdjust checkAdjust) {
        logger.info("/wms/adjust/list");
        startPage();
        List<CheckAdjust> list = checkAdjustService.selectCheckAdjustList(checkAdjust);
        return getDataTable(list);
    }

    /**
     * 获取库存盘点调整单详细信息
     */
    @PreAuthorize("@ss.hasPermi('wms:adjust:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/wms/adjust/getInfo/id");
        return success(checkAdjustService.selectCheckAdjustById(id));
    }

    /**
     * 修改库存盘点调整单（这里只有处理功能）
     */
    @PreAuthorize("@ss.hasPermi('wms:adjust:edit')")
    @Log(title = "库存盘点调整单", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody CheckAdjust checkAdjust){
        logger.info("/wms/adjust/edit");
        return AjaxResult.success(checkAdjustService.updateCheckAdjust(checkAdjust));
    }
    /**
     * 打印库存盘点调整单
     */
    @PreAuthorize("@ss.hasPermi('wms:adjust:query')")
    @Log(title = "库存盘点调整单", businessType = BusinessType.EXPORT)
    @PostMapping(value = "/downloadPDF")
    public void downloadPDF(@RequestBody CheckAdjustDto checkAdjustDto, HttpServletResponse responseBody){
        logger.info("/wms/adjust/edit");
        checkAdjustService.downloadPDF(checkAdjustDto,responseBody);
    }


}
