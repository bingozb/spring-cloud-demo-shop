package com.bhnote.model.vo;

import lombok.Data;

/**
 * @author bingo
 * @date 2022/1/6
 */
@Data
public class UserVO {

    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 账号
     */
    private String username;
}
