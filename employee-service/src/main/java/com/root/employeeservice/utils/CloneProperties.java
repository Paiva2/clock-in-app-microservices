package com.root.employeeservice.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CloneProperties {
    public CloneProperties() {
    }

    public void copyNonNullProperties(Object source, Object target) {
        if (!source.getClass().equals(target.getClass())) {
            throw new IllegalArgumentException("Classes must be equals to be cloned");
        }

        List<String> nonUpdatableFields = Arrays.asList("class", "id");

        BeanWrapper sourceBean = new BeanWrapperImpl(source);
        BeanWrapper targetBean = new BeanWrapperImpl(target);

        Arrays.asList(sourceBean.getPropertyDescriptors()).forEach((field) -> {
            String fieldName = field.getName();
            Object fieldValue = sourceBean.getPropertyValue(fieldName);

            if (nonUpdatableFields.contains(fieldName) || fieldValue == null) return;

            targetBean.setPropertyValue(fieldName, fieldValue);
        });
    }
}
