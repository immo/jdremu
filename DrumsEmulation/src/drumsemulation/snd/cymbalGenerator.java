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
    cymbalBellGenerator bing;
    cymbalRimGenerator hirim;
    contourNoiseGenerator whoosh;
    mfcNoiseGenerator uiiich;
    mfcNoiseGenerator zzsssh;
    resoOscillator lowresonant;
    resoOscillator lowresonant2;

    public cymbalGenerator() {
        this("");
    }

    public cymbalGenerator(String parms) {
        super();
        this.description = "Cymbal(" + parms + ")";
        frequency = 1300;
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

        bell = new cymbalBellGenerator("d=100,f="+(frequency*2)+",g="+utils.multiplyGainList(gain_factors, 0.4f));
        bing = new cymbalBellGenerator("d=40,f="+(frequency*4)+",g="+utils.multiplyGainList(gain_factors, 0.4f));
        rim = new cymbalRimGenerator("d=300,f="+(frequency)+",g="+utils.multiplyGainList(gain_factors, 0.5f));
        hirim = new cymbalRimGenerator("wave=sine,d=450,f="+(frequency*5)+",g="+utils.multiplyGainList(gain_factors, 0.5f));
        whoosh = new contourNoiseGenerator("filter=band 6000 4,d=300,a=30"+",g="+utils.multiplyGainList(gain_factors, 0.3f));
        uiiich = new mfcNoiseGenerator("a=75,s=60,d=120,filter1=band 2500 4,filter2=band 10000 2,f1=80,f12=160"+",g="+utils.multiplyGainList(gain_factors, 0.9f));
        zzsssh = new mfcNoiseGenerator("a=35,s=20,d=40,filter2=high 2500 4,filter1=band 10000 2,f1=40,f12=35"+",g="+utils.multiplyGainList(gain_factors, 0.7f));
        lowresonant = new resoOscillator("f="+(frequency/3)+",a=100,d=500,wave=sine,g="+utils.multiplyGainList(gain_factors, 0.01f));
        lowresonant2 = new resoOscillator("f="+(frequency/4)+",a=140,d=600,wave=sine,g="+utils.multiplyGainList(gain_factors, 0.02f));
    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        synchronized (sync_token) {
            bell.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            rim.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            whoosh.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            uiiich.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            hirim.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            lowresonant.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            lowresonant2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            zzsssh.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            bing.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        }
    }

    @Override
    public void hit(long when, float level) {
        hit2d(when, level, 0.2f, 0.5f);
    }

    @Override
    public void hit1d(long when, float level, float p1) {
        hit2d(when, level, p1,0.5f);
    }



    @Override
    public void hit2d(long when, float level, float p1, float p2) {


         synchronized (sync_token) {
            bell.hit(when, level*p1);
            rim.hit(when,level*(1.f-p1));
            whoosh.hit(when,level*p2);
            uiiich.hit(when,level*p2);
            hirim.hit(when, level*(1.f-p1));
            lowresonant.hit(when,level);
            lowresonant2.hit(when,level);
            zzsssh.hit(when, level*p2);
            bing.hit(when, level*(1.f-p2));
        }
    }



}
