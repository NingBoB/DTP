package com.dmm.middleware.dynamic.thread.pool.sdk.config;

import com.dmm.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import com.dmm.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.dmm.middleware.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import com.dmm.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import com.dmm.middleware.dynamic.thread.pool.sdk.tigger.job.ThreadPoolDataReportJob;
import com.dmm.middleware.dynamic.thread.pool.sdk.tigger.listen.ThreadPoolConfigAdjustListener;
import jodd.util.StringUtil;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author Mean
 * @date 2025/1/3 22:26
 * @description 动态配置的入口
 */
@Configuration
@EnableScheduling
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class.getName());

    private String applicationName;

    // 注册中心引入RedissonClient
    @Bean("dynamicThreadRedissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties){
        Config config = new Config();
        // 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive());

        RedissonClient redissonClient = Redisson.create(config);
        logger.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());
        return redissonClient;
    }

    // 获取注册中心对象
    @Bean
    public RedisRegistry redisRegistry(RedissonClient redissonClient){
        return new RedisRegistry(redissonClient);
    }

    // 将线程池注入服务，返回服务对象，外部通过该方法进行线程池配置
    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient){
        // 获取应用信息
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StringUtil.isBlank(applicationName)) {
            applicationName = "缺省的";
            logger.warn("动态线程池,启动提示: spring.application.name 为空，请检查配置文件");
        }

        // 将缓存数据设置到本地线程池上
        for (Map.Entry<String, ThreadPoolExecutor> entry : threadPoolExecutorMap.entrySet()) {
            // 获取缓存中的线程池配置
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + entry.getKey()).get();
            if (null == threadPoolConfigEntity) continue;
            // 同步本地线程池
            ThreadPoolExecutor threadPoolExecutor = entry.getValue();
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        }

        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }

    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(DynamicThreadPoolService dynamicThreadPoolService, RedisRegistry redisRegistry){
        return new ThreadPoolDataReportJob(dynamicThreadPoolService, redisRegistry);
    }

    @Bean
    public ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener(DynamicThreadPoolService service, RedisRegistry redisRegistry){
        return new ThreadPoolConfigAdjustListener(service, redisRegistry);
    }

    @Bean("dynamicThreadPoolRedisTopic")
    public RTopic threadPoolConfigAdjustListener(RedissonClient redissonClient, ThreadPoolConfigAdjustListener listener, RedisRegistry redisRegistry){
        RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + applicationName);
        topic.addListener(ThreadPoolConfigEntity.class, listener);
        // 测试用，实际可以不用返回，消息发布操作由redis自己实现
        return topic;
    }

}
