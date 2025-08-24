package sn.goudiaby.msante.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class PerformanceConfig {

    /**
     * In-memory cache manager for development/testing
     */
    @Bean
    @Profile({"dev", "test"})
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "doctors", "patients", "appointments", "availabilities", "users"
        );
    }

    /**
     * Redis cache manager for production
     */
    @Bean
    @Profile("prod")
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)) // 30 minutes TTL
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("doctors", 
                config.entryTtl(Duration.ofHours(1))) // Doctors cache for 1 hour
            .withCacheConfiguration("patients", 
                config.entryTtl(Duration.ofHours(2))) // Patients cache for 2 hours
            .withCacheConfiguration("appointments", 
                config.entryTtl(Duration.ofMinutes(15))) // Appointments cache for 15 minutes
            .withCacheConfiguration("availabilities", 
                config.entryTtl(Duration.ofMinutes(10))) // Availabilities cache for 10 minutes
            .build();
    }
}
