package com.ruoyi.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.mapper.SysFileMapper;
import com.ruoyi.system.service.ISysFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 系统附件Service接口
 *
 * @author yangjie
 * @date 2022-10-28
 */
@Slf4j
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

    @Autowired
    private SysFileMapper sysFileMapper;

    /**
     * 查询系统附件
     *
     * @param id 系统附件主键
     * @return 系统附件
     */
    @Override
    public SysFile selectSysFileById(String id) {
        QueryWrapper<SysFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return sysFileMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询系统附件
     *
     * @param ids 系统附件 IDs
     * @return 系统附件
     */
    @Override
    public List<SysFile> selectSysFileByIds(String[] ids) {
        QueryWrapper<SysFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return sysFileMapper.selectList(queryWrapper);
    }

    /**
     * 查询系统附件列表
     *
     * @param sysFile 系统附件
     * @return 系统附件集合
     */
    @Override
    public List<SysFile> selectSysFileList(SysFile sysFile) {
        sysFile.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        return sysFileMapper.selectSysFileList(sysFile);
    }

    /**
     * 新增系统附件
     *
     * @param sysFile 系统附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysFile(SysFile sysFile) {
        sysFile.setId(IdUtil.simpleUUID());
        sysFile.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        return sysFileMapper.insert(sysFile);
    }

    /**
     * 修改系统附件
     *
     * @param sysFile 系统附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysFile(SysFile sysFile) {
        return sysFileMapper.updateById(sysFile);
    }

    /**
     * 批量删除系统附件
     *
     * @param ids 需要删除的系统附件主键集合
     * @return 结果
     */
    @Override
    public int deleteSysFileByIds(String[] ids) {
        List<SysFile> sysFiles = new ArrayList<>();
        for (String id : ids) {
            SysFile sysFile = new SysFile();
            sysFile.setId(id);
            sysFile.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            sysFiles.add(sysFile);
        }
        return super.updateBatchById(sysFiles) ? 1 : 0;
    }

    /**
     * 删除系统附件信息
     *
     * @param id 系统附件主键
     * @return 结果
     */
    @Override
    public int deleteSysFileById(String id) {
        SysFile sysFile = new SysFile();
        sysFile.setId(id);
        sysFile.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return sysFileMapper.updateById(sysFile);
    }

}