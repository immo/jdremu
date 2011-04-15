/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.abstraction;

import drumsemulation.helper.poorDotGraph;
import drumsemulation.helper.poorDotParser;
import java.util.*;

/**
 *
 * @author immanuel
 */
public class abstractData {

    Map<String,scaffolding> scaffoldings;

    public abstractData() {
        scaffoldings = new TreeMap<String, scaffolding>();
    }

    public void build(String s) {
        scaffoldings.clear();

        ArrayList<poorDotGraph> gs = poorDotParser.parseThroughDot(s);
        Iterator<poorDotGraph> it;
        for (it=gs.iterator();it.hasNext();){
            poorDotGraph g = it.next();
            scaffolding scaff = new scaffolding(g);
            scaffoldings.put(g.name, scaff);
        }

        System.out.println(scaffoldings);
    }

    public String scaffoldingsContent() {
        return scaffoldings.toString();
    }

}
