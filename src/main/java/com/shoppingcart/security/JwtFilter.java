package com.shoppingcart.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shoppingcart.entity.AccessToken;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.repository.AccessTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Component
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter{

	private AccessTokenRepository accessTokenRepository;
	
	private JwtService jwtService;
	
	private CustomUserDetailService customUserDetailService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException {
			String at=null;
			String rt=null;
		Cookie[] cookies = request.getCookies();
		if(cookies !=null) {
			for(Cookie cookie:cookies) {
				if(cookie.getName().equals("at")) at=cookie.getValue();
				if(cookie.getName().equals("rt")) rt=cookie.getValue();
			}
			String username=null;
			if(at != null && rt != null) {	
				Optional<AccessToken> accessToken= accessTokenRepository.findByTokenAndIsBlocked(at, false);
				
				if(accessToken == null) throw new RuntimeException("AccessToken not found");
				else{
					log.info("Authenticating the token...!");
					username = jwtService.extrctUsername(at);
					if(username == null) throw new UserNotFoundException("Failed to Authenticate");
					UserDetails details = customUserDetailService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null,details.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					log.info("Authenticated Successfully...!");
				}
			}
		}
			filterChain.doFilter(request, response);
	}
}
