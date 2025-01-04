package com.dmm.middleware.dynamic.thread.pool.sdk.tigger.job;

import com.alibaba.fastjson.JSON;
import com.dmm.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import com.dmm.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.dmm.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * @author Mean
 * @date 2025/1/4 18:29
 * @description 线程池数据上报任务
 */
public class ThreadPoolDataReportJob {

	private final Logger logger = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);

	private final IDynamicThreadPoolService dynamicThreadPoolService;

	private final IRegistry registry;


	public ThreadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
		this.dynamicThreadPoolService = dynamicThreadPoolService;
		this.registry = registry;
	}

	/**
	 * 定时任务，扫描线程池配置
	 */
	@Scheduled(cron = "0/20 * * * * ?")
	public void execReportThreadPoolList() {
		List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolConfigList();
		registry.reportThreadPool(threadPoolConfigEntities);
		logger.info("动态线程池，上报线程池信息：{}", JSON.toJSONString(threadPoolConfigEntities));

		for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
			registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
			logger.info("动态线程池，上报线程池配置参数：{}", JSON.toJSONString(threadPoolConfigEntity));
		}
	}
}
