package com.project.myblog.security;


import com.project.myblog.entity.LogoutTrace;
import com.project.myblog.repository.LogoutTraceRepository;
import com.project.myblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

//import org.springframework.security.core.userdetails.UserDetailsService;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

/*
    @Autowired
    private UserDetailsService userDetailsService;
    */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    LogoutTraceRepository logoutTraceRepository;

    @Value("${jwt.header}")
    private String tokenHeader;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        
    	
    	String authToken = request.getHeader(this.tokenHeader);
    	
    	//ctrl su logoutTrace table
    	
    	Optional<LogoutTrace> lt = logoutTraceRepository.findByTokenNotValid(authToken);
    	if (lt.isPresent()) {
    			authToken=null;
    
		}
    	
        UserDetails userDetails = null;

        if(authToken != null){
            userDetails = jwtTokenUtil.getUserDetails(authToken);
        }

        // ctrl if user is banned. The ctrl doesen't start if the call is 'public'
        if(authToken != null && userDetails != null){
	        if(!userRepository.findByUsername(userDetails.getUsername()).get().getEnabled()) {
	        	userDetails = null;
			}
        }
        
        
        if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // controllo integrita' token
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}