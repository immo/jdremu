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
public class contourNoiseGenerator extends hitGenerator {

    Random rnd;
    long[] hit_time;
    long[] hit_amplitude31;
    int max_hits;
    int hit_round_robin;
    long damping31;
    long amplitude31;
    long[] c_lvl31; //channel panning levels
    long[] chan_lvls;
    long attack;
    long sustain;
    //biquad filter for noise-shaping
    long[] a;
    long[] b;
    long[] x;
    long[] y;
    int offset_turn;
    long[] amp31; //past amplitudes

    

    public contourNoiseGenerator(String parms) {
        super();

        rnd = new Random();
        max_hits = 4;
        hit_time = new long[max_hits];
        hit_amplitude31 = new long[max_hits];
        hit_round_robin = 0;
        amplitude31 = 0;
        attack = (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * 30) / 1000;
        sustain = (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * 20) / 1000;
        damping31 = (long) (((1l << 31) * (Math.exp(-0.69314718055994529 / ((drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * 50) / 1000)))));
        c_lvl31 = new long[]{1l << 31, 1l << 31};
        chan_lvls = new long[]{1l << 31, 1l << 31};

        a = new long[3];
        x = new long[3];
        b = new long[2];
        y = new long[3];
        amp31 = new long[3];
        offset_turn = 0;

        unsetBiquad();


        Scanner scan = new Scanner(parms);
        scan.useDelimiter(",");
        while (scan.hasNext()) {
            String parm = scan.next();
            if (parm.contains("=")) {
                int idx = parm.indexOf("=");
                String pname = parm.substring(0, idx).trim();
                String pval = parm.substring(idx + 1).trim();

                if (pname.equals("d")) { //decay (well, 1/2 value), ms
                    float half_ms = poorInputParser.parseFloat(pval);
                    int steps = (int) (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * half_ms) / 1000;
                    double x = Math.exp(-0.69314718055994529 / steps); //e^(ln(1/2)/steps)
                    damping31 = (long) (((1l << 31) * x));
                } else if (pname.equals("g")) { //gain levels, channelwise
                    Scanner pscan = new Scanner(pval);
                    pscan.useDelimiter(" ");
                    ArrayList<Long> chan_gain = new ArrayList<Long>();
                    while (pscan.hasNext()) {
                        String v = pscan.next().trim();
                        if (!v.isEmpty()) {
                            chan_gain.add((long) (poorInputParser.parseFloat(v) * (1l << 31)));
                        }
                    }
                    c_lvl31 = new long[chan_gain.size()];
                    chan_lvls = new long[chan_gain.size()];
                    for (int i = 0; i < chan_gain.size(); ++i) {
                        c_lvl31[i] = chan_gain.get(i);
                    }
                } else if (pname.equals("filter")) {
                    this.setFilter(pval);
                } else if (pname.equals("a")) { //attack in ms
                    attack = (long) ((drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * poorInputParser.parseFloat(pval)) / 1000.f);
                } else if (pname.equals("s")) { //sustain in ms
                    sustain = (long) ((drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * poorInputParser.parseFloat(pval)) / 1000.f);
                }

            }
        }

        this.description = "asdNoise(" + parms + ")";
    }

    public contourNoiseGenerator() {
        this("");
    }

    public void setFilter(String filterinfo) {
        Scanner s = new Scanner(filterinfo.trim());
        s.useDelimiter(" ");
        ArrayList<String> parts = new ArrayList<String>();
        while (s.hasNext()) {
            parts.add(s.next().trim());
        }
        if (parts.get(0).equals("low")) {
            setLowpassBiquad(Double.parseDouble(parts.get(1)), Double.parseDouble(parts.get(2)));
        } else if (parts.get(0).equals("high")) {
            setHighpassBiquad(Double.parseDouble(parts.get(1)), Double.parseDouble(parts.get(2)));
        } else if (parts.get(0).equals("band")) {
            setBandpassBiquad(Double.parseDouble(parts.get(1)), Double.parseDouble(parts.get(2)));
        } else {
            unsetBiquad();
        }
    }

    public void setLowpassBiquad(double center_freq, double bandwidth) {
        double omega = 2.0 * Math.PI * center_freq / (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate());
        double sn = Math.sin(omega);
        double cs = Math.cos(omega);
        double alpha = sn * Math.sinh(Math.log(2.0) / 2.0 * bandwidth * omega / sn);
        double a0r = 1.0 / (1.0 + alpha);
        a[0] = (long) ((a0r * (1.0 - cs) * 0.5) * (1l << 31));
        a[1] = (long) ((a0r * (1.0 - cs)) * (1l << 31));
        a[2] = (long) ((a0r * (1.0 - cs) * 0.5) * (1l << 31));
        b[0] = (long) ((a0r * (2.0 * cs)) * (1l << 31));
        b[1] = (long) ((a0r * (alpha - 1.0)) * (1l << 31));
    }

