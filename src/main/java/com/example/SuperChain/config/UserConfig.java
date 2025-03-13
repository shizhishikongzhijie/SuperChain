package com.example.SuperChain.config;

import com.example.SuperChain.bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shizhishi
 */
@Configuration
public class UserConfig {
    @Bean
    public User user(){
        return new User("","","");
    }
}
