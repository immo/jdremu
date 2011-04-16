/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drumsemulation.abstraction;

import drumsemulation.helper.poorDotGraph;
import drumsemulation.helper.poorDotParser;
import drumsemulation.helper.poorTokenizer;
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
            if (!joists.containsKey(varname)) {
                return new elementaryJoist(varname, 0.85f, 1.f);
            }
            joist j = joists.get(varname).getGoodCopy();

            scaffolding scaff = (scaffolding) j;

            String other = s.substring(idx);
            ArrayList<ArrayList<String>> tokens = poorTokenizer.tokenize(other);
            Iterator<ArrayList<String>> itoken;
            for (itoken = tokens.iterator(); itoken.hasNext();) {
                ArrayList<String> parameters = itoken.next();
                Iterator<String> parm = parameters.iterator();
                String type = parm.next();
                if (type.equals("(")) {
                    for (; parm.hasNext();) {
                        String t = parm.next();
                        int eq = t.indexOf("=");
                        if (eq >= 0) {
                            String name = t.substring(0, eq).trim();
                            String term = t.substring(idx + 1).trim();
                            scaff.bind(name, evaluateTerm(term));
                        } else {
                            Iterator<String> freetor = scaff.getUnbound().iterator();
                            if (freetor.hasNext()) {
                                String name = freetor.next();
                                String term = t.trim();
                                scaff.bind(name, evaluateTerm(term));
                            }
                        }
                    }
                } else if (type.equals("[")) {
                    for (; parm.hasNext();) {
                        String t = parm.next();
                        int eq = t.indexOf("=");
                        if (eq >= 0) {
                            String name = t.substring(0, eq).trim();
                            String term = t.substring(idx + 1).trim();
                            scaff.rename(name, term);
                        }
                    }

                }
            }
            return scaff;
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

    }

    public void buildVars(String s) {
        joists = new TreeMap<String, joist>(scaffoldings);

        Scanner sc = new Scanner(s);
        sc.useDelimiter(";");

        while (sc.hasNext()) {
            String line = sc.next();
            if (line.contains("=")) {
                int eq = line.indexOf("=");

                String name = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                joists.put(name, evaluateTerm(value));
            }
        }

        Iterator<String> is;
        for (is=joists.keySet().iterator();is.hasNext();) {
            String name = is.next();
            joists.get(name).prepareLayout();
            System.out.println(name+" = "+joists.get(name).duration());
        }
    }

    public String scaffoldingsContent() {
        return scaffoldings.toString();
    }

    public String joistsContent() {
        return joists.toString();
    }
}
