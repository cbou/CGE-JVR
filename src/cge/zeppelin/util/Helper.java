package cge.zeppelin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Helper {
    
    public final static InputStream getInputStreamResource(String filename) throws FileNotFoundException {
    	
        InputStream is = new FileInputStream("./resources/" + filename);
        if (is == null)
            throw new RuntimeException("Resource not found: " + filename);
        return is;
    }

    public final static File getFileResource(String filename) throws FileNotFoundException {
    	
        File file = new File("./resources/" + filename);
        if (!file.exists())
            throw new RuntimeException("Resource not found: " + filename);
        return file;
    }
}
