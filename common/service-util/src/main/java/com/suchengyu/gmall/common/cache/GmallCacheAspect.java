package com.suchengyu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import jodd.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * GmallCacheAspect
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-22
 * @Description:
 */
@Aspect //表示这是一个切面类
@Component
public class GmallCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 代理方法
     */
    @Around("@annotation(com.suchengyu.gmall.common.cache.GmallCache)") //切入点,加注解的方法
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        //缓存处理代码
        Object proceed = null;
        // 1. 设置缓存key
        Object[] args = point.getArgs();//通过反射获取被代理方法的参数
        String id = new String(args[0]+"");
        // 获得执行方法的注解，通过注解，判断当前方法，获得缓存前缀
        MethodSignature signature = (MethodSignature) point.getSignature();// 通过反射获得当前要执行的被代理方法信息
        Class returnType = signature.getReturnType();//被代理方法类型
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);//获取注解中的值
        String prefix = annotation.prefix();//获取key的前缀
        String suffix = annotation.suffix();//获取key的后缀
        String cacheKey = prefix + id + suffix;
        // 2. 查询缓存
        proceed =  cacheHit(cacheKey, returnType);
        if (null != proceed){
            return  proceed;
        }else {
            // 3. 设置分布式锁
            String uuid = UUID.randomUUID().toString();
            String stockLock = cacheKey + ":lock";//分布式锁
            Boolean stock = redisTemplate.opsForValue().setIfAbsent(stockLock, uuid, 120, TimeUnit.SECONDS);
            if (stock){
                // 4. 获得分布式锁才能调用目标方法查数据库
                try {
                    proceed = point.proceed();//调用目标方法
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                // 5. 查不到数据,往缓存中添加空数据,防止缓存穿透
                if (proceed == null){
                    try {
                        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(returnType.newInstance()),10,TimeUnit.SECONDS);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                // 6. 同步缓存,数据存入缓存
                redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(proceed));
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 7. 使用lua脚本删除分布式锁
                // 设置lua脚本返回的数据类型
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                // 设置lua脚本返回类型为Long
                redisScript.setResultType(Long.class);
                redisScript.setScriptText(script);
                redisTemplate.execute(redisScript, Arrays.asList(stockLock),uuid);
            }else {
                // 7. 没获得锁,睡1秒,查询缓存
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return cacheHit(cacheKey, returnType);//查询缓存
            }
        }
        return proceed;
    }

    /**
     * 查询缓存方法
     */
    public Object cacheHit(String cacheKey,Class returnType){
        Object cacheObject = null;
        String cacheValue = (String) redisTemplate.opsForValue().get(cacheKey);
        if (StringUtil.isNotBlank(cacheKey)){
           cacheObject = JSON.parseObject(cacheValue, returnType);
        }
        return  cacheObject;
    }
}
