package com.ruoyi.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.system.domain.SysFileTemp;
import com.ruoyi.system.mapper.SysFileTempMapper;
import com.ruoyi.system.service.ISysFileTempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 系统临时附件Service接口
 *
 * @author yangjie
 * @date 2022-10-28
 */
@Slf4j
@Service
public class SysFileTempServiceImpl extends ServiceImpl<SysFileTempMapper, SysFileTemp> implements ISysFileTempService {

    @Autowired
    private SysFileTempMapper sysFileTempMapper;

    /**
     * 查询系统临时附件
     *
     * @param id 系统临时附件主键
     * @return 系统临时附件
     */
    @Override
    public SysFileTemp selectSysFileTempById(String id) {
        QueryWrapper<SysFileTemp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return sysFileTempMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询系统临时附件
     *
     * @param ids 系统临时附件 IDs
     * @return 系统临时附件
     */
    @Override
    public List<SysFileTemp> selectSysFileTempByIds(String[] ids) {
        QueryWrapper<SysFileTemp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return sysFileTempMapper.selectList(queryWrapper);
    }

    /**
     * 查询系统临时附件列表
     *
     * @param sysFileTemp 系统临时附件
     * @return 系统临时附件集合
     */
    @Override
    public List<SysFileTemp> selectSysFileTempList(SysFileTemp sysFileTemp) {
        sysFileTemp.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        return sysFileTempMapper.selectSysFileTempList(sysFileTemp);
    }

    /**
     * 新增系统临时附件
     *
     * @param sysFileTemp 系统临时附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysFileTemp(SysFileTemp sysFileTemp) {
        sysFileTemp.setId(IdUtil.simpleUUID());
        sysFileTemp.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        return sysFileTempMapper.insert(sysFileTemp);
    }

    /**
     * 修改系统临时附件
     *
     * @param sysFileTemp 系统临时附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysFileTemp(SysFileTemp sysFileTemp) {
        return sysFileTempMapper.updateById(sysFileTemp);
    }

    /**
     * 批量删除系统临时附件
     *
     * @param ids 需要删除的系统临时附件主键集合
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysFileTempByIds(String[] ids) {
        List<SysFileTemp> sysFileTemps = new ArrayList<>();
        for (String id : ids) {
            SysFileTemp sysFileTemp = new SysFileTemp();
            sysFileTemp.setId(id);
            sysFileTemp.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            sysFileTemps.add(sysFileTemp);
        }
        return super.updateBatchById(sysFileTemps) ? 1 : 0;
    }

    /**
     * 删除系统临时附件信息
     *
     * @param id 系统临时附件主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysFileTempById(String id) {
        SysFileTemp sysFileTemp = new SysFileTemp();
        sysFileTemp.setId(id);
        sysFileTemp.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return sysFileTempMapper.updateById(sysFileTemp);
    }

    /**
     * 清理临时表数据及临时文件
     *
     * @param sysFileTempList
     */
    @Override
    public void cleanTempDataAndTempFiles(List<SysFileTemp> sysFileTempList) {
        if (CollUtil.isNotEmpty(sysFileTempList)) {
            List<String> idList = new ArrayList<>();
            for (SysFileTemp sysFileTemp : sysFileTempList) {
                log.debug("删除临时文件：" + sysFileTemp.getPath());
                cn.hutool.core.io.FileUtil.del(sysFileTemp.getPath());
                idList.add(sysFileTemp.getId());
            }
            getBaseMapper().deleteBatchIds(idList);
        }
    }

}