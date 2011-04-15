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
public class poorDotParser {

    public static ArrayList<poorDotGraph> parseFile(Scanner s) {
        ArrayList<poorDotGraph> l = new ArrayList<poorDotGraph>();

        s.useDelimiter("}");
        while (s.hasNext()) {
            StringBuffer graph = new StringBuffer();
            int curly_depth = 0;
            while (s.hasNext()) {
                String appendix = s.next();
                int L = appendix.length();
                for (int i = 0; i < L; ++i) {
                    if (appendix.charAt(i) == '{') {
                        curly_depth++;
                    }
                }

                graph.append(appendix + "}");
                curly_depth--;

                if (curly_depth <= 0) {
                    break;
                }
            }

            poorDotGraph g = new poorDotGraph();
            Scanner s2 = new Scanner(graph.toString());
            s2.useDelimiter("(\\{|\\}|;)");
            int nodecount = 0;
            HashMap<String,Integer> nodenames = new HashMap<String,Integer>();
            HashMap<Integer,String> nodelabels = new HashMap<Integer,String>();

            while (s2.hasNext()) {
                String pt = s2.next().trim();
                if (pt.startsWith("digraph")) {
                    g.name = pt.substring(7).trim();
                } else if (pt.startsWith("strict")) { //strict digraph G
                    g.name = pt.substring(6).trim().substring(7).trim();
                } else if ((pt.startsWith("subgraph"))||pt.isEmpty()||
                        (pt.startsWith("graph"))||(pt.startsWith("node"))||
                        (pt.startsWith("edge"))) {
                    //ignore this
                } else if (pt.contains("->")) {
                    //edge
                    String[] prms = pt.split("(->|\\[)");
                    if (prms.length >= 2) {
                        String from = prms[0].trim();
                        String to = prms[1].trim();
                        if (!nodenames.containsKey(from)) {
                            nodenames.put(from, nodecount);
                            nodelabels.put(nodecount, from);
                            nodecount++;
                        }
                        if (!nodenames.containsKey(to)) {
                            nodenames.put(to, nodecount);
                            nodelabels.put(nodecount, to);
                            nodecount++;
                        }
                        g.edges.add(new intPair(nodenames.get(from), nodenames.get(to)));
                    }
                } else {
                    int idx = pt.indexOf("[");
                    if (idx >= 0) {
                        String options = pt.substring(idx+1);
                        String name = pt.substring(0, idx).trim();
                        if (!nodenames.containsKey(name)) {
                            nodenames.put(name, nodecount);
                            nodecount++;
                        }
                        String label = name;

                        Scanner opts = new Scanner(options);
                        opts.useDelimiter(",");
                        while (opts.hasNext()) {
                            String t = opts.next().trim();
                            if (t.startsWith("label")) {
                                int eq = t.indexOf("=");
                                String rhs = t.substring(eq+1).trim();

                                if (rhs.endsWith("]")) {
                                    rhs = rhs.substring(0,rhs.length()-1).trim();
                                }
                                if (rhs.startsWith("\""))
                                {
                                    label = rhs.substring(1, rhs.length()-1).trim();
                                } else {
                                    label = rhs;
                                }
                            }
                        }

                        nodelabels.put(nodenames.get(name), label);
                    }
                }
            }

            for (int i=0;i<nodecount;++i) {
                if (nodelabels.containsKey(i))
                    g.nodes.add(nodelabels.get(i));
                else
                    g.nodes.add("");
            }
            

            l.add(g);
        }

        return l;
    }

}
