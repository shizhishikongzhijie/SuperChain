package com.example.SuperChain.service;

import com.example.SuperChain.bean.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author shizhishi
 */
@Service
public interface AccountService {
    String createAccount();
    Map<String, String> createAccountList(int num);

    String login(User user, String linkKey) throws Exception;

    String register(String email, String captcha) throws JsonProcessingException;

    String sendCaptcha(String email);

}
