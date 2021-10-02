package com.example.bkapi.common.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: bkapi
 * @ClassName Minio
 * @description:
 * @author: zzm
 * @create: 2020-05-12 14:22
 * @Version 1.0
 **/
@ConfigurationProperties(prefix = "minio")
public class Minio {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String endpointIn;

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

    public String getEndpointIn() {
        return endpointIn;
    }

    public void setEndpointIn(String endpointIn) {
        this.endpointIn = endpointIn;
    }
}
