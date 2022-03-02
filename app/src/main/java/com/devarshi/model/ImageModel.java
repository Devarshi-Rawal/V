package com.devarshi.model;

import java.io.File;

public class ImageModel {

    File file;
    java.io.File db_file;
    com.google.api.services.drive.model.File fileId;

    public com.google.api.services.drive.model.File getFileId() {
        return fileId;
    }

    public void setFileId(com.google.api.services.drive.model.File fileId) {
        this.fileId = fileId;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getDb_file() {
        return db_file;
    }

    public void setDb_file(File db_file) {
        this.db_file = db_file;
    }
}
