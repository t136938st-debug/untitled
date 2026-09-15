package org.example.controller;

import org.example.common.Result;
import org.example.dto.LoginDTO;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户Controller
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 顾客登录
     */
    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        // 校验是否为顾客角色
        if (user.getRole() != 1) {
            return Result.error("该账号不是顾客账号");
        }
        return Result.success(user);
    }

    /**
     * 管理员登录
     */
    @PostMapping("/admin/login")
    public Result<User> adminLogin(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        // 校验是否为管理员角色
        if (user.getRole() != 2) {
            return Result.error("该账号不是管理员账号");
        }
        return Result.success(user);
    }

    /**
     * 根据ID查询用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}
