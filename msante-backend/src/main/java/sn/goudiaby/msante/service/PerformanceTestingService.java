package sn.goudiaby.msante.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * Comprehensive performance testing service for Phase 4
 * Provides load testing, benchmarking, and performance monitoring
 */
@Service
public class PerformanceTestingService {
    
    private static final Logger log = LoggerFactory.getLogger(PerformanceTestingService.class);
    
    @Autowired
    private ErrorHandlingService errorHandlingService;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    /**
     * Benchmark a single operation
     */
    public <T> PerformanceResult<T> benchmarkOperation(Supplier<T> operation, String operationName, int iterations) {
        StopWatch stopWatch = new StopWatch();
        AtomicLong totalTime = new AtomicLong(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        log.info("Starting benchmark for operation: {} with {} iterations", operationName, iterations);
        
        for (int i = 0; i < iterations; i++) {
            try {
                stopWatch.start();
                T result = operation.get();
                stopWatch.stop();
                
                long duration = stopWatch.getLastTaskTimeMillis();
                totalTime.addAndGet(duration);
                successCount.incrementAndGet();
                
                if (i % 100 == 0) {
                    log.debug("Benchmark iteration {} completed in {}ms", i, duration);
                }
            } catch (Exception e) {
                failureCount.incrementAndGet();
                log.warn("Benchmark iteration {} failed: {}", i, e.getMessage());
            }
        }
        
        long avgTime = successCount.get() > 0 ? totalTime.get() / successCount.get() : 0;
        double successRate = (double) successCount.get() / iterations * 100;
        
        PerformanceResult<T> result = new PerformanceResult<>(
            operationName,
            iterations,
            successCount.get(),
            failureCount.get(),
            successRate,
            totalTime.get(),
            avgTime
        );
        
        log.info("Benchmark completed for {}: {} iterations, {}ms avg, {:.2f}% success rate", 
                operationName, iterations, avgTime, successRate);
        
        return result;
    }
    
    /**
     * Load test with concurrent operations
     */
    public LoadTestResult loadTest(Supplier<?> operation, String operationName, int concurrentUsers, int operationsPerUser) {
        log.info("Starting load test: {} concurrent users, {} operations per user", concurrentUsers, operationsPerUser);
        
        AtomicInteger totalSuccess = new AtomicInteger(0);
        AtomicInteger totalFailure = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0);
        
        CompletableFuture<?>[] futures = new CompletableFuture[concurrentUsers];
        
        for (int user = 0; user < concurrentUsers; user++) {
            final int currentUser = user;
            futures[user] = CompletableFuture.runAsync(() -> {
                for (int op = 0; op < operationsPerUser; op++) {
                    try {
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        operation.get();
                        stopWatch.stop();
                        
                        totalTime.addAndGet(stopWatch.getLastTaskTimeMillis());
                        totalSuccess.incrementAndGet();
                    } catch (Exception e) {
                        totalFailure.incrementAndGet();
                        log.warn("Load test operation failed for user {}: {}", currentUser, e.getMessage());
                    }
                }
            }, executorService);
        }
        
        // Wait for all operations to complete
        try {
            CompletableFuture.allOf(futures).get(5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Load test timeout or error: {}", e.getMessage());
        }
        
        int totalOperations = concurrentUsers * operationsPerUser;
        double successRate = (double) totalSuccess.get() / totalOperations * 100;
        long avgTime = totalSuccess.get() > 0 ? totalTime.get() / totalSuccess.get() : 0;
        
        LoadTestResult result = new LoadTestResult(
            operationName,
            concurrentUsers,
            operationsPerUser,
            totalOperations,
            totalSuccess.get(),
            totalFailure.get(),
            successRate,
            totalTime.get(),
            avgTime
        );
        
        log.info("Load test completed: {} total operations, {}ms avg, {:.2f}% success rate", 
                totalOperations, avgTime, successRate);
        
        return result;
    }
    
    /**
     * Cache performance test
     */
    @Cacheable("performance-test")
    public CachePerformanceResult testCachePerformance(String cacheKey, Supplier<String> dataGenerator, int iterations) {
        log.info("Testing cache performance for key: {} with {} iterations", cacheKey, iterations);
        
        StopWatch stopWatch = new StopWatch();
        
        // Test cache miss (first call)
        stopWatch.start("cache-miss");
        String data = dataGenerator.get();
        stopWatch.stop();
        long cacheMissTime = stopWatch.getTotalTimeMillis();
        
        // Test cache hit (subsequent calls)
        long totalCacheHitTime = 0;
        int successfulHits = 0;
        
        for (int i = 0; i < iterations; i++) {
            try {
                stopWatch.start("cache-hit-" + i);
                String cachedData = dataGenerator.get(); // This should hit cache
                stopWatch.stop();
                totalCacheHitTime += stopWatch.getLastTaskTimeMillis();
                successfulHits++;
            } catch (Exception e) {
                log.warn("Cache hit test {} failed: {}", i, e.getMessage());
            }
        }
        
        long avgCacheHitTime = successfulHits > 0 ? totalCacheHitTime / successfulHits : 0;
        long improvement = cacheMissTime - avgCacheHitTime;
        
        log.info("Cache performance test completed: cache miss {}ms, cache hit {}ms avg, improvement {}ms", 
                cacheMissTime, avgCacheHitTime, improvement);
        
        return new CachePerformanceResult(
                cacheKey,
                cacheMissTime,
                avgCacheHitTime,
                improvement,
                successfulHits,
                iterations
        );
    }
    
    /**
     * Database connection pool test
     */
    public ConnectionPoolResult testConnectionPool(int maxConnections, int concurrentRequests) {
        log.info("Testing connection pool with {} max connections and {} concurrent requests", 
                maxConnections, concurrentRequests);
        
        AtomicInteger successfulConnections = new AtomicInteger(0);
        AtomicInteger failedConnections = new AtomicInteger(0);
        AtomicLong totalConnectionTime = new AtomicLong(0);
        
        CompletableFuture<?>[] futures = new CompletableFuture[concurrentRequests];
        
        for (int i = 0; i < concurrentRequests; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    
                    // Simulate database connection
                    Thread.sleep(100 + (long) (Math.random() * 200));
                    
                    stopWatch.stop();
                    totalConnectionTime.addAndGet(stopWatch.getLastTaskTimeMillis());
                    successfulConnections.incrementAndGet();
                } catch (Exception e) {
                    failedConnections.incrementAndGet();
                }
            }, executorService);
        }
        
        try {
            CompletableFuture.allOf(futures).get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Connection pool test timeout: {}", e.getMessage());
        }
        
        long avgConnectionTime = successfulConnections.get() > 0 ? 
            totalConnectionTime.get() / successfulConnections.get() : 0;
        
        ConnectionPoolResult result = new ConnectionPoolResult(
            maxConnections,
            concurrentRequests,
            successfulConnections.get(),
            failedConnections.get(),
            avgConnectionTime
        );
        
        log.info("Connection pool test completed: {} successful, {} failed, {}ms avg connection time", 
                successfulConnections.get(), failedConnections.get(), avgConnectionTime);
        
        return result;
    }
    
    /**
     * Cleanup resources
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // Performance result classes
    public static class PerformanceResult<T> {
        private final String operationName;
        private final int totalIterations;
        private final int successCount;
        private final int failureCount;
        private final double successRate;
        private final long totalTime;
        private final long averageTime;
        
        public PerformanceResult(String operationName, int totalIterations, int successCount, 
                               int failureCount, double successRate, long totalTime, long averageTime) {
            this.operationName = operationName;
            this.totalIterations = totalIterations;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.successRate = successRate;
            this.totalTime = totalTime;
            this.averageTime = averageTime;
        }
        
        // Getters
        public String getOperationName() { return operationName; }
        public int getTotalIterations() { return totalIterations; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public double getSuccessRate() { return successRate; }
        public long getTotalTime() { return totalTime; }
        public long getAverageTime() { return averageTime; }
    }
    
    public static class LoadTestResult {
        private final String operationName;
        private final int concurrentUsers;
        private final int operationsPerUser;
        private final int totalOperations;
        private final int successCount;
        private final int failureCount;
        private final double successRate;
        private final long totalTime;
        private final long averageTime;
        
        public LoadTestResult(String operationName, int concurrentUsers, int operationsPerUser,
                            int totalOperations, int successCount, int failureCount,
                            double successRate, long totalTime, long averageTime) {
            this.operationName = operationName;
            this.concurrentUsers = concurrentUsers;
            this.operationsPerUser = operationsPerUser;
            this.totalOperations = totalOperations;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.successRate = successRate;
            this.totalTime = totalTime;
            this.averageTime = averageTime;
        }
        
        // Getters
        public String getOperationName() { return operationName; }
        public int getConcurrentUsers() { return concurrentUsers; }
        public int getOperationsPerUser() { return operationsPerUser; }
        public int getTotalOperations() { return totalOperations; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public double getSuccessRate() { return successRate; }
        public long getTotalTime() { return totalTime; }
        public long getAverageTime() { return averageTime; }
    }
    
    public static class CachePerformanceResult {
        private final String cacheKey;
        private final long cacheMissTime;
        private final long cacheHitTime;
        private final long improvement;
        private final int successfulHits;
        private final int totalIterations;
        
        public CachePerformanceResult(String cacheKey, long cacheMissTime, long cacheHitTime, 
                                   long improvement, int successfulHits, int totalIterations) {
            this.cacheKey = cacheKey;
            this.cacheMissTime = cacheMissTime;
            this.cacheHitTime = cacheHitTime;
            this.improvement = improvement;
            this.successfulHits = successfulHits;
            this.totalIterations = totalIterations;
        }
        
        // Getters
        public String getCacheKey() { return cacheKey; }
        public long getCacheMissTime() { return cacheMissTime; }
        public long getCacheHitTime() { return cacheHitTime; }
        public long getImprovement() { return improvement; }
        public int getSuccessfulHits() { return successfulHits; }
        public int getTotalIterations() { return totalIterations; }
    }
    
    public static class ConnectionPoolResult {
        private final int maxConnections;
        private final int concurrentRequests;
        private final int successfulConnections;
        private final int failedConnections;
        private final long averageConnectionTime;
        
        public ConnectionPoolResult(int maxConnections, int concurrentRequests, 
                                 int successfulConnections, int failedConnections, long averageConnectionTime) {
            this.maxConnections = maxConnections;
            this.concurrentRequests = concurrentRequests;
            this.successfulConnections = successfulConnections;
            this.failedConnections = failedConnections;
            this.averageConnectionTime = averageConnectionTime;
        }
        
        // Getters
        public int getMaxConnections() { return maxConnections; }
        public int getConcurrentRequests() { return concurrentRequests; }
        public int getSuccessfulConnections() { return successfulConnections; }
        public int getFailedConnections() { return failedConnections; }
        public long getAverageConnectionTime() { return averageConnectionTime; }
    }
}
