package com.xuecheng.ucenter.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feign.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("password_authservice")
public class PassWordAuthServiceImpl implements AuthService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;

    @Autowired
    XcMenuMapper xcMenuMapper;

    @Override
    public XcUserExt excute(AuthParamsDto authParamsDto) {
        //校验验证码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();

        if (StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)) {
            throw new RuntimeException("验证码为空");

        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (!verify) {
            throw new RuntimeException("验证码输入错误");
        }
        System.out.println("验证码正确。。。。。。。。。。。。。。。。");


        //账号
        String username = authParamsDto.getUsername();
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (user == null) {
            //返回空表示用户不存在
            throw new RuntimeException("账号不存在");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user, xcUserExt);
        //校验密码
        //取出数据库存储的正确密码
        String passwordDb = user.getPassword();
        String passwordForm = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }
        System.out.println("密码正确。。。。。。。。。。。。。。。。");

        return xcUserExt;
    }
}
