package io.github.howiefh.spock.config;

import com.github.fppt.jedismock.RedisServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fenghao
 * @version 1.0
 * @since 2024/1/7
 */
@Configuration
public class RedisMockConfiguration {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RedisServer redisServer() {
        System.out.println("redis start");
        return RedisServer.newRedisServer(6379);
    }
}
