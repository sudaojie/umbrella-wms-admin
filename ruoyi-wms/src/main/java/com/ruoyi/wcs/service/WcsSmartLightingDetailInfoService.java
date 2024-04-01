package com.ruoyi.wcs.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.iot.task.ctrl.LightCtrlTask;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.mapper.SysJobMapper;
import com.ruoyi.quartz.service.impl.SysJobServiceImpl;
import com.ruoyi.quartz.util.ScheduleUtils;
import com.ruoyi.wcs.constans.WcsTimerConstants;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsSmartLightingDetailInfo;
import com.ruoyi.wcs.domain.WcsSmartLightingJobRelateInfo;
import com.ruoyi.wcs.domain.dto.WcsSmartLightParamDto;
import com.ruoyi.wcs.domain.vo.WcsSmartLightVo;
import com.ruoyi.wcs.enums.wcs.*;
import com.ruoyi.wcs.mapper.WcsSmartLightingDetailInfoMapper;
import com.ruoyi.wcs.util.WcsGenerateCronUtil;
import lombok.SneakyThrows;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智慧照明系统详情信息Service接口
 *
 * @author hewei
 * @date 2023-04-12
 */
@Slf4j
@Service
public class WcsSmartLightingDetailInfoService extends ServiceImpl<WcsSmartLightingDetailInfoMapper, WcsSmartLightingDetailInfo> {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private WcsSmartLightingDetailInfoMapper wcsSmartLightingDetailInfoMapper;

    @Autowired
    private SysJobMapper sysJobMapper;

    @Autowired
    private SysJobServiceImpl sysJobService;

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    private WcsSmartLightingJobRelateInfoService wcsSmartLightingJobRelateInfoService;

    @Autowired
    private LightCtrlTask lightCtrlTask;

    /**
     * 查询智慧照明系统基本信息列表
     *
     * @param wcsSmartLightParamDto wcsSmartLightParamDto
     * @return list
     */
    public List<WcsSmartLightVo> queryList(WcsSmartLightParamDto wcsSmartLightParamDto) {
        List<WcsSmartLightVo> list = new ArrayList<>();
        list = wcsSmartLightingDetailInfoMapper.queryList(wcsSmartLightParamDto);
        return list;
    }

