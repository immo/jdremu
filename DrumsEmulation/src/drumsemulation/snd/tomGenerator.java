/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.snd;

/**
 *
 * @author immanuel
 */
public class tomGenerator extends hitGenerator {

    swingOscillator p1;
    swingOscillator p2;
    swingOscillator p3;

    swingOscillator click;

    long base_freq_p;
    long offset_p;

    long d2;
    long d3;

    public tomGenerator() {
        this("");

    }
    
    public tomGenerator(String parms) {
        base_freq_p = 100-13;
        offset_p = 13;
        drumsemulation.DrumsEmulationApp app = drumsemulation.DrumsEmulationApp.getApplication();
        p1 = new swingOscillator("f="+(base_freq_p+offset_p)+",a=40,d=160,g=0.5 0.5");
        p2 = new swingOscillator("f="+(2*base_freq_p+offset_p)+",a=10,d=40,g=0.4 0.4");
        p3 = new swingOscillator("f="+(3*base_freq_p+offset_p)+",a=15,d=28,g=0.25 0.25");
        click = new swingOscillator("f=5200,a=2,d=1,g=0.4 0.4,wave=cosquare");
        d2 = (app.getSampleRate()*5)/1000;
        d3 = (app.getSampleRate()*7)/1000;
        this.description = "Tom("+parms+")";
    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        p1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        p2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        p3.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        click.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
    }

    @Override
    public void hit(long when, float level) {
        p1.hit(when,level);
        p2.hit(when+d2,level*0.6f);
        p3.hit(when+d2+d3,level*0.4f);
        click.hit(when,level*0.8f);
    }

    @Override
    public void hit1d(long when, float level, float p1) {
        this.p1.hit(when,level);
        p2.hit(when+d2,level*p1);
        p3.hit(when+d2+d3,level*(1.f-p1));
        click.hit(when,level*0.8f);
        
    }

    @Override
    public void hit2d(long when, float level, float p1, float p2) {
        this.p1.hit(when,level);
        this.p2.hit(when+d2,level*p1);
        p3.hit(when+d2+d3,level*(1.f-p1));
        click.hit(when,level*p2);
    }





}
