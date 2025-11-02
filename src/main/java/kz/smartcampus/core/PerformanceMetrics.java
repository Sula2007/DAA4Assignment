package kz.smartcampus.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Performance tracking system for algorithm operations.
 * Tracks operation counts and execution times for analysis.
 */
public class PerformanceMetrics {
    private final Map<String, Long> counters;
    private long startTime;
    private long endTime;
    private boolean timerRunning;
    
    public PerformanceMetrics() {
        this.counters = new HashMap<>();
        this.timerRunning = false;
    }
    
    /**
     * Starts the execution timer
     */
    public void startTimer() {
        startTime = System.nanoTime();
        timerRunning = true;
    }
    
    /**
     * Stops the execution timer
     */
    public void stopTimer() {
        if (timerRunning) {
            endTime = System.nanoTime();
            timerRunning = false;
        }
    }
    
    /**
     * Returns execution time in milliseconds
     */
    public double getExecutionTimeMs() {
        if (timerRunning) {
            return (System.nanoTime() - startTime) / 1_000_000.0;
        }
        return (endTime - startTime) / 1_000_000.0;
    }
    
    /**
     * Returns execution time in nanoseconds
     */
    public long getExecutionTimeNanos() {
        if (timerRunning) {
            return System.nanoTime() - startTime;
        }
        return endTime - startTime;
    }
    
    /**
     * Increments a named counter
     */
    public void increment(String counterName) {
        counters.put(counterName, counters.getOrDefault(counterName, 0L) + 1);
    }
    
    /**
     * Adds a value to a named counter
     */
    public void add(String counterName, long value) {
        counters.put(counterName, counters.getOrDefault(counterName, 0L) + value);
    }
    
    /**
     * Gets the value of a counter
     */
    public long getCounter(String counterName) {
        return counters.getOrDefault(counterName, 0L);
    }
    
    /**
     * Resets all counters and timer
     */
    public void reset() {
        counters.clear();
        startTime = 0;
        endTime = 0;
        timerRunning = false;
    }
    
    /**
     * Returns all counter names
     */
    public Map<String, Long> getAllCounters() {
        return new HashMap<>(counters);
    }
    
    /**
     * Calculates total operations across all counters
     */
    public long getTotalOperations() {
        return counters.values().stream().mapToLong(Long::longValue).sum();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Performance Metrics:\n");
        sb.append(String.format("  Execution Time: %.3f ms\n", getExecutionTimeMs()));
        sb.append("  Operations:\n");
        
        counters.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> 
                sb.append(String.format("    %s: %d\n", entry.getKey(), entry.getValue()))
            );
        
        sb.append(String.format("  Total Operations: %d\n", getTotalOperations()));
        
        return sb.toString();
    }
    
    /**
     * Creates a formatted summary for reports
     */
    public String getFormattedSummary() {
        return String.format("Time: %.3fms | Ops: %d", 
            getExecutionTimeMs(), 
            getTotalOperations()
        );
    }
}
