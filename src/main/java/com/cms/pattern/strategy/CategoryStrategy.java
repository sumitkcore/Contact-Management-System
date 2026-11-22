package com.cms.pattern.strategy;

import com.cms.model.Contact;
import java.util.Comparator;

public class CategoryStrategy implements ContactSortingStrategy {
    @Override
    public Comparator<Contact> getComparator() {
        return (c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return 1;
            if (c2 == null) return -1;
            int catCmp = c1.getContactType().compareTo(c2.getContactType());
            if (catCmp != 0) return catCmp;
            return c1.getFullName().compareToIgnoreCase(c2.getFullName());
        };
    }

    @Override
    public String getDisplayName() {
        return "Category (Personal, Business, Emergency)";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
