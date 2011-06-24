package cge.zeppelin.prototype.particule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Resource {

    private Class<?> base;

    public Resource(Class<?> b) {
        base = b;
    }

    public InputStream getResource(String filename) {
        InputStream is = null;
		try {
			is = new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (is == null)
            throw new RuntimeException("Resource not found: " + base.getCanonicalName() + "/" + filename);
        return is;
    }
}
