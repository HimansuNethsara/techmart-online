package com.techmart.metrics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class PerformanceMetrics {

    private static final PerformanceMetrics INSTANCE = new PerformanceMetrics();
    private static final int MAX_RECENT_REQUESTS = 25;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalResponseTimeMs = new AtomicLong(0);
    private final AtomicLong slowestRequestMs = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    private final Map<String, EndpointStats> endpointStats = new ConcurrentHashMap<>();
    private final LinkedList<RequestLogEntry> recentRequests = new LinkedList<>();

    private PerformanceMetrics() {
    }

    public static PerformanceMetrics getInstance() {
        return INSTANCE;
    }

    public void recordRequest(String endpoint, long durationMs, int statusCode) {
        totalRequests.incrementAndGet();
        totalResponseTimeMs.addAndGet(durationMs);
        slowestRequestMs.accumulateAndGet(durationMs, Math::max);

        if (statusCode >= 400) {
            errorCount.incrementAndGet();
        }

        endpointStats
                .computeIfAbsent(endpoint, k -> new EndpointStats())
                .record(durationMs);

        synchronized (recentRequests) {
            recentRequests.addFirst(new RequestLogEntry(
                    LocalDateTime.now().format(TIME_FORMAT), endpoint, durationMs, statusCode));
            while (recentRequests.size() > MAX_RECENT_REQUESTS) {
                recentRequests.removeLast();
            }
        }
    }

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public double getAverageResponseTimeMs() {
        long count = totalRequests.get();
        return count == 0 ? 0.0 : (double) totalResponseTimeMs.get() / count;
    }

    public long getSlowestRequestMs() {
        return slowestRequestMs.get();
    }

    public long getErrorCount() {
        return errorCount.get();
    }

    public Map<String, EndpointStats> getEndpointStats() {
        return endpointStats;
    }

    public List<RequestLogEntry> getRecentRequests() {
        synchronized (recentRequests) {
            return new LinkedList<>(recentRequests);
        }
    }

    public static class EndpointStats {
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicLong totalTimeMs = new AtomicLong(0);
        private final AtomicLong maxTimeMs = new AtomicLong(0);

        void record(long durationMs) {
            count.incrementAndGet();
            totalTimeMs.addAndGet(durationMs);
            maxTimeMs.accumulateAndGet(durationMs, Math::max);
        }

        public long getCount() {
            return count.get();
        }

        public double getAverageMs() {
            long c = count.get();
            return c == 0 ? 0.0 : (double) totalTimeMs.get() / c;
        }

        public long getMaxMs() {
            return maxTimeMs.get();
        }
    }

    public static class RequestLogEntry {
        public final String time;
        public final String endpoint;
        public final long durationMs;
        public final int statusCode;

        RequestLogEntry(String time, String endpoint, long durationMs, int statusCode) {
            this.time = time;
            this.endpoint = endpoint;
            this.durationMs = durationMs;
            this.statusCode = statusCode;
        }
    }
}