package com.ruoyi.wms.basics.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.WmsTransferLocation;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.WmsTransferLocationMapper;
import com.ruoyi.wms.enums.AreaTypeEnum;
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
 * 传输带库位信息Service接口
 *
 * @author ruoyi
 * @date 2023-06-30
 */
@Slf4j
@Service
public class WmsTransferLocationService extends ServiceImpl<WmsTransferLocationMapper, WmsTransferLocation> {

    @Autowired
    private WmsTransferLocationMapper wmsTransferLocationMapper;
    @Autowired
    protected Validator validator;

    @Autowired
    private AreaMapper areaMapper;
    /**
     * 查询传输带库位信息
     *
     * @param id 传输带库位信息主键
     * @return 传输带库位信息
     */
    public WmsTransferLocation selectWmsTransferLocationById(String id){
        QueryWrapper<WmsTransferLocation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsTransferLocationMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询传输带库位信息
     *
     * @param ids 传输带库位信息 IDs
     * @return 传输带库位信息
     */
    public List<WmsTransferLocation> selectWmsTransferLocationByIds(String[] ids) {
        QueryWrapper<WmsTransferLocation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsTransferLocationMapper.selectList(queryWrapper);
    }

    /**
     * 查询传输带库位信息列表
     *
     * @param wmsTransferLocation 传输带库位信息
     * @return 传输带库位信息集合
     */
    public List<WmsTransferLocation> selectWmsTransferLocationList(WmsTransferLocation wmsTransferLocation){
        QueryWrapper<WmsTransferLocation> queryWrapper = getQueryWrapper(wmsTransferLocation);
        return wmsTransferLocationMapper.select(queryWrapper);
    }

    /**
     * 新增传输带库位信息
     *
     * @param wmsTransferLocation 传输带库位信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsTransferLocation insertWmsTransferLocation(WmsTransferLocation wmsTransferLocation){
        wmsTransferLocation.setId(IdUtil.simpleUUID());
        wmsTransferLocation.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsTransferLocationMapper.insert(wmsTransferLocation);
        return wmsTransferLocation;
    }

    /**
     * 修改传输带库位信息
     *
     * @param wmsTransferLocation 传输带库位信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsTransferLocation updateWmsTransferLocation(WmsTransferLocation wmsTransferLocation){
        if(StringUtils.isEmpty(wmsTransferLocation.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        wmsTransferLocationMapper.updateById(wmsTransferLocation);
        return wmsTransferLocation;
    }

    /**
     * 批量删除传输带库位信息
     *
     * @param ids 需要删除的传输带库位信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsTransferLocationByIds(String[] ids){
        List<WmsTransferLocation> wmsTransferLocations = new ArrayList<>();
        for (String id : ids) {
            WmsTransferLocation wmsTransferLocation = new WmsTransferLocation();
            wmsTransferLocation.setId(id);
            wmsTransferLocation.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsTransferLocations.add(wmsTransferLocation);
        }
        return super.updateBatchById(wmsTransferLocations) ? 1 : 0;
    }

    /**
     * 删除传输带库位信息信息
     *
     * @param id 传输带库位信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsTransferLocationById(String id){
        WmsTransferLocation wmsTransferLocation = new WmsTransferLocation();
        wmsTransferLocation.setId(id);
        wmsTransferLocation.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsTransferLocationMapper.updateById(wmsTransferLocation);
    }

    public QueryWrapper<WmsTransferLocation> getQueryWrapper(WmsTransferLocation wmsTransferLocation) {
        QueryWrapper<WmsTransferLocation> queryWrapper = new QueryWrapper<>();
        if (wmsTransferLocation != null) {
            wmsTransferLocation.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsTransferLocation.getDelFlag());
            //传输带库位编码
            if (StrUtil.isNotEmpty(wmsTransferLocation.getTranferLocationCode())) {
                queryWrapper.like("tranfer_location_code",wmsTransferLocation.getTranferLocationCode());
            }
            //传输带所属库区
            if (StrUtil.isNotEmpty(wmsTransferLocation.getAreaCode())) {
                queryWrapper.eq("area_code",wmsTransferLocation.getAreaCode());
            }
            //传输带朝向(1.左侧  2.右侧)
            if (StrUtil.isNotEmpty(wmsTransferLocation.getTranferLocationArrow())) {
                queryWrapper.eq("tranfer_location_arrow",wmsTransferLocation.getTranferLocationArrow());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param wmsTransferLocationList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<WmsTransferLocation> wmsTransferLocationList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsTransferLocationList) || wmsTransferLocationList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WmsTransferLocation wmsTransferLocation : wmsTransferLocationList) {
            if(null==wmsTransferLocation){
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                WmsTransferLocation u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsTransferLocation);
                    wmsTransferLocation.setId(IdUtil.simpleUUID());
                    wmsTransferLocation.setCreateBy(operName);
                    wmsTransferLocation.setCreateTime(new Date());
                    wmsTransferLocation.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsTransferLocationMapper.insert(wmsTransferLocation);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsTransferLocation);
                    //todo 验证
                    //int count = wmsTransferLocationMapper.checkCode(wmsTransferLocation);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        wmsTransferLocation.setId(u.getId());
                        wmsTransferLocation.setUpdateBy(operName);
                        wmsTransferLocation.setUpdateTime(new Date());
                        wmsTransferLocationMapper.updateById(wmsTransferLocation);
                        successNum++;
                    //}
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、第"+failureNum+"行数据导入失败：";
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
     * 获取存储库区编码
     *
     */
    public List<Area> getAreaCcq(){
        LambdaQueryWrapper<Area> areaWrapper = new LambdaQueryWrapper<>();
        areaWrapper.select(Area::getAreaCode,Area::getAreaName);
        areaWrapper.eq(Area::getAreaType, AreaTypeEnum.CCQ.getCode());
        areaWrapper.eq(Area::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        return areaMapper.selectList(areaWrapper);
    }

    /**
     * 根据库区获取传输带库位编码
     * @param areaCode
     * @param locationArrowCode
     * @return
     */
    public String getTransferLocationCodeByArea(String areaCode,String locationArrowCode){
        LambdaQueryWrapper<WmsTransferLocation> wmsTransferLocationQueryWrapper = new LambdaQueryWrapper<>();
        wmsTransferLocationQueryWrapper.eq(WmsTransferLocation::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        wmsTransferLocationQueryWrapper.eq(WmsTransferLocation::getAreaCode,areaCode);
        wmsTransferLocationQueryWrapper.eq(WmsTransferLocation::getTranferLocationArrow,locationArrowCode);
        String tranferLocationCode = getBaseMapper().selectOne(wmsTransferLocationQueryWrapper).getTranferLocationCode();
        return tranferLocationCode;
    }


    /**
     * 根据库位编码获取库区编码
     * @param locationCode
     * @return
     */
    public String getAreaCodeByTransferLocationCode(String locationCode){
        LambdaQueryWrapper<WmsTransferLocation> wmsTransferLocationQueryWrapper = new LambdaQueryWrapper<>();
        wmsTransferLocationQueryWrapper.eq(WmsTransferLocation::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        wmsTransferLocationQueryWrapper.eq(WmsTransferLocation::getTranferLocationCode,locationCode);
        String areaCode = getBaseMapper().selectOne(wmsTransferLocationQueryWrapper).getAreaCode();
        return areaCode;
    }
}
