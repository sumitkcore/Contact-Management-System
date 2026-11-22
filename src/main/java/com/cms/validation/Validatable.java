package com.cms.validation;

import com.cms.model.ValidationResult;

/**
 * Interface for domain objects that can validate their internal state.
 * Demonstrates Abstraction and Interface Contracts.
 */
public interface Validatable {
    /**
     * Validates the object and returns a ValidationResult.
     * @return ValidationResult containing valid flag and specific field errors if any.
     */
    ValidationResult validate();
}
