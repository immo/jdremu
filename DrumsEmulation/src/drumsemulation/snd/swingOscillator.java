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
    //all variables are fixed point integers in 8.23 binary format, mass is renormalized to 1

    long x23; //current position
    long v23; //current velocity
    long k23; //spring force factor
    long mu23; //friction factor
    long one_by_dt31; //multiplicative inverse of length of a timestep
    long[] c_lvl31; //channel panning levels
    long[] chan_lvls;

    public swingOscillator(String description) {
        this();
        this.description = "swOsc(" + description + ")";
    }

    public swingOscillator() {
        super();
        description = "swOsc()";
        x23 = 0;
        v23 = 0;
        k23 = 1000 << 23;
        mu23 = 0;
        c_lvl31 = new long[]{1l << 31, 1l << 31};
        chan_lvls = new long[]{1l << 31, 1l << 31};
        int sample_rate = drumsemulation.DrumsEmulationApp.getApplication().getSampleRate();
        one_by_dt31 = (1l << 31) / ((long) sample_rate);
    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {

        for (int c = 0; c < channels; ++c) {
            chan_lvls[c] = (c_lvl31[c] * lvl31) >> 31;
        }
        long next_frame_block = start_frame + frames;

        int idx = 0;
        synchronized (sync_token) {
            for (long f = start_frame; f < next_frame_block; ++f) {
                long F23 = -((k23 * x23) >> 23);
                v23 += ((F23 - ((v23 * mu23) >> 23)) * one_by_dt31) >> 31; //F = m*dv/dt = 1*dv/dt
                long old_x23 = x23;
                x23 += (v23 * one_by_dt31) >> 31;

                if ((old_x23 > 0) && (x23<=0)) {
                    System.out.println(f);
                }

                for (int c = 0; c < channels; ++c) {
                    buffer[idx] += (int)((x23 * chan_lvls[c]) >> 23);
                    idx++;
                }
            }
        }
    }

    @Override
    public void hit(long when, int level) {
        System.out.println(x23);
        synchronized (sync_token) {
            x23 = 1<<23;
            v23 = 0;
        }
    }

    public static void main(String[] args) {
        swingOscillator s = new swingOscillator();

        System.out.println("1l<<31 = "+(1l<<31));

        System.out.println("1/dt="+s.one_by_dt31);

        s.hit(0,0);
        int[] buffer = new int[] {0};
        for (long f=0;f<100;++f) {
            System.out.println(f+" = "+new Float(buffer[0])/new Float(1l<<31)+"  x="+s.x23+"  v="+s.v23);
            buffer[0] = 0;
            s.additiveSynthesis(f, buffer, 1,1, 1l<<31);
     
            
        }

        for (long f=100;f<28087;++f) {

            buffer[0] = 0;
            s.additiveSynthesis(f, buffer, 1,1, 1l<<31);


        }

        for (long f=28087;f<28100;++f) {
            System.out.println(f+" = "+new Float(buffer[0])/new Float(1l<<31)+"  x="+s.x23+"  v="+s.v23);
            buffer[0] = 0;
            s.additiveSynthesis(f, buffer, 1,1, 1l<<31);


        }
    }
}
