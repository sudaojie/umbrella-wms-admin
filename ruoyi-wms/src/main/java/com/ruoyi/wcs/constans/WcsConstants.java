package com.ruoyi.wcs.constans;

import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wms.wcstask.domain.WmsWcsCallbackInfo;

import java.util.concurrent.LinkedBlockingQueue;

public class WcsConstants {

    public static LinkedBlockingQueue<WmsWcsInfo> stackerCallBackQueue = new LinkedBlockingQueue<>();


    public static LinkedBlockingQueue<WmsWcsInfo> agvCallBackQueue = new LinkedBlockingQueue<>();

    public static LinkedBlockingQueue<WcsOperateTask> wcsOperateTaskQueue = new LinkedBlockingQueue<>();

    public static LinkedBlockingQueue<WmsWcsCallbackInfo> wcsCallbackInfoQueue = new LinkedBlockingQueue<>();

}
