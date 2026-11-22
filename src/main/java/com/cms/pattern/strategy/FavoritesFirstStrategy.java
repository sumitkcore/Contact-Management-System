package com.cms.pattern.strategy;

import com.cms.model.Contact;
import java.util.Comparator;

public class FavoritesFirstStrategy implements ContactSortingStrategy {
    @Override
    public Comparator<Contact> getComparator() {
        return (c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return 1;
            if (c2 == null) return -1;
            if (c1.isFavorite() && !c2.isFavorite()) return -1;
            if (!c1.isFavorite() && c2.isFavorite()) return 1;
            return c1.getFullName().compareToIgnoreCase(c2.getFullName());
        };
    }

    @Override
    public String getDisplayName() {
        return "Favorites First ★";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
