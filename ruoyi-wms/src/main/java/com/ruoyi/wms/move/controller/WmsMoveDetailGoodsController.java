package com.ruoyi.wms.move.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

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
import com.ruoyi.wms.move.domain.WmsMoveDetailGoods;
import com.ruoyi.wms.move.service.WmsMoveDetailGoodsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 移库单详情货物Controller
 *
 * @author nf
 * @date 2023-03-01
 */
@RestController
@RequestMapping("/wms/detailGoods")
public class WmsMoveDetailGoodsController extends BaseController {

    @Autowired
    private WmsMoveDetailGoodsService wmsMoveDetailGoodsService;

    /**
     * 查询移库单详情货物列表
     */
    @PreAuthorize("@ss.hasPermi('wms:move:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsMoveDetailGoods wmsMoveDetailGoods) {
        logger.info("/wms/move/list");
        startPage();
        List<WmsMoveDetailGoods> list = wmsMoveDetailGoodsService.selectWmsMoveDetailGoodsList(wmsMoveDetailGoods);
        return getDataTable(list);
    }

    /**
     * 导出移库单详情货物列表
     */
    @PreAuthorize("@ss.hasPermi('wms:move:export')")
    @Log(title = "移库单详情货物", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmsMoveDetailGoods wmsMoveDetailGoods){
        logger.info("/wms/move/export");
        List<WmsMoveDetailGoods> list = wmsMoveDetailGoodsService.selectWmsMoveDetailGoodsList(wmsMoveDetailGoods);
        ExcelUtil<WmsMoveDetailGoods> util = new ExcelUtil<WmsMoveDetailGoods>(WmsMoveDetailGoods.class);
        util.exportExcel(response, list, "移库单详情货物数据");
    }
    /**
     * 导入移库单详情货物列表
     */
    @PreAuthorize("@ss.hasPermi('wms:move:import')")
    @Log(title = "移库单详情货物", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/wms/move/import");
        ExcelUtil<WmsMoveDetailGoods> util = new ExcelUtil<WmsMoveDetailGoods>(WmsMoveDetailGoods.class);
        List<WmsMoveDetailGoods> wmsMoveDetailGoodsList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wmsMoveDetailGoodsService.importData(wmsMoveDetailGoodsList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<WmsMoveDetailGoods> util = new ExcelUtil<WmsMoveDetailGoods>(WmsMoveDetailGoods.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取移库单详情货物详细信息
     */
    @PreAuthorize("@ss.hasPermi('wms:move:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/wms/move/getInfo/id");
        return success(wmsMoveDetailGoodsService.selectWmsMoveDetailGoodsById(id));
    }

    /**
     * 新增移库单详情货物
     */
    @PreAuthorize("@ss.hasPermi('wms:move:add')")
    @Log(title = "移库单详情货物", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WmsMoveDetailGoods wmsMoveDetailGoods){
        logger.info("/wms/move/add");
        return AjaxResult.success(wmsMoveDetailGoodsService.insertWmsMoveDetailGoods(wmsMoveDetailGoods));
    }

    /**
     * 修改移库单详情货物
     */
    @PreAuthorize("@ss.hasPermi('wms:move:edit')")
    @Log(title = "移库单详情货物", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WmsMoveDetailGoods wmsMoveDetailGoods){
        if(StringUtils.isEmpty(wmsMoveDetailGoods.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/wms/move/edit");
        return AjaxResult.success(wmsMoveDetailGoodsService.updateWmsMoveDetailGoods(wmsMoveDetailGoods));
    }

    /**
     * 删除移库单详情货物
     */
    @PreAuthorize("@ss.hasPermi('wms:move:remove')")
    @Log(title = "移库单详情货物", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/wms/move/remove/id");
        return AjaxResult.success(wmsMoveDetailGoodsService.deleteWmsMoveDetailGoodsByIds(ids));
    }

}
