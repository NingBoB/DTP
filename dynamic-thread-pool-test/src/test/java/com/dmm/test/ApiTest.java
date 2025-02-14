package com.dmm.test;

import com.dmm.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * @author Mean
 * @date 2025/1/4 15:20
 * @description ApiTest
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

	@Resource
	private RTopic dynamicThreadPoolRedisTopic;

	@Test
	public void test_dynamicThreadPoolRedisTopic() throws InterruptedException {
		ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity("dynamic-thread-pool-test-app", "threadPoolExecutor01");
		threadPoolConfigEntity.setCorePoolSize(30);
		threadPoolConfigEntity.setMaximumPoolSize(80);
		// threadPoolConfigEntity.setCorePoolSize(30);
		// threadPoolConfigEntity.setMaximumPoolSize(20);
		dynamicThreadPoolRedisTopic.publish(threadPoolConfigEntity);

		new CountDownLatch(1).await();
	}
}
