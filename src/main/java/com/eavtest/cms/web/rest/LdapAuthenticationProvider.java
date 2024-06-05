package com.eavtest.cms.web.rest;

import java.util.List;
import javax.naming.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {

    private LdapContextSource contextSource;
    private LdapTemplate ldapTemplate;

    private void initContext(Authentication authentication) {
        contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://10.10.11.200:389/uid=" + authentication.getName() + ",ou=people,dc=innovaway,dc=it");
        contextSource.setAnonymousReadOnly(true);
        contextSource.setUserDn("uid=" + authentication.getName() + ",ou=people");
        contextSource.setPassword(authentication.getCredentials().toString());
        contextSource.afterPropertiesSet();

        ldapTemplate = new LdapTemplate(contextSource);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        initContext(authentication);
        Filter filter = new EqualsFilter("uid", authentication.getName());

        boolean authenticate = ldapTemplate.authenticate(
            LdapUtils.emptyLdapName(),
            filter.encode(),
            authentication.getCredentials().toString()
        );

        if (authenticate) {
            String userById = getGidNumberByUid(authentication.getName(), authentication);
            String roleByUid = getGroupCnByGidNumber(userById);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials().toString(),
                List.of(new SimpleGrantedAuthority("ROLE_" + roleByUid))
            );

            return auth;
        } else {
            throw new IllegalStateException("Wrong credentials");
        }
    }

    private String getGidNumberByUid(String uid, Authentication authentication) {
        List<String> usernames = ldapTemplate.search(
            "ldap://10.10.11.200:389/uid=" + authentication.getName() + ",ou=people,dc=innovaway,dc=it",
            "(uid=" + uid + ")",
            (AttributesMapper<String>) attrs -> {
                try {
                    return (String) attrs.get("gidNumber").get();
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        );

        if (!usernames.isEmpty()) {
            return usernames.get(0);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    private String getGroupCnByGidNumber(String gidNumber) {
        List<String> usernames = ldapTemplate.search(
            "ldap://10.10.11.200:389/ou=groups,dc=innovaway,dc=it",
            "(gidNumber=" + gidNumber + ")",
            (AttributesMapper<String>) attrs -> {
                try {
                    return (String) attrs.get("cn").get().toString().toUpperCase();
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        );

        if (!usernames.isEmpty()) {
            return usernames.get(0);
        } else {
            throw new IllegalStateException("Group not found with given gidNumber: " + gidNumber);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
