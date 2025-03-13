package com.example.SuperChain.util.resUtils;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回格式类
 *
 */
@Data
public class R {

    /*
      是否成功
      -- GETTER --
      以下是get/set方法，如果项目有集成lombok可以使用@Data注解代替

     */

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回的消息
     */
    private String message;

    /**
     * 放置响应的数据
     */
    private JSONObject data = new JSONObject();

    public R() {}

    /** 以下是定义一些常用到的格式，可以看到调用了我们创建的枚举类 */

    public static R ok() {
        R r = new R();
        r.setCode(ResultCodeEnum.SUCCESS.getCode());
        r.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return r;
    }
    public static R fail() {
        R r = new R();
        r.setCode(ResultCodeEnum.FAIL.getCode());
        r.setMessage(ResultCodeEnum.FAIL.getMessage());
        return r;
    }
    //TOKEN_EXPIRED
    public static R tokenExpired() {
        R r = new R();
        r.setCode(ResultCodeEnum.TOKEN_EXPIRED.getCode());
        r.setMessage(ResultCodeEnum.TOKEN_EXPIRED.getMessage());
        return r;
    }
    //TOKEN_DECODE_ERROR
    public static R tokenDecodeError() {
        R r = new R();
        r.setCode(ResultCodeEnum.TOKEN_DECODE_ERROR.getCode());
        r.setMessage(ResultCodeEnum.TOKEN_DECODE_ERROR.getMessage());
        return r;
    }
    //TOKEN_NOT_FOUND
    public static R tokenNotFound() {
        R r = new R();
        r.setCode(ResultCodeEnum.TOKEN_NOT_FOUND.getCode());
        r.setMessage(ResultCodeEnum.TOKEN_NOT_FOUND.getMessage());
        return r;
    }
    public static R loginOk(){
        R r = new R();
        r.setCode(ResultCodeEnum.LOGIN_SUCCESS.getCode());
        r.setMessage(ResultCodeEnum.LOGIN_SUCCESS.getMessage());
        return r;
    }

    public static R userExist(){
        R r = new R();
        r.setCode(ResultCodeEnum.USER_EXIST.getCode());
        r.setMessage(ResultCodeEnum.USER_EXIST.getMessage());
        return r;
    }

    //CAPTCHA_SEND_OK
    public static R captchaSendOk(){
        R r = new R();
        r.setCode(ResultCodeEnum.CAPTCHA_SEND_OK.getCode());
        r.setMessage(ResultCodeEnum.CAPTCHA_SEND_OK.getMessage());
        return r;
    }

    //CAPTCHA_FREQUENT
    public static R captchaFrequent(){
        R r = new R();
        r.setCode(ResultCodeEnum.CAPTCHA_FREQUENT.getCode());
        r.setMessage(ResultCodeEnum.CAPTCHA_FREQUENT.getMessage());
        return r;
    }

    public static R captchaSendFail(){
        R r = new R();
        r.setCode(ResultCodeEnum.CAPTCHA_SEND_FAIL.getCode());
        r.setMessage(ResultCodeEnum.CAPTCHA_SEND_FAIL.getMessage());
        return r;
    }

    public static R userNotExist(){
        R r = new R();
        r.setCode(ResultCodeEnum.USER_NOT_EXIST.getCode());
        r.setMessage(ResultCodeEnum.USER_NOT_EXIST.getMessage());
        return r;
    }

    public static R captchaExpired(){
        R r = new R();
        r.setCode(ResultCodeEnum.CAPTCHA_EXPIRED.getCode());
        r.setMessage(ResultCodeEnum.CAPTCHA_EXPIRED.getMessage());
        return r;
    }

    public static R captchaError(){
        R r = new R();
        r.setCode(ResultCodeEnum.CAPTCHA_ERROR.getCode());
        r.setMessage(ResultCodeEnum.CAPTCHA_ERROR.getMessage());
        return r;
    }
    public  static R registerOk(){
        R r = new R();
        r.setCode(ResultCodeEnum.REGISTER_SUCCESS.getCode());
        r.setMessage(ResultCodeEnum.REGISTER_SUCCESS.getMessage());
        return r;
    }

    public static R privateKeyError(){
        R r = new R();
        r.setCode(ResultCodeEnum.PRIVATE_KEY_ERROR.getCode());
        r.setMessage(ResultCodeEnum.PRIVATE_KEY_ERROR.getMessage());
        return r;
    }

    public static R setVoteOk(){
        R r = new R();
        r.setCode(ResultCodeEnum.SET_VOTE_OK.getCode());
        r.setMessage(ResultCodeEnum.SET_VOTE_OK.getMessage());
        return r;
    }

    public static R setVoteFail(){
        R r = new R();
        r.setCode(ResultCodeEnum.SET_VOTE_FAIL.getCode());
        r.setMessage(ResultCodeEnum.SET_VOTE_FAIL.getMessage());
        return r;
    }
    public static R searchSuccess(){
        R r = new R();
        r.setCode(ResultCodeEnum.SEARCH_SUCCESS.getCode());
        r.setMessage(ResultCodeEnum.SEARCH_SUCCESS.getMessage());
        return r;
    }

    public static R searchFail(){
        R r = new R();
        r.setCode(ResultCodeEnum.SEARCH_FAIL.getCode());
        r.setMessage(ResultCodeEnum.SEARCH_FAIL.getMessage());
        return r;
    }

    public static R searchResultNull(){
        R r = new R();
        r.setCode(ResultCodeEnum.SEARCH_RESULT_NULL.getCode());
        r.setMessage(ResultCodeEnum.SEARCH_RESULT_NULL.getMessage());
        return r;
    }

    public static R setResult(ResultCodeEnum resultCodeEnum) {
        R r = new R();
        r.setCode(resultCodeEnum.getCode());
        r.setMessage(resultCodeEnum.getMessage());
        return r;
    }


    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        this.setData(jsonObject);
        return this;
    }

    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }
}