    /**
     * 开启WCS照明设备
     *
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean start(String id) {
        if (StrUtil.isNotEmpty(id)) {
            List<String> ids = Arrays.asList(id.split(","));
            List<WcsSmartLightingDetailInfo> wcsSmartLightingDetailInfos = wcsSmartLightingDetailInfoMapper.selectBatchIds(ids);
            List<WcsSmartLightingDetailInfo> list = new ArrayList<>();
            if (CollUtil.isNotEmpty(ids)) {
                wcsSmartLightingDetailInfos.forEach(item -> {
                    WcsSmartLightingDetailInfo wcsSmartLightingDetailInfo = new WcsSmartLightingDetailInfo();
                    wcsSmartLightingDetailInfo.setId(item.getId());
                    wcsSmartLightingDetailInfo.setSwitchStatus(Integer.parseInt(WcsSwitchStatusEnum.OPEN.getCode()));
                    wcsSmartLightingDetailInfo.setBeginTime(item.getBeginTime());
                    wcsSmartLightingDetailInfo.setEndTime(item.getEndTime());
                    list.add(wcsSmartLightingDetailInfo);
                });
                QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
                qw.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
                qw.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
                List<String> deviceInfoIds = wcsSmartLightingDetailInfoMapper.selectList(new QueryWrapper<WcsSmartLightingDetailInfo>().in("id", ids)).stream().map(WcsSmartLightingDetailInfo::getDeviceInfoId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(deviceInfoIds)) {
                    qw.in("id", deviceInfoIds);
                    List<WcsDeviceBaseInfo> deviceBaseInfos = wcsDeviceBaseInfoService.getBaseMapper().selectList(qw);
                    if (CollUtil.isNotEmpty(deviceBaseInfos)) {
                        lightCtrlTask.openLight(deviceBaseInfos);
                    }
                }
            }
            if (CollUtil.isNotEmpty(list)) {
                return this.saveOrUpdateBatch(list);
            }
        } else {
            throw new ServiceException("启动照明设备缺失必要参数");
        }
        return false;
    }

    /**
     * 关闭WCS照明设备
     *
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean pause(String id) {
        if (StrUtil.isNotEmpty(id)) {
            List<String> ids = Arrays.asList(id.split(","));
            List<WcsSmartLightingDetailInfo> wcsSmartLightingDetailInfos = wcsSmartLightingDetailInfoMapper.selectBatchIds(ids);
            List<WcsSmartLightingDetailInfo> list = new ArrayList<>();
            if (CollUtil.isNotEmpty(ids)) {
                wcsSmartLightingDetailInfos.forEach(item -> {
                    WcsSmartLightingDetailInfo wcsSmartLightingDetailInfo = new WcsSmartLightingDetailInfo();
                    wcsSmartLightingDetailInfo.setId(item.getId());
                    wcsSmartLightingDetailInfo.setSwitchStatus(Integer.parseInt(WcsSwitchStatusEnum.CLOSE.getCode()));
                    wcsSmartLightingDetailInfo.setBeginTime(item.getBeginTime());
                    wcsSmartLightingDetailInfo.setEndTime(item.getEndTime());
                    list.add(wcsSmartLightingDetailInfo);
                });
                QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
                qw.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
                qw.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
                List<String> deviceInfoIds = wcsSmartLightingDetailInfoMapper.selectList(new QueryWrapper<WcsSmartLightingDetailInfo>().in("id", ids)).stream().map(WcsSmartLightingDetailInfo::getDeviceInfoId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(deviceInfoIds)) {
                    qw.in("id", deviceInfoIds);
                    List<WcsDeviceBaseInfo> deviceBaseInfos = wcsDeviceBaseInfoService.getBaseMapper().selectList(qw);
                    if (CollUtil.isNotEmpty(deviceBaseInfos)) {
                        lightCtrlTask.closeLight(deviceBaseInfos);
                    }
                }
            }
            if (CollUtil.isNotEmpty(list)) {
                return this.saveOrUpdateBatch(list);
            }
        } else {
            throw new ServiceException("关闭照明设备缺失必要参数");
        }
        return false;
    }

    /**
     * 保存WCS照明设备基本信息
     *
     * @param wcsSmartLightParamDto wcsSmartLightParamDto
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveData(WcsSmartLightParamDto wcsSmartLightParamDto) {
        if (ObjectUtil.isNotNull(wcsSmartLightParamDto)) {
            SimpleDateFormat sdf = new SimpleDateFormat(WcsTimerConstants.DATE_PATTERN);
            if (StrUtil.isNotEmpty(wcsSmartLightParamDto.getDeviceInfoId())) {
                WcsSmartLightingDetailInfo wcsSmartLightingDetailInfo = new WcsSmartLightingDetailInfo();
                wcsSmartLightingDetailInfo.setId(StrUtil.isNotEmpty(wcsSmartLightParamDto.getId())
                        ? wcsSmartLightParamDto.getId() : IdUtil.fastSimpleUUID())
                        .setDeviceInfoId(wcsSmartLightParamDto.getDeviceInfoId())
                        .setSwitchStatus(wcsSmartLightParamDto.getSwitchStatus())
                        .setSystemStatus(wcsSmartLightParamDto.getSystemStatus())
                        .setBeginTime(wcsSmartLightParamDto.getBeginTime())
                        .setEndTime(wcsSmartLightParamDto.getEndTime());

                List<WcsSmartLightingJobRelateInfo> list = wcsSmartLightingJobRelateInfoService.getBaseMapper()
                        .selectList(new QueryWrapper<WcsSmartLightingJobRelateInfo>()
                                .eq("device_info_id", wcsSmartLightParamDto.getDeviceInfoId())
                                .eq("type", WcsSmartLightRunStatusEnum.SINGLE.getCode()));
                if (CollUtil.isNotEmpty(list)) {
                    wcsSmartLightingJobRelateInfoService.getBaseMapper()
                            .delete(new QueryWrapper<WcsSmartLightingJobRelateInfo>()
                                    .eq("device_info_id", wcsSmartLightParamDto.getDeviceInfoId())
                                    .eq("type", WcsSmartLightRunStatusEnum.SINGLE.getCode()));
                    List<String> jobIds = list
                            .stream()
                            .map(WcsSmartLightingJobRelateInfo::getJobId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(jobIds)) {
                        List<Long> longs = new ArrayList<>();
                        jobIds.forEach(item -> {
                            longs.add(Long.valueOf(item));
                        });
                        Long[] longArray = longs.toArray(new Long[longs.size()]);
                        List<SysJob> deleteJobList = sysJobMapper.selectList(new QueryWrapper<SysJob>().in("job_id", Arrays.asList(longArray)));
                        if (CollUtil.isNotEmpty(deleteJobList)) {
                            deleteJobList.forEach(e -> {
                                try {
                                    sysJobService.deleteJob(e);
                                } catch (SchedulerException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }
                        ;
                    }
                }
                List<SysJob> jobList = new ArrayList<>();
                List<WcsSmartLightingJobRelateInfo> relateList = new ArrayList<>();
                WcsDeviceBaseInfo baseInfo = wcsDeviceBaseInfoService.getById(wcsSmartLightParamDto.getDeviceInfoId());
                if (ObjectUtil.isNotNull(wcsSmartLightParamDto.getBeginTime())) {
                    // 定时任务添加
                    long startJobId = generateUniqueId();
                    String jobName = StrUtil.format(WcsTimerConstants.START_SINGLE_TASK_NAME, baseInfo.getDeviceName());
                    SysJob startJob = new SysJob();
                    startJob.setJobId(startJobId);
                    startJob.setJobName(jobName);
                    startJob.setStatus(WcsTimerConstants.JOB_START_STATUS);
                    startJob.setConcurrent(WcsTimerConstants.NON_CONCURRENT);
                    startJob.setCronExpression(WcsGenerateCronUtil.INSTANCE
                            .generateCronByPeriodAndTime(WcsPeriodEnum.DAILY,
                                    sdf.format(wcsSmartLightParamDto.getBeginTime())));
                    startJob.setInvokeTarget(StrUtil.format(WcsTimerConstants.SINGLE_BEGIN_INVOKE_TARGET, startJobId));
                    startJob.setJobGroup(WcsTimerConstants.DEFAULT_GROUP_NAME);
                    startJob.setMisfirePolicy(WcsTimerConstants.IN_TIME_EXECUTE_STATUS);
                    startJob.setCreateBy(SecurityUtils.getUsername());
                    startJob.setCreateTime(new Date());

                    WcsSmartLightingJobRelateInfo beginRelateInfo = new WcsSmartLightingJobRelateInfo();
                    beginRelateInfo.setId(IdUtil.fastSimpleUUID())
                            .setDeviceInfoId(wcsSmartLightParamDto.getDeviceInfoId())
                            .setJobId(String.valueOf(startJobId))
                            .setStatus(Integer.valueOf(WcsSmartLightTimeStatusEnum.START.getCode()))
                            .setType(Integer.valueOf(WcsSmartLightRunStatusEnum.SINGLE.getCode()));

                    jobList.add(startJob);
                    relateList.add(beginRelateInfo);

                }
                if (ObjectUtil.isNotNull(wcsSmartLightParamDto.getEndTime())) {
                    long endJobId = generateUniqueId();
                    String jobName = StrUtil.format(WcsTimerConstants.FINISH_SINGLE_TASK_NAME, baseInfo.getDeviceName());
                    SysJob endJob = new SysJob();
                    endJob.setJobId(endJobId);
                    endJob.setJobName(jobName);
                    endJob.setStatus(WcsTimerConstants.JOB_START_STATUS);
                    endJob.setConcurrent(WcsTimerConstants.NON_CONCURRENT);
                    endJob.setCronExpression(WcsGenerateCronUtil.INSTANCE
                            .generateCronByPeriodAndTime(WcsPeriodEnum.DAILY,
                                    sdf.format(wcsSmartLightParamDto.getEndTime())));
                    endJob.setInvokeTarget(StrUtil.format(WcsTimerConstants.SINGLE_END_INVOKE_TARGET, endJobId));
                    endJob.setJobGroup(WcsTimerConstants.DEFAULT_GROUP_NAME);
                    endJob.setMisfirePolicy(WcsTimerConstants.IN_TIME_EXECUTE_STATUS);
                    endJob.setCreateBy(SecurityUtils.getUsername());
                    endJob.setCreateTime(new Date());

                    WcsSmartLightingJobRelateInfo endRelateInfo = new WcsSmartLightingJobRelateInfo();
                    endRelateInfo.setId(IdUtil.fastSimpleUUID())
                            .setDeviceInfoId(wcsSmartLightParamDto.getDeviceInfoId())
                            .setJobId(String.valueOf(endJobId))
                            .setStatus(Integer.valueOf(WcsSmartLightTimeStatusEnum.END.getCode()))
                            .setType(Integer.valueOf(WcsSmartLightRunStatusEnum.SINGLE.getCode()));

                    jobList.add(endJob);
                    relateList.add(endRelateInfo);
                }
                if (CollUtil.isNotEmpty(jobList)) {
                    jobList.forEach(item -> {
                        sysJobMapper.insertJob(item);
                        try {
                            ScheduleUtils.createScheduleJob(scheduler, item);
                        } catch (SchedulerException | TaskException e) {
                            e.printStackTrace();
                        }
                    });
                }
                if (CollUtil.isNotEmpty(relateList)) {
                    wcsSmartLightingJobRelateInfoService.saveOrUpdateBatch(relateList);
                }
                return this.saveOrUpdate(wcsSmartLightingDetailInfo);
            }
        }
        return false;
    }

    /**
     * 生成长整型唯一编号
     *
     * @return
     */
    public static long generateUniqueId() {
        String id = IdUtil.randomUUID();
        String str = "" + id;
        int uid = str.hashCode();
        String filterStr = "" + uid;
        str = filterStr.replaceAll("-", "");
        return Long.parseLong(str);
    }

