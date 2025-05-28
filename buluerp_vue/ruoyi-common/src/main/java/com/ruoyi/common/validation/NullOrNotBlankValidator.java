package com.ruoyi.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, CharSequence> {
    public NullOrNotBlankValidator() {
    }

    public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        if (charSequence == null) {
            return true;
        } else {
            return !charSequence.toString().trim().isEmpty();
        }
    }
}
