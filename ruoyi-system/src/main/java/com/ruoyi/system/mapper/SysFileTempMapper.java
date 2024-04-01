package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysFileTemp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 系统临时附件Mapper接口
 *
 * @author yangjie
 * @date 2022-10-28
 */
public interface SysFileTempMapper extends BaseMapper<SysFileTemp> {


    /**
     * 查询系统临时附件列表
     *
     * @param sysFileTemp 系统临时附件
     * @return 系统临时附件集合
     */
    List<SysFileTemp> selectSysFileTempList(SysFileTemp sysFileTemp);

}
