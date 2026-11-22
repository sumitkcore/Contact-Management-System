package com.cms.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object representing a physical address.
 * Demonstrates Encapsulation and Data Modeling.
 */
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;

    public Address(String street, String city, String state, String postalCode, String country) {
        this.street = street != null ? street.trim() : "";
        this.city = city != null ? city.trim() : "";
        this.state = state != null ? state.trim() : "";
        this.postalCode = postalCode != null ? postalCode.trim() : "";
        this.country = country != null ? country.trim() : "India";
    }

    public static Address empty() {
        return new Address("", "", "", "", "");
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public boolean isEmpty() {
        return street.isEmpty() && city.isEmpty() && state.isEmpty() && postalCode.isEmpty();
    }

    public String getFormatted() {
        if (isEmpty()) return "No address provided";
        StringBuilder sb = new StringBuilder();
        if (!street.isEmpty()) sb.append(street);
        if (!city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (!state.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        if (!postalCode.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(postalCode);
        }
        if (!country.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(city, address.city) &&
                Objects.equals(state, address.state) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, postalCode, country);
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
