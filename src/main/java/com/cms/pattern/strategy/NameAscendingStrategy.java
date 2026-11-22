package com.cms.pattern.strategy;

import com.cms.model.Contact;
import java.util.Comparator;

public class NameAscendingStrategy implements ContactSortingStrategy {
    @Override
    public Comparator<Contact> getComparator() {
        return (c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return 1;
            if (c2 == null) return -1;
            int cmp = c1.getFirstName().compareToIgnoreCase(c2.getFirstName());
            if (cmp != 0) return cmp;
            return c1.getLastName().compareToIgnoreCase(c2.getLastName());
        };
    }

    @Override
    public String getDisplayName() {
        return "Name (A → Z)";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
