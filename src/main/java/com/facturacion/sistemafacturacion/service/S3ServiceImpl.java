package com.facturacion.sistemafacturacion.service;


import com.facturacion.sistemafacturacion.dto.FileUploadResponse;
import com.facturacion.sistemafacturacion.model.Producto;
import com.facturacion.sistemafacturacion.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class S3ServiceImpl implements IS3Service {

    private final S3Client s3client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static:us-east-1}")
    private String region;

    private final ProductoRepository productoRepository;

    @Autowired
    public S3ServiceImpl(S3Client s3client, ProductoRepository productoRepository){
        this.s3client = s3client;
        this.productoRepository = productoRepository;
    }

    public Map<String, Object> getImagenProducto(Long productoId) {
        // Verificar que el producto existe
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isEmpty()) {
            throw new RuntimeException("Producto con ID " + productoId + " no encontrado");
        }

        Producto producto = productoOpt.get();

        if (producto.getUrlImagen() == null || producto.getUrlImagen().isEmpty()) {
            throw new RuntimeException("El producto no tiene imagen asociada");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("productoId", productoId);
        response.put("urlImagen", producto.getUrlImagen());
        response.put("producto", producto);

        return response;
    }

    public byte[] downloadFile(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3client.getObject(getObjectRequest);
            return s3Object.readAllBytes();

        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new RuntimeException("Archivo no encontrado en S3");
            }
            throw new RuntimeException("Error al descargar archivo de S3: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo: " + e.getMessage());
        }
    }

    public Map<String, Object> downloadImagenProducto(Long productoId) {
        // Verificar que el producto existe
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isEmpty()) {
            throw new RuntimeException("Producto con ID " + productoId + " no encontrado");
        }

        Producto producto = productoOpt.get();

        if (producto.getUrlImagen() == null || producto.getUrlImagen().isEmpty()) {
            throw new RuntimeException("El producto no tiene imagen asociada");
        }

        // Extraer la clave S3 de la URL
        String s3Key = extractS3KeyFromUrl(producto.getUrlImagen());
        if (s3Key == null) {
            throw new RuntimeException("No se pudo extraer la clave S3 de la URL");
        }

        // Descargar el archivo
        byte[] fileData = downloadFile(s3Key);

        // Extraer nombre del archivo de la clave S3
        String fileName = s3Key.substring(s3Key.lastIndexOf('/') + 1);

        Map<String, Object> response = new HashMap<>();
        response.put("data", fileData);
        response.put("fileName", fileName);
        response.put("productoId", productoId);
        response.put("s3Key", s3Key);

        return response;
    }

    public String generatePresignedUrl(String s3Key, int expirationSeconds) {
        try {
            // Crear el presigner
            try (S3Presigner presigner = S3Presigner.create()) {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build();

                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(expirationSeconds))
                        .getObjectRequest(getObjectRequest)
                        .build();

                PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
                return presignedRequest.url().toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al generar URL pre-firmada: " + e.getMessage());
        }
    }

    public Map<String, Object> uploadImagenParaProducto(Long productoId, MultipartFile file) throws IOException {
        // Verificar que el producto existe
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isEmpty()) {
            throw new RuntimeException("Producto con ID " + productoId + " no encontrado");
        }

        Producto producto = productoOpt.get();

        // Si el producto ya tiene una imagen, eliminar la anterior de S3
        if (producto.getUrlImagen() != null && !producto.getUrlImagen().isEmpty()) {
            try {
                // Extraer la clave S3 de la URL existente para eliminar el archivo anterior
                String oldKey = extractS3KeyFromUrl(producto.getUrlImagen());
                if (oldKey != null) {
                    deleteFile(oldKey);
                }
            } catch (Exception e) {
                System.err.println("Error al eliminar imagen anterior: " + e.getMessage());
            }
        }

        try {
            // Generar nombre único para el archivo
            String uniqueFileName = generateFileName(file.getOriginalFilename());
            String s3Key = "tienda/" + uniqueFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();

            s3client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // Construir la URL del archivo
            String fileUrl = getFileUrl(uniqueFileName);

            // Actualizar el producto con la URL de la imagen
            producto.setUrlImagen(fileUrl);
            productoRepository.save(producto);

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Imagen subida y asociada al producto correctamente");
            response.put("productoId", productoId);
            response.put("fileName", uniqueFileName);
            response.put("s3Key", s3Key);
            response.put("fileUrl", fileUrl);
            response.put("producto", producto);

            return response;

        } catch (IOException e) {
            throw new IOException("Error al subir archivo a S3: " + e.getMessage());
        } catch (S3Exception e) {
            throw new RuntimeException("Error de S3: " + e.getMessage());
        }
    }

    public FileUploadResponse uploadFile(MultipartFile file) throws IOException {
        try {
            // Generar nombre único para el archivo
            String uniqueFileName = generateFileName(file.getOriginalFilename());
            String s3Key = "tienda/" + uniqueFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();

            s3client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // Construir la URL del archivo
            String fileUrl = getFileUrl(uniqueFileName);

            return new FileUploadResponse(
                    uniqueFileName,
                    s3Key,
                    fileUrl
            );

        } catch (IOException e) {
            throw new IOException("Error al subir archivo a S3: " + e.getMessage());
        } catch (S3Exception e) {
            throw new RuntimeException("Error de S3: " + e.getMessage());
        }
    }

    public void deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("Error al eliminar archivo de S3: " + e.getMessage());
        }
    }

    public String getFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/tienda/%s",
                bucketName, region, fileName);
    }

    public String getFileUrlByS3Key(String s3Key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, s3Key);
    }

    // Generar nombre único para evitar conflictos
    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.lastIndexOf('.') > 0) {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        }

        // Crear un nombre único con timestamp y UUID
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        return String.format("%s_%s%s", timestamp, uuid, extension);
    }

    // Método auxiliar para extraer la clave S3 de una URL
    private String extractS3KeyFromUrl(String url) {
        try {
            // Formato esperado: https://bucket.s3.region.amazonaws.com/tienda/filename
            String[] parts = url.split("/");
            if (parts.length >= 2) {
                // Obtener las últimas dos partes: "tienda" y "filename"
                return parts[parts.length - 2] + "/" + parts[parts.length - 1];
            }
        } catch (Exception e) {
            System.err.println("Error al extraer clave S3 de URL: " + e.getMessage());
        }
        return null;
    }
}
