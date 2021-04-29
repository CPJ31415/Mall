package com.huajuan.mall.service.impl;

import com.huajuan.mall.MallApplicationTests;
import com.huajuan.mall.enums.ResponseEnum;
import com.huajuan.mall.enums.RoleEnum;
import com.huajuan.mall.pojo.User;
import com.huajuan.mall.service.IUserService;
import com.huajuan.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImplTest extends MallApplicationTests {

    public static final String USERNAME = "jack";

    public static final String PASSWORD = "123456";

    @Autowired
    private IUserService userService;

    @Before
    public void register() {
        User user = new User(USERNAME, PASSWORD, "jack@qq.com", RoleEnum.CUSTOMER.getCode());
        userService.register(user);
    }

    @Test
    public void login() {
        ResponseVo<User> responseVo = userService.login(USERNAME, PASSWORD);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}