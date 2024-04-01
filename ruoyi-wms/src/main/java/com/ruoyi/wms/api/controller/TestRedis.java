package com.ruoyi.wms.api.controller;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Anonymous
@RestController
@RequestMapping("/test/redis")
public class TestRedis {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LocationService locationService;

    @Autowired
    private RedisTemplate redisTemplate2;

    @Autowired
    private RedisCache redisCache;


    @GetMapping("/stringRedis/{id}")
    public AjaxResult stringRedis(@PathVariable("id") String id){

        //缓存穿透
//        Location location = queryWithPassThrough(id);

        //互斥锁解决缓存击穿
        Location location = queryWithMutex(id);

        if(location == null){
            return AjaxResult.error("库位不存在");
        }

        return AjaxResult.success(location);
    }

    //缓存穿透
    private Location queryWithPassThrough(String id){
        String string_key = "String:"+ id;

        //1.从redis读取数据
        String redisValue = redisCache.getCacheObject(string_key);

        //2.判断redis是否击中该数据
        if(StrUtil.isNotBlank(redisValue)){
            return JSONUtil.toBean(redisValue, Location.class);
        }
        //判断是否是空值
        if(null != redisValue){
            return null;
        }
        Location location = locationService.getBaseMapper().selectById(id);
        if(location == null){
            redisCache.setCacheObject(string_key,"",30000,TimeUnit.MINUTES);
            return null;
        }
        redisCache.setCacheObject(string_key,JSONUtil.toJsonStr(location),30000,TimeUnit.MINUTES);
        return  location;
    }

    //互斥锁解决缓存击穿
    private Location queryWithMutex(String id){
        String string_key = "String:"+ id;

        //1.从redis读取数据
        String redisValue = redisCache.getCacheObject(string_key);

        //2.判断redis是否击中该数据
        if(StrUtil.isNotBlank(redisValue)){
            //存在直接返回
            return JSONUtil.toBean(redisValue, Location.class);
        }
        //判断命中是否是空值
        if(null != redisValue){
            return null;
        }
        //4.实现缓存重建
        //4.1 获取互斥锁
        String cache_lock = "lock:key:"+id;
        Location location = null;
        try {
            boolean trylock = trylock(cache_lock);
            //4.2 判断是否获取成功
            if(!trylock){
                //4.3 失败，则失眠并重试
                Thread.sleep(500);
                queryWithMutex(id);
            }
            //获取锁成功再次检查redis缓存是否存在
            String redisValue2 = redisCache.getCacheObject(string_key);
            if(StrUtil.isNotBlank(redisValue2)){
                //存在直接返回
                return JSONUtil.toBean(redisValue, Location.class);
            }
            //4.4 成功，根据id查询数据库
            location = locationService.getBaseMapper().selectById(id);
            //5. 不存在，返回错误数据
            if(location == null){
                //讲空值写入redis中
                redisCache.setCacheObject(string_key,"",30000,TimeUnit.MINUTES);
                //返回错误数据
                return null;
            }
            //6. 存在，写入redis
            redisCache.setCacheObject(string_key,JSONUtil.toJsonStr(location),30000,TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw  new RuntimeException(e);
        } finally {
            //7. 释放互斥锁
            unlock(cache_lock);
        }
        //8.返回数据
        return  location;
    }

    //添加锁
    private  boolean trylock(String key){
        Boolean ifAbsent = redisTemplate2.opsForValue().setIfAbsent(key, "1", 10L, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(ifAbsent);
    }

    //释放锁
    private void unlock(String key){
        Boolean delete = redisTemplate2.delete(key);
    }

}
