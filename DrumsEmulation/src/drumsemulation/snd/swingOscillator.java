/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drumsemulation.snd;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author immanuel
 */
public class swingOscillator extends hitGenerator {

    int frequency; // frequency in Hz
    long amplitude31; //amplitude shifted by 31
    long damping31; //damping factor shifted by 31
    long phase_frame; // start sample time
    int max_hits; //max nbr of hits
    int poke_length; // length of hit poke
    long[] hit_time;
    long[] hit_amplitude31;
    int hit_round_robin;
    long[] c_lvl31; //channel panning levels
    long[] chan_lvls;
    functionTables.periodicWaveform waveform;

    public swingOscillator(String description) {
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

        this.description = "swOsc(" + description + ")";
    }

    public swingOscillator() {
        super();
        max_hits = 4;
        waveform = ft.waveforms.get("cosine");
        poke_length = drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() / 20;
        hit_time = new long[max_hits];
        hit_amplitude31 = new long[max_hits];
        description = "swOsc()";
        phase_frame = 0;
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
        long next_frame_block = start_frame + frames;
        long relative_frame = start_frame - phase_frame;
        int idx = 0;
        synchronized (sync_token) {
            boolean hit_waiting = true;
            for (long f = start_frame; f < next_frame_block; ++f) {
                long stick = Integer.MIN_VALUE;
                if (hit_waiting) {
                    hit_waiting = false;
                    for (int hit_idx = 0; hit_idx < max_hits; ++hit_idx) {
                        if (hit_time[hit_idx] > f) {
                            if (hit_time[hit_idx] < next_frame_block) {
                                hit_waiting = true;
                            }
                            continue;
                        }
                        if (f > hit_time[hit_idx] + poke_length + 1) {
                            continue;
                        }
                        hit_waiting = true;

                        long poke_pos = f - hit_time[hit_idx];
                        if (poke_pos == poke_length) {
                            if (amplitude31 < hit_amplitude31[hit_idx]) {
                                amplitude31 = hit_amplitude31[hit_idx];
                                relative_frame = 0;
                                phase_frame = f;
                            }
                        }

                        stick = Math.max(stick, ft.poke(poke_pos, poke_length, hit_amplitude31[hit_idx]));

                    }
                }

                if (amplitude31 > 0) {
                    long position = Math.max(stick, (waveform.wave(relative_frame, frequency) * amplitude31) >> 31);
                    amplitude31 = (amplitude31 * damping31) >> 31;

                    for (int c = 0; c < channels; ++c) {
                        buffer[idx] += (position * chan_lvls[c]) >> 31;
                        idx++;
                    }
                    relative_frame++;
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
            hit_amplitude31[hit_round_robin] = ft.level_to_amplitude31(level);
            hit_round_robin++;
            if (hit_round_robin == max_hits) {
                hit_round_robin = 0;
            }
        }
    }

    public static void main(String[] args) {
        swingOscillator s = new swingOscillator();

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
