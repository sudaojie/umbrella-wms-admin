package com.ruoyi.wms.basics.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.basics.domain.Supplier;
import com.ruoyi.wms.basics.mapper.GoodsInfoMapper;
import com.ruoyi.wms.basics.mapper.SupplierMapper;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.mapper.InbillDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 供应商基本信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@Slf4j
@Service
public class SupplierService extends ServiceImpl<SupplierMapper, Supplier> {

    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private InbillDetailMapper inbillDetailMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询供应商基本信息
     *
     * @param id 供应商基本信息主键
     * @return 供应商基本信息
     */
    public Supplier selectSupplierById(String id) {
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return supplierMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询供应商基本信息
     *
     * @param ids 供应商基本信息 IDs
     * @return 供应商基本信息
     */
    public List<Supplier> selectSupplierByIds(String[] ids) {
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return supplierMapper.selectList(queryWrapper);
    }

    /**
     * 查询供应商基本信息列表
     *
     * @param supplier 供应商基本信息
     * @return 供应商基本信息集合
     */
    public List<Supplier> selectSupplierList(Supplier supplier) {
        QueryWrapper<Supplier> queryWrapper = getQueryWrapper(supplier);
        return supplierMapper.select(queryWrapper);
    }

    /**
     * 新增供应商基本信息
     *
     * @param supplier 供应商基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Supplier insertSupplier(Supplier supplier) {
        supplier.setId(IdUtil.simpleUUID());
        supplier.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        supplierMapper.insert(supplier);
        return supplier;
    }

    /**
     * 修改供应商基本信息
     *
     * @param supplier 供应商基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Supplier updateSupplier(Supplier supplier) {
        supplierMapper.updateById(supplier);
        return supplier;
    }

    /**
     * 批量删除供应商基本信息
     *
     * @param ids 需要删除的供应商基本信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteSupplierByIds(String[] ids) {
        List<Supplier> suppliers = new ArrayList<>();
        for (String id : ids) {
            Supplier supplier = new Supplier();
            supplier.setId(id);
            supplier.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            suppliers.add(supplier);
        }
        List<Supplier> supplierList = supplierMapper.selectBatchIds(Arrays.asList(ids));
        for (Supplier supplier : supplierList) {
            int count = inbillDetailMapper.selectDataBySupplierCode(supplier.getSupplierCode());
            if (count > 0) {
                throw new ServiceException(supplier.getSupplierName() + "该数据已被引用无法删除");
            }
        }
        return AjaxResult.success(super.updateBatchById(suppliers) ? 1 : 0);
    }

    /**
     * 删除供应商基本信息信息
     *
     * @param id 供应商基本信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteSupplierById(String id) {
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return supplierMapper.updateById(supplier);
    }

    public QueryWrapper<Supplier> getQueryWrapper(Supplier supplier) {
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        if (supplier != null) {
            supplier.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", supplier.getDelFlag());
            //供应商编码
            if (StrUtil.isNotEmpty(supplier.getSupplierCode())) {
                queryWrapper.like("supplier_code", supplier.getSupplierCode());
            }
            //供应商名称
            if (StrUtil.isNotEmpty(supplier.getSupplierName())) {
                queryWrapper.like("supplier_name", supplier.getSupplierName());
            }
            //供应商联系人
            if (StrUtil.isNotEmpty(supplier.getSupplierContactUser())) {
                queryWrapper.eq("supplier_contact_user", supplier.getSupplierContactUser());
            }
            //供应商联系电话
            if (StrUtil.isNotEmpty(supplier.getSupplierPhone())) {
                queryWrapper.eq("supplier_phone", supplier.getSupplierPhone());
            }

        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param supplierList  模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName      操作人姓名
     * @return
     */
    public String importData(List<Supplier> supplierList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(supplierList) || supplierList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Supplier supplier : supplierList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                Supplier u = supplierMapper.selectDataByCode(supplier.getSupplierCode());
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, supplier);
                    supplier.setId(IdUtil.simpleUUID());
                    supplier.setCreateBy(operName);
                    supplier.setCreateTime(new Date());
                    supplier.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    supplierMapper.insert(supplier);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, supplier);
                    int count = supplierMapper.checkCode(supplier);
                    if (count > 0) {//判断是否重复
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，供应商编码重复；");
                    } else {
                        supplier.setId(u.getId());
                        supplier.setUpdateBy(operName);
                        supplier.setUpdateTime(new Date());
                        supplierMapper.updateById(supplier);
                        successNum++;
                    }
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，供应商编码重复；");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、第" + failureNum + "行数据导入失败：";
                failureMsg.append(msg + "数据类型不匹配或者长度太长");
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条。");
        }
        return successMsg.toString();
    }

    public AjaxResult checkData(Supplier supplier) {
        //验证供应商编码唯一性
        int count = supplierMapper.checkCode(supplier);
        if(count>0){
            throw new ServiceException("该供应商编码已存在，请保证供应商编码唯一");
        }
        return AjaxResult.success(true);
    }

    public List getSupplierData() {
        return supplierMapper.getSupplierData();
    }
}
