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
import drumsemulation.helper.*;
/**
 *
 * @author immanuel
 */
public class cymbalBellGenerator extends hitGenerator {

    swingOscillator clicker;
    swingOscillator m1,m2,m3,m4;
    swingOscillator e1,e2,e3,e4;
    float click_gain;
    int frequency;
    int freq_error;
    float decay;
    long dm1,dm2,dm3,dm4;
    long de1,de2,de3;

    public cymbalBellGenerator() {
        this("");
    }

    public cymbalBellGenerator(String parms) {
        super();
        this.description = "CymbalBell(" + parms + ")";
        click_gain = 0.6f;
        frequency = 800;
        freq_error = 42;
        decay = 500;
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
                        float f = poorInputParser.parseFloat(gvals.next().trim());
                        gain_factors.add(f);
                    }
                } else if (pname.equals("f")) {
                    frequency = Integer.parseInt(pval);

                }else if (pname.equals("d")) {
                    decay = poorInputParser.parseFloat(pval);

                }
            }
        }

        clicker = new swingOscillator("f=8000,wave=cosquare,a=2,d=3"+",g="+utils.multiplyGainList(gain_factors, 0.5f));
        m1 = new swingOscillator("f="+(frequency/2+freq_error)+",wave=cosine,a=0,d="+(decay)+",g="+utils.multiplyGainList(gain_factors, 0.5f));
        m2 = new swingOscillator("f="+(frequency+freq_error)+",wave=cosine,a=0,d="+(decay*0.8f)+",g="+utils.multiplyGainList(gain_factors, 0.4f));
        m3 = new swingOscillator("f="+(frequency*2+freq_error)+",wave=cosine,a=0,d="+(decay*0.6f)+",g="+utils.multiplyGainList(gain_factors, 0.3f));
        m4 = new swingOscillator("f="+(frequency*3+freq_error)+",wave=cosine,a=0,d="+(decay*0.48f)+",g="+utils.multiplyGainList(gain_factors, 0.3f));

        dm1 = (rate*100)/(frequency*1000);
        dm2 = (rate*50)/(frequency*1000);
        dm3 = (rate*25)/(frequency*1000);
        dm4 = (rate*11)/(frequency*1000);

        e1 = new swingOscillator("f="+((frequency*42)/10+freq_error)+",wave=cosine,a=0,d="+(decay*0.4f)+",g="+utils.multiplyGainList(gain_factors, 0.5f));
        e2 = new swingOscillator("f="+((frequency*54)/10+freq_error)+",wave=cosine,a=0,d="+(decay*0.36f)+",g="+utils.multiplyGainList(gain_factors, 0.4f));
        e3 = new swingOscillator("f="+((frequency*68)/10+freq_error)+",wave=cosine,a=0,d="+(decay*0.2f)+",g="+utils.multiplyGainList(gain_factors, 0.3f));
        e4 = new swingOscillator("f="+((frequency*168)/10+freq_error)+",wave=cosine,a=0,d="+(decay*0.2f)+",g="+utils.multiplyGainList(gain_factors, 0.3f));

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
