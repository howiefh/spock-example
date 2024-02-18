/*
 * @(#)RedisLockService 1.0 2024/1/7
 *
 * Copyright 2024 Feng Hao.
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
package io.github.howiefh.spock.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.util.concurrent.locks.Lock;

/**
 * redis 锁服务
 *
 * @author fenghao
 * @version 1.0
 * @since 2024/1/7
 */
@Slf4j
public class RedisLockService {
    public static final long SIXTY_EXPIRE_SECONDS = 60L;

    private final RedisLockRegistry redisLockRegistry;

    public RedisLockService(RedisLockRegistry redisLockRegistry) {
        this.redisLockRegistry = redisLockRegistry;
    }

    public Lock getLock(String lockKey) {
        return redisLockRegistry.obtain(lockKey);
    }
}
