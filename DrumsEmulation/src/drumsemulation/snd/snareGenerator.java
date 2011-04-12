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
public class snareGenerator extends hitGenerator {

    swingOscillator b1, b2;
    swingOscillator p1, p2, p3;
    swingOscillator q1, q2, q3;
    swingOscillator click;
    contourNoiseGenerator snares;
    int p_base, q_base, p_shift, q_shift;
    int frequency;
    int click_frequency;
    float decay;
    int db2, dp2, dp3, dq2, dq3;
    int dsnares;
    float snare_factor;
    float click_gain;
    String waveform;

    

    public snareGenerator(String parms) {
        super();
        this.description = "Snare(" + parms + ")";
        
        waveform = "cosine";

        ArrayList<Float> gain_factors = new ArrayList<Float>();
        gain_factors.add(1.f);
        gain_factors.add(1.f);


        drumsemulation.DrumsEmulationApp app = drumsemulation.DrumsEmulationApp.getApplication();
        int rate = app.getSampleRate();

        p_base = 111;
        q_base = 111;
        p_shift = 175;
        q_shift = 224;
        frequency = 180;
        decay = 240;
        click_frequency = 5200;
        click_gain = 0.4f;
        snare_factor = 1.f;
        

        Scanner scan = new Scanner(parms);
        scan.useDelimiter(",");
        while (scan.hasNext()) {
            String parm = scan.next();
            if (parm.contains("=")) {
                int idx = parm.indexOf("=");
                String pname = parm.substring(0, idx).trim();
                String pval = parm.substring(idx + 1).trim();

                if (pname.equals("reso")) {
                    waveform = pval;
                } else if (pname.equals("f")) {
                    frequency = Integer.parseInt(pval);
                } else if (pname.equals("fp")) {
                    p_base = Integer.parseInt(pval);
                } else if (pname.equals("fpo")) {
                    p_shift = Integer.parseInt(pval);
                } else if (pname.equals("fq")) {
                    q_base = Integer.parseInt(pval);
                } else if (pname.equals("fqo")) {
                    q_shift = Integer.parseInt(pval);
                } else if (pname.equals("d")) {
                    decay = (Float.parseFloat(pval));
                } else if (pname.equals("fc")) {
                    click_frequency = Integer.parseInt(pval);
                } else if (pname.equals("g")) {
                    Scanner gvals = new Scanner(pval);
                    gvals.useDelimiter(" ");
                    gain_factors.clear();
                    while (gvals.hasNext()) {
                        float f = Float.parseFloat(gvals.next().trim());
                        gain_factors.add(f);
                    }
                } else if (pname.equals("click")) {
                    click_gain = Float.parseFloat(pval);
                } else if (pname.equals("snare")) {
                    snare_factor = Float.parseFloat(pval);
                }
            }
        }
        String rstr=",wave="+waveform;

        db2 = (rate * 5 * 31) / (frequency * 1000);
        dp2 = (rate * 5 * 31) / ((p_base + p_shift) * 1000);
        dq2 = (rate * 5 * 31) / ((q_base + q_shift) * 1000);
        dp3 = (rate * 7 * 31) / ((p_base + p_shift) * 1000);
        dq3 = (rate * 7 * 31) / ((q_base + q_shift) * 1000);
        dsnares = (rate * 13 * 31) / (frequency*1000);
        

        b1 = new swingOscillator("f="+frequency+",a=2,d="+decay+",g="+utils.multiplyGainList(gain_factors, 0.3f));
        b2 = new swingOscillator("f="+((int)(frequency*1.835))+",a=2,d="+(decay/2)+",g="+utils.multiplyGainList(gain_factors, 0.3f));
        p1 = new swingOscillator("f=" + (p_base + p_shift) + ",a=2,d="+(decay/3)+",g="+utils.multiplyGainList(gain_factors, 0.15f)+rstr);
        p2 = new swingOscillator("f=" + (2 * p_base + p_shift) + ",a=2,d="+(decay/4)+",g="+utils.multiplyGainList(gain_factors, 0.15f)+rstr);
        p3 = new swingOscillator("f=" + (3 * p_base + p_shift) + ",a=2,d="+(decay/6)+",g="+utils.multiplyGainList(gain_factors, 0.15f)+rstr);
        q1 = new swingOscillator("f=" + (q_base + q_shift) + ",a=2,d="+(decay/2)+",g="+utils.multiplyGainList(gain_factors, 0.15f)+rstr);
        q2 = new swingOscillator("f=" + (2 * q_base + q_shift) + ",a=2,d="+(decay/4)+",g="+utils.multiplyGainList(gain_factors, 0.15f)+rstr);
        q3 = new swingOscillator("f=" + (3 * q_base + q_shift) + ",a=2,d="+(decay/6)+",g="+utils.multiplyGainList(gain_factors, 0.15f)+rstr);
        click = new swingOscillator("f="+click_frequency+",a=2,ar=0.5,d=1,g="+utils.multiplyGainList(gain_factors, click_gain));
        if (snare_factor > 0.f) {
            snares = new contourNoiseGenerator("filter=low 5000 2,g="+utils.multiplyGainList(gain_factors, 0.45f*snare_factor)+",a="+(decay/12)+",s="+(decay/16)+",d="+(decay/9));
        } else {
            snares = null;
        }
        
    }

    public snareGenerator() {
        this("");

    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        synchronized (sync_token) {
            b1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            b2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            p1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            p2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            p3.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            q1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            q2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            q3.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            click.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            if (snares != null) {
                snares.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            }
        }
    }

    @Override
    public void hit(long when, float level) {
        hit2d(when,level,0.5f,0.5f);
    }

    @Override
    public void hit1d(long when, float level, float p1) {
        hit2d(when,level,p1,0.5f);
        
    }

    @Override
    public void hit2d(long when, float level, float p1, float p2) {
        

        synchronized (sync_token) {
            b1.hit(when, level);
            b2.hit(when + db2, level);
            this.p1.hit(when, level*p1);
            this.p2.hit(when + dp2, level*p1);
            p3.hit(when + dp2 + dp3, level*p1);
            q1.hit(when, level*p2);
            q2.hit(when + dq2, level*p2);
            q3.hit(when + dq2 + dq3, level*p2);
            click.hit(when, level);
            if (snares != null) {
                snares.hit(when+dsnares,level);
            }
        }
    }


}
