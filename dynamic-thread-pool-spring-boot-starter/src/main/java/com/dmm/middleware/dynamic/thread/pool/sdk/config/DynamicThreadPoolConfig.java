package com.dmm.middleware.dynamic.thread.pool.sdk.config;

import com.dmm.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import jodd.util.StringUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

/**
 * @author Mean
 * @date 2025/1/3 22:26
 * @description 动态配置的入口
 */
@Configuration
public class DynamicThreadPoolConfig {

    private final Logger logger = Logger.getLogger(DynamicThreadPoolConfig.class.getName());

    // 将线程池注入服务，返回服务对象
    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutors){
        // 获取应用信息
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StringUtil.isBlank(applicationName)) {
            applicationName = "缺省的";
            logger.warning("动态线程池,启动提示: spring.application.name 为空，请检查配置文件");
        }
        return new DynamicThreadPoolService(applicationName, threadPoolExecutors);
    }


}
