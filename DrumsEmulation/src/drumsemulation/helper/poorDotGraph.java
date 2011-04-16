/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.helper;

import java.util.*;
import sun.applet.Main;

/**
 *
 * @author immanuel
 */
public class poorDotGraph {

    public String name;
    public ArrayList<String> nodes;
    public Set<intPair> edges; //.get(0) -> .get(1)

    public poorDotGraph() {
        nodes = new ArrayList<String>();
        edges = new TreeSet<intPair>();
        name = new String("G");
    }

    public poorDotGraph(poorDotGraph copy) {
        nodes = new ArrayList<String>(copy.nodes);
        edges = new TreeSet<intPair>(copy.edges);
        name = new String(copy.name);
    }

    public Map<Integer,Set<Integer>> getPrecursorNodes() {
        int N = nodes.size();
        Map<Integer,Set<Integer>> map = new TreeMap<Integer,Set<Integer>>();
        for (int i=0;i<N;++i) {
            Set<Integer> precursors = new TreeSet<Integer>();
            map.put(i, precursors);
        }
        Iterator<intPair> it;
        for (it = edges.iterator();it.hasNext();){
            intPair p = it.next();
            map.get(p.get(1)).add(p.get(0));
        }
        return map;
    }

    @Override
    public String toString() {
        int N = nodes.size();
        StringBuffer g  = new StringBuffer();
        g.append("digraph \"" + name + "\" {\n");

        for (int i=0;i<N;++i) {
            g.append("    v"+i+" [label=\""+nodes.get(i)+"\"];\n");
        }
        g.append("\n");
        Iterator<intPair> it;
        for (it = edges.iterator();it.hasNext();){
            intPair p = it.next();
            g.append("    v"+p.get(0)+" -> v"+p.get(1)+";\n");
        }
        g.append("}\n");

        return g.toString();
    }

    
}
