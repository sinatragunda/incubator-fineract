/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 November 2022 at 02:29
 */
package org.apache.fineract.infrastructure.filemanager.helper;

import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.filemanager.data.FolderData;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FolderIteratorHelper {

    public static List<FolderData> files(){


        String env = System.getenv("WESE_LOG_ENV");
        String tenant = ThreadLocalContextUtil.getTenant().getTenantIdentifier();

        String logsPath = String.format("%s/%s",env ,tenant);

        System.err.println("--------------fullpath is ---------"+logsPath);

        Path path = Paths.get(logsPath);
        
        List<FolderData> folderDataList  = new ArrayList();
        Stream<Path> pathStream = null ;
        try {
            List<Path> pathList   = Files.list(path).collect(Collectors.toList());
            pathList.forEach(e->{
                File file = e.toFile();
                String filename = file.getName();
                String fileUrl = file.getAbsolutePath();
                FolderData folderData = new FolderData(filename ,fileUrl);
                folderDataList.add(folderData);

            });
        }
        catch (Exception i){
            System.err.println("----------------patha does not exists yet ----------");
        }

        System.err.println("---------------root path for this function");
        folderDataList.forEach(e->{
            System.err.println("----------------filename --------------"+e.getFilename());
        });

        return folderDataList;
    }
}
