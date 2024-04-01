package com.ruoyi.wcs.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.WmsWcsTaskTypeEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.service.WcsOperateTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * WCS任务信息Controller
 *
 * @author yangjie
 * @date 2023-02-28
 */
@RestController
@RequestMapping("/wcs/operateTask")
public class WcsOperateTaskController extends BaseController {

    @Autowired
    private WcsOperateTaskService wcsOperateTaskService;

    /**
     * 查询WCS任务信息列表
     */
    @PreAuthorize("@ss.hasPermi('wcs:operateTask:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WcsOperateTask wcsOperateTask) {
        logger.info("/wcs/operateTask/list");
        startPage();
        List<WcsOperateTask> list = wcsOperateTaskService.selectWcsOperateTaskList(wcsOperateTask);
        return getDataTable(list);
    }


    /**
     * 获取WCS任务信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('wcs:operateTask:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/wcs/operateTask/getInfo/id");
        return success(wcsOperateTaskService.selectWcsOperateTaskById(id));
    }

    /**
     * 新增WCS任务信息
     */
    @PreAuthorize("@ss.hasPermi('wcs:operateTask:add')")
    @Log(title = "WCS任务信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WcsOperateTask wcsOperateTask) {
        logger.info("/wcs/operateTask/add");
        return AjaxResult.success(wcsOperateTaskService.insertWcsOperateTask(wcsOperateTask));
    }

    /**
     * 修改WCS任务信息
     */
    @PreAuthorize("@ss.hasPermi('wcs:operateTask:edit')")
    @Log(title = "WCS任务信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WcsOperateTask wcsOperateTask) {
        logger.info("/wcs/operateTask/edit");
        return AjaxResult.success(wcsOperateTaskService.updateWcsOperateTask(wcsOperateTask));
    }

    /**
     * 删除WCS任务信息
     */
    @PreAuthorize("@ss.hasPermi('wcs:operateTask:remove')")
    @Log(title = "WCS任务信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/wcs/operateTask/remove/id");
        return AjaxResult.success(wcsOperateTaskService.deleteWcsOperateTaskByIds(ids));
    }

    /**
     * 强制手动完成堆垛机WCS任务
     */
    @Log(title = "强制手动完成堆垛机WCS任务", businessType = BusinessType.UPDATE)
    @PostMapping("/forceCompleteStacker/{id}")
    public AjaxResult forceCompleteStacker(@PathVariable String id) {
        logger.info("/wcs/operateTask/forceCompleteStacker/{}",id);
        return AjaxResult.success(wcsOperateTaskService.forceCompleteStacker(id));
    }


    /**
     * 手动取消堆垛机WCS任务
     */
    @Log(title = "手动取消堆垛机WCS任务", businessType = BusinessType.UPDATE)
    @PostMapping("/handleCancelStacker/{id}")
    public AjaxResult handleCancelStacker(@PathVariable String id) {
        logger.info("/wcs/operateTask/handleCancelStacker/{}",id);
        return AjaxResult.success(wcsOperateTaskService.handleCancelStacker(id));
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, WcsOperateTask wcsOperateTask) {
        List<WcsOperateTask> wcsOperateTasks = wcsOperateTaskService.selectWcsOperateTaskList(wcsOperateTask);
        for (WcsOperateTask operateTask : wcsOperateTasks) {
            //设备类型
            if ("1".equals(operateTask.getTaskDeviceType())) {
                operateTask.setTaskDeviceType("AGV");
            } else {
                operateTask.setTaskDeviceType("堆垛机");
            }
            //操作类型
            if (WmsWcsTypeEnum.TAKETRAY.getCode().equals(operateTask.getOperateType())) {
                operateTask.setOperateType("取盘");
            } else if (WmsWcsTypeEnum.PUTTRAY.getCode().equals(operateTask.getOperateType())) {
                operateTask.setOperateType("回盘");
            } else {
                operateTask.setOperateType("移库");
            }
            //任务类型
            if (WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode().equals(operateTask.getTaskType())) {
                operateTask.setTaskType("正常入库");
            } else if (WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode().equals(operateTask.getTaskType())) {
                operateTask.setTaskType("正常出库");
            } else if (WmsWcsTaskTypeEnum.DRY_STORAGE.getCode().equals(operateTask.getTaskType())) {
                operateTask.setTaskType("晾晒入库");
            } else if (WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode().equals(operateTask.getTaskType())) {
                operateTask.setTaskType("晾晒出库");
            } else if (WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode().equals(operateTask.getTaskType())) {
                operateTask.setTaskType("移库");
            } else if (WmsWcsTaskTypeEnum.NO_ORDER.getCode().equals(operateTask.getTaskType())) {
                operateTask.setTaskType("无单");
            } else if (WmsWcsTaskTypeEnum.CHECK_WAREHOUSEING.getCode().equals(operateTask.getTaskType())) {
                operateTask.setTaskType("盘点入库");
            } else if (WmsWcsTaskTypeEnum.CHECK_OUTBOUND.getCode().equals(operateTask.getTaskType())) {
                operateTask.setTaskType("盘点出库");
            } else {
                operateTask.setTaskType("回库");
            }

            if ("0".equals(operateTask.getTaskStatus())) {
                operateTask.setTaskStatus("未执行");
            } else if ("1".equals(operateTask.getTaskStatus())) {
                operateTask.setTaskStatus("执行中");
            } else if ("2".equals(operateTask.getTaskStatus())) {
                operateTask.setTaskStatus("执行成功");
            } else if ("3".equals(operateTask.getTaskStatus())) {
                operateTask.setTaskStatus("执行失败");
            } else if ("4".equals(operateTask.getTaskStatus())) {
                operateTask.setTaskStatus("人工中断");
            }
        }
        ExcelUtil<WcsOperateTask> util = new ExcelUtil<WcsOperateTask>(WcsOperateTask.class);
        util.exportExcel(response, wcsOperateTasks, "定时任务");
    }

}
