package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.common.CustomException;
import org.example.common.JwtUtils;
import org.example.config.WxMiniConfig;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.service.WxMiniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序 Service 实现类
 */
@Service
@Slf4j
public class WxMiniServiceImpl implements WxMiniService {

    /** 微信 jscode2session 接口地址 */
    private static final String WX_LOGIN_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    @Autowired
    private WxMiniConfig wxMiniConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Map<String, Object> wxLogin(String code) {
        // 1. 调用微信接口获取 openid
        String openid = getOpenId(code);
        log.info("微信登录获取openid: {}", openid);

        // 2. 根据 openid 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid, openid);
        User user = userService.getOne(queryWrapper);

        // 3. 如果用户不存在，自动注册
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setUsername("wx_" + openid.substring(0, 8));
            user.setName("微信用户");
            user.setRole(1); // 默认顾客角色
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setDeleted(0);
            userService.save(user);
            log.info("微信小程序自动注册新用户，openid: {}", openid);
        }

        // 4. 生成 JWT Token
        String token = jwtUtils.generateToken(user.getId(), openid);

        // 5. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("name", user.getName());
        result.put("avatar", user.getAvatar());
        result.put("role", user.getRole());

        return result;
    }

    /**
     * 调用微信 jscode2session 接口获取 openid
     *
     * @param code 小程序端 wx.login() 获取的临时登录凭证
     * @return openid
     */
    private String getOpenId(String code) {
        String url = String.format(WX_LOGIN_URL,
                wxMiniConfig.getAppid(),
                wxMiniConfig.getAppsecret(),
                code);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String json = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.info("微信jscode2session响应: {}", json);

                JSONObject jsonObject = JSON.parseObject(json);

                // 微信接口返回错误
                if (jsonObject.containsKey("errcode") && jsonObject.getIntValue("errcode") != 0) {
                    String errmsg = jsonObject.getString("errmsg");
                    log.error("微信登录失败: {}", errmsg);
                    throw new CustomException("微信登录失败: " + errmsg);
                }

                String openid = jsonObject.getString("openid");
                if (openid == null || openid.isEmpty()) {
                    throw new CustomException("微信登录失败: 未获取到openid");
                }
                return openid;
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new CustomException("调用微信接口异常，请稍后再试");
        }
    }
}
