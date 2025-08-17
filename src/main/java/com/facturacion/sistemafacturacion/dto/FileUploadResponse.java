package com.facturacion.sistemafacturacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String fileName;
    private String s3Key;
    private String fileUrl;
    private String message;

    public FileUploadResponse(String fileName, String s3Key, String fileUrl) {
        this.fileName = fileName;
        this.s3Key = s3Key;
        this.fileUrl = fileUrl;
        this.message = "Archivo cargado correctamente";
    }
}
