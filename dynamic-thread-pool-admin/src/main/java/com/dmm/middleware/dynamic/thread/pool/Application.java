package com.dmm.middleware.dynamic.thread.pool;

import com.dmm.middleware.dynamic.thread.pool.config.RedisClientConfigProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Mean
 * @date 2025/1/4 21:07
 * @description Application
 */
@SpringBootApplication
@Configurable
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean("redissonClient")
	public RedissonClient redissonClient(RedisClientConfigProperties properties) {
		Config config = new Config();
		// 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
		config.setCodec(JsonJacksonCodec.INSTANCE);
		config.useSingleServer()
				.setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
				.setConnectionPoolSize(properties.getPoolSize())
				.setConnectionMinimumIdleSize(properties.getMinIdleSize())
				.setIdleConnectionTimeout(properties.getIdleTimeout())
				.setConnectTimeout(properties.getConnectTimeout())
				.setRetryAttempts(properties.getRetryAttempts())
				.setRetryInterval(properties.getRetryInterval())
				.setPingConnectionInterval(properties.getPingInterval())
				.setKeepAlive(properties.isKeepAlive());
		return Redisson.create(config);
	}


}
