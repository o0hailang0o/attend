package com.hailang.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hailang.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

public class TokenInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    private final StringRedisTemplate stringRedisTemplate;

    public TokenInterceptor(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            writeUnauthorized(response, "token不能为空");
            return false;
        }
        String json = stringRedisTemplate.opsForValue().get("sysUser_" + token);
        if (json == null) {
            writeUnauthorized(response, "token无效或已过期");
            return false;
        }
        SysUser user = objectMapper.readValue(json, SysUser.class);
        request.setAttribute("sysUser", user);
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String msg) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(401);
        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":401,\"msg\":\"" + msg + "\",\"data\":null}");
        writer.flush();
    }
}
