package com.ruoyi.wms.move.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wms.move.vo.WmsMoveDetailVo;
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
import com.ruoyi.wms.move.domain.WmsMoveDetail;
import com.ruoyi.wms.move.service.WmsMoveDetailService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 移库单详情Controller
 *
 * @author nf
 * @date 2023-03-01
 */
@RestController
@RequestMapping("/wms/detail")
public class WmsMoveDetailController extends BaseController {

    @Autowired
    private WmsMoveDetailService wmsMoveDetailService;

    /**
     * 查询移库单详情列表
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsMoveDetail wmsMoveDetail) {
        logger.info("/wms/detail/list");
        startPage();
        List<WmsMoveDetail> list = wmsMoveDetailService.selectWmsMoveDetailList(wmsMoveDetail);
        return getDataTable(list);
    }

    /**
     * 导出移库单详情列表
     */
    @PreAuthorize("@ss.hasPermi('wms:detail:export')")
    @Log(title = "移库单详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmsMoveDetail wmsMoveDetail) {
        logger.info("/wms/detail/export");
        List<WmsMoveDetail> list = wmsMoveDetailService.selectWmsMoveDetailList(wmsMoveDetail);
        ExcelUtil<WmsMoveDetail> util = new ExcelUtil<WmsMoveDetail>(WmsMoveDetail.class);
        util.exportExcel(response, list, "移库单详情数据");
    }

    /**
     * 导入移库单详情列表
     */
    @PreAuthorize("@ss.hasPermi('wms:detail:import')")
    @Log(title = "移库单详情", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/wms/detail/import");
        ExcelUtil<WmsMoveDetail> util = new ExcelUtil<WmsMoveDetail>(WmsMoveDetail.class);
        List<WmsMoveDetail> wmsMoveDetailList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wmsMoveDetailService.importData(wmsMoveDetailList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<WmsMoveDetail> util = new ExcelUtil<WmsMoveDetail>(WmsMoveDetail.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取移库单详情详细信息
     */
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/wms/detail/getInfo/id");
        return success(wmsMoveDetailService.selectWmsMoveDetailById(id));
    }

    /**
     * 新增移库单详情
     */
    @Log(title = "移库单详情", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WmsMoveDetail wmsMoveDetail) {
        logger.info("/wms/detail/add");
        return AjaxResult.success(wmsMoveDetailService.insertWmsMoveDetail(wmsMoveDetail));
    }

    /**
     * 修改移库单详情
     */
    @Log(title = "移库单详情", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WmsMoveDetail wmsMoveDetail) {
        if (StringUtils.isEmpty(wmsMoveDetail.getId())) {
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/wms/detail/edit");
        return AjaxResult.success(wmsMoveDetailService.updateWmsMoveDetail(wmsMoveDetail));
    }

    /**
     * 删除移库单详情
     */
    @Log(title = "移库单详情", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/wms/detail/remove/id");
        return AjaxResult.success(wmsMoveDetailService.deleteWmsMoveDetailByIds(ids));
    }

    /**
     * PDA-获取移库任务
     */
    @PostMapping("/selectMoveTask")
    public TableDataInfo selectMoveTask(@RequestBody WmsMoveDetail wmsMoveDetail) {
        logger.info("/wms/detail/selectMoveTask");
        startPage();
        List<WmsMoveDetail> list = wmsMoveDetailService.selectMoveTask(wmsMoveDetail);
        return getDataTable(list);
    }

    /**
     * pda-人工移库回盘
     */
    @Log(title = "pda-人工移库回盘", businessType = BusinessType.OTHER)
    @PostMapping(value = "/manMadeMove")
    public AjaxResult manMadeMove(@RequestBody WmsMoveDetailVo map) {
        logger.info("/wms/detail/manMadeMove");
        return wmsMoveDetailService.manMadeMove(map);
    }

    /**
     * 开始移库任务
     */
    @Log(title = "开始移库任务", businessType = BusinessType.OTHER)
    @PostMapping(value = "/startMoveTake")
    public AjaxResult startMoveTake(@RequestBody WmsMoveDetailVo map) {
        logger.info("/wms/detail/startMoveTake");
        return wmsMoveDetailService.startMoveTake(map);
    }


}
