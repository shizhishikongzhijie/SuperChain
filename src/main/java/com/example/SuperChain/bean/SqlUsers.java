package com.example.SuperChain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;

/**
 * @author shizhishi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SqlUsers {
    int id;
    String email;
    String publicKey;
    String captcha;
    Date date;
    public SqlUsers(String email, String captcha){
        this.email = email;
        this.captcha = captcha;
    }
}
