package com.dmm.middleware.dynamic.thread.pool.sdk.domain;

import com.alibaba.fastjson.JSON;
import com.dmm.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Mean
 * @date 2025/1/4 16:23
 * @description 动态线程池服务
 */

public class DynamicThreadPoolService implements IDynamicThreadPoolService{

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);

    private final String applicationName;

    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    public DynamicThreadPoolService(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    /**
     * 获取全部线程池状态信息
     * @return
     */
    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolConfigList() {
        Set<String> threadPoolBeanNames = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolVOS = new ArrayList<>(threadPoolExecutorMap.size());
        for (String threadPoolBeanName : threadPoolBeanNames) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolBeanName);
            ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, threadPoolBeanName);
            threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
            threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
            threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
            threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getTypeName());
            threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
            threadPoolConfigVO.setRemainingCapacity(threadPoolConfigVO.getRemainingCapacity());
            threadPoolVOS.add(threadPoolConfigVO);
        }
        return threadPoolVOS;
    }

    /**
     * 通过名字获取线程池信息
     * @param threadPoolName
     * @return
     */
    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (null == threadPoolExecutor) return new ThreadPoolConfigEntity(applicationName, threadPoolName);
        ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, threadPoolName);
        threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
        threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getTypeName());
        threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
        threadPoolConfigVO.setRemainingCapacity(threadPoolConfigVO.getRemainingCapacity());
        if (logger.isDebugEnabled()) {
            logger.info("动态线程池，配置查询 应用名:{} 线程名:{} 池化配置:{}", applicationName, threadPoolName, JSON.toJSONString(threadPoolConfigVO));
        }
        return threadPoolConfigVO;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        if (null == threadPoolConfigEntity || !applicationName.equals(threadPoolConfigEntity.getAppName())) return;
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        if (null == threadPoolExecutor) return;
        // 配置参数 【核心线程数和最大线程数】
        threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
    }
}
