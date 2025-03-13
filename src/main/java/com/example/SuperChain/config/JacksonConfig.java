package com.example.SuperChain.config;

import com.example.SuperChain.bean.CustomECPointDeserializer;
import com.example.SuperChain.bean.CustomECPointSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shizhishi
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ECPoint.class, new CustomECPointSerializer());
        module.addDeserializer(ECPoint.class, new CustomECPointDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}