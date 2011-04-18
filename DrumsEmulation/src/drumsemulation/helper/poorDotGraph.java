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

    public ArrayList<Set<Integer>> getPredAndSuccNodes(int node) {
        ArrayList<Set<Integer>> inout = new ArrayList<Set<Integer>>();
        inout.add(new TreeSet<Integer>());
        inout.add(new TreeSet<Integer>());
        Iterator<intPair> it;
        for (it = edges.iterator();it.hasNext();){
            intPair p = it.next();
            if (p.get(0)==node) {
                inout.get(1).add(p.get(1)); // successor
            }
            if (p.get(1)==node) {
                inout.get(0).add(p.get(0)); // predecessor
            }
        }
        return inout;
    }

    public ArrayList<Set<Integer>> getInAndOutNodes() {
        ArrayList<Set<Integer>> inout = new ArrayList<Set<Integer>>();
        inout.add(new TreeSet<Integer>());
        inout.add(new TreeSet<Integer>());
        int N = nodes.size();
        for (int i=0;i<N;++i) {
            for (int j=0;j<2;++j) {
                inout.get(j).add(i);
            }
        }
        Iterator<intPair> it;
        for (it = edges.iterator();it.hasNext();){
            intPair p = it.next();
            inout.get(1).remove(p.get(0)); // has successor -> no out node
            inout.get(0).remove(p.get(1)); // has predecessor -> no in node
        }
        return inout;
    }

    public void removeNode(int i) {
        int N = nodes.size();
        if ((i<0)||(i>=N)) {
            return;
        }
        nodes.set(i, nodes.get(N-1));
        Set<intPair> new_edges = new TreeSet<intPair>();
        Iterator<intPair> it;
        for (it=edges.iterator();it.hasNext();) {
            intPair p = it.next();
            if ((p.get(0)==i)||(p.get(1)==i)) {
                
                //do nothing
            } else {
                
                if (p.get(0)==N-1) {
                    p.set(0,i);
                
                }
                if (p.get(1)==N-1) {
                    p.set(1,i);
                
                }
                new_edges.add(p);
            }
        }
        edges = new_edges;
        
        nodes.remove(N-1);
    }

    public void removeNodes(Set<Integer> del) {
        TreeSet<Integer> sorted_del = new TreeSet<Integer>(new greaterThanComparator());
        sorted_del.addAll(del);
        Iterator<Integer> it;
        for (it = sorted_del.iterator();it.hasNext();) {
            Integer i = it.next();
            removeNode(i);
        }
    }

    public void addGraph(poorDotGraph g2) {
        int N = nodes.size();
        nodes.addAll(g2.nodes);
        Iterator<intPair> it;
        for (it=g2.edges.iterator();it.hasNext();)
        {
            intPair p = it.next();
            edges.add(new intPair(p.get(0)+N,p.get(1)+N));
        }
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
