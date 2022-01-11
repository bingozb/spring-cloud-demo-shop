package com.bhnote.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.TimeZone;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Jackson 全局配置
 *
 * @author bingo
 * @date 2022/1/6
 */
@Configuration
public class JacksonConfig {

    private final static String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        // json中多余的参数不抛异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // null 不返回
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 驼峰 <-> 下划线
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        // Long -> String，解决前端精度丢失问题（JS数字最长17位，超过17位会精度丢失，而雪花算法生成的ID为19位）
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        // Date
        objectMapper.setDateFormat(new SimpleDateFormat(PATTERN));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.setLocale(Locale.CHINESE);
        // LocalDate
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(ofPattern(PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(ofPattern(PATTERN)));
        // LocalDateTime
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        objectMapper.registerModule(javaTimeModule);
        objectMapper.registerModule(new Jdk8Module());

        return objectMapper;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return getObjectMapper();
    }

    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(ofPattern(PATTERN)));
        }
    }

    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
            return LocalDateTime.parse(p.getValueAsString(), ofPattern(PATTERN));
        }
    }
}
