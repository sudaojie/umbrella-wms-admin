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
import com.ruoyi.file.service.UploadService;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.domain.vo.FormalFileVo;
import com.ruoyi.wms.basics.domain.Temp;
import com.ruoyi.wms.basics.mapper.TempMapper;
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
 * 模板配置Service接口
 *
 * @author ruoyi
 * @date 2023-01-09
 */
@Slf4j
@Service
public class TempService extends ServiceImpl<TempMapper, Temp> {

    @Autowired
    private TempMapper wmsWarehouseTempMapper;
    @Autowired
    private UploadService uploadService;
    @Autowired
    protected Validator validator;
    /**
     * 查询模板配置
     *
     * @param id 模板配置主键
     * @return 模板配置
     */
    public Temp selectWmsWarehouseTempById(String id) {
        QueryWrapper<Temp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsWarehouseTempMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询模板配置
     *
     * @param ids 模板配置 IDs
     * @return 模板配置
     */
    public List<Temp> selectWmsWarehouseTempByIds(String[] ids) {
        QueryWrapper<Temp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsWarehouseTempMapper.selectList(queryWrapper);
    }

    /**
     * 查询模板配置列表
     *
     * @param wmsWarehouseTemp 模板配置
     * @return 模板配置集合
     */
    public List<Temp> selectWmsWarehouseTempList(Temp wmsWarehouseTemp) {
        QueryWrapper<Temp> queryWrapper = getQueryWrapper(wmsWarehouseTemp);
        return wmsWarehouseTempMapper.select(queryWrapper);
    }

    /**
     * 新增模板配置
     *
     * @param wmsWarehouseTemp 模板配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Temp insertWmsWarehouseTemp(Temp wmsWarehouseTemp) {

        wmsWarehouseTemp.setId(IdUtil.simpleUUID());
        wmsWarehouseTemp.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        FormalFileVo formalFileVo = new FormalFileVo();
        formalFileVo.setBuniessId(wmsWarehouseTemp.getId());
        formalFileVo.setTempIds(wmsWarehouseTemp.getFileKey());
        List<SysFile> sysFiles = uploadService.convertFromSysFileTemp(formalFileVo);
        SysFile sysFile = sysFiles.get(0);
        wmsWarehouseTemp.setFileName(sysFile.getDisplayName());
        wmsWarehouseTempMapper.insert(wmsWarehouseTemp);
        return wmsWarehouseTemp;
    }

    /**
     * 修改模板配置
     *
     * @param wmsWarehouseTemp 模板配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Temp updateWmsWarehouseTemp(Temp wmsWarehouseTemp) {
        FormalFileVo formalFileVo = new FormalFileVo();
        formalFileVo.setBuniessId(wmsWarehouseTemp.getId());
        formalFileVo.setTempIds(wmsWarehouseTemp.getFileKey());
        List<SysFile> sysFiles = uploadService.convertFromSysFileTemp(formalFileVo);
        if (sysFiles.size() > 0) {
            SysFile sysFile = sysFiles.get(0);
            wmsWarehouseTemp.setFileName(sysFile.getDisplayName());
        }
        wmsWarehouseTempMapper.updateById(wmsWarehouseTemp);
        return wmsWarehouseTemp;
    }

    /**
     * 批量删除模板配置
     *
     * @param ids 需要删除的模板配置主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWarehouseTempByIds(String[] ids) {
        List<Temp> wmsWarehouseTemps = new ArrayList<>();
        for (String id : ids) {
            Temp wmsWarehouseTemp = new Temp();
            wmsWarehouseTemp.setId(id);
            wmsWarehouseTemp.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsWarehouseTemps.add(wmsWarehouseTemp);
        }
        int i = super.updateBatchById(wmsWarehouseTemps) ? 1 : 0;
        if (i == 1) {
            for (String id : ids) {
                uploadService.deleteLocalFile(id);
            }
        }
        return i;
    }

    /**
     * 验证模板编码唯一性
     *
     * @param wmsWarehouseTemp 验证对象
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult checkTempId(Temp wmsWarehouseTemp) {
        //验证模板编码唯一性
        int count = wmsWarehouseTempMapper.checkTempId(wmsWarehouseTemp);
        if(count>0){
            throw new ServiceException("该模板编码已存在，请保证模板编码唯一");
        }
        return AjaxResult.success(true);
    }

    /**
     * 删除模板配置信息
     *
     * @param id 模板配置主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWarehouseTempById(String id) {
        Temp wmsWarehouseTemp = new Temp();
        wmsWarehouseTemp.setId(id);
        wmsWarehouseTemp.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsWarehouseTempMapper.updateById(wmsWarehouseTemp);
    }

    public QueryWrapper<Temp> getQueryWrapper(Temp wmsWarehouseTemp) {
        QueryWrapper<Temp> queryWrapper = new QueryWrapper<>();
        if (wmsWarehouseTemp != null) {
            wmsWarehouseTemp.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", wmsWarehouseTemp.getDelFlag());
            //模板ID
            if (StrUtil.isNotEmpty(wmsWarehouseTemp.getTempId())) {
                queryWrapper.eq("temp_id", wmsWarehouseTemp.getTempId());
            }
            //模板名称
            if (StrUtil.isNotEmpty(wmsWarehouseTemp.getTempName())) {
                queryWrapper.like("temp_name", wmsWarehouseTemp.getTempName());
            }
            //所属模块
            if (StrUtil.isNotEmpty(wmsWarehouseTemp.getModule())) {
                queryWrapper.like("module", wmsWarehouseTemp.getModule());
            }
            //文件名称
            if (StrUtil.isNotEmpty(wmsWarehouseTemp.getFileName())) {
                queryWrapper.like("file_name", wmsWarehouseTemp.getFileName());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param tempList
     * @param updateSupport
     * @param operName
     * @return
     */
    public String importUser(List<Temp> tempList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(tempList) || tempList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Temp temp : tempList) {
            try {
                // 验证是否存在这个模板
                Temp u = wmsWarehouseTempMapper.selectTempByTempId(temp.getTempId());
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, temp);
                    temp.setId(IdUtil.simpleUUID());
                    temp.setCreateBy(operName);
                    temp.setCreateTime(new Date());
                    temp.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsWarehouseTempMapper.insert(temp);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、模板 " + temp.getTempName() + " 导入成功");
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, temp);
                    temp.setId(u.getId());
                    int count = wmsWarehouseTempMapper.checkTempId(temp);
                    if (count > 0) {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、模板 " + temp.getTempName() + "的编码 已存在");
                    } else {
                        temp.setUpdateBy(operName);
                        wmsWarehouseTempMapper.updateById(temp);
                        successNum++;
                        successMsg.append("<br/>" + successNum + "、模板 " + temp.getTempName() + " 更新成功");
                    }
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、模板 " + temp.getTempName() + "的编码 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、模板 " + temp.getTempName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    public Temp selectWmsWarehouseTempByTempId(String tempId) {
        Temp temp = wmsWarehouseTempMapper.selectTempByTempId(tempId);
        return temp;
    }
}
