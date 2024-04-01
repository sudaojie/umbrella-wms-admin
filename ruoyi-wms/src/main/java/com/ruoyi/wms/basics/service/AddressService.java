package com.ruoyi.wms.basics.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.basics.domain.Address;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.mapper.AddressMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址基本信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@Slf4j
@Service
public class AddressService extends ServiceImpl<AddressMapper, Address> {

    @Autowired
    protected Validator validator;
    @Autowired
    private AddressMapper addressMapper;

    /**
     * 查询地址基本信息
     *
     * @param id 地址基本信息主键
     * @return 地址基本信息
     */
    public Address selectAddressById(String id) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        Address address = addressMapper.selectOne(queryWrapper);
        address.setSelected(Arrays.asList(address.getProvince().split("/")));
        return address;
    }


    /**
     * 根据ids查询地址基本信息
     *
     * @param ids 地址基本信息 IDs
     * @return 地址基本信息
     */
    public List<Address> selectAddressByIds(String[] ids) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return addressMapper.selectList(queryWrapper);
    }

    /**
     * 查询地址基本信息列表
     *
     * @param address 地址基本信息
     * @return 地址基本信息集合
     */
    public List<Address> selectAddressList(Address address) {
        QueryWrapper<Address> queryWrapper = getQueryWrapper(address);
        return addressMapper.select(queryWrapper);
    }

    /**
     * 新增地址基本信息
     *
     * @param address 地址基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Address insertAddress(Address address) {
        address.setId(IdUtil.simpleUUID());
        address.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        address.setProvince(address.getSelected().stream().collect(Collectors.joining("/")));
        addressMapper.insert(address);
        return address;
    }

    /**
     * 修改地址基本信息
     *
     * @param address 地址基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Address updateAddress(Address address) {
        if(StringUtils.isNotNull(address.getSelected())){
            address.setProvince(address.getSelected().stream().collect(Collectors.joining("/")));
        }
        addressMapper.updateById(address);
        return address;
    }

    /**
     * 批量删除地址基本信息
     *
     * @param ids 需要删除的地址基本信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteAddressByIds(String[] ids) {
        List<Address> addresss = new ArrayList<>();
        for (String id : ids) {
            Address address = new Address();
            address.setId(id);
            address.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            addresss.add(address);
        }
        return super.updateBatchById(addresss) ? 1 : 0;
    }

    /**
     * 删除地址基本信息信息
     *
     * @param id 地址基本信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteAddressById(String id) {
        Address address = new Address();
        address.setId(id);
        address.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return addressMapper.updateById(address);
    }

    public QueryWrapper<Address> getQueryWrapper(Address address) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        if (address != null) {
            address.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", address.getDelFlag());
            //收货人
            if (StrUtil.isNotEmpty(address.getReceiver())) {
                queryWrapper.and(QueryWrapper -> {
                    QueryWrapper.like("receiver", address.getReceiver()).or();
                    QueryWrapper.like("mobile_phone", address.getReceiver()).or();
                    QueryWrapper.like("company", address.getReceiver());
                });
            }
            //手机号
            if (StrUtil.isNotEmpty(address.getMobilePhone())) {
                queryWrapper.eq("mobile_phone", address.getMobilePhone());
            }
            //邮编
            if (StrUtil.isNotEmpty(address.getPostalCode())) {
                queryWrapper.eq("postal_code", address.getPostalCode());
            }
            //行政区划
            if (StrUtil.isNotEmpty(address.getProvince())) {
                queryWrapper.eq("province", address.getProvince());
            }
            //地址
            if (StrUtil.isNotEmpty(address.getAddress())) {
                queryWrapper.eq("address", address.getAddress());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param addressList   模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName      操作人姓名
     * @return
     */
    public String importData(List<Address> addressList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(addressList) || addressList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Address address : addressList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                Address u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, address);
                    address.setId(IdUtil.simpleUUID());
                    address.setCreateBy(operName);
                    address.setCreateTime(new Date());
                    address.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    addressMapper.insert(address);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, address);
                    //todo 验证
                    //int count = addressMapper.checkCode(address);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    address.setId(u.getId());
                    address.setUpdateBy(operName);
                    address.setUpdateTime(new Date());
                    addressMapper.updateById(address);
                    successNum++;
                    //}
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，数据重复；");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、第" + failureNum + "行数据导入失败：";
                failureMsg.append(msg + e.getMessage());
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

    /**
     * 获取地址信息
     *
     * @return 【{label：xx，value：xx}】
     */
    public List getAddressList() {
        return addressMapper.getAddressList();
    }
}
