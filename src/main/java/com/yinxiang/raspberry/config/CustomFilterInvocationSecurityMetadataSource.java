package com.yinxiang.raspberry.config;

import com.yinxiang.raspberry.mapper.MenuMapper;
import com.yinxiang.raspberry.model.Menu;
import com.yinxiang.raspberry.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

@Component
public class CustomFilterInvocationSecurityMetadataSource
        implements FilterInvocationSecurityMetadataSource {
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Autowired
    MenuMapper menuMapper;
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object)
            throws IllegalArgumentException {
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        List<Menu> allMenus = menuMapper.getAllMenus();   //只有这里用到了getAllMenus()
        for(Menu menu : allMenus) {
            //System.out.println(menu.getPattern());
            for(Role role:menu.getRoles()) {
                //System.out.println(role.getName());
            }
            //System.out.println("*******");
        }
        //System.out.println("requestURL  "+requestUrl);
        //System.out.println("---------------");
        for (Menu menu : allMenus) {
            if (antPathMatcher.match(menu.getPattern(), requestUrl)) {
                    List<Role> roles = menu.getRoles();
                    String[] roleArr = new String[roles.size()];
                    for (int i = 0; i < roleArr.length; i++) {
                        roleArr[i] = roles.get(i).getName();
                        //System.out.println("有哪些角色:"+roles.get(i).getName());
                    }
                    return SecurityConfig.createList(roleArr);
            }
        }
        return SecurityConfig.createList("ROLE_LOGIN");
    }
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
