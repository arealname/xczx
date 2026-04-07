package com.xuecheng.ucenter.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private ApplicationContext applicationContext;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;

        try {
            authParamsDto = JSON.parseObject(username, AuthParamsDto.class);
        } catch (Exception e) {
            System.out.println("格式错误");
        }

        String authType = authParamsDto.getAuthType();

        AuthService pas = applicationContext.getBean(authType + "_authservice", AuthService.class);
        System.out.println("进入" + authType + "_authservice" + "进行验证");
        XcUserExt excute1 = pas.excute(authParamsDto);


        UserDetails userPrincipal = getUserPrincipal(excute1);
        System.out.println(userPrincipal);
        return userPrincipal;
    }

    @Autowired
    private XcMenuMapper xcMenuMapper;

    public UserDetails getUserPrincipal(XcUserExt user) {
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String id = user.getId();
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(id);
        List<String> permissions = new ArrayList<>();

        if (xcMenus.size() <= 0) {
            //用户权限,如果不加则报Cannot pass a null GrantedAuthority collection
            permissions.add("p1");
        } else {
            xcMenus.forEach(menu -> {
                permissions.add(menu.getCode());
            });
        }
        String password = user.getPassword();
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象

        String[] array = permissions.toArray(new String[0]);
        UserDetails userDetails = User.withUsername(userString).password(password).authorities(array).build();
        return userDetails;
    }


}
