package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.SysFileTemp;

import java.util.List;

/**
 * 系统临时附件Service接口
 *
 * @author yangjie
 * @date 2022-10-28
 */
public interface ISysFileTempService extends IService<SysFileTemp> {


    /**
     * 查询系统临时附件
     *
     * @param id 系统临时附件主键
     * @return 系统临时附件
     */
    public SysFileTemp selectSysFileTempById(String id);

    /**
     * 根据ids查询系统临时附件
     *
     * @param ids 系统临时附件 IDs
     * @return 系统临时附件
     */
    public List<SysFileTemp> selectSysFileTempByIds(String[] ids);

    /**
     * 查询系统临时附件列表
     *
     * @param sysFileTemp 系统临时附件
     * @return 系统临时附件集合
     */
    public List<SysFileTemp> selectSysFileTempList(SysFileTemp sysFileTemp);


    /**
     * 新增系统临时附件
     *
     * @param sysFileTemp 系统临时附件
     * @return 结果
     */
    public int insertSysFileTemp(SysFileTemp sysFileTemp);

    /**
     * 修改系统临时附件
     *
     * @param sysFileTemp 系统临时附件
     * @return 结果
     */
    public int updateSysFileTemp(SysFileTemp sysFileTemp);


    /**
     * 批量删除系统临时附件
     *
     * @param ids 需要删除的系统临时附件主键集合
     * @return 结果
     */
    public int deleteSysFileTempByIds(String[] ids);

    /**
     * 删除系统临时附件信息
     *
     * @param id 系统临时附件主键
     * @return 结果
     */
    public int deleteSysFileTempById(String id);

    /**
     * 清理临时表数据及临时文件
     * @param sysFileTempList
     */
    public void cleanTempDataAndTempFiles(List<SysFileTemp> sysFileTempList);

}