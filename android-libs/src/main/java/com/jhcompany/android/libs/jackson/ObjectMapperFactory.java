package com.jhcompany.android.libs.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 기본적인 Jackson 환경 설정
 */
public class ObjectMapperFactory {

    public static ObjectMapper newMapper() {
        ObjectMapper result = new ObjectMapper();

        result.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        result.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
        result.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        result.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        result.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);

        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        result.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, false);
        result.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        result.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        result.setPropertyNamingStrategy(new AnnotatedPropertyNaming());
        return result;
    }
}
