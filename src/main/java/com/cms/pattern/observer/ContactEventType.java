package com.cms.pattern.observer;

/**
 * Classification of contact mutation events for the Observer pattern.
 */
public enum ContactEventType {
    ADDED,
    UPDATED,
    DELETED,
    RESTORED,
    PERMANENTLY_DELETED,
    BULK_IMPORTED,
    DATABASE_SWITCHED
}
