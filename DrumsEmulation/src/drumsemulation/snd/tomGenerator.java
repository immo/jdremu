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
public class tomGenerator extends hitGenerator {

    swingOscillator p1;
    swingOscillator p2;
    swingOscillator p3;

    swingOscillator click;

    int base_freq_p;
    int offset_p;
    float decay;

    long d2;
    long d3;

    public tomGenerator() {
        this("");

    }
    
    public tomGenerator(String parms) {
        base_freq_p = 100-13;
        offset_p = 13;
        decay = 160.f;

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

                if (pname.equals("f")) {
                    base_freq_p = Integer.parseInt(pval);
                } else if (pname.equals("d")) {
                    decay = Float.parseFloat(pval);
                } else if (pname.equals("fo")) {
                    offset_p = Integer.parseInt(pval);
                } else if (pname.equals("g")) {
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


        drumsemulation.DrumsEmulationApp app = drumsemulation.DrumsEmulationApp.getApplication();
        p1 = new swingOscillator("f="+(base_freq_p+offset_p)+",a=40,d="+decay+",g="+utils.multiplyGainList(gain_factors,0.5f));
        p2 = new swingOscillator("f="+(2*base_freq_p+offset_p)+",a=10,d="+(decay/4)+",g="+utils.multiplyGainList(gain_factors, 0.4f));
        p3 = new swingOscillator("f="+(3*base_freq_p+offset_p)+",a=15,d="+(decay/5)+",g="+utils.multiplyGainList(gain_factors, 0.25f));
        click = new swingOscillator("f=5200,a=2,ar=0.5,d=1,g="+utils.multiplyGainList(gain_factors, 0.4f)+",wave=cosquare");
        d2 = (app.getSampleRate()*5*83)/(1000*base_freq_p);
        d3 = (app.getSampleRate()*7*83)/(1000*base_freq_p);
        this.description = "Tom("+parms+")";
    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        p1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        p2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        p3.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        click.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
    }

    @Override
    public void hit(long when, float level) {
        p1.hit(when,level);
        p2.hit(when+d2,level*0.6f);
        p3.hit(when+d2+d3,level*0.4f);
        click.hit(when,level*0.8f);
    }

    @Override
    public void hit1d(long when, float level, float p1) {
        this.p1.hit(when,level);
        p2.hit(when+d2,level*p1);
        p3.hit(when+d2+d3,level*(1.f-p1));
        click.hit(when,level*0.8f);
        
    }

    @Override
    public void hit2d(long when, float level, float p1, float p2) {
        this.p1.hit(when,level);
        this.p2.hit(when+d2,level*p1);
        p3.hit(when+d2+d3,level*(1.f-p1));
        click.hit(when,level*p2);
    }





}
