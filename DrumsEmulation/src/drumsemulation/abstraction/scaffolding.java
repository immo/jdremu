/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drumsemulation.abstraction;

import drumsemulation.helper.poorDotGraph;
import java.util.*;

/**
 *
 * @author immanuel
 */
public class scaffolding extends joist {

    public poorDotGraph g;
    public Map<Integer, joist> bindings;
    public Set<String> bound;

    public scaffolding() {
        this.g = new poorDotGraph();
        this.bindings = new HashMap<Integer, joist>();
        this.bound = new TreeSet<String>();
    }

    public scaffolding(scaffolding copy) {
        this.g = new poorDotGraph(copy.g);
        this.bindings = new HashMap<Integer, joist>(copy.bindings);
        this.bound = new TreeSet<String>(copy.bound);
    }

    public scaffolding(poorDotGraph g) {
        this();
        this.g = g;
        bindCanonical();
        System.out.println(bindings);
    }

    public void bindCanonical() {
        int N = g.nodes.size();

        drumsemulation.DrumsEmulationApp app = drumsemulation.DrumsEmulationApp.getApplication();
        Set<String> modes = app.allKnownModes();

        for (int i = 0; i < N; ++i) {
            String binder = g.nodes.get(i);
            String n = binder.replaceAll("\\s", "");

            if (!bound.contains(binder)) {
                if (n.contains("@")) {
                    int at = n.indexOf("@");
                    String r = n.substring(at + 1);
                    n = n.substring(0, at);
                    if (r.contains("!")) {
                        int excl = r.indexOf("!");
                        String e = r.substring(excl + 1);
                        r = r.substring(0, excl);
                        bind(binder, new elementaryJoist(n, Float.parseFloat(e), Float.parseFloat(r)));
                    } else if (n.contains("!")) {
                        int excl = n.indexOf("!");
                        String e = n.substring(excl + 1);
                        n = n.substring(0, excl);
                        bind(binder, new elementaryJoist(n, Float.parseFloat(e), Float.parseFloat(r)));
                    } else {
                        bind(binder, new elementaryJoist(n, 0.85f, Float.parseFloat(r)));
                    }

                } else if (n.contains("!")) {
                    int excl = n.indexOf("!");
                    String e = n.substring(excl + 1);
                    n = n.substring(0, excl);
                    bind(binder, new elementaryJoist(n, Float.parseFloat(e), 1.f));
                } else {
                    if (modes.contains(n)) {
                        bind(n, new elementaryJoist(n, 0.85f, 1.f));
                    }
                }
            }
        }
    }

    public Set<String> getUnbound() {
        Set<String> unbound = new TreeSet<String>(g.nodes);
        unbound.removeAll(bound);
        return unbound;
    }

    public void bind(String name, joist to) {
        int N = g.nodes.size();
        for (int i = 0; i < N; ++i) {
            if (g.nodes.get(i).equals(name)) {
                bindings.put(i, to);
            }
        }
        bound.add(name);
    }

    @Override
    public String toString() {
        return g.toString();
    }
}
