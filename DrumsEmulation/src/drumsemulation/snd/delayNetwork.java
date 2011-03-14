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
public class delayNetwork extends hitGenerator {

    int max_hits;
    long[] hit_time;
    long[] hit_amplitude31;
    int hit_round_robin;
    long next_frame_block;
    long ping, yoing;
    int stick_target;
    int nodes;
    int max_delay;
    int current_offset;
    int nbr_connections;
    long[][] amplitudes; //its amplitude[node][current_offset-delay /%/ max_delay] for the nodes amplitude before delay frames
    long[] combined_amplitudes; // sum of all amplitudes at one time frame
    long[][] connections; //its connections[i] in the format [source, target, delay, damp]
    int nbr_output;
    long[][] output; //output weights, output[i] is in the format [source, delay, factor]
    long[] c_lvl31; //channel panning levels
    long[] chan_lvls;
    long total_amplitude31;
    long total_damping31;

    public delayNetwork() {
        this("");
    }

    public delayNetwork(String parms) {

        ArrayList<Integer> c_from = new ArrayList<Integer>();
        ArrayList<Integer> c_to = new ArrayList<Integer>();
        ArrayList<Double> c_delay = new ArrayList<Double>();
        ArrayList<Double> c_damp = new ArrayList<Double>();

        c_from.add(0);
        c_to.add(0);
        c_delay.add(1.2);
        c_damp.add(0.8);
        c_from.add(0);
        c_to.add(0);
        c_delay.add(2.2);
        c_damp.add(0.7);
        c_from.add(0);
        c_to.add(0);
        c_delay.add(18.0);
        c_damp.add(0.05);

        c_from.add(0);
        c_to.add(1);
        c_delay.add(5.0);
        c_damp.add(0.8);

        c_from.add(0);
        c_to.add(2);
        c_delay.add(12.0);
        c_damp.add(0.45);

        c_from.add(1);
        c_to.add(2);
        c_delay.add(18.0);
        c_damp.add(0.4);

        c_from.add(2);
        c_to.add(1);
        c_delay.add(3.0);
        c_damp.add(0.2);

        c_from.add(2);
        c_to.add(0);
        c_delay.add(3.0);
        c_damp.add(0.4);

        c_from.add(1);
        c_to.add(0);
        c_delay.add(12.0);
        c_damp.add(0.1);

        nodes = 3;
        max_delay = (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * 20) / 1000;
        ping = (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * 2) / 1000;
        yoing = (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * 5) / 1000;
        current_offset = 0;
        stick_target = 0;

        max_hits = 4;
        hit_amplitude31 = new long[max_hits];
        hit_time = new long[max_hits];
        hit_round_robin = 0;
        nbr_output = 1;

        c_lvl31 = new long[]{1l << 31, 1l << 31};
        chan_lvls = new long[]{1l << 31, 1l << 31};

        total_damping31 = (long)(0.99999*(1l<<31));
        total_amplitude31 = 0;

        Scanner scan = new Scanner(description);
        scan.useDelimiter(",");
        while (scan.hasNext()) {
            String parm = scan.next();
            if (parm.contains("=")) {
                int idx = parm.indexOf("=");
                String pname = parm.substring(0, idx).trim();
                String pval = parm.substring(idx + 1).trim();

                if (pname.equals("g")) { //gain levels, channelwise
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
                }
            }
        }


        nbr_connections = c_to.size();
        connections = new long[nbr_connections][4];

        for (int i = 0; i < c_to.size(); ++i) {
            connections[i][0] = c_from.get(i);
            connections[i][1] = c_to.get(i);
            connections[i][2] = (long) (c_delay.get(i) * drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() / 1000.);
            connections[i][3] = (long) ((c_damp.get(i) * (1l << 31)));
        }


        amplitudes = new long[nodes][max_delay];
        combined_amplitudes = new long[max_delay];

        output = new long[nbr_output][3];
        output[0][0] = 0;
        output[0][1] = 0;
        output[0][2] = (long) ((1l << 31) * 0.5);



        this.description = "delayNet(" + parms + ")";
    }

    @Override
    public void hit(long when, float level) {
        synchronized (sync_token) {
            if (when < next_frame_block) {
                when = next_frame_block;
            }
            hit_time[hit_round_robin] = when;
            hit_amplitude31[hit_round_robin] = ft.level_to_amplitude31(level);
            hit_round_robin++;
            if (hit_round_robin == max_hits) {
                hit_round_robin = 0;
            }
        }
    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        int idx = 0;
        for (int c = 0; c < channels; ++c) {
            chan_lvls[c] = (c_lvl31[c] * lvl31) >> 31;
        }

        synchronized (sync_token) {
            next_frame_block = start_frame + frames;
            for (long f = start_frame; f < next_frame_block; ++f) {

                for (int i = 0; i < nodes; ++i) {
                    amplitudes[i][current_offset] = 0;
                }

            
                for (int i = 0; i < nbr_connections; ++i) {
                    int source = (int) connections[i][0];
                    int target = (int) connections[i][1];
                    int delay = (int) connections[i][2];
                    long damping = connections[i][3];
                    int delayed_offset = current_offset - delay;
                    if (delayed_offset < 0) {
                        delayed_offset += max_delay;
                    }
                    amplitudes[target][current_offset] += (amplitudes[source][delayed_offset] * damping) >> 31;
                }

                boolean clear_out = false;
                
                for (int i = 0; i < max_hits; ++i) {
                    if ((f >= hit_time[i]) && (f < hit_time[i] + ping + yoing)) {
                        if (f == hit_time[i]) {
                            if (total_amplitude31 < hit_amplitude31[i]){
                                total_amplitude31 = hit_amplitude31[i];
                            }
                        }
                        if (clear_out) {
                            clear_out = false;
                            amplitudes[stick_target][current_offset] = (ft.piyoing(f - hit_time[i], ping, yoing) * hit_amplitude31[i]) >> 31;
                        } else {
                        amplitudes[stick_target][current_offset] = utils.Amax((ft.piyoing(f - hit_time[i], ping, yoing) * hit_amplitude31[i]) >> 31,
                                amplitudes[stick_target][current_offset]);
                        }

                    }
                }

                long amp_sum = 0;
                for (int i=0;i<nodes;++i) {
                    amplitudes[i][current_offset] = (amplitudes[i][current_offset]*total_amplitude31) >> 31;
                    amp_sum += amplitudes[i][current_offset];
                }

                combined_amplitudes[current_offset] = amp_sum;

                


                current_offset++;
                if (current_offset == max_delay) {
                    current_offset = 0;
                }

                long output_sum = 0;
                for (int i = 0; i < nbr_output; ++i) {
                    int offset = current_offset - (int) output[i][1];
                    if (offset < 0) {
                        offset += max_delay;
                    }
                    output_sum += (amplitudes[(int) output[i][0]][offset] * output[i][2]) >> 31;
                }

                total_amplitude31 = (total_amplitude31*total_damping31) >> 31;

                for (int c = 0; c < channels; ++c) {
                    buffer[idx] += (output_sum * chan_lvls[c]) >> 31;
                    idx++;
                }
            }
        }
    }
}
