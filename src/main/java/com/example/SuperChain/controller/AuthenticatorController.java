package com.example.SuperChain.controller;

import com.example.SuperChain.util.AESUtils;
import com.example.SuperChain.util.GoogleAuthenticator;
import com.example.SuperChain.util.resUtils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shizhishi
 */
@Slf4j
@RestController
public class AuthenticatorController {
    /**
     * 生成 Google 密钥，两种方式任选一种
     */
    @GetMapping("/getSecret")
    public String getSecret() {
        return GoogleAuthenticator.getSecretKey();
    }

    /**
     * 生成二维码，APP直接扫描绑定，两种方式任选一种
     */
    @GetMapping("/getQRCode")
    public String getQRCode(String name, HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
        // 生成二维码内容
        String qrCodeText = GoogleAuthenticator.getQrCodeText(GoogleAuthenticator.getSecretKey(), name, "");
        // 生成二维码输出
//        new SimpleQrcodeGenerator().generate(qrCodeText).toStream(response.getOutputStream());
        return AESUtils.aesEncrypt(R.ok().data("qrCodeText", qrCodeText).toJSONString(), linkKey) ;
    }

    /**
     * 获取code
     */
    @GetMapping("/getCode")
    public String getCode(String secretKey) {
        return GoogleAuthenticator.getCode(secretKey);
    }

    /**
     * 验证 code 是否正确
     */
    @GetMapping("/checkCode")
    public String checkCode(String secret, String code) {
        boolean b = GoogleAuthenticator.checkCode(secret, Long.parseLong(code), System.currentTimeMillis());
        if (b) {
            return "success";
        }
        return "error";
    }

}