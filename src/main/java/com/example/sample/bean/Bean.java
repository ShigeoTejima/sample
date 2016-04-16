package com.example.sample.bean;

import com.example.sample.validator.Date;
import com.example.sample.validator.NotNullEntry;
import com.example.sample.validator.SizeEntry;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

public class Bean {

    @AssertTrue
    private boolean aaa;

    @NotEmpty
    @NotNullEntry
    @SizeEntry(max = 10)
    private List<String> strings;

    @NotEmpty
    @NotNullEntry
    @Valid
    private List<NestedBean> nestedBeans;

    @NotNullEntry
    private String[] array;

    @Date(pattern = {"yyyy-MM-dd", "yyyy/MM/dd"})
    private String createdAt;

    public boolean isAaa() {
        return aaa;
    }

    public void setAaa(boolean aaa) {
        this.aaa = aaa;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public List<NestedBean> getNestedBeans() {
        return nestedBeans;
    }

    public void setNestedBeans(List<NestedBean> nestedBeans) {
        this.nestedBeans = nestedBeans;
    }

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static class NestedBean {

        public NestedBean(String value) {
            this.value = value;
        }

        @NotNull
        private String value;

        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
    }
}
