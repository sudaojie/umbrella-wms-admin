package com.ruoyi.system.service.impl;

import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.constant.BusinessConstants;
import com.ruoyi.common.core.domain.entity.SysDistrict;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.system.service.ISysDistrictService;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.system.mapper.SysDistrictMapper;

/**
 * 行政区划Service接口
 *
 * @author ruoyi
 * @date 2023-01-04
 */
@Slf4j
@Service
public class SysDistrictServiceImpl extends ServiceImpl<SysDistrictMapper, SysDistrict> implements ISysDistrictService {

    @Autowired
    private SysDistrictMapper sysDistrictMapper;

    /**
     * 查询行政区划
     *
     * @param id 行政区划主键
     * @return 行政区划
     */
    @Override
    public SysDistrict selectSysDistrictById(String id){
        QueryWrapper<SysDistrict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return sysDistrictMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询行政区划
     *
     * @param ids 行政区划 IDs
     * @return 行政区划
     */
    @Override
    public List<SysDistrict> selectSysDistrictByIds(String[] ids) {
        QueryWrapper<SysDistrict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return sysDistrictMapper.selectList(queryWrapper);
    }

    /**
     * 查询行政区划列表
     *
     * @param sysDistrict 行政区划
     * @return 行政区划集合
     */
    @Override
    public List<SysDistrict> selectSysDistrictList(SysDistrict sysDistrict){
        QueryWrapper<SysDistrict> queryWrapper = getQueryWrapper(sysDistrict);
        return sysDistrictMapper.select(sysDistrict);
    }

    /**
     * 新增行政区划
     *
     * @param sysDistrict 行政区划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDistrict insertSysDistrict(SysDistrict sysDistrict){
        sysDistrict.setId(IdUtil.simpleUUID());
        sysDistrict.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        sysDistrictMapper.insert(sysDistrict);
        return sysDistrict;
    }

    /**
     * 修改行政区划
     *
     * @param sysDistrict 行政区划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDistrict updateSysDistrict(SysDistrict sysDistrict){
        sysDistrictMapper.updateById(sysDistrict);
        return sysDistrict;
    }

    /**
     * 批量删除行政区划
     *
     * @param ids 需要删除的行政区划主键集合
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysDistrictByIds(String[] ids){
        List<SysDistrict> sysDistricts = new ArrayList<>();
        for (String id : ids) {
            SysDistrict sysDistrict = new SysDistrict();
            sysDistrict.setId(id);
            sysDistrict.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            sysDistricts.add(sysDistrict);
        }
        return super.updateBatchById(sysDistricts) ? 1 : 0;
    }

    /**
     * 删除行政区划信息
     *
     * @param id 行政区划主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysDistrictById(String id){
        SysDistrict sysDistrict = new SysDistrict();
        sysDistrict.setId(id);
        sysDistrict.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return sysDistrictMapper.updateById(sysDistrict);
    }

    public QueryWrapper<SysDistrict> getQueryWrapper(SysDistrict sysDistrict) {
        QueryWrapper<SysDistrict> queryWrapper = new QueryWrapper<>();
        if (sysDistrict != null) {
            sysDistrict.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            //名称
            if (StrUtil.isNotEmpty(sysDistrict.getDistrictName())) {
                queryWrapper.like("district_name", sysDistrict.getDistrictName());
            }
            //父 ID
            if (StrUtil.isNotEmpty(sysDistrict.getPId())) {
                queryWrapper.eq("p_id", sysDistrict.getPId());
            }
            //拼音首字母
            if (StrUtil.isNotEmpty(sysDistrict.getInitial())) {
                queryWrapper.eq("initial", sysDistrict.getInitial());
            }
            //拼音首字母集合
            if (StrUtil.isNotEmpty(sysDistrict.getInitials())) {
                queryWrapper.eq("initials", sysDistrict.getInitials());
            }
            //拼音
            if (StrUtil.isNotEmpty(sysDistrict.getPinyin())) {
                queryWrapper.eq("pinyin", sysDistrict.getPinyin());
            }
            //附加说明
            if (StrUtil.isNotEmpty(sysDistrict.getExtra())) {
                queryWrapper.eq("extra", sysDistrict.getExtra());
            }
            //行政级别
            if (StrUtil.isNotEmpty(sysDistrict.getSuffix())) {
                queryWrapper.eq("suffix", sysDistrict.getSuffix());
            }
            //行政代码
            if (StrUtil.isNotEmpty(sysDistrict.getCode())) {
                queryWrapper.eq("code", sysDistrict.getCode());
            }
            //区号
            if (StrUtil.isNotEmpty(sysDistrict.getAreaCode())) {
                queryWrapper.eq("area_code", sysDistrict.getAreaCode());
            }
            //排序
            if (sysDistrict.getOrderNo() != null) {
                queryWrapper.eq("order_no", sysDistrict.getOrderNo());
            }
        }
        return queryWrapper;
    }

    /**
     * 加载子级
     * @param pId
     * @return
     */
    public List<SysDistrict> listChildren(String pId) {
        if (StrUtil.isNotEmpty(pId)) {
            QueryWrapper<SysDistrict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("p_id", pId);
            queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
            return sysDistrictMapper.selectList(queryWrapper);
        }
        return new ArrayList<>();
    }

    /**
     * 加载编号和名称映射关系
     * @param pIds
     * @return
     */
    public Map<String, String> listChildrenIds(String pIds) {
        List<SysDistrict> list;
        List<String> pIdList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        if (StrUtil.isNotEmpty(pIds)) {
            pIdList = Arrays.asList(pIds.split(","));
        }
        if (CollUtil.isNotEmpty(pIdList)) {
            pIdList = pIdList.stream().filter(StrUtil::isNotEmpty).collect(Collectors.toList());
            QueryWrapper<SysDistrict> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", pIdList);
            queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
            list = sysDistrictMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(list)) {
                map = list.stream().collect(Collectors.toMap(SysDistrict::getId, SysDistrict::getDistrictName));
            }
            return map;
        }
        return map;
    }
}
