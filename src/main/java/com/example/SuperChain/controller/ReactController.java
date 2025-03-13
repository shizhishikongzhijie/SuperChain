package com.example.SuperChain.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.SuperChain.mapper.ReactMapper;
import com.example.SuperChain.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author shizhishi
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ReactController {
    @Resource
    private final ReactMapper reactMapper;

    public ReactController(ReactMapper reactMapper) {
        this.reactMapper = reactMapper;
    }

    @GetMapping("/getHomePage")
    public String getHomePage(HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
        Map<String, Object> result = reactMapper.getHomePage();
        //将map转换为json
        JSONObject json =  new JSONObject(result);
        log.info("getHomePage: "+json.toJSONString());
        return AESUtils.aesEncrypt(json.toJSONString(), linkKey);
    }
}
