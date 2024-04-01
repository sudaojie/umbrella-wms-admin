package com.ruoyi.wms.utils.socket;

import lombok.Data;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * 设备传感器数据流对象
 */
@Data
public class DeviceDataStream {


    private DataInputStream input;

    private DataOutputStream out;
}
