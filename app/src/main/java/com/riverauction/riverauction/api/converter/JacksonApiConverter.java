package com.riverauction.riverauction.api.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.jhcompany.android.libs.jackson.Jackson;
import com.riverauction.riverauction.api.service.APIErrorResponse;
import com.riverauction.riverauction.api.model.CResponseStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import static com.riverauction.riverauction.api.APIConstant.KEY_STATUS;


public class JacksonApiConverter implements Converter {

    private static final String MIME_APPLICATION_JSON = "application/json; charset=UTF-8";

    private final ObjectMapper objectMapper;

    public JacksonApiConverter() {
        this(Jackson.getMapper());
    }

    public JacksonApiConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        // Null 값을 서버로 전달
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        try {
            JavaType javaType = Jackson.getType(type);

            String response = getBodyString(body);
            if (Strings.isNullOrEmpty(response)) {
                throw new ConversionException(new IllegalArgumentException("Response must not be null or empty string"));
            }

            JsonNode responseJson = Jackson.stringToNode(response);

            // Between 과 다르게 status code 200 으로 에러가 오는게 아니라 4xx, 5xx 로 에러가 온다.
            // 따라서 아래 코드는 안탈 것이다. 하지만 혹시몰라서 남겨두었다.
            if (!isSuccess(responseJson)) {
                if (APIErrorResponse.class == type) {
                    return Jackson.nodeToObject(responseJson, javaType);
                } else {
                    throw new ConversionException(new IllegalArgumentException("Error String must be casted to ErrorResponse"));
                }
            }

            return Jackson.nodeToObject(responseJson, javaType);
        } catch (IOException e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return new TypedByteArray(MIME_APPLICATION_JSON, json.getBytes("UTF-8"));
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static boolean isSuccess(JsonNode jsonNode) {
        if (jsonNode.has(KEY_STATUS)) {
            CResponseStatus status = CResponseStatus.valueOf(jsonNode.get(KEY_STATUS).asText());
            return CResponseStatus.SUCCESS == status;
        }
        return false;
    }

    public static String getBodyString(TypedInput typedInput) throws IOException {
        if (typedInput != null) {
            if (!(typedInput instanceof TypedByteArray)) {
                // Read the entire response body to we can log it and replace the original response
                typedInput = readBody(typedInput);
            }

            byte[] bodyBytes = ((TypedByteArray) typedInput).getBytes();
            String bodyMime = typedInput.mimeType();
            String bodyCharset = MimeUtil.parseCharset(bodyMime, "utf-8");
            return new String(bodyBytes, bodyCharset);
        }
        return null;
    }

    private static TypedInput readBody(TypedInput typedInput) throws IOException {
        String bodyMime = typedInput.mimeType();
        byte[] bodyBytes = streamToBytes(typedInput.in());
        return new TypedByteArray(bodyMime, bodyBytes);
    }

    private static byte[] streamToBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (stream != null) {
            byte[] buf = new byte[1024];
            int r;
            while ((r = stream.read(buf)) != -1) {
                byteArrayOutputStream.write(buf, 0, r);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
}
