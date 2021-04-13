package ru.redguy.tftpserver.datasource;

import java.io.InputStream;
import java.io.OutputStream;

public interface IDataSource {
    public boolean isFileExists(String file);
    public boolean isFile(String file);
    public boolean isCanRead(String file);

    public OutputStream getOutputStream(String file);

    public InputStream getInputStream(String file);

    public void delete(String file);
}
