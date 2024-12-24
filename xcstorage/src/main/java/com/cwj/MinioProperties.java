package com.cwj;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@ConfigurationProperties(prefix = "cwj.minio")
@Data
public class MinioProperties implements Serializable {
    private String minioDomain;
    private String minioPublicKey;
    private String minioSecretKey;
    private String minioBucket;
}
