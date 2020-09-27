package org.isf.security;

import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    protected UserBrowsingManager manager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = manager.getUserByName(username);
        } catch (OHServiceException e) {
            log.error("User login received an unexpected OHServiceException", e);
            throw new UsernameNotFoundException(username + " authentication failed", e);
        }
        if (user == null) {
            throw new UsernameNotFoundException(username + " was not found");
        }
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getUserGroupName().getCode()));
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getUserName(), user.getPasswd(), true, true, true, true, authorities
                );
        return userDetails;
    }

}
