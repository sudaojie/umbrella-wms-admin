package com.ruoyi.wms.utils;

import java.text.DecimalFormat;

/**
 * 数据转换工具类
 */
public class FormatDataUtils {

    // 截取小数点后两位
    public static String getRateStr(String rateStr) {
        int i = rateStr.indexOf(".");
        if(i != -1){
            //获取小数点的位置
            int num = 0;
            num = rateStr.indexOf(".");
            //获取小数点后面的数字 是否有两位 不足两位补足两位
            String dianAfter = rateStr.substring(0,num+1);
            String afterData = rateStr.replace(dianAfter, "");
            if(afterData.length() < 2){
                afterData = afterData + "0" ;
            }
            return rateStr.substring(0,num) + "." + afterData.substring(0,2);
        }else {
            rateStr = rateStr + ".00" ;
            return rateStr;
        }
    }

}
