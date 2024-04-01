package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.SysFile;

import java.util.List;

/**
 * 系统附件Service接口
 *
 * @author yangjie
 * @date 2022-10-28
 */
public interface ISysFileService extends IService<SysFile> {


    /**
     * 查询系统附件
     *
     * @param id 系统附件主键
     * @return 系统附件
     */
    public SysFile selectSysFileById(String id);


    /**
     * 根据ids查询系统附件
     *
     * @param ids 系统附件 IDs
     * @return 系统附件
     */
    public List<SysFile> selectSysFileByIds(String[] ids);

    /**
     * 查询系统附件列表
     *
     * @param sysFile 系统附件
     * @return 系统附件集合
     */
    public List<SysFile> selectSysFileList(SysFile sysFile);

    /**
     * 新增系统附件
     *
     * @param sysFile 系统附件
     * @return 结果
     */
    public int insertSysFile(SysFile sysFile);

    /**
     * 修改系统附件
     *
     * @param sysFile 系统附件
     * @return 结果
     */
    public int updateSysFile(SysFile sysFile);

    /**
     * 批量删除系统附件
     *
     * @param ids 需要删除的系统附件主键集合
     * @return 结果
     */
    public int deleteSysFileByIds(String[] ids);

    /**
     * 删除系统附件信息
     *
     * @param id 系统附件主键
     * @return 结果
     */
    public int deleteSysFileById(String id);

}