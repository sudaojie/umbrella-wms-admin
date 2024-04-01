package com.ruoyi.wms.stock.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wms.stock.domain.WmsDryInbill;
import com.ruoyi.wms.stock.service.WmsDryInbillService;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;


/**
 * 晾晒入库单Controller
 *
 * @author nf
 * @date 2023-03-10
 */
@RestController
@RequestMapping("/stock/dryInbill")
public class WmsDryInbillController extends BaseController {

    @Autowired
    private WmsDryInbillService wmsDryInbillService;

    /**
     * 查询晾晒入库单列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryInbill:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsDryInbill wmsDryInbill) {
        logger.info("/stock/dryInbill/list");
        startPage();
        List<WmsDryInbill> list = wmsDryInbillService.selectWmsDryInbillList(wmsDryInbill);
        return getDataTable(list);
    }

    /**
     * 导出晾晒入库单列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryInbill:export')")
    @Log(title = "晾晒入库单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmsDryInbill wmsDryInbill){
        logger.info("/stock/dryInbill/export");
        List<WmsDryInbill> list = wmsDryInbillService.selectWmsDryInbillList(wmsDryInbill);
        ExcelUtil<WmsDryInbill> util = new ExcelUtil<WmsDryInbill>(WmsDryInbill.class);
        util.exportExcel(response, list, "晾晒入库单数据");
    }
    /**
     * 导入晾晒入库单列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryInbill:import')")
    @Log(title = "晾晒入库单", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/stock/dryInbill/import");
        ExcelUtil<WmsDryInbill> util = new ExcelUtil<WmsDryInbill>(WmsDryInbill.class);
        List<WmsDryInbill> wmsDryInbillList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wmsDryInbillService.importData(wmsDryInbillList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<WmsDryInbill> util = new ExcelUtil<WmsDryInbill>(WmsDryInbill.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取晾晒入库单详细信息
     */
    @PreAuthorize("@ss.hasPermi('stock:dryInbill:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/stock/dryInbill/getInfo/id");
        return success(wmsDryInbillService.selectWmsDryInbillById(id));
    }

    /**
     * PDA获取晾晒入库单详细信息
     */
    @PreAuthorize("@ss.hasPermi('stock:dryInbill:query')")
    @GetMapping(value = "/getDataById/{id}")
    public AjaxResult getDataById(@PathVariable("id") String id){
        logger.info("/stock/dryInbill/getDataById/id");
        return success(wmsDryInbillService.getDataById(id));
    }

    /**
     * 新增晾晒入库单
     */
    @PreAuthorize("@ss.hasPermi('stock:dryInbill:add')")
    @Log(title = "新增晾晒入库单", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WmsDryInbill wmsDryInbill){
        logger.info("/stock/dryInbill/add");
        return AjaxResult.success(wmsDryInbillService.insertWmsDryInbill(wmsDryInbill));
    }
    /**
     * 开始执行晾晒入库单
     */
    @PreAuthorize("@ss.hasPermi('stock:dryInbill:edit')")
    @Log(title = "开始执行晾晒入库单", businessType = BusinessType.INSERT)
    @PostMapping(value = "/startTask")
    public AjaxResult startTask(@RequestBody WmsDryInbill wmsDryInbill){
        logger.info("/stock/dryInbill/startTask");
        return AjaxResult.success(wmsDryInbillService.startTask(wmsDryInbill));
    }

    /**
     * 修改晾晒入库单
     */
    @PreAuthorize("@ss.hasPermi('stock:dryInbill:edit')")
    @Log(title = "修改晾晒入库单", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WmsDryInbill wmsDryInbill){

        logger.info("/stock/dryInbill/edit");
        return AjaxResult.success(wmsDryInbillService.updateWmsDryInbill(wmsDryInbill));
    }

    /**
     * 删除晾晒入库单
     */
    @PreAuthorize("@ss.hasPermi('stock:dryInbill:remove')")
    @Log(title = "晾晒入库单", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/stock/dryInbill/remove/id");
        return AjaxResult.success(wmsDryInbillService.deleteWmsDryInbillByIds(ids));
    }

    /**
     * PDA获取晾晒出库单详细信息
     */
    @GetMapping(value = "/getOutData")
    public AjaxResult getOutData(){
        logger.info("/stock/dryInbill/getOutData");
        return success(wmsDryInbillService.getOutData());
    }

}
