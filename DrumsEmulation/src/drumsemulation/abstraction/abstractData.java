/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drumsemulation.abstraction;


import java.util.*;
import drumsemulation.helper.*;
/**
 *
 * @author immanuel
 */
public class abstractData {

    Map<String, scaffolding> scaffoldings;
    Map<String, joist> joists;

    joist master_joist;

    public abstractData() {
        scaffoldings = new TreeMap<String, scaffolding>();
        joists = new TreeMap<String, joist>();
        master_joist = new joist();
    }

    public synchronized void tick(long when, float t, float next_t, float previous_t) {
        if ((previous_t < 0.f)&&(0.f<=t))
            master_joist.enter(when);
        
        master_joist.tick(when, t, next_t, previous_t);
    }

    public synchronized void build(String s) {
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

    public synchronized void setMaster(joist m) {
        master_joist = m;
    }

    public synchronized joist getNamedJoist(String name) {
        if (joists.containsKey(name)) {
            return joists.get(name);
        } else return null;
    }

    public synchronized joist evaluateTerm(String s) {

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
            joist j;
            if (!joists.containsKey(varname)) {
                if (!scaffoldings.containsKey(varname)) {
                    return new elementaryJoist(varname, 0.85f, 1.f);
                } else  {
                    j = scaffoldings.get(varname).getGoodCopy();
                }
            } else {
             j = joists.get(varname).getGoodCopy();
            }

            if (!(j instanceof scaffolding)) {
                return j;
            }

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
                        String t = parm.next().trim();
                        int eq = t.indexOf("=");
                        int max_eq1 = (t+"[").indexOf("[");
                        int max_eq2 = (t+"(").indexOf("(");

                        if ((eq >= 0)&&(eq<max_eq1)&&(eq<max_eq2)) {
                            String name = t.substring(0, eq).trim();
                            String term = t.substring(eq + 1).trim();
                            if (name.equals("speed")) {
                                scaff.set_time_factor(poorInputParser.parseFloat(term));
                            } else if (name.equals("speed*")) {
                                scaff.multiply_time_factor(poorInputParser.parseFloat(term));
                            } else {
                                scaff.bind(name, evaluateTerm(term));
                            }
                        } else {
                            if (!t.isEmpty()) {
                                Iterator<String> freetor = scaff.getUnbound().iterator();
                                if (freetor.hasNext()) {
                                    String name = freetor.next();
                                    String term = t.trim();
                                    scaff.bind(name, evaluateTerm(term));
                                }
                            }
                        }
                    }
                } else if (type.equals("[")) {
                    for (; parm.hasNext();) {
                        String t = parm.next();
                        int eq = t.indexOf("=");
                        int max_eq1 = (t+"[").indexOf("[");
                        int max_eq2 = (t+"(").indexOf("(");
                        
                        if ((eq >= 0)&&(eq<max_eq1)&&(eq<max_eq2)) {
                            if (t.substring(0, eq).endsWith(":")) { // := --> insert

                                String name = t.substring(0, eq-1).trim();
                                String term = t.substring(eq + 1).trim();
                                joist insert = evaluateTerm(term);
                                if (insert instanceof scaffolding) {
                                    scaff.insert(name, (scaffolding)insert);
                                } else {
                                    scaff.bind(name,insert);
                                }
                            } else { // = --> rename
                                String name = t.substring(0, eq).trim();
                                String term = t.substring(eq + 1).trim();
                                scaff.rename(name, term);
                            }
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
                        return new elementaryJoist(n, poorInputParser.parseFloat(e), poorInputParser.parseFloat(r));
                    } else if (n.contains("!")) {
                        int excl = n.indexOf("!");
                        String e = n.substring(excl + 1);
                        n = n.substring(0, excl);
                        return new elementaryJoist(n, poorInputParser.parseFloat(e), poorInputParser.parseFloat(r));
                    } else {
                        return new elementaryJoist(n, 0.85f, poorInputParser.parseFloat(r));
                    }

                } else if (n.contains("!")) {
                    int excl = n.indexOf("!");
                    String e = n.substring(excl + 1);
                    n = n.substring(0, excl);
                    return new elementaryJoist(n, poorInputParser.parseFloat(e), 1.f);
                } else {
                    return new elementaryJoist(n, 0.85f, 1.f);
                }
            }
        }

    }

    public synchronized void buildVars(String s) {
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
        for (is = joists.keySet().iterator(); is.hasNext();) {
            String name = is.next();
            joists.get(name).prepareLayout();
            System.out.println(name + " = " + joists.get(name).duration());
        }
    }

    public synchronized String scaffoldingsContent() {
        StringBuffer s = new StringBuffer();
        s.append(scaffoldings.toString());
        Iterator<String> it;
        for (it = scaffoldings.keySet().iterator(); it.hasNext();) {
            s.append("\n");
            s.append(scaffoldings.get(it.next()).g.toString());
        }
        return s.toString();
    }

    public synchronized String joistsContent() {
        StringBuffer s = new StringBuffer();
        s.append(joists.toString());
        s.append("\n\n");
        Iterator<String> it;
        for (it = joists.keySet().iterator(); it.hasNext();) {
            String name = it.next();
            joist j = joists.get(name);
            if (j instanceof scaffolding) {
                scaffolding sc = (scaffolding) j;
                s.append("\n");
                s.append(name + " := " + sc.g.toString() + " & " + j.toString());
            }

        }
        return s.toString();

    }
}
