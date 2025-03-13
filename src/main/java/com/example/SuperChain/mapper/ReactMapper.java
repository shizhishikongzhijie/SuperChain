package com.example.SuperChain.mapper;

import com.example.SuperChain.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author shizhishi
 */
@Mapper
public interface ReactMapper {
    User getUserByEmail(String email);

    Map<String, Object> getHomePage();

    void saveUserCredentials(@Param("formatPublicKey") String formatPublicKey,@Param("email") String email);
}
