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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drumsemulation.snd;

import java.util.*;

/**
 *
 * @author immanuel
 */
public class hitGenerator extends soundGenerator {

    final Object sync_token;
    Map<Long, Integer> future_hits;
    ArrayList<Long> future_hits_t;
    Random rnd;
    int noise_level;
    String description;

    public hitGenerator() {
        this.sync_token = new Object();
        this.future_hits = new TreeMap<Long, Integer>();
        this.future_hits_t = new ArrayList<Long>();
        this.rnd = new Random();
        this.description = "Beep()";
    }

    public hitGenerator(String description) {
        this();
        this.description = "Beep("+description+")";
    }


    public void hit(long when, int level) {
        synchronized (sync_token) {
            future_hits.put(when, level);
            future_hits_t.ensureCapacity(future_hits.size());
            int i = 0;
            int s = future_hits_t.size();
            Iterator<Long> it = future_hits.keySet().iterator();
            while (it.hasNext()) {
                if (i < s) {
                    future_hits_t.set(i, it.next());
                } else {
                    future_hits_t.add(it.next());
                }
                ++i;
            }
        }
    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        long next_block_start_frame = start_frame + frames;
        int frame_idx = 0;
        synchronized (sync_token) {
            if (!future_hits_t.isEmpty()) {
                boolean missed_hit = false;
                int max_level = 0;
                while ((!future_hits_t.isEmpty()) && (future_hits_t.get(0) < start_frame)) {
                    Long l = future_hits_t.get(0);
                    future_hits_t.remove(l);
                    int lvl = future_hits.get(l);
                    if (lvl > max_level) {
                        max_level = lvl;
                    }
                    future_hits.remove(l);
                    missed_hit = true;
                }
                if (missed_hit) {
                    int hit_level = (max_level & 0x7F) << 5;
                    if (hit_level > noise_level) {
                        noise_level = hit_level;
                    }
                }

                while ((!future_hits_t.isEmpty()) && (future_hits_t.get(0) < next_block_start_frame)) {
                    Long next_event = future_hits_t.get(0);
                    for (long f = start_frame; f < next_event; ++f) {
                        if (noise_level > 0) {
                            int noise = (int) (((long) ((rnd.nextInt() >> 12) * noise_level) * lvl31) >> 31);
                            for (int c = 0; c < channels; ++c) {
                                buffer[frame_idx * channels + c] += noise;
                            }

                            noise_level--;
                        }
                        frame_idx++;

                    }
                    start_frame = next_event;
                    
                    int lvl = (future_hits.get(next_event) & 0x7F) << 5;
                    future_hits.remove(next_event);
                    future_hits_t.remove(next_event);
                    if (lvl > noise_level) {
                        noise_level = lvl;
                    }
                }
            }

        }
        if (noise_level > 0) {
            for (long f = start_frame; f < next_block_start_frame; ++f) {
                int noise = (int) (((long) ((rnd.nextInt() >> 12) * noise_level) * lvl31) >> 31);
                for (int c = 0; c < channels; ++c) {
                    buffer[frame_idx * channels + c] += noise;
                }
                frame_idx++;
                noise_level--;
                if (noise_level == 0) {
                    return;
                }
            }
        }
    }

    public static hitGenerator getGeneratorByDesc(String desc) {
        hitGenerator error = new hitGenerator();
        error.description = desc + "!";
        desc = desc.trim();


        if (desc.contains("(")) {
            int idx = desc.indexOf("(");
            String type = desc.substring(0,idx).trim();
            String parms = desc.substring(idx+1);
            if (parms.substring(parms.length()-1).equals(")")) {
                parms = parms.substring(0,parms.length()-1).trim();
                
                if (type.equals("Beep")) {
                    return new hitGenerator(parms);
                } else if (type.equals("swOsc")) {
                    return new swingOscillator(parms);
                }

            }
        }


        return error;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }



    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        hitGenerator h = new hitGenerator();

        h.hit(100, 1);
        h.hit(30, 1);
        h.hit(1030, 1);
        h.hit(677, 1);
        h.additiveSynthesis(300, null, 0, 100, 1l << 31);
        System.out.println(h.future_hits_t);
        System.out.println(getGeneratorByDesc("Beep( 1 =2 )"));
    }
}
