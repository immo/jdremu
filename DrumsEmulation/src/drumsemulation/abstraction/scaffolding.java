/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drumsemulation.abstraction;

import drumsemulation.helper.*;
import java.util.*;

/**
 *
 * @author immanuel
 */
public class scaffolding extends joist {

    public poorDotGraph g;
    public Map<Integer, joist> bindings;
    public Map<String, joist> bound;
    public Set<String> canonical;
    public boolean prepared;
    public Map<Integer, Float> start_times;
    public Map<Integer, Float> stop_times;
    public int joist_count;

    public scaffolding() {
        this.g = new poorDotGraph();
        this.bindings = new HashMap<Integer, joist>();
        this.bound = new TreeMap<String, joist>();
        this.canonical = new TreeSet<String>();
        this.prepared = false;
        this.start_times = new TreeMap<Integer, Float>();
        this.stop_times = new TreeMap<Integer, Float>();
        this.joist_count = 0;
    }

    public scaffolding(scaffolding copy) {
        this.g = new poorDotGraph(copy.g);
        this.bindings = new HashMap<Integer, joist>();
        this.bindings.putAll(copy.bindings);
        this.bound = new TreeMap<String, joist>();
        this.bound.putAll(copy.bound);
        this.canonical = new TreeSet<String>(copy.canonical);
        this.prepared = copy.prepared;
        this.start_times = new TreeMap<Integer, Float>();
        this.start_times.putAll(copy.start_times);
        this.stop_times = new TreeMap<Integer, Float>();
        this.stop_times.putAll(copy.stop_times);
        this.joist_count = copy.joist_count;
    }

    public scaffolding(String dotCode) {
        this((poorDotParser.parseThroughDot(dotCode)).iterator().next());
    }

    public scaffolding(poorDotGraph g) {
        this();
        this.g = g;
        bindCanonical();

    }

    @Override
    public void enter(long when) {
        if (!prepared) {
            System.out.println("Error: scaffolding entered when not prepared!");
            prepareLayout();
        }
    }

    @Override
    public void tick(long when, float t, float next_t, float previous_t) {
        Set<Integer> b = bindings.keySet();

        for (int i = 0; i < joist_count; ++i) {
            if (!b.contains(i)) {
                continue;
            }

            if (start_times == null) {
                System.out.println("ST===0!!!");
            }

            if (!start_times.containsKey(i)) {
                System.out.println("NO KEY ="+i);
            }
            
            float start = start_times.get(i);
            float stop = stop_times.get(i);
            joist j = bindings.get(i);
            if ((previous_t < start)&&(start <= t)) {
                j.enter(when);
            }
            if ((start <= t)&&(t < stop)) {
                j.tick(when, t-start, next_t-start, previous_t-start);
            }
        }
        
    }

    public void bindCanonical() {
        int N = g.nodes.size();

        drumsemulation.DrumsEmulationApp app = drumsemulation.DrumsEmulationApp.getApplication();
        Set<String> modes = app.allKnownModes();

        for (int i = 0; i < N; ++i) {
            String binder = g.nodes.get(i);
            String n = binder.replaceAll("\\s", "");

            if (!bound.containsKey(binder)) {
                if (n.contains("@")) {
                    int at = n.indexOf("@");
                    String r = n.substring(at + 1);
                    n = n.substring(0, at);
                    if (r.contains("!")) {
                        int excl = r.indexOf("!");
                        String e = r.substring(excl + 1);
                        r = r.substring(0, excl);
                        bind(binder, new elementaryJoist(n, poorInputParser.parseFloat(e), poorInputParser.parseFloat(r)));
                        canonical.add(binder);
                    } else if (n.contains("!")) {
                        int excl = n.indexOf("!");
                        String e = n.substring(excl + 1);
                        n = n.substring(0, excl);
                        bind(binder, new elementaryJoist(n, poorInputParser.parseFloat(e), poorInputParser.parseFloat(r)));
                        canonical.add(binder);
                    } else {
                        bind(binder, new elementaryJoist(n, 0.85f, poorInputParser.parseFloat(r)));
                        canonical.add(binder);
                    }

                } else if (n.contains("!")) {
                    int excl = n.indexOf("!");
                    String e = n.substring(excl + 1);
                    n = n.substring(0, excl);
                    bind(binder, new elementaryJoist(n, poorInputParser.parseFloat(e), 1.f));
                    canonical.add(binder);
                } else {
                    if (modes.contains(n)) {
                        bind(binder, new elementaryJoist(n, 0.85f, 1.f));
                        canonical.add(binder);
                    }
                }
            }
        }
    }

    public Set<String> getUnbound() {
        Set<String> unbound = new TreeSet<String>(g.nodes);
        unbound.removeAll(bound.keySet());
        return unbound;
    }

    public void bind(String name, joist to) {
        int N = g.nodes.size();

        for (int i = 0; i < N; ++i) {
            if (!bindings.containsKey(i)) {
                if (g.nodes.get(i).equals(name)) {
                    bindings.put(i, to);
                }
            }
        }
        bound.put(name, to);
    }

    public void rename(String old, String newname) {
        int N = g.nodes.size();
        for (int i = 0; i < N; ++i) {
            if (g.nodes.get(i).equals(old)) {
                if (!bindings.containsKey(i)) {
                    g.nodes.set(i, newname);
                } else {
                    g.nodes.set(i, g.nodes.get(i) + "*" + newname);
                }
            }
        }
    }

