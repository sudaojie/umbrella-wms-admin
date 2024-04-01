package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.Address;
import com.ruoyi.wms.basics.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 地址基本信息Controller
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@RestController
@RequestMapping("/basics/address")
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;

    /**
     * 查询地址基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:address:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Address address) {
        logger.info("/basics/address/list");
        startPage();
        List<Address> list = addressService.selectAddressList(address);
        return getDataTable(list);
    }

    /**
     * 导出地址基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:address:export')")
    @Log(title = "地址基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Address address) {
        logger.info("/basics/address/export");
        List<Address> list = addressService.selectAddressList(address);
        ExcelUtil<Address> util = new ExcelUtil<Address>(Address.class);
        util.exportExcel(response, list, "地址基本信息数据");
    }

    /**
     * 导入地址基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:address:import')")
    @Log(title = "地址基本信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/basics/address/import");
        ExcelUtil<Address> util = new ExcelUtil<Address>(Address.class);
        List<Address> addressList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = addressService.importData(addressList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Address> util = new ExcelUtil<Address>(Address.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取地址基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:address:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/address/getInfo/id");
        return success(addressService.selectAddressById(id));
    }

    /**
     * 新增地址基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:address:add')")
    @Log(title = "地址基本信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Address address) {
        logger.info("/basics/address/add");
        return AjaxResult.success(addressService.insertAddress(address));
    }

    /**
     * 修改地址基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:address:edit')")
    @Log(title = "地址基本信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Address address) {
        if(StringUtils.isEmpty(address.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/basics/address/edit");
        return AjaxResult.success(addressService.updateAddress(address));
    }

    /**
     * 删除地址基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:address:remove')")
    @Log(title = "地址基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/address/remove/id");
        return AjaxResult.success(addressService.deleteAddressByIds(ids));
    }
    /**
     * 获取全部地址
     */
    @Log(title = "地址基本信息", businessType = BusinessType.DELETE)
    @PostMapping("/getAddressList")
    public AjaxResult getAddressList() {
        logger.info("/basics/address/getAddressList");
        return AjaxResult.success(addressService.getAddressList());
    }

}
