package com.cms.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Background Concurrency Manager for Periodic Auto-Save.
 * 
 * Demonstrates:
 * - Multithreading & Concurrency
 * - ScheduledExecutorService daemon thread
 * - Thread-safe periodic flush of in-memory data
 */
public class AutoSaveManager {

    private static final AutoSaveManager INSTANCE = new AutoSaveManager();
    private ScheduledExecutorService scheduler;
    private ContactService contactService;
    private boolean isRunning = false;
    private long lastSaveTimestamp = System.currentTimeMillis();

    private AutoSaveManager() {}

    public static AutoSaveManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes and starts the background auto-save daemon.
     */
    public synchronized void start(ContactService service, long periodSeconds) {
        this.contactService = service;
        if (isRunning) return;

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "CMS-AutoSave-Daemon");
            t.setDaemon(true); // Daemon thread exits when JVM exits
            return t;
        });

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                if (contactService != null) {
                    contactService.flushStorage();
                    lastSaveTimestamp = System.currentTimeMillis();
                }
            } catch (Exception e) {
                System.err.println("Notice: Auto-save execution notice: " + e.getMessage());
            }
        }, periodSeconds, periodSeconds, TimeUnit.SECONDS);

        isRunning = true;
        System.out.println("Auto-save background daemon started (Interval: " + periodSeconds + "s).");
    }

    public synchronized void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getLastSaveTimestamp() {
        return lastSaveTimestamp;
    }
}
