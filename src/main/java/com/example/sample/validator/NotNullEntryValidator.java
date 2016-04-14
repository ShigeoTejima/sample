package com.example.sample.validator;

import java.util.Arrays;
import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullEntryValidator implements ConstraintValidator<NotNullEntry, Object>{

    @Override
    public void initialize(NotNullEntry annotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (!isArray(value) && !isCollection(value)) {
            throw new IllegalArgumentException("value has expected a Collection or Array.");
        }

        return isnotHasNullEntry(isArray(value) ? Arrays.asList((Object[])value) : (Collection)value);
    }

    private boolean isArray(Object value) {
        return value.getClass().isArray();
    }

    private boolean isCollection(Object value) {
        return (value instanceof Collection);
    }
    
    private boolean isnotHasNullEntry(Collection collection) {
        for (Object entry : collection) {
            if (entry == null) {
                return false;
            }
        }
        return true;
    }
}
