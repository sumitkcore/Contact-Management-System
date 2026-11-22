package com.cms.io;

import com.cms.model.AuditLog;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton Activity and Audit Logger.
 * Demonstrates the Singleton Pattern and Persistent File Logging.
 */
public class AuditLogger {
    private static final String LOG_FILE = "contact_management_audit.log";
    private static final AuditLogger INSTANCE = new AuditLogger();

    private AuditLogger() {}

    public static AuditLogger getInstance() {
        return INSTANCE;
    }

    public synchronized void log(AuditLog log) {
        if (log == null) return;
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(LOG_FILE, true), StandardCharsets.UTF_8))) {
            writer.println(log.toString());
        } catch (IOException e) {
            System.err.println("Warning: Failed to write to audit log: " + e.getMessage());
        }
    }

    public synchronized void log(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = "[" + LocalDateTime.now().format(dtf) + "] " + message;
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(LOG_FILE, true), StandardCharsets.UTF_8))) {
            writer.println(formatted);
        } catch (IOException e) {
            System.err.println("Warning: Failed to write to audit log: " + e.getMessage());
        }
    }
}
