/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.security;

import java.util.ArrayList;
import java.util.List;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.model.Permission;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    protected UserBrowsingManager manager;

    @Autowired
	protected PermissionManager permissionManager;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = manager.getUserByName(username);
        } catch (OHServiceException serviceException) {
            LOGGER.error("User login received an unexpected OHServiceException.", serviceException);
            throw new UsernameNotFoundException(username + " authentication failed.", serviceException);
        }
        if (user == null) {
            throw new UsernameNotFoundException(username + " was not found.");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        List<Permission> permissions;
        try {
            permissions = permissionManager.retrievePermissionsByUsername(username);
        } catch (OHServiceException serviceException) {
            LOGGER.error("Retrieving permissions for user received an unexpected OHServiceException.", serviceException);
            throw new UsernameNotFoundException(username + " authentication failed.", serviceException);
        }
        for (Permission p : permissions) {
	    authorities.add(new SimpleGrantedAuthority(p.getName()));
        }

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getUserName(), user.getPasswd(), true, true, true, true, authorities
                );
        return userDetails;
    }

}
