package swkom_dms.minIO;

import java.io.InputStream;

public interface FileStorage {
    void upload(String objectName, InputStream file);
    byte[] download(String objectName) ;
}

