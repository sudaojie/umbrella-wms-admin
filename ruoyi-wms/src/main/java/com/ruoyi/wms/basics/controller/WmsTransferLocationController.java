package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.WmsTransferLocation;
import com.ruoyi.wms.basics.service.WmsTransferLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 传输带库位信息Controller
 *
 * @author ruoyi
 * @date 2023-06-30
 */
@RestController
@RequestMapping("/basics/transfer")
public class WmsTransferLocationController extends BaseController {

    @Autowired
    private WmsTransferLocationService wmsTransferLocationService;

    /**
     * 查询传输带库位信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:transferLocation:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsTransferLocation wmsTransferLocation) {
        logger.info("/basics/transfer/list");
        startPage();
        List<WmsTransferLocation> list = wmsTransferLocationService.selectWmsTransferLocationList(wmsTransferLocation);
        return getDataTable(list);
    }

    /**
     * 导出传输带库位信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:transferLocation:export')")
    @Log(title = "传输带库位信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmsTransferLocation wmsTransferLocation){
        logger.info("/basics/transfer/export");
        List<WmsTransferLocation> list = wmsTransferLocationService.selectWmsTransferLocationList(wmsTransferLocation);
        ExcelUtil<WmsTransferLocation> util = new ExcelUtil<WmsTransferLocation>(WmsTransferLocation.class);
        util.exportExcel(response, list, "传输带库位信息数据");
    }
    /**
     * 导入传输带库位信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:transferLocation:import')")
    @Log(title = "传输带库位信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/basics/transfer/import");
        ExcelUtil<WmsTransferLocation> util = new ExcelUtil<WmsTransferLocation>(WmsTransferLocation.class);
        List<WmsTransferLocation> wmsTransferLocationList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wmsTransferLocationService.importData(wmsTransferLocationList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<WmsTransferLocation> util = new ExcelUtil<WmsTransferLocation>(WmsTransferLocation.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取传输带库位信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:transferLocation:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/basics/transfer/getInfo/id");
        return success(wmsTransferLocationService.selectWmsTransferLocationById(id));
    }

    /**
     * 新增传输带库位信息
     */
    @PreAuthorize("@ss.hasPermi('basics:transferLocation:add')")
    @Log(title = "传输带库位信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WmsTransferLocation wmsTransferLocation){
        logger.info("/basics/transfer/add");
        return AjaxResult.success(wmsTransferLocationService.insertWmsTransferLocation(wmsTransferLocation));
    }

    /**
     * 修改传输带库位信息
     */
    @PreAuthorize("@ss.hasPermi('basics:transferLocation:edit')")
    @Log(title = "传输带库位信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WmsTransferLocation wmsTransferLocation){
        logger.info("/basics/transfer/edit");
        return AjaxResult.success(wmsTransferLocationService.updateWmsTransferLocation(wmsTransferLocation));
    }

    /**
     * 删除传输带库位信息
     */
    @PreAuthorize("@ss.hasPermi('basics:transfer:remove')")
    @Log(title = "传输带库位信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/basics/transfer/remove/id");
        return AjaxResult.success(wmsTransferLocationService.deleteWmsTransferLocationByIds(ids));
    }

    /**
     * 获取存储库区编码
     *
     */
    @PreAuthorize("@ss.hasPermi('basics:transferLocation:list')")
    @GetMapping("/getAreaCcq")
    public AjaxResult getAreaCcq(){
        logger.info("/basics/transfer/getAreaCcq");
        return AjaxResult.success(wmsTransferLocationService.getAreaCcq());
    }

}
