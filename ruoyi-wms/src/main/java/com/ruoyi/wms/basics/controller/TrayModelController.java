package com.ruoyi.wms.basics.controller;

import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.TrayModel;
import com.ruoyi.wms.basics.service.TrayModelService;
import com.ruoyi.wms.utils.FormatDataUtils;
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
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 托盘规格Controller
 *
 * @author ruoyi
 * @date 2023-02-21
 */
@RestController
@RequestMapping("/basics/model")
public class TrayModelController extends BaseController {

    @Autowired
    private TrayModelService sysTrayModelService;

    //容量数据转换
    DecimalFormat df = new DecimalFormat("0.000000");

    /**
     * 查询托盘规格列表
     */
    @PreAuthorize("@ss.hasPermi('basics:model:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody TrayModel sysTrayModel) {
        logger.info("/basics/model/list");
        startPage();
        List<TrayModel> list = sysTrayModelService.selectSysTrayModelList(sysTrayModel);
        return getDataTable(list);
    }

    /**
     * 导出托盘规格列表
     */
    @PreAuthorize("@ss.hasPermi('basics:model:export')")
    @Log(title = "托盘规格", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TrayModel sysTrayModel){
        logger.info("/basics/model/export");
        List<TrayModel> list = sysTrayModelService.selectSysTrayModelList(sysTrayModel);
        ExcelUtil<TrayModel> util = new ExcelUtil<TrayModel>(TrayModel.class);
        util.exportExcel(response, list, "托盘规格数据");
    }
    /**
     * 导入托盘规格列表
     */
    @PreAuthorize("@ss.hasPermi('basics:model:import')")
    @Log(title = "托盘规格", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/basics/model/import");

        ExcelUtil<TrayModel> util = new ExcelUtil<TrayModel>(TrayModel.class);
        List<TrayModel> sysTrayModelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        for (TrayModel model:sysTrayModelList){
            df.setRoundingMode(RoundingMode.HALF_UP);
            //计算容量值保留两位小数
            String value= FormatDataUtils.getRateStr(df.format(model.getModelLength().multiply(model.getModelWidth()).multiply(model.getModelHeight()).divide(new BigDecimal("1000000"))));
            model.setModelVolume(new BigDecimal(value));
            List<TrayModel> trayModel = sysTrayModelService.checkData(model);
            if(trayModel.size()>0){
                throw new RuntimeException("规格编号重复");
            }
        }
        String message = sysTrayModelService.importData(sysTrayModelList, updateSupport, operName);
        return AjaxResult.success(message);
    }



    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<TrayModel> util = new ExcelUtil<TrayModel>(TrayModel.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取托盘规格详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:model:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/basics/model/getInfo/id");
        return success(sysTrayModelService.selectSysTrayModelById(id));
    }

    /**
     * 新增托盘规格
     */
    @PreAuthorize("@ss.hasPermi('basics:model:add')")
    @Log(title = "托盘规格", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody TrayModel sysTrayModel){
        logger.info("/basics/model/add");
        List<TrayModel> trayModel = sysTrayModelService.selectSysTrayModelByCode(sysTrayModel);
        if(trayModel.size()>0){
            throw new RuntimeException("规格编号重复");
        }else {
            return AjaxResult.success(sysTrayModelService.insertSysTrayModel(sysTrayModel));
        }
    }

    /**
     * 修改托盘规格
     */
    @PreAuthorize("@ss.hasPermi('basics:model:edit')")
    @Log(title = "托盘规格", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody TrayModel sysTrayModel){
        logger.info("/basics/model/edit");
        List<TrayModel> trayModel = sysTrayModelService.checkData(sysTrayModel);
        if(trayModel.size()>0){
            throw new RuntimeException("规格编号重复");
        }else {
            return AjaxResult.success(sysTrayModelService.updateSysTrayModel(sysTrayModel));
        }
    }

    /**
     * 删除托盘规格
     */
    @PreAuthorize("@ss.hasPermi('basics:model:remove')")
    @Log(title = "托盘规格", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/basics/model/remove/id");
        return AjaxResult.success(sysTrayModelService.deleteSysTrayModelByIds(ids));
    }

    /**
     * 获取托盘规格信息
     */
    @PreAuthorize("@ss.hasPermi('basics:model:query')")
    @PostMapping("/getTrayModelData")
    public AjaxResult getTrayModelData() {
        logger.info("/basics/model/getTrayModelData");
        startPage();
        return AjaxResult.success( sysTrayModelService.getTrayModelData());
    }

}
