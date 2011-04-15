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

import java.io.*;

/**
 *
 * @author immanuel
 */
public class testingMain {
     public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
            poorDotGraph g = new poorDotGraph();
            g.name = "PartI";
            g.edges.add(new intPair(0,1));
            g.nodes.add("snare!0.75@1.0");
            g.nodes.add("snare!0.75@1.0");
            System.out.println(g.toString());
      }
}
