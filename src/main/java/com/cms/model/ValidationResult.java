package com.cms.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Container capturing validation outcomes with detailed field error mapping.
 */
public final class ValidationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean valid;
    private final LinkedHashMap<String, String> errors;

    private ValidationResult(boolean valid, Map<String, String> errors) {
        this.valid = valid;
        this.errors = errors != null ? new LinkedHashMap<>(errors) : new LinkedHashMap<>();
    }

    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyMap());
    }

    public static ValidationResult failure(Map<String, String> errors) {
        return new ValidationResult(false, errors);
    }

    public static ValidationResult singleError(String field, String message) {
        LinkedHashMap<String, String> err = new LinkedHashMap<>();
        err.put(field, message);
        return new ValidationResult(false, err);
    }

    public boolean isValid() {
        return valid;
    }

    public Map<String, String> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    public String getFormattedErrorMessage() {
        if (valid || errors.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            sb.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString().trim();
    }
}
