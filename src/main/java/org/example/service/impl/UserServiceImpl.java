package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.CustomException;
import org.example.dto.LoginDTO;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户登录
     */
    @Override
    public User login(LoginDTO loginDTO) {
        // 根据用户名查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = this.getOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            throw new CustomException("用户名或密码错误");
        }

        // 校验密码（微信登录用户无密码，不能通过账号密码登录）
        if (user.getPassword() == null || !user.getPassword().equals(loginDTO.getPassword())) {
            throw new CustomException("用户名或密码错误");
        }

        // 密码脱敏，不返回给前端
        user.setPassword(null);
        return user;
    }
}
