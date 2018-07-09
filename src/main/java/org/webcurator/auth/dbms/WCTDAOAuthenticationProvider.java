/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.auth.dbms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

/**
 * This authentication provider is used by the Acegi security framework to log a user in 
 * by checking the provided username and credential against the WCT core database.
 * If the user is authenticated then the users granted authorities are also loaded from the 
 * WCT database.
 * @author bprice
 */
public class WCTDAOAuthenticationProvider extends org.acegisecurity.userdetails.jdbc.JdbcDaoImpl {

    @SuppressWarnings("unchecked")
	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        List users = usersByUsernameMapping.execute(username);

        if (users.size() == 0) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDetails user = (UserDetails) users.get(0); // contains no GrantedAuthority[]

        List dbAuths = authoritiesByUsernameMapping.execute(user.getUsername());

        if (dbAuths.size() == 0) {
            throw new UsernameNotFoundException("User has no GrantedAuthority");
        }

        GrantedAuthority[] arrayAuths = {};

        addCustomAuthorities(user.getUsername(), dbAuths);

        arrayAuths = (GrantedAuthority[]) dbAuths.toArray(arrayAuths);

        String returnUsername = user.getUsername();

        if (!isUsernameBasedPrimaryKey()) {
            returnUsername = username;
        }

        return new User(returnUsername, user.getPassword(), user.isEnabled(),
            true, true, true, arrayAuths);
    }

    
    /**
     * Extension point to allow other MappingSqlQuery objects to be substituted
     * in a subclass
     */
    protected void initMappingSqlQueries() {
        this.usersByUsernameMapping = new UsersByUsernameMapping(getDataSource());
        this.authoritiesByUsernameMapping = new AuthoritiesByUsernameMapping(getDataSource());
    }
    
    /**
     * Query object to look up a user's authorities.
     */
    protected class AuthoritiesByUsernameMapping extends MappingSqlQuery {
        protected AuthoritiesByUsernameMapping(DataSource ds) {
            super(ds, getAuthoritiesByUsernameQuery());
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }

        protected Object mapRow(ResultSet rs, int rownum)
            throws SQLException {
            String roleName = getRolePrefix() + rs.getString(1);
            GrantedAuthorityImpl authority = new GrantedAuthorityImpl(roleName);

            return authority;
        }
    }
    
    /**
     * Query object to look up a user.
     */
    protected class UsersByUsernameMapping extends MappingSqlQuery {
        protected UsersByUsernameMapping(DataSource ds) {
            super(ds, getUsersByUsernameQuery());
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }

        protected Object mapRow(ResultSet rs, int rownum)
            throws SQLException {
            String username = rs.getString(1);
            String password = rs.getString(2);
            boolean enabled = rs.getBoolean(3);
            boolean credentialsNonExpired = rs.getBoolean(4);
            
            if (password == null) {
                //set the password to blank for users authenticated by an external Authentication source
                password = "";
            }
            UserDetails user = new User(username, password, enabled, true,
                    !credentialsNonExpired, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("HOLDER")});

            return user;
        }
    }
}
