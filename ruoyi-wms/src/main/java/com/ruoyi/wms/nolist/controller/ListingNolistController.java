package com.ruoyi.wms.nolist.controller;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.wms.nolist.domain.ListingNolist;
import com.ruoyi.wms.nolist.service.ListingNolistService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 无单上架Controller
 *
 * @author ruoyi
 * @date 2023-03-06
 */
@RestController
@RequestMapping("/nolist/listingnolist")
public class ListingNolistController extends BaseController {

    @Autowired
    private ListingNolistService listingNolistService;

    /**
     * 查询无单上架列表
     */
    @PreAuthorize("@ss.hasPermi('nolist:listingnolist:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody ListingNolist listingNolist) {
        logger.info("/nolist/listingnolist/list");
        startPage();
        List<ListingNolist> list = listingNolistService.selectListingNolistList(listingNolist);
        return getDataTable(list);
    }
    /**
     * 查询无单上架详情
     */
    @PreAuthorize("@ss.hasPermi('nolist:listingnolist:list')")
    @PostMapping("/listDetail")
    public TableDataInfo listDetail(@RequestBody ListingNolist listingNolist) {
        logger.info("/nolist/listingnolist/listDetail");
        startPage();
        List<ListingNolist> list = listingNolistService.listDetail(listingNolist);
        return getDataTable(list);
    }

    /**
     * 导出无单上架列表
     */
    @PreAuthorize("@ss.hasPermi('nolist:listingnolist:export')")
    @Log(title = "无单上架", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListingNolist listingNolist){
        logger.info("/nolist/listingnolist/export");
        List<ListingNolist> list = listingNolistService.selectListingNolistList(listingNolist);
        ExcelUtil<ListingNolist> util = new ExcelUtil<ListingNolist>(ListingNolist.class);
        util.exportExcel(response, list, "无单上架数据");
    }
    /**
     * 导入无单上架列表
     */
    @PreAuthorize("@ss.hasPermi('nolist:listingnolist:import')")
    @Log(title = "无单上架", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/nolist/listingnolist/import");
        ExcelUtil<ListingNolist> util = new ExcelUtil<ListingNolist>(ListingNolist.class);
        List<ListingNolist> listingNolistList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = listingNolistService.importData(listingNolistList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<ListingNolist> util = new ExcelUtil<ListingNolist>(ListingNolist.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取无单上架详细信息
     */
    @PreAuthorize("@ss.hasPermi('nolist:listingnolist:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/nolist/listingnolist/getInfo/id");
        return success(listingNolistService.selectListingNolistById(id));
    }

    /**
     * 新增无单上架
     */
    @PreAuthorize("@ss.hasPermi('nolist:listingnolist:add')")
    @Log(title = "无单上架", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody ListingNolist listingNolist){
        logger.info("/nolist/listingnolist/add");
        return AjaxResult.success(listingNolistService.insertListingNolist(listingNolist));
    }

    /**
     * 修改无单上架
     */
    @PreAuthorize("@ss.hasPermi('nolist:listingnolist:edit')")
    @Log(title = "无单上架", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody ListingNolist listingNolist){
        if(StringUtils.isEmpty(listingNolist.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/nolist/listingnolist/edit");
        return AjaxResult.success(listingNolistService.updateListingNolist(listingNolist));
    }

    /**
     * 删除无单上架
     */
    @PreAuthorize("@ss.hasPermi('nolist:listingnolist:remove')")
    @Log(title = "无单上架", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/nolist/listingnolist/remove/id");
        return AjaxResult.success(listingNolistService.deleteListingNolistByIds(ids));
    }



}
