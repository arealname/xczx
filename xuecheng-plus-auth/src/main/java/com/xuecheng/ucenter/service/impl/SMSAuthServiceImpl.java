package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feign.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("sms_authservice")
public class SMSAuthServiceImpl implements AuthService {


    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;
    @Override
    public XcUserExt excute(AuthParamsDto authParamsDto) {
        String cellphone = authParamsDto.getCellphone();

        String checkcode=authParamsDto.getCheckcode();
        String checkcodekey=authParamsDto.getCheckcodekey();

        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone, cellphone));
        if(user==null){
            //返回空表示用户不存在
            throw new RuntimeException("账号不存在");
        }


        //校验密码
        //取出数据库存储的正确密码

        boolean matches =checkCodeClient.verify(checkcodekey,checkcode);
        if(!matches){
            throw new RuntimeException("账号或验证码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user,xcUserExt);

        return xcUserExt;

    }
}
