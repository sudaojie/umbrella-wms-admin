package com.ruoyi.wms.move.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
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
import com.ruoyi.wms.move.domain.WmsMoveList;
import com.ruoyi.wms.move.service.WmsMoveListService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;


/**
 * 移库单Controller
 *
 * @author nf
 * @date 2023-03-01
 */
@RestController
@RequestMapping("/wms/move")
public class WmsMoveListController extends BaseController {

    @Autowired
    private WmsMoveListService wmsMoveListService;

    /**
     * 查询移库单列表
     */
    @PreAuthorize("@ss.hasPermi('wms:move:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsMoveList wmsMoveList) {
        logger.info("/wms/move/list");
        startPage();
        List<WmsMoveList> list = wmsMoveListService.selectWmsMoveListList(wmsMoveList);
        return getDataTable(list);
    }

    /**
     * 导出移库单列表
     */
    @PreAuthorize("@ss.hasPermi('wms:move:export')")
    @Log(title = "移库单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmsMoveList wmsMoveList){
        logger.info("/wms/move/export");
        List<WmsMoveList> list = wmsMoveListService.selectWmsMoveListList(wmsMoveList);
        ExcelUtil<WmsMoveList> util = new ExcelUtil<WmsMoveList>(WmsMoveList.class);
        util.exportExcel(response, list, "移库单数据");
    }
    /**
     * 导入移库单列表
     */
    @PreAuthorize("@ss.hasPermi('wms:move:import')")
    @Log(title = "移库单", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/wms/move/import");
        ExcelUtil<WmsMoveList> util = new ExcelUtil<WmsMoveList>(WmsMoveList.class);
        List<WmsMoveList> wmsMoveListList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wmsMoveListService.importData(wmsMoveListList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<WmsMoveList> util = new ExcelUtil<WmsMoveList>(WmsMoveList.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取移库单详细信息
     */
    @PreAuthorize("@ss.hasPermi('wms:move:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/wms/move/getInfo/id");
        return success(wmsMoveListService.selectWmsMoveListById(id));
    }

    /**
     * 新增移库单
     */
    @PreAuthorize("@ss.hasPermi('wms:move:add')")
    @Log(title = "移库单", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WmsMoveList wmsMoveList){
        logger.info("/wms/move/add");
        return AjaxResult.success(wmsMoveListService.insertWmsMoveList(wmsMoveList));
    }

    /**
     * 修改移库单
     */
    @PreAuthorize("@ss.hasPermi('wms:move:edit')")
    @Log(title = "移库单", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WmsMoveList wmsMoveList){
        logger.info("/wms/move/edit");
        return AjaxResult.success(wmsMoveListService.updateWmsMoveList(wmsMoveList));
    }
    /**
     * 开始移库单
     */
    @PreAuthorize("@ss.hasPermi('wms:move:edit')")
    @Log(title = "移库单", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateStatus")
    public AjaxResult updateStatus(@RequestBody WmsMoveList wmsMoveList){
        logger.info("/wms/move/updateStatus");
        return AjaxResult.success(wmsMoveListService.updateStatus(wmsMoveList));
    }

    /**
     * 删除移库单
     */
    @PreAuthorize("@ss.hasPermi('wms:move:remove')")
    @Log(title = "移库单", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/wms/move/remove/id");
        return AjaxResult.success(wmsMoveListService.deleteWmsMoveListByIds(ids));
    }

}
