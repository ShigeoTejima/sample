package com.example.sample.validator;

import java.util.Arrays;
import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SizeEntryValidator implements ConstraintValidator<SizeEntry, Object> {

    private String message;
    private int min;
    private int max;

    @Override
    public void initialize(SizeEntry annotation) {
        this.message = annotation.message();
        this.min = annotation.min();
        this.max = annotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (!isArray(value) && !isCollection(value)) {
            throw new IllegalArgumentException("value has expected a Collection or Array.");
        }

        boolean isValid = true;
        int index = 0;
        Collection<Object> collection = isArray(value) ? Arrays.asList((Object[])value) : (Collection)value;
        for (Object object : collection) {
            if (object == null) {
                continue;
            }
            if (object instanceof String) {
                int length = ((String)object).length();
                if (length < min || length > max) {
                    /*
                    ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(this.message);
                    ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeContextBuilder leafBuilder = violationBuilder.addBeanNode().inIterable();
                    leafBuilder.atIndex(index);
                    violationBuilder.addConstraintViolation();
                    context.disableDefaultConstraintViolation();
                            */
                    context.buildConstraintViolationWithTemplate(this.message)
                           .addBeanNode()
                           .inIterable()
                           .atKey("hoge")
//                           .atIndex(index)
                           .addConstraintViolation()
                           .disableDefaultConstraintViolation();
                    isValid = false;
                }
            }
            index++;
        }
        return isValid;
    }

    private boolean isArray(Object value) {
        return value.getClass().isArray();
    }

    private boolean isCollection(Object value) {
        return (value instanceof Collection);
    }
    
}
