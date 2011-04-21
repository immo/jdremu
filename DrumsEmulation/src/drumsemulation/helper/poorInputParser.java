/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.helper;

/**
 *
 * @author immanuel
 */
public class poorInputParser {

    public static float parseFloat(String s) {
        if (s.contains("/")) {
            int idx = s.indexOf("/");
            return Float.parseFloat(s.substring(0,idx).trim())/Float.parseFloat(s.substring(idx+1).trim());
        }
        return Float.parseFloat(s);
    }
}
