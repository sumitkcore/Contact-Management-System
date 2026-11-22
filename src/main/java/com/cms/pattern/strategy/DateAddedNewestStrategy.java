package com.cms.pattern.strategy;

import com.cms.model.Contact;
import java.util.Comparator;

public class DateAddedNewestStrategy implements ContactSortingStrategy {
    @Override
    public Comparator<Contact> getComparator() {
        return (c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return 1;
            if (c2 == null) return -1;
            if (c1.getCreatedAt() == null && c2.getCreatedAt() == null) return 0;
            if (c1.getCreatedAt() == null) return 1;
            if (c2.getCreatedAt() == null) return -1;
            return c2.getCreatedAt().compareTo(c1.getCreatedAt()); // Newest first
        };
    }

    @Override
    public String getDisplayName() {
        return "Date Added (Newest First)";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
