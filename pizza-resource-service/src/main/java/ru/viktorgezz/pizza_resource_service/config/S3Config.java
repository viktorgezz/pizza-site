package ru.viktorgezz.pizza_resource_service.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    private static final  String SIGNING_REGION = "us-east-1";

    private final String URL_S3;
    private final String ROOT_USER;
    private final String ROOT_PASSWORD;


    public S3Config(
            @Value("${custom.url.s3}") String urlS3,
            @Value("${custom.minio.root-user}") String rootUser,
            @Value("${custom.minio.root-password}") String rootPassword
    ) {
        this.URL_S3 = urlS3;
        this.ROOT_USER = rootUser;
        this.ROOT_PASSWORD = rootPassword;
    }

    @Bean
    public AmazonS3 amazonS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                URL_S3,
                                SIGNING_REGION
                        )
                )
                .withPathStyleAccessEnabled(true)
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        ROOT_USER,
                                        ROOT_PASSWORD
                                )
                        )
                )
                .build();
    }
} 