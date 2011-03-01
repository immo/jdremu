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

    swingOscillator clicker;
    swingOscillator m1,m2,m3,m4;
    swingOscillator e1,e2,e3,e4;
    float click_gain;
    int frequency;
    long dm1,dm2,dm3,dm4;
    long de1,de2,de3;

    public cymbalGenerator() {
        this("");
    }

    public cymbalGenerator(String parms) {
        super();
        this.description = "Cymbal(" + parms + ")";
        click_gain = 0.6f;
        frequency = 800;
        long rate = drumsemulation.DrumsEmulationApp.getApplication().getSampleRate();

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
                }
            }
        }

        clicker = new swingOscillator("f=8000,wave=cosquare,a=2,d=3,g=0.5 0.5");
        m1 = new swingOscillator("f="+(frequency/2)+",wave=cosine,a=0,d=500,g=0.5 0.5");
        m2 = new swingOscillator("f="+frequency+",wave=cosine,a=0,d=400,g=0.4 0.4");
        m3 = new swingOscillator("f="+(frequency*2)+",wave=cosine,a=0,d=300,g=0.3 0.3");
        m4 = new swingOscillator("f="+(frequency*3)+",wave=cosine,a=0,d=240,g=0.3 0.3");

        dm1 = (rate*100)/(frequency*1000);
        dm2 = (rate*50)/(frequency*1000);
        dm3 = (rate*25)/(frequency*1000);
        dm4 = (rate*11)/(frequency*1000);

        e1 = new swingOscillator("f="+((frequency*42)/10)+",wave=cosine,a=0,d=200,g=0.5 0.5");
        e2 = new swingOscillator("f="+((frequency*54)/10)+",wave=cosine,a=0,d=180,g=0.4 0.4");
        e3 = new swingOscillator("f="+((frequency*68)/10)+",wave=cosine,a=0,d=100,g=0.3 0.3");
        e4 = new swingOscillator("f="+((frequency*168)/10)+",wave=cosine,a=0,d=100,g=0.3 0.3");

        de1 = (rate*4)/(1000);
        de2 = (rate*2)/(1000);
        de3 = (rate*1)/(1000);
        
    }

    @Override
    public void hit(long when, float level) {
        synchronized (sync_token) {
            clicker.hit(when, level);

            m1.hit(when+dm1, level);
            m2.hit(when+dm2, level);
            m3.hit(when+dm3, level);
            m4.hit(when+dm4, level);
            e1.hit(when+de1, level);
            e2.hit(when+de2, level);
            e3.hit(when+de3, level);
            e4.hit(when,level);
        }
    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        synchronized (sync_token) {
            clicker.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            m1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            m2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            m3.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            m4.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            e1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            e2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            e3.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            e4.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        }
    }
}
