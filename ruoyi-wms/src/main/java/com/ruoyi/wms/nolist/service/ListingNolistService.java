package com.ruoyi.wms.nolist.service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Date;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.nolist.domain.ListingNolist;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.nolist.mapper.ListingNolistMapper;
import com.ruoyi.wms.nolist.domain.ListingNolist;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 无单上架Service接口
 *
 * @author ruoyi
 * @date 2023-03-06
 */
@Slf4j
@Service
public class ListingNolistService extends ServiceImpl<ListingNolistMapper, ListingNolist> {

    @Autowired
    private ListingNolistMapper listingNolistMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询无单上架
     *
     * @param id 无单上架主键
     * @return 无单上架
     */
    public ListingNolist selectListingNolistById(String id){
        QueryWrapper<ListingNolist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return listingNolistMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询无单上架
     *
     * @param ids 无单上架 IDs
     * @return 无单上架
     */
    public List<ListingNolist> selectListingNolistByIds(String[] ids) {
        QueryWrapper<ListingNolist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return listingNolistMapper.selectList(queryWrapper);
    }

    /**
     * 查询无单上架列表
     *
     * @param listingNolist 无单上架
     * @return 无单上架集合
     */
    public List<ListingNolist> selectListingNolistList(ListingNolist listingNolist){
        QueryWrapper<ListingNolist> queryWrapper = getQueryWrapper(listingNolist);
        queryWrapper.groupBy("listing_code");
        return listingNolistMapper.selectList(queryWrapper);
    }

    /**
     * 新增无单上架
     *
     * @param listingNolist 无单上架
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ListingNolist insertListingNolist(ListingNolist listingNolist){
        listingNolist.setId(IdUtil.simpleUUID());
        listingNolist.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        listingNolistMapper.insert(listingNolist);
        return listingNolist;
    }

    /**
     * 修改无单上架
     *
     * @param listingNolist 无单上架
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ListingNolist updateListingNolist(ListingNolist listingNolist){
        listingNolistMapper.updateById(listingNolist);
        return listingNolist;
    }

    /**
     * 批量删除无单上架
     *
     * @param ids 需要删除的无单上架主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteListingNolistByIds(String[] ids){
        List<ListingNolist> listingNolists = new ArrayList<>();
        for (String id : ids) {
            ListingNolist listingNolist = new ListingNolist();
            listingNolist.setId(id);
            listingNolist.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            listingNolists.add(listingNolist);
        }
        return super.updateBatchById(listingNolists) ? 1 : 0;
    }

    /**
     * 删除无单上架信息
     *
     * @param id 无单上架主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteListingNolistById(String id){
        ListingNolist listingNolist = new ListingNolist();
        listingNolist.setId(id);
        listingNolist.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return listingNolistMapper.updateById(listingNolist);
    }

    public QueryWrapper<ListingNolist> getQueryWrapper(ListingNolist listingNolist) {
        QueryWrapper<ListingNolist> queryWrapper = new QueryWrapper<>();
        if (listingNolist != null) {
            listingNolist.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",listingNolist.getDelFlag());
            //上架单号
            if (StrUtil.isNotEmpty(listingNolist.getListingCode())) {
                queryWrapper.like("listing_code",listingNolist.getListingCode());
            }
            //货物唯一码
            if (StrUtil.isNotEmpty(listingNolist.getOnlyCode())) {
                queryWrapper.like("only_code",listingNolist.getOnlyCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(listingNolist.getMpCode())) {
                queryWrapper.like("mp_code",listingNolist.getMpCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(listingNolist.getGoodsCode())) {
                queryWrapper.like("goods_code",listingNolist.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(listingNolist.getGoodsName())) {
                queryWrapper.like("goods_name",listingNolist.getGoodsName());
            }

            //库位编号
            if (StrUtil.isNotEmpty(listingNolist.getLocationCode())) {
                queryWrapper.like("location_code",listingNolist.getLocationCode());
            }
            //库位名称
            if (StrUtil.isNotEmpty(listingNolist.getLocationName())) {
                queryWrapper.like("location_name",listingNolist.getLocationName());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(listingNolist.getTrayCode())) {
                queryWrapper.like("tray_code",listingNolist.getTrayCode());
            }
            //上架时间
            if (StringUtils.isNotEmpty(listingNolist.getParams())&&StringUtils.isNotNull(listingNolist.getParams().get("beginListingTime"))) {
                String begin = listingNolist.getParams().get("beginListingTime")+" 00:00:00";
                String end = listingNolist.getParams().get("endListingTime")+" 23:59:59";
                queryWrapper.between("listing_time",begin,end);
            }
            //制单时间
            if (StringUtils.isNotEmpty(listingNolist.getParams())&&StringUtils.isNotNull(listingNolist.getParams().get("beginCreateTime"))) {
                String begin = listingNolist.getParams().get("beginCreateTime")+" 00:00:00";
                String end = listingNolist.getParams().get("endCreateTime")+" 23:59:59";
                queryWrapper.between("create_time",begin,end);
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param listingNolistList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<ListingNolist> listingNolistList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(listingNolistList) || listingNolistList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (ListingNolist listingNolist : listingNolistList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                ListingNolist u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, listingNolist);
                    listingNolist.setId(IdUtil.simpleUUID());
                    listingNolist.setCreateBy(operName);
                    listingNolist.setCreateTime(new Date());
                    listingNolist.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    listingNolistMapper.insert(listingNolist);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, listingNolist);
                    //todo 验证
                    //int count = listingNolistMapper.checkCode(listingNolist);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        listingNolist.setId(u.getId());
                        listingNolist.setUpdateBy(operName);
                        listingNolist.setUpdateTime(new Date());
                        listingNolistMapper.updateById(listingNolist);
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
     * 查询无单上架列表
     *
     * @param listingNolist 无单上架
     * @return 无单上架集合
     */
    public List<ListingNolist> listDetail(ListingNolist listingNolist){
        QueryWrapper<ListingNolist> queryWrapper = getQueryWrapper(listingNolist);
        return listingNolistMapper.selectList(queryWrapper);
    }

}
