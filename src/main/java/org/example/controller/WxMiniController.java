package org.example.controller;

import org.example.common.Result;
import org.example.service.WxMiniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信小程序 Controller
 */
@RestController
@RequestMapping("/wx")
public class WxMiniController {

    @Autowired
    private WxMiniService wxMiniService;

    /**
     * 微信小程序登录接口
     * 小程序端调用 wx.login() 获取 code，传入此接口换取 token
     *
     * @param params 包含 code 字段
     * @return 包含 token 和用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        if (code == null || code.isEmpty()) {
            return Result.error("code不能为空");
        }

        Map<String, Object> data = wxMiniService.wxLogin(code);
        return Result.success(data);
    }
}