    /**
     * 获取批量设置的时间
     *
     * @return WcsSmartLightVo wcsSmartLightVo
     */
    public WcsSmartLightVo getBatchSetTime() {
        WcsSmartLightVo wcsSmartLightVo = new WcsSmartLightVo();
        List<WcsSmartLightingJobRelateInfo> list =
                wcsSmartLightingJobRelateInfoService.getBaseMapper().selectList(new QueryWrapper<WcsSmartLightingJobRelateInfo>()
                        .eq("type", WcsSmartLightRunStatusEnum.MULTI.getCode()));
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(e -> {
                if (Integer.valueOf(WcsSmartLightTimeStatusEnum.START.getCode()).equals(e.getBatchOpenCloseFlag())) {
                    wcsSmartLightVo.setBatchOpenCloseFlag(true);
                } else {
                    wcsSmartLightVo.setBatchOpenCloseFlag(false);
                }
                if (WcsSmartLightTimeStatusEnum.START.getCode().equals(String.valueOf(e.getStatus()))) {
                    wcsSmartLightVo.setBeginTime(e.getBeginTime());
                }
                if (WcsSmartLightTimeStatusEnum.END.getCode().equals(String.valueOf(e.getStatus()))) {
                    wcsSmartLightVo.setEndTime(e.getEndTime());
                }
            });
        }
        return wcsSmartLightVo;
    }

    /**
     * 批量设置定时时间
     *
     * @param wcsSmartLightParamDto wcsSmartLightParamDto
     * @return Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("all")
    @SneakyThrows
    public Boolean saveBatchSetTime(WcsSmartLightParamDto wcsSmartLightParamDto) throws Exception {
        List<SysJob> jobList = new ArrayList<>();
        List<WcsSmartLightingJobRelateInfo> relateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(WcsTimerConstants.DATE_PATTERN);
        if (ObjectUtil.isNotNull(wcsSmartLightParamDto)) {
            List<WcsSmartLightingJobRelateInfo> list = wcsSmartLightingJobRelateInfoService.getBaseMapper()
                    .selectList(new QueryWrapper<WcsSmartLightingJobRelateInfo>()
                            .eq("type", WcsSmartLightRunStatusEnum.MULTI.getCode()));

            if (CollUtil.isNotEmpty(list)) {
                wcsSmartLightingJobRelateInfoService.getBaseMapper()
                        .delete(new QueryWrapper<WcsSmartLightingJobRelateInfo>()
                                .eq("type", WcsSmartLightRunStatusEnum.MULTI.getCode()));
                List<String> jobIds = list
                        .stream()
                        .map(WcsSmartLightingJobRelateInfo::getJobId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(jobIds)) {
                    List<Long> longs = new ArrayList<>();
                    jobIds.forEach(item -> {
                        longs.add(Long.valueOf(item));
                    });
                    Long[] longArray = longs.toArray(new Long[longs.size()]);
                    List<SysJob> deleteJobList = sysJobMapper.selectList(new QueryWrapper<SysJob>().in("job_id", Arrays.asList(longArray)));
                    if (CollUtil.isNotEmpty(deleteJobList)) {
                        deleteJobList.forEach(e -> {
                            try {
                                sysJobService.deleteJob(e);
                            } catch (SchedulerException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                    ;
                }
            }
            if (ObjectUtil.isNull(wcsSmartLightParamDto.getBatchOpenCloseFlag())) {
                wcsSmartLightParamDto.setBatchOpenCloseFlag(false);
            }
            if (wcsSmartLightParamDto.getBeginTime() != null) {
                // 定时任务添加
                long startJobId = generateUniqueId();
                String jobName = StrUtil.format(WcsTimerConstants.START_TASK_NAME);
                SysJob startJob = new SysJob();
                startJob.setJobId(startJobId);
                startJob.setJobName(jobName);
                startJob.setStatus(WcsTimerConstants.JOB_START_STATUS);
                startJob.setConcurrent(WcsTimerConstants.NON_CONCURRENT);
                startJob.setCronExpression(WcsGenerateCronUtil.INSTANCE
                        .generateCronByPeriodAndTime(WcsPeriodEnum.DAILY,
                                sdf.format(wcsSmartLightParamDto.getBeginTime())));

                startJob.setInvokeTarget(StrUtil.format(WcsTimerConstants.START_INVOKE_TARGET, startJobId));
                startJob.setJobGroup(WcsTimerConstants.DEFAULT_GROUP_NAME);
                startJob.setMisfirePolicy(WcsTimerConstants.IN_TIME_EXECUTE_STATUS);
                startJob.setCreateBy(SecurityUtils.getUsername());
                startJob.setCreateTime(new Date());

                WcsSmartLightingJobRelateInfo beginRelateInfo = new WcsSmartLightingJobRelateInfo();

                beginRelateInfo.setId(IdUtil.fastSimpleUUID())
                        .setDeviceInfoId("")
                        .setJobId(String.valueOf(startJobId))
                        .setStatus(Integer.valueOf(WcsSmartLightTimeStatusEnum.START.getCode()))
                        .setBeginTime(wcsSmartLightParamDto.getBeginTime())
                        .setBatchOpenCloseFlag(wcsSmartLightParamDto.getBatchOpenCloseFlag() ? Integer.valueOf(WcsSmartLightTimeStatusEnum.START.getCode()) : Integer.valueOf(WcsSmartLightTimeStatusEnum.END.getCode()))
                        .setType(Integer.valueOf(WcsSmartLightRunStatusEnum.MULTI.getCode()));

                jobList.add(startJob);
                relateList.add(beginRelateInfo);
            }
            if (wcsSmartLightParamDto.getEndTime() != null) {
                // 定时任务添加
                long endJobId = generateUniqueId();
                String jobName = StrUtil.format(WcsTimerConstants.FINISH_TASK_NAME);
                SysJob endJob = new SysJob();
                endJob.setJobId(endJobId);
                endJob.setJobName(jobName);
                endJob.setStatus(WcsTimerConstants.JOB_START_STATUS);
                endJob.setConcurrent(WcsTimerConstants.NON_CONCURRENT);
                endJob.setCronExpression(WcsGenerateCronUtil.INSTANCE
                        .generateCronByPeriodAndTime(WcsPeriodEnum.DAILY,
                                sdf.format(wcsSmartLightParamDto.getEndTime())));

                endJob.setInvokeTarget(StrUtil.format(WcsTimerConstants.FINISH_INVOKE_TARGET, endJobId));
                endJob.setJobGroup(WcsTimerConstants.DEFAULT_GROUP_NAME);
                endJob.setMisfirePolicy(WcsTimerConstants.IN_TIME_EXECUTE_STATUS);
                endJob.setCreateBy(SecurityUtils.getUsername());
                endJob.setCreateTime(new Date());

                WcsSmartLightingJobRelateInfo endRelateInfo = new WcsSmartLightingJobRelateInfo();
                endRelateInfo.setId(IdUtil.fastSimpleUUID())
                        .setDeviceInfoId("")
                        .setJobId(String.valueOf(endJobId))
                        .setStatus(Integer.valueOf(WcsSmartLightTimeStatusEnum.END.getCode()))
                        .setEndTime(wcsSmartLightParamDto.getEndTime())
                        .setBatchOpenCloseFlag(wcsSmartLightParamDto.getBatchOpenCloseFlag() ? Integer.valueOf(WcsSmartLightTimeStatusEnum.START.getCode()) : Integer.valueOf(WcsSmartLightTimeStatusEnum.END.getCode()))
                        .setType(Integer.valueOf(WcsSmartLightRunStatusEnum.MULTI.getCode()));

                jobList.add(endJob);
                relateList.add(endRelateInfo);
            }
            if (CollUtil.isNotEmpty(jobList)) {
                jobList.forEach(item -> {
                    sysJobMapper.insertJob(item);
                    try {
                        ScheduleUtils.createScheduleJob(scheduler, item);
                    } catch (SchedulerException | TaskException e) {
                        e.printStackTrace();
                    }
                });
            }
            if (CollUtil.isNotEmpty(relateList)) {
                wcsSmartLightingJobRelateInfoService.saveOrUpdateBatch(relateList);
            }
            List<WcsSmartLightingJobRelateInfo> relateInfos = wcsSmartLightingJobRelateInfoService.getBaseMapper().selectList(new QueryWrapper<WcsSmartLightingJobRelateInfo>());
            List<String> jobIds = relateInfos.stream().map(WcsSmartLightingJobRelateInfo::getJobId).collect(Collectors.toList());
            List<SysJob> jobs = sysJobMapper.selectList(new QueryWrapper<SysJob>().in("job_id", jobIds));
            // 如果开启全局 单个执行定时任务失效 如果关闭全局 单个执行定时任务生效
            List<SysJob> updateJobs = new ArrayList<>();
            if (wcsSmartLightParamDto.getBatchOpenCloseFlag()) {
                relateInfos.forEach(e -> {
                    for (SysJob item : jobs) {
                        SysJob sysJob = new SysJob();
                        BeanUtil.copyProperties(item, sysJob);
                        if (String.valueOf(sysJob.getJobId()).equals(e.getJobId())) {
                            if (String.valueOf(e.getType()).equals(WcsSmartLightRunStatusEnum.MULTI.getCode())) {
                                sysJob.setStatus(WcsTimerConstants.JOB_START_STATUS);
                            } else {
                                sysJob.setStatus(WcsTimerConstants.JOB_CLOSE_STATUS);
                            }
                            if (String.valueOf(e.getType()).equals(WcsSmartLightRunStatusEnum.SINGLE.getCode())) {
                                sysJob.setStatus(WcsTimerConstants.JOB_CLOSE_STATUS);
                            } else {
                                sysJob.setStatus(WcsTimerConstants.JOB_START_STATUS);
                            }
                            updateJobs.add(sysJob);
                        }
                    }
                });
            } else {
                relateInfos.forEach(e -> {
                    for (SysJob item : jobs) {
                        SysJob sysJob = new SysJob();
                        BeanUtil.copyProperties(item, sysJob);
                        if ((String.valueOf(sysJob.getJobId())).equals(e.getJobId())) {
                            if (String.valueOf(e.getType()).equals(WcsSmartLightRunStatusEnum.MULTI.getCode())) {
                                sysJob.setStatus(WcsTimerConstants.JOB_CLOSE_STATUS);
                            } else {
                                sysJob.setStatus(WcsTimerConstants.JOB_START_STATUS);
                            }
                            if (String.valueOf(e.getType()).equals(WcsSmartLightRunStatusEnum.SINGLE.getCode())) {
                                sysJob.setStatus(WcsTimerConstants.JOB_START_STATUS);
                            } else {
                                sysJob.setStatus(WcsTimerConstants.JOB_CLOSE_STATUS);
                            }
                            updateJobs.add(sysJob);
                        }
                    }
                });
            }
            if (CollUtil.isNotEmpty(updateJobs)) {
                updateJobs.forEach(e -> {
                    try {
                        sysJobService.changeStatus(e);
                    } catch (SchedulerException ex) {
                        log.error(ex.getMessage());
                    }
                });
            }
            return true;
        }
        return false;
    }
}
