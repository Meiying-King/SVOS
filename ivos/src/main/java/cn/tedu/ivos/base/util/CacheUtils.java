package cn.tedu.ivos.base.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheUtils {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //redis 的 set key  value命令  设置缓存
    public void set(String key, Object value){
        redisTemplate.opsForValue().set(key,value);
    }

    //redis 的 get key 命令  获取缓存
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    //redis 的del key 命令  删除缓存
   public void delete(String key){
       redisTemplate.delete(key);
   }

    //redis 的 set key timeout value命令  设置缓存,自动在 timeout 时间后失效
   public void set(String key, Object value, long timeout){
        redisTemplate.opsForValue().set(key,value,timeout);
   }


}
