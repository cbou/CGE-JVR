package cge.zeppelin.util;

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

}
