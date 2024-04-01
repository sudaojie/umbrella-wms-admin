package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysDistrict;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 行政区划 服务层
 *
 * @author ruoyi
 */
public interface ISysDistrictService {
    /**
     * 根据id查询行政区划
     *
     * @param id 编号
     * @return SysDistrict
     */
    public SysDistrict selectSysDistrictById(String id);

    /**
     * 根据ids查询行政区划
     *
     * @param ids 行政区划 IDs
     * @return 行政区划
     */
    public List<SysDistrict> selectSysDistrictByIds(String[] ids);

    /**
     * 查询行政区划列表
     *
     * @param sysDistrict 行政区划
     * @return 行政区划集合
     */
    public List<SysDistrict> selectSysDistrictList(SysDistrict sysDistrict);

    /**
     * 新增行政区划
     *
     * @param sysDistrict 行政区划
     * @return 结果
     */
    public SysDistrict insertSysDistrict(SysDistrict sysDistrict);

    /**
     * 修改行政区划
     *
     * @param sysDistrict 行政区划
     * @return 结果
     */
    public SysDistrict updateSysDistrict(SysDistrict sysDistrict);

    /**
     * 批量删除行政区划
     *
     * @param ids 需要删除的行政区划主键集合
     * @return 结果
     */
    public int deleteSysDistrictByIds(String[] ids);

    /**
     * 删除行政区划信息
     *
     * @param id 行政区划主键
     * @return 结果
     */
    public int deleteSysDistrictById(String id);

}
