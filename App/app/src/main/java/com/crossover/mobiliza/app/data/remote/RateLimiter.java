package com.crossover.mobiliza.app.data.remote;

import android.os.SystemClock;

import androidx.collection.ArrayMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RateLimiter<TKey> {

    private final long timeoutMs;
    private Map<TKey, Long> timestamps;

    public RateLimiter(int timeout, TimeUnit timeUnit) {
        this.timestamps = new ArrayMap<>();
        this.timeoutMs = timeUnit.toMillis((long) timeout);
    }

    public synchronized boolean shouldFetch(TKey key) {
        Long lastFetched = timestamps.containsKey(key) ? timestamps.get(key) : null;
        long now = SystemClock.uptimeMillis();
        if (lastFetched == null) {
            timestamps.put(key, now);
            return true;
        }
        if (now - lastFetched > timeoutMs) {
            timestamps.put(key, now);
            return true;
        }
        return false;
    }

}