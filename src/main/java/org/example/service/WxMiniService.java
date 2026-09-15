package org.example.service;

import java.util.Map;

/**
 * 微信小程序 Service 接口
 */
public interface WxMiniService {

    /**
     * 微信小程序登录
     * 1. 调用微信 jscode2session 接口获取 openid
     * 2. 根据 openid 查询用户，不存在则自动注册
     * 3. 生成 JWT token 返回
     *
     * @param code 小程序端 wx.login() 获取的临时登录凭证
     * @return 包含 token 和用户信息的 Map
     */
    Map<String, Object> wxLogin(String code);
}
