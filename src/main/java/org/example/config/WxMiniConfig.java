package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置类
 * 读取 application.yml 中 wx.mini 下的 appid 和 appsecret
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.mini")
public class WxMiniConfig {

    /** 小程序 AppID */
    private String appid;

    /** 小程序 AppSecret */
    private String appsecret;
}
