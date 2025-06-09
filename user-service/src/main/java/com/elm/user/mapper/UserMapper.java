package com.elm.user.mapper;

import com.elm.common.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE userId = #{userId}")
    User getUserById(@Param("userId") Integer userId);

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User getUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Insert("INSERT INTO user (username, password, phone, userSex) VALUES (#{username}, #{password}, #{phone}, #{userSex})")
    int saveUser(User user);
} 