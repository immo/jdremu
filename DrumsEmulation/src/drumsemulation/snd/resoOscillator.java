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
public class resoOscillator extends hitGenerator {

    int frequency; // frequency in Hz
    long amplitude31; //amplitude shifted by 31
    long damping31; //damping factor shifted by 31
    long phase_frame; // start sample time
    int max_hits; //max nbr of hits
    int poke_length; // length of hit poke
    int poke_miss_release_length; // length of poke release if hit was too weak
    long[] hit_time;
    long[] hit_amplitude31;
    boolean[] hit_miss; //hit was too weak
    int hit_round_robin;
    long[] c_lvl31; //channel panning levels
    long[] chan_lvls;
    functionTables.periodicWaveform waveform;
    long next_frame_block;


    public resoOscillator(String description) {
        this();

        Scanner scan = new Scanner(description);
        scan.useDelimiter(",");
        while (scan.hasNext()) {
            String parm = scan.next();
            if (parm.contains("=")) {
                int idx = parm.indexOf("=");
                String pname = parm.substring(0, idx).trim();
                String pval = parm.substring(idx + 1).trim();

                if (pname.equals("f")) {
                    this.frequency = Integer.parseInt(pval);
                } else if (pname.equals("a")) { //attack, ms
                    this.poke_length = (int) (((float) drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * Float.parseFloat(pval)) / 1000.0f);
                } else if (pname.equals("phase")) { //phase, ms
                    this.phase_frame = -(int) (((float) drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * Float.parseFloat(pval)) / 1000.0f);
                } else if (pname.equals("ar")) { //failed attack poke release, ms
                    this.poke_miss_release_length = (int) (((float) drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * Float.parseFloat(pval)) / 1000.0f);
                } else if (pname.equals("d")) { //decay (well, 1/2 value), ms
                    float half_ms = Float.parseFloat(pval);
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
                            chan_gain.add((long) (Float.parseFloat(v) * (1l << 31)));
                        }
                    }
                    c_lvl31 = new long[chan_gain.size()];
                    chan_lvls = new long[chan_gain.size()];
                    for (int i = 0; i < chan_gain.size(); ++i) {
                        c_lvl31[i] = chan_gain.get(i);
                    }
                } else if (pname.equals("wave")) {
                    waveform = ft.waveforms.get(pval);
                }

            }
        }

        this.description = "rOsc(" + description + ")";
    }

    public resoOscillator() {
        super();
        max_hits = 4;
        waveform = ft.waveforms.get("sine");
        poke_length = drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() / 20;
        poke_miss_release_length = (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * 2) / 1000;
        hit_time = new long[max_hits];
        hit_amplitude31 = new long[max_hits];
        hit_miss = new boolean[max_hits];
        description = "rOsc()";
        phase_frame = new Random().nextInt(drumsemulation.DrumsEmulationApp.getApplication().getSampleRate());
        frequency = 220;
        amplitude31 = 0;
        c_lvl31 = new long[]{1l << 31, 1l << 31};
        chan_lvls = new long[]{1l << 31, 1l << 31};
        damping31 = (9999l << 31) / 10000l;
        hit_round_robin = 0;

    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {

        for (int c = 0; c < channels; ++c) {
            chan_lvls[c] = (c_lvl31[c] * lvl31) >> 31;
        }

        long relative_frame = start_frame - phase_frame;
        int idx = 0;
        synchronized (sync_token) {
            next_frame_block = start_frame + frames;

            boolean hit_waiting = true;
            for (long f = start_frame; f < next_frame_block; ++f) {
                
                if (hit_waiting) {
                    hit_waiting = false;
                    for (int hit_idx = 0; hit_idx < max_hits; ++hit_idx) {
                        if (hit_time[hit_idx] > f) {
                            if (hit_time[hit_idx] < next_frame_block) {
                                hit_waiting = true;
                            }
                            continue;
                        }
                        if (f > hit_time[hit_idx] + poke_length + 1 + (hit_miss[hit_idx]?poke_miss_release_length:0) ) {
                            continue;
                        }
                        hit_waiting = true;

                        long poke_pos = f - hit_time[hit_idx];
                        if (poke_pos == poke_length) {
                            if (amplitude31 <= hit_amplitude31[hit_idx]) {
                                amplitude31 = hit_amplitude31[hit_idx];
                                //relative_frame = 0;
                                //phase_frame = f;
                            } else {

                                hit_miss[hit_idx] = true;
                            }
                        }
                        if (poke_pos < poke_length) {
                            long amp31 = (hit_amplitude31[hit_idx]*poke_pos)/poke_length;
                            if (amplitude31 <= amp31) {
                                amplitude31 = amp31;
                            }
                            
                        } else if (hit_miss[hit_idx]) {
                            
                        }

                    }
                }

                if (amplitude31 > 0) {
                    long position = (waveform.wave(relative_frame, frequency) * amplitude31) >> 31;
                    amplitude31 = (amplitude31 * damping31) >> 31;

                    for (int c = 0; c < channels; ++c) {
                        buffer[idx] += (position * chan_lvls[c]) >> 31;
                        idx++;
                    }
                    relative_frame++;
                } else if (!hit_waiting) { //amplitude == 0 and there are no hits waiting
                    break;
                } else {
                    
                    relative_frame++;
                }
            }
        }
    }

    @Override
    public void hit(long when, float level) {

        synchronized (sync_token) {
            if (when < next_frame_block) {
                when = next_frame_block;
            }
            hit_time[hit_round_robin] = when;
            hit_amplitude31[hit_round_robin] = ft.level_to_amplitude31(level);
            hit_miss[hit_round_robin] = false;
            hit_round_robin++;
            if (hit_round_robin == max_hits) {
                hit_round_robin = 0;
            }
        }
    }

    public static void main(String[] args) {
        resoOscillator s = new resoOscillator();

        System.out.println("1l<<31 = " + (1l << 31));

        System.out.println("(1*1l)<<31 = " + ((1 * 1l) << 31));

        s.hit(0, 127);
        int[] buffer = new int[]{0};
        for (long f = 0; f < 100; ++f) {
            System.out.println(f + " = " + new Float(buffer[0]) / new Float(1l << 31));
            buffer[0] = 0;
            s.additiveSynthesis(f, buffer, 1, 1, 1l << 31);


        }

        for (long f = 100; f < 28087; ++f) {

            buffer[0] = 0;
            s.additiveSynthesis(f, buffer, 1, 1, 1l << 31);


        }

        for (long f = 28087; f < 28100; ++f) {
            System.out.println(f + " = " + new Float(buffer[0]) / new Float(1l << 31));
            buffer[0] = 0;
            s.additiveSynthesis(f, buffer, 1, 1, 1l << 31);


        }
    }
}
