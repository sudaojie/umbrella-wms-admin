package com.ruoyi.wms.nolist.service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Date;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wms.enums.ListingEnum;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.nolist.domain.NolistWait;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.nolist.mapper.NolistWaitMapper;
import com.ruoyi.wms.nolist.domain.NolistWait;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 无单上架待上架列Service接口
 *
 * @author ruoyi
 * @date 2023-03-08
 */
@Slf4j
@Service
public class NolistWaitService extends ServiceImpl<NolistWaitMapper, NolistWait> {

    @Autowired
    private NolistWaitMapper nolistWaitMapper;
    @Autowired
    private TblstockMapper tblstockMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询无单上架待上架列
     *
     * @param id 无单上架待上架列主键
     * @return 无单上架待上架列
     */
    public NolistWait selectNolistWaitById(String id){
        QueryWrapper<NolistWait> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return nolistWaitMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询无单上架待上架列
     *
     * @param ids 无单上架待上架列 IDs
     * @return 无单上架待上架列
     */
    public List<NolistWait> selectNolistWaitByIds(String[] ids) {
        QueryWrapper<NolistWait> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return nolistWaitMapper.selectList(queryWrapper);
    }

    /**
     * 查询无单上架待上架列列表
     *
     * @param nolistWait 无单上架待上架列
     * @return 无单上架待上架列集合
     */
    public List<NolistWait> selectNolistWaitList(NolistWait nolistWait){
        QueryWrapper<NolistWait> queryWrapper = getQueryWrapper(nolistWait);
        List<NolistWait> list = nolistWaitMapper.select(queryWrapper);
        for (NolistWait obj:list) {
            List<String> partsCodes = tblstockMapper.selectPartsCodeByTrayCode(obj.getTrayCode());
            obj.setPartsCodes(partsCodes);
        }
        return list;
    }

    /**
     * 新增无单上架待上架列
     *
     * @param nolistWait 无单上架待上架列
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public NolistWait insertNolistWait(NolistWait nolistWait){
        nolistWait.setId(IdUtil.simpleUUID());
        nolistWait.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        nolistWaitMapper.insert(nolistWait);
        return nolistWait;
    }

    /**
     * 修改无单上架待上架列
     *
     * @param nolistWait 无单上架待上架列
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public NolistWait updateNolistWait(NolistWait nolistWait){
        nolistWaitMapper.updateById(nolistWait);
        return nolistWait;
    }

    /**
     * 批量删除无单上架待上架列
     *
     * @param ids 需要删除的无单上架待上架列主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteNolistWaitByIds(String[] ids){
        List<NolistWait> nolistWaits = new ArrayList<>();
        for (String id : ids) {
            NolistWait nolistWait = new NolistWait();
            nolistWait.setId(id);
            nolistWait.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            nolistWaits.add(nolistWait);
        }
        return super.updateBatchById(nolistWaits) ? 1 : 0;
    }

    /**
     * 删除无单上架待上架列信息
     *
     * @param id 无单上架待上架列主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteNolistWaitById(String id){
        NolistWait nolistWait = new NolistWait();
        nolistWait.setId(id);
        nolistWait.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return nolistWaitMapper.updateById(nolistWait);
    }

    public QueryWrapper<NolistWait> getQueryWrapper(NolistWait nolistWait) {
        QueryWrapper<NolistWait> queryWrapper = new QueryWrapper<>();
        if (nolistWait != null) {
            nolistWait.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",nolistWait.getDelFlag());
            //托盘编号
            if (StrUtil.isNotEmpty(nolistWait.getTrayCode())) {
                queryWrapper.like("tray_code",nolistWait.getTrayCode());
            }
            //上架状态;(0-未上架 1-已上架 2-上架中)
            if (StrUtil.isNotEmpty(nolistWait.getListingStatus())) {
                queryWrapper.eq("listing_status",nolistWait.getListingStatus());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param nolistWaitList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<NolistWait> nolistWaitList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(nolistWaitList) || nolistWaitList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (NolistWait nolistWait : nolistWaitList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                NolistWait u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, nolistWait);
                    nolistWait.setId(IdUtil.simpleUUID());
                    nolistWait.setCreateBy(operName);
                    nolistWait.setCreateTime(new Date());
                    nolistWait.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    nolistWaitMapper.insert(nolistWait);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, nolistWait);
                    //todo 验证
                    //int count = nolistWaitMapper.checkCode(nolistWait);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        nolistWait.setId(u.getId());
                        nolistWait.setUpdateBy(operName);
                        nolistWait.setUpdateTime(new Date());
                        nolistWaitMapper.updateById(nolistWait);
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
     * 无单上架
     */
    public void putOnTray(String trayCode){
        //删除待上架列表
        LambdaUpdateWrapper<NolistWait> nolistWaitUpdateWrapper = Wrappers.lambdaUpdate();
        nolistWaitUpdateWrapper.set(BaseEntity::getDelFlag,DelFlagEnum.DEL_YES.getCode())
                .set(NolistWait::getListingStatus, ListingEnum.ALREADY.getCode())
                .eq(NolistWait::getListingStatus,ListingEnum.ING.getCode())
                .eq(NolistWait::getTrayCode,trayCode);
        nolistWaitMapper.update(null,nolistWaitUpdateWrapper);
        //解锁托盘库存
        LambdaUpdateWrapper<Tblstock> tblstockUpdateWrapper = Wrappers.lambdaUpdate();
        tblstockUpdateWrapper.set(Tblstock::getLockStatus, LockEnum.NOTLOCK.getCode())
                .eq(Tblstock::getTrayCode,trayCode);
        tblstockMapper.update(null,tblstockUpdateWrapper);
    }
}
