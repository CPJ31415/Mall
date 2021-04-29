package com.huajuan.mall.controller;

import com.huajuan.mall.consts.MallConst;
import com.huajuan.mall.form.UserLoginForm;
import com.huajuan.mall.form.UserRegisterForm;
import com.huajuan.mall.pojo.User;
import com.huajuan.mall.service.IUserService;
import com.huajuan.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/user/register")
    public ResponseVo register(@Valid @RequestBody UserRegisterForm userRegisterForm) {

        User user = new User();
        BeanUtils.copyProperties(userRegisterForm, user);
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  HttpSession session) {

        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());

        //设置Session
        session.setAttribute(MallConst.CURRENT_USER, userResponseVo.getData());

        return userResponseVo;
    }

    /*
     * cookie 跨域
     * session id
     */

    //session保存在内存里, 改用token+redis
    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success(user);
    }

    @PostMapping("/user/logout")
    public ResponseVo logout(HttpSession session) {
        session.removeAttribute(MallConst.CURRENT_USER);

        return ResponseVo.success();
    }
}
