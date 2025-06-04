package ru.viktorgezz.pizza_resource_service.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3Service.class);

    private final AmazonS3 s3Client;
    private final String bucketName;

    @Autowired
    public S3Service(
            AmazonS3 s3Client,
            @Value("${custom.minio.bucket-name}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename() + "_" + UUID.randomUUID().toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        s3Client.putObject(new PutObjectRequest(
                bucketName,
                fileName,
                file.getInputStream(),
                metadata
        ));

        return s3Client.getUrl(
                        bucketName,
                        fileName
                )
                .toString();
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    public byte[] getFileByUrl(String fileUrl) {
        try {
            // Извлекаем имя файла из URL
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            
            // Получаем объект из S3
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            
            // Получаем поток данных
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            
            try {
                // Читаем все байты из потока
                return IOUtils.toByteArray(inputStream);
            } finally {
                // Закрываем поток
                inputStream.close();
                s3Object.close();
            }
        } catch (AmazonServiceException e) {
            log.error("Ошибка получения файла из S3: {}", e.getMessage());
            throw new RuntimeException("Error getting file from S3", e);
        } catch (IOException e) {
            log.error("Ошибка чтения файла: {}", e.getMessage());
            throw new RuntimeException("Error reading file from S3", e);
        }
    }

    public ObjectMetadata getFileMetadata(String fileName) {
        try {
            return s3Client.getObjectMetadata(bucketName, fileName);
        } catch (AmazonServiceException e) {
            log.error("Ошибка получения мета-данных файла {}", e.getMessage());
            throw new RuntimeException("Error getting file metadata from S3", e);
        }
    }
} 