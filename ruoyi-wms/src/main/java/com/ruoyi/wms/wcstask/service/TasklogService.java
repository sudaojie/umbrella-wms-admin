package com.ruoyi.wms.wcstask.service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Date;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.wcstask.domain.Tasklog;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.wcstask.mapper.TasklogMapper;
import com.ruoyi.wms.wcstask.domain.Tasklog;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 给wcs的任务日志Service接口
 *
 * @author ruoyi
 * @date 2023-02-28
 */
@Slf4j
@Service
public class TasklogService extends ServiceImpl<TasklogMapper, Tasklog> {

    @Autowired
    private TasklogMapper tasklogMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询给wcs的任务日志
     *
     * @param id 给wcs的任务日志主键
     * @return 给wcs的任务日志
     */
    public Tasklog selectTasklogById(String id){
        QueryWrapper<Tasklog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return tasklogMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询给wcs的任务日志
     *
     * @param ids 给wcs的任务日志 IDs
     * @return 给wcs的任务日志
     */
    public List<Tasklog> selectTasklogByIds(String[] ids) {
        QueryWrapper<Tasklog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return tasklogMapper.selectList(queryWrapper);
    }

    /**
     * 查询给wcs的任务日志列表
     *
     * @param tasklog 给wcs的任务日志
     * @return 给wcs的任务日志集合
     */
    public List<Tasklog> selectTasklogList(Tasklog tasklog){
        QueryWrapper<Tasklog> queryWrapper = getQueryWrapper(tasklog);
        return tasklogMapper.select(queryWrapper);
    }

    /**
     * 新增给wcs的任务日志
     *
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Tasklog insertTasklog(WmsWcsInfo info){
        Tasklog tasklog = new Tasklog();
        tasklog.setTaskData(JSON.toJSONString(info));
        tasklog.setOpType((String)info.get(WmsWcsInfo.TYPE));
        tasklog.setAreaType((String)info.get(WmsWcsInfo.AREATYPE));
        tasklog.setId(IdUtil.simpleUUID());
        tasklog.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        tasklogMapper.insert(tasklog);
        return tasklog;
    }

    /**
     * 批量新增给wcs的任务日志
     *
     * @param infoList
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<WmsWcsInfo> infoList){
        List<Tasklog> collect = infoList.stream().map(info -> {
            Tasklog tasklog = new Tasklog();
            tasklog.setTaskData(JSON.toJSONString(info));
            tasklog.setOpType((String)info.get(WmsWcsInfo.TYPE));
            tasklog.setAreaType((String)info.get(WmsWcsInfo.AREATYPE));
            tasklog.setId(IdUtil.simpleUUID());
            tasklog.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            return tasklog;
        }).collect(Collectors.toList());
        return saveBatch(collect,collect.size());
    }

    /**
     * 修改给wcs的任务日志
     *
     * @param tasklog 给wcs的任务日志
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Tasklog updateTasklog(Tasklog tasklog){
        tasklogMapper.updateById(tasklog);
        return tasklog;
    }

    /**
     * 批量删除给wcs的任务日志
     *
     * @param ids 需要删除的给wcs的任务日志主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteTasklogByIds(String[] ids){
        List<Tasklog> tasklogs = new ArrayList<>();
        for (String id : ids) {
            Tasklog tasklog = new Tasklog();
            tasklog.setId(id);
            tasklog.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            tasklogs.add(tasklog);
        }
        return super.updateBatchById(tasklogs) ? 1 : 0;
    }

    /**
     * 删除给wcs的任务日志信息
     *
     * @param id 给wcs的任务日志主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteTasklogById(String id){
        Tasklog tasklog = new Tasklog();
        tasklog.setId(id);
        tasklog.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return tasklogMapper.updateById(tasklog);
    }

    public QueryWrapper<Tasklog> getQueryWrapper(Tasklog tasklog) {
        QueryWrapper<Tasklog> queryWrapper = new QueryWrapper<>();
        if (tasklog != null) {
            tasklog.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",tasklog.getDelFlag());
            //数据
            if (StrUtil.isNotEmpty(tasklog.getTaskData())) {
                queryWrapper.eq("task_data",tasklog.getTaskData());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param tasklogList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<Tasklog> tasklogList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(tasklogList) || tasklogList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Tasklog tasklog : tasklogList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                Tasklog u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, tasklog);
                    tasklog.setId(IdUtil.simpleUUID());
                    tasklog.setCreateBy(operName);
                    tasklog.setCreateTime(new Date());
                    tasklog.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    tasklogMapper.insert(tasklog);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, tasklog);
                    //todo 验证
                    //int count = tasklogMapper.checkCode(tasklog);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        tasklog.setId(u.getId());
                        tasklog.setUpdateBy(operName);
                        tasklog.setUpdateTime(new Date());
                        tasklogMapper.updateById(tasklog);
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
}
