package com.huajuan.mall.service;

import com.huajuan.mall.pojo.User;
import com.huajuan.mall.vo.ResponseVo;

public interface IUserService {
    /*
     注册
     */
    ResponseVo<User> register(User user);

    /*
     登录
     */
    ResponseVo<User> login(String username, String password);
}
