package swkom_dms.minIO;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {
    @Value("${minio.bucket-name}")
    private String bucketName;
    @Value("${minio.access.name}")
    private String accessKey;
    @Value("${minio.access.secret}")
    private String accessSecret;
    @Value("${minio.endpoint}")
    private String minioEndpoint;
    @Value("${minio.endpoint-port}")
    private String minioEndpointPort;

    public String getBucketName() {
        return bucketName;
    }

    @Bean
    public MinioClient generateMinioClient() {
        try {
            return MinioClient.builder()
                    .endpoint(minioEndpoint, Integer.parseInt(minioEndpointPort), false)
                    .credentials(accessKey, accessSecret)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

