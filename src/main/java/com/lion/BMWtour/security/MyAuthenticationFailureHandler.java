package com.lion.BMWtour.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String msg = "아이디 또는 비밀번호가 틀렸습니다.";
        String url = "/user/login";
        request.getSession().setAttribute("msg", msg); // 세션에 에러 메시지 저장
        request.getSession().setAttribute("url", url);

        // 경고 메시지 페이지로 포워드
        response.sendRedirect("/common/alertMsg");
    }

}