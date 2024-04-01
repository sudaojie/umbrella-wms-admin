package com.ruoyi.wms.utils;

import cn.hutool.core.util.IdUtil;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 烟感温湿度生成
 */
public class HumiditySmokeUtils {
    public static List<WcsDeviceBaseInfo> generate() {
        List<WcsDeviceBaseInfo> list = new ArrayList<>();
        int num = 1001;
        int num1 = 2001;
        int num2 = 3001;
        int num3 = 4001;
        for (int i = 1; i < 13; i++) {
            WcsDeviceBaseInfo wcsDeviceBaseInfo = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo.setDeviceNo("YW" + num);
            wcsDeviceBaseInfo.setDeviceName("烟感" + wcsDeviceBaseInfo.getDeviceNo());
            wcsDeviceBaseInfo.setDeviceType("4");
            wcsDeviceBaseInfo.setDeviceArea("1");
            wcsDeviceBaseInfo.setDeviceAddress("03");
            wcsDeviceBaseInfo.setTemplature("0.00");
            wcsDeviceBaseInfo.setHumidity("0.00");
            wcsDeviceBaseInfo.setEnableStatus("0");
            wcsDeviceBaseInfo.setSmokeFlag("0");
            wcsDeviceBaseInfo.setDelFlag("0");
            list.add(wcsDeviceBaseInfo);

            WcsDeviceBaseInfo wcsDeviceBaseInfo5 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo5.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo5.setDeviceNo("PW" + num);
            wcsDeviceBaseInfo5.setDeviceName("温湿度" + wcsDeviceBaseInfo5.getDeviceNo());
            wcsDeviceBaseInfo5.setDeviceType("4");
            wcsDeviceBaseInfo5.setDeviceArea("1");
            wcsDeviceBaseInfo5.setDeviceAddress("03");
            wcsDeviceBaseInfo5.setTemplature("0.00");
            wcsDeviceBaseInfo5.setHumidity("0.00");
            wcsDeviceBaseInfo5.setEnableStatus("0");
            wcsDeviceBaseInfo5.setDelFlag("0");
            list.add(wcsDeviceBaseInfo5);
            num++;


            WcsDeviceBaseInfo wcsDeviceBaseInfo1 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo1.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo1.setDeviceNo("YW" + num1);
            wcsDeviceBaseInfo1.setDeviceName("烟感" + wcsDeviceBaseInfo1.getDeviceNo());
            wcsDeviceBaseInfo1.setDeviceType("4");
            wcsDeviceBaseInfo1.setDeviceArea("1");
            wcsDeviceBaseInfo1.setDeviceAddress("03");
            wcsDeviceBaseInfo1.setTemplature("0.00");
            wcsDeviceBaseInfo1.setSmokeFlag("0");
            wcsDeviceBaseInfo1.setHumidity("0.00");
            wcsDeviceBaseInfo1.setEnableStatus("0");
            wcsDeviceBaseInfo1.setDelFlag("0");
            list.add(wcsDeviceBaseInfo1);

            WcsDeviceBaseInfo wcsDeviceBaseInfo6 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo6.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo6.setDeviceNo("PW" + num1);
            wcsDeviceBaseInfo6.setDeviceName("温湿度" + wcsDeviceBaseInfo6.getDeviceNo());
            wcsDeviceBaseInfo6.setDeviceType("4");
            wcsDeviceBaseInfo6.setDeviceArea("1");
            wcsDeviceBaseInfo6.setDeviceAddress("03");
            wcsDeviceBaseInfo6.setTemplature("0.00");
            wcsDeviceBaseInfo6.setHumidity("0.00");
            wcsDeviceBaseInfo6.setEnableStatus("0");
            wcsDeviceBaseInfo6.setDelFlag("0");
            list.add(wcsDeviceBaseInfo6);
            num1++;


            WcsDeviceBaseInfo wcsDeviceBaseInfo2 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo2.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo2.setDeviceNo("YW" + num2);
            wcsDeviceBaseInfo2.setDeviceName("烟感" + wcsDeviceBaseInfo2.getDeviceNo());
            wcsDeviceBaseInfo2.setDeviceType("4");
            wcsDeviceBaseInfo2.setDeviceArea("1");
            wcsDeviceBaseInfo2.setDeviceAddress("03");
            wcsDeviceBaseInfo2.setTemplature("0.00");
            wcsDeviceBaseInfo2.setSmokeFlag("0");
            wcsDeviceBaseInfo2.setHumidity("0.00");
            wcsDeviceBaseInfo2.setEnableStatus("0");
            wcsDeviceBaseInfo2.setDelFlag("0");
            list.add(wcsDeviceBaseInfo2);

            WcsDeviceBaseInfo wcsDeviceBaseInfo7 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo7.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo7.setDeviceNo("PW" + num2);
            wcsDeviceBaseInfo7.setDeviceName("温湿度" + wcsDeviceBaseInfo7.getDeviceNo());
            wcsDeviceBaseInfo7.setDeviceType("4");
            wcsDeviceBaseInfo7.setDeviceArea("1");
            wcsDeviceBaseInfo7.setDeviceAddress("03");
            wcsDeviceBaseInfo7.setTemplature("0.00");
            wcsDeviceBaseInfo7.setHumidity("0.00");
            wcsDeviceBaseInfo7.setEnableStatus("0");
            wcsDeviceBaseInfo7.setDelFlag("0");
            list.add(wcsDeviceBaseInfo7);
            num2++;


            WcsDeviceBaseInfo wcsDeviceBaseInfo3 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo3.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo3.setDeviceNo("YW" + num3);
            wcsDeviceBaseInfo3.setDeviceName("烟感" + wcsDeviceBaseInfo3.getDeviceNo());
            wcsDeviceBaseInfo3.setDeviceType("4");
            wcsDeviceBaseInfo3.setDeviceArea("1");
            wcsDeviceBaseInfo3.setDeviceAddress("03");
            wcsDeviceBaseInfo3.setTemplature("0.00");
            wcsDeviceBaseInfo3.setHumidity("0.00");
            wcsDeviceBaseInfo3.setEnableStatus("0");
            wcsDeviceBaseInfo3.setSmokeFlag("0");
            wcsDeviceBaseInfo3.setDelFlag("0");
            list.add(wcsDeviceBaseInfo3);

            WcsDeviceBaseInfo wcsDeviceBaseInfo8 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo8.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo8.setDeviceNo("PW" + num3);
            wcsDeviceBaseInfo8.setDeviceName("温湿度" + wcsDeviceBaseInfo8.getDeviceNo());
            wcsDeviceBaseInfo8.setDeviceType("4");
            wcsDeviceBaseInfo8.setDeviceArea("1");
            wcsDeviceBaseInfo8.setDeviceAddress("03");
            wcsDeviceBaseInfo8.setTemplature("0.00");
            wcsDeviceBaseInfo8.setHumidity("0.00");
            wcsDeviceBaseInfo8.setEnableStatus("0");
            wcsDeviceBaseInfo8.setDelFlag("0");
            list.add(wcsDeviceBaseInfo8);
            num3++;
        }
        int num4 = 5001;
        int num5 = 6001;
        int num6 = 7001;
        int num7 = 8001;
        for (int i = 1; i < 5; i++) {

            WcsDeviceBaseInfo wcsDeviceBaseInfo9 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo9.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo9.setDeviceNo("YW" + num4);
            wcsDeviceBaseInfo9.setDeviceName("烟感" + wcsDeviceBaseInfo9.getDeviceNo());
            wcsDeviceBaseInfo9.setDeviceType("4");
            wcsDeviceBaseInfo9.setDeviceArea("1");
            wcsDeviceBaseInfo9.setDeviceAddress("03");
            wcsDeviceBaseInfo9.setTemplature("0.00");
            wcsDeviceBaseInfo9.setSmokeFlag("0");
            wcsDeviceBaseInfo9.setHumidity("0.00");
            wcsDeviceBaseInfo9.setEnableStatus("0");
            wcsDeviceBaseInfo9.setDelFlag("0");
            list.add(wcsDeviceBaseInfo9);

            WcsDeviceBaseInfo wcsDeviceBaseInfo20 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo20.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo20.setDeviceNo("PW" + num4);
            wcsDeviceBaseInfo20.setDeviceName("温湿度" + wcsDeviceBaseInfo20.getDeviceNo());
            wcsDeviceBaseInfo20.setDeviceType("4");
            wcsDeviceBaseInfo20.setDeviceArea("1");
            wcsDeviceBaseInfo20.setDeviceAddress("03");
            wcsDeviceBaseInfo20.setTemplature("0.00");
            wcsDeviceBaseInfo20.setHumidity("0.00");
            wcsDeviceBaseInfo20.setEnableStatus("0");
            wcsDeviceBaseInfo20.setDelFlag("0");
            list.add(wcsDeviceBaseInfo20);
            num4++;

            WcsDeviceBaseInfo wcsDeviceBaseInfo10 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo10.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo10.setDeviceNo("YW" + num5);
            wcsDeviceBaseInfo10.setDeviceName("烟感" + wcsDeviceBaseInfo10.getDeviceNo());
            wcsDeviceBaseInfo10.setDeviceType("4");
            wcsDeviceBaseInfo10.setDeviceArea("1");
            wcsDeviceBaseInfo10.setDeviceAddress("03");
            wcsDeviceBaseInfo10.setTemplature("0.00");
            wcsDeviceBaseInfo10.setSmokeFlag("0");
            wcsDeviceBaseInfo10.setHumidity("0.00");
            wcsDeviceBaseInfo10.setEnableStatus("0");
            wcsDeviceBaseInfo10.setDelFlag("0");
            list.add(wcsDeviceBaseInfo10);

            WcsDeviceBaseInfo wcsDeviceBaseInfo21 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo21.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo21.setDeviceNo("PW" + num5);
            wcsDeviceBaseInfo21.setDeviceName("温湿度" + wcsDeviceBaseInfo21.getDeviceNo());
            wcsDeviceBaseInfo21.setDeviceType("4");
            wcsDeviceBaseInfo21.setDeviceArea("1");
            wcsDeviceBaseInfo21.setDeviceAddress("03");
            wcsDeviceBaseInfo21.setTemplature("0.00");
            wcsDeviceBaseInfo21.setSmokeFlag("0");
            wcsDeviceBaseInfo21.setHumidity("0.00");
            wcsDeviceBaseInfo21.setEnableStatus("0");
            wcsDeviceBaseInfo21.setDelFlag("0");
            list.add(wcsDeviceBaseInfo21);
            num5++;

            WcsDeviceBaseInfo wcsDeviceBaseInfo11 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo11.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo11.setDeviceNo("YW" + num6);
            wcsDeviceBaseInfo11.setDeviceName("烟感" + wcsDeviceBaseInfo11.getDeviceNo());
            wcsDeviceBaseInfo11.setDeviceType("4");
            wcsDeviceBaseInfo11.setDeviceArea("1");
            wcsDeviceBaseInfo11.setDeviceAddress("03");
            wcsDeviceBaseInfo11.setTemplature("0.00");
            wcsDeviceBaseInfo11.setSmokeFlag("0");
            wcsDeviceBaseInfo11.setHumidity("0.00");
            wcsDeviceBaseInfo11.setEnableStatus("0");
            wcsDeviceBaseInfo11.setDelFlag("0");
            list.add(wcsDeviceBaseInfo11);
            num6++;

            WcsDeviceBaseInfo wcsDeviceBaseInfo12 = new WcsDeviceBaseInfo();
            wcsDeviceBaseInfo12.setId(IdUtil.fastSimpleUUID());
            wcsDeviceBaseInfo12.setDeviceNo("YW" + num7);
            wcsDeviceBaseInfo12.setDeviceName("烟感" + wcsDeviceBaseInfo12.getDeviceNo());
            wcsDeviceBaseInfo12.setDeviceType("4");
            wcsDeviceBaseInfo12.setDeviceArea("1");
            wcsDeviceBaseInfo12.setDeviceAddress("03");
            wcsDeviceBaseInfo12.setTemplature("0.00");
            wcsDeviceBaseInfo12.setSmokeFlag("0");
            wcsDeviceBaseInfo12.setHumidity("0.00");
            wcsDeviceBaseInfo12.setEnableStatus("0");
            wcsDeviceBaseInfo12.setDelFlag("0");
            list.add(wcsDeviceBaseInfo12);
            num7++;

        }
        return list;
    }
}
