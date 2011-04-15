/*
 *  DrumsEmulation - drum emulator & sythesizer
 *  Copyright (C) 2011 C.D.Immanuel Albrecht
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */
package drumsemulation.helper;

import drumsemulation.abstraction.abstractData;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author immanuel
 */
public class testingMain {

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        poorDotGraph g = new poorDotGraph();
        g.name = "PartI";
        g.edges.add(new intPair(0, 1));
        g.nodes.add("snare!0.75@1.0");
        g.nodes.add("snare!0.75@1.0");
        System.out.println(g.toString());



        Scanner s = new Scanner("digraph UNNAMED { s [label=\"red\", pos=\"asdds\", sds]; q [label=Y]; s -> q [asd ,d,d]; subgraph { }; } strict digraph N { x -> y; } digraph K { }");
        System.out.println(poorDotParser.parseFile(s));


        Process p = new ProcessBuilder("dot", "/home/immanuel/tmp/test.dot").start();
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(testingMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("exit="+p.exitValue());

        Scanner scn = new Scanner(p.getInputStream());

        System.out.println(scn.next());

        System.out.println(poorDotParser.parseThroughDot("digraph \"3x\" { \"snare@\" -> \"a b c\" -> z;}"));

        abstractData dta = new abstractData();

        Set<String> st = new TreeSet<String>();
        st.add("ab");
        String q = " a \t  d";
        System.out.println(q.replaceAll("\\s", ""));
        if (st.contains(q+"b")) {
            System.out.println("ab");
        }

    }
}
