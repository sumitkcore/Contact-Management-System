package com.cms.pattern.observer;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe Subject in the Observer pattern.
 * Manages listeners and safely dispatches events on the Swing Event Dispatch Thread (EDT).
 */
public class ContactEventManager {
    private static final ContactEventManager INSTANCE = new ContactEventManager();
    private final List<ContactChangeListener> listeners = new CopyOnWriteArrayList<>();

    private ContactEventManager() {}

    public static ContactEventManager getInstance() {
        return INSTANCE;
    }

    public void addListener(ContactChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(ContactChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public void fireEvent(ContactChangeEvent event) {
        if (event == null) return;
        // Ensure dispatch happens on the Swing EDT for UI safety
        if (SwingUtilities.isEventDispatchThread()) {
            notifyAllListeners(event);
        } else {
            SwingUtilities.invokeLater(() -> notifyAllListeners(event));
        }
    }

    private void notifyAllListeners(ContactChangeEvent event) {
        for (ContactChangeListener listener : listeners) {
            try {
                listener.onContactChanged(event);
            } catch (Exception e) {
                System.err.println("Error notifying ContactChangeListener: " + e.getMessage());
            }
        }
    }
}
