package com.bhnote.model;

import lombok.Data;

/**
 * @author bingo
 * @date 2022/1/6
 */
@Data
public class LoginUser {

    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String NICKNAME = "nickname";

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /*
      可存放登录用户的其它信息
      ...
     */
}
