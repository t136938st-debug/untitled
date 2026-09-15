package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.LoginDTO;
import org.example.entity.User;

/**
 * 用户Service接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录成功的用户信息（不含密码）
     */
    User login(LoginDTO loginDTO);
}
