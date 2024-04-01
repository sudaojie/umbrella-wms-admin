package com.ruoyi.wms.api.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.enums.wcs.WcsTaskRunStatusEnum;
import com.ruoyi.wcs.mapper.WcsOperateTaskMapper;
import com.ruoyi.wcs.req.ConveyorLineCommon;
import com.ruoyi.wms.api.dto.*;
import com.ruoyi.wms.api.service.WmsApiService;
import com.ruoyi.wms.basics.domain.AgvPutWayFinshLog;
import com.ruoyi.wms.basics.mapper.AgvPutWayFinshLogMapper;
import com.ruoyi.wms.utils.constant.CsdLocationConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * WMS对外提供服务Controller
 */
@Anonymous
@RestController
@RequestMapping("/wms/api")
public class WmsApiController extends BaseController {


    @Autowired
    private WmsApiService wmsApiService;

    @Value("${stacker.remote.api}")
    private String stackerRemoteApiUrl;

    @Autowired
    private WcsOperateTaskMapper wcsOperateTaskMapper;

    /**
     * 堆垛机到位信息号上报
     *
     * @return
     */
    @RequestMapping("/stackerPositionSignal")
    @ResponseBody
    public AjaxResult stackerPositionSignal(@RequestBody StackerPositionSignalDto stackerPositionSignalDto) {
        logger.info("获取" + stackerPositionSignalDto.getStackerId() + "号堆垛机到位信号");
        logger.info("堆垛机到位信息号上报:/wms/api/stackerPositionSignal");
        logger.info("请求参数:{}", JSON.toJSONString(stackerPositionSignalDto));
        return wmsApiService.stackerPositionSignal(stackerPositionSignalDto);
    }


    /**
     * 堆垛机报警信息上报
     *
     * @return
     */
    @RequestMapping("/stackerWarnReport")
    @ResponseBody
    public AjaxResult stackerWarnReport(@RequestBody StackerWarnReportDto stackerWarnReportDto) {
        logger.info("堆垛机报警信息上报:/wms/api/stackerWarnReport");
        logger.info("请求参数:{}", JSON.toJSONString(stackerWarnReportDto));
        return AjaxResult.success(wmsApiService.stackerWarnReport(stackerWarnReportDto));
    }


    /**
     * AGV到位信号上报
     *
     * @return
     */
    @RequestMapping("/agvPositionSignal")
    @ResponseBody
    public AjaxResult agvPositionSignal(@RequestBody AgvPositionSignalDto agvPositionSignalDto) {
        logger.info("AGV到位信号上报:/wms/api/agvPositionSignal");
        logger.info("请求参数:{}", JSON.toJSONString(agvPositionSignalDto));
        return AjaxResult.success(wmsApiService.agvPositionSignal(agvPositionSignalDto));
    }


    /**
     * 堆垛机任务开始执行的信号推送
     * @param taskNo
     * @return
     */
    @RequestMapping("/stackerStartActionSignal")
    @ResponseBody
    public AjaxResult stackerStartActionSignal(String taskNo){
        logger.info("堆垛机任务开始执行的信号推送:/wms/api/stackerStartActionSignal "+taskNo);
        return AjaxResult.success(wmsApiService.stackerStartActionSignal(taskNo));
    }


