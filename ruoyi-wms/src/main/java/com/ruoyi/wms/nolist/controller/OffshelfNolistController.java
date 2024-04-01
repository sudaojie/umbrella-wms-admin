package com.ruoyi.wms.nolist.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wms.outbound.dto.OutbillGoodsDto;
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
import com.ruoyi.wms.nolist.domain.OffshelfNolist;
import com.ruoyi.wms.nolist.service.OffshelfNolistService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 无单下架Controller
 *
 * @author ruoyi
 * @date 2023-03-06
 */
@RestController
@RequestMapping("/nolist/offshelfnolist")
public class OffshelfNolistController extends BaseController {

    @Autowired
    private OffshelfNolistService offshelfNolistService;

    /**
     * 查询无单下架列表
     */
    @PreAuthorize("@ss.hasPermi('nolist:offshelfnolist:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody OffshelfNolist offshelfNolist) {
        logger.info("/nolist/offshelfnolist/list");
        startPage();
        List<OffshelfNolist> list = offshelfNolistService.selectOffshelfNolistList(offshelfNolist);
        return getDataTable(list);
    }
    /**
     * 查询无单下架列表
     */
    @PreAuthorize("@ss.hasPermi('nolist:offshelfnolist:list')")
    @PostMapping("/listDetail")
    public TableDataInfo listDetail(@RequestBody OffshelfNolist offshelfNolist) {
        logger.info("/nolist/offshelfnolist/listDetail");
        startPage();
        List<OffshelfNolist> list = offshelfNolistService.listDetail(offshelfNolist);
        return getDataTable(list);
    }

    /**
     * 导出无单下架列表
     */
    @PreAuthorize("@ss.hasPermi('nolist:offshelfnolist:export')")
    @Log(title = "无单下架", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OffshelfNolist offshelfNolist){
        logger.info("/nolist/offshelfnolist/export");
        List<OffshelfNolist> list = offshelfNolistService.selectOffshelfNolistList(offshelfNolist);
        ExcelUtil<OffshelfNolist> util = new ExcelUtil<OffshelfNolist>(OffshelfNolist.class);
        util.exportExcel(response, list, "无单下架数据");
    }
    /**
     * 导入无单下架列表
     */
    @PreAuthorize("@ss.hasPermi('nolist:offshelfnolist:import')")
    @Log(title = "无单下架", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/nolist/offshelfnolist/import");
        ExcelUtil<OffshelfNolist> util = new ExcelUtil<OffshelfNolist>(OffshelfNolist.class);
        List<OffshelfNolist> offshelfNolistList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = offshelfNolistService.importData(offshelfNolistList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<OffshelfNolist> util = new ExcelUtil<OffshelfNolist>(OffshelfNolist.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取无单下架详细信息
     */
    @PreAuthorize("@ss.hasPermi('nolist:offshelfnolist:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/nolist/offshelfnolist/getInfo/id");
        return success(offshelfNolistService.selectOffshelfNolistById(id));
    }

    /**
     * 新增无单下架
     */
    @PreAuthorize("@ss.hasPermi('nolist:offshelfnolist:add')")
    @Log(title = "无单下架", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody OffshelfNolist offshelfNolist){
        logger.info("/nolist/offshelfnolist/add");
        return AjaxResult.success(offshelfNolistService.insertOffshelfNolist(offshelfNolist));
    }

    /**
     * 修改无单下架
     */
    @PreAuthorize("@ss.hasPermi('nolist:offshelfnolist:edit')")
    @Log(title = "无单下架", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody OffshelfNolist offshelfNolist){
        if(StringUtils.isEmpty(offshelfNolist.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/nolist/offshelfnolist/edit");
        return AjaxResult.success(offshelfNolistService.updateOffshelfNolist(offshelfNolist));
    }

    /**
     * 删除无单下架
     */
    @PreAuthorize("@ss.hasPermi('nolist:offshelfnolist:remove')")
    @Log(title = "无单下架", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/nolist/offshelfnolist/remove/id");
        return AjaxResult.success(offshelfNolistService.deleteOffshelfNolistByIds(ids));
    }

    /**
     * 无单下架取盘
     */
    @Log(title = "无单下架取盘", businessType = BusinessType.OTHER)
    @PostMapping(value = "/takeTray")
    public AjaxResult takeTray(@RequestBody OutbillGoodsDto map){
        logger.info("/nolist/offshelfnolist/takeTray");
        return offshelfNolistService.takeTray(map);
    }

    /**
     * 无单上架组盘
     */
    @Log(title = "无单上架组盘", businessType = BusinessType.OTHER)
    @PostMapping(value = "/groupDisk")
    public AjaxResult groupDisk(@RequestBody OutbillGoodsDto map){
        logger.info("/nolist/offshelfnolist/groupDisk");
        return offshelfNolistService.groupDisk(map);
    }

    /**
     * PDA-拉取离线数据
     */
    @GetMapping(value = "/pullData")
    public AjaxResult pullData(){
        logger.info("/nolist/offshelfnolist/pullData");
        return offshelfNolistService.pullData();
    }

}
