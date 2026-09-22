package com.b2cmall.common;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public final class JwtUtil {
    private JwtUtil() {}
    public static String create(Long id, Long shopId, String username, String key, long seconds) {
        Map<String,Object> payload = new HashMap<>();
        long now = Instant.now().getEpochSecond();
        payload.put("id",id); payload.put("shopId",shopId); payload.put("username",username);
        payload.put("iat",now); payload.put("exp",now+seconds);
        payload.put("jti",UUID.randomUUID().toString());
        payload.put("iss","b2c-mall");
        return JWTUtil.createToken(payload, JWTSignerUtil.hs256(key.getBytes(StandardCharsets.UTF_8)));
    }
    public static JWT verify(String token, String key) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            if (!"HS256".equals(jwt.getHeader("alg")) ||
                !jwt.verify(JWTSignerUtil.hs256(key.getBytes(StandardCharsets.UTF_8))) ||
                !"b2c-mall".equals(jwt.getPayload("iss"))) throw new IllegalArgumentException();
            long expiry=Long.parseLong(String.valueOf(jwt.getPayload("exp")));
            if (expiry<=Instant.now().getEpochSecond()) throw new IllegalArgumentException();
            if (id(jwt,"id")<=0 || id(jwt,"shopId")<=0) throw new IllegalArgumentException();
            return jwt;
        } catch (Exception e) { throw new BizException(401,"登录凭证无效或已过期"); }
    }
    public static long id(JWT jwt,String name) { return Long.parseLong(String.valueOf(jwt.getPayload(name))); }
    public static String token(String header) {
        String value=header==null ? "" : header.trim();
        return value.regionMatches(true,0,"Bearer ",0,7) ? value.substring(7).trim() : value;
    }
}
