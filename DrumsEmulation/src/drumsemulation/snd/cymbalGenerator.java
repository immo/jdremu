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
package drumsemulation.snd;

import java.util.*;
import java.util.logging.*;
import javax.sound.sampled.*;

/**
 *
 * @author immanuel
 */
public class cymbalGenerator extends hitGenerator {

    int frequency;
    float decay;

    cymbalBellGenerator bell;
    cymbalRimGenerator rim;
    mfcNoiseGenerator whoosh;

    public cymbalGenerator() {
        this("");
    }

    public cymbalGenerator(String parms) {
        super();
        this.description = "Cymbal(" + parms + ")";
        frequency = 600;
        decay = 200;

        ArrayList<Float> gain_factors = new ArrayList<Float>();
        gain_factors.add(1.f);
        gain_factors.add(1.f);
        Scanner scan = new Scanner(parms);
        scan.useDelimiter(",");
        while (scan.hasNext()) {
            String parm = scan.next();
            if (parm.contains("=")) {
                int idx = parm.indexOf("=");
                String pname = parm.substring(0, idx).trim();
                String pval = parm.substring(idx + 1).trim();
                if (pname.equals("g")) {
                    Scanner gvals = new Scanner(pval);
                    gvals.useDelimiter(" ");
                    gain_factors.clear();
                    while (gvals.hasNext()) {
                        float f = Float.parseFloat(gvals.next().trim());
                        gain_factors.add(f);
                    }
                } else if (pname.equals("f")) {
                    frequency = Integer.parseInt(pval);

                }else if (pname.equals("d")) {
                    decay = Float.parseFloat(pval);

                }
            }
        }

        bell = new cymbalBellGenerator("f="+frequency);
        rim = new cymbalRimGenerator("f="+(frequency*4/3));
        whoosh = new mfcNoiseGenerator("d=300,a=30");

    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        synchronized (sync_token) {
            bell.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            rim.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            whoosh.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        }
    }

    @Override
    public void hit(long when, float level) {
        synchronized (sync_token) {
            bell.hit(when, level);
            rim.hit(when,level);
            whoosh.hit(when,level);
        }
    }

}
