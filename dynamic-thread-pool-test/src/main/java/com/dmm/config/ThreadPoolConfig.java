package com.dmm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

/**
 * @author Mean
 * @date 2025/1/4 15:32
 * @description ThreadPoolConfig
 */
@Slf4j
@EnableAsync
@Configuration
// @EnableConfigurationProperties(ThreadPoolConfigProperties.class)    // 去扫描或者将其配置文件为Bean对象
public class ThreadPoolConfig {

    @Bean("threadPoolExecutor01")
    public ThreadPoolExecutor threadPoolExecutor01(ThreadPoolConfigProperties properties) {
        // 实例化策略
        RejectedExecutionHandler handler;
        switch (properties.getPolicy()) {
            case "AbortPolice":
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
            case "DiscardPolicy":
                handler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case "DiscardOldestPolicy":
                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            case "CallerRunsPolicy":
                handler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            default:
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }
        // 创建线程池: 核心线程数、最大线程数、线程等待存活时间（当线程数超过核心线程数的时候，这些超过的线程将等待一段时间后再被释放）、时间单位、阻塞队列、线程池工厂、超出线程池容量的策略
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(properties.getBlockQueueSize()),
                Executors.defaultThreadFactory(),
                handler);
    }

    @Bean("threadPoolExecutor02")
    public ThreadPoolExecutor threadPoolExecutor02(ThreadPoolConfigProperties properties) {
        // 实例化策略
        RejectedExecutionHandler handler;
        switch (properties.getPolicy()) {
            case "AbortPolice":
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
            case "DiscardPolicy":
                handler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case "DiscardOldestPolicy":
                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            case "CallerRunsPolicy":
                handler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            default:
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }
        // 创建线程池: 核心线程数、最大线程数、线程等待存活时间（当线程数超过核心线程数的时候，这些超过的线程将等待一段时间后再被释放）、时间单位、阻塞队列、线程池工厂、超出线程池容量的策略
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(properties.getBlockQueueSize()),
                Executors.defaultThreadFactory(),
                handler);
    }

}
