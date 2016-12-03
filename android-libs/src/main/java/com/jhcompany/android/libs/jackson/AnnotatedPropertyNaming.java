package com.jhcompany.android.libs.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

import java.lang.reflect.Field;

public class AnnotatedPropertyNaming extends PropertyNamingStrategy {

    @Override
    public String nameForField(MapperConfig config, AnnotatedField field, String defaultName) {
        JsonProperty mark = field.getAnnotation(JsonProperty.class);
        if (mark != null) {
            return mark.value();
        }
        return CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES.nameForField(config, field, defaultName);
    }

    @Override
    public String nameForGetterMethod(MapperConfig config, AnnotatedMethod method, String defaultName) {
        Field[] fields = method.getDeclaringClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!fieldName.equalsIgnoreCase(defaultName)) {
                continue;
            }
            JsonProperty mark = field.getAnnotation(JsonProperty.class);
            if (mark != null) {
                return mark.value();
            }
        }

        JsonProperty mark = method.getAnnotation(JsonProperty.class);
        if (mark != null) {
            return mark.value();
        }
        return CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES.nameForGetterMethod(config, method, defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig config, AnnotatedMethod method, String defaultName) {
        Field[] fields = method.getDeclaringClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!fieldName.equalsIgnoreCase(defaultName)) {
                continue;
            }
            JsonProperty mark = field.getAnnotation(JsonProperty.class);
            if (mark != null) {
                return mark.value();
            }
        }

        JsonProperty mark = method.getAnnotation(JsonProperty.class);
        if (mark != null) {
            return mark.value();
        }
        return CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES.nameForSetterMethod(config, method, defaultName);
    }
}
