package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.SysFile;

import java.util.List;

/**
 * 系统附件Mapper接口
 *
 * @author yangjie
 * @date 2022-10-28
 */
public interface SysFileMapper extends BaseMapper<SysFile> {


    /**
     * 查询系统附件列表
     *
     * @param sysFile 系统附件
     * @return 系统附件集合
     */
    List<SysFile> selectSysFileList(SysFile sysFile);

}