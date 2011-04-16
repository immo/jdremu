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
public class scaffolding extends joist {

    public poorDotGraph g;
    public Map<Integer, joist> bindings;
    public Map<String, joist> bound;
    public Set<String> canonical;
    public boolean prepared;
    public Map<Integer, Float> start_times;
    public Map<Integer, Float> stop_times;


    public scaffolding() {
        this.g = new poorDotGraph();
        this.bindings = new HashMap<Integer, joist>();
        this.bound = new TreeMap<String, joist>();
        this.canonical = new TreeSet<String>();
        this.prepared = false;
        this.start_times = new TreeMap<Integer, Float>();
        this.stop_times = new TreeMap<Integer, Float>();
    }

    public scaffolding(scaffolding copy) {
        this.g = new poorDotGraph(copy.g);
        this.bindings = new HashMap<Integer, joist>(copy.bindings);
        this.bound = new TreeMap<String, joist>(copy.bound);
        this.canonical = new TreeSet<String>(copy.canonical);
        this.prepared = copy.prepared;
        this.start_times = new TreeMap<Integer, Float>(copy.start_times);
        this.stop_times = new TreeMap<Integer, Float>(copy.stop_times);
    }

    public scaffolding(String dotCode) {
        this((poorDotParser.parseThroughDot(dotCode)).iterator().next());
    }

    public scaffolding(poorDotGraph g) {
        this();
        this.g = g;
        bindCanonical();

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
                        bind(binder, new elementaryJoist(n, Float.parseFloat(e), Float.parseFloat(r)));
                        canonical.add(binder);
                    } else if (n.contains("!")) {
                        int excl = n.indexOf("!");
                        String e = n.substring(excl + 1);
                        n = n.substring(0, excl);
                        bind(binder, new elementaryJoist(n, Float.parseFloat(e), Float.parseFloat(r)));
                        canonical.add(binder);
                    } else {
                        bind(binder, new elementaryJoist(n, 0.85f, Float.parseFloat(r)));
                        canonical.add(binder);
                    }

                } else if (n.contains("!")) {
                    int excl = n.indexOf("!");
                    String e = n.substring(excl + 1);
                    n = n.substring(0, excl);
                    bind(binder, new elementaryJoist(n, Float.parseFloat(e), 1.f));
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
            if (g.nodes.get(i).equals(name)) {
                bindings.put(i, to);
            }
        }
        bound.put(name, to);
    }

    public void rename(String old, String newname) {
        int N = g.nodes.size();
        for (int i = 0; i < N; ++i) {
            if (g.nodes.get(i).equals(old)) {
                g.nodes.set(i, newname);
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

        Map<Integer,Set<Integer>> precursors = g.getPrecursorNodes();

        Set<Integer> needAdjustment = new TreeSet<Integer>(bindings.keySet());
        while (!needAdjustment.isEmpty()) {
            Iterator<Integer> i;
            Set<Integer> justAdjusted = new TreeSet<Integer>();
            for (i=needAdjustment.iterator();i.hasNext();){
                Integer t = i.next();
                TreeSet needed = new TreeSet<Integer>(precursors.get(t));
                needed.retainAll(needAdjustment);
                if (needed.isEmpty()) {
                    float start = 0.f;
                    Iterator<Integer> j;
                    for (j=precursors.get(t).iterator();j.hasNext();) {
                        float t2 = stop_times.get(j.next());
                        if (t2 > start) {
                            start = t2;
                        }
                    }

                    start_times.put(t, start);
                    stop_times.put(t,start + bindings.get(t).duration());

                    justAdjusted.add(t);
                }
            }
            needAdjustment.removeAll(justAdjusted);
        }

        prepared = true;
        _duration = 0.f;
        Iterator<Integer> i;
        for (i=stop_times.keySet().iterator();i.hasNext();) {
            float t2 = stop_times.get(i.next());
            if (t2 > _duration) {
                _duration = t2;
            }
        }

    }

    @Override
    public joist getGoodCopy() {
        return new scaffolding(this);
    }

}
