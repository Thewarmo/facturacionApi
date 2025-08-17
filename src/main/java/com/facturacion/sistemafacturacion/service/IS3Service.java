package com.facturacion.sistemafacturacion.service;

import com.facturacion.sistemafacturacion.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IS3Service {
    FileUploadResponse uploadFile(MultipartFile file) throws IOException;
    Map<String, Object> uploadImagenParaProducto(Long productoId, MultipartFile file) throws IOException;
    void deleteFile(String s3Key);
    String getFileUrl(String fileName);
    String getFileUrlByS3Key(String s3Key);
    Map<String, Object> getImagenProducto(Long productoId);
    byte[] downloadFile(String s3Key);
    Map<String, Object> downloadImagenProducto(Long productoId);
    String generatePresignedUrl(String s3Key, int expirationSeconds);
}
