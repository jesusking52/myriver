package com.jhcompany.android.libs.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Jackson Json 을 사용하는 간소화된 인터페이스를 제공한다.
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class Jackson {

    private static ObjectMapper MAPPER;

    private Jackson() {
    }

    public static void setMapper(ObjectMapper mapper) {
        MAPPER = mapper;
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static JavaType getType(Class simpleClazz) {
        return MAPPER.getTypeFactory().uncheckedSimpleType(simpleClazz);
    }

    public static JavaType getType(Type clazz){
        return MAPPER.getTypeFactory().constructType(clazz);
    }

    public static JavaType getType(Class<?> parametrized, Class<?>... parameterClasses) {
        return MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    @SuppressWarnings("DuplicateThrows")
    public static JsonNode stringToNode(String jsonFormat) throws IOException, JsonProcessingException {
        return MAPPER.readTree(jsonFormat);
    }

    public static <T> T stringToObject(String jsonFormat, Class<T> clazz) throws IOException {
        return MAPPER.readValue(jsonFormat, clazz);
    }

    public static <T> T stringToObject(String jsonFormat, Class<?> parametrized, JavaType... parameterTypes) throws IOException {
        JavaType genericType = MAPPER.getTypeFactory().constructParametricType(parametrized, parameterTypes);
        return MAPPER.readValue(jsonFormat, genericType);
    }

    public static <V, T> String objectToString(V object, Class<T> clazz) throws JsonProcessingException {
        if (null == object) {
            return null;
        }
        return MAPPER.writerWithType(clazz).writeValueAsString(object);
    }

    public static <V> JsonNode objectToNode(V object) {
        return MAPPER.convertValue(object, JsonNode.class);
    }

    public static <T> T nodeToObject(JsonNode jackson, JavaType type) {
        return MAPPER.convertValue(jackson, type);
    }
}
