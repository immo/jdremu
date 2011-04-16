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

    Map<String, scaffolding> scaffoldings;
    Map<String, joist> joists;

    public abstractData() {
        scaffoldings = new TreeMap<String, scaffolding>();
        joists = new TreeMap<String, joist>();
    }

    public void build(String s) {
        scaffoldings.clear();

        ArrayList<poorDotGraph> gs = poorDotParser.parseThroughDot(s);
        Iterator<poorDotGraph> it;
        for (it = gs.iterator(); it.hasNext();) {
            poorDotGraph g = it.next();
            scaffolding scaff = new scaffolding(g);
            scaffoldings.put(g.name, scaff);
        }

        System.out.println(scaffoldings);
    }

    public joist evaluateTerm(String s) {

        s = s.trim();
        if (s.contains("(") || s.contains("[")) {
            int idx = s.indexOf("(");
            if (idx >= 0) {
                int idx2 = s.indexOf("[");
                if (idx2 >= 0) {
                    if (idx > idx2) {
                        idx = idx2;
                    }
                }
            } else {
                idx = s.indexOf("[");
            }
            String varname = s.substring(0, idx).trim();
            String other = s.substring(idx);
            System.out.println(varname + "::" + other);
        } else {
            if (joists.containsKey(s)) {
                return joists.get(s).getGoodCopy();
            } else {
                String n = s.replaceAll("\\s", "");


                if (n.contains("@")) {
                    int at = n.indexOf("@");
                    String r = n.substring(at + 1);
                    n = n.substring(0, at);
                    if (r.contains("!")) {
                        int excl = r.indexOf("!");
                        String e = r.substring(excl + 1);
                        r = r.substring(0, excl);
                        return new elementaryJoist(n, Float.parseFloat(e), Float.parseFloat(r));
                    } else if (n.contains("!")) {
                        int excl = n.indexOf("!");
                        String e = n.substring(excl + 1);
                        n = n.substring(0, excl);
                        return new elementaryJoist(n, Float.parseFloat(e), Float.parseFloat(r));
                    } else {
                        return new elementaryJoist(n, 0.85f, Float.parseFloat(r));
                    }

                } else if (n.contains("!")) {
                    int excl = n.indexOf("!");
                    String e = n.substring(excl + 1);
                    n = n.substring(0, excl);
                    return new elementaryJoist(n, Float.parseFloat(e), 1.f);
                } else {
                    return new elementaryJoist(n, 0.85f, 1.f);
                }
            }
        }
        return null;
    }

    public void buildVars(String s) {
        joists = new TreeMap<String, joist>();

        Scanner sc = new Scanner(s);
        sc.useDelimiter(";");

        while (sc.hasNext()) {
            String line = sc.next();
            if (line.contains("=")) {
                int eq = line.indexOf("=");

                String name = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
            }
        }
    }

    public String scaffoldingsContent() {
        return scaffoldings.toString();
    }
}
