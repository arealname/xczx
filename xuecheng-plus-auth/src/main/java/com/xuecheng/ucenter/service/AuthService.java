package com.xuecheng.ucenter.service;


import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;


public interface AuthService {
    public XcUserExt excute(AuthParamsDto authParamsDto);
}
