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
    int nodes;
    int max_delay;
    int current_offset;
    int nbr_connections;
    long[][] amplitudes; //its amplitude[node][current_offset-delay /%/ max_delay] for the nodes amplitude before delay frames
    long[][] connections; //its connections[i] in the format [source, target, delay, damp]
    int nbr_output;
    long[][] output; //output weights, output[i] is in the format [source, delay, factor]
    long[] c_lvl31; //channel panning levels
    long[] chan_lvls;

    public delayNetwork() {
        this("");
    }

    public delayNetwork(String parms) {
        nodes = 1;
        max_delay = (drumsemulation.DrumsEmulationApp.getApplication().getSampleRate() * 20) / 1000;
        current_offset = 0;
        nbr_connections = 1;
        max_hits = 4;
        hit_amplitude31 = new long[max_hits];
        hit_time = new long[max_hits];
        hit_round_robin = 0;
        nbr_output = 1;

        c_lvl31 = new long[]{1l<<31, 1l<<31};
        chan_lvls = new long[]{1l<<31, 1l<<31};

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


        amplitudes = new long[nodes][max_delay];
        connections = new long[nbr_connections][4];
        output = new long[nbr_output][3];
        output[0][0] = 0;
        output[0][1] = 0;
        output[0][2] = 1l << 31;


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

                for (int i=0;i<nodes;++i) {
                    amplitudes[i][current_offset] = 0;
                }

                for (int i=0;i<nbr_connections;++i) {
                    int source = (int)connections[i][0];
                    int target = (int)connections[i][1];
                    int delay = (int)connections[i][2];
                    long damping = connections[i][3];
                    int delayed_offset = current_offset-delay;
                    if (delayed_offset < 0) {
                        delayed_offset += max_delay;
                    }
                    amplitudes[target][current_offset] += (amplitudes[source][delayed_offset] * damping) >> 31;
                }


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
                    output_sum += (amplitudes[(int)output[i][0]][offset] * output[i][2]) >> 31;
                }

                for (int c = 0; c < channels; ++c) {
                    buffer[idx] += (output_sum * chan_lvls[c]) >> 31;
                    idx++;
                }
            }
        }
    }
}