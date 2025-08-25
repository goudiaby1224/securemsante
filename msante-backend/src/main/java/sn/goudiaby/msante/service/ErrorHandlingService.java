package sn.goudiaby.msante.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Comprehensive error handling service for Phase 4
 * Provides centralized error management, logging, and recovery mechanisms
 */
@Service
public class ErrorHandlingService {
    
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingService.class);
    
    /**
     * Execute operation with retry mechanism
     */
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public <T> T executeWithRetry(Supplier<T> operation, String operationName) {
        try {
            log.info("Executing operation: {}", operationName);
            T result = operation.get();
            log.info("Operation {} completed successfully", operationName);
            return result;
        } catch (Exception e) {
            log.error("Operation {} failed: {}", operationName, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Execute operation with timeout
     */
    public <T> T executeWithTimeout(Supplier<T> operation, long timeoutSeconds, String operationName) {
        try {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(operation);
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Operation {} timed out after {} seconds: {}", 
                     operationName, timeoutSeconds, e.getMessage());
            throw new RuntimeException("Operation timed out: " + operationName, e);
        }
    }
    
    /**
     * Execute operation with circuit breaker pattern
     */
    public <T> T executeWithCircuitBreaker(Supplier<T> operation, String operationName) {
        // Simple circuit breaker implementation
        try {
            return operation.get();
        } catch (Exception e) {
            log.error("Circuit breaker triggered for operation {}: {}", operationName, e.getMessage());
            // In a real implementation, this would track failure rates and open/close the circuit
            throw new RuntimeException("Circuit breaker open for operation: " + operationName, e);
        }
    }
    
    /**
     * Log error with context information
     */
    public void logError(String operation, String errorMessage, Throwable throwable, Object... context) {
        String contextString = context.length > 0 ? String.join(", ", 
            java.util.Arrays.stream(context).map(Object::toString).toArray(String[]::new)) : "none";
        log.error("Error in operation '{}': {} | Context: {} | Timestamp: {}", 
                 operation, errorMessage, contextString, LocalDateTime.now(), throwable);
    }
    
    /**
     * Handle database transaction errors
     */
    @Transactional
    public <T> T executeInTransaction(Supplier<T> operation, String operationName) {
        try {
            return operation.get();
        } catch (Exception e) {
            log.error("Transaction failed for operation {}: {}", operationName, e.getMessage());
            throw new RuntimeException("Transaction failed: " + operationName, e);
        }
    }
    
    /**
     * Graceful degradation - return fallback value on error
     */
    public <T> T executeWithFallback(Supplier<T> operation, T fallbackValue, String operationName) {
        try {
            return operation.get();
        } catch (Exception e) {
            log.warn("Operation {} failed, using fallback value: {}", operationName, e.getMessage());
            return fallbackValue;
        }
    }
    
    /**
     * Async execution with error handling
     */
    public <T> CompletableFuture<T> executeAsync(Supplier<T> operation, String operationName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return operation.get();
            } catch (Exception e) {
                log.error("Async operation {} failed: {}", operationName, e.getMessage());
                throw new RuntimeException("Async operation failed: " + operationName, e);
            }
        });
    }
}
