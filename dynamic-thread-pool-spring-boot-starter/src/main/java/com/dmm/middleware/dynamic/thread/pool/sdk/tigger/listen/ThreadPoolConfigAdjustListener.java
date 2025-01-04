package com.dmm.middleware.dynamic.thread.pool.sdk.tigger.listen;

import com.alibaba.fastjson.JSON;
import com.dmm.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import com.dmm.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.dmm.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import com.dmm.middleware.dynamic.thread.pool.sdk.tigger.job.ThreadPoolDataReportJob;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * @author Mean
 * @date 2025/1/4 18:30
 * @description 动态线程池变更监听
 */
public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {

	private final Logger logger = LoggerFactory.getLogger(ThreadPoolConfigAdjustListener.class);

	private final IDynamicThreadPoolService service;

	private final IRegistry registry;

	public ThreadPoolConfigAdjustListener(IDynamicThreadPoolService service, IRegistry registry) {
		this.service = service;
		this.registry = registry;
	}

	/**
	 * 监听线程池配置修改事件，并完成更新
	 * @param charSequence
	 * @param threadPoolConfigEntity
	 */
	@Override
	public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
		logger.info("监听到线程池变更，变更内容：{}", JSON.toJSONString(threadPoolConfigEntity));
		// 修改线程池
		service.updateThreadPoolConfig(threadPoolConfigEntity);
		
		// 更新后上报最新的数据
		List<ThreadPoolConfigEntity> threadPoolConfigEntities = service.queryThreadPoolConfigList();
		registry.reportThreadPool(threadPoolConfigEntities);

		ThreadPoolConfigEntity threadPoolConfigCurrent = service.queryThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
		registry.reportThreadPoolConfigParameter(threadPoolConfigCurrent);

		logger.info("动态线程池，上报线程池信息：{}", JSON.toJSONString(threadPoolConfigEntities));
	}
}
