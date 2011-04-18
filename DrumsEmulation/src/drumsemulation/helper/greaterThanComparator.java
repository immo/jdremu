/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.helper;

import java.util.*;

/**
 *
 * @author immanuel
 */
public class greaterThanComparator implements Comparator<Object> {

    public int compare(Object t, Object t1) {
        Comparable<Object> c1 = (Comparable<Object>)t1;
        return c1.compareTo(t);
    }

}

