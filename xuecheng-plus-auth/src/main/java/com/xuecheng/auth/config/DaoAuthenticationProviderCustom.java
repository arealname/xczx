package com.xuecheng.auth.config;

import com.xuecheng.ucenter.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class DaoAuthenticationProviderCustom extends DaoAuthenticationProvider {

    @Autowired
    public void setUserDetailsService(MyUserDetailService myuserDetailService) {
        super.setUserDetailsService(myuserDetailService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }
}
