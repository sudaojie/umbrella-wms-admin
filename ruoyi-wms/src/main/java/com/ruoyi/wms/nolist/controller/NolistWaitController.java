package com.ruoyi.wms.nolist.controller;

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
import com.ruoyi.wms.nolist.domain.NolistWait;
import com.ruoyi.wms.nolist.service.NolistWaitService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 无单上架待上架列Controller
 *
 * @author ruoyi
 * @date 2023-03-08
 */
@RestController
@RequestMapping("/nolist/nolistwait")
public class NolistWaitController extends BaseController {

    @Autowired
    private NolistWaitService nolistWaitService;

    /**
     * 查询无单上架待上架列列表
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody NolistWait nolistWait) {
        logger.info("/nolist/nolistwait/list");
        startPage();
        List<NolistWait> list = nolistWaitService.selectNolistWaitList(nolistWait);
        return getDataTable(list);
    }

    /**
     * 导出无单上架待上架列列表
     */
    @PreAuthorize("@ss.hasPermi('nolist:nolistwait:export')")
    @Log(title = "无单上架待上架列", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NolistWait nolistWait){
        logger.info("/nolist/nolistwait/export");
        List<NolistWait> list = nolistWaitService.selectNolistWaitList(nolistWait);
        ExcelUtil<NolistWait> util = new ExcelUtil<NolistWait>(NolistWait.class);
        util.exportExcel(response, list, "无单上架待上架列数据");
    }
    /**
     * 导入无单上架待上架列列表
     */
    @PreAuthorize("@ss.hasPermi('nolist:nolistwait:import')")
    @Log(title = "无单上架待上架列", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/nolist/nolistwait/import");
        ExcelUtil<NolistWait> util = new ExcelUtil<NolistWait>(NolistWait.class);
        List<NolistWait> nolistWaitList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = nolistWaitService.importData(nolistWaitList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<NolistWait> util = new ExcelUtil<NolistWait>(NolistWait.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取无单上架待上架列详细信息
     */
    @PreAuthorize("@ss.hasPermi('nolist:nolistwait:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/nolist/nolistwait/getInfo/id");
        return success(nolistWaitService.selectNolistWaitById(id));
    }

    /**
     * 新增无单上架待上架列
     */
    @PreAuthorize("@ss.hasPermi('nolist:nolistwait:add')")
    @Log(title = "无单上架待上架列", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody NolistWait nolistWait){
        logger.info("/nolist/nolistwait/add");
        return AjaxResult.success(nolistWaitService.insertNolistWait(nolistWait));
    }

    /**
     * 修改无单上架待上架列
     */
//    @PreAuthorize("@ss.hasPermi('nolist:nolistwait:edit')")
    @Log(title = "无单上架待上架列", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody NolistWait nolistWait){
        if(StringUtils.isEmpty(nolistWait.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/nolist/nolistwait/edit");
        return AjaxResult.success(nolistWaitService.updateNolistWait(nolistWait));
    }

    /**
     * 删除无单上架待上架列
     */
//    @PreAuthorize("@ss.hasPermi('nolist:nolistwait:remove')")
    @Log(title = "无单上架待上架列", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/nolist/nolistwait/remove/id");
        return AjaxResult.success(nolistWaitService.deleteNolistWaitByIds(ids));
    }

}
