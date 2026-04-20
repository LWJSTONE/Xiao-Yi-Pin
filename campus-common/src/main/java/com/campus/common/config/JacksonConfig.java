package com.campus.common.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 全局配置
 * 将 Long 类型序列化为 String，防止前端 JavaScript 精度丢失
 * （JS 的 Number.MAX_SAFE_INTEGER 为 9007199254740991，雪花ID超出此范围）
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longToStringCustomizer() {
        return builder -> {
            // Long 包装类型 → String
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            // long 基本类型 → String
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
        };
    }
}
