package sn.goudiaby.msante.service;

import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class ResilienceService {

    /**
     * Retry mechanism for database operations
     */
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public <T> T executeWithRetry(Supplier<T> operation) {
        return operation.get();
    }

    /**
     * Async execution with timeout
     */
    public <T> CompletableFuture<T> executeAsync(Supplier<T> operation, long timeoutSeconds) {
        return CompletableFuture.supplyAsync(operation)
            .orTimeout(timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Circuit breaker pattern implementation
     */
    public <T> T executeWithCircuitBreaker(Supplier<T> operation, Supplier<T> fallback) {
        try {
            return operation.get();
        } catch (Exception e) {
            // Log the error
            System.err.println("Primary operation failed, using fallback: " + e.getMessage());
            return fallback.get();
        }
    }

    /**
     * Bulk operation with batching
     */
    public <T> void executeBulkOperation(Iterable<T> items, int batchSize, 
                                        java.util.function.Consumer<java.util.List<T>> batchProcessor) {
        java.util.List<T> batch = new java.util.ArrayList<>();
        for (T item : items) {
            batch.add(item);
            if (batch.size() >= batchSize) {
                batchProcessor.accept(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            batchProcessor.accept(batch);
        }
    }

    /**
     * Health check for service dependencies
     */
    public boolean isServiceHealthy(String serviceName) {
        // Implement actual health check logic
        // For now, return true
        return true;
    }

    /**
     * Graceful degradation for non-critical features
     */
    public <T> T executeWithGracefulDegradation(Supplier<T> primaryOperation, 
                                               Supplier<T> fallbackOperation,
                                               boolean isCritical) {
        try {
            return primaryOperation.get();
        } catch (Exception e) {
            if (isCritical) {
                throw new RuntimeException("Critical operation failed: " + e.getMessage(), e);
            } else {
                System.err.println("Non-critical operation failed, using fallback: " + e.getMessage());
                return fallbackOperation.get();
            }
        }
    }
}
