package com.example.sample;

import com.example.sample.bean.Bean;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class ValidatorApp {

    public static void main(String[] args) {
        new ValidatorApp().run();
    }
    
    void run() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Bean bean = new Bean();
        bean.setAaa(true);
//        bean.setStrings(Collections.emptyList());
//        bean.setStrings(Arrays.asList("foo", null));
        bean.setStrings(Arrays.asList("foo", "baaaaaaaaar", "baz"));
        bean.setArray(new String[]{"x", null});
//        bean.setNestedBeans(Arrays.asList(new Bean.NestedBean("_a"), null));
        bean.setCreatedAt("2016/04/14");
        Set<ConstraintViolation<Bean>> violations = validator.validate(bean);
        for (ConstraintViolation<Bean> violation : violations) {
            System.out.println(String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()));
        }

    }
}
