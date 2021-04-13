package ru.redguy.tftpserver.datasource;

import java.io.*;
import java.nio.file.Path;

public class FileSystem implements IDataSource {

    String path;

    public FileSystem(File file) {
        path = file.getAbsolutePath()+"/";
    }

    public FileSystem(Path path) {
        this.path = path.toAbsolutePath().toString()+"/";
    }

    public FileSystem(String path) {
        this.path = path;
        if(!this.path.endsWith("/")) {
            this.path = this.path+"/";
        }
    }

    @Override
    public boolean isFileExists(String localPath) {
        return new File(this.path,localPath).exists();
    }

    @Override
    public boolean isFile(String file) {
        return new File(this.path,file).isFile();
    }

    @Override
    public boolean isCanRead(String file) {
        return new File(this.path,file).canRead();
    }

    @Override
    public OutputStream getOutputStream(String file) {
        try {
            return new FileOutputStream(new File(this.path,file));
        } catch (FileNotFoundException e) {
            try {
                new File(this.path,file).createNewFile();
                return new FileOutputStream(new File(this.path,file));
            } catch (IOException ioException) {
                new File(this.path,file).getParentFile().mkdirs();
                try {
                    new File(this.path,file).createNewFile();
                    return new FileOutputStream(new File(this.path,file));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(String file) {
        try {
            return new FileInputStream(new File(this.path,file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public void delete(String file) {
        new File(this.path,file).delete();
    }
}
