package com.huajuan.mall.service.impl;

import com.huajuan.mall.dao.UserMapper;
import com.huajuan.mall.enums.ResponseEnum;
import com.huajuan.mall.enums.RoleEnum;
import com.huajuan.mall.pojo.User;
import com.huajuan.mall.service.IUserService;
import com.huajuan.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.huajuan.mall.enums.ResponseEnum.*;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseVo<User> register(User user) {

        int countByUsername = userMapper.countByUsername(user.getUsername());
        if (countByUsername > 0) {
            return ResponseVo.error(USERNAME_EXIST);
        }

        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByEmail > 0) {
            throw new RuntimeException("该邮箱已注册");
        }

        user.setRole(RoleEnum.CUSTOMER.getCode());
        //MD5
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

        //写入数据库
        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0) {
            return ResponseVo.error(ERROR);
        }

        return ResponseVo.success();
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            //用户不存在（返回：用户名或密码错误 ）
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        if (!user.getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            //密码错误(返回：用户名或密码错误 )
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        user.setPassword("");
        return ResponseVo.success(user);
    }
}
