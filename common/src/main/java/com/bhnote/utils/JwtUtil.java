package com.bhnote.utils;

import com.bhnote.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author bingo
 * @date 2022/1/6
 */
public class JwtUtil {

    /**
     * token过期时间
     */
    private static final long EXPIRE = 1000L * 3600 * 24 * 30;

    /**
     * 加密的密钥
     */
    private static final String SECRET = "bhnote.com";

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "shop";

    /**
     * subject
     */
    private static final String SUBJECT = "shop";


    /**
     * 根据用户信息生成令牌
     */
    public static String generateToken(LoginUser user) {
        String token = Jwts.builder()
                .setSubject(SUBJECT)
                .claim(LoginUser.ID, user.getId())
                .claim(LoginUser.USERNAME, user.getUsername())
                .claim(LoginUser.NICKNAME, user.getNickname())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
        return TOKEN_PREFIX + token;
    }

    /**
     * 校验token
     */
    public static LoginUser checkToken(String token) {

        if (StringUtils.isBlank(token)) {
            return null;
        }
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
        } catch (Exception e) {
            return null;
        }
        if (claims == null) {
            return null;
        }

        LoginUser loginUser = new LoginUser();
        if (claims.containsKey(LoginUser.ID)) {
            loginUser.setId(Long.valueOf(claims.get(LoginUser.ID).toString()));
        }
        if (claims.containsKey(LoginUser.USERNAME)) {
            loginUser.setUsername(claims.get(LoginUser.USERNAME).toString());
        }
        if (claims.containsKey(LoginUser.NICKNAME)) {
            loginUser.setNickname(claims.get(LoginUser.NICKNAME).toString());
        }
        return loginUser;
    }
}