package org.isf.xmpp.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.File;

public class FileDTO {
    @NotNull
    @ApiModelProperty(notes = "user of the file", example="Mario", position = 0)
    String user;

    @NotNull
    @ApiModelProperty(notes = "name of the file", example="Test.pdf", position = 0)
    String filename;

    @NotNull
    @ApiModelProperty(notes = "BlobFile", example="", position = 1)
    private byte[] blobFile;

    @ApiModelProperty(notes = "description of the file", example="Mario", position = 2)
    String description;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getBlobFile() {
        return blobFile;
    }

    public void setBlobFile(byte[] blobFile) {
        this.blobFile = blobFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
