/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.helper;

import java.util.*;
import java.util.ArrayList;

/**
 *
 * @author immanuel
 */
public class poorTokenizer {

    static public ArrayList<ArrayList<String>> tokenize(String s) {
        s = s.trim();
        ArrayList<ArrayList<String>> tokens = new ArrayList<ArrayList<String>>();
        int depth=0;
        ArrayList<String> tok = new ArrayList<String>();
        String current = "";
        int N = s.length();
        for (int i=0;i<N;++i) {
            char c = s.charAt(i);
            if (depth==0) {
                if ((c=='(')||(c=='[')) {
                    if (!current.isEmpty()) {
                        tok.add(current);
                    }
                    if (!tok.isEmpty()) {
                       tokens.add(tok);
                    }
                    depth ++;
                    tok = new ArrayList<String>();
                    current = "";
                    if (c=='(')
                        tok.add("(");
                    else
                        tok.add("[");
                }
            } else {

               if ((c=='(')||(c=='[')) {
                   depth++;
               } else if ((c==']'||(c==')'))) {
                   depth--;
                   if (depth == 0) ;
               }

            }
        }


        return tokens;
    }

}
