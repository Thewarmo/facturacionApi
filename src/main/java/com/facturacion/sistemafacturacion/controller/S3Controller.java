package com.facturacion.sistemafacturacion.controller;

import com.facturacion.sistemafacturacion.dto.FileUploadResponse;
import com.facturacion.sistemafacturacion.service.IS3Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class S3Controller {

    @Autowired
    private IS3Service is3Service;

//    @PostMapping("/upload")
//    public FileUploadResponse uploadFile(@RequestParam("file")MultipartFile file) throws IOException {
//        return is3Service.uploadFile(file);
//
//    }

    @PostMapping("/upload/{productoId}")
    public ResponseEntity<?> uploadImagenProducto(
            @PathVariable Long productoId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validaciones básicas
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El archivo está vacío"));
            }

            // Validar tipo de archivo (opcional)
            String contentType = file.getContentType();
            if (contentType == null || !isValidImageType(contentType)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Tipo de archivo no válido. Solo se permiten imágenes."));
            }

            // Validar tamaño del archivo (opcional - ejemplo: 5MB máximo)
            if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El archivo es demasiado grande. Máximo 5MB."));
            }

            // Subir imagen y asociarla al producto
            Map<String, Object> response = is3Service.uploadImagenParaProducto(productoId, file);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar el archivo: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validaciones básicas
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El archivo está vacío"));
            }

            // Validar tipo de archivo (opcional)
            String contentType = file.getContentType();
            if (contentType == null || !isValidImageType(contentType)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Tipo de archivo no válido. Solo se permiten imágenes."));
            }

            // Validar tamaño del archivo (opcional - ejemplo: 5MB máximo)
            if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El archivo es demasiado grande. Máximo 5MB."));
            }

            FileUploadResponse response = is3Service.uploadFile(file);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar el archivo: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("s3Key") String s3Key) {
        try {
            is3Service.deleteFile(s3Key);
            return ResponseEntity.ok(Map.of("message", "Archivo eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar archivo: " + e.getMessage()));
        }
    }

    @GetMapping("/url")
    public ResponseEntity<?> getFileUrl(@RequestParam("s3Key") String s3Key) {
        try {
            String url = is3Service.getFileUrlByS3Key(s3Key);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener URL: " + e.getMessage()));
        }
    }

    @GetMapping("/imagen/producto/{productoId}")
    public ResponseEntity<?> getImagenProducto(@PathVariable Long productoId) {
        try {
            Map<String, Object> response = is3Service.getImagenProducto(productoId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener imagen: " + e.getMessage()));
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileData = is3Service.downloadFile("tienda/" + fileName);

            // Determinar el tipo de contenido basado en la extensión del archivo
            String contentType = determineContentType(fileName);

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                    .body(fileData);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Archivo no encontrado: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al descargar archivo: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/stream/producto/{productoId}")
    public ResponseEntity<byte[]> streamImagenProducto(@PathVariable Long productoId) {
        try {
            Map<String, Object> imageData = is3Service.downloadImagenProducto(productoId);
            byte[] fileData = (byte[]) imageData.get("data");
            String fileName = (String) imageData.get("fileName");

            String contentType = determineContentType(fileName);

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                    .header("Cache-Control", "max-age=3600") // Cache por 1 hora
                    .body(fileData);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Imagen no encontrada: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al obtener imagen: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/url/signed/{fileName}")
    public ResponseEntity<?> getSignedUrl(@PathVariable String fileName,
                                          @RequestParam(defaultValue = "3600") int expirationSeconds) {
        try {
            String signedUrl = is3Service.generatePresignedUrl("tienda/" + fileName, expirationSeconds);
            return ResponseEntity.ok(Map.of(
                    "signedUrl", signedUrl,
                    "expiresIn", expirationSeconds + " segundos"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al generar URL firmada: " + e.getMessage()));
        }
    }

    private String determineContentType(String fileName) {
        String extension = "";
        if (fileName != null && fileName.lastIndexOf('.') > 0) {
            extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        }

        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "pdf" -> "application/pdf";
            default -> "application/octet-stream";
        };
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp");
    }


}
