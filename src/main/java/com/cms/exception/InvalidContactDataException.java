package com.cms.exception;

import com.cms.model.ValidationResult;

/**
 * Checked exception thrown when contact fields fail business validation rules.
 */
public class InvalidContactDataException extends ContactManagementException {
    private static final long serialVersionUID = 1L;

    private final ValidationResult validationResult;

    public InvalidContactDataException(String message, ValidationResult validationResult) {
        super(message);
        this.validationResult = validationResult;
    }

    public InvalidContactDataException(ValidationResult validationResult) {
        this("Contact validation failed:\n" + (validationResult != null ? validationResult.getFormattedErrorMessage() : "Unknown error"), validationResult);
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
