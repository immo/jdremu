/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drumsemulation.snd;

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

    static functionTables ft = functionTables.getObject();

    public swingOscillator(String description) {
        this();
        this.description = "swOsc(" + description + ")";
    }

    public swingOscillator() {
        super();
        max_hits = 8;
        poke_length = drumsemulation.DrumsEmulationApp.getApplication().getSampleRate()/20;
        hit_time = new long[max_hits];
        hit_amplitude31 = new long[max_hits];
        description = "swOsc()";
        phase_frame = 0;
        frequency = 220;
        amplitude31 = 0;
        c_lvl31 = new long[]{1l << 31, 1l << 31};
        chan_lvls = new long[]{1l << 31, 1l << 31};        
        damping31 = (9999l << 31)/10000l;
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
            for (long f = start_frame; f < next_frame_block; ++f) {
                long stick = Integer.MIN_VALUE;
                for (int hit_idx =0;hit_idx<max_hits;++hit_idx) {
                    if ((hit_time[hit_idx] > f)||(f>hit_time[hit_idx]+poke_length+1)) {
                        continue;
                    }
                    
                    long poke_pos = f-hit_time[hit_idx];
                    if (poke_pos == poke_length) {
                        if (amplitude31<hit_amplitude31[hit_idx]) {
                            amplitude31 = hit_amplitude31[hit_idx];
                            relative_frame = 0;
                            phase_frame = f;
                        }
                    } 
                        
                     stick = Math.max(stick, ft.poke(poke_pos, poke_length, hit_amplitude31[hit_idx]));
                    
                }


                long position = Math.max(stick,(ft.cosine(relative_frame, frequency)*amplitude31)>>31);
                long damped31 = (amplitude31*damping31)>>31;
                
                amplitude31 = damped31;


                for (int c = 0; c < channels; ++c) {
                    buffer[idx] += (position * chan_lvls[c]) >> 31;
                    idx++;
                }
                relative_frame++;
            }
        }
    }

    @Override
    public void hit(long when, int level) {
        System.out.println(hit_round_robin);
        synchronized (sync_token) {
            hit_time[hit_round_robin] = when;
            hit_amplitude31[hit_round_robin] = (level&0x7F)<<23;
            hit_round_robin++;
            if (hit_round_robin == max_hits) {
                hit_round_robin = 0;
            }
        }
    }

    public static void main(String[] args) {
        swingOscillator s = new swingOscillator();

        System.out.println("1l<<31 = "+(1l<<31));

        System.out.println("(1*1l)<<31 = "+((1*1l)<<31));

        s.hit(0,127);
        int[] buffer = new int[] {0};
        for (long f=0;f<100;++f) {
            System.out.println(f+" = "+new Float(buffer[0])/new Float(1l<<31));
            buffer[0] = 0;
            s.additiveSynthesis(f, buffer, 1,1, 1l<<31);
     
            
        }

        for (long f=100;f<28087;++f) {

            buffer[0] = 0;
            s.additiveSynthesis(f, buffer, 1,1, 1l<<31);


        }

        for (long f=28087;f<28100;++f) {
            System.out.println(f+" = "+new Float(buffer[0])/new Float(1l<<31));
            buffer[0] = 0;
            s.additiveSynthesis(f, buffer, 1,1, 1l<<31);


        }
    }
}
