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

    int rim_frequency;
    int bell_frequency;
    int zss_frequency;
    float bell_gain;
    float rim_gain;
    float whoosh_gain;
    float zzsssh_gain;
    float reso_gain;
    float ui_gain;
    int hi_woosh;
    int lo_woosh;
    int hi_zssh;
    int lo_zssh;
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
    boolean p_bell, p_rim, p_bing, p_hirim,
            p_whoosh, p_uiiich, p_zzsssh,
            p_lowresonant, p_lowresonant2;

    public cymbalGenerator() {
        this("");
    }

    public cymbalGenerator(String parms) {
        super();
        this.description = "Cymbal(" + parms + ")";

        p_bell = true;
        p_rim = true;
        p_bing = true;
        p_hirim = true;
        p_whoosh = true;
        p_uiiich = true;
        p_zzsssh = true;
        p_lowresonant = true;
        p_lowresonant2 = true;

        bell_gain = 1.f;
        rim_gain = 1.f;
        whoosh_gain = 1.f;
        zzsssh_gain = 1.f;
        reso_gain = 1.f;
        ui_gain = 1.f;


        rim_frequency = 2300;
        bell_frequency = 3100;
        zss_frequency = 5500;

        hi_woosh = 12000;
        lo_woosh = 3200;
        hi_zssh = 9000;
        lo_zssh = 2600;
        decay = 600;

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
                    rim_frequency = Integer.parseInt(pval);
                } else if (pname.equals("fb")) {
                    bell_frequency = Integer.parseInt(pval);
                } else if (pname.equals("fw")) {
                    zss_frequency = Integer.parseInt(pval);
                } else if (pname.equals("fh1")) {
                    hi_woosh = Integer.parseInt(pval);
                } else if (pname.equals("fl1")) {
                    lo_woosh = Integer.parseInt(pval);
                } else if (pname.equals("fh2")) {
                    hi_zssh = Integer.parseInt(pval);
                } else if (pname.equals("fl2")) {
                    lo_zssh = Integer.parseInt(pval);
                } else if (pname.equals("d")) {
                    decay = Float.parseFloat(pval);
                } else if (pname.equals("gf")) {
                    rim_gain = Float.parseFloat(pval);
                } else if (pname.equals("gb")) {
                    bell_gain = Float.parseFloat(pval);
                } else if (pname.equals("gw")) {
                    whoosh_gain = Float.parseFloat(pval);
                } else if (pname.equals("gz")) {
                    zzsssh_gain = Float.parseFloat(pval);
                } else if (pname.equals("gu")) {
                    ui_gain = Float.parseFloat(pval);
                } else if (pname.equals("gr")) {
                    reso_gain = Float.parseFloat(pval);
                } else if (pname.equals("no")) {
                    p_bell = !pval.contains("bell");
                    p_bing = !pval.contains("bing");
                    p_hirim = !pval.contains("hi");
                    p_lowresonant = !pval.contains("low");
                    p_lowresonant2 = !pval.contains("lo2");
                    p_rim = !pval.contains("rim");
                    p_uiiich = !pval.contains("ui");
                    p_zzsssh = !pval.contains("zsh");
                    p_whoosh = !pval.contains("wh");
                }
            }
        }

        bell = new cymbalBellGenerator("d=" + (decay / 6) + ",f=" + (bell_frequency * 2)
                + ",g=" + utils.multiplyGainList(gain_factors, 0.4f * bell_gain));
        bing = new cymbalBellGenerator("d=" + (decay / 15) + ",f=" + (bell_frequency * 4)
                + ",g=" + utils.multiplyGainList(gain_factors, 0.4f * bell_gain));
        rim = new cymbalRimGenerator("d=" + (decay / 2) + ",f=" + (rim_frequency)
                + ",g=" + utils.multiplyGainList(gain_factors, 0.5f * rim_gain));
        hirim = new cymbalRimGenerator("wave=sine,d=" + (decay * 4 / 3) + ",f=" + (rim_frequency * 5)
                + ",g=" + utils.multiplyGainList(gain_factors, 0.5f * rim_gain));
        whoosh = new contourNoiseGenerator("filter=band " + zss_frequency
                + " 4,d=" + (decay / 2) + ",a=30" + ",g=" + utils.multiplyGainList(gain_factors, 0.3f * whoosh_gain));
        uiiich = new mfcNoiseGenerator("a=75,s=60,d=" + (decay / 5) + ",filter1=band " + lo_woosh
                + " 4,filter2=band " + hi_woosh + " 2,f1=80,f12=160"
                + ",g=" + utils.multiplyGainList(gain_factors, 0.9f * ui_gain));
        zzsssh = new mfcNoiseGenerator("a=35,s=20,d=" + (decay / 15) + ",filter2=high " + lo_zssh
                + " 4,filter1=band " + hi_zssh + " 2,f1=40,f12=35"
                + ",g=" + utils.multiplyGainList(gain_factors, 0.7f * zzsssh_gain));
        lowresonant = new resoOscillator("f=" + (rim_frequency / 3) + ",a=100,d=" + (decay * 5 / 6) + ",wave=sine,g=" + utils.multiplyGainList(gain_factors, 0.01f * reso_gain));
        lowresonant2 = new resoOscillator("f=" + (bell_frequency / 4) + ",a=140,d=" + (decay) + ",wave=sine,g=" + utils.multiplyGainList(gain_factors, 0.02f * reso_gain));

    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        synchronized (sync_token) {
            if (p_bell) {
                bell.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }
            if (p_rim) {
                rim.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }
            if (p_whoosh) {
                whoosh.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }
            if (p_uiiich) {
                uiiich.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }
            if (p_hirim) {
                hirim.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }
            if (p_lowresonant) {
                lowresonant.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }
            if (p_lowresonant2) {
                lowresonant2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }
            if (p_zzsssh) {
                zzsssh.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }
            if (p_bing) {
                bing.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }

        }
    }

    @Override
    public void hit(long when, float level) {
        hit2d(when, level, 0.2f, 0.5f);
    }

    @Override
    public void hit1d(long when, float level, float p1) {
        hit2d(when, level, p1, 0.5f);
    }

    @Override
    public void hit2d(long when, float level, float p1, float p2) {


        synchronized (sync_token) {
            if (p_lowresonant) {
                lowresonant.hit(when, level);
            }
            if (p_lowresonant2) {
                lowresonant2.hit(when, level);
            }

            if (p_bell) {
                bell.hit(when, level * p1);
            }
            if (p_hirim) {
                hirim.hit(when, level * p1);
            }

            if (p_rim) {
                rim.hit(when, level * (1.f - p1));
            }
            if (p_bing) {
                bing.hit(when, level * (1.f - p2));
            }

            if (p_zzsssh) {
                zzsssh.hit(when, level * p2);
            }
            if (p_whoosh) {
                whoosh.hit(when, level * p2);
            }
            if (p_uiiich) {
                uiiich.hit(when, level * p2);
            }


        }
    }
}
