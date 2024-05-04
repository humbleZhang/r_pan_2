package com.imooc.pan.server.common.config;

import com.imooc.pan.core.constants.RPanConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.imooc.pan.server")
@Data
public class PanServerConfig {

    @Value("${server.port}")
    private Integer serverPort;

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = RPanConstants.ONE_INT;

    /**
     * 分享链接的前缀
     */
    private String sharePrefix = "http://127.0.0.1:" + serverPort + "/share/";

}
