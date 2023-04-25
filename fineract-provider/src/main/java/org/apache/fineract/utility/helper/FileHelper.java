/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 25 April 2023 at 09:42
 */
package org.apache.fineract.utility.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class FileHelper {


    public static File fromInputStream(InputStream inputStream ,String extension){

        Random random = new Random(TimeHelperEx.now());
        String filename = String.format("Temp_jar_file_%s.%s" ,String.valueOf(random.nextInt()) ,extension);
        System.err.println("---------------------------filename is "+filename);
        File file = new File(filename);
        int BUFFER_SIZE = 1024 ;
        try {
            file.createNewFile();
            byte[] b = new byte[BUFFER_SIZE];
            int read ;
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            while((read = inputStream.read(b)) != -1 ){
                fileOutputStream.write(b ,0 ,read);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.err.println("--------------is file still same data or need to be read again ? "+file.getTotalSpace());
        return file;
    }
}
