package com.example.manager.photos.takeorchoseimg;

import java.io.Serializable;

public class FileRepresentation implements Serializable {
    private String id;
    private String fileName;
    private String url;
    private long fileSize;


    public FileRepresentation() {
    }

    public FileRepresentation(String id, String fileName, String url, long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
        this.fileSize = fileSize;
    }

    public String getId() {
        return id;
    }

    public String getSimpleName() {
        return fileName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
