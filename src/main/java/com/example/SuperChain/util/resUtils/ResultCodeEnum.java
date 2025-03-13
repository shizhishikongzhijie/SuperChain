package com.example.SuperChain.util.resUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 状态码
 *
 * @author shizhishi
 */
@Getter
@ToString
@AllArgsConstructor
public enum ResultCodeEnum {

    SUCCESS(200, "成功！"),
    //token过期
    TOKEN_EXPIRED(300, "token过期！"),
    //token解码错误
    TOKEN_DECODE_ERROR(301, "token解码错误！"),
    //未找到token
    TOKEN_NOT_FOUND(302, "未找到token！"),
    PARAMETER_ERROR(400, "参数异常！"),
    NOT_LOGIN(300, "未登陆，请登陆后再进行操作！"),
    LOGIN_SUCCESS(200, "登陆成功！"),
    //注册成功‘
    REGISTER_SUCCESS(200, "注册成功！"),
    //用户已存在
    USER_EXIST(300, "用户已存在"),
    USER_NOT_EXIST(301, "用户不存在"),
    PRIVATE_KEY_ERROR(301, "私钥错误"),
    //验证码发送成功
    CAPTCHA_SEND_OK(200, "验证码发送成功！"),
    //验证码发送频繁
    CAPTCHA_FREQUENT(301, "验证码发送频繁！"),
    //验证码发送失败
    CAPTCHA_SEND_FAIL(302, "验证码发送失败！"),
    //验证码已过期
    CAPTCHA_EXPIRED(302, "验证码已过期！"),
    //验证码错误
    CAPTCHA_ERROR(303, "验证码错误！"),
    SET_VOTE_OK(200, "创建投票成功！"),
    SET_VOTE_FAIL(400, "创建投票失败！"),
    //搜索结果为空
    SEARCH_RESULT_NULL(400, "搜索结果为空！"),
    //搜索成功
    SEARCH_SUCCESS(200, "搜索成功！"),
    //搜索失败
    SEARCH_FAIL(404, "搜索失败！"),
    FAIL(500, "服务异常！");


    private final Integer code;
    private final String message;

}