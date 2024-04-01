package com.ruoyi.wms.utils;

import com.ruoyi.common.core.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流水码工具类
 *
 * @author ruoyi
 */
@Component
public class SerialCodeUtils {

    @Autowired
    private RedisCache redisCache;

    /**
     *  获取入库、出库、上架单号\移库单号/下架单号
     *  格式：prefix+20230201121212
     * @param prefix 单号前缀
     * @return
     */
    public String getOrderNo(String prefix){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return prefix + sdf.format(new Date());
    }

    /**
     *  获取批次号
     *  格式：20230201+两位流水码
     * @return
     */
    public String getCharg(){
        String charg = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String format = sdf.format(new Date());
        String key = "charg" + format;
        Object cacheObject = redisCache.getCacheObject(key);
        if (cacheObject != null){
            Integer integer = Integer.valueOf(cacheObject.toString())+1;
            redisCache.setCacheObject(key,integer,1, TimeUnit.DAYS);
            charg = format + String.format("%02d", integer);
        }else{
            redisCache.setCacheObject(key,1,1, TimeUnit.DAYS);
            charg = format + "01";
        }
        return charg;
    }

    /**
     *  获取唯一码
     *  格式：inBillCode+四位流水码
     * @return
     */
    public String getOnlyCode(String inBillCode){
        String onlyCode = "";
        String key = inBillCode;
        Object cacheObject = redisCache.getCacheObject(key);
        if (cacheObject != null){
            Integer integer = Integer.valueOf(cacheObject.toString())+1;
            redisCache.setCacheObject(key,integer);
            onlyCode = inBillCode + String.format("%04d", integer);
        }else{
            redisCache.setCacheObject(key,1);
            onlyCode = inBillCode + "0001";
        }
        return onlyCode;
    }

    private static final String SERIAL_NUMBER_PREFIX = "000";
    private static AtomicInteger counter = new AtomicInteger(0);

    // 生成下一个唯一机件号
    public synchronized String getPartsCode(String prefix) {
        // 获取递增后的计数器并格式化为四位数
        String formattedCounter = String.format("%04d", counter.incrementAndGet());
        // 获取当前时间戳的后四位，并格式化为四位数
        String timestamp = String.format("%04d", System.currentTimeMillis() % 10000);
        // 将计数器和时间戳添加到固定前缀中，生成唯一序列号
        String uniqueSerialNumber = SERIAL_NUMBER_PREFIX + timestamp + formattedCounter;
        return prefix + uniqueSerialNumber; // 将唯一序列号添加到固定前缀中
    }
}
