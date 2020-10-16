package ru.redguy.tftpserver.datasource;

import ru.redguy.tftpserver.IDataSource;

import java.io.File;
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
}
