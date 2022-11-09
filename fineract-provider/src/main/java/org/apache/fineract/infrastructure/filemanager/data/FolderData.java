/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 November 2022 at 03:43
 */
package org.apache.fineract.infrastructure.filemanager.data;

public class FolderData {


    private final String filename ;
    private final String fileUrl ;

    public FolderData(final String filename,final String fileUrl) {
        this.filename = filename;
        this.fileUrl = fileUrl;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
