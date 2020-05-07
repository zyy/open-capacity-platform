package com.open.capacity.client.config;

/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * 程序名 : GatewayRedisAutoConfiguration
 * 建立日期: 2018-09-09
 * 作者 : someday
 * 备注 : Spring Data Redis配合Lettuce客户端构建RedisClusterClient之自适应拓扑刷新（Adaptive updates与定时拓扑刷新（Periodic updates）
 * <p>
 * 修改历史
 * 序号 	       日期 		        修改人 		         修改原因
 */
@Configuration
@AutoConfigureAfter(RedisReactiveAutoConfiguration.class)
@AutoConfigureBefore(GatewayAutoConfiguration.class)
@ConditionalOnClass({ RedisTemplate.class, DispatcherHandler.class })
public class GatewayRedisAutoConfiguration {

	@Bean
	@SuppressWarnings("unchecked")
	public RedisScript redisRequestRateLimiterScript() {
		DefaultRedisScript redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(
				new ClassPathResource("META-INF/scripts/request_rate_limiter.lua")));
		redisScript.setResultType(List.class);
		return redisScript;
	}

	@Bean
	// TODO: replace with ReactiveStringRedisTemplate in future
	public ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate(
			ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		RedisSerializer<String> serializer = new StringRedisSerializer();
		RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
				.<String, String>newSerializationContext().key(serializer)
				.value(serializer).hashKey(serializer).hashValue(serializer).build();
		return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory,
				serializationContext);
	}

	@Bean
	@ConditionalOnMissingBean
	public RedisRateLimiter redisRateLimiter(
			ReactiveRedisTemplate<String, String> redisTemplate,
			@Qualifier(RedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript,
			Validator validator) {
		return new RedisRateLimiter(redisTemplate, redisScript, validator);
	}

}
