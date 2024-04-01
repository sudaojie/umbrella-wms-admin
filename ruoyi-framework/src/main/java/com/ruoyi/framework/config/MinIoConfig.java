package com.ruoyi.framework.config;


import com.ruoyi.common.constant.Constants;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class MinIoConfig {

    /**
     * minio 地址
     */
    // @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * minio accessKey
     */
    // @Value("${minio.accessKey}")
    private String accessKey;

    /**
     * minio secretKey
     */
    // @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient getMinioClient(@Value("${service.uploadType}")String uploadType) {
        if(uploadType.equals(Constants.UPLOAD_TYPE_MINIO)){
            log.info("<========获取MinioClient连接对象========>");
            log.info("Minio配置参数:endpoint:{},accessKey{},secretKey{}",endpoint,accessKey,secretKey);
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            return minioClient;
        }else{
            log.error("当前附件上传方式不支持MinIo上传");
            return null;
        }
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}


