package com.example.worker.minio;

import java.io.InputStream;

public interface FileStorage {
    void upload(String objectName, InputStream file);
    byte[] download(String objectName) ;
}