    public void insert(String name, scaffolding insertation) {

        System.out.println("replacing " + name + " with " + insertation);

        int N = g.nodes.size();
        Set<Integer> replaced_nodes = new TreeSet<Integer>();
        int N2 = insertation.g.nodes.size();
        ArrayList<Set<Integer>> in_out = insertation.g.getInAndOutNodes();


        for (int i = 0; i < N; ++i) {
            if (!bindings.containsKey(i)) {
                if (g.nodes.get(i).equals(name)) {
                    replaced_nodes.add(i);
                    ArrayList<Set<Integer>> pred_succ = g.getPredAndSuccNodes(i);
                    int first_index = g.nodes.size();

//                    System.out.println("pred_succ="+pred_succ);
//                    System.out.println("in_out="+in_out);
//
//
//                    System.out.println("xxxxxxxxx");
//                    System.out.println(g);
                    g.addGraph(insertation.g);
//                    System.out.println("|||||||||");
//                    System.out.println(g);
                    Iterator<Integer> qit;

                    for (qit = pred_succ.get(0).iterator(); qit.hasNext();) {
                        int q = qit.next();
                        Iterator<Integer> rit;
                        for (rit = in_out.get(0).iterator(); rit.hasNext();) {
                            int r = rit.next() + first_index;
                            g.edges.add(new intPair(q, r));
                        }
                    }
                    for (qit = pred_succ.get(1).iterator(); qit.hasNext();) {
                        int q = qit.next();
                        Iterator<Integer> rit;
                        for (rit = in_out.get(1).iterator(); rit.hasNext();) {
                            int r = rit.next() + first_index;
                            g.edges.add(new intPair(r, q));
                        }
                    }

//                    System.out.println("________");
//                    System.out.println(g);

                    for (qit = insertation.bindings.keySet().iterator(); qit.hasNext();) {
                        Integer b = qit.next();
                        bindings.put(b + first_index, insertation.bindings.get(b));
                    }

                }
            }
        }

        removeNodes(replaced_nodes);
    }

    public void removeNode(int i) {
        int N = g.nodes.size();
        g.removeNode(i);
        bindings.remove(i);

        if (bindings.containsKey(N - 1)) {
            bindings.put(i, bindings.get(N - 1));
            bindings.remove(N - 1);
        }
    }

    public void removeNodes(Set<Integer> del) {
        TreeSet<Integer> sorted_del = new TreeSet<Integer>(new greaterThanComparator());
        sorted_del.addAll(del);
        Iterator<Integer> it;
        for (it = sorted_del.iterator(); it.hasNext();) {
            Integer i = it.next();
            removeNode(i);
        }
    }

    public void disprepare() {
        prepared = false;
        Set<joist> children = new HashSet<joist>();
        Iterator<String> it;
        for (it = bound.keySet().iterator(); it.hasNext();) {
            children.add(bound.get(it.next()));
        }
        Iterator<joist> jt;
        for (jt = children.iterator(); jt.hasNext();) {
            joist j = jt.next();
            if (j instanceof scaffolding) {
                ((scaffolding)j).disprepare();
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append(g.name + "(");
        Iterator<String> it;
        boolean sep = false;
        for (it = bound.keySet().iterator(); it.hasNext();) {
            String varname = it.next();
            if (!canonical.contains(varname)) {
                if (sep) {
                    out.append(", ");
                }

                sep = true;

                out.append(varname);
                out.append("=");
                out.append(bound.get(varname).toString());
            }
        }
        out.append(")");
        return out.toString();
    }

    public String describeGraph() {
        return g.toString();
    }

    @Override
    public void prepareLayout() {
        if (prepared) {
            return;
        }
        Set<joist> children = new HashSet<joist>();
        Iterator<String> it;
        for (it = bound.keySet().iterator(); it.hasNext();) {
            children.add(bound.get(it.next()));
        }
        Iterator<joist> jt;
        for (jt = children.iterator(); jt.hasNext();) {
            jt.next().prepareLayout();
        }

        Map<Integer, Set<Integer>> precursors = g.getPrecursorNodes();

        start_times = new TreeMap<Integer,Float>();
        stop_times = new TreeMap<Integer,Float>();

        Set<Integer> needAdjustment = new TreeSet<Integer>(bindings.keySet());
        while (!needAdjustment.isEmpty()) {
            Iterator<Integer> i;
            Set<Integer> justAdjusted = new TreeSet<Integer>();
            for (i = needAdjustment.iterator(); i.hasNext();) {
                Integer t = i.next();
                TreeSet needed = new TreeSet<Integer>(precursors.get(t));
                needed.retainAll(needAdjustment);
                if (needed.isEmpty()) {
                    float start = 0.f;
                    Iterator<Integer> j;
                    for (j = precursors.get(t).iterator(); j.hasNext();) {
                        float t2 = stop_times.get(j.next());
                        if (t2 > start) {
                            start = t2;
                        }
                    }

                    start_times.put(t, start);
                    stop_times.put(t, start + bindings.get(t).duration());

                    justAdjusted.add(t);
                }
            }
            needAdjustment.removeAll(justAdjusted);
        }

        prepared = true;
        _duration = 0.f;
        Iterator<Integer> i;
        for (i = stop_times.keySet().iterator(); i.hasNext();) {
            float t2 = stop_times.get(i.next());
            if (t2 > _duration) {
                _duration = t2;
            }
        }

        joist_count = g.nodes.size();

    }

    @Override
    public joist getGoodCopy() {
        return new scaffolding(this);
    }
}
