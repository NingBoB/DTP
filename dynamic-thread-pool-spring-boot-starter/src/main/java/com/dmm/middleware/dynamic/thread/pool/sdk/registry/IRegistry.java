package com.dmm.middleware.dynamic.thread.pool.sdk.registry;

import com.dmm.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @author Mean
 * @date 2025/1/4 16:59
 * @description 注册中心接口，具体可以由redis、zk、mysql等实现
 */
public interface IRegistry {

	/**
	 * 上报线程池
	 * @param threadPoolConfigEntities
	 */
	void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities);

	/**
	 * 上报线程池配置参数
	 * @param threadPoolConfigEntity
	 */
	void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);

}
