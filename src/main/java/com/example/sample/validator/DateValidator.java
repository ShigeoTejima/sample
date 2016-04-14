package com.example.sample.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<Date, String> {

    private String[] patterns;

    @Override
    public void initialize(Date annotation) {
        this.patterns = annotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        for (String pattern : this.patterns) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            dateFormat.setLenient(false);
            
            try {
                dateFormat.parse(value);
                return true;
            } catch (ParseException ex) {
//                Logger.getLogger(DateValidator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
}
