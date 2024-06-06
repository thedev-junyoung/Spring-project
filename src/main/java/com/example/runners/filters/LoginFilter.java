package com.example.runners.filters;

import com.example.runners.dto.user.RunnerUserDetails;
import com.example.runners.utils.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWT jwt;

    public LoginFilter(AuthenticationManager authenticationManager, JWT jwt){
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication() in LoginFilter"+obtainEmail(request));
        String username = obtainEmail(request);
        String password = obtainPassword(request);

        System.out.println(username + " login!");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
        System.out.println("authToken:"+authToken);
        return authenticationManager.authenticate(authToken);
    }

    private String obtainEmail(HttpServletRequest request) {
        return request.getParameter("email");
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        System.out.println("successfulAuthentication() in LoginFilter");

        // create JWT token with user info

        RunnerUserDetails customUserDetails = (RunnerUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwt.createJwt(username, role, 60*60*1000L);
        System.out.println("JWT token 생성 :"+token);
        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        System.out.println("unsuccessfulAuthentication() in LoginFilter");
        response.setStatus(401);
    }


}