    /**
     * 给输送线请求叉货/取货
     *
     * @return
     */
    @RequestMapping(value = "/conveyorState", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map conveyorState(@RequestBody ConveyorLineCommon conveyorLineCommon) {
        String conveyorNo = conveyorLineCommon.getConveyorNo();
        logger.info("获取输送线状态:/wms/api/conveyorState");
        logger.info("请求参数:{}", conveyorNo);
        logger.info("任务号:{}", conveyorLineCommon.getTaskNo());
        Map<String, Object> map = new HashMap<>();
        Optional<String> obj = Arrays.stream(CsdLocationConstants.csdLocation).filter(str -> str.equals(conveyorNo)).findFirst();
        ////判断必须是设计好的输送线
        if (!obj.isPresent()) {
            map.put("msg", "输送带编号不存在");
            map.put("code", "0");
            return map;
        }
        //如果成功  请求堆垛机接口，输送线是否就绪
        String msg = "输送带未就绪";
        String state = "0";
        int taskType = 0;
        LambdaQueryWrapper<WcsOperateTask> taskQueryWrapper = new LambdaQueryWrapper<>();
        taskQueryWrapper.eq(WcsOperateTask::getTaskNo, conveyorLineCommon.getTaskNo());
        WcsOperateTask wcsOperateTask = wcsOperateTaskMapper.selectOne(taskQueryWrapper);
        //任务类型 （1.入库 2.出库）
        if (WmsWcsTypeEnum.TAKETRAY.getCode().equals(wcsOperateTask.getOperateType())) {
            taskType = 2;
        } else if (WmsWcsTypeEnum.PUTTRAY.getCode().equals(wcsOperateTask.getOperateType())) {
            taskType = 1;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("tfNum", conveyorNo);
        dataMap.put("taskNo", conveyorLineCommon.getTaskNo());
        dataMap.put("taskType", taskType);
        logger.info("任务状态:{}", taskType);
        try {
            logger.info("调用堆垛机的WCS系统的输送线状态接口，请求参数:{}",JSON.toJSONString(dataMap));
            String responseBody = HttpUtils.doPostByJson(stackerRemoteApiUrl + "/stackerTasks/getTransferLineReady", dataMap);
            // 处理响应
            if (responseBody != null) {
                logger.info("调用堆垛机的WCS系统的输送线状态接口返回:{}",responseBody);
                StackerWorkState cargoInfo = objectMapper.readValue(responseBody, StackerWorkState.class);
                // 在这里对响应进行处理
                logger.info("cargoInfo.getStatus():{}",cargoInfo.getStatus());
                // 在这里对响应进行处理
                //1 表示可以伸叉工作了
                if ("1".equals(cargoInfo.getStatus())) {
                    msg = "输送带已就绪";
                    state = "1";
                    wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.IN_PROGRESS.getCode());
                    wcsOperateTaskMapper.updateById(wcsOperateTask);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("msg", msg);
        map.put("code", state);
        return map;
    }

    @Autowired
    private AgvPutWayFinshLogMapper agvPutWayFinshLogMapper;

    /**
     * 告知取货/放货完成操作
     *
     * @return
     */
    @RequestMapping(value = "/agvCompleteState")
    @ResponseBody
    public AjaxResult agvCompleteState(@RequestBody ConveyorLineCommon conveyorLineCommon) {
        String conveyorNo = conveyorLineCommon.getConveyorNo();
        logger.info("告知取货/放货完成操作:/wms/api/agvCompleteState");
        logger.info("调用WCS的getAGVDone完成,请求参数:{}", JSON.toJSONString(conveyorLineCommon));
        WcsOperateTask wcsOperateTask = wcsOperateTaskMapper.selectOne(new QueryWrapper<WcsOperateTask>()
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("task_no", conveyorLineCommon.getTaskNo())
        );
        int taskType = 1;
        //任务类型 （1.入库 2.出库）
        if (WmsWcsTypeEnum.TAKETRAY.getCode().equals(wcsOperateTask.getOperateType())) {
            taskType = 2;
        } else if (WmsWcsTypeEnum.PUTTRAY.getCode().equals(wcsOperateTask.getOperateType())) {
            taskType = 1;
        }
        try {
            // 设置请求体
            Map<String, Object> dataMap = new LinkedHashMap<>();
            dataMap.put("lineNo", conveyorNo);
            dataMap.put("taskType", taskType);
            logger.info("调用WCS的getAGVDone完成,Map对象请求参数:{}", JSON.toJSONString(dataMap));
            String responseBody = HttpUtils.doPostByJson(stackerRemoteApiUrl + "/stackerTasks/getAGVDone", dataMap);
            if (responseBody != null) {
                logger.info("调用WCS的getAGVDone完成,响应参数:{}", responseBody);
                QueryWrapper<AgvPutWayFinshLog> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode());
                queryWrapper.eq("tray_code",wcsOperateTask.getTrayNo());
                queryWrapper.orderByDesc("create_time");
                queryWrapper.last("limit 1");

                AgvPutWayFinshLog search = agvPutWayFinshLogMapper.selectOne(queryWrapper);
                if(search == null){
                    AgvPutWayFinshLog agvPutWayFinshLog = new AgvPutWayFinshLog();
                    agvPutWayFinshLog.setTrayCode(wcsOperateTask.getTrayNo());
                    agvPutWayFinshLog.setAgvTaskTypeStatus("Y");
                    agvPutWayFinshLog.setLineNo(conveyorLineCommon.getConveyorNo());
                    agvPutWayFinshLogMapper.insert(agvPutWayFinshLog);
                    logger.info("AGV取放货完成日志插入成功:{}", JSON.toJSONString(agvPutWayFinshLog));
                }else{
                    search.setAgvTaskTypeStatus("Y");
                    search.setLineNo(conveyorLineCommon.getConveyorNo());
                    agvPutWayFinshLogMapper.updateById(search);
                    logger.info("AGV取放货完成日志修改成功:{}", JSON.toJSONString(search));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.success(conveyorLineCommon.getConveyorNo());
    }

    public static void main(String[] args) {
        String conveyorNo = "csd_02_04_01_30";
        // 设置请求体
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("lineNo", conveyorNo);
        dataMap.put("taskType", 1);
        String responseBody = HttpUtils.doPostByJson("http://192.168.0.246:8000/api" + "/stackerTasks/getAGVDone", dataMap);
        System.out.println(responseBody);
    }

    /**
     * 获取堆垛机io控制接口
     *
     * @return
     */
    @RequestMapping("/getStackerErrorCode")
    public AjaxResult getStackerErrorCode() {
        logger.info("AGV到位信号上报:/wms/api/getStackerErrorCode");
        String jsonString = HttpUtils.sendGet(stackerRemoteApiUrl + "/stackerTasks/getStackerErrorCode");
        ObjectMapper objectMapper = new ObjectMapper();
        List<StackerControlDto> stackerControlDtoList = new ArrayList<>();
        try {
            if (StringUtils.isNotEmpty(jsonString)) {
                // 将JSON字符串转换为集合对象
                List<Map<String, Object>> collection = objectMapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>() {
                });
                StackerControlDto tempStackerControlDto;
                // 打印集合对象
                for (Map<String, Object> obj : collection) {
                    tempStackerControlDto = new StackerControlDto();
                    tempStackerControlDto.setIoAddress(String.valueOf(obj.get("ioAddress")));
                    tempStackerControlDto.setIoName(String.valueOf(obj.get("ioName")));
                    tempStackerControlDto.setIoValue(String.valueOf(obj.get("ioValue")));
                    tempStackerControlDto.setIoAddress(String.valueOf(obj.get("ioAddress")));
                    stackerControlDtoList.add(tempStackerControlDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.success(stackerControlDtoList);
    }


    /**
     * 根据任务号获取WCS任务信息
     *
     * @return
     */
    @RequestMapping("/getWcsTaskInfoByTaskNo")
    @ResponseBody
    public AjaxResult getWcsTaskInfoByTaskNo(String taskNo) {
        logger.info("根据任务号获取WCS任务信息:/wms/api/getWcsTaskInfoByTaskNo,任务号:{}", taskNo);
        return AjaxResult.success(wmsApiService.getWcsTaskInfoByTaskNo(taskNo));
    }

    /**
     * 获取堆垛机状态信息集合
     *
     * @return
     */
    @RequestMapping("/getStackerStatusList")
    @ResponseBody
    public AjaxResult getStackerStatusList() {
        logger.info("根据任务号获取WCS任务信息:/wms/api/getStackerStatusList");
        return AjaxResult.success(wmsApiService.getStackerStatusList());
    }
}
