package com.elm.user.mapper;

import com.elm.common.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE userId = #{userId}")
    User getUserById(@Param("userId") String userId);

    @Select("SELECT * FROM user WHERE userName = #{userName} AND password = #{password}")
    User getUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

    @Select("SELECT * FROM user WHERE userId = #{userId} AND password = #{password}")
    User getUserByIdAndPassword(@Param("userId") String userId, @Param("password") String password);

    @Insert("INSERT INTO user (userId, password, userName, userSex, delTag) VALUES (#{userId}, #{password}, #{userName}, #{userSex}, 1)")
    int saveUser(User user);
} 