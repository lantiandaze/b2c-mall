package com.b2cmall.employee.service;

import com.b2cmall.common.*;
import cn.hutool.jwt.JWT;
import com.b2cmall.employee.dao.EmployeeMapper;
import com.b2cmall.employee.dao.po.EmployeePO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.dao.DataAccessException;
import java.time.Duration;
import java.util.Collections;

@Service
public class TokenService {
    private final StringRedisTemplate redis;
    private final EmployeeMapper mapper;
    private final String key, prefix;
    private final long seconds;
    public TokenService(StringRedisTemplate redis, EmployeeMapper mapper,
                        @Value("${jwt.token.key}") String key,
                        @Value("${jwt.token.ttl-seconds:7200}") long seconds,
                        @Value("${jwt.token.redis-prefix:b2c:token:}") String prefix) {
        if(key.length()<32 || seconds<1) throw new IllegalArgumentException("JWT配置不正确");
        this.redis=redis;this.mapper=mapper;this.key=key;this.seconds=seconds;this.prefix=prefix;
    }
    private String redisKey(Long shopId,Long id) {return prefix+shopId+":"+id;}
    public String issue(EmployeePO employee) {
        String token=JwtUtil.create(employee.id,employee.shopId,employee.username,key,seconds);
        try {redis.opsForValue().set(redisKey(employee.shopId,employee.id),token,Duration.ofSeconds(seconds));}
        catch (DataAccessException e) {throw new BizException(503,"登录缓存暂不可用，请稍后重试");}
        return token;
    }
    public EmployeePO authenticate(String header) {
        String token=JwtUtil.token(header);
        JWT jwt=JwtUtil.verify(token,key);
        EmployeePO employee=mapper.findById(JwtUtil.id(jwt,"id"));
        if(employee==null || employee.status==null || employee.status!=1 ||
           employee.shopId!=JwtUtil.id(jwt,"shopId")) throw new BizException(401,"登录凭证无效或账号已禁用");
        try {
            if(!token.equals(redis.opsForValue().get(redisKey(employee.shopId,employee.id))))
                throw new BizException(401,"登录已失效，请重新登录");
        } catch(DataAccessException e) {throw new BizException(503,"登录缓存暂不可用，请稍后重试");}
        return employee;
    }
    public boolean check(String header) {
        try {authenticate(header);return true;}
        catch(BizException e) {if(e.status==401)return false;throw e;}
    }
    public void logout(String header) {
        EmployeePO employee=authenticate(header);
        // Compare and delete atomically so an old logout cannot remove a newer login.
        DefaultRedisScript<Long> script=new DefaultRedisScript<>(
            "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end",Long.class);
        try {redis.execute(script,Collections.singletonList(redisKey(employee.shopId,employee.id)),JwtUtil.token(header));}
        catch(DataAccessException e) {throw new BizException(503,"登录缓存暂不可用，请稍后重试");}
    }
}
