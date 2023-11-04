package io.collective;

import java.time.Clock;
import java.time.Instant;

public class SimpleAgedCache {
    private static final int MAX_ENTRIES = 10;
    private ExpirableEntry[] entries = new ExpirableEntry[MAX_ENTRIES];
    private Clock clock = Clock.systemDefaultZone();

    public SimpleAgedCache(Clock clock) {
        this.clock = clock;
    }

    public SimpleAgedCache() {
    }

    public void put(Object key, Object value, int retentionInMillis) {
        for (int i = 0; i < entries.length; i ++)
            if (entries[i] == null) {
                entries[i] = new ExpirableEntry(key, value, retentionInMillis);
                break;
            }
    }


    public boolean isEmpty() {
        removeExpired();
        int counter = 0;
        for (int i = 0; i < entries.length; i ++)
            if (entries[i] != null)
                counter ++;
        return counter == 0;
    }

    public int size() {
        removeExpired();
        int counter = 0;
        for (int i = 0; i < entries.length; i ++)
            if (entries[i] != null)
                counter ++;
        return counter;
    }

    public Object get(Object key) {
        removeExpired();
        for (int i = 0; i < entries.length; i ++)
            if ((entries[i] != null) && (entries[i].key == key))
                return entries[i].value;
        return null;
    }

    public void removeExpired() {
        Instant currentInstant = clock.instant();
        for (int i = 0; i < entries.length; i ++) {
            if ((entries[i] != null) && (entries[i].expirationInstant.isBefore(currentInstant)))
                entries[i] = null;
        }
    }

    private class ExpirableEntry {
        private Object key;
        private Object value;
        private Instant expirationInstant;

        private ExpirableEntry(Object key, Object value, int retentionInMillis) {
            this.key = key;
            this.value = value;
            this.expirationInstant = clock.instant().plusMillis(retentionInMillis);
        }
    }
}
