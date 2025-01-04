package com.dmm.middleware.dynamic.thread.pool.sdk.domain;

import com.dmm.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @author Mean
 * @date 2025/1/4 16:17
 * @description 动态线程池服务接口
 */
public interface IDynamicThreadPoolService {

    // 获取信息
    List<ThreadPoolConfigEntity> queryThreadPoolConfigList();

    ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName);

    // 更新信息
    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);

}
