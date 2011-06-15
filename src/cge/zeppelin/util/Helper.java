package cge.zeppelin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Helper {
    
    /**
     * Convenience function to map a variable from one coordinate space
     * to another. Equivalent to unlerp() followed by lerp().
     * http://www.google.com/codesearch/p?hl=en#Ej56LtI_pY0/trunk/processing/core/src/processing/core/PApplet.java&q=map%20package:http://processing%5C.googlecode%5C.com&sa=N&cd=4&ct=rc&l=3353
     */
    public final static float map(float value,
                                  float istart, float istop,
                                  float ostart, float ostop) {
      return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }


    public final static InputStream getInputStreamResource(String filename) throws FileNotFoundException {
    	
        InputStream is = new FileInputStream("./ressources/" + filename);
        if (is == null)
            throw new RuntimeException("Resource not found: " + filename);
        return is;
    }

    public final static File getFileResource(String filename) throws FileNotFoundException {
    	
        File file = new File("./ressources/" + filename);
        if (!file.exists())
            throw new RuntimeException("Resource not found: " + filename);
        return file;
    }
}