    public void setHighpassBiquad(double center_freq, double bandwidth) {
        double omega = 2.0 * Math.PI * center_freq / (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate());
        double sn = Math.sin(omega);
        double cs = Math.cos(omega);
        double alpha = sn * Math.sinh(Math.log(2.0) / 2.0 * bandwidth * omega / sn);
        double a0r = 1.0 / (1.0 + alpha);
        a[0] = (long) ((a0r * (1.0 + cs) * 0.5) * (1l << 31));
        a[1] = (long) ((a0r * -(1.0 + cs)) * (1l << 31));
        a[2] = (long) ((a0r * (1.0 + cs) * 0.5) * (1l << 31));
        b[0] = (long) ((a0r * (2.0 * cs)) * (1l << 31));
        b[1] = (long) ((a0r * (alpha - 1.0)) * (1l << 31));
    }

    public void setBandpassBiquad(double center_freq, double bandwidth) {
        double omega = 2.0 * Math.PI * center_freq / (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate());
        double sn = Math.sin(omega);
        double cs = Math.cos(omega);
        double alpha = sn * Math.sinh(Math.log(2.0) / 2.0 * bandwidth * omega / sn);
        double a0r = 1.0 / (1.0 + alpha);
        a[0] = (long) ((a0r * alpha) * (1l << 31));
        a[1] = 0;
        a[2] = (long) ((a0r * -alpha) * (1l << 31));
        b[0] = (long) ((a0r * (2.0 * cs)) * (1l << 31));
        b[1] = (long) ((a0r * (alpha - 1.0)) * (1l << 31));
    }

    public void unsetBiquad() {
        a[0] = 1l << 31;
        a[1] = 0;
        a[2] = 0;
        b[0] = 0;
        b[1] = 0;
    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        for (int c = 0; c < channels; ++c) {
            chan_lvls[c] = (c_lvl31[c] * lvl31) >> 31;
        }
        long next_frame_block = start_frame + frames;

        int idx = 0;
        synchronized (sync_token) {
            boolean hit_waiting = true;
            for (long f = start_frame; f < next_frame_block; ++f) {
                int offset_last2;
                int offset_last;
                if (offset_turn == 0) {
                    offset_turn = 1;
                    offset_last2 = 2;
                    offset_last = 0;
                } else if (offset_turn == 1) {
                    offset_turn = 2;
                    offset_last2 = 0;
                    offset_last = 1;
                } else {
                    offset_turn = 0;
                    offset_last2 = 1;
                    offset_last = 2;
                }
                if (hit_waiting) {
                    hit_waiting = false;
                    for (int hit_idx = 0; hit_idx < max_hits; ++hit_idx) {
                        if (hit_time[hit_idx] > f) {
                            if (hit_time[hit_idx] < next_frame_block) {
                                hit_waiting = true;
                            }
                            continue;
                        }
                        if (f > hit_time[hit_idx] + attack + sustain) {
                            continue;
                        }
                        hit_waiting = true;

                        long poke_pos = f - hit_time[hit_idx];
                        if (poke_pos < attack) {
                            long attack_amplitude = (hit_amplitude31[hit_idx] * poke_pos) / attack;
                            if (amplitude31 < attack_amplitude) {
                                amplitude31 = attack_amplitude;
                            }
                        } else {
                            if (amplitude31 < hit_amplitude31[hit_idx]) {
                                amplitude31 = hit_amplitude31[hit_idx];
                            }
                        }


                    }
                }
                long position = 0;
                if (amplitude31 > 0) {
                    amp31[offset_turn] = amplitude31;


                    position = (rnd.nextInt() * amplitude31) >> 31;
                    amplitude31 = (amplitude31 * damping31) >> 31;

                    x[offset_turn] = position;
                    y[offset_turn] =
                            ((a[0] * x[offset_turn]) >> 31)
                            + ((a[1] * x[offset_last]) >> 31)
                            + ((a[2] * x[offset_last2]) >> 31)
                            + ((b[0] * y[offset_last]) >> 31)
                            + ((b[1] * y[offset_last2]) >> 31);


                    for (int c = 0; c < channels; ++c) {
                        buffer[idx] += (y[offset_turn] * chan_lvls[c]) >> 31;
                        idx++;
                    }

                } else if (amp31[offset_turn] > 0) { //fan out filter
                    amp31[offset_turn] = 0;
                    x[offset_turn] = 0;
                    y[offset_turn] =
                            ((a[1] * x[offset_last]) >> 31)
                            + ((a[2] * x[offset_last2]) >> 31)
                            + ((b[0] * y[offset_last]) >> 31)
                            + ((b[1] * y[offset_last2]) >> 31);

                    for (int c = 0; c < channels; ++c) {
                        buffer[idx] += (y[offset_turn] * chan_lvls[c]) >> 31;
                        idx++;
                    }
                } else if (!hit_waiting) { //amplitude == 0 and there are no hits waiting
                    break;
                }

            }
        }
    }

    @Override
    public void hit(long when, float level) {
        synchronized (sync_token) {
            hit_time[hit_round_robin] = when;
            hit_amplitude31[hit_round_robin] = ft.level_to_amplitude31(level,6.f,0.f);
            hit_round_robin++;
            if (hit_round_robin == max_hits) {
                hit_round_robin = 0;
            }
        }
    }
}
