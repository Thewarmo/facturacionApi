package com.facturacion.sistemafacturacion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3config {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static:us-east-1}")
    private String region;

    @Bean
    public S3Client s3Client() {

        System.out.println("=== AWS S3 CONFIG DEBUG ===");
        System.out.println("Access Key presente: " + (accessKey != null && !accessKey.isEmpty()));
        System.out.println("Secret Key presente: " + (secretKey != null && !secretKey.isEmpty()));
        System.out.println("Region: " + region);

        if (accessKey == null || accessKey.trim().isEmpty()) {
            throw new IllegalStateException("AWS Access Key no está configurado. Verifica AWS_ACCESS_KEY_ID en Render.");
        }

        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("AWS Secret Key no está configurado. Verifica AWS_SECRET_ACCESS_KEY en Render.");
        }

        try {
            Region awsRegion = Region.of(region.toLowerCase().replace('_', '-'));
            AwsCredentials credentials = AwsBasicCredentials.create(accessKey.trim(), secretKey.trim());

            S3Client s3Client = S3Client.builder()
                    .region(awsRegion)
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();

            System.out.println("✅ S3Client creado exitosamente para región: " + awsRegion);
            return s3Client;

        } catch (Exception e) {
            System.err.println("❌ Error creando S3Client: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create S3Client", e);
        }
    }
}